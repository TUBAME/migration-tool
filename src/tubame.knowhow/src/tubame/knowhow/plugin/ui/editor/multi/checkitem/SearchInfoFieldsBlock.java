/*
 * SearchInfoFieldsBlock.java
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
package tubame.knowhow.plugin.ui.editor.multi.checkitem;

import tubame.common.util.CmnStringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import tubame.knowhow.biz.model.LineNumberEnum;

import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.model.view.SearchInfoViewType;
import tubame.knowhow.plugin.ui.editor.multi.listener.DirtyListener;
import tubame.knowhow.plugin.ui.wizard.listener.AppropriateLineListener;
import tubame.knowhow.plugin.ui.wizard.listener.SelectPythonModuleItemListener;
import tubame.knowhow.util.ControlUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Search information block defined class know-how XML editor (check items tab).<br/>
 */
public class SearchInfoFieldsBlock extends AbstractFieldsBlock {

    /** Survey content */
    private Text investigationText;
    /** Line number description */
    private Text lineNumberContentsText;
    /** Number of lines */
    private Spinner lineNumberSpinner;
    /** Number of lines recorded description */
    private Text appropriateContext;
    /** Number of lines recorded */
    private Button appropriateLine;
    /** Search module details. */
    private Text pythonModuleContext;
    /** Search module */
    private Combo pythonModule;
    /** Search Keyword 2 */
    private Text searchKeyword2;
    /** Search Keyword 1 */
    private Text searchKeyword1;
    /** Search for files */
    private Text fileTypeText;
    /** Search information group */
    private Group searchInfoGroup;
    /** Search information item ID */
    private String searchItemId;
    /** Unknown / TODO line button */
    private Combo unKnownLine;

    /**
     * Constructor.<br/>
     * 
     * @param checkItemFieldsPage
     *            Check items page information
     */
    public SearchInfoFieldsBlock(CheckItemFieldsPage checkItemFieldsPage) {
        super(checkItemFieldsPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void makeFormPart(Composite composite) {

        searchInfoGroup = ControlUtil.createGroup(composite, SWT.NONE
                | SWT.SCROLL_LINE, CmnStringUtil.EMPTY);
        searchInfoGroup.setLayout(new GridLayout(2, false));
        searchInfoGroup.setLayoutData(ControlUtil
                .getAutoSizingHorizontalGridData());

        makeCheckItemArea(searchInfoGroup);
    }

    /**
     * Create each area of search information section.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeCheckItemArea(Group checkItemGroup) {
        makeFileTypeArea(checkItemGroup);
        makeSearchKeyword1Area(checkItemGroup);
        makeSearchKeyword2Area(checkItemGroup);
        makePythonModuleArea(checkItemGroup);
        makePythonModuleDescriptionArea(checkItemGroup);
        makeAppropriateExclusionFlag(checkItemGroup);
        makeAppropriateDescriptionArea(checkItemGroup);
        makeUnknownLineArea(checkItemGroup);
        makeLineNumberArea(checkItemGroup);
        makeLineNumberContentsArea(checkItemGroup);
        makeLineNumberInvestigationArea(checkItemGroup);

    }

    /**
     * Create an area of study contents of the line number.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeLineNumberInvestigationArea(Group checkItemGroup) {

        Label investigationLabel = new Label(checkItemGroup, SWT.NULL);
        investigationLabel.setText(ResourceUtil.investigationLabel);
        investigationLabel.setLayoutData(ControlUtil.getTopGridData());

        investigationText = new Text(checkItemGroup, SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL | SWT.WRAP);
        investigationText.setLayoutData(ControlUtil
                .getThreeColumnGridData(investigationText));
        investigationText.addKeyListener(new DirtyListener(super.getEditor()));

    }

    /**
     * Create an area of line number basis.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeLineNumberContentsArea(Group checkItemGroup) {

        Label lineNumberContents = new Label(checkItemGroup, SWT.NULL);
        lineNumberContents.setText(ResourceUtil.lineNumberContents);
        lineNumberContents.setLayoutData(ControlUtil.getTopGridData());

        lineNumberContentsText = new Text(checkItemGroup, SWT.MULTI
                | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        lineNumberContentsText.setLayoutData(ControlUtil
                .getThreeColumnGridData(lineNumberContentsText));
        lineNumberContentsText.addKeyListener(new DirtyListener(super
                .getEditor()));
    }

    /**
     * Create an area the number of lines.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeLineNumberArea(Group checkItemGroup) {

        Label lineNumber = new Label(checkItemGroup, SWT.NULL);
        lineNumber.setText(ResourceUtil.lineNumber);
        lineNumber.setLayoutData(ControlUtil.getTopGridData());

        lineNumberSpinner = new Spinner(checkItemGroup, SWT.BORDER);
        lineNumberSpinner.setMinimum(0);
        lineNumberSpinner.setLayoutData(ControlUtil.getTopGridData());
        lineNumberSpinner.addSelectionListener(new DirtyListener(super
                .getEditor()));
        lineNumberSpinner.addKeyListener(new DirtyListener(super.getEditor()));
        unKnownLine.addSelectionListener(new SelectionListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!unKnownLine.getText().equals(CmnStringUtil.EMPTY)) {
                    lineNumberSpinner.setEnabled(false);
                } else {
                    lineNumberSpinner.setEnabled(true);
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // no operation
            }
        });
    }

    /**
     * Create a line number unknown / TODO area.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeUnknownLineArea(Group checkItemGroup) {
        Label unKnownLineLabel = new Label(checkItemGroup, SWT.NULL);
        unKnownLineLabel.setText(ResourceUtil.unKnownLineLabel);
        unKnownLineLabel.setLayoutData(ControlUtil.getTopGridData());

        unKnownLine = new Combo(checkItemGroup, SWT.READ_ONLY);
        ControlUtil.addComboBoxItem(LineNumberEnum.values(), unKnownLine);
        unKnownLine.setLayoutData(ControlUtil.getTopGridData());
        unKnownLine.addSelectionListener(new DirtyListener(super.getEditor()));

    }

    /**
     * Create an area the number of lines recorded description.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeAppropriateDescriptionArea(Group checkItemGroup) {
        Label appropriateContents = new Label(checkItemGroup, SWT.NULL);
        appropriateContents.setText(ResourceUtil.appropriateContents);
        appropriateContents.setLayoutData(ControlUtil.getTopGridData());

        appropriateContext = new Text(checkItemGroup, SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL | SWT.WRAP);
        appropriateContext.setEnabled(false);
        appropriateContext.setLayoutData(ControlUtil
                .getThreeColumnGridData(appropriateContext));
        appropriateContext.addKeyListener(new DirtyListener(super.getEditor()));
        appropriateLine.addSelectionListener(new AppropriateLineListener(
                appropriateLine, appropriateContext));

    }

    /**
     * Create an area the number of lines recorded.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeAppropriateExclusionFlag(Group checkItemGroup) {
        Label appropriateLineLabel = new Label(checkItemGroup, SWT.NULL);
        appropriateLineLabel.setText(ResourceUtil.appropriateLineLabel);
        appropriateLineLabel.setLayoutData(ControlUtil.getTopGridData());

        appropriateLine = new Button(checkItemGroup, SWT.CHECK);
        appropriateLine.setLayoutData(ControlUtil.getTopGridData());
        appropriateLine.addSelectionListener(new DirtyListener(super
                .getEditor()));
    }

    /**
     * Create an area of search module overview.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makePythonModuleDescriptionArea(Group checkItemGroup) {
        Label pythonModuleContextLabel = new Label(checkItemGroup, SWT.NULL);
        pythonModuleContextLabel.setText(ResourceUtil.pythonModuleContextLabel);
        pythonModuleContextLabel.setLayoutData(ControlUtil.getTopGridData());

        pythonModuleContext = new Text(checkItemGroup, SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);

        pythonModuleContext.setLayoutData(ControlUtil
                .getThreeColumnGridData(pythonModuleContext));
        pythonModuleContext
                .addKeyListener(new DirtyListener(super.getEditor()));
        pythonModule.addSelectionListener(new SelectPythonModuleItemListener(
                pythonModule, pythonModuleContext));
    }

    /**
     * Create an area of search module.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makePythonModuleArea(Group checkItemGroup) {
        Label pythonModuleLabel = new Label(checkItemGroup, SWT.NULL);
        pythonModuleLabel.setText(ResourceUtil.pythonModuleLabel);
        pythonModuleLabel.setLayoutData(ControlUtil.getTopGridData());

        pythonModule = new Combo(checkItemGroup, SWT.READ_ONLY);
        ControlUtil.addPythonModule(pythonModule,
                FileManagement.getSearchModuleMap());
        // CheckStyle Magic number
        pythonModule
                .setLayoutData(ControlUtil.getSizingHorizontalGridData(150));
        pythonModule.addSelectionListener(new DirtyListener(super.getEditor()));
    }

    /**
     * Create an area of search keyword 2.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeSearchKeyword2Area(Group checkItemGroup) {

        Label searchKeyword2Label = new Label(checkItemGroup, SWT.NULL);
        searchKeyword2Label.setText(ResourceUtil.searchKeyword2Label);
        searchKeyword2Label.setLayoutData(ControlUtil.getTopGridData());

        searchKeyword2 = new Text(checkItemGroup, SWT.BORDER);
        // CheckStyle Magic number
        searchKeyword2.setLayoutData(ControlUtil
                .getSizingHorizontalGridData(150));
        searchKeyword2.addKeyListener(new DirtyListener(super.getEditor()));

    }

    /**
     * Create an area of the search keyword 1.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeSearchKeyword1Area(Group checkItemGroup) {

        Label searchKeyword1Label = new Label(checkItemGroup, SWT.NULL);
        searchKeyword1Label.setText(ResourceUtil.searchKeyword1Label);
        searchKeyword1Label.setLayoutData(ControlUtil.getTopGridData());

        searchKeyword1 = new Text(checkItemGroup, SWT.BORDER);
        // CheckStyle Magic number
        searchKeyword1.setLayoutData(ControlUtil
                .getSizingHorizontalGridData(150));
        searchKeyword1.addKeyListener(new DirtyListener(super.getEditor()));
    }

    /**
     * Create an area to search for files.<br/>
     * 
     * @param checkItemGroup
     *            Group
     */
    private void makeFileTypeArea(Group checkItemGroup) {

        Label fileTypeLabel = new Label(checkItemGroup, SWT.NULL);
        fileTypeLabel.setText(ResourceUtil.fileTypeLabel);
        fileTypeLabel.setLayoutData(ControlUtil.getTopGridData());

        fileTypeText = new Text(checkItemGroup, SWT.BORDER);
        // CheckStyle Magic number
        fileTypeText
                .setLayoutData(ControlUtil.getSizingHorizontalGridData(150));
        fileTypeText.addKeyListener(new DirtyListener(super.getEditor()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRegisterItemData(AbstractViewType abstractSampleViewData) {
        SearchInfoViewType searchInfoViewType = (SearchInfoViewType) abstractSampleViewData;
        this.searchItemId = searchInfoViewType.getRegisterKey();
        this.investigationText.setText(searchInfoViewType.getInvestigation());
        this.lineNumberContentsText.setText(searchInfoViewType
                .getLineNumberContents());
        this.unKnownLine.setText(searchInfoViewType.getUnKnownLine());
        this.lineNumberSpinner.setSelection(Integer.parseInt(searchInfoViewType
                .getLineNumber()));
        this.appropriateContext.setText(searchInfoViewType
                .getAppropriateContext());
        this.appropriateLine.setSelection(searchInfoViewType
                .isAppropriateLine());
        this.pythonModuleContext.setText(searchInfoViewType
                .getPythonModuleContext());
        this.pythonModule.setText(searchInfoViewType.getPythonModule());
        this.searchKeyword2.setText(searchInfoViewType.getSearchKeyword2());
        this.searchKeyword1.setText(searchInfoViewType.getSearchKeyword1());
        this.fileTypeText.setText(searchInfoViewType.getFileType());
        initEnableItem();

    }

    /**
     * Make the initial setting of the Enable item.<br/>
     * Make the initial Enable setting the number of lines input fields / line
     * number recorded place.<br/>
     * 
     */
    private void initEnableItem() {
        if (appropriateLine.getSelection()) {
            appropriateContext.setEnabled(false);
        }
        if (!unKnownLine.getText().equals(CmnStringUtil.EMPTY)) {
            lineNumberSpinner.setEnabled(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnableGroup(boolean enableFlag) {
        this.searchInfoGroup.setEnabled(enableFlag);
        for (Control control : searchInfoGroup.getChildren()) {
            control.setEnabled(enableFlag);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        this.searchItemId = CmnStringUtil.BLANK;
        this.investigationText.setText(CmnStringUtil.EMPTY);
        this.lineNumberContentsText.setText(CmnStringUtil.EMPTY);
        this.lineNumberSpinner.setSelection(0);
        this.appropriateContext.setText(CmnStringUtil.EMPTY);
        this.appropriateLine.setSelection(true);
        this.unKnownLine.setText(CmnStringUtil.EMPTY);
        this.pythonModuleContext.setText(CmnStringUtil.EMPTY);
        this.pythonModule.setText(CmnStringUtil.EMPTY);
        this.searchKeyword2.setText(CmnStringUtil.EMPTY);
        this.searchKeyword1.setText(CmnStringUtil.EMPTY);
        this.fileTypeText.setText(CmnStringUtil.EMPTY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClear() {
        if (this.searchInfoGroup != null) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateData(
            PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation) {
        SearchInfoViewType searchInfoViewType = (SearchInfoViewType) portabilityKnowhowListViewOperation
                .getKnowhowViewType();
        if (this.searchItemId.equals(searchInfoViewType.getRegisterKey())) {
            searchInfoViewType.setFileType(fileTypeText.getText());
            searchInfoViewType.setSearchKeyword1(searchKeyword1.getText());
            searchInfoViewType.setSearchKeyword2(searchKeyword2.getText());
            searchInfoViewType.setPythonModule(pythonModule.getText());
            searchInfoViewType.setPythonModuleContext(pythonModuleContext
                    .getText());
            searchInfoViewType.setUnKnownLine(unKnownLine.getText());
            searchInfoViewType.setLineNumber(lineNumberSpinner.getText());
            searchInfoViewType.setLineNumberContents(lineNumberContentsText
                    .getText());
            searchInfoViewType.setAppropriateLine(appropriateLine
                    .getSelection());
            searchInfoViewType.setAppropriateContext(appropriateContext
                    .getText());
            searchInfoViewType.setInvestigation(investigationText.getText());
            portabilityKnowhowListViewOperation
                    .setKnowhowViewType(searchInfoViewType);
        }
    }
}
