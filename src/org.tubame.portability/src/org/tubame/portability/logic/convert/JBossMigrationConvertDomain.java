/*
 * JBossMigrationConvertDomain.java
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.tubame.portability.exception.JbmException;
import org.tubame.portability.logic.convert.command.Convertor;
import org.tubame.portability.model.generated.model.ConvertType;
import org.tubame.portability.model.generated.model.MigrationItem;
import org.tubame.portability.model.generated.model.MigrationTarget;
import org.tubame.portability.model.generated.model.WebLogic;
import org.tubame.portability.util.FileUtil;
import org.tubame.portability.util.resource.MessageUtil;

/**
 * Conversion run class.<br/>
 * Called by the automatic conversion wizard. <br/>
 * Get the converter, you call the conversion process.<br/>
 */
public class JBossMigrationConvertDomain extends AbstractConvertDomain {
    /**
     * Plug-in directory
     */
    private String pluginDir;
    /**
     * Converted file path list
     */
    private final List<String> filePathList;
    /**
     * Monitor
     */
    private IProgressMonitor monitor = null;

    /**
     * Constructor. The delegate to the base class.<br/>
     * 
     * @param webLogic
     *            WebLogicVersion(@see {@link WebLogic})
     * @param convertType
     *            Convert type(@see {@link ConvertType})
     * @throws JbmException
     *             Converter generation failure
     */
    public JBossMigrationConvertDomain(WebLogic webLogic,
            ConvertType convertType) throws JbmException {
        super(webLogic, convertType);
        filePathList = new ArrayList<String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doExecute(IProgressMonitor monitor,
            MigrationItem migrationItem, String projectHome,
            String outputFolder, String pluginDir) throws InterruptedException {
        this.monitor = monitor;
        Convertor convertor = getConvertor(migrationItem.getConvertType(),
                super.getWebLogic());
        if (convertor != null) {
            doConvert(migrationItem, convertor, projectHome, outputFolder,
                    pluginDir);
        } else {
            State state = ConvertState.getInstance();
            state.addException(String.format(
                    MessageUtil.ERR_CONVERT_CONVERTER_GET_ERROR, migrationItem
                            .getNumber(), migrationItem.getConvertType()
                            .toString()), null);
        }
    }

    /**
     * Do the conversion. <br/>
     * The treatment failure, registered with the {@link ConvertState} State.<br/>
     * 
     * @param migrationItem
     *            XML object
     * @param convertor
     *            Converter
     * @param containerFullPath
     *            Convert file path
     * @param outputFolderRelativePath
     *            Output directory (relative path)
     * @param pluginDir
     *            Plug-in directory
     * @throws InterruptedException
     *             Cancellation
     */
    private void doConvert(MigrationItem migrationItem, Convertor convertor,
            String containerFullPath, String outputFolderRelativePath,
            String pluginDir) throws InterruptedException {
        this.pluginDir = pluginDir;
        for (MigrationTarget target : migrationItem.getMigrationTargets()
                .getMigrationTarget()) {
            if (monitor.isCanceled()) {
                throw new InterruptedException(MessageUtil.INF_CANCEL);
            }

            if (!target.isConvert()) {
                convert(migrationItem, convertor, containerFullPath,
                        outputFolderRelativePath, target);

            }
        }
    }

    /**
     * Do the conversion process.<br/>
     * To complete the conversion source following exception occurs in each
     * source file.<br/>
     * 
     * @param migrationItem
     *            XML element:MigrationItem
     * @param convertor
     *            Converter
     * @param containerFullPath
     *            Directory path to convert
     * @param outputFolderRelativePath
     *            Directory path after conversion
     * @param target
     *            XML element:MigrationTarget
     */
    private void convert(MigrationItem migrationItem, Convertor convertor,
            String containerFullPath, String outputFolderRelativePath,
            MigrationTarget target) {
        try {
            String targetPath = containerFullPath + FileUtil.FILE_SEPARATOR
                    + target.getTargetFile();

            StringBuffer convertPath = new StringBuffer();
            convertPath.append(outputFolderRelativePath);
            convertPath.append(FileUtil.FILE_SEPARATOR);
            convertPath.append(target.getTargetFile());

            String targetFilePath = null;
            if (convertor.getWriteFileName() != null) {
                File file = new File(convertPath.toString());
                targetFilePath = file.getParent() + FileUtil.FILE_SEPARATOR
                        + convertor.getWriteFileName();
            } else {
                targetFilePath = convertPath.toString();
            }
            convertor.preConvert();
            convertor.doConvert(targetPath, targetFilePath, pluginDir);
            target.setConvert(true);
            target.setConvertedFile(FileUtil.getRelativePath(containerFullPath
                    + File.separator, targetFilePath));
            filePathList.add(target.getConvertedFile());
        } catch (RuntimeException e) {
            // XXX
            // Capturing RuntimeException, but I want to detect all Exception
            // (without the error termination, to convert the source file next),
            // it is exempt in this code.
            target.setConvert(false);
            ConvertState.getInstance().addException(
                    String.format(MessageUtil.ERR_CONVERT_MESSAGE,
                            migrationItem.getNumber(), target.getTargetFile()),
                    new Exception(e.getMessage(), e));
        } catch (Exception e) {
            // XXX
            // To capture the Exception, but I want to detect all Exception
            // (without the error termination, to convert the source file next),
            // it is exempt in this code.
            target.setConvert(false);
            ConvertState.getInstance().addException(
                    String.format(MessageUtil.ERR_CONVERT_MESSAGE,
                            migrationItem.getNumber(), target.getTargetFile()),
                    e);
        }
    }

    /**
     * Get Convertor along the ConvertType.<br/>
     * 
     * @param convertType
     *            Converter Type
     * @param weblogicVersion
     *            WebLogic Version
     * @return Converter
     */
    private Convertor getConvertor(ConvertType convertType,
            WebLogic weblogicVersion) {
        for (Convertor convertor : getConvertorList()) {
            if (convertor.getConverType().equals(convertType)
                    && convertor.getWebLogicVersion().equals(weblogicVersion)) {
                return convertor;
            }
        }
        return null;
    }

    /**
     * Get filePathList.<br/>
     * 
     * @return filePathList
     */
    public List<String> getFilePathList() {
        return filePathList;
    }
}
