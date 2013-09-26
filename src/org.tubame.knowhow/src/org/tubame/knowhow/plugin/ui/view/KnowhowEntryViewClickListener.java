/*
 * KnowhowEntryViewClickListener.java
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
package org.tubame.knowhow.plugin.ui.view;


import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.exception.JbmException;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.logic.KnowhowManagement;
import org.tubame.knowhow.plugin.model.view.KnowhowViewType;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import org.tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import org.tubame.knowhow.plugin.ui.editor.multi.checkitem.CheckItemFieldsPage;
import org.tubame.knowhow.plugin.ui.editor.multi.docbook.KnowhowDetailEditor;
import org.tubame.knowhow.util.PluginUtil;

/**
 * Class that controls the item when double-clicked of know-how entry view.<br/>
 */
public class KnowhowEntryViewClickListener implements IDoubleClickListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowEntryViewClickListener.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void doubleClick(DoubleClickEvent event) {
        KnowhowEntryViewClickListener.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_DOUBLE_CLICK_ITEM));
        // Editor acquisition
        MaintenanceKnowhowMultiPageEditor knowhowEditor = PluginUtil
                .getKnowhowEditor();

        IStructuredSelection structuredSelection = (IStructuredSelection) event
                .getSelection();
        PortabilityKnowhowListViewData knowhowListViewData = (PortabilityKnowhowListViewData) structuredSelection
                .getFirstElement();

        if (knowhowEditor != null) {
            if (isFocusPageControl(knowhowEditor)) {
                focusPageControl(knowhowEditor, knowhowListViewData);
            }
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
     * Judgment or page transition is possible.<br/>
     * Return False if there is a problem with the know-how editor.<br/>
     * 
     * @param knowhowEditor
     *            Know-how editor
     * @return true:Page transition possible false:Page transition not
     */
    private boolean isFocusPageControl(
            MaintenanceKnowhowMultiPageEditor knowhowEditor) {
        if (knowhowEditor.isKnowhowDetailStorage()) {
            if (knowhowEditor.check()) {
                return true;
            } else {
                JbmException
                        .outputExceptionLog(
                                LOGGER,
                                null,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.CHECK_NEST_SECTION_TAG));
                ErrorDialog
                        .openErrorDialog(
                                PluginUtil.getActiveWorkbenchShell(),
                                null,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.CHECK_NEST_SECTION_TAG));
            }
        }
        return false;
    }

    /**
     * Control the focus setting to the know-how of the tag editor.<br/>
     * Focus on any of the tags of know-how editor.<br/>
     * 
     * @param knowhowEditor
     *            Know-how editor
     * @param knowhowListViewOperation
     *            Know-how editor Item Information
     */
    private void focusPageControl(
            MaintenanceKnowhowMultiPageEditor knowhowEditor,
            PortabilityKnowhowListViewOperation knowhowListViewOperation) {
        if (PortabilityKnowhowListViewData.LEVEL_FIRST == knowhowListViewOperation
                .getLevel()) {
            forcusPage(knowhowEditor,
                    PortabilityKnowhowListViewData.LEVEL_FIRST);
        } else if (PortabilityKnowhowListViewData.LEVEL_SECOND == knowhowListViewOperation
                .getLevel()) {
            forcusPage(knowhowEditor,
                    PortabilityKnowhowListViewData.LEVEL_SECOND);
            // Reflect the data in front of screen transition
            reflectionKnowhowDetaildata(knowhowEditor, knowhowListViewOperation);
        } else if (PortabilityKnowhowListViewData.LEVEL_THIRD == knowhowListViewOperation
                .getLevel()) {
            forcusPage(knowhowEditor,
                    PortabilityKnowhowListViewData.LEVEL_THIRD);
            // Reflect the data in front of screen transition
            reflectionItemData(knowhowEditor, knowhowListViewOperation);
        }
    }

    /**
     * put a focus.<br/>
     * Clear the data after processing to give focus.<br/>
     * 
     * @param knowhowEditor
     *            Know-how editor
     * @param level
     *            Focus level
     */
    private void forcusPage(MaintenanceKnowhowMultiPageEditor knowhowEditor,
            int level) {
        knowhowEditor.focusPageControl(level);
        knowhowEditor.clearPageData();
    }

    /**
     * Reflected in the data editor of the items that have been registered. <br/>
     * 
     * @param focusPageControl
     *            MaintenanceKnowhowMultiPageEditor
     * @param knowhowListViewData
     *            PortabilityKnowhowListViewOperation
     */
    private void reflectionItemData(
            MaintenanceKnowhowMultiPageEditor focusPageControl,
            PortabilityKnowhowListViewOperation knowhowListViewData) {
        if (focusPageControl != null) {
            CheckItemFieldsPage checkItemFieldsPage = focusPageControl
                    .getCheckItemFieldsPage();
            checkItemFieldsPage.updateCheckItemBrock(knowhowListViewData);
        }
    }

    /**
     * Reflecting the editor for more information on know-how.<br/>
     * Reflecting the editor details of know-how that is selected in the
     * know-how entry view.<br/>
     * 
     * @param focusPageControl
     *            MaintenanceKnowhowMultiPageEditor
     * @param knowhowListViewOperation
     *            PortabilityKnowhowListViewOperation
     */
    private void reflectionKnowhowDetaildata(
            MaintenanceKnowhowMultiPageEditor focusPageControl,
            PortabilityKnowhowListViewOperation knowhowListViewOperation) {

        KnowhowViewType knowhowViewType = (KnowhowViewType) knowhowListViewOperation
                .getKnowhowViewType();
        String knowhowDetailRefKey = knowhowViewType.getKnowhowDetailRefKey();

        if (focusPageControl != null && knowhowDetailRefKey != null) {
            KnowhowDetailEditor knowhowDetailEditor = focusPageControl
                    .getKnowhowDetailEditor();
            // Get the display data, set to Know how tab object
            knowhowDetailEditor.setKnowhowDetailType(KnowhowManagement
                    .getKnowhowDetailInfo(knowhowDetailRefKey));
            knowhowDetailEditor.setRegisterKnowhowDetail(
                    focusPageControl.getKnowhowDetailTempFile(),
                    focusPageControl.getDirty());
        }
    }
}
