/*
 * PhaseOutbound.java
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
package tubame.wsearch.biz.model;

/**
 * It is a wrapper class for a value output from the service.<br/>
 */
public class PhaseOutbound {

    /**
     * Output value
     */
    private Object value;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param value
     *            Output value
     */
    public PhaseOutbound(Object value) {
        super();
        this.value = value;
    }

    /**
     * Get the output value.<br/>
     * 
     * @return Output value
     */
    public Object getValue() {
        return value;
    }
}
