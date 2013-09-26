/*
 * JbmSearchWizard.java
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.tubame.portability.exception.JbmException;
import org.tubame.portability.logic.KnowhowXmlConvertFactory;
import org.tubame.portability.logic.search.SearchToolWithProgress;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.PythonUtil;
import org.tubame.portability.util.StringUtil;
import org.tubame.portability.util.resource.ApplicationPropertyUtil;
import org.tubame.portability.util.resource.MessageUtil;
import org.tubame.portability.util.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wizard class of search input screen.<br/>
 * Take the following pages.<br/>
 * {@link AbstractJbmSelectionPage}
 */
public class JbmSearchWizard extends Wizard implements INewWizard {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JbmSearchWizard.class);

    /**
     * Screen the Wizard uses
     */
    private final JbmSearchSelectionPage jbmSearchSelectionPage;

    /**
     * Get the search screen.<br/>
     * 
     * @return jbmSearchSelectionPage
     */
    public JbmSearchSelectionPage getjbmSearchSelectionPage() {
        return jbmSearchSelectionPage;
    }

    /**
     * Get the title of the screen.<br/>
     * 
     * @return Title of the screen
     */
    public String getDialogTitle() {
        return ResourceUtil.DIALOG_INFO_SEARCH;
    }

    /**
     * Get the error terms of the cancellation.<br/>
     * 
     * @return Error message of cancellation
     */
    public String getErrorRunCancel() {
        return MessageUtil.INF_SEARCH_CANCELED;
    }

    /**
     * Get the error terms of the failure.<br/>
     * 
     * @return Error message on failure
     */
    public String getErrorRunFalse() {
        return MessageUtil.ERR_SEARCH_FAILED;
    }

    /**
     * Get the wording of the processing is completed.<br/>
     * 
     * @return Completion of message processing
     */
    public String getRunComplete() {
        return MessageUtil.INF_SEARCH_COMPLETE;
    }

    /**
     * Constructor.<br/>
     * 
     * @param window
     *            Window
     * @param resource
     *            Target resource
     */
    public JbmSearchWizard(IWorkbenchWindow window, IResource resource) {
        super();
        jbmSearchSelectionPage = new JbmSearchSelectionPage(resource);
        super.setWindowTitle(ResourceUtil.DIALOG_SEARCH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        LOGGER.info(String.format(
                MessageUtil.LOG_INFO_ACTION_P,
                ResourceUtil.DIALOG_SEARCH,
                MessageUtil.LOG_INFO_BTN_NAME_FINISH,
                "TargetText=[" + jbmSearchSelectionPage.getTargetText()
                        + "] KnowhowXmlFilePath=["
                        + jbmSearchSelectionPage.getKnowhowXmlFilePath()
                        + "] OutJbmFileText=["
                        + jbmSearchSelectionPage.getOutJbmFileText() + "]"));
        boolean validate = jbmSearchSelectionPage.checkFileUpdate();
        // False when it was stuck to validate
        if (!validate) {
            LOGGER.info(String.format(MessageUtil.LOG_INFO_ACTION,
                    jbmSearchSelectionPage.getDialogTitle(),
                    MessageUtil.LOG_INFO_BTN_NAME_NO));
            return false;
        }
        try {
            LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_START,
                    MessageUtil.LOG_INFO_PROC_NAME_SEARCH));

            // Close the file with the same name that is already open
            PluginUtil.closeEditor(jbmSearchSelectionPage.getOutJbmFileText());

            // Convert an existing format file knowhowXML
            // knowhow.xml > keywordSearch.csv
            // knowhow.xml > checkListInformation.xml
            KnowhowXmlConvertFactory.getKnowhowXmlConvertFacade()
                    .convertSearchFiles(
                            PluginUtil.getFileFullPath(jbmSearchSelectionPage
                                    .getKnowhowXmlFilePath()));

            // Search process
            SearchToolWithProgress progress = new SearchToolWithProgress(
                    PluginUtil.getFileFullPath(jbmSearchSelectionPage
                            .getTargetText()),
                    PythonUtil
                            .getSearchKeywordFilePath(ApplicationPropertyUtil.SEARCH_KEYWORD_FILE),
                    PluginUtil.getFileFullPath(jbmSearchSelectionPage
                            .getOutJbmFileText()));
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(
                    PluginUtil.getActiveWorkbenchShell());
            dialog.run(true, true, progress);
            if (progress.isFileOut()) {
                // Refresh
                PluginUtil.refreshWorkSpace(dialog.getProgressMonitor());
                // Open Perspective
                PluginUtil.openSeachPerspective();
                // Open the editor
                PluginUtil.openEditor(
                        jbmSearchSelectionPage.getOutJbmFileText(),
                        PluginUtil.getSearchEditorId());
                LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_END,
                        MessageUtil.LOG_INFO_PROC_NAME_SEARCH));
                PluginUtil.viewInfoDialog(getDialogTitle(), getRunComplete());
            } else {
                LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_END_P,
                        MessageUtil.LOG_INFO_PROC_NAME_SEARCH,
                        MessageUtil.INF_SEARCH_NON));
                PluginUtil.viewInfoDialog(getDialogTitle(),
                        MessageUtil.INF_SEARCH_NON);
                return false;
            }
        } catch (IOException e) {
            LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
                    MessageUtil.LOG_INFO_PROC_NAME_SEARCH), e);
            PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse(), e);
            return false;
        } catch (InterruptedException e) {
            // Cancellation
            LOGGER.debug(MessageUtil.INF_CANCEL);
            LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_CANCEL,
                    MessageUtil.LOG_INFO_PROC_NAME_SEARCH));
            PluginUtil.viewInfoDialog(getDialogTitle(), getErrorRunCancel());
            return false;
        } catch (InvocationTargetException e) {
            LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
                    MessageUtil.LOG_INFO_PROC_NAME_SEARCH), e);
            PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse()
                    + StringUtil.LINE_SEPARATOR + e.getMessage(), e);
            return false;
        } catch (WorkbenchException e) {
            LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END_P,
                    MessageUtil.LOG_INFO_PROC_NAME_SEARCH,
                    MessageUtil.ERR_SEARCH_PERSPECTIVE_OPEN), e);
            // Failure in perspective related
            PluginUtil.viewErrorDialog(getDialogTitle(),
                    MessageUtil.ERR_SEARCH_PERSPECTIVE_OPEN, e);
            return false;
        } catch (JbmException e) {
            LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
                    MessageUtil.LOG_INFO_PROC_NAME_SEARCH), e);
            // Conversion failure to existing form of know-how XML file
            PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse()
                    + StringUtil.LINE_SEPARATOR + e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performCancel() {
        LOGGER.info(String.format(MessageUtil.LOG_INFO_ACTION,
                ResourceUtil.DIALOG_SEARCH,
                MessageUtil.LOG_INFO_BTN_NAME_CANCEL));
        return super.performCancel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {
        super.addPage(jbmSearchSelectionPage);
        super.addPages();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // no oparation.
    }
}
