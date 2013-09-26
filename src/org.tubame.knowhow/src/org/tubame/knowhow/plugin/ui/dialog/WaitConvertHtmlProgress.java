/*
 * WaitConvertHtmlProgress.java
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
package org.tubame.knowhow.plugin.ui.dialog;

import java.lang.reflect.InvocationTargetException;

import org.tubame.common.logic.converter.CmnDocBookConverter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.exception.JbmException;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.logic.FileManagement;
import org.tubame.knowhow.plugin.logic.FileManagement.XML_TYPE;

/**
 * Wait progress dialog class when performing the conversion to HTML file.<br/>
 */
public class WaitConvertHtmlProgress implements IRunnableWithProgress {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WaitConvertHtmlProgress.class);
    private static final int CONVERT_RESULT = CmnDocBookConverter.RETCODE_SUCCESS;
    /** Original file */
    private String orgFilePath;
    /** Output destination file */
    private String outputFilePath;
    /** XML type */
    private XML_TYPE xmlType;

    /**
     * Constructor.<br/>
     * 
     * @param orgFilePath
     *            Original file
     * @param outputFilePath
     *            Destination file
     * @param xmlType
     *            XML type
     */
    public WaitConvertHtmlProgress(String orgFilePath, String outputFilePath,
            XML_TYPE xmlType) {
        this.orgFilePath = orgFilePath;
        this.outputFilePath = outputFilePath;
        this.xmlType = xmlType;
        WaitConvertHtmlProgress.LOGGER.debug("[orgFilePath]" + orgFilePath
                + "[outputFilePath]" + outputFilePath + "[xmlType]" + xmlType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        WaitConvertHtmlProgress.LOGGER.debug("[monitor]" + monitor);
        WaitConvertHtmlProgress.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_START_CONVERT_HTML));
        monitor.beginTask(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.CONVERT_HTML),
                IProgressMonitor.UNKNOWN);
        int convResult = FileManagement.outputHtml(orgFilePath, outputFilePath,
                xmlType);
        if (convResult == CmnDocBookConverter.RETCODE_ERR_OUTPUT_DIR_NOT_FOUND) {
            JbmException.outputExceptionLog(LOGGER, null, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.ERROR_SAVE_DOCBOOK));
            // Throw InvocationTargetException as the error handling
            throw new InvocationTargetException(
                    null,
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.ERROR_SAVE_DOCBOOK));
        } else if (convResult != CmnDocBookConverter.RETCODE_WARN_CLOSE_OUTPUT_FILE_FAILED) {
            // Failed to close processing of the output file
            // The process is continued by logs a warning
            LOGGER.warn(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_WARN_CLOSE_OUTPUT_FILE_FAILED));

        } else if (convResult != CONVERT_RESULT) {
            JbmException.outputExceptionLog(LOGGER, null, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.ERROR_HTML_CONVERT));
            // Throw InvocationTargetException as the error handling
            throw new InvocationTargetException(
                    null,
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.ERROR_HTML_CONVERT));
        }
        if (monitor.isCanceled()) {
            LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.PRESS_CANCEL_BUTTON));
            throw new InterruptedException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.PRESS_CANCEL_BUTTON));
        }
        WaitConvertHtmlProgress.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_STOP_CONVERT_HTML));
        monitor.done();
    }
}
