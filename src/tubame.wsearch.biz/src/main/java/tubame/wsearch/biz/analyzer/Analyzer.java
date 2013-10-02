/*
 * Analyzer.java
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
package tubame.wsearch.biz.analyzer;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import tubame.wsearch.biz.ex.WSearchBizException;

/**
 * The interface of the analyzer for analyzing the content each.<br/>
 */
public interface Analyzer {

    /**
     * Value that specifies if you want to get a Java content analysis results
     * in {@link #getAnalyzedMap(int)}
     */
    public static final int TYPE_JAVA = 0;

    /**
     * Value that specifies if you want to get a Class content analysis results
     * in {@link #getAnalyzedMap(int)}
     */
    public static final int TYPE_CLASS = 10;

    /**
     * Value that specifies if you want to get a JSP(import) content analysis
     * results in {@link #getAnalyzedMap(int)}
     */
    public static final int TYPE_JSP_IMPORT = 20;

    /**
     * Value that specifies if you want to get a JSP(useBean) content analysis
     * results in {@link #getAnalyzedMap(int)}
     */
    public static final int TYPE_JSP_USEBEAN = 21;

    /**
     * Value that specifies if you want to get a JSP(taglib) content analysis
     * results in {@link #getAnalyzedMap(int)}
     */
    public static final int TYPE_JSP_TAGLIB = 22;

    /**
     * Value that specifies if you want to get a XML(xmlns) content analysis
     * results in {@link #getAnalyzedMap(int)}
     */
    public static final int TYPE_XML_XMLNS = 30;

    /**
     * Value that specifies if you want to get a XML(schemaLocation) content
     * analysis results in {@link #getAnalyzedMap(int)}
     */
    public static final int TYPE_XML_XSD = 31;

    /**
     * Value that specifies if you want to get a XML(DOCTYPE) content analysis
     * results in {@link #getAnalyzedMap(int)}
     */
    public static final int TYPE_XML_DTD = 32;

    /**
     * Start the analysis of the content.<br/>
     * 
     * @throws ParseException
     *             If it fails to parse code
     */
    public void analyze() throws WSearchBizException;

    /**
     * If True already done {@link #analyze()} method.<br/>
     * 
     * @return If true of the executed {@link #analyze()} method
     */
    public boolean isAnalyzed();

    /**
     * Return in the Map the analysis result of the extraction.<br/>
     * Map has become organized as follows.<br/>
     * key: Key that uniquely identifies the information that is extracted
     * (package name, etc.)<br/>
     * value: List place of extraction (ParseResult)<br/>
     * 
     * @return Map of the analysis result of extracting
     * @throws IllegalStateException
     *             If you have not done {@link #analyze()} method
     */
    public Map<String, List<ParserResult>> getAnalyzedMap();

    /**
     * Return in the Map the analysis result of the extraction.<br/>
     * Map has become organized as follows.<br/>
     * key: Key that uniquely identifies the information that is extracted
     * (package name, etc.)<br/>
     * value: List place of extraction (ParseResult)<br/>
     * 
     * @param mapType
     *            Map type
     * @return Map of the analysis result of extracting
     * @throws IllegalArgumentException
     *             If you specify a maptype that do not support
     * @throws IllegalStateException
     *             If you have not done {@link #analyze()} method
     */
    public Map<String, List<ParserResult>> getAnalyzedMap(int mapType);
}
