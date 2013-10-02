/*
 * CreateKnowhowWizard.java
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
import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.view.KnowhowViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.plugin.ui.editor.multi.docbook.KnowhowDetailEditor;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.ViewUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Registration Wizard class know-how to the know-how entry view.<br/>
 */
public class CreateKnowhowWizard extends Wizard implements INewWizard {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CreateKnowhowWizard.class);
    /** Wizard registration information */
    private CreateKnowhowPage createKnowhowPage;
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
    public CreateKnowhowWizard(IWorkbenchWindow window, String windowTitle,
            PortabilityKnowhowListViewOperation selectedEntry) {
        super();
        super.setWindowTitle(windowTitle);
        this.selectedEntry = selectedEntry;
        CreateKnowhowWizard.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_CREATE_KNOWHOW));
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

        if (CmnStringUtil.isEmpty(createKnowhowPage.getRegisteredName())) {
            createKnowhowPage.setMessage(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.INPUT_KNOWHOW_SUBJECT),
                    IMessageProvider.ERROR);
            return false;
        } else {
            registerKnowhowEntry();
            return true;
        }
    }

    /**
     * Register the know-how to the item under the selected.<br/>
     * 
     */
    private void registerKnowhowEntry() {
        if (isRegisterKnowhow(selectedEntry)) {
            PortabilityKnowhowListViewOperation knowhowListViewOperation = ViewUtil
                    .registerViewData(selectedEntry, createKnowhowPage,
                            EntryItemManagement.knowhowNumbering(),
                            PortabilityKnowhowListViewData.LEVEL_SECOND);
            MaintenanceKnowhowMultiPageEditor knowhowEditor = PluginUtil
                    .getKnowhowEditor();
            if (knowhowEditor != null) {
                focusKnowhowdetailPage(knowhowEditor);
                createKnowhowDetail(knowhowEditor, knowhowListViewOperation);
            } else {
                JbmException
                        .outputExceptionLog(
                                LOGGER,
                                null,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.NOT_OPEN_KNOWHOW_XML));
                ErrorDialog
                        .openErrorDialog(
                                PluginUtil.getActiveWorkbenchShell(),
                                null,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.NOT_OPEN_KNOWHOW_XML));
            }
        }
    }

    /**
     * Generates detailed information know-how. To generate a template
     * information initial registration.<br/>
     * <?xml version="1.0" encoding="UTF-8" standalone="yes"?> <doc:article
     * xmlns:doc="http://docbook.org/ns/docbook"
     * xmlns:ns2="http://www.w3.org/1999/xlink"> <doc:info>
     * <doc:title>SUBJECT_NAME</doc:title> </doc:info> <doc:section>
     * <doc:title>SUBJECT_NAME</doc:title> <doc:para>PARA_CONTENT</doc:para>
     * </doc:section> </doc:article> </p>
     * 
     * @param knowhowEditor
     *            Know-how editor
     * @param knowhowListViewOperation
     *            Know-how items
     */
    private void createKnowhowDetail(
            MaintenanceKnowhowMultiPageEditor knowhowEditor,
            PortabilityKnowhowListViewOperation knowhowListViewOperation) {

        // knowhowEditor.clearPageData(1);

        // Registration Know how
        KnowhowViewType knowhowViewType = (KnowhowViewType) knowhowListViewOperation
                .getKnowhowViewType();
        String knowhowDetailId = EntryItemManagement.knowhowDetailNumbering();
        knowhowViewType.setKnowhowDetailRefKey(knowhowDetailId);
        knowhowListViewOperation.setKnowhowViewType(knowhowViewType);

        KnowhowDetailEditor knowhowDetailEditor = knowhowEditor
                .getKnowhowDetailEditor();
        if (knowhowDetailEditor.getKnowhowDetailType() != null) {
            knowhowEditor.isKnowhowDetailStorage();
        }
        knowhowDetailEditor.setKnowhowDetailType(KnowhowManagement
                .createTemplateKnowhowDetail(knowhowDetailId,
                        knowhowViewType.getRegisterName()));
        knowhowDetailEditor.setRegisterKnowhowDetail(
                knowhowEditor.getKnowhowDetailTempFile(), true);
    }

    /**
     * Focus on know-how details tab.<br/>
     * 
     * @param knowhowEditor
     *            Know-how editor
     */
    private void focusKnowhowdetailPage(
            MaintenanceKnowhowMultiPageEditor knowhowEditor) {
        knowhowEditor
                .focusPageControl(PortabilityKnowhowListViewData.LEVEL_SECOND);

    }

    /**
     * Information of the registered destination is determined whether a
     * subordinate category.<br/>
     * 
     * @param knowhowListViewOperation
     *            Entry items
     * @return ture:Registered false:Rejection
     */
    private boolean isRegisterKnowhow(
            PortabilityKnowhowListViewOperation knowhowListViewOperation) {
        if (knowhowListViewOperation == null
                || !knowhowListViewOperation.isCategory()) {
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
        this.createKnowhowPage = new CreateKnowhowPage(
                ResourceUtil.createknowhow, ResourceUtil.registerKnowhow);
        this.createKnowhowPage
                .setDescription(ResourceUtil.createknowhowDescription);

        super.addPage(createKnowhowPage);
    }
}
