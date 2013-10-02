/*
 * AbstractCreateKnowhowPage.java
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

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tubame.knowhow.util.ControlUtil;

/**
 * Abstract class of registration Wizard.<br/>
 * Class that defines the text area to register the name of know-how or category
 * name.<br/>
 */
public abstract class AbstractCreateKnowhowPage extends WizardPage {

    /** Wizard name */
    private String wizardName;
    /** Data registered */
    private Text registeredName;
    /** Registration label name */
    private Label registerLabelName;
    /** Parent group */
    private Group parentGroup;
    /** The length of the width of the text area */
    private static final int TEXT_NAME_WIDTH_HINT = 600;

    /**
     * Constructor.<br/>
     * 
     * @param pageName
     *            Page name
     * @param wizardName
     *            Wizard name
     */
    protected AbstractCreateKnowhowPage(String pageName, String wizardName) {
        super(pageName);
        this.wizardName = wizardName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        setRegisterNameArea(composite);

        super.setControl(composite);

    }

    /**
     * Define the area to set the name.<br/>
     * Define the area to register a name or know-how category name.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void setRegisterNameArea(Composite composite) {
        parentGroup = new Group(composite, SWT.SHADOW_NONE);
        parentGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // CheckStyle Magic number
        parentGroup.setLayout(new GridLayout(4, false));

        registerLabelName = new Label(parentGroup, SWT.NULL);
        registerLabelName.setLayoutData(ControlUtil.getTopGridData());
        registerLabelName.setText(wizardName);

        GridData gridDataSubject = ControlUtil
                .getThreeGridWidthData(TEXT_NAME_WIDTH_HINT);
        this.registeredName = new Text(parentGroup, SWT.BORDER);
        this.registeredName.setLayoutData(gridDataSubject);

    }

    /**
     * Set the label name.<br/>
     * 
     * @param labelName
     *            Label name
     */
    protected void setLabelText(String labelName) {
        if (!CmnStringUtil.isEmpty(labelName)) {
            registerLabelName.setText(labelName);
        } else {
            registerLabelName.setText(wizardName);
        }
    }

    /**
     * Get the data registered in the text.<br/>
     * 
     * @return Registered name
     */
    public String getRegisteredName() {
        return registeredName.getText();
    }

    /**
     * Get parentGroup.<br/>
     * 
     * @return parentGroup
     */
    public Group getParentGroup() {
        return parentGroup;
    }
}
