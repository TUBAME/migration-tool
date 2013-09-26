/*
 * State.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class to manage static state.<br/>
 * Manage processing failure flag and list of exceptions.<br/>
 */
public class State {
    /**
     * Exception list
     */
    private final List<Exception> exceptions = new ArrayList<Exception>();
    /**
     * fail flag
     */
    private boolean fail;

    /**
     * Do the initialization.<br/>
     * Clear flag, the exception list.
     * 
     */
    public void init() {
        fail = false;
        exceptions.clear();
    }

    /**
     * Get or have failed to convert processing. <br/>
     * 
     * @return true:Failure false:Success
     */
    public boolean isFailed() {
        return fail;
    }

    /**
     * Added exception information to the exception list.<br/>
     * 
     * @param message
     *            Exception message
     * @param exception
     *            Exception information
     */
    public void addException(String message, Exception exception) {
        if (exception == null) {
            exceptions.add(new Exception(message));
        } else {
            exceptions.add(new Exception(message, exception));
        }
        fail = true;
    }

    /**
     * Get the exception list.<br/>
     * 
     * @return Exception list
     */
    public List<Exception> getExceptionList() {
        return exceptions;
    }
}
