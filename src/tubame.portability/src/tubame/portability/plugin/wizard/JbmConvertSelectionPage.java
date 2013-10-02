/*
 * JbmConvertSelectionPage.java
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
package tubame.portability.plugin.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import tubame.portability.model.generated.model.WebLogic;
import tubame.portability.util.FileUtil;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Wizard page class in the transformation input screen.<br/>
 * Various control of the input form transplant source (destination) or Weblogic
 * version.<br/>
 */
public class JbmConvertSelectionPage extends AbstractJbmSelectionPage {

    /**
     * Resources to be processed
     */
    private IResource resource;

    /**
     * Translated path source file output destination folder selected in this
     * screen
     */
    private Text outSourceDirectoryText;

    /**
     * WebLogic version that has been selected in this screen
     */
    private Combo webLogicVersionCombo;

    /**
     * Constructor.<br/>
     * Initializes the resource to be processed.<br/>
     * 
     * @param resource
     *            Resources to be processed
     */
    public JbmConvertSelectionPage(IResource resource) {
        this.resource = resource;
    }

    /**
     * {@inheritDoc}
     */
    public String getTargetText() {
        return resource.getProject().getName() + FileUtil.FILE_SEPARATOR
                + resource.getProjectRelativePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleMessage() {
        return ResourceUtil.WIZ_TITLE_CONVERT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferenceButtonLabelString() {
        return ResourceUtil.REFERENCE;
    }

    /**
     * Get the WebLogic version selection label string.<br/>
     * 
     * @return WebLogic version selection label string
     */
    public String getWebLogicVersionSelectedLabelString() {
        return ResourceUtil.SELECT_WEBLOGICVERSION;
    }

    /**
     * Get the converted source file output destination selection label string
     * to be displayed in this screen.<br/>
     * 
     * @return Porting the original source file group label
     */
    public String getOutSourceFileLabel() {
        return ResourceUtil.SELECT_FILE_CONVERTED;
    }

    /**
     * Get the path of the converted source file destination.<br/>
     * 
     * @return Converted source file destination path
     */
    public String getOutSourceFolderText() {
        return StringUtil.getText(outSourceDirectoryText);
    }

    /**
     * Get the WebLogic version.<br/>
     * 
     * @return WebLogic Version
     */
    public String getWebLogicVersionCombo() {
        return StringUtil.getComboText(webLogicVersionCombo);
    }

    /**
     * Get the wording of the error if you do not specify a source file after
     * conversion output destination.<br/>
     * 
     * @return Wording of the error if you do not specify a source file after
     *         conversion destination
     */
    public String getErrorConvertDirectoryNotEnteredString() {
        return MessageUtil.ERR_CONVERT_DIRECTORY_NOT_ENTERED;
    }

    /**
     * Get the wording of the error if the project name in the search results
     * file output destination does not exist.<br/>
     * 
     * @return Error message if the project name in the search results file
     *         output destination does not exist
     */
    public String getErrorProjectNotValue() {
        return MessageUtil.ERR_CONVERT_PROJECT_NOT_VALUE;
    }

    /**
     * Get the wording of the error if the project name in the search results
     * file output destination is not open.<br/>
     * 
     * @return Error message if the project name in the search results file
     *         output destination is not open
     */
    public String getErrorProjectNotOpen() {
        return MessageUtil.ERR_CONVERT_PROJECT_NOT_OPEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addControl(Composite composite) {
        setOutSourceFolderArea(composite);
        setWebLogicVersionArea(composite);
    }

    /**
     * Generate label text after conversion source file selection area.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void setOutSourceFolderArea(Composite composite) {
        Group group = new Group(composite, SWT.SHADOW_NONE);
        group.setText(getOutSourceFileLabel());
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setLayout(new GridLayout(2, false));

        // Converted source file output destination selection text
        outSourceDirectoryText = new Text(group, SWT.BORDER);
        outSourceDirectoryText.setLayoutData(new GridData(
                GridData.FILL_HORIZONTAL));
        // Set initial value to the project name that is selected in the
        // Explorer currently
        outSourceDirectoryText.setText(resource.getProject().getName());
        addModifiListener(outSourceDirectoryText);

        // Converted source file output destination selection button
        Button button = new Button(group, SWT.NULL);
        button.setText(getReferenceButtonLabelString());
        button.addSelectionListener(new BrowseDirectoryButtonSelectionListener(
                outSourceDirectoryText));
    }

    /**
     * Generate label pull-down box in the WebLogic version area.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void setWebLogicVersionArea(Composite composite) {
        Group group = new Group(composite, SWT.SHADOW_NONE);
        group.setText(getWebLogicVersionSelectedLabelString());
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setLayout(new GridLayout());

        // WebLogic version selection combo
        webLogicVersionCombo = new Combo(group, SWT.READ_ONLY);
        for (WebLogic webLogic : WebLogic.values()) {
            webLogicVersionCombo.add(webLogic.value());
        }
        webLogicVersionCombo.setText(WebLogic.V_8.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addTextvalidate() {
        // Not entered check
        if (StringUtil.isEmpty(getOutSourceFolderText())) {
            setErrorMessage(getErrorConvertDirectoryNotEnteredString());
            return false;
        }
        // Project reality check
        if (!PluginUtil.projectExists(getOutSourceFolderText())) {
            setErrorMessage(getErrorProjectNotValue());
            return false;
        }
        // Project open check
        if (!isProjectOpen(getOutSourceFolderText(), getErrorProjectNotOpen())) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorFileUpdateConfirm() {
        // Because the file to delete each time, but the present method is
        // called.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfirmString() {
        // Because the file to delete each time, but the present method is
        // called.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutFilePath() {
        return StringUtil.getText(outSourceDirectoryText);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String getKnowhowXmlFileLabelString() {
        return ResourceUtil.SELECT_KNOWHOW_XML_FILE;
    }
}
