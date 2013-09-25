/*
 * CheckItemViewType.java
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
package org.tubame.knowhow.plugin.model.view;

import org.tubame.knowhow.biz.model.generated.knowhow.CheckItem;
import org.tubame.knowhow.biz.model.PortabilityDegreeEnum;
import org.tubame.knowhow.biz.model.PortabilityDegreeVariable;
import org.tubame.knowhow.plugin.ui.wizard.register.AbstractCreateKnowhowPage;
import org.tubame.knowhow.plugin.ui.wizard.register.CreateCheckItemPage;

/**
 * Check Item Information class know-how of entry view.<br/>
 * JDK7<br/>
 */
public class CheckItemViewType extends AbstractViewType {

    /** Serial ID */
    private static final long serialVersionUID = 8299846477883553526L;
    /** Search indicates whether or not to perform */
    private boolean searchExistance;
    /** Search procedure */
    private String searchProcess;
    /** Transplant factors */
    private String portabilityFactor;
    /** Porting difficulty */
    private String protabilityDegree;
    /** More difficulty */
    private String degreeDetail;
    /** Hearing confirmation item */
    private String hearingConfirm;
    /** Visual confirmation item */
    private String visualConfirm;
    /** Search reference key */
    private String searchRefKey;

    /**
     * Constructor.<br/>
     * 
     * @param knowhowPageData
     *            Check Item Information
     * @param keyValue
     *            Item ID
     */
    public CheckItemViewType(AbstractCreateKnowhowPage knowhowPageData,
            String keyValue) {

        super.setRegisterName(knowhowPageData.getRegisteredName());
        super.setRegisterKey(keyValue);
        CreateCheckItemPage checkItemPage = (CreateCheckItemPage) knowhowPageData;
        searchProcess = checkItemPage.getSearchProcess();
        searchExistance = checkItemPage.isSearchExistance();
        portabilityFactor = checkItemPage.getPortabilityFactor();
        protabilityDegree = checkItemPage.getProtabilityDegree();
        degreeDetail = checkItemPage.getDegreeDetail();
        visualConfirm = checkItemPage.getVisualConfirm();
        hearingConfirm = checkItemPage.getHearingConfirm();
    }

    /**
     * Constructor.<br/>
     * 
     * @param checkItem
     *            Know-how XML (check items)
     */
    public CheckItemViewType(CheckItem checkItem) {

        super.setRegisterName(checkItem.getCheckItemName());
        super.setRegisterKey(checkItem.getCheckItemId());
        searchRefKey = checkItem.getSearchRefKey();
        searchProcess = checkItem.getSearchProcess();
        searchExistance = checkItem.isSearchExistance();
        portabilityFactor = checkItem.getPortabilityFactor();
        protabilityDegree = getDegreeVariableXmlToView(checkItem
                .getPortabilityDegree());
        degreeDetail = checkItem.getDegreeDetail();
        visualConfirm = checkItem.getVisualConfirm();
        hearingConfirm = checkItem.getHearingConfirm();

    }

    /**
     * Get SearchProcess.
     * 
     * @return searchProcess
     */
    public String getSearchProcess() {
        return searchProcess;
    }

    /**
     * Get portabilityFactor.<br/>
     * 
     * @return portabilityFactor
     */
    public String getPortabilityFactor() {
        return portabilityFactor;
    }

    /**
     * Get protabilityDegree.<br/>
     * 
     * @return protabilityDegree
     */
    public String getProtabilityDegree() {
        return protabilityDegree;
    }

    /**
     * Get degreeDetail.<br/>
     * 
     * @return degreeDetail
     */
    public String getDegreeDetail() {
        return degreeDetail;
    }

    /**
     * Get hearingConfirm.<br/>
     * 
     * @return hearingConfirm
     */
    public String getHearingConfirm() {
        return hearingConfirm;
    }

    /**
     * Get VisualConfirm.<br/>
     * 
     * @return visualConfirm
     */
    public String getVisualConfirm() {
        return visualConfirm;
    }

    /**
     * Get searchExistance.<br/>
     * 
     * @return searchExistance
     */
    public boolean isSearchExistance() {
        return this.searchExistance;
    }

    /**
     * Get searchRefKey.<br/>
     * 
     * @return searchRefKey
     */
    public String getSearchRefKey() {
        return searchRefKey;
    }

    /**
     * Set searchExistance.<br/>
     * 
     * @param searchExistance
     *            searchExistance
     */
    public void setSearchExistance(boolean searchExistance) {
        this.searchExistance = searchExistance;
    }

    /**
     * Set searchProcess.<br/>
     * 
     * @param searchProcess
     *            searchProcess
     */
    public void setSearchProcess(String searchProcess) {
        this.searchProcess = searchProcess;
    }

    /**
     * Set portabilityFactor.
     * 
     * @param portabilityFactor
     *            portabilityFactor
     */
    public void setPortabilityFactor(String portabilityFactor) {
        this.portabilityFactor = portabilityFactor;
    }

    /**
     * Set protabilityDegree.<br/>
     * 
     * @param protabilityDegree
     *            protabilityDegree
     */
    public void setProtabilityDegree(String protabilityDegree) {
        this.protabilityDegree = protabilityDegree;
    }

    /**
     * Set degreeDetail.<br/>
     * 
     * @param degreeDetail
     *            degreeDetail
     */
    public void setDegreeDetail(String degreeDetail) {
        this.degreeDetail = degreeDetail;
    }

    /**
     * Set hearingConfirm.<br/>
     * 
     * @param hearingConfirm
     *            hearingConfirm
     */
    public void setHearingConfirm(String hearingConfirm) {
        this.hearingConfirm = hearingConfirm;
    }

    /**
     * Set visualConfirm.<br/>
     * 
     * @param visualConfirm
     *            visualConfirm
     */
    public void setVisualConfirm(String visualConfirm) {
        this.visualConfirm = visualConfirm;
    }

    /**
     * Set searchRefKey.<br/>
     * 
     * @param searchRefKey
     *            searchRefKey
     */
    public void setSearchRefKey(String searchRefKey) {
        this.searchRefKey = searchRefKey;
    }

    /**
     * Get the variable of transplant difficulty.<br/>
     * Get transplant difficulty for XML data from the view data.<br/>
     * 
     * @return Difficulty variable
     */
    public String getDegreeVariableViewToXml() {
        if (PortabilityDegreeEnum.High.getName().equals(protabilityDegree)) {
            return PortabilityDegreeVariable.High.value();
        } else if (PortabilityDegreeEnum.Middle.getName().equals(
                protabilityDegree)) {
            return PortabilityDegreeVariable.Middle.value();
        } else if (PortabilityDegreeEnum.Low.getName()
                .equals(protabilityDegree)) {
            return PortabilityDegreeVariable.Low.value();
        } else if (PortabilityDegreeEnum.Not_Portability.getName().equals(
                protabilityDegree)) {
            return PortabilityDegreeVariable.NOT_TRN.value();
        } else if (PortabilityDegreeEnum.Unknown.getName().equals(
                protabilityDegree)) {
            return PortabilityDegreeVariable.Unknown.value();
        } else {
            return protabilityDegree;
        }
    }

    /**
     * Get the variable of transplant difficulty.<br/>
     * Get the difficulty of porting data view data from XML.<br/>
     * 
     * @return Difficulty Emun
     */
    private String getDegreeVariableXmlToView(String protabilityDegree) {
        if (PortabilityDegreeVariable.High.value().equals(protabilityDegree)) {
            return PortabilityDegreeEnum.High.getName();
        } else if (PortabilityDegreeVariable.Middle.value().equals(
                protabilityDegree)) {
            return PortabilityDegreeEnum.Middle.getName();
        } else if (PortabilityDegreeVariable.Low.value().equals(
                protabilityDegree)) {
            return PortabilityDegreeEnum.Low.getName();
        } else if (PortabilityDegreeVariable.NOT_TRN.value().equals(
                protabilityDegree)) {
            return PortabilityDegreeEnum.Not_Portability.getName();
        } else if (PortabilityDegreeVariable.Unknown.value().equals(
                protabilityDegree)) {
            return PortabilityDegreeEnum.Unknown.getName();
        } else {
            return PortabilityDegreeEnum.Not_Search.getName();
        }
    }

}
