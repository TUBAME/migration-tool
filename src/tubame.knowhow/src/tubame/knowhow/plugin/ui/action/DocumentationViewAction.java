/*
 * DocumentationViewAction.java
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
package tubame.knowhow.plugin.ui.action;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.util.CmnFileUtil;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.logic.FileManagement.XML_TYPE;
import tubame.knowhow.plugin.ui.dialog.ConfirmDialog;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.dialog.WaitConvertHtmlProgress;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.util.FileUtil;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * viewed in a Web browser of Eclipse standard entry in the documentation tab.<br/>
 */
public class DocumentationViewAction implements IEditorActionDelegate {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentationViewAction.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        DocumentationViewAction.LOGGER.debug("[action]" + action);

        MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil
                .getKnowhowEditor();
        if (isDocumentationValidation(knowhowMultiPageEditor)) {
            try {
                String temporaryHtmlFilePath = getTemporaryHtmlFilePath(knowhowMultiPageEditor
                        .getKnowhowSelectionProject());
                // HTML save processing (Passing of XLS file)
                ProgressMonitorDialog dialog = new ProgressMonitorDialog(
                        PluginUtil.getActiveWorkbenchShell());
                WaitConvertHtmlProgress convertHtmlThread = new WaitConvertHtmlProgress(
                        knowhowMultiPageEditor.getFileLocationFullPath(),
                        temporaryHtmlFilePath, XML_TYPE.KNOWHOW_XML);
                dialog.run(true, true, convertHtmlThread);
                PluginUtil.refreshWorkSpace();
                String url = FileUtil.getURL(temporaryHtmlFilePath).toString();
                if (url != null) {
                    PluginUtil.openWebBrowzer(url);
                }
            } catch (InterruptedException e) {
                ConfirmDialog
                        .openInformation(
                                PluginUtil.getActiveWorkbenchShell(),
                                ResourceUtil.convertHtml,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.PRESS_CANCEL_BUTTON));
            } catch (InvocationTargetException e) {
                ErrorDialog
                        .openErrorDialog(PluginUtil.getActiveWorkbenchShell(),
                                e, e.getMessage());
            } catch (JbmException e) {
                ErrorDialog
                        .openErrorDialog(PluginUtil.getActiveWorkbenchShell(),
                                e, e.getMessage());
            }
        }
        DocumentationViewAction.LOGGER
                .info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_OPEN_ECLIPSE_STANDARD_WEBBROWSER));
    }

    
    private String getTemporaryHtmlFilePath(IProject project) {
        String temporaryHtmlFilePath = null;
        temporaryHtmlFilePath =  project.getLocation().toOSString()
                + CmnFileUtil.FILE_SEPARATOR
                + ApplicationPropertiesUtil
                        .getProperty(ApplicationPropertiesUtil.TEMPORARY_HTML_DOCUMENTATION);
        FileManagement.setKnowhowHtmlTempFilePath(temporaryHtmlFilePath);
            temporaryHtmlFilePath = FileManagement.getKnowhowHtmlTempFilePath();
        return temporaryHtmlFilePath;
    }

    /**
     * Determine whether can see in the Web browser of Eclipse standard.<br/>
     * The return to true to display if possible.<br/>
     * 
     * @param knowhowMultiPageEditor
     *            Know-how editor
     * @return true:Displayable false:Undisplayable
     */
    private boolean isDocumentationValidation(
            MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor) {
        if (knowhowMultiPageEditor == null) {
            JbmException.outputExceptionLog(LOGGER, null, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.FAIL_GET_KNOWHOW_EDIROR));
            ErrorDialog
                    .openErrorDialog(
                            PluginUtil.getActiveWorkbenchShell(),
                            null,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.FAIL_GET_KNOWHOW_EDIROR));
            return false;
        }
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
