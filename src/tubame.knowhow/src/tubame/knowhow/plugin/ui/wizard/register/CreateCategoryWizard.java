/*
 * CreateCategoryWizard.java
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
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.ViewUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Category registration Wizard class know-how to the entry view.<br/>
 */
public class CreateCategoryWizard extends Wizard implements INewWizard {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CreateCategoryWizard.class);
    /** Wizard registration information */
    private CreateCategoryPage createCategoryPage;
    /** Additional target entry */
    private PortabilityKnowhowListViewOperation selectedEntry;

    /**
     * Constructor.<br/>
     * 
     * @param window
     *            IWorkbenchWindow
     * @param windowTitle
     *            Window title
     * @param selectedEntry
     *            Additional target entry
     */
    public CreateCategoryWizard(IWorkbenchWindow window, String windowTitle,
            PortabilityKnowhowListViewOperation selectedEntry) {
        super();
        super.setWindowTitle(windowTitle);
        this.selectedEntry = selectedEntry;
        CreateCategoryWizard.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_CREATE_CATEGORY));
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
        if (CmnStringUtil.isEmpty(createCategoryPage.getRegisteredName())) {
            createCategoryPage.setMessage(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.INPUT_CATEGORY_SUBJECT),
                    IMessageProvider.ERROR);
            return false;
        } else {
            try {
                registerCategoryEntry();
            } catch (JbmException e) {
                JbmException
                        .outputExceptionLog(
                                LOGGER,
                                e,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.FAIL_CREATE_CATEGORY));
                ErrorDialog
                        .openErrorDialog(
                                PluginUtil.getActiveWorkbenchShell(),
                                null,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.FAIL_CREATE_CATEGORY));
                return false;
            }
            return true;
        }
    }

    /**
     * Register a category to items under or top level items that have been
     * selected.<br/>
     * 
     * @throws JbmException
     *             Jbm exception
     * 
     */
    private void registerCategoryEntry() throws JbmException {
        if (selectedEntry != null) {
            registerCategoryToListVew(selectedEntry);
        } else {
            registerNewCategoryToListVew();
        }
    }

    /**
     * Focus on the documentation tab.<br/>
     * It displays an error message if the editor is not open.<br/>
     * 
     */
    private void focusDocumentationPage() {
        MaintenanceKnowhowMultiPageEditor knowhowEditor = PluginUtil
                .getKnowhowEditor();
        if (knowhowEditor != null) {
            knowhowEditor
                    .focusPageControl(PortabilityKnowhowListViewData.LEVEL_FIRST);
            knowhowEditor.clearPageData();
            // Update notification of the editor
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
     * The new registration category the know-how to the entry view.<br/>
     * 
     * @throws JbmException
     *             Jbm exception
     * 
     */
    private void registerNewCategoryToListVew() throws JbmException {
        try {
            ViewUtil.registerNewViewData(createCategoryPage,
                    EntryItemManagement.categoryNumbering(),
                    PortabilityKnowhowListViewData.LEVEL_FIRST);
            focusDocumentationPage();
        } catch (JbmException e) {
            throw new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.FAIL_CREATE_CATEGORY),
                    e);
        }
    }

    /**
     * Registering a category to view entry know-how.<br/>
     * 
     * @param knowhowListViewOperation
     *            PortabilityKnowhowListViewData
     */
    private void registerCategoryToListVew(
            PortabilityKnowhowListViewOperation knowhowListViewOperation) {
        if (isRegisterCategory(knowhowListViewOperation)) {
            ViewUtil.registerViewData(knowhowListViewOperation,
                    createCategoryPage,
                    EntryItemManagement.categoryNumbering(),
                    PortabilityKnowhowListViewData.LEVEL_FIRST);
            focusDocumentationPage();
        }
    }

    /**
     * Information of the registered destination is determined whether a
     * subordinate category.<br/>
     * And return true if it can be registered.<br/>
     * 
     * @param knowhowListViewOperation
     *            PortabilityKnowhowListViewData
     * @return true:Registered false:Rejection
     */
    private boolean isRegisterCategory(
            PortabilityKnowhowListViewOperation knowhowListViewOperation) {
        if (knowhowListViewOperation.getLevel() != PortabilityKnowhowListViewData.LEVEL_FIRST) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {

        // Wizard generation of category creation
        this.createCategoryPage = new CreateCategoryPage(
                ResourceUtil.createCategory, ResourceUtil.registerCategory);
        this.createCategoryPage
                .setDescription(ResourceUtil.createCategoryDescription);

        super.addPage(createCategoryPage);
    }
}
