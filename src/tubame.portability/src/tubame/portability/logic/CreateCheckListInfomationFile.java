/*
 * CreateCheckListInfomationFile.java
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
package tubame.portability.logic;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import tubame.knowhow.biz.model.generated.knowhow.Category;
import tubame.knowhow.biz.model.generated.knowhow.Chapter;
import tubame.knowhow.biz.model.generated.knowhow.CheckItem;
import tubame.knowhow.biz.model.generated.knowhow.ChildChapter;
import tubame.knowhow.biz.model.generated.knowhow.KnowhowInfomation;
import tubame.knowhow.biz.model.generated.knowhow.SearchInfomation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import tubame.portability.exception.JbmException;
import tubame.portability.model.CheckListInformation;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Make the generation of checklist information know-how from an XML file XML.<br/>
 */
public class CreateCheckListInfomationFile {
	
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CreateCheckListInfomationFile.class);
    
    /**
     * Heading tag information list
     */
    private static List<Chapter> chapterList = null;

    /**
     * Map of the category list
     */
    private static Map<String, Category> categoryMap = null;

    /**
     * Map of know-how list
     */
    private static Map<String, KnowhowInfomation> knowhowMap = null;

    /**
     * Map of search information
     */
    private static Map<String, SearchInfomation> searchInfoMap = null;

    /**
     * Attributes of XML (checkListInfomation)
     */
    private static final String ROOT_ELEMENT = "checkListInformation";
    /**
     * Attributes of XML (description)
     */
    public static final String DESCRIPTION = "description";
    /**
     * Attributes of XML (no)
     */
    public static final String NO = "no";
    /**
     * Attributes of XML (big: large item)
     */
    public static final String BIG = "big";
    /**
     * Attributes of XML (middle: in item)
     */
    public static final String MIDDLE = "middle";
    /**
     * Attributes of XML (visualConfirm: visual confirmation)
     */
    public static final String VISUAL_CONFIRM = "visualConfirm";
    /**
     * Attributes of XML (hearingConfirm: Confirmation hearing)
     */
    public static final String HEARING_CONFIRM = "hearingConfirm";
    /**
     * Attributes of XML (searchProcess: Search Procedure)
     */
    public static final String SEARCH_PROCESS = "searchProcess";
    /**
     * Attributes of XML (portabilityFactor: transplant factor)
     */
    public static final String PORTABILITY_FACTOR = "portabilityFactor";
    /**
     * Attributes of XML (degreeDetail: difficulty details)
     */
    public static final String DEGREE_DETAIL = "degreeDetail";
    /**
     * Attributes of XML (appropriateContents: recorded required)
     */
    public static final String APPROPPRIATE = "appropriateContents";
    /**
     * Attributes of XML (Investigation: line census method)
     */
    public static final String INVESTIGATION = "Investigation";
    /**
     * Attributes of XML (lineNumber: number of lines)
     */
    public static final String LINE_NUMBER = "lineNumber";
    /**
     * Attributes of XML (lineNumberContents: line number basis)
     */
    public static final String LINE_NUMBER_CONTENTS = "lineNumberContents";

    /**
     * Line number calculation flag
     */
    private static boolean calculationFlag;

    /**
     * Convert to the checklist information know-how from an XML file XML.<br/>
     * 
     * @param convert
     *            PortabilityKnowhowConverter
     * @throws JbmException
     *             Portable study tool exception
     */
    public static void xmlToXml(PortabilityKnowhowConverter convert)
            throws JbmException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        Map<String, CheckListInformation> xmlList = null;

        // Map generation of each knowhow list
        convert.createProtabilityKnowhow();
        chapterList = convert.getChapterList();
        categoryMap = convert.getCategoryMap();
        knowhowMap = convert.getKnowhowMap();
        searchInfoMap = convert.getSearchInfoMap();

        // Get the contents of the checkListInfomation.xml
        xmlList = getCheckListInfoData();

        try {
            builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (Exception e) {
            throw new JbmException(MessageUtil.ERR_PARSING_XML, e);
        }

        // Create checkListInformation node, add to document
        Element checkListElement = document.createElement(ROOT_ELEMENT);
        document.appendChild(checkListElement);

        for (Map.Entry<String, CheckListInformation> xml : xmlList.entrySet()) {

            // description
            Element descriptionElement = document.createElement(DESCRIPTION);
            descriptionElement.setAttribute(NO, xml.getKey());
            checkListElement.appendChild(descriptionElement);

            // Large category
            Element bigElement = document.createElement(BIG);
            descriptionElement.appendChild(bigElement);
            Text bigText = document.createTextNode(xml.getValue().getBigText());
            bigElement.appendChild(bigText);

            // Intermediate category
            Element middleElement = document.createElement(MIDDLE);
            descriptionElement.appendChild(middleElement);
            Text middleText = document.createTextNode(xml.getValue()
                    .getMiddleText());
            middleElement.appendChild(middleText);

            // Visual confirmation
            Element visualElement = document.createElement(VISUAL_CONFIRM);
            descriptionElement.appendChild(visualElement);
            Text visualText = document.createTextNode(xml.getValue()
                    .getCheckEyeText());
            visualElement.appendChild(visualText);

            // Confirmation hearing
            Element hearingElement = document.createElement(HEARING_CONFIRM);
            descriptionElement.appendChild(hearingElement);
            Text hearingText = document.createTextNode(xml.getValue()
                    .getHearingText());
            hearingElement.appendChild(hearingText);

            // Search procedure
            Element processElement = document.createElement(SEARCH_PROCESS);
            descriptionElement.appendChild(processElement);
            Text processText = document.createTextNode(xml.getValue()
                    .getSearchText());
            processElement.appendChild(processText);

            // Porting factor
            Element factorElement = document.createElement(PORTABILITY_FACTOR);
            descriptionElement.appendChild(factorElement);
            Text factorText = document.createTextNode(xml.getValue()
                    .getFactorText());
            factorElement.appendChild(factorText);

            // Difficulty details
            Element detailElement = document.createElement(DEGREE_DETAIL);
            descriptionElement.appendChild(detailElement);
            Text detailText = document.createTextNode(xml.getValue()
                    .getDegreeDetailText());
            detailElement.appendChild(detailText);

            // Required fields recorded
            Element appropElement = document.createElement(APPROPPRIATE);
            descriptionElement.appendChild(appropElement);
            Text appropText = document.createTextNode(xml.getValue()
                    .getAppropriateText());
            appropElement.appendChild(appropText);

            // Investigative method
            Element investElement = document.createElement(INVESTIGATION);
            descriptionElement.appendChild(investElement);
            Text investText = document.createTextNode(xml.getValue()
                    .getInvestText());
            investElement.appendChild(investText);

            // Number of lines
            Element lineNumElement = document.createElement(LINE_NUMBER);
            descriptionElement.appendChild(lineNumElement);
            Text lineNumText = document.createTextNode(xml.getValue()
                    .getLineNumberText());
            lineNumElement.appendChild(lineNumText);

            // Line number basis
            Element contentsElement = document
                    .createElement(LINE_NUMBER_CONTENTS);
            descriptionElement.appendChild(contentsElement);
            Text contentsText = document.createTextNode(xml.getValue()
                    .getLineNumberContentsText());
            contentsElement.appendChild(contentsText);
        }

        // The output of the XML document
        DOMSource source = new DOMSource(document);
        String xmlOutPath = "";
        try {
            xmlOutPath = PluginUtil.getPluginDir()
                    + ApplicationPropertyUtil.CHECK_LIST_INFORMATION_FILE_PATH;
            
            LOGGER.info("CheckListInformationPath="+CheckListInformationFactory.getCheckListInformationPath());

            
            StreamResult result = new StreamResult(CheckListInformationFactory.getCheckListInformationPath());
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = null;

            transformer = transFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,
                    ApplicationPropertyUtil.CHARSET_XML);
            transformer.transform(source, result);

        }
        catch (IOException e) {
            // Plug-in directory acquisition failure
            throw new JbmException(
                    MessageUtil.ERR_PLUGINUTIL_PLUGIN_DIRECTORY_GET, e);
        }
        catch (TransformerConfigurationException e) {
            // Transformer generation failure
            throw new JbmException(MessageUtil.ERR_CONVERT_KNOWHOW_XML, e);
        } catch (TransformerException e) {
            // Conversion failure
            throw new JbmException(MessageUtil.ERR_CONVERT_KNOWHOW_XML, e);
        }
    }

    /**
     * Get the information of the parent checklist information XML.<br/>
     * 
     * @return Map<String, CheckListInformation>
     */
    private static Map<String, CheckListInformation> getCheckListInfoData() {
        Map<String, CheckListInformation> listMap = new LinkedHashMap<String, CheckListInformation>();
        String big;

        // 1.ChapterList
        for (Chapter chapter : chapterList) {
            calculationFlag = false;
            // <Large item>
            big = chapter.getChapterName();

            // 2.ChildChapter
            List<ChildChapter> children = chapter.getChildChapters();
            getChildrenInfoData(children, listMap, big);
        }
        return listMap;
    }

    /**
     * Get the child hierarchical data of checklist information.<br/>
     * 
     * @param children
     *            Child hierarchical data
     * @param listMap
     *            Map<String, CheckListInformation>
     * @param big
     *            Large category
     */
    private static void getChildrenInfoData(List<ChildChapter> children,
            Map<String, CheckListInformation> listMap, String big) {
        String childChapNo;
        BigInteger knowhowNo;
        BigInteger checkItemNo;
        String mid;
        String visual;
        String hearing;
        String proc;
        String factor;
        String detail;

        String lineNum;
        String lineNumContent;
        String invest;

        String approp;

        for (ChildChapter childChapter : children) {
            // No (heading No. Category No)
            childChapNo = childChapter.getChildCapterNo();

            String cateRefKey = childChapter.getChapterCategoryRefKey();
            // 3.CategoryList
            for (Map.Entry<String, Category> category : categoryMap.entrySet()) {
                // Mapping and determination of CategoryList ChapterList
                String categoryId = category.getKey();
                if (cateRefKey.equals(categoryId)) {
                    if (!category.getValue().isAppropriate()) {
                        calculationFlag = true;
                    }

                    for (String knowhowRefKey : category.getValue()
                            .getKnowhowRefKeies()) {
                        // <Active items>
                        mid = category.getValue().getCategoryName();

                        // 4.KnowhowList
                        for (Map.Entry<String, KnowhowInfomation> knowhow : knowhowMap
                                .entrySet()) {

                            // Mapping and determination of KnowhowList
                            // CategoryList
                            String knowhowId = knowhow.getKey();
                            if (knowhowRefKey.equals(knowhowId)) {
                                // No (know-how No)
                                knowhowNo = knowhow.getValue().getKnowhowNo();

                                // 5.CheckItem
                                for (CheckItem checkItem : knowhow.getValue()
                                        .getCheckItems()) {

                                    // No (check item No)
                                    checkItemNo = checkItem.getCheckItemNo();
                                    // <Visual confirmation>
                                    visual = checkItem.getVisualConfirm();
                                    // <Hearing Contents>
                                    hearing = checkItem.getHearingConfirm();
                                    // <Search procedure>
                                    proc = checkItem.getSearchProcess();
                                    // <Transplant factor>
                                    factor = checkItem.getPortabilityFactor();
                                    // <Difficulty details>
                                    detail = checkItem.getDegreeDetail();

                                    String searchRefKey = checkItem
                                            .getSearchRefKey();

                                    // Decision of whether or not search
                                    // information registration
                                    if (searchRefKey == null) {
                                        // Skip search for information without
                                        // registration
                                        continue;
                                    }

                                    // 6.SearchInfomationList
                                    for (Map.Entry<String, SearchInfomation> search : searchInfoMap
                                            .entrySet()) {

                                        String searchInfoId = search.getKey();
                                        // Mapping and determination of
                                        // SearchInfomationList KnowhowList
                                        if (searchRefKey.equals(searchInfoId)) {
                                            // <Number of lines>
                                            lineNum = CreateCheckListInfomationFile
                                                    .getLineNum(search);
                                            // <Line number basis>
                                            lineNumContent = search.getValue()
                                                    .getLineNumberInfomation()
                                                    .getLineNumberContents();
                                            // Line census method>
                                            invest = search.getValue()
                                                    .getLineNumberInfomation()
                                                    .getInvestigation();
                                            // Recorded Required fields>
                                            if (!search.getValue()
                                                    .getAppropriate()
                                                    .isLineNumberAppropriate()) {
                                                // If there is no line number
                                                // calculation and set the
                                                // non-calculated description
                                                approp = search
                                                        .getValue()
                                                        .getAppropriate()
                                                        .getAppropriateContents();
                                            } else {
                                                // When there is a number of
                                                // lines calculated and set to
                                                // an empty
                                                approp = StringUtil.EMPTY;
                                            }
                                            /**
                                             * It contains the contents of
                                             * CheckListInfomation.xml
                                             */
                                            String no = childChapNo + "-"
                                                    + knowhowNo + "-"
                                                    + checkItemNo;
                                            CheckListInformation xmlValue = new CheckListInformation(
                                                    big, mid, visual, hearing,
                                                    proc, factor, detail,
                                                    approp, invest, lineNum,
                                                    lineNumContent);
                                            listMap.put(no, xmlValue);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            List<ChildChapter> moreChildren = childChapter.getChildChapters();
            if (moreChildren.size() > 0) {
                getChildrenInfoData(moreChildren, listMap, big);
            } else {
                calculationFlag = false;
            }
        }
    }

    /**
     * Get the number of lines.<br/>
     * 
     * @param search
     *            Search Info Map
     * @return Number of lines
     */
    private static String getLineNum(Entry<String, SearchInfomation> search) {
        if (!calculationFlag
                && search.getValue().getAppropriate().isLineNumberAppropriate()) {
            return search.getValue().getLineNumberInfomation().getLineNumber();
        }
        return "0";
    }
}
