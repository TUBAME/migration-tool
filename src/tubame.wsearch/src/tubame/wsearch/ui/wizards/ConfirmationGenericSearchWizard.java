/*
 * ConfirmationGenericSearchWizard.java
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
package tubame.wsearch.ui.wizards;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.util.CmnStringUtil;
import tubame.wsearch.Activator;
import tubame.wsearch.biz.cache.CacheBase.TYPE;
import tubame.wsearch.biz.cache.WSearchAnalyzerCacheArgument;
import tubame.wsearch.biz.ex.WSearchBizException;
import tubame.wsearch.biz.model.SearchFilter;
import tubame.wsearch.cache.AnalyzerCacheLoaderDelegate;
import tubame.wsearch.logics.WSearchBizDomain;
import tubame.wsearch.ui.dialog.ErrorDialog;
import tubame.wsearch.util.PluginUtil;
import tubame.wsearch.util.resource.ResourceUtil;

/**
 * It is a general-purpose search wizard confirmation class.<br/>
 */
public class ConfirmationGenericSearchWizard extends Wizard implements
        INewWizard {

    /**
     * Page
     */
    private ConfirmationGenericSearchPage confirmationGenericSearchPage;

    /**
     * Target resource
     */
    private IResource resource;

    /**
     * Business logic access domain
     */
    private WSearchBizDomain bizDomain;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConfirmationGenericSearchWizard.class);

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param window
     *            Window
     * @param resource
     *            Target resource
     */
    public ConfirmationGenericSearchWizard(IWorkbenchWindow window,
            IResource resource) {
        super();
        this.resource = resource;
        bizDomain = new WSearchBizDomain();
        setWindowTitle(Activator
                .getResourceString(ConfirmationGenericSearchWizard.class
                        .getName() + ".label.Title"));
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     */
    public ConfirmationGenericSearchWizard() {
        super();
        bizDomain = new WSearchBizDomain();
        setWindowTitle(Activator
                .getResourceString(ConfirmationGenericSearchWizard.class
                        .getName() + ".label.Title"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "ConfirmationGenericSearchWizard#init");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "ConfirmationGenericSearchWizard#performFinish");

        
        if (CmnStringUtil.isEmpty(confirmationGenericSearchPage
                .getOutputPlace().getText())) {
            MessageDialog.openError(getShell(), Activator
                    .getResourceString(ConfirmationGenericSearchWizard.class
                            .getName() + ".title.error"), Activator
                    .getResourceString(ConfirmationGenericSearchWizard.class
                            .getName() + ".err.msg.NoPath"));
            return false;
        }
//        File resultPath = new File(PluginUtil.getSelectedProject()
//                .getLocation().removeLastSegments(1).toString(),
//                confirmationGenericSearchPage.getOutputPlace().getText());
        File resultPath = new File(this.getRealOutputFolderPath());
        if (!resultPath.exists()) {
            MessageDialog.openError(getShell(), Activator
                    .getResourceString(ConfirmationGenericSearchWizard.class
                            .getName() + ".title.error"), Activator
                    .getResourceString(ConfirmationGenericSearchWizard.class
                            .getName() + ".err.msg.NotExists"));
            return false;
        }
        if (!resultPath.isDirectory()) {
            MessageDialog.openError(getShell(), Activator
                    .getResourceString(ConfirmationGenericSearchWizard.class
                            .getName() + ".title.error"), Activator
                    .getResourceString(ConfirmationGenericSearchWizard.class
                            .getName() + ".err.msg.NotDirectory"));
            return false;
        }
        File resultFile = new File(resultPath.getPath(),
                ResourceUtil.RESULT_FILE_NAME);
        if (resultFile.exists()) {
            int ret = new Dialog(getShell()) {
                protected Control createDialogArea(Composite parent) {
                    Composite composite = (Composite) super
                            .createDialogArea(parent);
                    composite
                            .getShell()
                            .setText(
                                    Activator
                                            .getResourceString(ConfirmationGenericSearchWizard.class
                                                    .getName()
                                                    + ".title.Overwrite"));
                    Label label = new Label(composite, SWT.SHADOW_NONE);

                    label.setText(Activator
                            .getResourceString(ConfirmationGenericSearchWizard.class
                                    .getName() + ".label.Overwrite"));
                    return composite;
                }
            }.open();
            if (ret == IDialogConstants.CANCEL_ID) {
                return false;
            }
        }

        // Acquisition of preferences extension of search
        Set<SearchFilter> srcSearchFilters = Activator.getSrcSearchFilters();
        Set<SearchFilter> targets = new HashSet<SearchFilter>();
        for (SearchFilter searchFilter : srcSearchFilters) {
            if (searchFilter.isEnabled()) {
                targets.add(searchFilter);
            }
        }
        if (targets.size() == 0) {
            String msg = Activator.getResourceString(this.getClass().getName()
                    + ".err.msg.SearchFilterCheckUnEnableERR");
            Activator.logWithDialog(new IllegalArgumentException(msg), msg);
            return false;
        }
        IProject selectedProject = PluginUtil.getSelectedProject();
        
        String selectionDir = selectedProject.getName();
        String stateDir = Activator.getDefault().getStateLocation()
                .toOSString();
        WSearchAnalyzerCacheArgument cacheArgument = new WSearchAnalyzerCacheArgument(
                TYPE.ANALYZE, stateDir, selectionDir,
                new AnalyzerCacheLoaderDelegate(),this.getRealProjectPath());
        try {
            this.bizDomain.cacheInit(cacheArgument, this.resource);
        } catch (WSearchBizException e1) {

            // Interrupt the process.
            return false;
        }
//        String outputDirPath = selectedProject.getLocation()
//                .removeLastSegments(1).toString()
//                + CmnStringUtil.SLASH
//                + confirmationGenericSearchPage.getOutputPlace().getText();
        
        String outputDirPath = PluginUtil.getRealOutputDirPath(selectedProject,confirmationGenericSearchPage.getOutputPlace().getText());

        if (this.bizDomain.createAnalyzeAndCompareJobs(this.resource,
                srcSearchFilters, outputDirPath)) {
            try {
                this.bizDomain.generateDestLibMetaData(Activator
                        .getLibraryRepositories().values(), cacheArgument,
                        stateDir, outputDirPath);
            } catch (WorkbenchException e) {
                String message = Activator
                        .getResourceString(ConfirmationGenericSearchWizard.class
                                .getName() + ".err.msg.GenerateLibMetaDataErr");
                Activator.log(e, message);
                ErrorDialog.openErrorDialog(getShell(), e, message);
                return false;
            }
        } else {
            return false;
        }
        LOGGER.info(Activator.getResourceString("debug.msg.end")
                + "ConfirmationGenericSearchWizard#performFinish");
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {
        confirmationGenericSearchPage = new ConfirmationGenericSearchPage(
                Activator
                        .getResourceString(ConfirmationGenericSearchWizard.class
                                .getName() + ".label.PageName"));
        confirmationGenericSearchPage.setDescription(Activator
                .getResourceString(ConfirmationGenericSearchWizard.class
                        .getName() + ".label.DescriptionText"));

        super.addPage(confirmationGenericSearchPage);
    }
    
	public String getRealOutputFolderPath(){
		String outputPath = this.confirmationGenericSearchPage.getOutputPlace().getText();
		String outputPathExecludeProjectName = getOutputPathExecludeProjectName(this.resource.getProject().getName(),outputPath);
		return this.resource.getProject().getLocation().toOSString() + File.separator + outputPathExecludeProjectName;
	}
	
	public String getRealProjectPath(){
		return this.resource.getProject().getLocation().toOSString();
	}

	private String getOutputPathExecludeProjectName(String projectName, String includeProjectNameVal) {
		if(projectName.equals(includeProjectNameVal)){
			return "";
		}else{
			if(includeProjectNameVal.length()>projectName.length() ){
				return includeProjectNameVal.substring(includeProjectNameVal.length());
			}
		}
		return null;
	}
	
}