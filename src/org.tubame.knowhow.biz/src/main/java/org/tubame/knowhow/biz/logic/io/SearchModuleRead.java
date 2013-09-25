/*
 * SearchModuleRead.java
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
package org.tubame.knowhow.biz.logic.io;

import org.tubame.knowhow.biz.exception.JbmException;
import org.tubame.knowhow.biz.model.generated.python.PortabilitySearchModule;

/**
 * Search module information XML file read interface.<br/>
 * Define methods for reads the information search module XML.<br/>
 */
public interface SearchModuleRead {

    /**
     * Reading of information search module XML.<br/>
     * Generate reads the search module information object XML file specified by
     * the parameter.<br/>
     * 
     * @param filepath
     *            Read target XML file
     * @return Search module information class
     * @throws JbmException
     *             JBM exception
     */
    PortabilitySearchModule read(String filepath) throws JbmException;

}
