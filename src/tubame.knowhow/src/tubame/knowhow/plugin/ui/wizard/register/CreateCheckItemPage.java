/*
 * CreateCheckItemPage.java
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
package tubame.knowhow.plugin.ui.wizard.register;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import tubame.knowhow.biz.model.DegreeDetailEnum;
import tubame.knowhow.biz.model.PortabilityDegreeEnum;
import tubame.knowhow.biz.model.PortabilityFactorEnum;

import tubame.knowhow.util.ControlUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Class defined wizards check item information registration.<br/>
 */
public class CreateCheckItemPage extends AbstractCreateKnowhowPage {

    /** Search procedure */
    private Text searchProcess;
    /** Search implementation */
    private Button searchExistance;
    /** Confirmation hearing */
    private Text hearingConfirm;
    /** Visual confirmation */
    private Text visualConfirm;
    /** Porting factor */
    private Combo portabilityFactor;
    /** Degree of difficulty */
    private Combo protabilityDegree;
    /** Difficulty details */
    private Combo degreeDetail;
    /** The length of the width of the combo box area */
    private static final int COMBO_WIDTH_HINT = 150;

    /**
     * Constructor.<br/>
     * 
     * @param pageName
     *            Page information
     * @param wizardName
     *            Wizard information
     */
    protected CreateCheckItemPage(String pageName, String wizardName) {
        super(pageName, wizardName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        super.createControl(composite);

        setRegistercheckItemArea(composite);
        super.setLabelText(ResourceUtil.checkItemInfoLabel);
        super.setControl(composite);

    }

    /**
     * Create a check item information setting area.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void setRegistercheckItemArea(Composite composite) {

        Group checkItemAreaGroup = new Group(composite, SWT.SHADOW_NONE);
        checkItemAreaGroup
                .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // CheckStyle Magic number
        checkItemAreaGroup.setLayout(new GridLayout(4, false));

        // Search procedure
        setSearchProcessArea(checkItemAreaGroup);
        // Porting factor
        setProtabilityFactor(checkItemAreaGroup);
        // Degree of difficulty
        setPortabilityDegree(checkItemAreaGroup);
        // Difficulty details
        setDegreeDetail(checkItemAreaGroup);
        // Visual confirmation
        setVisualConfirmArea(checkItemAreaGroup);
        // Confirmation hearing
        setHearingConfirmArea(checkItemAreaGroup);

    }

    /**
     * Set the confirmation hearing area.<br/>
     * 
     * @param checkItemAreaGroup
     *            Group
     */
    private void setHearingConfirmArea(Group checkItemAreaGroup) {

        Label hearingConfirmLabel = new Label(checkItemAreaGroup, SWT.NULL);
        hearingConfirmLabel.setText(ResourceUtil.hearingConfirm);
        hearingConfirmLabel.setLayoutData(ControlUtil.getTopGridData());

        hearingConfirm = new Text(checkItemAreaGroup, SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL | SWT.WRAP);
        hearingConfirm.setLayoutData(ControlUtil
                .getThreeColumnGridData(hearingConfirm));

    }

    /**
     * Set the visual confirmation area.<br/>
     * 
     * @param checkItemAreaGroup
     *            Group
     */
    private void setVisualConfirmArea(Group checkItemAreaGroup) {

        Label visualConfirmLabel = new Label(checkItemAreaGroup, SWT.NULL);
        visualConfirmLabel.setText(ResourceUtil.visualConfirm);
        visualConfirmLabel.setLayoutData(ControlUtil.getTopGridData());

        this.visualConfirm = new Text(checkItemAreaGroup, SWT.MULTI
                | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);

        this.visualConfirm.setLayoutData(ControlUtil
                .getThreeColumnGridData(visualConfirm));

    }

    /**
     * Set the difficulty detail area.<br/>
     * 
     * @param checkItemAreaGroup
     *            Group
     */
    private void setDegreeDetail(Group checkItemAreaGroup) {

        Label degreeDetailLabel = new Label(checkItemAreaGroup, SWT.NULL);
        degreeDetailLabel.setText(ResourceUtil.degreeDetail);
        degreeDetailLabel.setLayoutData(ControlUtil.getTopGridData());
        this.degreeDetail = new Combo(checkItemAreaGroup, SWT.READ_ONLY);
        ControlUtil.addComboBoxItem(DegreeDetailEnum.values(), degreeDetail);
        degreeDetail.setText(DegreeDetailEnum.High.getName());
        this.degreeDetail.setLayoutData(ControlUtil
                .getThreeGridWidthData(COMBO_WIDTH_HINT));
    }

    /**
     * Set the difficulty area.<br/>
     * 
     * @param checkItemAreaGroup
     *            Group
     */
    private void setPortabilityDegree(Group checkItemAreaGroup) {
        Label portabilityDegreeLabel = new Label(checkItemAreaGroup, SWT.NULL);
        portabilityDegreeLabel.setText(ResourceUtil.portabilityDegree);
        portabilityDegreeLabel.setLayoutData(ControlUtil.getTopGridData());
        this.protabilityDegree = new Combo(checkItemAreaGroup, SWT.READ_ONLY);
        ControlUtil.addComboBoxItem(PortabilityDegreeEnum.values(),
                protabilityDegree);
        protabilityDegree.setText(PortabilityDegreeEnum.High.getName());
        this.protabilityDegree.setLayoutData(ControlUtil
                .getThreeGridWidthData(COMBO_WIDTH_HINT));

    }

    /**
     * Set the transplant factor area.<br/>
     * 
     * @param checkItemAreaGroup
     *            Group
     */
    private void setProtabilityFactor(Group checkItemAreaGroup) {

        Label protabilityFactorLabel = new Label(checkItemAreaGroup, SWT.NULL);
        protabilityFactorLabel.setText(ResourceUtil.protabilityFactor);
        protabilityFactorLabel.setLayoutData(ControlUtil.getTopGridData());
        this.portabilityFactor = new Combo(checkItemAreaGroup, SWT.READ_ONLY);
        ControlUtil.addComboBoxItem(PortabilityFactorEnum.values(),
                portabilityFactor);
        portabilityFactor.setText(PortabilityFactorEnum.Weblogic_Specific
                .getName());
        this.portabilityFactor.setLayoutData(ControlUtil
                .getThreeGridWidthData(COMBO_WIDTH_HINT));
    }

    /**
     * Set the search procedure area.<br/>
     * 
     * @param checkItemAreaGroup
     *            Group
     */
    private void setSearchProcessArea(Group checkItemAreaGroup) {

        Label searchProcessLabel = new Label(checkItemAreaGroup, SWT.NULL);
        searchProcessLabel.setText(ResourceUtil.searchProcess);
        searchProcessLabel.setLayoutData(ControlUtil.getTopGridData());

        searchProcess = new Text(checkItemAreaGroup, SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL | SWT.WRAP);
        searchProcess.setLayoutData(ControlUtil
                .getThreeColumnGridData(searchProcess));

        Label searchExistanceLabel = new Label(checkItemAreaGroup, SWT.NULL);
        searchExistanceLabel.setText(ResourceUtil.searchExistance);
        searchExistanceLabel.setLayoutData(ControlUtil.getTopGridData());
        searchExistance = new Button(checkItemAreaGroup, SWT.CHECK);
        searchExistance.setLayoutData(ControlUtil.getTopGridData());
        searchExistance.setSelection(true);

    }

    /**
     * Get search implementation. The return true if you want to search
     * implementation.<br/>
     * 
     * @return true:Search implementation false:Do not search conducted
     */
    public boolean isSearchExistance() {
        return searchExistance.getSelection();
    }

    /**
     * Get the search procedure string.<br/>
     * 
     * @return Search procedure
     */
    public String getSearchProcess() {
        return searchProcess.getText();
    }

    /**
     * Get the confirmation hearing string.<br/>
     * 
     * @return Confirmation hearing
     */
    public String getHearingConfirm() {
        return hearingConfirm.getText();
    }

    /**
     * Get a visual confirmation string.<br/>
     * 
     * @return Visual confirmation
     */
    public String getVisualConfirm() {
        return visualConfirm.getText();
    }

    /**
     * Get the transplant factor string.<br/>
     * 
     * @return Porting factor
     */
    public String getPortabilityFactor() {
        return portabilityFactor.getText();
    }

    /**
     * Get the difficulty string.<br/>
     * 
     * @return Degree of difficulty
     */
    public String getProtabilityDegree() {
        return protabilityDegree.getText();
    }

    /**
     * Get the difficulty detail string.<br/>
     * 
     * @return Difficulty details
     */
    public String getDegreeDetail() {
        return degreeDetail.getText();
    }
}
