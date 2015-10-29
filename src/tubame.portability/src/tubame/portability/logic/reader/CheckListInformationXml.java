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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import tubame.portability.exception.JbmException;
import tubame.portability.logic.CheckListInformationFactory;
import tubame.portability.logic.CreateCheckListInfomationFile;
import tubame.portability.util.resource.MessageUtil;

/**
 * Get a variety of description read, in XPATH the checklist information in XML
 * format.<br/>
 * Self Plugin / xml / checkListInformation.xml XML file (application properties
 * described).<br/>
 */
public class CheckListInformationXml implements CheckListInformationReader {

    private Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();;

    private String projectPath;

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
        return getText(no, CreateCheckListInfomationFile.BIG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMiddleDescription(String no) throws JbmException {
        return getText(no, CreateCheckListInfomationFile.MIDDLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCheckEyeDescription(String no) throws JbmException {
        if (CheckListInformationXml.checkEyeDescriptionList.containsKey(no)) {
            return CheckListInformationXml.checkEyeDescriptionList.get(no);
        }
        String result = getText(no,
                CreateCheckListInfomationFile.VISUAL_CONFIRM);
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
        String result = getText(no,
                CreateCheckListInfomationFile.HEARING_CONFIRM);
        CheckListInformationXml.hearingDescriptionList.put(no, result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSearchDescription(String no) throws JbmException {
        return getText(no,
                CreateCheckListInfomationFile.SEARCH_PROCESS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFactorDescription(String no) throws JbmException {
        return getText(no,
                CreateCheckListInfomationFile.PORTABILITY_FACTOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDegreeDescription(String no) throws JbmException {
        return getText(no,
                CreateCheckListInfomationFile.DEGREE_DETAIL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAppropriateDescription(String no) throws JbmException {
        return getText(no,
                CreateCheckListInfomationFile.APPROPPRIATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInvestigationDescription(String no) throws JbmException {
        return getText(no,
                CreateCheckListInfomationFile.INVESTIGATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLineNumberDescription(String no) throws JbmException {
        return getText(no,
                CreateCheckListInfomationFile.LINE_NUMBER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLineNumberContentsDescription(String no)
            throws JbmException {
        return getText(no,
                CreateCheckListInfomationFile.LINE_NUMBER_CONTENTS);
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
    private String getText(String no, String mode) throws JbmException {
        	if(map==null){
        		throw new JbmException(MessageUtil.ERR_CONVERT_FILE_CLOSE);
        	}
        	Map<String, String> elm = map.get(no);
            return elm.get(mode).trim();
    }

    /**
     * Instantiation of the document and XPATH.<br/>
     */
    public void setInitila() throws ParserConfigurationException,
            SAXException, IOException {
        File file = new File(createTargetFilePath());
        if(!file.exists()){
            LOGGER.warn(MessageUtil.ERR_CONVERT_FILE_CLOSE);
        	return;
        }
        parseXML(createTargetFilePath());
    }


    /**
     * Get the checklist information file path.<br/>
     * 
     * @return Check list information file path
     * @throws IOException
     *             Self Plugin directory failure
     */
    protected String createTargetFilePath() throws IOException {
//        String path = PluginUtil.getPluginDir()
//                + ApplicationPropertyUtil.CHECK_LIST_INFORMATION_FILE_PATH;
        String path = CheckListInformationFactory.getCheckListInformationPath();
        LOGGER.debug(MessageUtil.INF_CHECKLIST_INFORMATION_PATH + path);
        return path;
    }

	private void parseXML(String fileName)
			throws FactoryConfigurationError {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = null;
		XMLStreamReader reader = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(fileName));
			reader = factory.createXMLStreamReader(stream);

			String no = null;
			Map<String, String> elms = null;
			while (reader.hasNext()) {
				reader.next();
				switch (reader.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					if (CreateCheckListInfomationFile.DESCRIPTION.equals(reader.getLocalName())) {
						no = reader.getAttributeValue(null, CreateCheckListInfomationFile.NO);
						elms = addElms(reader);
						map.put(no, elms);
					}
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
	}

	private Map<String, String> addElms(XMLStreamReader reader) throws Exception {
		Map<String, String> elms = new HashMap<String, String>();
		boolean end = false;
		String elm = null;
		String data = "";
		while(reader.hasNext() && end != true) {
			switch (reader.getEventType()) {
			case XMLStreamReader.START_ELEMENT:
				elm = reader.getLocalName();
				break;
			case XMLStreamReader.END_ELEMENT:
				if(CreateCheckListInfomationFile.DESCRIPTION.equals(reader.getLocalName())) {
					end = true;
					continue;
				}
				elms.put(elm, data);
				data = "";
				break;
			case XMLStreamReader.CHARACTERS:
				data += reader.getText();
				break;
			}
			reader.next();
		}
		return elms;
	}
}