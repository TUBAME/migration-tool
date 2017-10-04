/*
 * DocBookViewerMenuAction.java
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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
 * Viewed in a Web browser of Eclipse standard content that was entered on the
 * know-how detail editor.<br/>
 */
public class DocBookViewerMenuAction implements IEditorActionDelegate {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocBookViewerMenuAction.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        DocBookViewerMenuAction.LOGGER.debug("[action]" + action);

        MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil
                .getKnowhowEditor();
        String knowhowDetailFilePath = getKnowhowDetailFilePath(knowhowMultiPageEditor);
        if (isKnowhowDetailValidation(knowhowMultiPageEditor,
                knowhowDetailFilePath)) {
            try {
                String temporaryHtmlFilePath = getTemporaryHtmlFilePath(knowhowMultiPageEditor
                        .getKnowhowSelectionProject());
                              
                // HTML save processing (Passing of XLS file)
                ProgressMonitorDialog dialog = new ProgressMonitorDialog(
                        PluginUtil.getActiveWorkbenchShell());
                
                WaitConvertHtmlProgress convertHtmlThread = new WaitConvertHtmlProgress(
                        knowhowDetailFilePath, temporaryHtmlFilePath,
                        XML_TYPE.DOCBOOK_XML);
                
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
        DocBookViewerMenuAction.LOGGER
                .info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_OPEN_ECLIPSE_STANDARD_WEBBROWSER));
    }

 


	private String getTemporaryHtmlFilePath(IProject project) {
        String temporaryHtmlFilePath = null;
        temporaryHtmlFilePath = project.getLocation().toOSString()
                + CmnFileUtil.FILE_SEPARATOR 
                + ApplicationPropertiesUtil
                        .getProperty(ApplicationPropertiesUtil.TEMPORARY_HTML_KNOWHOWDETAIL);
        FileManagement.setDocBookHtmlTempFilePath(temporaryHtmlFilePath);
        return temporaryHtmlFilePath;
    }

    /**
     * Judgment know-how detail information you can check.<br/>
     * Return true if it can be confirmed.<br/>
     * 
     * @param knowhowMultiPageEditor
     *            Know-how editor
     * @param knowhowDetailFilePath
     *            Know-how detail attachments
     * @return true:Know-how detailView displayable false:know-how detailView
     *         undisplayable
     */
    private boolean isKnowhowDetailValidation(
            MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor,
            String knowhowDetailFilePath) {
        if (knowhowMultiPageEditor.getKnowhowDetailEditor()
                .isEditorInputReadOnly()) {
            return false;
        }
        if (!CmnFileUtil.fileExists(knowhowDetailFilePath)) {
            JbmException.outputExceptionLog(LOGGER, null, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.NOT_EXIST_DOCBOOK_FILE));
            ErrorDialog
                    .openErrorDialog(
                            PluginUtil.getActiveWorkbenchShell(),
                            null,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.NOT_EXIST_DOCBOOK_FILE));
            return false;
        }
        if (!knowhowMultiPageEditor.isKnowhowDetailStorage()) {
            return false;
        }
        return true;
    }

    /**
     * Get the attachment of know-how detail.<br/>
     * 
     * @param knowhowMultiPageEditor
     *            Know-how editor
     * @return Know-how detail attachments
     */
    private String getKnowhowDetailFilePath(
            MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor) {
        if (knowhowMultiPageEditor != null) {
            return knowhowMultiPageEditor.getKnowhowDetailTempFile();
        }
        return null;
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
