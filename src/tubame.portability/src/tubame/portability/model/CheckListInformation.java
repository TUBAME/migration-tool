/*
 * CheckListInformation.java
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
package tubame.portability.model;

/**
 * Checklist information view data.<br/>
 */
public class CheckListInformation {
    /**
     * Large Item Description
     */
    private String bigText;
    /**
     * Middle Item Description
     */
    private String middleText;
    /**
     * Visual check Description
     */
    private String checkEyeText;
    /**
     * Hearing check Description
     */
    private String hearingText;
    /**
     * Search procedure
     */
    private String searchText;
    /**
     * Porting factor
     */
    private String factorText;
    /**
     * Difficulty details
     */
    private String degreeDetailText;
    /**
     * Required fields recorded
     */
    private String appropriateText;
    /**
     * Investigative method
     */
    private String investText;
    /**
     * Number of lines
     */
    private String lineNumberText;
    /**
     * Line number basis
     */
    private String lineNumberContentsText;

    /**
     * Constructor.<br/>
     * 
     */
    public CheckListInformation() {
        super();
    }

    /**
     * Constructor.<br/>
     * Generate this class.<br/>
     * 
     * @param big
     *            Large Item Description
     * @param middle
     *            Middle Item Description
     * @param checkEye
     *            Visual confirmation
     * @param hearing
     *            Hearing confirmation item
     * @param factor
     * @param detail
     * @param approp
     * @param invest
     * @param lineNum
     * @param lineNumContents
     */
    public CheckListInformation(String big, String middle, String checkEye,
            String hearing, String process, String factor, String detail,
            String approp, String invest, String lineNum, String lineNumContents) {
        super();
        setBigText(big);
        setMiddleText(middle);
        setCheckEyeText(checkEye);
        setHearingText(hearing);
        setSearchText(process);
        setFactorText(factor);
        setDegreeDetailText(detail);
        setAppropriateText(approp);
        setInvestText(invest);
        setLineNumberText(lineNum);
        setLineNumberContentsText(lineNumContents);
    }

    /**
     * Get a large item description.<br/>
     * 
     * @return Large Item Description
     */
    public String getBigText() {
        return bigText;
    }

    /**
     * Set a large item description.<br/>
     * 
     * @param bigText
     *            Large Item Description
     */
    public void setBigText(String bigText) {
        this.bigText = bigText;
    }

    /**
     * Get middle item description.<br/>
     * 
     * @return Middle Item Description
     */
    public String getMiddleText() {
        return middleText;
    }

    /**
     * Set Middle item description.<br/>
     * 
     * @param middleText
     *            Middle Item Description
     */
    public void setMiddleText(String middleText) {
        this.middleText = middleText;
    }

    /**
     * Get a visual check Description.<br/>
     * 
     * @return Visual check Description
     */
    public String getCheckEyeText() {
        return checkEyeText;
    }

    /**
     * Set a visual check Description.<br/>
     * 
     * @param checkEyeText
     *            Visual check Description
     */
    public void setCheckEyeText(String checkEyeText) {
        this.checkEyeText = checkEyeText;
    }

    /**
     * Get a hearing check Description.<br/>
     * 
     * @return Hearing check Description
     */
    public String getHearingText() {
        return hearingText;
    }

    /**
     * Set hearing check Description.<br/>
     * 
     * @param hearingText
     *            Hearing check Description
     */
    public void setHearingText(String hearingText) {
        this.hearingText = hearingText;
    }

    /**
     * Get the search procedure.<br/>
     * 
     * @return Search procedure
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Set the search procedure.<br/>
     * 
     * @param searchText
     *            Search procedure
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * Get transplant factors.<br/>
     * 
     * @return Porting factor
     */
    public String getFactorText() {
        return factorText;
    }

    /**
     * Set the transplant factor.<br/>
     * 
     * @param factorText
     *            Porting factor
     */
    public void setFactorText(String factorText) {
        this.factorText = factorText;
    }

    /**
     * Get more difficulty.<br/>
     * 
     * @return Difficulty details
     */
    public String getDegreeDetailText() {
        return degreeDetailText;
    }

    /**
     * Set more difficulty.<br/>
     * 
     * @param degreeDetailText
     *            Difficulty details
     */
    public void setDegreeDetailText(String degreeDetailText) {
        this.degreeDetailText = degreeDetailText;
    }

    /**
     * Get record required.<br/>
     * 
     * @return Required fields recorded
     */
    public String getAppropriateText() {
        return appropriateText;
    }

    /**
     * Set the record required.<br/>
     * 
     * @param appropriateText
     *            Required fields recorded
     */
    public void setAppropriateText(String appropriateText) {
        this.appropriateText = appropriateText;
    }

    /**
     * Get the survey methods.<br/>
     * 
     * @return Investigative method
     */
    public String getInvestText() {
        return investText;
    }

    /**
     * Set the survey methods.<br/>
     * 
     * @param investText
     *            Investigative method
     */
    public void setInvestText(String investText) {
        this.investText = investText;
    }

    /**
     * Get the number of lines.<br/>
     * 
     * @return Number of lines
     */
    public String getLineNumberText() {
        return lineNumberText;
    }

    /**
     * Set the number of lines.<br/>
     * 
     * @param lineNumberText
     *            Number of lines
     */
    public void setLineNumberText(String lineNumberText) {
        this.lineNumberText = lineNumberText;
    }

    /**
     * Get the number of lines basis.<br/>
     * 
     * @return Line number basis
     */
    public String getLineNumberContentsText() {
        return lineNumberContentsText;
    }

    /**
     * Set the number of lines basis.<br/>
     * 
     * @param lineNumContentsText
     *            Line number basis
     */
    public void setLineNumberContentsText(String lineNumContentsText) {
        lineNumberContentsText = lineNumContentsText;
    }
}
