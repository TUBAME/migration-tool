/*
 * CheckListInformationXml.java
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
package tubame.portability.logic.reader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.logic.CreateCheckListInfomationFile;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Get a variety of description read, in XPATH the checklist information in XML
 * format.<br/>
 * Self Plugin / xml / checkListInformation.xml XML file (application properties
 * described).<br/>
 */
public class CheckListInformationXml implements CheckListInformationReader {

    /**
     * XPath object
     */
    private XPath xpath;
    /**
     * Document object
     */
    private Document doc;

    /**
     * Adapter for speed improvement
     */
    private static Map<String, String> checkEyeDescriptionList = new HashMap<String, String>();

    /**
     * Adapter for speed improvement
     */
    private static Map<String, String> hearingDescriptionList = new HashMap<String, String>();

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CheckListInformationXml.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBigDescription(String no) throws JbmException {
        return getText(createLocation(no, CreateCheckListInfomationFile.BIG));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMiddleDescription(String no) throws JbmException {
        return getText(createLocation(no, CreateCheckListInfomationFile.MIDDLE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCheckEyeDescription(String no) throws JbmException {
        if (CheckListInformationXml.checkEyeDescriptionList.containsKey(no)) {
            return CheckListInformationXml.checkEyeDescriptionList.get(no);
        }
        String result = getText(createLocation(no,
                CreateCheckListInfomationFile.VISUAL_CONFIRM));
        CheckListInformationXml.checkEyeDescriptionList.put(no, result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHearingDescription(String no) throws JbmException {
        if (CheckListInformationXml.hearingDescriptionList.containsKey(no)) {
            return CheckListInformationXml.hearingDescriptionList.get(no);
        }
        String result = getText(createLocation(no,
                CreateCheckListInfomationFile.HEARING_CONFIRM));
        CheckListInformationXml.hearingDescriptionList.put(no, result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSearchDescription(String no) throws JbmException {
        return getText(createLocation(no,
                CreateCheckListInfomationFile.SEARCH_PROCESS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFactorDescription(String no) throws JbmException {
        return getText(createLocation(no,
                CreateCheckListInfomationFile.PORTABILITY_FACTOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDegreeDescription(String no) throws JbmException {
        return getText(createLocation(no,
                CreateCheckListInfomationFile.DEGREE_DETAIL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAppropriateDescription(String no) throws JbmException {
        return getText(createLocation(no,
                CreateCheckListInfomationFile.APPROPPRIATE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInvestigationDescription(String no) throws JbmException {
        return getText(createLocation(no,
                CreateCheckListInfomationFile.INVESTIGATION));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLineNumberDescription(String no) throws JbmException {
        return getText(createLocation(no,
                CreateCheckListInfomationFile.LINE_NUMBER));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLineNumberContentsDescription(String no)
            throws JbmException {
        return getText(createLocation(no,
                CreateCheckListInfomationFile.LINE_NUMBER_CONTENTS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAdapter() {
        checkEyeDescriptionList.clear();
        hearingDescriptionList.clear();
    }

    /**
     * If the document and xpath does not exist, <br/>
     * after generating an instance, <br/>
     * get the value of the location in the xpath.<br/>
     * 
     * @param location
     *            Location
     * @return Acquisition value at xpath
     * @throws JbmException
     *             Xpath acquisition failure
     */
    private String getText(String location) throws JbmException {
        try {
            setInitila();
            return xpath.evaluate(location, doc).trim();
        } catch (XPathExpressionException e) {
            throw new JbmException(
                    e,
                    LOGGER,
                    ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.CHECKLIST_INFORMATION_FILE_ERROR });
        } catch (SAXException e) {
            throw new JbmException(
                    e,
                    LOGGER,
                    ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.CHECKLIST_INFORMATION_FILE_ERROR });
        } catch (IOException e) {
            throw new JbmException(
                    e,
                    LOGGER,
                    ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.CHECKLIST_INFORMATION_FILE_ERROR });
        } catch (ParserConfigurationException e) {
            throw new JbmException(
                    e,
                    LOGGER,
                    ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.CHECKLIST_INFORMATION_FILE_ERROR });
        }
    }

    /**
     * Instantiation of the document and XPATH.<br/>
     * 
     * @throws ParserConfigurationException
     *             DocumentBuilder instantiation failure
     * @throws IOException
     *             Self Plugin directory failure
     * @throws SAXException
     *             XML parsing error
     */
    protected void setInitila() throws ParserConfigurationException,
            SAXException, IOException {
        // Do regeneration check item information XML file generated from
        // knowledge XML.
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        doc = builder.parse(new File(createTargetFilePath()));
        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
    }

    /**
     * Get the checklist information file path.<br/>
     * 
     * @return Check list information file path
     * @throws IOException
     *             Self Plugin directory failure
     */
    protected String createTargetFilePath() throws IOException {
        String path = PluginUtil.getPluginDir()
                + ApplicationPropertyUtil.CHECK_LIST_INFORMATION_FILE_PATH;
        LOGGER.debug(MessageUtil.INF_CHECKLIST_INFORMATION_PATH + path);
        return path;
    }

    /**
     * Generate the Location of XPATH.<br/>
     * Generate the Location of checkListInformation parent element.<br/>
     * 
     * @param no
     *            Target No
     * @param mode
     *            Attribute value
     * @return Location
     */
    private String createLocation(String no, String mode) {
        StringBuilder sb = new StringBuilder();
        sb.append("/checkListInformation/description[@no='");
        sb.append(no);
        sb.append("']/");
        sb.append(mode);
        return sb.toString();
    }
}
