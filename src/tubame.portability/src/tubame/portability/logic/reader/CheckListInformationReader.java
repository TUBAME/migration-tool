/*
 * CheckListInformationReader.java
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

import tubame.portability.exception.JbmException;

/**
 * Interfaces to manage the checklist information data.<br/>
 * If the check list information is manipulated, implements this interface.<br/>
 */
public interface CheckListInformationReader {
    /**
     * Get a description of the major items of the checklist information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getBigDescription(String no) throws JbmException;

    /**
     * Get a description of the item in the checklist information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getMiddleDescription(String no) throws JbmException;

    /**
     * Get a description of the visual inspection checklist information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getCheckEyeDescription(String no) throws JbmException;

    /**
     * Get a description of the confirmation hearing checklist and information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getHearingDescription(String no) throws JbmException;

    /**
     * Get a description of the search procedure checklist of information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getSearchDescription(String no) throws JbmException;

    /**
     * Get a description of transplant factors checklist information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getFactorDescription(String no) throws JbmException;

    /**
     * Get a description of the difficulty of detail checklist information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getDegreeDescription(String no) throws JbmException;

    /**
     * Get a description of the reported required checklist items information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getAppropriateDescription(String no) throws JbmException;

    /**
     * Get the Description of the investigation procedure of checklist
     * information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getInvestigationDescription(String no) throws JbmException;

    /**
     * Get the line number of checklist information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getLineNumberDescription(String no) throws JbmException;

    /**
     * Get the Description of the number of lines of evidence checklist
     * information.<br/>
     * 
     * @param no
     *            No
     * @return Description
     * @throws JbmException
     *             XML operation failure
     */
    public String getLineNumberContentsDescription(String no)
            throws JbmException;

    /**
     * The initialization of the processing speed adapter.<br/>
     * 
     */
    public void clearAdapter();
}
