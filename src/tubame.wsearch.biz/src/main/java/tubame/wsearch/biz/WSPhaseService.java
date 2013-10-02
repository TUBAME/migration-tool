/*
 * WSPhaseService.java
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
package tubame.wsearch.biz;

import tubame.wsearch.biz.model.PhaseInbound;
import tubame.wsearch.biz.model.PhaseOutbound;

/**
 * It is the base class for classes that provide services for each phase of
 * general-purpose search.<br/>
 * Please implement the execute method of this class of treatment in each phase.<br/>
 */
public interface WSPhaseService<T> {

    /**
     * The type that defines the type of service.<br/>
     */
    public enum TYPE {
        DESTLIB_METADATA_GENERATE, SRC_PARSER_AND_COMPARE
    }

    /**
     * It is a method that implements the processing of each phase.<br/>
     * 
     * @param inbound
     *            Input information required for processing
     * @return Processing result
     */
    PhaseOutbound execute(PhaseInbound<T> inbound);
}
