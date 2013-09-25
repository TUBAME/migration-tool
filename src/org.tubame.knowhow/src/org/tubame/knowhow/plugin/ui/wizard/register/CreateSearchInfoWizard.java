/*
 * CreateSearchInfoWizard.java
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
package org.tubame.knowhow.plugin.ui.wizard.register;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.logic.EntryItemManagement;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import org.tubame.knowhow.plugin.ui.editor.multi.checkitem.CheckItemFieldsPage;
import org.tubame.knowhow.util.PluginUtil;
import org.tubame.knowhow.util.ViewUtil;
import org.tubame.knowhow.util.resource.ResourceUtil;

/**
 * Check items Registration Wizard class know-how to the entry view.<br/>
 */
public class CreateSearchInfoWizard extends Wizard implements INewWizard {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CreateSearchInfoWizard.class);
    /** Wizard registration information */
    private CreateSearchInfoPage createSearchInfoPage;
    /** Additional target entry */
    private PortabilityKnowhowListViewOperation selectedEntry;

    /**
     * Constructor.
     * 
     * @param window
     *            IWorkbenchWindow
     * @param windowTitle
     *            Title
     * @param selectedEntry
     *            Add a entry
     */
    public CreateSearchInfoWizard(IWorkbenchWindow window, String windowTitle,
            PortabilityKnowhowListViewOperation selectedEntry) {
        super();
        super.setWindowTitle(windowTitle);
        this.selectedEntry = selectedEntry;
        CreateSearchInfoWizard.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_CREATE_SEARCHINFO));
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
        // Adding a check list information
        registerSearchInfo(selectedEntry);
        return true;
    }

    /**
     * Subscribe to view the search information.<br/>
     * 
     * @param childListViewData
     *            Search information items
     */
    private void registerSearchInfo(
            PortabilityKnowhowListViewOperation childListViewData) {
        // Add Condition
        ViewUtil.registerViewData(childListViewData, createSearchInfoPage,
                EntryItemManagement.searchInfoNumbering(),
                PortabilityKnowhowListViewData.LEVEL_FOURTH);
        focusCheckItemPage(childListViewData);
    }

    /**
     * Focus on the check item details tab.<br/>
     * 
     * @param knowhowListViewOperation
     *            Check Item Information
     */
    private void focusCheckItemPage(
            PortabilityKnowhowListViewOperation knowhowListViewOperation) {
        MaintenanceKnowhowMultiPageEditor knowhowEditor = PluginUtil
                .getKnowhowEditor();
        if (knowhowEditor != null) {
            knowhowEditor
                    .focusPageControl(PortabilityKnowhowListViewData.LEVEL_THIRD);
            knowhowEditor.clearPageData();
            CheckItemFieldsPage checkItemFieldsPage = knowhowEditor
                    .getCheckItemFieldsPage();
            checkItemFieldsPage.updateCheckItemBrock(knowhowListViewOperation);
            knowhowEditor.setDirty(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {

        // Wizard generation of checklist
        this.createSearchInfoPage = new CreateSearchInfoPage(
                ResourceUtil.createSearchInfo,
                ResourceUtil.registerSearchInfoTitle);
        this.createSearchInfoPage
                .setDescription(ResourceUtil.createSearchDescription);
        super.addPage(createSearchInfoPage);

    }
}
