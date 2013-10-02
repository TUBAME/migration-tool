/*
 * JaxbValidationEventHandler.java
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
package tubame.knowhow.biz.logic;

import java.util.Map;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

/**
 * Validation process of JAXB.<br/>
 */
public class JaxbValidationEventHandler implements ValidationEventHandler {
    /** Error information storage map **/
    private Map<String, Integer> errMap;
    /** Return value of Validation at the time */
    private boolean returnValidationError;

    /**
     * Constructor.<br/>
     * Receives the error information storage map.<br/>
     * 
     * @param errMap
     *            Error information storage map
     */
    public JaxbValidationEventHandler(Map<String, Integer> errMap) {
        this.errMap = errMap;

    }

    /**
     * Constructor.<br/>
     * Receives the error information storage map.<br/>
     * 
     * @param errMap
     *            Error information storage map
     * @param returnValidationError
     *            Return value of Validation at the time
     */
    public JaxbValidationEventHandler(Map<String, Integer> errMap,
            boolean returnValidationError) {
        this.errMap = errMap;
        this.returnValidationError = returnValidationError;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleEvent(ValidationEvent event) {
        if (event.getSeverity() == ValidationEvent.FATAL_ERROR
                || event.getSeverity() == ValidationEvent.ERROR) {
            ValidationEventLocator locator = event.getLocator();
            errMap.put("line", locator.getLineNumber());
            errMap.put("column", locator.getColumnNumber());
            return returnValidationError;
        }
        return true;
    }
}
