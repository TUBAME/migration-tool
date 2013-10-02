/*
 * KnowhowDetailEditor.java
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
package tubame.knowhow.plugin.ui.editor.multi.docbook;

import tubame.common.util.CmnStringUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.view.KnowhowDetailType;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.plugin.ui.editor.multi.listener.DirtyListener;
import tubame.knowhow.util.PluginUtil;

/**
 * Class that controls the know-how detail editor.<br/>
 */
public class KnowhowDetailEditor extends StructuredTextEditor {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowDetailEditor.class);
    /** Know-how detail data model. */
    private KnowhowDetailType knowhowDetailType;
    /** Editor know-how. */
    private MaintenanceKnowhowMultiPageEditor maintenanceKnowhowMultiPageEditor;

    /**
     * Constructor.<br/>
     * 
     * @param maintenanceKnowhowMultiPageEditor
     *            Know-how editor
     */
    public KnowhowDetailEditor(
            MaintenanceKnowhowMultiPageEditor maintenanceKnowhowMultiPageEditor) {
        super();
        KnowhowDetailEditor.LOGGER
                .info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_CREATE_ENABLE_EDIT_KNOWHOWPAGE));
        this.maintenanceKnowhowMultiPageEditor = maintenanceKnowhowMultiPageEditor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleEditorInputChanged() {
        KnowhowDetailEditor.LOGGER.debug(CmnStringUtil.EMPTY);
        final IDocumentProvider provider = getDocumentProvider();
        IEditorInput input = getEditorInput();
        if (provider instanceof IDocumentProviderExtension) {
            IDocumentProviderExtension extension = (IDocumentProviderExtension) provider;
            try {
                extension.synchronize(input);
            } catch (CoreException e) {
                JbmException
                        .outputExceptionLog(
                                LOGGER,
                                e,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.FATAL_UPDATE_KNOWHOW_DETAIL));
            }
        }
    }

    /**
     * Set the data obtained from the know-how information on Know-how detail
     * attachment.<br/>
     * 
     * @param knowhowDetailTempFile
     *            Know-how detail attachment path
     * @param dirty
     *            Save state setting
     */
    public void setRegisterKnowhowDetail(String knowhowDetailTempFile,
            boolean dirty) {
        KnowhowDetailEditor.LOGGER.debug("[knowhowDetailTempFile]"
                + knowhowDetailTempFile + "[dirty]" + dirty);
        try {
            addDocumentListener();
            KnowhowManagement.docBookWrite(knowhowDetailTempFile,
                    knowhowDetailType);
            // Editor update process know-how
            handleEditorInputChanged();
            maintenanceKnowhowMultiPageEditor.setDirty(dirty);
        } catch (JbmException e) {
            JbmException.outputExceptionLog(LOGGER, e, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.FAIL_WRITE_FILE));
            ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(),
                    e, MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.FAIL_WRITE_FILE));
        }
    }

    /**
     * Add the Save the listener of Know-how detail editor when editing.<br/>
     * 
     */
    private void addDocumentListener() {
        IDocument document = getSourceViewer().getDocument();
        document.addDocumentPartitioningListener(new DirtyListener(
                maintenanceKnowhowMultiPageEditor));
        document.addDocumentListener(new DirtyListener(
                maintenanceKnowhowMultiPageEditor));
    }

    /**
     * Get knowhowDetailType.<br/>
     * 
     * @return Know-how detail data model
     */
    public KnowhowDetailType getKnowhowDetailType() {
        KnowhowDetailEditor.LOGGER.debug(CmnStringUtil.EMPTY);
        return knowhowDetailType;
    }

    /**
     * Set knowhowDetailType.<br/>
     * 
     * @param knowhowDetailType
     *            Know-how detail data model
     */
    public void setKnowhowDetailType(KnowhowDetailType knowhowDetailType) {
        KnowhowDetailEditor.LOGGER.debug("[knowhowDetailType]"
                + knowhowDetailType);
        this.knowhowDetailType = knowhowDetailType;
    }

    /**
     * If there is a change in the Know-how detail page, <br/>
     * and temporary storage in memory on the Know-how detail information.<br/>
     * 
     * @return true:Writing success false:Write failure
     */
    public boolean temporaryStorageKnowhowDetailOnMemory() {
        KnowhowDetailEditor.LOGGER.debug(CmnStringUtil.EMPTY);
        if (isDirty()) {
            doSave(PluginUtil.getProgressMonitor());
        }
        // Get display original data
        KnowhowDetailType knowhowDetailType = getKnowhowDetailType();
        if (knowhowDetailType != null) {
            return storageDocBook(knowhowDetailType);
        }
        return true;
    }

    /**
     * Save DocBook data. <br/>
     * If it is successfully saved, and return true.<br/>
     * 
     * @param knowhowDetailType
     *            Know-how detail information
     * @return true:Normal false:Abnormality
     */
    private boolean storageDocBook(KnowhowDetailType knowhowDetailType) {
        if (KnowhowManagement.isExistKnowhowDetail(knowhowDetailType)) {
            // Overwrite to change data to display original data
            try {
                knowhowDetailType.setDocbookdata(KnowhowManagement
                        .readEditingDocBook(maintenanceKnowhowMultiPageEditor
                                .getKnowhowDetailTempFile()));
                KnowhowManagement.addKnowhowDetail(knowhowDetailType);
                return true;
            } catch (JbmException e) {
                JbmException
                        .outputExceptionLog(
                                LOGGER,
                                e,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.MAKE_CHECK_SYNTAX));
                ErrorDialog.openErrorDialog(PluginUtil
                        .getActiveWorkbenchShell(), e, MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.MAKE_CHECK_SYNTAX));
            }
        }
        return false;
    }
}
