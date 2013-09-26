/*
 * ConvertWithProgress.java
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
package org.tubame.portability.logic.convert;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.tubame.portability.exception.JbmException;
import org.tubame.portability.logic.ConvertAccessFactory;
import org.tubame.portability.logic.ConvertFileScanner;
import org.tubame.portability.logic.ConvertFileScannerFacade;
import org.tubame.portability.logic.JbmAccessFactory;
import org.tubame.portability.logic.convert.command.Convertor;
import org.tubame.portability.model.generated.model.ConvertType;
import org.tubame.portability.model.generated.model.JbossMigrationConvertTool;
import org.tubame.portability.model.generated.model.MigrationItem;
import org.tubame.portability.model.generated.model.MigrationTarget;
import org.tubame.portability.model.generated.model.WebLogic;
import org.tubame.portability.util.FileUtil;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.resource.ApplicationPropertyUtil;
import org.tubame.portability.util.resource.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversion process in a specific version of WebLogic to JBoss from WebLogic.<br/>
 */
public class ConvertWithProgress implements IRunnableWithProgress {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConvertWithProgress.class);

    /**
     * Plug-in directory
     */
    private final String pluginDirectory;
    /**
     * Porting the source directory
     */
    private final String workspaceRoot;
    /**
     * Porting directory
     */
    private final String convertResultDirectory;
    /**
     * Full path of porting directory
     */
    private final String convertResultDirectoryFull;
    /**
     * Weblogic version
     */
    private final String weblogicVersion;
    /**
     * The path of the file jbm
     */
    private final String jbmFilePath;

    /**
     * List of items to be converted
     */
    private List<MigrationItem> targetList;
    /**
     * Data type of AbstractConvertDomain
     */
    private AbstractConvertDomain abstractConvertDomain;
    /**
     * Data type of ConvertFileScanner
     */
    private ConvertFileScanner convertedFileScanner;
    /**
     * Conversion status file path
     */
    private String convertStatusFilePath;

    /**
     * Constructor.<br/>
     * 
     * @param pluginDir
     *            Plug-in directory
     * @param workspaceRoot
     *            Workspace root
     * @param convertResultDirectory
     *            Porting directory
     * @param weblogic
     *            Weblogic version
     * @param jbmFilePath
     *            The path of the file jbm
     */
    public ConvertWithProgress(String pluginDir, String workspaceRoot,
            String convertResultDirectory, String weblogic, String jbmFilePath) {
        LOGGER.debug("[pluginDir]" + pluginDir + "[workspaceRoot]"
                + workspaceRoot + "[convertResultDirectory]"
                + convertResultDirectory + "[weblogic]" + weblogic
                + "[jbmFilePath]" + jbmFilePath);

        pluginDirectory = pluginDir;
        this.workspaceRoot = workspaceRoot;
        this.convertResultDirectory = convertResultDirectory;
        convertResultDirectoryFull = PluginUtil
                .getFileFullPath(convertResultDirectory);
        weblogicVersion = weblogic;
        this.jbmFilePath = jbmFilePath;
    }

    /**
     * Get a list of items that you want to convert.<br/>
     * 
     * @return List<MigrationItem>
     */
    public List<MigrationItem> getTargetList() {
        return targetList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        try {
            // Title Settings
            monitor.beginTask(getProgressTitle(), IProgressMonitor.UNKNOWN);
            // MigrationItem list acquisition
            targetList = createMigrationItemList(jbmFilePath);
            // Conversion run
            convert(monitor, targetList, pluginDirectory, workspaceRoot,
                    convertResultDirectoryFull);
            // Writing
            ConvertAccessFactory.getConvertWriteFacade().save(targetList,
                    getConvertStatusFilePath(convertResultDirectory));
        } catch (InterruptedException e) {
            deleteConvertFile(monitor, targetList);
            throw e;
        } catch (JbmException e) {
            // Write failure
            deleteConvertFile(monitor, targetList);
            PluginUtil.viewErrorDialog(getProgressTitle(), e.getMessage(), e);
        }
    }

    /**
     * Delete files of cancellation.<br/>
     * 
     * @param monitor
     *            Monitor
     * @param targetList
     *            Delete target list
     */
    protected void deleteConvertFile(IProgressMonitor monitor,
            List<MigrationItem> targetList) {
        try {
            for (MigrationItem migrationItem : targetList) {
                for (MigrationTarget migrationTarget : migrationItem
                        .getMigrationTargets().getMigrationTarget()) {
                    if (migrationTarget.isConvert()) {
                        IFile file = PluginUtil.createIFile(ResourcesPlugin
                                .getWorkspace().getRoot(), migrationTarget
                                .getConvertedFile());
                        file.delete(true, monitor);
                    }
                }
            }
        } catch (CoreException e) {
            // File failed to remove
            PluginUtil.viewErrorDialog(getProgressTitle(), e.getMessage(), e);
        }
    }

    /**
     * List minute of target, converted by the {@link Convertor} conversion
     * process of target.<br/>
     * 
     * @param monitor
     *            Monitor
     * @param targetList
     *            Target list
     * @param pluginDir
     *            Plug-in directory
     * @param targetDirectory
     *            Porting the source directory
     * @param convertResultDirectory
     *            Porting directory
     * @throws InterruptedException
     *             Cancellation
     * @throws JbmException
     *             Emissions converter instantiation failure
     */
    protected void convert(IProgressMonitor monitor,
            List<MigrationItem> targetList, String pluginDir,
            String targetDirectory, String convertResultDirectory)
            throws InterruptedException, JbmException {
        State state = ConvertState.getInstance();
        state.init();

        for (MigrationItem target : targetList) {
            if (monitor.isCanceled()) {
                throw new InterruptedException(MessageUtil.INF_CANCEL);
            }

            // Folder name to each output No conversion result
            String outputBaseRelative = createOutputRelativeFolder(
                    convertResultDirectory, target.getNumber());

            // Execution
            executeJBossMigrationCovertDomain(monitor, target, targetDirectory,
                    outputBaseRelative, pluginDir);

            // Checking the State
            state = ConvertState.getInstance();
            if (state.isFailed()) {
                for (Exception exception : state.getExceptionList()) {
                    PluginUtil.logException(exception);
                }
            }
            monitor.worked(1);
            Thread.sleep(6000);
        }
        if (abstractConvertDomain != null) {
            convertedFileScanner = new ConvertFileScannerFacade()
                    .getScanner(((JBossMigrationConvertDomain) abstractConvertDomain)
                            .getFilePathList());
        }
    }

    /**
     * Run the conversion process.<br/>
     * 
     * @param monitor
     *            Monitor
     * @param target
     *            Target item
     * @param targetDirectory
     *            Directory on the target
     * @param outputBaseRelative
     *            Destination Base directory
     * @param pluginDir
     *            Plug-in directory
     * @throws JbmException
     *             Exceptional cases
     * @throws InterruptedException
     *             Cancellation
     */
    protected void executeJBossMigrationCovertDomain(IProgressMonitor monitor,
            MigrationItem target, String targetDirectory,
            String outputBaseRelative, String pluginDir) throws JbmException,
            InterruptedException {
        abstractConvertDomain = new JBossMigrationConvertDomain(
                getWebLogic(weblogicVersion), getConvertType());
        abstractConvertDomain.doExecute(monitor, target,
                targetDirectory.replace("/", FileUtil.FILE_SEPARATOR),
                outputBaseRelative, pluginDir);
    }

    /**
     * Create a relative path to the file writing.<br/>
     * 
     * @param root
     *            Convert the destination directory
     * @param number
     *            Number
     * @return Relative path
     */
    private String createOutputRelativeFolder(String root, String number) {
        StringBuffer string = new StringBuffer();
        // String outputBaseFull = fileSelectionPage.getOutputFolderBase()
        // .getLocation().toOSString();
        // String top = fileSelectionPage.getOutputFolderBase().getProject()
        // .getParent().getLocation().toOSString();
        // String relativePath = FileUtil.getRelativePath(top
        // + FileUtil.FILE_SEPARATOR, outputBaseFull);
        // string.append(relativePath);
        string.append(root);
        string.append(FileUtil.FILE_SEPARATOR);
        string.append("No");
        string.append(number);
        return string.toString();
    }

    /**
     * Create a list from the data conversion of jbm file.<br/>
     * 
     * @param jbmFilePath
     *            The path of the file jbm
     * @return List<MigrationItem>
     * @throws JbmException
     *             Jbm file read failure
     */
    protected List<MigrationItem> createMigrationItemList(String jbmFilePath)
            throws JbmException {
        // File read results
        JbossMigrationConvertTool data = JbmAccessFactory.getJbmReadFacade()
                .readToJbossMigrationConvertTool(jbmFilePath);
        // Conversion status file write

        return data.getMigrationItems().getMigrationItem();
    }

    /**
     * Get WebLogicVer instance.<br/>
     * Get the {@link WebLogic} instance from WebLogicVer that are selected
     * screen.<br/>
     * 
     * @param version
     *            WebLogic version that was selected in the screen
     * @return WebLogic
     */
    private WebLogic getWebLogic(String version) {
        return WebLogic.fromValue(version);
    }

    /**
     * Get the conversion type {@link ConvertType} ALL fixed.<br/>
     * 
     * @return Conversion type
     */
    private ConvertType getConvertType() {
        return ConvertType.ALL;
    }

    /**
     * Get the title of the progress status screen.<br/>
     * 
     * @return Title
     */
    public String getProgressTitle() {
        return MessageUtil.INF_CONVERT_RUNNING;
    }

    /**
     * Get the path of conversion status file.<br/>
     * 
     * @param outFilePath
     *            Path of conversion destination
     * @return String Path
     */
    protected String getConvertStatusFilePath(String outFilePath) {

        convertStatusFilePath = PluginUtil.getWorkspaceRoot()
                + FileUtil.FILE_SEPARATOR
                + PluginUtil.getProjectName(outFilePath)
                + FileUtil.FILE_SEPARATOR
                + ApplicationPropertyUtil.DEFAULT_CONVERT_STATUS_FILE_NAME;

        return convertStatusFilePath;
    }

    /**
     * Get the logger.<br/>
     * 
     * @return logger
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Get checkOutPutData.<br/>
     * 
     * @return checkOutPutData
     */
    public ConvertFileScanner getConvertFileScanner() {
        return convertedFileScanner;
    }

    /**
     * Get convertResultDirectory.<br/>
     * 
     * @return convertResultDirectory
     */
    public String getConvertStatusFilePath() {
        return convertStatusFilePath;
    }
}
