/*
 * AbstractViewType.java
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
package org.tubame.knowhow.plugin.model.view;

import java.io.Serializable;

import org.tubame.common.util.CmnStringUtil;

/**
 * Abstract class of display data model of know-how entry view.<br/>
 * JDK7<br/>
 */
public abstract class AbstractViewType implements Serializable {

    /** Serial ID */
    private static final long serialVersionUID = 1L;
    /** Registered name */
    private String registerName;
    /** Registration ID */
    private String registerKey;

    /**
     * Get registerName.
     * 
     * @return registerName
     */
    public String getRegisterName() {
        return registerName;
    }

    /**
     * Get registerName.<br/>
     * 
     * @param registerName
     *            registerName
     */
    public void setRegisterName(String registerName) {
        this.registerName = controlSetName(registerName);
    }

    /**
     * Control the setting of the subject name.<br/>
     * Return [-] If the subject name is empty.<br/>
     * 
     * @param registerName
     *            Subject Name
     * @return Subject Name
     */
    private String controlSetName(String registerName) {
        // Show "-" The default if there is no name
        if (!CmnStringUtil.isEmpty(registerName.trim())) {
            return registerName;
        }
        return CmnStringUtil.HYPHEN;
    }

    /**
     * Get registerKey.<br/>
     * 
     * @return registerKey
     */
    public String getRegisterKey() {
        return registerKey;
    }

    /**
     * Set the registerKey.<br/>
     * 
     * @param registerKey
     *            registerKey
     */
    public void setRegisterKey(String registerKey) {
        this.registerKey = registerKey;
    }

}
