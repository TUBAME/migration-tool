/*
 * XmlAnalyzer.java
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.DefaultHandler;

import tubame.wsearch.biz.WSPhaseService;
import tubame.wsearch.biz.analyzer.Analyzer;
import tubame.wsearch.biz.analyzer.ParserResult;
import tubame.wsearch.biz.ex.WSearchBizException;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * This is a class to extract schema information you are using in your code to
 * parse the XML file, namespace information, the DTD information.<br/>
 * Analysis on the assumption that the imported project to the eclipse.<br/>
 * </p> Example of usage.<br/>
 * 
 * <pre>
 * ResourcesPlugin.getWorkspace().getRoot().getProject(&quot;test&quot;)
 *         .accept(new IResourceVisitor() {
 *             public boolean visit(IResource resource) throws CoreException {
 *                 List&lt;Pattern&gt; ignores = new ArrayList&lt;Pattern&gt;();
 *                 ignores.add(Pattern.compile(&quot;&circ;java.*&quot;));
 *                 ignores.add(Pattern.compile(&quot;&circ;jp.*&quot;));
 *                 switch (resource.getType()) {
 *                 case IResource.FILE:
 *                     Analyzer analyzer = null;
 *                     if (resource.getFileExtension() == null) {
 *                         break;
 *                     } else if (resource.getFileExtension().equals(&quot;xml&quot;)) {
 *                         analyzer = new XmlAnalyzer((IFile) resource, ignores);
 *                         analyzer.analyze();
 *                         Map&lt;String, List&lt;ParseResult&gt;&gt; map = analyzer
 *                                 .getResultMap();
 *                         // Can also be retrieved by specifying a Map as follows:
 *                         // .getResultMap(Analyzer.TYPE_XML_XMLNS);
 *                         // .getResultMap(Analyzer.TYPE_XML_XSD);
 *                         // .getResultMap(Analyzer.TYPE_XML_DTD);
 *                         for (Map.Entry&lt;String, List&lt;ParseResult&gt;&gt; entry : map
 *                                 .entrySet()) {
 *                             for (ParseResult rs : entry.getValue()) {
 *                                 System.out.println(rs);
 *                             }
 *                         }
 *                     }
 *                     break;
 *                 }
 *                 return true;
 *             }
 *         });
 * </pre>
 * 
 * This implementation is not synchronized.<br/>
 * If you want to access multiple threads in parallel, those threads to change
 * the member variable, it must be synchronized externally.<br/>
 */
public class XmlAnalyzer extends DefaultHandler2 implements Analyzer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(XmlAnalyzer.class);

    /**
     * True if the analyze the executed
     */
    private boolean analyzed;

    /**
     * Object that manages the analysis point of XML file
     */
    private Locator locator;

    /**
     * Path of XML file
     */
    private String filePath;

    /**
     * Map that is registered namespace information groups are used in XML file
     */
    private Map<String, List<ParserResult>> xmlnsResultMap;

    /**
     * Map that is registered schema information groups are used in XML file
     */
    private Map<String, List<ParserResult>> xsdResultMap;

    /**
     * Map that is registered information DTD groups are used in the XML file
     */
    private Map<String, List<ParserResult>> dtdResultMap;

    /**
     * List for storing the namespace for the schema file name
     */
    private List<String> removeXmlnsList;

    /**
     * List of strings of regular expressions that extraction and excluded
     */
    private List<Pattern> ignoreList;

    /**
     * Constructor.<br/>
     * Initialize the XmlAnalyzer.<br/>
     * 
     * @param filePath
     *            Path of the XML file
     */
    public XmlAnalyzer(final String filePath) {
        this(filePath, new ArrayList<Pattern>());
    }

    /**
     * Constructor.<br/>
     * Initialize the XmlAnalyzer.<br/>
     * By specifying a list of regular expression string keywords that extracted
     * outside, you can refine the extraction target.<br/>
     * 
     * @param filePath
     *            File Path
     * @param List
     *            of strings of regular expressions that extraction and excluded
     */
    public XmlAnalyzer(final String filePath, final List<Pattern> ignoreList) {
        super();
        this.filePath = filePath;
        this.xmlnsResultMap = new HashMap<String, List<ParserResult>>();
        this.xsdResultMap = new HashMap<String, List<ParserResult>>();
        this.dtdResultMap = new HashMap<String, List<ParserResult>>();
        this.removeXmlnsList = new ArrayList<String>();
        this.analyzed = false;
        this.ignoreList = ignoreList;
    }

    /**
     * Get a SAXParser configured properly.<br/>
     * 
     * @param parser
     *            Parser to use
     * @return SAXParser that is generated
     * @throws SAXException
     *             If the Parser settings that does not exist
     * @throws ParserConfigurationException
     *             If the Parser settings that does not exist
     */
    static SAXParser getSAXParser(final DefaultHandler parser)
            throws SAXException, ParserConfigurationException {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        factory.setFeature(
                "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false);
        final SAXParser saxParser = factory.newSAXParser();
        final XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler",
                parser);
        return saxParser;
    }

    /**
     * Register the Map is a member variable that corresponds to the mapType you
     * specify, the class information extracted.<br/>
     * This method does not cause the user to call.<br/>
     * 
     * @param mapType
     *            Type of map you want to register
     * @param key
     *            Tag library information or class name
     * @param node
     *            Attributes object where that class is used
     */
    private void putResultMap(final int mapType, final String key,
            final Attributes node) {
        for (Pattern pattern : this.ignoreList) {
            final Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                return;
            }
        }
        List<ParserResult> nodeList = null;
        switch (mapType) {
        case TYPE_XML_XMLNS:
            nodeList = this.xmlnsResultMap.get(key);
            if (nodeList == null) {
                nodeList = new ArrayList<ParserResult>();
                this.xmlnsResultMap.put(key, nodeList);
            }
            break;
        case TYPE_XML_XSD:
            nodeList = this.xsdResultMap.get(key);
            if (nodeList == null) {
                nodeList = new ArrayList<ParserResult>();
                this.xsdResultMap.put(key, nodeList);
            }
            break;
        case TYPE_XML_DTD:
            nodeList = this.dtdResultMap.get(key);
            if (nodeList == null) {
                nodeList = new ArrayList<ParserResult>();
                this.dtdResultMap.put(key, nodeList);
            }
            break;
        default:
            throw new IllegalArgumentException(MessageFormat.format(
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".err.illegalarg"), mapType));
        }
        nodeList.add(new ParserResult(key, this.filePath, this.locator
                .getLineNumber(), this.locator.getColumnNumber(), node));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void analyze() throws WSearchBizException {
        // Updates the map of member variables to analyze the code.
        // Nothing takes action if the parsed already.
        if (!isAnalyzed()) {
            File file = new File(this.filePath);
            SAXParser saxParser = null;
            InputStream inStream = null;
            try {
                saxParser = getSAXParser(this);
                LOGGER.trace(MessageUtil
                        .getResourceString("debug.msg.read.file")
                        + this.filePath);
                inStream = new BufferedInputStream(new FileInputStream(file));
                InputSource inputSource = new InputSource(inStream);
                saxParser.parse(inputSource, this);
                // Namespace can get the schema file name Delete
                for (String xmlns : this.removeXmlnsList) {
                    this.xmlnsResultMap.remove(xmlns);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new WSearchBizException(
                        WSPhaseService.TYPE.SRC_PARSER_AND_COMPARE,
                        e.getMessage());
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        // Without the need for handling
                    }
                }
            }
            this.analyzed = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnalyzed() {
        return this.analyzed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<ParserResult>> getAnalyzedMap() {
        // The Map, xmlns *, *: all information that you are using
        // schemaLocation, in the DTD is included.
        if (isAnalyzed()) {
            Map<String, List<ParserResult>> map = new HashMap<String, List<ParserResult>>();
            map.putAll(this.xmlnsResultMap);
            map.putAll(this.xsdResultMap);
            map.putAll(this.dtdResultMap);
            return map;
        }
        throw new IllegalStateException(MessageUtil.getResourceString(this
                .getClass().getName() + ".err.illegalstate"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<ParserResult>> getAnalyzedMap(final int mapType) {
        if (isAnalyzed()) {
            switch (mapType) {
            case TYPE_XML_XMLNS:
                return this.xmlnsResultMap;
            case TYPE_XML_XSD:
                return this.xsdResultMap;
            case TYPE_XML_DTD:
                return this.dtdResultMap;
            default:
                throw new IllegalArgumentException(MessageFormat.format(
                        MessageUtil.getResourceString(this.getClass().getName()
                                + ".err.illegalarg"), mapType));
            }
        }
        throw new IllegalStateException(MessageUtil.getResourceString(this
                .getClass().getName() + ".err.illegalstate"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startDTD(final String name, final String publicId,
            final String systemId) throws SAXException {
        if (systemId != null) {
            putResultMap(TYPE_XML_DTD, systemId.trim(), null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri, final String localName,
            final String qName, final Attributes attrs) {
        for (int i = 0; i < attrs.getLength(); i++) {
            String attrQName = attrs.getQName(i);
            // Extract namespace
            if (attrQName.startsWith("xmlns")) {
                // It is not extracted namespace of the schema file definition
                if (!attrs.getValue(i).contains("www.w3.org")) {
                    putResultMap(TYPE_XML_XMLNS, attrs.getValue(i).trim(),
                            attrs);
                }
            }
            // Extract of the mapping schema file and name space
            else if (attrQName.contains("schemaLocation")) {
                String[] schemas = attrs.getValue(i).split("\\s+");
                for (int j = 0; j < schemas.length; j++) {
                    // If the schema file specified portion
                    if ((j + 1) % 2 == 0) {
                        removeXmlnsList.add(schemas[j - 1]);
                        putResultMap(TYPE_XML_XSD, schemas[j], attrs);
                    }
                }
            }
            // Extract the schema file
            else if (attrQName.contains("noNamespaceSchemaLocation")) {
                String[] schemas = attrs.getValue(i).split("\\s+");
                for (int j = 0; j < schemas.length; j++) {
                    putResultMap(TYPE_XML_XSD, schemas[j], attrs);
                }
            }
        }
    }
}
