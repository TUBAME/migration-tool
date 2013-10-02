/*
 * InsertTagMenuAction.java
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

import tubame.common.util.CmnStringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.dialog.TableTagDialog;
import tubame.knowhow.plugin.ui.editor.multi.docbook.KnowhowDetailEditor;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Insert tag of any know-how detail tab.<br/>
 * Insert tag is selected of know-how detail tab (cursor is hitting) in place.<br/>
 */
public class InsertTagMenuAction implements IEditorActionDelegate {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(InsertTagMenuAction.class);
    /** IMAGE tag */
    private static final String INSERT_IMAGE_TAG = "tubame.knowhow.maintenance.portability.ui.actions.InsertImageTag";
    /** CODE tag */
    private static final String INSERT_CODE_TAG = "tubame.knowhow.maintenance.portability.ui.actions.InsertCodeTag";
    /** TABLE tag */
    private static final String INSERT_TABLE_TAG = "tubame.knowhow.maintenance.portability.ui.actions.InsertTableTag";
    /** TEXT tag */
    private static final String INSERT_TEXT_TAG = "tubame.knowhow.maintenance.portability.ui.actions.InsertTextTag";
    /** VARLISTENTRY tag */
    private static final String INSERT_VARLISTENTRY_TAG = "tubame.knowhow.maintenance.portability.ui.actions.InsertVarlistentryTag";
    /** know-how detail editor */
    private KnowhowDetailEditor knowhowDetailEditor;
    /** Document */
    private IDocument document;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        LOGGER.debug("[action]" + action);

        String insertData = CmnStringUtil.EMPTY;
        StringBuilder templateFilePath = new StringBuilder();
        templateFilePath.append(PluginUtil.getPluginDir());
        // Generation of document
        createDetailEditorData();
        if (!knowhowDetailEditor.isEditorInputReadOnly()) {
            String indentationSpace = null;
            try {
                indentationSpace = createIndentationSpace();
                if (INSERT_TEXT_TAG.equals(action.getId())) {
                    templateFilePath.append(ResourceUtil.templateTextTagFile);
                    insertData = KnowhowManagement.getTemplateTag(
                            templateFilePath.toString(), indentationSpace);
                } else if (INSERT_CODE_TAG.equals(action.getId())) {
                    templateFilePath.append(ResourceUtil.templateCodeTagFile);
                    insertData = KnowhowManagement.getTemplateTag(
                            templateFilePath.toString(), indentationSpace);
                } else if (INSERT_VARLISTENTRY_TAG.equals(action.getId())) {
                    templateFilePath
                            .append(ResourceUtil.templateVarlistentryTagFile);
                    insertData = KnowhowManagement.getTemplateTag(
                            templateFilePath.toString(), indentationSpace);
                } else if (INSERT_TABLE_TAG.equals(action.getId())) {
                    templateFilePath.append(ResourceUtil.templateTableTagFile);
                    insertData = createTableTagData(templateFilePath,
                            indentationSpace);
                } else if (INSERT_IMAGE_TAG.equals(action.getId())) {
                    templateFilePath.append(ResourceUtil.templateImageTagFile);
                    insertData = createImageTagData(templateFilePath,
                            indentationSpace);
                }
                insertTextEditor(insertData);
                LOGGER.info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_INSERT_TAG));
            } catch (JbmException e) {
                JbmException
                        .outputExceptionLog(
                                LOGGER,
                                e,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.FAIL_GET_INDENTATION_SPACE));
                ErrorDialog
                        .openErrorDialog(
                                PluginUtil.getActiveWorkbenchShell(),
                                e,
                                MessagePropertiesUtil.getMessage(MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.FAIL_GET_INDENTATION_SPACE)));
            }
        }
    }

    /**
     * Generate indentation space.<br/>
     * 
     * @return Indent space
     * @throws JbmException
     *             Jbm exception
     */
    private String createIndentationSpace() throws JbmException {
        StringBuilder indentationSpaceStr = new StringBuilder();
        try {
            int startLine = getTextSelection().getStartLine();
            int lineOffset = document.getLineInformation(startLine).getOffset();
            int indentationSpace = document.getLineInformationOfOffset(
                    lineOffset).getLength();
            for (int spaceIndex = 0; spaceIndex < indentationSpace; spaceIndex++) {
                indentationSpaceStr.append(CmnStringUtil.BLANK);
            }
            return indentationSpaceStr.toString();
        } catch (BadLocationException e) {
            throw new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.FAIL_GET_INDENTATION_SPACE),
                    e);
        }
    }

    /**
     * Generate information of know-how detail tab.<br/>
     * Generate documentation data and know-how editor information.<br/>
     * 
     */
    private void createDetailEditorData() {
        // Generation of know-how editor
        this.knowhowDetailEditor = PluginUtil.getKnowhowEditor()
                .getKnowhowDetailEditor();
        // Generation of document
        IEditorInput editorInput = knowhowDetailEditor.getEditorInput();
        this.document = knowhowDetailEditor.getDocumentProvider().getDocument(
                editorInput);
    }

    /**
     * Get TextSelection.<br/>
     * 
     * @return TextSelection
     */
    private TextSelection getTextSelection() {
        return (TextSelection) knowhowDetailEditor.getSelectionProvider()
                .getSelection();
    }

    /**
     * Generate a table tag data.<br/>
     * Generates a table tag of row * column number specified in the row *
     * column input dialog, return.<br/>
     * 
     * @param templateFilePath
     *            Template file path
     * @param indentationSpace
     *            Indent space
     * @return Insert data
     * @throws JbmException
     *             Jbm exception
     */
    private String createTableTagData(StringBuilder templateFilePath,
            String indentationSpace) throws JbmException {
        String insertData = null;
        TableTagDialog tableTagDialog = new TableTagDialog(
                PluginUtil.getActiveWorkbenchShell());
        if (IDialogConstants.OK_ID == tableTagDialog.open()) {
            // From the data matrix, the process of inserting a row entry tag
            insertData = KnowhowManagement.getReplacementTemplateTag(
                    templateFilePath.toString(),
                    indentationSpace,
                    createRowAndEntry(tableTagDialog.getRowIndex(),
                            tableTagDialog.getEntryIndex(), indentationSpace));
        }
        return insertData;
    }

    /**
     * Generate image tag data.<br/>
     * Return the data that is converted to * base64 format the image data of
     * the image tag when inserting from file input dialog.<br/>
     * 
     * @param templateFilePath
     *            Template file path
     * @param indentationSpace
     *            Indent space
     * @return Insert data
     * @throws JbmException
     *             Jbm exception
     */
    private String createImageTagData(StringBuilder templateFilePath,
            String indentationSpace) throws JbmException {
        String insertData = null;
        IFile searchTargetFile = fileBrowse();
        if (searchTargetFile != null) {
            insertData = KnowhowManagement.getReplacementTemplateTag(
                    templateFilePath.toString(),
                    indentationSpace,
                    createFileRefStr(searchTargetFile.getProjectRelativePath()
                            .toString(), searchTargetFile.getFileExtension()));
        }
        return insertData;
    }

    /**
     * View the file selection dialog.<br/>
     * Called when the know-how detail editor "Image tag Insert" menu is
     * pressed.<br/>
     * 
     * @return File
     */
    protected IFile fileBrowse() {
        String filePath = PluginUtil.getKnowhowEditor()
                .getKnowhowDetailRelativeFilePath();
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
                null, new WorkbenchLabelProvider(),
                new WorkbenchContentProvider());
        IProject project = PluginUtil.getProject(filePath);
        dialog.setInput(getDialogInput(project));
        dialog.setMessage(getDialogMessage(project));
        dialog.setTitle(ResourceUtil.selectImageFile);
        dialog.setHelpAvailable(false);
        dialog.addFilter(new ViewerFilter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                    Object element) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    if (file.getFileExtension() == null
                            || !isImageExtension(file.getFileExtension())) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * Determines whether the file to be added to the file selection
             * dialog.<br/>
             * Return True if file to be added.<br/>
             * 
             * @param fileExtension
             *            Extension
             * @return true:Selection false:Non-selection
             */
            private boolean isImageExtension(String fileExtension) {
                String[] extensions = ResourceUtil.imageFileExtension
                        .split(CmnStringUtil.CSV_DELIMITER);
                for (String extension : extensions) {
                    if (fileExtension.equals(extension)) {
                        return true;
                    }
                }
                return false;
            }
        });
        // Open the dialog.
        if (dialog.open() == Window.OK) {
            Object result = dialog.getFirstResult();
            if (result instanceof IFile) {
                return (IFile) result;
            }
        }
        return null;
    }

    /**
     * Get the resources to be input in the dialog.<br/>
     * Return the project under if the project is (or input) selection.<br/>
     * Return the workspace under when it is not selected.<br/>
     * 
     * @param project
     *            Project that is (or input) selection
     * @return Resources to be input in the dialog
     */
    private IResource getDialogInput(IProject project) {
        if (project != null) {
            return project;
        } else {
            return (IResource) ResourcesPlugin.getWorkspace();
        }
    }

    /**
     * Get the message dialog.<br/>
     * If the project is (or input) is selected, the display in addition to the
     * project name.<br/>
     * 
     * @param project
     *            Project that is (or input) selection
     * @return Message
     */
    private String getDialogMessage(IProject project) {
        String projectName = CmnStringUtil.EMPTY;
        if (project != null) {
            projectName = project.getName();
        }
        return String.format(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.SELECT_INSERT_IMAGE_FILE),
                projectName);
    }

    /**
     * Generate the column information and line information.<br/>
     * Generate matrix tag that matches the row / column size specified in the
     * dialog.<br/>
     * 
     * @param rowIndex
     *            Line index
     * @param entryIndex
     *            Column index
     * @param indentationSpace
     *            Indent space
     * @return Replacement string of row / column
     */
    private String createRowAndEntry(int rowIndex, int entryIndex,
            String indentationSpace) {
        StringBuilder templateStr = new StringBuilder();
        // CheckStyle Magic number
        templateStr.append(CmnStringUtil.getTagStr("<tgroup cols='"
                + entryIndex + "'>", 1));
        templateStr.append(CmnStringUtil.newLine(indentationSpace));
        // CheckStyle Magic number
        templateStr.append(CmnStringUtil.getTagStr("<tbody>", 2));
        for (int i = 0; i < rowIndex; i++) {
            templateStr.append(CmnStringUtil.newLine(indentationSpace));
            // CheckStyle Magic number
            templateStr.append(CmnStringUtil.getTagStr("<row>", 3));
            for (int j = 0; j < entryIndex; j++) {
                templateStr.append(CmnStringUtil.newLine(indentationSpace));
                // CheckStyle Magic number
                templateStr.append(CmnStringUtil.getTagStr(
                        "<entry>TODO:Please enter the Entry</entry>", 4));
            }
            templateStr.append(CmnStringUtil.newLine(indentationSpace));
            // CheckStyle Magic number
            templateStr.append(CmnStringUtil.getTagStr("</row>", 3));
        }
        templateStr.append(CmnStringUtil.newLine(indentationSpace));
        // CheckStyle Magic number
        templateStr.append(CmnStringUtil.getTagStr("</tbody>", 2));
        templateStr.append(CmnStringUtil.newLine(indentationSpace));
        // CheckStyle Magic number
        templateStr.append(CmnStringUtil.getTagStr("</tgroup>", 1));
        return templateStr.toString();
    }

    /**
     * Create imagedata tag.<br/>
     * Setting and extension (relative) file path to the attributes of the
     * imagedata tag.<br/>
     * 
     * @param filePath
     *            Relative path of the file
     * @param extension
     *            Extension
     * @return replacement string imageData
     */
    private String createFileRefStr(String filePath, String extension) {
        // image tag
        return CmnStringUtil.getTagStr("<imagedata fileref=\"" + filePath
                + "\" format=\"" + extension + "\"/>", 3);
    }

    /**
     * Set the input tag string in the editor selection place.<br/>
     * 
     * @param insertData
     *            Input tag string
     */
    private void insertTextEditor(String insertData) {

        try {
            TextSelection textSelection = getTextSelection();
            document.replace(textSelection.getOffset(),
                    textSelection.getLength(), insertData);
        } catch (BadLocationException e) {
            JbmException
                    .outputExceptionLog(
                            LOGGER,
                            e,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.INAPPROPRIATE_INSERT_PLACE));
            ErrorDialog
                    .openErrorDialog(
                            PluginUtil.getActiveWorkbenchShell(),
                            e,
                            MessagePropertiesUtil.getMessage(MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.INAPPROPRIATE_INSERT_PLACE)));
        }
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
