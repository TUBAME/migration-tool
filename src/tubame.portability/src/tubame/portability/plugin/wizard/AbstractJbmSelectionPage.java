/*
 * AbstractJbmSelectionPage.java
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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import tubame.portability.util.PluginUtil;

/**
 * Wizard page base class of the input screen.<br/>
 * Various control of the input form transplant source (destination) and search
 * result files.<br/>
 */
public abstract class AbstractJbmSelectionPage extends WizardPage implements
        FileBrowseButton {

    /**
     * Get the processing resources.<br/>
     * 
     * @return Processed resources
     */
    public abstract String getTargetText();

    /**
     * Get Save string.<br/>
     * Get the string to be displayed on the screen Comfirm.<br/>
     * 
     * @return Save string
     */
    public abstract String getConfirmString();

    /**
     * Get the know-how XML file selection label string to be displayed in this
     * screen.<br/>
     * 
     * @return Know-how XML file group label
     */
    public abstract String getKnowhowXmlFileLabelString();

    /**
     * Get the Browse button label string to be displayed in this screen.<br/>
     * 
     * @return Browse button label
     */
    public abstract String getReferenceButtonLabelString();

    /**
     * Get the error terms of the Save cancellation.<br/>
     * 
     * @return Error message of Save cancellation
     */
    public abstract String getErrorFileUpdateConfirm();

    /**
     * Added check of input text.<br/>
     * 
     * @return true:None validateError false:There is validateError
     */
    public abstract boolean addTextvalidate();

    /**
     * Add the components to be displayed on the page.<br/>
     * 
     * @param composite
     *            Composite
     */
    public abstract void addControl(Composite composite);

    /**
     * Get the title to the top of the page.<br/>
     * 
     * @return Title to the top of the page
     */
    public abstract String getTitleMessage();

    /**
     * Constructor.<br/>
     * It passed the process to WizardPage.<br/>
     * 
     */
    public AbstractJbmSelectionPage() {
        super(AbstractJbmSelectionPage.class.getName());
        setTitle(getTitleMessage());
    }

    /**
     * Configuring the display of the wizard page.<br/>
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        addControl(container);
        super.setControl(container);
    }

    /**
     * Set the text change notification listener to Text.<br/>
     * 
     * @param text
     *            Text to set a listener
     */
    protected void addModifiListener(Text text) {
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(textValidate());
            }
        });
    }

    /**
     * Input validation.<br/>
     * Do the Validate transplant original source storage folder, <br/>
     * the search results file output destination.<br/>
     * 
     * @return true:None validateError false:There is validateError
     * 
     */
    public boolean textValidate() {
        // Input check that you added in the subclass
        if (!addTextvalidate()) {
            return false;
        }
        setErrorMessage(null);
        setPageComplete(true);

        return true;
    }

    /**
     * The acquisition project that is included in the file path from the
     * workspace root is whether the currently open.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @param errorMessage
     *            Error message when closing the project
     * @return true:Open false:Not open
     */
    public boolean isProjectOpen(String filePath, String errorMessage) {
        if (!PluginUtil.isProjectOpen(filePath)) {
            setErrorMessage(errorMessage);
            return false;

        }
        return true;
    }
}
