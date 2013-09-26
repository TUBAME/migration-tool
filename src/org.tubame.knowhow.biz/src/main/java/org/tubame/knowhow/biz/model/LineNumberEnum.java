/*
 * LineNumberEnum.java
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
package org.tubame.knowhow.biz.model;

import org.tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;

/**
 * Class that defines display in the search condition registration, the choice
 * of line number information.<br/>
 */
public enum LineNumberEnum implements DefineEnumOperator {

    /** Enumerated Empty */
    Empty(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.EMPTY)),
    /** Enumerated ToDoSE */
    ToDoSE(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.TODOSE)),
    /** Enumerated Unknown */
    Unknown(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.LINENUMBER_UNKNOWN));

    /** Member variable of type String */
    private final String value;

    /**
     * Constructor.<br/>
     * 
     * @param v
     *            Enumeration data string
     */
    LineNumberEnum(String v) {
        value = v;
    }

    /**
     * Get a search keyword type.<br/>
     * 
     * @param v
     *            Enumeration data string
     * @return InputKeywordFileType
     */
    public static LineNumberEnum fromValue(String v) {
        for (LineNumberEnum c : LineNumberEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return value;
    }
}
