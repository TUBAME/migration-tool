/*
 * JbmConvertWizard.java
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
package org.tubame.portability.plugin.wizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.tubame.portability.exception.JbmException;
import org.tubame.portability.logic.JbmAccessFactory;
import org.tubame.portability.logic.convert.ConvertState;
import org.tubame.portability.logic.convert.ConvertWithProgress;
import org.tubame.portability.logic.convert.State;
import org.tubame.portability.logic.search.ConvertSearchToolWithProgress;
import org.tubame.portability.model.generated.model.ConvertType;
import org.tubame.portability.model.generated.model.JbossMigrationConvertTool;
import org.tubame.portability.model.generated.model.MigrationItem;
import org.tubame.portability.model.generated.model.MigrationTarget;
import org.tubame.portability.plugin.view.ConvertView;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.PythonUtil;
import org.tubame.portability.util.StringUtil;
import org.tubame.portability.util.resource.ApplicationPropertyUtil;
import org.tubame.portability.util.resource.MessageUtil;
import org.tubame.portability.util.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wizard class in the transformation input screen.<br/>
 * Take the following pages.<br/>
 * {@link JbmConvertSelectionPage}<br/>
 * 
 */
public class JbmConvertWizard extends Wizard implements INewWizard {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JbmConvertWizard.class);

    /**
     * Screen the Wizard uses
     */
    private final JbmConvertSelectionPage jbmConvertSelectionPage;

    /**
     * Get the title of the screen.<br/>
     * 
     * @return Title of the screen
     */
    public String getDialogTitle() {
        return ResourceUtil.DIALOG_CONVERT;
    }

    /**
     * Get the error terms of the cancellation.<br/>
     * 
     * @return Error message of cancellation
     */
    public String getErrorRunCancel() {
        return MessageUtil.INF_CONVERT_CANCEL;
    }

    /**
     * Get the error terms of the failure.<br/>
     * 
     * @return Error message on failure
     */
    public String getErrorRunFalse() {
        return MessageUtil.ERR_CONVERT_FAILED;
    }

    /**
     * Get the wording of the processing is completed.<br/>
     * 
     * @return Completion of message processing
     */
    public String getRunComplete() {
        return MessageUtil.INF_CONVERT_COMPLETE;
    }

    /**
     * Constructor.<br/>
     * 
     * @param window
     *            Window
     * @param resource
     *            Target resource
     */
    public JbmConvertWizard(IWorkbenchWindow window, IResource resource) {
        super();
        jbmConvertSelectionPage = new JbmConvertSelectionPage(resource);
        super.setWindowTitle(getDialogTitle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        LOGGER.info(String.format(
                MessageUtil.LOG_INFO_ACTION_P,
                ResourceUtil.DIALOG_CONVERT,
                MessageUtil.LOG_INFO_BTN_NAME_FINISH,
                "TargetText=[" + jbmConvertSelectionPage.getTargetText()
                        + "] OutSourceFolderText=["
                        + jbmConvertSelectionPage.getOutSourceFolderText()
                        + "] WebLogicVersionCombo=["
                        + jbmConvertSelectionPage.getWebLogicVersionCombo()
                        + "]"));
        boolean validate = jbmConvertSelectionPage.textValidate();
        // False if you get caught in the validate
        if (!validate) {
            return false;
        }
        boolean searchOk = false;
        ConvertView view = null;
        ConvertWithProgress convertProgress = null;
        String jbmFilePath = null;
        try {
            LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_START,
                    MessageUtil.LOG_INFO_PROC_NAME_CONVERT));

            // Get the view
            view = PluginUtil.getConvertView();
            jbmFilePath = PluginUtil.getResolvedPluginDir()
                    + ApplicationPropertyUtil.CONVERT_JBM_OUT_FILE_PATH;
            String target = PluginUtil.getFileFullPath(jbmConvertSelectionPage
                    .getTargetText());

            File deleteFile = new File(jbmFilePath);
            if (deleteFile != null && deleteFile.isFile()) {
                if (!deleteFile.delete()) {
                    LOGGER.warn(String.format(
                            MessageUtil.LOG_WARN_FILE_DELETE_FAIL, jbmFilePath));
                }
            }

            // Search process
            ConvertSearchToolWithProgress progress = new ConvertSearchToolWithProgress(
                    target,
                    PythonUtil
                            .getSearchKeywordFilePath(ApplicationPropertyUtil.SEARCH_CONVERT_KEYWORD_FILE),
                    jbmFilePath);
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(
                    PluginUtil.getActiveWorkbenchShell());

            dialog.run(true, true, progress);
            if (!progress.isFileOut()) {
                LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_END_P,
                        MessageUtil.LOG_INFO_PROC_NAME_CONVERT,
                        MessageUtil.INF_SEARCH_NON));
                PluginUtil.viewInfoDialog(getDialogTitle(),
                        MessageUtil.INF_SEARCH_NON);
                return false;
            }

            // The cancellation or if there is no conversion files, the process
            // is terminated
            if (!isJbmFileNormal(jbmFilePath)) {
                LOGGER.debug(MessageUtil.INF_CANCEL);
                return false;
            }
            searchOk = true;

            // Conversion process
            convertProgress = new ConvertWithProgress(
                    PluginUtil.getPluginDir(), PluginUtil.getWorkspaceRoot(),
                    jbmConvertSelectionPage.getOutSourceFolderText(),
                    jbmConvertSelectionPage.getWebLogicVersionCombo(),
                    jbmFilePath);
            dialog.run(true, true, convertProgress);
            // Refresh
            PluginUtil.refreshWorkSpace(dialog.getProgressMonitor());

            // Conversion Complete
            LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_END,
                    MessageUtil.LOG_INFO_PROC_NAME_CONVERT));
            State state = ConvertState.getInstance();
            if (state.isFailed()) {
                PluginUtil.viewErrorDialog(getDialogTitle(),
                        createRunMessage(convertProgress, getErrorRunFalse()),
                        null);
                // View error log view
                PluginUtil.showErrorLogView();
            } else {

                PluginUtil.viewInfoDialog(getDialogTitle(),
                        createRunMessage(convertProgress, getRunComplete()));
            }
            if (convertProgress.getConvertFileScanner().scanFileData()) {
                PluginUtil.viewInfoDialog(getDialogTitle(),
                        MessageUtil.CONVERTEDFILE_HAS_TODO);

            }

        } catch (IOException e) {
            LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
                    MessageUtil.LOG_INFO_PROC_NAME_CONVERT), e);
            PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse(), e);
            if (searchOk) {
                PluginUtil.viewErrorDialog(getDialogTitle(),
                        getErrorRunFalse(), e);
            } else {
                PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse()
                        + StringUtil.LINE_SEPARATOR + e.getMessage(), e);
            }
            return false;
        } catch (InterruptedException e) {
            // Cancellation
            // Refresh
            PluginUtil.refreshWorkSpace(null);
            PluginUtil.viewInfoDialog(getDialogTitle(), getErrorRunCancel());
            LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_CANCEL,
                    MessageUtil.LOG_INFO_PROC_NAME_CONVERT));
            LOGGER.debug(MessageUtil.INF_CANCEL);
            return false;
        } catch (InvocationTargetException e) {
            // Failure
            LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
                    MessageUtil.LOG_INFO_PROC_NAME_CONVERT), e);
            if (searchOk) {
                PluginUtil.viewErrorDialog(getDialogTitle(),
                        createRunMessage(convertProgress, getErrorRunFalse()),
                        e);
            } else {
                PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse()
                        + StringUtil.LINE_SEPARATOR + e.getMessage(), e);
            }
            return false;
        } catch (JbmException e) {
            LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
                    MessageUtil.LOG_INFO_PROC_NAME_CONVERT), e);
            if (searchOk) {
                PluginUtil.viewErrorDialog(getDialogTitle(),
                        createRunMessage(convertProgress, getErrorRunFalse()),
                        e);
            } else {
                PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse()
                        + StringUtil.LINE_SEPARATOR + e.getMessage(), e);
            }
            return false;
        } finally {
            if (view != null && convertProgress != null) {
                view.update(convertProgress.getTargetList(),
                        convertProgress.getConvertStatusFilePath());

            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performCancel() {
        LOGGER.info(String.format(MessageUtil.LOG_INFO_ACTION,
                ResourceUtil.DIALOG_CONVERT,
                MessageUtil.LOG_INFO_BTN_NAME_CANCEL));
        return super.performCancel();
    }

    /**
     * Create a message string result.<br/>
     * 
     * @param convertProgress
     *            Conversion result information
     * @param runMessage
     *            Message on the first line
     * @return String
     */
    private String createRunMessage(ConvertWithProgress convertProgress,
            String runMessage) {
        int okCount = 0;
        int ngCount = 0;
        if (convertProgress != null) {
            List<MigrationItem> itemList = convertProgress.getTargetList();
            for (MigrationItem item : itemList) {
                for (MigrationTarget migrationTarget : item
                        .getMigrationTargets().getMigrationTarget()) {
                    if (migrationTarget.isConvert()) {
                        okCount++;
                    } else {
                        ngCount++;
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(runMessage);
        sb.append(StringUtil.LINE_SEPARATOR);
        sb.append(String.format(MessageUtil.INF_CONVERT_OUT_MESSAGE, okCount,
                ngCount));
        return sb.toString();
    }

    /**
     * Get data exists in the jbm file.<br/>
     * 
     * @param jbmFilePath
     *            The path of the file jbm
     * @return boolean true:Data exists false:There is no data
     */
    private boolean isJbmFileNormal(String jbmFilePath) {
        try {
            // Jbm file read
            JbossMigrationConvertTool convertData = JbmAccessFactory
                    .getJbmReadFacade().readToJbossMigrationConvertTool(
                            jbmFilePath);

            // If there is data
            if (convertData.getMigrationItems().getMigrationItem().size() > 0) {
                // Transformation execution confirmation
                if (checkConvertTypeCount(convertData)) {
                    return true;
                }
                // Conversion cancellation
                LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_CANCEL,
                        MessageUtil.LOG_INFO_PROC_NAME_CONVERT));
                PluginUtil
                        .viewInfoDialog(getDialogTitle(), getErrorRunCancel());
                return false;
            }
        } catch (JbmException e) {
            // no oparation
        }
        // If there is no data
        LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_END_P,
                MessageUtil.LOG_INFO_PROC_NAME_CONVERT,
                MessageUtil.ERR_CONVERT_DATA_NOT_FOUND));
        PluginUtil.viewInfoDialog(getDialogTitle(),
                MessageUtil.ERR_CONVERT_DATA_NOT_FOUND);
        return false;
    }

    /**
     * Get How many there is a convertible data from the search results, <br/>
     * and display the conversion execution confirmation dialog.<br/>
     * 
     * @param convertData
     *            Search result data
     * @return boolean true:Dialog OK false:Dialog canceled
     */
    private boolean checkConvertTypeCount(JbossMigrationConvertTool convertData) {
        int ejbCount = 0;
        int cmpCount = 0;

        // Get to convert each type the number of convertible data
        for (MigrationItem item : convertData.getMigrationItems()
                .getMigrationItem()) {
            int count = item.getMigrationTargets().getMigrationTarget().size();
            if (ConvertType.EJB_JAR_XML_CONVERT.equals(item.getConvertType())) {
                ejbCount += count;
            } else if (ConvertType.EJB_CMP_XML_CONVERT.equals(item
                    .getConvertType())) {
                cmpCount += count;
            }
        }
        // Conversion run confirmation dialog
        if (PluginUtil.viewQuestionDialog(getDialogTitle(), String.format(
                MessageUtil.QUE_CONVERT_DATA_CONFIRM, ejbCount, cmpCount))) {
            return true;
        }
        return false;
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
    public void addPages() {
        super.addPage(jbmConvertSelectionPage);
        super.addPages();
    }
}
