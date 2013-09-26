/*
 * PortabilityFactorEnum.java
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
 * Class that defines the choice of transplant factors that appear in the check
 * list registration.<br/>
 */
public enum PortabilityFactorEnum implements DefineEnumOperator {

    /** Weblogic specific enumeration */
    Weblogic_Specific(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.WEBLOGIC_SPECIFIC)),
    /** Change of enumeration AP server specification */
    APServerAPIChange(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.AP_SERVERAPICHANGE)),
    /** Enumeration AP use library */
    AP_Library(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.AP_LIBRARY)),
    /** Enumeration AP server-specific */
    APServer_Specific(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.APSERVERSPECIFIC)),
    /** Change of the enumeration type JSP / Servlet specification */
    Jsp_Servlet(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.JSP_SERVLET)),
    /** The changes made by the enumerated type Java version up */
    JavaVersionUp(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.JAVA_VERSIONUP)),
    /** Change of enumeration DBMS */
    DBMSChange(ApplicationPropertiesUtil
            .getProperty(ApplicationPropertiesUtil.DBMS_CHANGE));

    /** Member variable of type String */
    private final String value;

    /**
     * Constructor.<br/>
     * 
     * @param v
     *            Enumeration data string
     */
    PortabilityFactorEnum(String v) {
        value = v;
    }

    /**
     * Get a search keyword type.<br/>
     * 
     * @param v
     *            Enumeration data string
     * @return InputKeywordFileType
     */
    public static PortabilityFactorEnum fromValue(String v) {
        for (PortabilityFactorEnum c : PortabilityFactorEnum.values()) {
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
