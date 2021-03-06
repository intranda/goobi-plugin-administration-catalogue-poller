package de.intranda.goobi.plugins.cataloguePoller;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

import de.sub.goobi.config.ConfigPlugins;
import lombok.extern.log4j.Log4j2;

// This class goes to GUI folder because it ends with *QuartzListener.class
// it must be in GUI/ or lib/ folder

@WebListener
@Log4j2
public class QuartzListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // stop the catalogue poller job
        try {
            SchedulerFactory schedFact = new StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.deleteJob("Catalogue Poller", "Goobi Admin Plugin");
            log.info("Scheduler for 'Catalogue Poller' stopped");
        } catch (SchedulerException e) {
            log.error("Error while stopping the job", e);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        log.info("Starting 'Catalogue Poller' scheduler");

        XMLConfiguration config = ConfigPlugins.getPluginConfig("intranda_administration_catalogue_poller");
        config.setExpressionEngine(new XPathExpressionEngine());

        try {
            // get default scheduler
            SchedulerFactory schedFact = new StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            List<HierarchicalConfiguration> rules = config.configurationsAt("rule");

            for (HierarchicalConfiguration rule : rules) {

                String ruleName = rule.getString("@title");
                // get start time, set Calendar object  / default 22:00:00
                String configuredStartTime = rule.getString("@startTime");

                if (StringUtils.isBlank(configuredStartTime)) {
                    log.error("No starttime found for rule {}, starting at 22:00:00", ruleName);
                    configuredStartTime = "22:00:00";
                } else if (!configuredStartTime.matches("([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")) {
                    log.error("Invalid time format found for rule {}, use hh:mm:ss. Starting at 22:00:00", ruleName);
                    configuredStartTime = "22:00:00";
                }

                // get delay between trigger / default 24h
                int delay = rule.getInt("@delay", 24);

                log.info("Definition for rule {} : starting at {}, repeat every {} hour(s).", ruleName, configuredStartTime, delay);

                // configure time to start
                java.util.Calendar startTime = java.util.Calendar.getInstance();
                startTime.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(configuredStartTime.substring(0, 2)));
                startTime.set(java.util.Calendar.MINUTE, Integer.parseInt(configuredStartTime.substring(3, 5)));
                startTime.set(java.util.Calendar.SECOND, Integer.parseInt(configuredStartTime.substring(6, 8)));
                startTime.set(java.util.Calendar.MILLISECOND, 0);

                //if the startTime will be before the current time, move to next day
                if (startTime.getTime().before(new Date())) {
                    startTime.add(java.util.Calendar.DAY_OF_MONTH, 1);
                }

                // create new job
                JobDetail jobDetail = new JobDetail("Catalogue Poller " + ruleName, "Goobi Admin Plugin", QuartzJob.class);
                JobDataMap map = new JobDataMap();
                map.put("rule", ruleName);
                jobDetail.setJobDataMap(map);
                Trigger trigger = TriggerUtils.makeHourlyTrigger(delay);
                trigger.setName("Catalogue Poller");
                trigger.setStartTime(startTime.getTime());

                // register job and trigger at scheduler
                sched.scheduleJob(jobDetail, trigger);
            }

        } catch (SchedulerException e) {
            log.error("Error while executing the scheduler", e);
        }
    }
}
