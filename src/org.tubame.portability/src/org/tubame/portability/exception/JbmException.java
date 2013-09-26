/*
 * JbmException.java
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
package org.tubame.portability.exception;

import org.tubame.portability.util.StringUtil;
import org.slf4j.Logger;

/**
 * Manage exceptions of this tool.<br/>
 * Wrapper class for exceptions that occur in the portable study tool.<br/>
 */
public class JbmException extends Exception {
    /**
     * Enumerate the error level.<br/>
     * Define the error log level to be output to the Log4j.<br/>
     */
    public static enum ERROR_LEVEL {
        /**
         * ERROR
         */
        ERROR,
        /**
         * WARN
         */
        WARN,
        /**
         * INFO
         */
        INFO,
        /**
         * DEBUG
         */
        DEBUG,
        /**
         * TRACE
         */
        TRACE
    };

    /**
     * Error message display
     */
    private String viewMessage;

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 6096161468173728516L;

    /**
     * Constructor.<br/>
     * Make the output of the detail message received.<br/>
     * 
     * @param message
     *            Detailed message
     */
    public JbmException(String message) {
        super(message);
        setViewMessage(message);
    }

    /**
     * Constructor.<br/>
     * And detailed message, received and output the cause.<br/>
     * 
     * @param message
     *            Detailed message
     * @param cause
     *            Cause
     */
    public JbmException(String message, Throwable cause) {
        super(message, cause);
        setViewMessage(message);
    }

    /**
     * Constructor.<br/>
     * The output to log4j.<br/>
     * 
     * @param ex
     *            Exception information
     * @param logger
     *            Logger
     * @param level
     *            Error level
     * @param message
     *            Display string
     */
    public JbmException(Exception ex, Logger logger, ERROR_LEVEL level,
            String... message) {
        super(ex);
        setViewMessage(outputExceptionLog(ex, logger, level, message));
    }

    /**
     * Output the exception log to log4j.<br/>
     * 
     * @param ex
     *            Exception information
     * @param logger
     *            Logger
     * @param level
     *            Error level
     * @param message
     *            Display string
     * @return Error message display
     */
    public static String outputExceptionLog(Exception ex, Logger logger,
            ERROR_LEVEL level, String... message) {
        // Error message display created
        StringBuilder out = new StringBuilder();
        for (String element : message) {
            out.append(element);
            out.append(StringUtil.EMPTY);
        }
        // Exception string created
        StringBuilder exception = new StringBuilder();
        if (ex != null) {
            for (StackTraceElement element : ex.getStackTrace()) {
                exception.append(StringUtil.LINE_SEPARATOR);
                exception.append(element);
            }
        }
        // Log output
        switch (level) {
        case ERROR:
            logger.error(out.toString() + exception.toString());
            break;
        case WARN:
            logger.warn(out.toString() + exception.toString());
            break;
        case INFO:
            logger.info(out.toString() + exception.toString());
            break;
        case DEBUG:
            logger.debug(out.toString() + exception.toString());
            break;
        case TRACE:
            logger.trace(out.toString() + exception.toString());
            break;
        default:
            break;
        }

        return out.toString();
    }

    /**
     * Get the error message display.<br/>
     * 
     * @return Error message display
     */
    public String getViewMessage() {
        return viewMessage;
    }

    /**
     * Set the error message display.<br/>
     * 
     * @param viewMessage
     *            Error message display
     */
    public void setViewMessage(String viewMessage) {
        this.viewMessage = viewMessage;
    }

}
