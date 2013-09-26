/*
 * DocBookWrite.java
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

import java.io.IOException;

import org.docbook.ns.docbook.Article;
import org.tubame.knowhow.biz.exception.JbmException;

/**
 * DocBookXML file write interface.<br/>
 * Defines a method for writing the know-how for more information.<br/>
 */
public interface DocBookWrite {

    /**
     * Writing DocBookXML.<br/>
     * Generates an XML from Article class, make the output to the location
     * specified.<br/>
     * 
     * @param filepath
     *            Output XML path
     * @param article
     *            Artile class
     * @throws JbmException
     *             JBM exception
     * @throws IOException
     *             IO exception
     */
    void write(String filepath, Article article) throws JbmException,
            IOException;
}
