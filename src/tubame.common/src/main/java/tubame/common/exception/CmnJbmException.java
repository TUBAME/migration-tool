/*
 * CmnJbmException.java
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
package tubame.common.exception;

/**
 * Make the exception handling of JBoss migration.<br/>
 * Logged in the Application log received by constructor, an exception and
 * exception message.<br/>
 */
public class CmnJbmException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -268613806483765361L;

    /**
     * Constructor.<br/>
     * Make the output of the detail message received.<br/>
     * 
     * @param message
     *            the detail message
     */
    public CmnJbmException(String message) {
        super(message);
    }

    /**
     * Constructor.<br/>
     * Output the cause and detail message received.<br/>
     * 
     * @param message
     *            Detailed message
     * @param cause
     *            Cause
     */
    public CmnJbmException(String message, Throwable cause) {
        super(message, cause);
    }
}
