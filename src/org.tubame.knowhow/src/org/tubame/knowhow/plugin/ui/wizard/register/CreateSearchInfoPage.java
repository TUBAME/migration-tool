/*
 * CreateSearchInfoPage.java
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
package org.tubame.knowhow.plugin.ui.wizard.register;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.tubame.knowhow.biz.model.LineNumberEnum;
import org.tubame.knowhow.plugin.logic.FileManagement;
import org.tubame.knowhow.plugin.ui.wizard.listener.AppropriateLineListener;
import org.tubame.knowhow.plugin.ui.wizard.listener.SelectPythonModuleItemListener;
import org.tubame.knowhow.util.ControlUtil;
import org.tubame.knowhow.util.resource.ResourceUtil;

/**
 * Concrete class of search information registration wizard page.<br/>
 */
public class CreateSearchInfoPage extends AbstractCreateKnowhowPage {

    /** File type */
    private Text fileType;
    /** Search Keywords */
    private Text searchKeyword1;
    /** Search 2 */
    private Text searchKeyword2;
    /** Number of lines */
    private Spinner lineNumber;
    /** Line number basis */
    private Text lineNumberContents;
    /** Survey content */
    private Text investigation;
    /** Number of lines recorded */
    private Button appropriateLine;
    /** Search module selection */
    private Combo pythonModule;
    /** Search module selection */
    private Text pythonModuleContext;
    /** Recorded exclusion description */
    private Text appropriateContext;
    /** Unknown / TODO line button */
    private Combo unKnownLine;
    /** The length of the width of the text area */
    private static final int TEXT_WIDTH_HINT = 200;
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
    protected CreateSearchInfoPage(String pageName, String wizardName) {
        super(pageName, wizardName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        // super.createControl(composite);

        setRegisterSearchInfoArea(composite);

        super.setControl(composite);

    }

    /**
     * Create Search Information setting area.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void setRegisterSearchInfoArea(Composite composite) {
        Group searchInfoAreaGroup = new Group(composite, SWT.SHADOW_NONE);
        searchInfoAreaGroup
                .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // CheckStyle Magic number
        searchInfoAreaGroup.setLayout(new GridLayout(4, false));

        setFileTypeArea(searchInfoAreaGroup);
        setSearchKeyword1Area(searchInfoAreaGroup);
        setSearchKeyword2Area(searchInfoAreaGroup);
        setPythonModuleArea(searchInfoAreaGroup);
        setAppropriateExclusionFlag(searchInfoAreaGroup);
        setLineNumberArea(searchInfoAreaGroup);
        setLineNumberContentsArea(searchInfoAreaGroup);
    }

    /**
     * Set the number of lines Description area.<br/>
     * 
     * @param searchInfoAreaGroup
     *            Group
     */
    private void setLineNumberContentsArea(Group searchInfoAreaGroup) {

        Label lineNumberLabel = new Label(searchInfoAreaGroup, SWT.NULL);
        lineNumberLabel.setText(ResourceUtil.lineNumberContents);
        lineNumberLabel.setLayoutData(ControlUtil.getTopGridData());

        lineNumberContents = new Text(searchInfoAreaGroup, SWT.MULTI
                | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        lineNumberContents.setLayoutData(ControlUtil
                .getThreeColumnGridData(lineNumberContents));

        Label investigationLabel = new Label(searchInfoAreaGroup, SWT.NULL);
        investigationLabel.setText(ResourceUtil.investigationLabel);
        investigationLabel.setLayoutData(ControlUtil.getTopGridData());

        investigation = new Text(searchInfoAreaGroup, SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL | SWT.WRAP);
        investigation.setLayoutData(ControlUtil
                .getThreeColumnGridData(investigation));
    }

    /**
     * Set recorded exclusion flag area.<br/>
     * 
     * @param searchInfoAreaGroup
     *            Group
     */
    private void setAppropriateExclusionFlag(Group searchInfoAreaGroup) {

        Label appropriateLineLabel = new Label(searchInfoAreaGroup, SWT.NULL);
        appropriateLineLabel.setText(ResourceUtil.appropriateLineLabel);
        appropriateLineLabel.setLayoutData(ControlUtil.getTopGridData());
        appropriateLine = new Button(searchInfoAreaGroup, SWT.CHECK);
        appropriateLine.setLayoutData(ControlUtil.getTopGridData());
        appropriateLine.setSelection(true);

        // Recorded mandatory flag is activated when the True
        Label appropriateContents = new Label(searchInfoAreaGroup, SWT.NULL);
        appropriateContents.setText(ResourceUtil.appropriateContents);
        appropriateContents.setLayoutData(ControlUtil.getTopGridData());

        appropriateContext = new Text(searchInfoAreaGroup, SWT.MULTI
                | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
        appropriateContext.setEnabled(false);
        appropriateContext.setLayoutData(ControlUtil
                .getThreeColumnGridData(appropriateContext));
        appropriateLine.addSelectionListener(new AppropriateLineListener(
                appropriateLine, appropriateContext));
    }

    /**
     * Set the number of lines setting area.<br/>
     * 
     * @param searchInfoAreaGroup
     *            Group
     */
    private void setLineNumberArea(Group searchInfoAreaGroup) {

        Label toDolineLabel = new Label(searchInfoAreaGroup, SWT.NULL);
        toDolineLabel.setText(ResourceUtil.unKnownLineLabel);
        toDolineLabel.setLayoutData(ControlUtil.getTopGridData());

        unKnownLine = new Combo(searchInfoAreaGroup, SWT.READ_ONLY);
        unKnownLine.setLayoutData(ControlUtil.getTopGridData());
        ControlUtil.addComboBoxItem(LineNumberEnum.values(), unKnownLine);

        Label lineNumberLabel = new Label(searchInfoAreaGroup, SWT.NULL);
        lineNumberLabel.setText(ResourceUtil.lineNumber);
        lineNumberLabel.setLayoutData(ControlUtil.getTopGridData());

        lineNumber = new Spinner(searchInfoAreaGroup, SWT.BORDER);
        lineNumber.setMinimum(0);
        lineNumber.setSelection(0);
        lineNumber.setLayoutData(ControlUtil
                .getSizingHorizontalGridData(TEXT_WIDTH_HINT));

        unKnownLine.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!unKnownLine.getText().equals(
                        LineNumberEnum.Empty.getName())) {
                    lineNumber.setEnabled(false);
                } else {
                    lineNumber.setEnabled(true);
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
     * Set the search module settings area.<br/>
     * 
     * @param searchInfoAreaGroup
     *            Group
     */
    private void setPythonModuleArea(Group searchInfoAreaGroup) {

        Label pythonModuleLabel = new Label(searchInfoAreaGroup, SWT.NULL);
        pythonModuleLabel.setText(ResourceUtil.pythonModuleLabel);
        pythonModuleLabel.setLayoutData(ControlUtil.getTopGridData());

        pythonModule = new Combo(searchInfoAreaGroup, SWT.READ_ONLY);
        ControlUtil.addPythonModule(pythonModule,
                FileManagement.getSearchModuleMap());
        pythonModule.setLayoutData(ControlUtil
                .getSizingHorizontalTopGridData(COMBO_WIDTH_HINT));

        Label pythonModuleContextLabel = new Label(searchInfoAreaGroup,
                SWT.NULL);
        pythonModuleContextLabel.setText(ResourceUtil.pythonModuleContextLabel);
        pythonModuleContextLabel.setLayoutData(ControlUtil.getTopGridData());

        pythonModuleContext = new Text(searchInfoAreaGroup, SWT.MULTI
                | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
        GridData readOnlyContext = ControlUtil
                .getThreeColumnGridData(pythonModuleContext);
        // CheckStyle Magic number
        readOnlyContext.widthHint = 150;
        pythonModuleContext.setLayoutData(readOnlyContext);
        pythonModule.addSelectionListener(new SelectPythonModuleItemListener(
                pythonModule, pythonModuleContext));

    }

    /**
     * Set 2 setting area search keyword.<br/>
     * 
     * @param searchInfoAreaGroup
     *            Group
     */
    private void setSearchKeyword2Area(Group searchInfoAreaGroup) {
        Label searchKeyword2Label = new Label(searchInfoAreaGroup, SWT.NULL);
        searchKeyword2Label.setText(ResourceUtil.searchKeyword2Label);
        searchKeyword2Label.setLayoutData(ControlUtil.getTopGridData());

        searchKeyword2 = new Text(searchInfoAreaGroup, SWT.BORDER);
        // CheckStyle Magic number
        searchKeyword2.setLayoutData(ControlUtil.getThreeGridWidthData(300));
    }

    /**
     * Set one set area search keyword.<br/>
     * 
     * @param searchInfoAreaGroup
     *            Group
     */
    private void setSearchKeyword1Area(Group searchInfoAreaGroup) {

        Label searchKeyword1Label = new Label(searchInfoAreaGroup, SWT.NULL);
        searchKeyword1Label.setText(ResourceUtil.searchKeyword1Label);
        searchKeyword1Label.setLayoutData(ControlUtil.getTopGridData());

        searchKeyword1 = new Text(searchInfoAreaGroup, SWT.BORDER);
        // CheckStyle Magic number
        searchKeyword1.setLayoutData(ControlUtil.getThreeGridWidthData(300));
    }

    /**
     * Set the file type setting area.<br/>
     * 
     * @param searchInfoAreaGroup
     *            Group
     */
    private void setFileTypeArea(Group searchInfoAreaGroup) {

        Label fileTypeLabel = new Label(searchInfoAreaGroup, SWT.NULL);
        fileTypeLabel.setText(ResourceUtil.fileTypeLabel);
        fileTypeLabel.setLayoutData(ControlUtil.getTopGridData());

        fileType = new Text(searchInfoAreaGroup, SWT.BORDER);
        // CheckStyle Magic number
        fileType.setLayoutData(ControlUtil.getThreeGridWidthData(300));

    }

    /**
     * Get the search string file.<br/>
     * 
     * @return Search for files
     */
    public String getFileType() {
        return fileType.getText();
    }

    /**
     * Get Search Keywords string.<br/>
     * 
     * @return Search Keywords 1
     */
    public String getSearchKeyword1() {
        return searchKeyword1.getText();
    }

    /**
     * Get Search 2 string.<br/>
     * 
     * @return Search keyword 2
     */
    public String getSearchKeyword2() {
        return searchKeyword2.getText();
    }

    /**
     * Get the line number string.<br/>
     * 
     * @return Number of lines
     */
    public String getLineNumber() {
        return lineNumber.getText();
    }

    /**
     * Get the number of lines basis string.<br/>
     * 
     * @return Line number basis
     */
    public String getLineNumberContents() {
        return lineNumberContents.getText();
    }

    /**
     * Get the survey content string.<br/>
     * 
     * @return Survey content
     */
    public String getInvestigation() {
        return investigation.getText();
    }

    /**
     * Get the number of lines recorded string.<br/>
     * 
     * @return Number of lines recorded
     */
    public boolean isAppropriateLine() {
        return appropriateLine.getSelection();
    }

    /**
     * Get the Search module string.<br/>
     * 
     * @return Search module
     */
    public String getPythonModule() {
        return pythonModule.getText();
    }

    /**
     * Get more search module string.<br/>
     * 
     * @return Search module detail
     */
    public String getPythonModuleContext() {
        return pythonModuleContext.getText();
    }

    /**
     * Get the number of lines recorded reason string.<br/>
     * 
     * @return Number of lines recorded reason
     */
    public String getAppropriateContext() {
        return appropriateContext.getText();
    }

    /**
     * Get unknown / TODO line string.<br/>
     * 
     * @return Unknown / TODO line
     */
    public String getUnKnownLine() {
        return unKnownLine.getText();
    }
}
