/*
 * AbstractEntryItemAction.java
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.util.PluginUtil;

/**
 * Abstract class of item registration action.<br/>
 */
public abstract class AbstractEntryItemAction extends Action {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractEntryItemAction.class);
    /** Menu name */
    private String menuName;
    /** Entry objects contained in the select target */
    private PortabilityKnowhowListViewOperation selectedEntry;

    /**
     * Constructor.<br/>
     * 
     * @param selectedEntry
     *            Entry that is stored in the select target
     * @param menuName
     *            Menu name
     */
    public AbstractEntryItemAction(
            PortabilityKnowhowListViewOperation selectedEntry, String menuName) {
        this.selectedEntry = selectedEntry;
        this.menuName = menuName;
        AbstractEntryItemAction.LOGGER.debug("[selectedEntry]" + selectedEntry
                + "[menuName]" + menuName);
    }

    /**
     * Get selectedEntry.<br/>
     * 
     * @return selectedEntry
     */
    public PortabilityKnowhowListViewOperation getSelectedEntry() {
        return selectedEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        AbstractEntryItemAction.LOGGER.debug(CmnStringUtil.EMPTY);

        WizardDialog wizardDialog = new WizardDialog(
                PluginUtil.getActiveWorkbenchShell(), getWizard());
        wizardDialog.setHelpAvailable(false);
        wizardDialog.open();
        AbstractEntryItemAction.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_OPEN_KNOWHOW_WIZARD));
    }

    /**
     * Get entry wizard.<br/>
     * 
     * @return Entry wizard
     */
    protected abstract IWizard getWizard();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return this.menuName;
    }

}
