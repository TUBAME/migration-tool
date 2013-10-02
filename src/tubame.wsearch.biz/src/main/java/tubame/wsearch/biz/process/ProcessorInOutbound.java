/*
 * ProcessorInOutbound.java
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
package tubame.wsearch.biz.process;

import tubame.wsearch.biz.model.PhaseInbound;
import tubame.wsearch.biz.model.PhaseOutbound;

/**
 * It is a wrapper class of input and output values to be used in the service.<br/>
 */
public class ProcessorInOutbound {

    /**
     * Input value
     */
    private PhaseInbound inbound;

    /**
     * Output value
     */
    private PhaseOutbound outbound;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param inbound
     *            Input value
     */
    public ProcessorInOutbound(PhaseInbound inbound) {
        super();
        this.inbound = inbound;
    }

    /**
     * Get the output value.
     * 
     * @return Output value
     */
    public PhaseOutbound getOutbound() {
        return outbound;
    }

    /**
     * Set the output value.<br/>
     * 
     * @param outbound
     *            Output value
     */
    public void setOutbound(PhaseOutbound outbound) {
        this.outbound = outbound;
    }

    /**
     * get the input value.<br/>
     * 
     * @return Input value
     */
    public PhaseInbound getInbound() {
        return inbound;
    }
}
