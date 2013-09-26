/*
 * SaveCsvAction.java
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
package org.tubame.portability.plugin.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tubame.common.util.CmnFileUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TreeItem;
import org.tubame.portability.exception.JbmException;
import org.tubame.portability.logic.JbmWriteFacade;
import org.tubame.portability.model.JbmEditorMigrationRow;
import org.tubame.portability.plugin.editor.MigrationEditorOperation;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.StringUtil;
import org.tubame.portability.util.resource.ApplicationPropertyUtil;
import org.tubame.portability.util.resource.MessageUtil;
import org.tubame.portability.util.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that controls the output a CSV file.<br/>
 * Output to any location CSV format file of edits in the search results editor.<br/>
 */
public class SaveCsvAction extends AbstractJbmEditorCommandButton {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SaveCsvAction.class);
    /**
     * Output file (temporary) file
     */
    private String outputFilePath;

    /**
     * {@inheritDoc}
     */
    @Override
    void commandButtonExecute(MigrationEditorOperation editor) {
        LOGGER.info(MessageUtil.INF_ACTION_SAVE_CSV);

        TreeViewer treeViewer = editor.getTreeViewer();
        List<JbmEditorMigrationRow> list = createEditorViewData(treeViewer);
        try {
            String temporaryCsvFilePath = PluginUtil.getPluginDir()
                    + ApplicationPropertyUtil.OUTPUT_TEMPORARY_CSV;
            outputTemporaryCsv(list, temporaryCsvFilePath);

            FileDialog dialog = new FileDialog(treeViewer.getControl()
                    .getShell(), SWT.SAVE);
            String[] extensions = { ApplicationPropertyUtil.EXTENSION_CSV };
            dialog.setFilterExtensions(extensions);
            dialog.setFileName(ApplicationPropertyUtil.DEFAULT_CSV_FILENAME);
            outputFilePath = temporaryCsvFilePath.replaceFirst(
                    StringUtil.SLASH, StringUtil.EMPTY);
            final String formPath = outputFilePath;
            final String toPath = dialog.open();
            if (toPath != null) {
                Job job = new Job(MessageUtil.INF_SAVE_CSV_START) {
                    @Override
                    protected IStatus run(IProgressMonitor monitor) {

                        monitor.beginTask(MessageUtil.INF_SAVE_CSV_START,
                                IProgressMonitor.UNKNOWN);

                        // Cancellation process
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        // CSV output
                        CmnFileUtil.copyFile(formPath, toPath);
                        monitor.done();
                        return Status.OK_STATUS;
                    }
                };
                job.setUser(true);
                job.schedule();
            }
        } catch (JbmException e) {
            PluginUtil.viewErrorDialog(ResourceUtil.OUTPUT_CSV,
                    MessageUtil.ERR_OUTPUT_CSV, e);
        } catch (IOException e) {
            PluginUtil.viewErrorDialog(ResourceUtil.OUTPUT_CSV,
                    MessageUtil.ERR_OUTPUT_CSV, e);
        }
    }

    /**
     * The output temporary files in the CSV output.<br/>
     * 
     * @param list
     *            Editor display data
     * @param temporaryCsvFilePath
     *            Temporary file path
     * @throws JbmException
     *             Jbm exception
     */
    private void outputTemporaryCsv(List<JbmEditorMigrationRow> list,
            String temporaryCsvFilePath) throws JbmException {
        JbmWriteFacade facade = new JbmWriteFacade();
        facade.update(temporaryCsvFilePath, list);
    }

    /**
     * Get the data that are currently displayed in the editor.<br/>
     * 
     * @param treeViewer
     *            TreeViewer
     * @return Editor display data
     */
    private List<JbmEditorMigrationRow> createEditorViewData(
            TreeViewer treeViewer) {
        List<JbmEditorMigrationRow> list = new ArrayList<JbmEditorMigrationRow>();
        for (TreeItem item : treeViewer.getTree().getItems()) {
            list.add((JbmEditorMigrationRow) item.getData());
        }
        return list;
    }
}
