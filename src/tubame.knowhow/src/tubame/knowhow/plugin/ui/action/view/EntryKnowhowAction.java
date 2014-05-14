/*
 * EntryKnowhowAction.java
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
import tubame.knowhow.plugin.ui.wizard.register.CreateKnowhowWizard;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Action class know-how of registration Wizard display.<br/>
 */
public class EntryKnowhowAction extends AbstractEntryItemAction {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntryKnowhowAction.class);

    /**
     * Constructor.<br/>
     * 
     * @param selectedEntry
     *            Entry that is stored in the select target
     */
    public EntryKnowhowAction(PortabilityKnowhowListViewOperation selectedEntry) {
        super(selectedEntry, ResourceUtil.registerKnowhow);
        EntryKnowhowAction.LOGGER.debug(CmnStringUtil.EMPTY);
    }

    /**
     * Get the know-how wizard.<br/>
     * 
     * @return Know-how Wizard
     */
    protected IWizard getWizard() {
        EntryKnowhowAction.LOGGER
                .info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_OPEN_KNOWHOWINFORMATION_WIZARD));
        return new CreateKnowhowWizard(PluginUtil.getActiveWorkbenchWindow(),
                ResourceUtil.registerKnowhow, getSelectedEntry());
    }
}
