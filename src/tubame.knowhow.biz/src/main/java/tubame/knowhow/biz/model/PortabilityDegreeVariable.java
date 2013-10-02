/*
 * PortabilityDegreeVariable.java
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
package tubame.knowhow.biz.model;

/**
 * Enumeration class that defines the variable name of transplant difficulty.<br/>
 */
public enum PortabilityDegreeVariable {

    /** High */
    High,
    /** Middle */
    Middle,
    /** Low */
    Low,
    /** NOT_TRN */
    NOT_TRN,
    /** Unknown */
    Unknown;

    /**
     * Get the name.<br/>
     * 
     * @return name
     */
    public String value() {
        return name();
    }

    /**
     * Get a search keyword type.<br/>
     * 
     * @param v
     *            Enumeration data string
     * @return InputKeywordFileType
     */
    public static PortabilityDegreeVariable fromValue(String v) {
        return valueOf(v);
    }

}
