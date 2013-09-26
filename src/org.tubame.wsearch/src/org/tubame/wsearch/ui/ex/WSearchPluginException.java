/*
 * WSearchPluginException.java
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
package org.tubame.wsearch.ui.ex;

/**
 * It is a class that will handle exceptions that occur in the general-purpose
 * search plug-in.<br/>
 */
public class WSearchPluginException extends Exception {

    /**
     * Serialization key
     */
    private static final long serialVersionUID = -8424685293343194485L;

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     */
    public WSearchPluginException() {
        super();
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param message
     *            Message
     * @param cause
     *            Exception that caused
     */
    public WSearchPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param message
     *            Message
     */
    public WSearchPluginException(String message) {
        super(message);
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param cause
     *            Exception that caused
     */
    public WSearchPluginException(Throwable cause) {
        super(cause);
    }
}