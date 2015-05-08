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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import tubame.portability.model.ReportTemplateType;
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
public class ReportGenDirSelectionPage extends AbstractJbmSelectionPage {

    /**
     * Resources to be processed
     */
    private IResource resource;

    /**
     * Translated path source file output destination folder selected in this
     * screen
     */
    private Text outSourceDirectoryText;

	private Combo reportTemplateCombo;


    /**
     * Constructor.<br/>
     * Initializes the resource to be processed.<br/>
     * 
     * @param resource
     *            Resources to be processed
     */
    public ReportGenDirSelectionPage(IResource resource) {
        this.resource = resource;
    }

    public Combo getReportTemplateCombo() {
		return reportTemplateCombo;
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
        return ResourceUtil.WIZ_TITLE_REPORTGEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferenceButtonLabelString() {
        return ResourceUtil.REFERENCE;
    }


    /**
     * Get the converted source file output destination selection label string
     * to be displayed in this screen.<br/>
     * 
     * @return Porting the original source file group label
     */
    public String getOutSourceFileLabel() {
        return ResourceUtil.SELECT_REPORT_OUTPUT_DIR;
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
     * Get the wording of the error if you do not specify a source file after
     * conversion output destination.<br/>
     * 
     * @return Wording of the error if you do not specify a source file after
     *         conversion destination
     */
    public String getErrorOutputDirectoryNotEnteredString() {
        return MessageUtil.ERR_REPORT_DIRECTORY_NOT_ENTERED;
    }


    /**
     * Get the wording of the error if the project name in the search results
     * file output destination is not open.<br/>
     * 
     * @return Error message if the project name in the search results file
     *         output destination is not open
     */
    public String getErrorProjectNotOpen() {
        return MessageUtil.ERR_REPORTGEN_PROJECT_NOT_OPEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addControl(Composite composite) {
        setOutSourceFolderArea(composite);
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
        
        
        Group group2 = new Group(composite, SWT.SHADOW_NONE);
        group2.setText(ResourceUtil.SELECT_TPL);
        group2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group2.setLayout(new GridLayout());
        
        reportTemplateCombo = new Combo(group2, SWT.READ_ONLY);
        ReportTemplateType[] values = ReportTemplateType.values();
        for (ReportTemplateType reportTemplateType : values) {
        	reportTemplateCombo.add(reportTemplateType.getValue());
		}
        
        reportTemplateCombo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				setPageComplete(textValidate());
			}
		});
        
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addTextvalidate() {
        // Not entered check
        if (StringUtil.isEmpty(getOutSourceFolderText())) {
            setErrorMessage(getErrorOutputDirectoryNotEnteredString());
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
        
        //template check
        if (this.getReportTemplateCombo().getText()=="") {
            setErrorMessage(ResourceUtil.SELECT_TPL);
            return false;
        }
        return true;
    }


	/**
     * {@inheritDoc}
     */
    @Override
    public String getOutFilePath() {
        return StringUtil.getText(outSourceDirectoryText);
    }
    
    /**
     * Get the wording of the error if the project name in the search results
     * file output destination does not exist.<br/>
     * 
     * @return Error message if the project name in the search results file
     *         output destination does not exist
     */
    public String getErrorProjectNotValue() {
        return MessageUtil.ERR_REPORTGEN_PROJECT_NOT_VALUE;
    }

	@Override
	public String getConfirmString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKnowhowXmlFileLabelString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorFileUpdateConfirm() {
		// TODO Auto-generated method stub
		return null;
	}

	public IProject getSelectedProject() {
		return resource.getProject();
	}
	
	public String getOutputFullPath(){
		String outputPathExcludeProjectName = getOutputPathExcludeProjectName();
		if(outputPathExcludeProjectName!= null){
			return resource.getProject().getFile(outputPathExcludeProjectName).getLocation().toOSString();
		}
		return resource.getLocation().toOSString();
	}
	
	
	public String getSearchTargetFullPath(){
		return  this.resource.getLocation().toOSString();
	}
	
	

	public String getSearchTargetDirPath(){
		return resource.getLocation().toOSString();
	}
	
	private String getOutputPathExcludeProjectName(){
		if(outSourceDirectoryText.getText().equals(this.resource.getProject().getName())){
			return null;
		}
		return outSourceDirectoryText.getText().substring(this.resource.getProject().getName().length());
	}
	
	
	
}
