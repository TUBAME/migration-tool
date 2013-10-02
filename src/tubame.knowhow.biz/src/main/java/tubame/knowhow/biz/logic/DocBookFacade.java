/*
 * DocBookFacade.java
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

import java.io.IOException;

import org.docbook.ns.docbook.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.logic.io.ConvertDocBookFilePath;
import tubame.knowhow.biz.logic.io.DocBookRead;
import tubame.knowhow.biz.logic.io.DocBookWrite;
import tubame.knowhow.biz.logic.io.DocBookXMLReader;
import tubame.knowhow.biz.logic.io.DocBookXMLWriter;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

/**
 * Facade to carry out reading the DocBook format.<br/>
 */
public final class DocBookFacade {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocBookFacade.class);
    /** DocBookWrite */
    private static DocBookWrite docBookWrite = new DocBookXMLWriter();

    /**
     * Constructor.<br/>
     */
    private DocBookFacade() {
        // no operation
    }

    /**
     * Call the {@link DocBookXMLReader}, to implement the reading process.<br/>
     * 
     * @param filePath
     *            Path of the XML file to be read subject
     * @return Article module information class
     * @throws JbmException
     *             JBM exception
     */
    public static Article read(String filePath) throws JbmException {
        LOGGER.debug("[Article]=" + filePath);
        return DocBookFacade.getDocBookXMLRead().read(filePath);
    }

    /**
     * Call the {@link DocBookXMLReader}, to implement the reading process.<br/>
     * 
     * @param filePath
     *            Path of the XML file to be read subject
     * @return Article module information class
     * @throws JbmException
     *             JBM exception
     */
    public static Article readFullPath(String filePath) throws JbmException {
        LOGGER.debug("[Article]=" + filePath);
        return DocBookFacade.getDocBookReadFullPath().read(filePath);
    }

    /**
     * Get information Article module XML file read interface.<br/>
     * 
     * @return DocBookRead
     */
    protected static DocBookRead getDocBookXMLRead() {
        return new DocBookXMLReader();
    }

    /**
     * Get information Article module XML file read interface.<br/>
     * 
     * @return ConvertDocBookFilePath
     */
    protected static DocBookRead getDocBookReadFullPath() {
        return new ConvertDocBookFilePath();
    }

    /**
     * Make call {@link DocBookXMLWriter}, the writing process. <br/>
     * 
     * @param filepath
     *            Path of the XML file to be written target
     * @param article
     *            Article
     * @throws JbmException
     *             JBM exception
     */
    public static void write(String filepath, Article article)
            throws JbmException {
        try {
            DocBookFacade.getDocBookWrite().write(filepath, article);
        } catch (IOException e) {
            throw new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.ERROR_FILE_SAVE_FAILURE),
                    e);
        }
    }

    /**
     * Get DocBookXML file writing logic.<br/>
     * 
     * @return DocBookWrite
     */
    protected static DocBookWrite getDocBookWrite() {
        return DocBookFacade.docBookWrite;
    }

}
