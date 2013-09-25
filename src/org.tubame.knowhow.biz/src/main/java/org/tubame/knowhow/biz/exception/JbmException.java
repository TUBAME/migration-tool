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
package org.tubame.knowhow.biz.exception;

import org.slf4j.Logger;

/**
 * Make the exception handling of porting tool maintenance know-how.<br/>
 * The logged in the Application log received by constructor, an exception and
 * exception message.<br/>
 */
public class JbmException extends Exception {

    /** SerialVersionUID */
    private static final long serialVersionUID = -268613806483765361L;

    /**
     * Constructor.<br/>
     * Make the output of the detail message received.<br/>
     * 
     * @param message
     *            Detailed message
     */
    public JbmException(String message) {
        super(message);
    }

    /**
     * Constructor.<br/>
     * Output of the cause and detail message received.<br/>
     * 
     * @param message
     *            Detailed message
     * @param cause
     *            Cause
     */
    public JbmException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Output the exception log to sl4j.<br/>
     * 
     * @param logger
     *            Logger
     * @param cause
     *            Cause
     * @param message
     *            Detailed message
     */
    public static void outputExceptionLog(Logger logger, Throwable cause,
            String... message) {
        // Error message display created
        StringBuilder out = new StringBuilder();
        for (String element : message) {
            out.append(element);
            out.append("");
        }
        // Exception string created
        StringBuilder exception = new StringBuilder();
        if (cause != null) {
            for (StackTraceElement element : cause.getStackTrace()) {
                exception.append(System.getProperty("line.separator"));
                exception.append(element);
            }
        }
        logger.error(out.toString() + exception.toString());
    }
}
