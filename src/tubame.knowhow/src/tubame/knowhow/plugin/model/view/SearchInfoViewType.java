/*
 * SearchInfoViewType.java
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
package tubame.knowhow.plugin.model.view;

import java.util.Map;

import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.model.generated.knowhow.SearchInformation;
import tubame.knowhow.biz.model.LineNumberEnum;

import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.ui.wizard.register.AbstractCreateKnowhowPage;
import tubame.knowhow.plugin.ui.wizard.register.CreateSearchInfoPage;

/**
 * Class to search for information of know-how entry view.<br/>
 * Class that defines the model of information search expertise of entry view.<br/>
 */
public class SearchInfoViewType extends AbstractViewType {

    /** Serial ID */
    private static final long serialVersionUID = -219036105402792525L;
    /** Number of lines recorded */
    private boolean appropriateLine;
    /** Unknown / TODO line */
    private String unKnownLine;
    /** Search file type */
    private String fileType;
    /** Search Keywords 1 */
    private String searchKeyword1;
    /** Search keyword 2 */
    private String searchKeyword2;
    /** Number of lines */
    private String lineNumber;
    /** Line number basis */
    private String lineNumberContents;
    /** Survey content */
    private String investigation;
    /** Search module */
    private String pythonModule;
    /** Search module content. */
    private String pythonModuleContext;
    /** Number of lines recorded basis */
    private String appropriateContext;

    /**
     * Constructor.<br/>
     * 
     * @param knowhowPageData
     *            Search information registration page data
     * @param keyValue
     *            Item ID
     */
    public SearchInfoViewType(AbstractCreateKnowhowPage knowhowPageData,
            String keyValue) {

        super.setRegisterName(keyValue);
        super.setRegisterKey(keyValue);
        CreateSearchInfoPage searchInfoPage = (CreateSearchInfoPage) knowhowPageData;

        appropriateLine = searchInfoPage.isAppropriateLine();
        fileType = searchInfoPage.getFileType();
        searchKeyword1 = searchInfoPage.getSearchKeyword1();
        searchKeyword2 = searchInfoPage.getSearchKeyword2();
        lineNumber = searchInfoPage.getLineNumber();
        unKnownLine = searchInfoPage.getUnKnownLine();
        lineNumberContents = searchInfoPage.getLineNumberContents();
        investigation = searchInfoPage.getInvestigation();
        pythonModule = searchInfoPage.getPythonModule();
        pythonModuleContext = searchInfoPage.getPythonModuleContext();
        appropriateContext = searchInfoPage.getAppropriateContext();
    }

    /**
     * Constructor.<br/>
     * 
     * @param searchInfo
     *            Know-how XML (information retrieval)
     */
    public SearchInfoViewType(SearchInformation searchInfo) {
        super.setRegisterName(searchInfo.getSearchInfoId());
        super.setRegisterKey(searchInfo.getSearchInfoId());
        appropriateLine = searchInfo.getAppropriate().isLineNumberAppropriate();
        fileType = searchInfo.getFileType();
        searchKeyword1 = searchInfo.getSearchKey1();
        searchKeyword2 = searchInfo.getSearchKey2();
        setLineNumberInfo(searchInfo);
        lineNumberContents = searchInfo.getLineNumberInformation()
                .getLineNumberContents();
        investigation = searchInfo.getLineNumberInformation().getInvestigation();
        pythonModule = searchInfo.getPythonModule();
        pythonModuleContext = obtainPythonModuleContext(searchInfo
                .getPythonModule());
        appropriateContext = searchInfo.getAppropriate()
                .getAppropriateContents();
    }

    /**
     * Set the line number information.<br/>
     * The number of lines it is determined either TODO, unknown, specified
     * number of lines,set the line number information.<br/>
     * 
     * @param searchInfo
     *            Search information
     */
    private void setLineNumberInfo(SearchInformation searchInfo) {
        if (searchInfo.getLineNumberInformation().getLineNumber()
                .equals(LineNumberEnum.ToDoSE.getName())) {
            lineNumber = CmnStringUtil.INITIAL_NUMBER;
            unKnownLine = searchInfo.getLineNumberInformation().getLineNumber();
        } else if (searchInfo.getLineNumberInformation().getLineNumber()
                .equals(LineNumberEnum.Unknown.getName())) {
            lineNumber = CmnStringUtil.INITIAL_NUMBER;
            unKnownLine = searchInfo.getLineNumberInformation().getLineNumber();
        } else {
            lineNumber = searchInfo.getLineNumberInformation().getLineNumber();
            unKnownLine = CmnStringUtil.EMPTY;
        }
    }

    /**
     * Get the search module content.<br/>
     * Extract the contents of the search module from any module Search Map
     * information.<br/>
     * 
     * @param pythonModule
     *            Search module
     * @return Search module content
     */
    private String obtainPythonModuleContext(String pythonModule) {
        for (Map.Entry<String, String> entry : FileManagement
                .getSearchModuleMap().entrySet()) {
            if (entry.getKey().equals(pythonModule)) {
                return entry.getValue();
            }
        }
        return CmnStringUtil.EMPTY;
    }

    /**
     * Get appropriateLine.<br/>
     * 
     * @return appropriateLine
     */
    public boolean isAppropriateLine() {
        return appropriateLine;
    }

    /**
     * Get fileType.<br/>
     * 
     * @return fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Get searchKeyword1.<br/>
     * 
     * @return searchKeyword1
     */
    public String getSearchKeyword1() {
        return searchKeyword1;
    }

    /**
     * Get searchKeyword2.<br/>
     * 
     * @return searchKeyword2
     */
    public String getSearchKeyword2() {
        return searchKeyword2;
    }

    /**
     * Get lineNumber.<br/>
     * 
     * @return lineNumber
     */
    public String getLineNumber() {
        return lineNumber;
    }

    /**
     * Get lineNumberContents.<br/>
     * 
     * @return lineNumberContents
     */
    public String getLineNumberContents() {
        return lineNumberContents;
    }

    /**
     * Get investigation.<br/>
     * 
     * @return investigation
     */
    public String getInvestigation() {
        return investigation;
    }

    /**
     * Get pythonModule.<br/>
     * 
     * @return pythonModule
     */
    public String getPythonModule() {
        return pythonModule;
    }

    /**
     * Get pythonModuleContext.<br/>
     * 
     * @return pythonModuleContext
     */
    public String getPythonModuleContext() {
        return pythonModuleContext;
    }

    /**
     * Get appropriateContext.<br/>
     * 
     * @return appropriateContext
     */
    public String getAppropriateContext() {
        return appropriateContext;
    }

    /**
     * Get unKnownLine.<br/>
     * 
     * @return unKnownLine
     */
    public String getUnKnownLine() {
        return unKnownLine;
    }

    /**
     * Set appropriateLine.<br/>
     * 
     * @param appropriateLine
     *            appropriateLine
     */
    public void setAppropriateLine(boolean appropriateLine) {
        this.appropriateLine = appropriateLine;
    }

    /**
     * Set the fileType.<br/>
     * 
     * @param fileType
     *            fileType
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * Set searchKeyword1.<br/>
     * 
     * @param searchKeyword1
     *            searchKeyword1
     */
    public void setSearchKeyword1(String searchKeyword1) {
        this.searchKeyword1 = searchKeyword1;
    }

    /**
     * Set searchKeyword2.<br/>
     * 
     * @param searchKeyword2
     *            searchKeyword2
     */
    public void setSearchKeyword2(String searchKeyword2) {
        this.searchKeyword2 = searchKeyword2;
    }

    /**
     * Set the lineNumber.<br/>
     * 
     * @param lineNumber
     *            lineNumber
     */
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Set lineNumberContents.<br/>
     * 
     * @param lineNumberContents
     *            lineNumberContents
     */
    public void setLineNumberContents(String lineNumberContents) {
        this.lineNumberContents = lineNumberContents;
    }

    /**
     * Set the investigation.<br/>
     * 
     * @param investigation
     *            investigation
     */
    public void setInvestigation(String investigation) {
        this.investigation = investigation;
    }

    /**
     * Set pythonModule.<br/>
     * 
     * @param pythonModule
     *            pythonModule
     */
    public void setPythonModule(String pythonModule) {
        this.pythonModule = pythonModule;
    }

    /**
     * Set pythonModuleContext.<br/>
     * 
     * @param pythonModuleContext
     *            pythonModuleContext
     */
    public void setPythonModuleContext(String pythonModuleContext) {
        this.pythonModuleContext = pythonModuleContext;
    }

    /**
     * Set appropriateContext.<br/>
     * 
     * @param appropriateContext
     *            appropriateContext
     */
    public void setAppropriateContext(String appropriateContext) {
        this.appropriateContext = appropriateContext;
    }

    /**
     * Set unKnownLine.<br/>
     * 
     * @param unKnownLine
     *            unKnownLine
     */
    public void setUnKnownLine(String unKnownLine) {
        this.unKnownLine = unKnownLine;
    }

}
