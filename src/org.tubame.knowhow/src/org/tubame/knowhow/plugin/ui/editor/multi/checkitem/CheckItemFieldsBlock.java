/*
 * CheckItemFieldsBlock.java
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
package org.tubame.knowhow.plugin.ui.editor.multi.checkitem;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.tubame.knowhow.biz.model.DegreeDetailEnum;
import org.tubame.knowhow.biz.model.PortabilityDegreeEnum;
import org.tubame.knowhow.biz.model.PortabilityFactorEnum;
import org.tubame.knowhow.plugin.model.view.AbstractViewType;
import org.tubame.knowhow.plugin.model.view.CheckItemViewType;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.plugin.ui.editor.multi.listener.DirtyListener;
import org.tubame.knowhow.util.ControlUtil;
import org.tubame.knowhow.util.resource.ResourceUtil;

/**
 * Check items block defined class know-how XML editor (check items tab).<br/>
 */
public class CheckItemFieldsBlock extends AbstractFieldsBlock {

    /** Hearing confirmation item. */
    private Text hearingConfirmText;
    /** Visual confirmation item */
    private Text visualConfirmText;
    /** More difficulty */
    private Combo degreeDetailCombo;
    /** Degree of difficulty. */
    private Combo portabilityDegreeCombo;
    /** Transplant factors */
    private Combo portabilityFactorCombo;
    /** Search implementation */
    private Button searchExistanceButton;
    /** Search procedure */
    private Text searchProcessText;
    /** Check item ID */
    private String checkItemID;
    /** Check item group */
    private Group checkItemGroup;

    /**
     * Constructor.<br/>
     * 
     * @param checkItemFieldsPage
     *            Check items page information
     */
    public CheckItemFieldsBlock(CheckItemFieldsPage checkItemFieldsPage) {
        super(checkItemFieldsPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void makeFormPart(Composite composite) {

        checkItemGroup = ControlUtil.createGroup(composite, SWT.NONE
                | SWT.SCROLL_LINE, CmnStringUtil.EMPTY);
        checkItemGroup.setLayout(new GridLayout(2, false));
        checkItemGroup.setLayoutData(ControlUtil
                .getAutoSizingHorizontalGridData());

        makeCheckItemArea(checkItemGroup);
    }

    /**
     * Create each area of check items section.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeCheckItemArea(Group checkItemGroup) {
        // Search procedure area
        makeSearchProgressArea(checkItemGroup);
        // Search implementation area
        makeSearchExistanceArea(checkItemGroup);
        // Porting factor area
        makeProtabilityFactorArea(checkItemGroup);
        // Porting difficulty area
        makePortabilityDegreeArea(checkItemGroup);
        // Details area transplant difficulty
        makeDegreeDetailArea(checkItemGroup);
        // Visual inspection area
        makeVisualConfirmArea(checkItemGroup);
        // Confirmation hearing area
        makeHearingConfirmArea(checkItemGroup);

    }

    /**
     * Create an area of hearing confirmation.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeHearingConfirmArea(Group checkItemGroup) {

        Label hearingConfirm = new Label(checkItemGroup, SWT.NULL);
        hearingConfirm.setText(ResourceUtil.hearingConfirm);
        hearingConfirm.setLayoutData(ControlUtil.getTopGridData());

        hearingConfirmText = new Text(checkItemGroup, SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL | SWT.WRAP);
        hearingConfirmText.setLayoutData(ControlUtil
                .getThreeColumnGridData(hearingConfirmText));
        hearingConfirmText.addKeyListener(new DirtyListener(super.getEditor()));

    }

    /**
     * Create an area of visual confirmation.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeVisualConfirmArea(Group checkItemGroup) {

        Label visualConfirm = new Label(checkItemGroup, SWT.NULL);
        visualConfirm.setText(ResourceUtil.visualConfirm);
        visualConfirm.setLayoutData(ControlUtil.getTopGridData());

        visualConfirmText = new Text(checkItemGroup, SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL | SWT.WRAP);
        visualConfirmText.setLayoutData(ControlUtil
                .getThreeColumnGridData(visualConfirmText));
        visualConfirmText.addKeyListener(new DirtyListener(super.getEditor()));

    }

    /**
     * Create an area of more transplant difficulty.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeDegreeDetailArea(Group checkItemGroup) {
        Label degreeDetail = new Label(checkItemGroup, SWT.NULL);
        degreeDetail.setText(ResourceUtil.degreeDetail);
        degreeDetail.setLayoutData(ControlUtil.getTopGridData());

        degreeDetailCombo = new Combo(checkItemGroup, SWT.READ_ONLY);
        ControlUtil.addComboBoxItem(DegreeDetailEnum.values(),
                degreeDetailCombo);
        degreeDetailCombo.setLayoutData(ControlUtil.getTopGridData());
        degreeDetailCombo.addSelectionListener(new DirtyListener(super
                .getEditor()));

    }

    /**
     * Create an area of transplant difficulty.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makePortabilityDegreeArea(Group checkItemGroup) {
        Label portabilityDegree = new Label(checkItemGroup, SWT.NULL);
        portabilityDegree.setText(ResourceUtil.portabilityDegree);
        portabilityDegree.setLayoutData(ControlUtil.getTopGridData());

        portabilityDegreeCombo = new Combo(checkItemGroup, SWT.READ_ONLY);
        ControlUtil.addComboBoxItem(PortabilityDegreeEnum.values(),
                portabilityDegreeCombo);
        portabilityDegreeCombo.setLayoutData(ControlUtil.getTopGridData());
        portabilityDegreeCombo.addSelectionListener(new DirtyListener(super
                .getEditor()));
    }

    /**
     * Create an area of transplant factors.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeProtabilityFactorArea(Group checkItemGroup) {

        Label protabilityFactor = new Label(checkItemGroup, SWT.NULL);
        protabilityFactor.setText(ResourceUtil.protabilityFactor);
        protabilityFactor.setLayoutData(ControlUtil.getTopGridData());

        portabilityFactorCombo = new Combo(checkItemGroup, SWT.READ_ONLY);
        ControlUtil.addComboBoxItem(PortabilityFactorEnum.values(),
                portabilityFactorCombo);
        // CheckStyle magic number
        portabilityFactorCombo.setLayoutData(ControlUtil
                .getSizingHorizontalGridData(150));
        portabilityFactorCombo.addSelectionListener(new DirtyListener(super
                .getEditor()));
    }

    /**
     * Create a search area of implementation.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeSearchExistanceArea(Group checkItemGroup) {

        Label searchExistance = new Label(checkItemGroup, SWT.NULL);
        searchExistance.setText(ResourceUtil.searchExistance);
        searchExistance.setLayoutData(ControlUtil.getTopGridData());

        searchExistanceButton = new Button(checkItemGroup, SWT.CHECK);
        searchExistanceButton.setLayoutData(ControlUtil.getTopGridData());
        searchExistanceButton.addSelectionListener(new DirtyListener(super
                .getEditor()));
    }

    /**
     * Create a search procedure area.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeSearchProgressArea(Group checkItemGroup) {

        Label searchProcess = new Label(checkItemGroup, SWT.NULL);
        searchProcess.setText(ResourceUtil.searchProcess);
        searchProcess.setLayoutData(ControlUtil.getTopGridData());

        searchProcessText = new Text(checkItemGroup, SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL | SWT.WRAP);
        searchProcessText.setLayoutData(ControlUtil
                .getThreeColumnGridData(searchProcessText));
        searchProcessText.addKeyListener(new DirtyListener(super.getEditor()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRegisterItemData(AbstractViewType abstractSampleViewData) {
        CheckItemViewType sampleCheckItemType = (CheckItemViewType) abstractSampleViewData;
        this.checkItemID = sampleCheckItemType.getRegisterKey();
        this.searchProcessText.setText(sampleCheckItemType.getSearchProcess());
        this.searchExistanceButton.setSelection(sampleCheckItemType
                .isSearchExistance());
        this.portabilityFactorCombo.setText(sampleCheckItemType
                .getPortabilityFactor());
        this.portabilityDegreeCombo.setText(sampleCheckItemType
                .getProtabilityDegree());
        this.degreeDetailCombo.setText(sampleCheckItemType.getDegreeDetail());
        this.visualConfirmText.setText(sampleCheckItemType.getVisualConfirm());
        this.hearingConfirmText
                .setText(sampleCheckItemType.getHearingConfirm());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnableGroup(boolean enableFlag) {
        this.checkItemGroup.setEnabled(enableFlag);
        for (Control control : checkItemGroup.getChildren()) {
            control.setEnabled(enableFlag);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        this.checkItemID = CmnStringUtil.BLANK;
        this.searchProcessText.setText(CmnStringUtil.EMPTY);
        this.searchExistanceButton.setSelection(false);
        this.portabilityFactorCombo.setText(CmnStringUtil.EMPTY);
        this.portabilityDegreeCombo.setText(CmnStringUtil.EMPTY);
        this.degreeDetailCombo.setText(CmnStringUtil.EMPTY);
        this.visualConfirmText.setText(CmnStringUtil.EMPTY);
        this.hearingConfirmText.setText(CmnStringUtil.EMPTY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClear() {
        if (this.checkItemGroup != null) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateData(
            PortabilityKnowhowListViewOperation tempKnowhowListViewData) {

        CheckItemViewType checkItemViewType = (CheckItemViewType) tempKnowhowListViewData
                .getKnowhowViewType();
        if (this.checkItemID.equals(checkItemViewType.getRegisterKey())) {
            checkItemViewType.setSearchProcess(searchProcessText.getText());
            checkItemViewType.setSearchExistance(searchExistanceButton
                    .getSelection());
            checkItemViewType.setPortabilityFactor(portabilityFactorCombo
                    .getText());
            checkItemViewType.setProtabilityDegree(portabilityDegreeCombo
                    .getText());
            checkItemViewType.setDegreeDetail(degreeDetailCombo.getText());
            checkItemViewType.setVisualConfirm(visualConfirmText.getText());
            checkItemViewType.setHearingConfirm(hearingConfirmText.getText());
            tempKnowhowListViewData.setKnowhowViewType(checkItemViewType);
        }
    }

}
