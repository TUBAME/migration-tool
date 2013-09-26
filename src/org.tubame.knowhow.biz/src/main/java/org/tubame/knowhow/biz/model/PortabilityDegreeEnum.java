/*
 * PortabilityDegreeEnum.java
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
 * Class that defines and displays a check list registration, the choice of
 * transplant difficulty.<br/>
 */
public enum PortabilityDegreeEnum implements DefineEnumOperator {

    /** Enumeration high */
    High(ApplicationPropertiesUtil.getProperty(ApplicationPropertiesUtil.HIGH)),
    /** Enumeration in */
    Middle(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.MIDDLE)),
    /** Low enumeration */
    Low(ApplicationPropertiesUtil.getProperty(ApplicationPropertiesUtil.LOW)),
    /** Enumeration transplant impossible */
    Not_Portability(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.NOT_PORTABILITY)),
    /** Enumerated type unknown */
    Unknown(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.UNKNOWN)),
    /** Enumerated type "-" */
    Not_Search(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.NOT_SEARCH));

    /** Member variable of type String */
    private final String value;

    /**
     * Constructor.<br/>
     * 
     * @param v
     *            Enumeration data string
     */
    PortabilityDegreeEnum(String v) {
        value = v;
    }

    /**
     * Get a search keyword type.<br/>
     * 
     * @param v
     *            Enumeration data string
     * @return InputKeywordFileType
     */
    public static PortabilityDegreeEnum fromValue(String v) {
        for (PortabilityDegreeEnum c : PortabilityDegreeEnum.values()) {
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
