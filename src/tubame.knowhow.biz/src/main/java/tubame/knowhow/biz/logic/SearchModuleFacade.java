/*
 * SearchModuleFacade.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.logic.converter.SearchModuleConverter;
import tubame.knowhow.biz.logic.io.ConvertSearchModuleFilePath;
import tubame.knowhow.biz.logic.io.SearchModuleRead;
import tubame.knowhow.biz.logic.io.SearchModuleXMLReader;
import tubame.knowhow.biz.model.generated.python.PortabilitySearchModule;

/**
 * Facade to carry the load of the search module information.<br/>
 */
public final class SearchModuleFacade {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SearchModuleFacade.class);

    /**
     * Constructor.<br/>
     */
    private SearchModuleFacade() {
        // no operation
    }

    /**
     * Call the {@link SearchModuleXMLReader}, to implement the reading process.<br/>
     * 
     * @param filePath
     *            Path of the XML file to be read subject
     * @return Search module information class
     * @throws JbmException
     *             JBM exception
     */
    public static PortabilitySearchModule read(String filePath)
            throws JbmException {
        LOGGER.debug("[PortabilitySearchModule]=" + filePath);
        return SearchModuleFacade.getSearchModuleRead().read(filePath);
    }

    /**
     * Call the {@link SearchModuleXMLReader}, to implement the reading process.<br/>
     * 
     * @param filePath
     *            Path of the XML file to be read subject
     * @return Search module information class
     * @throws JbmException
     *             JBM exception
     */
    public static PortabilitySearchModule readFullPath(String filePath)
            throws JbmException {
        LOGGER.debug("[PortabilitySearchModule]=" + filePath);
        return SearchModuleFacade.getSearchModuleReadFullPath().read(filePath);
    }

    /**
     * Get information search module XML file read interface.<br/>
     * 
     * @return SearchModuleRead
     */
    protected static SearchModuleRead getSearchModuleRead() {
        return new SearchModuleXMLReader();
    }

    /**
     * Get information search module XML file read interface.<br/>
     * 
     * @return SearchModuleRead
     */
    protected static SearchModuleRead getSearchModuleReadFullPath() {
        return new ConvertSearchModuleFilePath();
    }

    /**
     * Get information search module conversion data.<br/>
     * 
     * @return SearchModuleConverter
     */
    public static SearchModuleConverter getSearchModuleConverter() {
        return new SearchModuleConverter();
    }
}
