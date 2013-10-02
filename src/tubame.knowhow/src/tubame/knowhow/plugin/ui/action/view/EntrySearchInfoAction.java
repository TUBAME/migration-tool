/*
 * EntrySearchInfoAction.java
 * Created on 2013/06/28
 *
 * Copyright (C) 2011-2013 Nippon Telegraph and Telephone Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tubame.knowhow.plugin.ui.action.view;

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.wizard.IWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.wizard.register.CreateSearchInfoWizard;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Action class of search information registration Wizard display.<br/>
 */
public class EntrySearchInfoAction extends AbstractEntryItemAction {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntrySearchInfoAction.class);

    /**
     * Constructor.<br/>
     * 
     * @param selectedEntry
     *            Entry that is stored in the select target
     */
    public EntrySearchInfoAction(
            PortabilityKnowhowListViewOperation selectedEntry) {
        super(selectedEntry, ResourceUtil.registerSearchInfoTitle);
        EntrySearchInfoAction.LOGGER.debug(CmnStringUtil.EMPTY);
    }

    /**
     * Get information search wizard.<br/>
     * 
     * @return Search information Wizard
     */
    protected IWizard getWizard() {
        EntrySearchInfoAction.LOGGER
                .info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_OPEN_SEARCHINFOMATION_WIZARD));
        return new CreateSearchInfoWizard(
                PluginUtil.getActiveWorkbenchWindow(),
                ResourceUtil.registerSearchInfoTitle, getSelectedEntry());
    }
}
