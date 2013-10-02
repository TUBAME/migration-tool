/*
 * CreateCheckItemWizard.java
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
package tubame.knowhow.plugin.ui.wizard.register;

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.logic.EntryItemManagement;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.plugin.ui.editor.multi.checkitem.CheckItemFieldsPage;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.ViewUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Check items Registration Wizard class know-how to the entry view.<br/>
 */
public class CreateCheckItemWizard extends Wizard implements INewWizard {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CreateCheckItemWizard.class);
    /** Wizard registration information */
    private CreateCheckItemPage createCheckItemPage;
    /** Additional target entry */
    private PortabilityKnowhowListViewOperation selectedEntry;

    /**
     * Constructor.<br/>
     * 
     * @param window
     *            IWorkbenchWindow
     * @param windowTitle
     *            Title
     * @param selectedEntry
     *            Add a entry
     */
    public CreateCheckItemWizard(IWorkbenchWindow window, String windowTitle,
            PortabilityKnowhowListViewOperation selectedEntry) {
        super();
        super.setWindowTitle(windowTitle);
        this.selectedEntry = selectedEntry;
        CreateCheckItemWizard.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_CREATE_CHECKITEM));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        if (CmnStringUtil.isEmpty(createCheckItemPage.getRegisteredName())) {
            createCheckItemPage.setMessage(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.INPUT_CHECKITEM_SUBJECT),
                    IMessageProvider.ERROR);
            return false;
        } else {
            // Adding a check list information
            registerCheckItem(selectedEntry);
            return true;
        }

    }

    /**
     * Focus on the check item details tab.<br/>
     * It displays an error message if the editor know-how is not open.<br/>
     * 
     * @param knowhowListViewData
     *            PortabilityKnowhowListViewOperation
     */
    private void focusCheckItemPage(
            PortabilityKnowhowListViewOperation knowhowListViewData) {
        MaintenanceKnowhowMultiPageEditor knowhowEditor = PluginUtil
                .getKnowhowEditor();
        if (knowhowEditor != null) {
            knowhowEditor
                    .focusPageControl(PortabilityKnowhowListViewData.LEVEL_THIRD);
            knowhowEditor.clearPageData();
            CheckItemFieldsPage checkItemFieldsPage = knowhowEditor
                    .getCheckItemFieldsPage();
            checkItemFieldsPage.updateCheckItemBrock(knowhowListViewData);
            knowhowEditor.setDirty(true);
        } else {
            JbmException.outputExceptionLog(LOGGER, null, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.NOT_OPEN_KNOWHOW_XML));
            ErrorDialog
                    .openErrorDialog(
                            PluginUtil.getActiveWorkbenchShell(),
                            null,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.NOT_OPEN_KNOWHOW_XML));
        }
    }

    /**
     * The registration check item to view entry know-how.<br/>
     * 
     * @param portabilityKnowhowListViewData
     *            PortabilityKnowhowListViewData
     */
    private void registerCheckItem(
            PortabilityKnowhowListViewOperation portabilityKnowhowListViewData) {
        PortabilityKnowhowListViewOperation childListViewData = null;
        if (isRegisterCheckItem(portabilityKnowhowListViewData)) {
            childListViewData = ViewUtil.registerViewData(selectedEntry,
                    createCheckItemPage,
                    EntryItemManagement.checkItemNumbering(),
                    PortabilityKnowhowListViewData.LEVEL_THIRD);
            focusCheckItemPage(childListViewData);
        }
    }

    /**
     * Information of the registered destination is determined whether the
     * know-how under. <br/>
     * The return to true can be registered if.<br/>
     * 
     * @param portabilityKnowhowListViewData
     *            PortabilityKnowhowListViewOperation
     * @return true:Registered false:Rejection
     */
    private boolean isRegisterCheckItem(
            PortabilityKnowhowListViewOperation portabilityKnowhowListViewData) {
        if (portabilityKnowhowListViewData == null
                || !portabilityKnowhowListViewData.isKnowhow()) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {

        // Wizard generation of checklist
        this.createCheckItemPage = new CreateCheckItemPage(
                ResourceUtil.createCheckItemInfo,
                ResourceUtil.registerCheckItemInfo);
        this.createCheckItemPage
                .setDescription(ResourceUtil.createCheckItemDescription);

        super.addPage(createCheckItemPage);

    }
}
