/*
 * NonDTDCheckEntityResolver.java
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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Resolver class to disable the check of DTD.<br/>
 */
public class NonDTDCheckEntityResolver implements EntityResolver {

    /**
     * Generate Document that does not check the DTD.<br/>
     * 
     * @param filePath
     *            File path of the URL
     * @return Document
     * @throws ParserConfigurationException
     *             Perth configuration failure
     * @throws SAXException
     *             SAX abnormal
     * @throws IOException
     *             File access failure
     */
    public static Document createDocument(String filePath, String encode)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        builder.setEntityResolver(new NonDTDCheckEntityResolver());

        File file = new File(URLDecoder.decode(filePath, encode));
        return builder.parse(file);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        // Magic number
        StringReader stringReader = new StringReader("");
        return new InputSource(stringReader);
    }
}
