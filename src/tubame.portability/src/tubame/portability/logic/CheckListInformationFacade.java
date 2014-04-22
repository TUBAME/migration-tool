/*
 * CheckListInformationFacade.java
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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import tubame.portability.exception.JbmException;
import tubame.portability.logic.reader.CheckListInformationReader;
import tubame.portability.logic.reader.CheckListInformationXml;
import tubame.portability.util.StringUtil;

/**
 * Facade class to manipulate the checklist information data.<br/>
 * Get the description read, large, medium and visual hearing item checklist
 * information XML file.<br/>
 */
public class CheckListInformationFacade {
    /**
     * Reader that reads an XML
     */
    private final CheckListInformationReader checkListInformationRead = new CheckListInformationXml();

    /**
     * Get the checklist file information acquisition class.<br/>
     * {@link CheckListInformationXml}
     * 
     * @return Checklist information manipulation class
     */
    protected CheckListInformationReader createCheckListInformationReader() {
        return checkListInformationRead;
    }

    /**
     * Initialize checklist file information acquisition class.<br/>
     * 
     */
    public void initCheckListInformationReader() {
		try {
			checkListInformationRead.setInitila();
		} catch (ParserConfigurationException e) {
			// no operation
		} catch (SAXException e) {
			// no operation
		} catch (IOException e) {
			// no operation
		}
        checkListInformationRead.clearAdapter();
    }

    /**
     * Get a large item description that corresponds to the No.<br/>
     * 
     * @param no
     *            No
     * @return Large Item Description
     */
    public String getBigDescription(String no) {
        try {
            return createCheckListInformationReader().getBigDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get the item description in that corresponds to the No.<br/>
     * 
     * @param no
     *            No
     * @return In Item Description
     */
    public String getMiddleDescription(String no) {
        try {
            return createCheckListInformationReader().getMiddleDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * 
     * Get a visual check Description corresponding to the No.<br/>
     * 
     * @param no
     *            No
     * @return Visual check Description
     */
    public String getCheckEyeDescription(String no) {
        try {
            return createCheckListInformationReader()
                    .getCheckEyeDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get hearing item description that corresponds to the No.<br/>
     * 
     * @param no
     *            No
     * @return Hearing Item Description
     */
    public String getHearingDescription(String no) {
        try {
            return createCheckListInformationReader().getHearingDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get a search step-by-step instructions corresponding to the No.<br/>
     * 
     * @param no
     *            No
     * @return Search Step-by-Step Instructions
     */
    public String getSearchDescription(String no) {
        try {
            return createCheckListInformationReader().getSearchDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get a transplant factors corresponding descriptions No.<br/>
     * 
     * @param no
     *            No
     * @return Porting factor description
     */
    public String getFactorDescription(String no) {
        try {
            return createCheckListInformationReader().getFactorDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get detailed description difficulty corresponding to the No.<br/>
     * 
     * @param no
     *            No
     * @return Difficulty detailed description
     */
    public String getDegreeDescription(String no) {
        try {
            return createCheckListInformationReader().getDegreeDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get a description of the reported mandatory fields corresponding to No.<br/>
     * 
     * @param no
     *            No
     * @return Description of recorded mandatory
     */
    public String getAppropriateDescription(String no) {
        try {
            return createCheckListInformationReader()
                    .getAppropriateDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get the corresponding survey methods described in No.<br/>
     * 
     * @param no
     *            No
     * @return Description of recorded mandatory
     */
    public String getInvestigationDescription(String no) {
        try {
            return createCheckListInformationReader()
                    .getInvestigationDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get the number of lines corresponding to the No.<br/>
     * 
     * @param no
     *            No
     * @return Number of lines
     */
    public String getLineNumberDescription(String no) {
        try {
            return createCheckListInformationReader().getLineNumberDescription(
                    no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get the corresponding line number basis to No.<br/>
     * 
     * @param no
     *            No
     * @return Line number basis
     */
    public String getLineNumberContentsDescription(String no) {
        try {
            return createCheckListInformationReader()
                    .getLineNumberContentsDescription(no);
        } catch (JbmException e) {
            // no operation
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get with / without visual confirmation item.<br/>
     * If you have a visual confirmation content, visual confirmation item.<br/>
     * 
     * @param no
     *            No
     * @return true:There is visual confirmation item, false:There is no
     *         confirmation item the visual
     */
    public boolean isCheckEye(String no) {
        if (StringUtil.isEmpty(getCheckEyeDescription(no))) {
            return false;
        }
        return true;
    }

    /**
     * Get with / without hearing item.<br/>
     * If the confirmation hearing content is present, hearing confirmation
     * item.<br/>
     * 
     * @param no
     *            No
     * @return true:There hearing confirmation item false:Without hearing
     *         confirmation item
     */
    public boolean isHearing(String no) {
        if (StringUtil.isEmpty(getHearingDescription(no))) {
            return false;
        }
        return true;
    }
}
