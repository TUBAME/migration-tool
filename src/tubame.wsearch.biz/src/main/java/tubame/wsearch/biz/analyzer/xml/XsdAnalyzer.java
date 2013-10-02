/*
 * XsdAnalyzer.java
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
package tubame.wsearch.biz.analyzer.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * This is a class to parse the XSD file.<br/>
 * Parses the XSD file, you get the value of the targetNamespace that is
 * defined.<br/>
 */
public class XsdAnalyzer extends DefaultHandler2 {

    /**
     * Namespace
     */
    private String targetNamespace;

    /**
     * SAX parser
     */
    private SAXParser parser;

    /**
     * Whether analyzed
     */
    private boolean isAnalyzed;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(XsdAnalyzer.class);

    /**
     * Constructor.<br/>
     * Initialize the SAX parser.<br/>
     * 
     * @throws SAXException
     *             SAX Exception
     * @throws ParserConfigurationException
     *             ParserConfiguration Exception
     */
    public XsdAnalyzer() throws SAXException, ParserConfigurationException {
        this.parser = XmlAnalyzer.getSAXParser(this);
        this.isAnalyzed = false;
    }

    /**
     * Analyze the XSD file.<br/>
     * Parse an XSD file that exists in the file path given.<br/>
     * 
     * @param file
     *            The path to the XSD file to be analyzed
     * @throws SAXException
     *             If it fails to interpret the XML
     * @throws IOException
     *             If it fails to load the file
     */
    public void analyze(File file) throws SAXException, IOException {
        this.targetNamespace = null;
        LOGGER.trace(MessageUtil.getResourceString("debug.msg.read.file")
                + file.toString());
        InputStream inStream = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(file));
            InputSource inputSource = new InputSource(inStream);
            this.parser.parse(inputSource, this);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }
        this.isAnalyzed = true;
    }

    /**
     * Get the value of the targetNamespace.<br/>
     * Get the value of the targetNamespace extracted by analysis.<br/>
     * Return null if a targetNamespace could not be extracted.<br/>
     * throw an IllegalStateException if you have not analyzed yet.<br/>
     * 
     * @return Value of targetNamespace
     */
    public String getTargetNamespace() {
        if (!isAnalyzed) {
            throw new IllegalStateException(XsdAnalyzer.class.getName()
                    + ".err.illegalstate");
        }
        return this.targetNamespace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri, final String localName,
            final String qName, final Attributes attrs) {
        if (qName.contains("schema")) {
            this.targetNamespace = attrs.getValue("targetNamespace");
        }
    }
}
