/*
 * SaveOutputAction.java
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
package org.tubame.knowhow.plugin.ui.action;

import java.lang.reflect.InvocationTargetException;

import org.tubame.common.util.CmnFileUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.exception.JbmException;
import org.tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.logic.FileManagement;
import org.tubame.knowhow.plugin.logic.FileManagement.XML_TYPE;
import org.tubame.knowhow.plugin.ui.dialog.ConfirmDialog;
import org.tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import org.tubame.knowhow.plugin.ui.dialog.WaitConvertHtmlProgress;
import org.tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import org.tubame.knowhow.util.PluginUtil;
import org.tubame.knowhow.util.resource.ResourceUtil;

/**
 * Class of storage action.<br/>
 * Class that controls the preservation of know-how or XML DocBook.<br/>
 */
public class SaveOutputAction implements IEditorActionDelegate {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SaveOutputAction.class);
    /** Know-how XML save ID */
    private static final String SAVE_KNOWHOW_XML_ACTION_ID = "org.tubame.knowhow.maintenance.portability.ui.actions.SaveKnowhowAction";
    /** DocBook save ID */
    private static final String SAVE_DOCBOOK_ACTION_ID = "org.tubame.knowhow.maintenance.portability.ui.actions.SaveDocBookAction";

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        SaveOutputAction.LOGGER.debug("[action]" + action);
        MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil
                .getKnowhowEditor();
        if (isOutputFile(knowhowMultiPageEditor)) {
            String targetFilePath = knowhowMultiPageEditor
                    .getFileLocationFullPath();
            String targetFileName = CmnFileUtil.getFileName(targetFilePath);
            FileDialog dialog = new FileDialog(
                    PluginUtil.getActiveWorkbenchShell(), SWT.SAVE);
            if (SAVE_KNOWHOW_XML_ACTION_ID.equals(action.getId())) {
                saveKnowhowXml(dialog, targetFilePath, targetFileName);
            } else if (SAVE_DOCBOOK_ACTION_ID.equals(action.getId())) {
                saveDocBook(dialog, targetFilePath, targetFileName);
            }
            SaveOutputAction.LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_OUTPUT_KNOWHOW));
        }
    }

    /**
     * Determining whether the output file is possible.<br/>
     * The warning dialog if the file unsaved at the time, and return false.<br/>
     * 
     * @param knowhowMultiPageEditor
     *            Know-how editor
     * @return true:Know-how save false:Know-how can not be saved
     */
    private boolean isOutputFile(
            MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor) {
        if (knowhowMultiPageEditor.isDirty()) {
            JbmException
                    .outputExceptionLog(
                            LOGGER,
                            null,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.SAVE_PROCESS_KNOWHOW_EDITOR));
            ErrorDialog
                    .openErrorDialog(
                            PluginUtil.getActiveWorkbenchShell(),
                            null,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.SAVE_PROCESS_KNOWHOW_EDITOR));
            return false;
        }
        return true;
    }

    /**
     * Save (DocBook) know-how HTML.<br/>
     * 
     * @param dialog
     *            Dialog
     * @param targetFilePath
     *            Save target file path
     * @param targetFileName
     *            Save target file name
     * @return true:Successful save false:Save failure
     */
    private boolean saveDocBook(FileDialog dialog, String targetFilePath,
            String targetFileName) {
        SaveOutputAction.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_SAVE_HTML));
        String changeHtmlFileName = CmnFileUtil.extensionChange(targetFileName,
                ApplicationPropertiesUtil
                        .getProperty(ApplicationPropertiesUtil.HTML));
        String[] exts = { ApplicationPropertiesUtil
                .getProperty(ApplicationPropertiesUtil.HTML_EXTENSION) };
        dialog.setFilterExtensions(exts);
        dialog.setText(ResourceUtil.saveDocBook);
        dialog.setFileName(changeHtmlFileName);
        String outputFilePath = dialog.open();
        if (outputFilePath != null) {
            try {
                SaveOutputAction.LOGGER.info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_START_SAVE_HTML));
                ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(
                        PluginUtil.getActiveWorkbenchShell());
                WaitConvertHtmlProgress convertHtmlThread = new WaitConvertHtmlProgress(
                        targetFilePath, outputFilePath, XML_TYPE.KNOWHOW_XML);
                progressDialog.run(true, true, convertHtmlThread);
            } catch (InvocationTargetException e) {
                ErrorDialog
                        .openErrorDialog(PluginUtil.getActiveWorkbenchShell(),
                                e, e.getMessage());
                return false;
            } catch (InterruptedException e) {
                ConfirmDialog
                        .openInformation(
                                PluginUtil.getActiveWorkbenchShell(),
                                ResourceUtil.convertHtml,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.PRESS_CANCEL_BUTTON));
                return false;
            }
            SaveOutputAction.LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_STOP_SAVE_HTML));
            ConfirmDialog
                    .openInformation(
                            PluginUtil.getActiveWorkbenchShell(),
                            ResourceUtil.saveDocBook,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.PERFORM_SAVE_KNOWHOW_HTML));
            return true;
        }
        return false;
    }

    /**
     * Save the know-how XML.<br/>
     * 
     * @param dialog
     *            Dialog
     * @param targetFilePath
     *            Save target file path
     * @param targetFileName
     *            Save target file name
     * @return true:Successful save false:Save failure
     */
    private boolean saveKnowhowXml(FileDialog dialog, String targetFilePath,
            String targetFileName) {
        SaveOutputAction.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_SAVE_XML));
        // Settings dialog
        String[] exts = { ApplicationPropertiesUtil
                .getProperty(ApplicationPropertiesUtil.XML_EXTENSION) };
        dialog.setFilterExtensions(exts);
        dialog.setText(ResourceUtil.saveKnowhowXml);
        dialog.setFileName(targetFileName);
        String outputFilePath = dialog.open();
        if (outputFilePath != null) {
            SaveOutputAction.LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_START_SAVE_XML));
            FileManagement.outputXML(targetFilePath, outputFilePath);
            SaveOutputAction.LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_STOP_SAVE_XML));
            ConfirmDialog
                    .openInformation(
                            PluginUtil.getActiveWorkbenchShell(),
                            ResourceUtil.saveKnowhowXml,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.PERFORM_SAVE_KNOWHOW_XML));
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        // no operation
    }

}
