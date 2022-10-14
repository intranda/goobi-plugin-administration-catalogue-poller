///**
// * This file is part of a plugin for Goobi - a Workflow tool for the support of mass digitization.
// *
// * Visit the websites for more information.
// *          - https://goobi.io
// *          - https://www.intranda.com
// *          - https://github.com/intranda/goobi
// *
// * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
// * Software Foundation; either version 2 of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
// * Temple Place, Suite 330, Boston, MA 02111-1307 USA
// *
// */
//package de.intranda.goobi.plugins.cataloguePoller;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//
//import de.intranda.goobi.plugins.cataloguePoller.PollDocStruct.PullDiff;
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//public class QuartzJob implements Job {
//
//    // This class is part of the GUI package, if access to other plugin jar is
//    // needed, it must be initialized first
//    // For each call a new instance is created, class gets destroyed after
//    // execute() is finished
//
//    @Override
//    public void execute(JobExecutionContext context) {
//
//        log.debug("Execute job for rule: " + context.getJobDetail().getName() + " - " + context.getRefireCount());
//        String ruleName = context.getJobDetail().getJobDataMap().getString("rule");
//        CataloguePoll cp = new CataloguePoll();
//        cp.execute(ruleName);
//        log.debug(cp.getDifferences().size() + " processes were checked for new catalogue information.");
//        int changed = 0;
//        for (PullDiff pd : cp.getDifferences()) {
//            if (pd.getMessages().size() > 0) {
//                log.debug("Catalogue entry updated for process " + pd.getProcessTitle());
//                changed++;
//                for (String diff : pd.getMessages()) {
//                    log.debug("Catalogue difference: " + diff);
//                }
//            }
//        }
//        log.debug(changed + " processes were updated with new catalogue information.");
//
//        // IAdministrationPlugin plugin = (IAdministrationPlugin)
//        // PluginLoader.getPluginByTitle(PluginType.Administration,
//        // "intranda_admin_catalogue_poller");
//        // if (plugin != null) {
//        // log.debug("Plugin initialised: " + plugin.getTitle());
//        // }
//
//    }
//
//}
