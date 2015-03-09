/*
 * CommencementPage.java
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
package tubame.knowhow.plugin.ui.wizard.commencement;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.ui.wizard.listener.CreatePatternSelectionListener;
import tubame.knowhow.plugin.ui.wizard.listener.ExistingKnowhowSelectionListener;
import tubame.knowhow.util.ControlUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Page class know-how of XML file Wizard start page.<br/>
 */
public class CommencementPage extends WizardPage {

    /** Subject Name input fields */
    private Text subjectName;
    /** URI input fields */
    private Text inputURI;
    /** New button (radio button) */
    private Button radioNew;
    /** From URI acquisition button (radio button) */
    private Button radioURI;
    /** Local input destination folder specified button */
    private Button inputLocalFolder;
    /** Group of New */
    private Group newCreateAreaGroup;
    /** Group acquired from URI */
    private Group fromUrlAreaGroup;
    /** Selected object */
    private IStructuredSelection selection;
    /** Unselected flag */
    private boolean notSelectionResource;

    /**
     * Constructor.<br/>
     * 
     * @param pageName
     *            Page name
     * @param selection
     *            Selected object
     */
    protected CommencementPage(String pageName, IStructuredSelection selection) {
        super(pageName);
        this.selection = selection;
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());

        radioNew = new Button(composite, SWT.RADIO);
        radioNew.setText(ResourceUtil.newCreate);
        setNewCreateArea(composite);

        radioURI = new Button(composite, SWT.RADIO);
        radioURI.setText(ResourceUtil.fromURI);
        setFromUrlArea(composite);

        createEnableGroupListener();
        init(composite);
        super.setControl(composite);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IWizardPage getPreviousPage() {
        this.setMessage(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.COMMENCEMENT_PAGE_DESCRIPTION));
        return super.getPreviousPage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canFlipToNextPage() {
        if (this.notSelectionResource) {
            return false;
        }
        return true;
    }

    /**
     * Make the initial processing of the initial page.<br/>
     * Perform initial treatment focus, active, etc.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void init(Composite composite) {
        if (selection.isEmpty()) {
            controlNotSelectionResource(
                    composite,
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.NOT_START_PORTABILITY_KNOWHOW));
        } else if (!(selection instanceof TreeSelection)) {
            controlNotSelectionResource(
                    composite,
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.NOT_SELECT_GENERAL_PROJECT));
        } else {
            radioNew.setFocus();
            inputURI.setEnabled(false);
            inputLocalFolder.setEnabled(false);
        }
    }

    /**
     * The control of each item when you run the know-how in the unselected
     * state.<br/>
     * It is not selected, deactivates each item, set the message of the wizard.<br/>
     * 
     * @param composite
     *            Composite
     * @param errorMessage
     *            Error message
     */
    private void controlNotSelectionResource(Composite composite,
            String errorMessage) {
        childItemDisenabled(composite.getChildren());
        childItemDisenabled(newCreateAreaGroup.getChildren());
        childItemDisenabled(fromUrlAreaGroup.getChildren());
        this.setErrorMessage(errorMessage);
        notSelectionResource = true;
        canFlipToNextPage();
    }

    /**
     * The deactivation child items.<br/>
     * 
     * @param childControls
     *            Child items
     */
    private void childItemDisenabled(Control[] childControls) {
        for (Control control : childControls) {
            control.setEnabled(false);
        }
    }

    /**
     * Set the layout of the acquisition part from URL.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void setFromUrlArea(Composite composite) {

        fromUrlAreaGroup = new Group(composite, SWT.SHADOW_NONE);
        fromUrlAreaGroup
                .setLayoutData(ControlUtil.getFillGridData(true, false));
        // CheckStyle Magic number
        fromUrlAreaGroup.setLayout(new GridLayout(3, false));

        Label label = new Label(fromUrlAreaGroup, SWT.NULL);
        label.setText(ResourceUtil.uriLabel);
        inputURI = new Text(fromUrlAreaGroup, SWT.BORDER);
        inputURI.setLayoutData(ControlUtil.getFillGridData(true, true));

        inputLocalFolder = new Button(fromUrlAreaGroup, SWT.PUSH);
        inputLocalFolder.setText(ResourceUtil.reference);
        inputLocalFolder
                .addSelectionListener(new ExistingKnowhowSelectionListener(
                        inputURI, this));

        // In the following initial treatment only label
        label.setEnabled(false);

    }

    /**
     * Set the layout of the acquisition part from New.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void setNewCreateArea(Composite composite) {
        newCreateAreaGroup = new Group(composite, SWT.SHADOW_NONE);
        newCreateAreaGroup.setLayoutData(ControlUtil.getFillGridData(true,
                false));
        // CheckStyle Magic number
        newCreateAreaGroup.setLayout(new GridLayout(2, false));

        Label label = new Label(newCreateAreaGroup, SWT.NULL);
        label.setText(ResourceUtil.subjectNameLabel);
        subjectName = new Text(newCreateAreaGroup, SWT.BORDER);
        subjectName.setLayoutData(ControlUtil.getFillGridData(true, true));
    }

    /**
     * And registration of the active listener and non-activity of each radio
     * button.<br/>
     * And registration of the active listener and non-activity of the radio
     * button of the acquisition from the New / URL.<br/>
     * 
     */
    private void createEnableGroupListener() {
        // Radio button listener of New
        radioNew.addSelectionListener(new CreatePatternSelectionListener(
                newCreateAreaGroup, fromUrlAreaGroup, this));
        // Radio button listeners get from a URI
        radioURI.addSelectionListener(new CreatePatternSelectionListener(
                fromUrlAreaGroup, newCreateAreaGroup, this));
    }

    /**
     * Get the subject name.<br/>
     * 
     * @return Subject Name
     */
    public String getSubject() {
        return this.subjectName.getText();
    }

    /**
     * Return of the determination result of the newly created if you are
     * selecting.<br/>
     * Decision returns the result of the radio button New is whether it is
     * selected.<br/>
     * 
     * @return Boolean true:Selection false:Unselected
     */
    public boolean isSelectSubject() {
        return radioNew.getSelection();
    }

    /**
     * The decision returns the result of the acquisition radio button is if you
     * are selecting from URI.<br/>
     * 
     * @return Boolean true:Selection false:Unselected
     */
    public boolean isSelectURI() {
        return radioURI.getSelection();
    }

    /**
     * Get inputURI.<br/>
     * 
     * @return inputURI
     */
    public String getInputUri() {
        return inputURI.getText();
    }

}
