/*
 * ConfirmationPage.java
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

import java.io.File;
import java.io.IOException;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.ui.wizard.listener.BrowseFileButtonSelectionListener;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Page class know-how XML file Wizard final (output destination specified)
 * page.<br/>
 */
public class ConfirmationPage extends WizardPage {

    /** Destination information */
    private Text outputPlace;
    /** Destination specified button */
    private Button outputFolder;
    /** Know-how file name */
    private String knowhowFileName;
    /** Import file */
    private File importFile;
    /** Project name that is selected in the Package Explorer */
    private static String selectedProjectPath = CmnStringUtil.EMPTY;

    /**
     * Constructor.<br/>
     * 
     * @param pageName
     *            Page name
     * @param selection
     *            IStructuredSelection
     */
    protected ConfirmationPage(String pageName, IStructuredSelection selection) {
        super(pageName);
        setSelectionProject(selection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Group group = new Group(composite, SWT.SHADOW_NONE);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // CheckStyle Magic number
        group.setLayout(new GridLayout(3, false));

        setOutputPlaceArea(group);
        super.setControl(composite);
    }

    /**
     * Set the output destination specified area.<br/>
     * 
     * @param group
     *            Destination specified area
     */
    private void setOutputPlaceArea(Group group) {

        Label label = new Label(group, SWT.NULL);
        label.setText(ResourceUtil.outputLabel);

        GridData gridDataURL = new GridData(GridData.FILL_HORIZONTAL);
        this.outputPlace = new Text(group, SWT.BORDER);
        this.outputPlace.setLayoutData(gridDataURL);

        this.outputFolder = new Button(group, SWT.PUSH);
        this.outputFolder.setText(ResourceUtil.reference);
        // Adding a listener
        outputFolder
                .addSelectionListener(new BrowseFileButtonSelectionListener(
                        this, outputPlace, ResourceUtil.xml));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean visible) {
        super.getControl().setVisible(visible);
        outputFolder
                .removeSelectionListener(new BrowseFileButtonSelectionListener(
                        this, outputPlace, ResourceUtil.xml));
        // Setting of the button listener
        String filePath = ConfirmationPage.selectedProjectPath
                + CmnFileUtil.FILE_SEPARATOR + knowhowFileName;
        outputPlace.setText(filePath);
    }

    /**
     * Generate file name from the initial page information.<br/>
     * Generates a [PortabilityKnowhow.xml], <br/>
     * it generates a file name that you selected in the initial page If you
     * have obtained from the URI in the case of New.<br/>
     * 
     * @param firstMaintenanceKnowhowPage
     *            First page information
     */
    protected void createInitialFileName(
            CommencementPage firstMaintenanceKnowhowPage) {
        if (firstMaintenanceKnowhowPage.isSelectSubject()) {
            this.knowhowFileName = ApplicationPropertiesUtil
                    .getProperty(ApplicationPropertiesUtil.NEW_FILENAME_KNOWHOW_XML);
        } else {
            this.importFile = CmnFileUtil.getFile(firstMaintenanceKnowhowPage
                    .getInputUri());
            this.knowhowFileName = CmnFileUtil
                    .getFileName(firstMaintenanceKnowhowPage.getInputUri());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IWizardPage getPreviousPage() {
        this.setMessage(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.CONFIRMATION_PAGE_DESCRIPTION));
        return super.getPreviousPage();
    }

    /**
     * Get information on the output destination.<br/>
     * 
     * @return Destination information
     */
    public String outputFilePath() {
        return this.outputPlace.getText();
    }

    /**
     * Generate IFile.<br/>
     * 
     * @return IFile
     * @throws JbmException
     *             Jbm exception
     */
    protected IFile createNewFile() throws JbmException {
        try {
            IContainer container = ResourcesPlugin.getWorkspace().getRoot();
            return PluginUtil.createIFile(container, outputFilePath(), true);
        } catch (IOException e) {
            throw new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.FAIL_CREATE_FILE),
                    e);
        }
    }

    /**
     * Get the know-how XML file name.<br/>
     * 
     * @return Know-how XML file name
     */
    public String getKnowhowFileName() {
        return knowhowFileName;
    }

    /**
     * Get the know-how existing XML file.<br/>
     * 
     * @return Existing know-how
     */
    public File getImportKnowhowFile() {
        return importFile;
    }

    /**
     * Set in the project name field.<br/>
     * Gets the project name from the selection elements of the package
     * Explorer, <br/>
     * and set in the field.<br/>
     * 
     * @param selection
     *            ISelection the selectionChanged receives
     */
    private void setSelectionProject(ISelection selection) {
        if (!(selection instanceof TreeSelection)) {
            return;
        }
        Object type = ((TreeSelection) selection).getFirstElement();
        String project = CmnStringUtil.EMPTY;

        // The selected object, in the case of projects that are open
        if (type instanceof IProject) {
            if (PluginUtil.isProjectOpen(((IProject) type).getName())) {
                project = ((IProject) type).getName();
            }
        }
        // For folders
        if (type instanceof IFolder) {
            project = ((IFolder) type).getProject().getName();
        }
        // If the file
        if (type instanceof IFile) {
            project = ((IFile) type).getProject().getName();
        }
        ConfirmationPage.selectedProjectPath = project;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canFlipToNextPage() {
        return false;
    }

}
