/*
 * SearchModuleConverter.java
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
package org.tubame.knowhow.biz.logic.converter;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.model.generated.python.PortabilitySearchModule;
import org.tubame.knowhow.biz.model.generated.python.SearchModule;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

/**
 * Conversion to the data to be used by knowledge GUI module information from
 * the search XML.<br/>
 */
public class SearchModuleConverter {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SearchModuleConverter.class);
    /** Search module map */
    private Map<String, String> searchModuleMap = new LinkedHashMap<String, String>();

    /**
     * Converted to Map format information search module XML.<br/>
     * 
     * @param portabilitySearchModule
     *            Search module information
     */
    public void convert(PortabilitySearchModule portabilitySearchModule) {
        LOGGER.debug(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_PARAMA)
                + "PortabilitySearchModule");
        for (SearchModule searchModule : portabilitySearchModule
                .getSearchModuleList().getSearchModule()) {
            searchModuleMap.put(searchModule.getModuleName(),
                    searchModule.getModuleDescription());
        }
    }

    /**
     * Get searchModuleMap.<br/>
     * 
     * @return searchModuleMap
     */
    public Map<String, String> getSearchModuleMap() {
        return searchModuleMap;
    }
}
