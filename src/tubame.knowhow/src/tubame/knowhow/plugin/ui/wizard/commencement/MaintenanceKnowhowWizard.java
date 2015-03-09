/*
 * MaintenanceKnowhowWizard.java
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.editor.EntryOperator;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Know-how XML file creation and editing Wizard class.<br/>
 */
public class MaintenanceKnowhowWizard extends Wizard implements INewWizard, IExecutableExtension {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceKnowhowWizard.class);
	/** Selected object */
	private IStructuredSelection selection;
	/** Configuration element */
	private IConfigurationElement config;
	/** Know-how create / edit page information */
	private CommencementPage commencementPage;
	/** Configuration information confirmation page information */
	private ConfirmationPage confirmationPage;

	private IProject selectionKnowhowProject;

	/**
	 * Constructor.<br/>
	 * Set the title.<br/>
	 * 
	 */
	public MaintenanceKnowhowWizard() {
		super();
		super.setWindowTitle(ResourceUtil.knowhowXml);
		MaintenanceKnowhowWizard.LOGGER.info(MessagePropertiesUtil
				.getMessage(MessagePropertiesUtil.LOG_CREATE_KNOWHOW_MAINTENANCE_WIZARD));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		
		Object type = ((TreeSelection) selection).getFirstElement();
       
        // The selected object, in the case of projects that are open
        if (type instanceof IProject) {
        	IProject tmpProject = (IProject) type;
            if (tmpProject.isOpen()) {
            	selectionKnowhowProject = (IProject) type;
            }
        }
        String project = null;
        // For folders
        if (type instanceof IFolder) {
            project = ((IFolder) type).getProject().getName();
            selectionKnowhowProject = ((IFolder) type).getProject();
        }
        // If the file
        if (type instanceof IFile) {
            project = ((IFile) type).getProject().getName();
            selectionKnowhowProject = ((IFile) type).getProject();
        }
        
		try {
			return isOpenKnowhoEditor();
		} catch (InterruptedException e) {
			JbmException.outputExceptionLog(LOGGER, e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FATAL_PERFORM_FINISH));
			ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(), e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FATAL_PERFORM_FINISH));
			return false;
		} catch (InvocationTargetException e) {
			JbmException.outputExceptionLog(LOGGER, e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FATAL_PERFORM_FINISH));
			ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(), e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FATAL_PERFORM_FINISH));
			return false;
		} catch (JbmException e) {
			JbmException.outputExceptionLog(LOGGER, e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FATAL_PERFORM_FINISH));
			ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(), e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FATAL_PERFORM_FINISH));
			return false;
		}
	}

	/**
	 * Import XML know-how of existing know-how information.<br/>
	 * 
	 * @param file
	 *            Target file
	 * @param knowhowFileFullPath
	 *            Know-how file path
	 */
	private void importKnowhowXml(File file, String knowhowFileFullPath) {
		if (file != null) {
			// Copy the new file data in the file original
			FileManagement.outputXML(file.getPath(), knowhowFileFullPath);
		}
	}

	/**
	 * Create XML know-how to the project under.<br/>
	 * Create a file that you specified for the project that is selected on the
	 * confirmation page.<br/>
	 * 
	 * @return true:File creation normal false:File creation abnormal
	 * @throws JbmException
	 *             JbmException exception
	 * @throws InterruptedException
	 *             InterruptedException exception
	 * @throws InvocationTargetException
	 *             InvocationTargetException exception
	 */
	private boolean isOpenKnowhoEditor() throws JbmException, InvocationTargetException, InterruptedException {
		// Validation set
		 if (!isValidation()) {
		 return false;
		 }
		IFile file = confirmationPage.createNewFile();
		if (file == null) {
			return false;
		}
		// Get full path of the file
		String knowhowXmlFullPath = file.getLocation().toOSString();

		// Transplant maintenance know-how start
		createPortabilityKnowhow(file, knowhowXmlFullPath);

		this.selectionKnowhowProject = file.getProject();
		return true;
	}

	/**
	 * Decision transplant know-how can do start. The return of true normal
	 * case.<br/>
	 * Set the message in the message part of the Page to return false if the
	 * abnormality.<br/>
	 * 
	 * @return true:Normal false:Abnormality
	 */
	private boolean isValidation() {
		String fileName = CmnFileUtil.getFileName(confirmationPage.outputFilePath());
		String outputPathExcludeKnowhowFileName = confirmationPage.outputFilePath().substring(0, confirmationPage.outputFilePath().length() - fileName.length()-File.separator.length());
		if(!checkWorkSpaceAndProjectName(outputPathExcludeKnowhowFileName)){
			//ディレクトリ存在しない
			confirmationPage.setMessage(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERR_OUTPUT_DIR_NOT_EXIST),
					IMessageProvider.ERROR);
			return false;
		}
		
		String outputFilePathExcludeProjectName = confirmationPage.getOutputFilePathExcludeProjectName();
		IProject selectionProject = confirmationPage.getSelectionProject();
		

		IFile outputFile = selectionProject.getFile(outputFilePathExcludeProjectName);
		if (outputFile.exists()) {
			confirmationPage.setMessage(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.EXIST_SAME_FILE),
					IMessageProvider.ERROR);
			return false;
		} else if (!outputFile.getLocation().toFile().getParentFile().exists()) {
			confirmationPage.setMessage(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_FOLDER_NOT_VALUE),
					IMessageProvider.ERROR);
			return false;
		} else if (!outputFile.getFileExtension().equals(ResourceUtil.xml)) {
			confirmationPage.setMessage(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.NOT_XML_EXTENSION),
					IMessageProvider.ERROR);
			return false;
		}

		File importKnowhowFile = confirmationPage.getImportKnowhowFile();
		if (commencementPage.isSelectSubject() && importKnowhowFile != null
				&& !importKnowhowFile.exists()) {
			confirmationPage.setMessage(
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_NOT_EXIT_IMPORTFILE),
					IMessageProvider.ERROR);
			return false;
		} else if (commencementPage.isSelectURI() && importKnowhowFile != null
				&& !CmnFileUtil.isFileSize(importKnowhowFile.getPath())) {
			confirmationPage.setMessage(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_FILESIZE_IS_NULL),
					IMessageProvider.ERROR);
			return false;
		}
		return true;
	}

	private boolean checkWorkSpaceAndProjectName(String outputPathExcludeKnowhowFileName) {
		String outputFullPathExcludeKnowhowFileName =  PluginUtil.getWorkspaceRoot() + File.separator + outputPathExcludeKnowhowFileName;
		String outputFullPathExcludeKnowhowFileName2 = selectionKnowhowProject.getLocation().toFile().getParentFile()+  File.separator + outputPathExcludeKnowhowFileName;
		if(!new File(outputFullPathExcludeKnowhowFileName).exists() && !new File(outputFullPathExcludeKnowhowFileName2).exists()){
			return false;
		}
		return true;
	}

	/**
	 * Generates a transplant know-how, and display the know-how and know-how
	 * entry view editor.<br/>
	 * 
	 * @param file
	 *            IFile
	 * @param knowhowXmlFullPath
	 *            File full path
	 * @throws JbmException
	 *             JBM exception
	 */
	private void createPortabilityKnowhow(IFile file, String knowhowXmlFullPath) throws JbmException {
		MaintenanceKnowhowWizard.LOGGER.info(MessagePropertiesUtil
				.getMessage(MessagePropertiesUtil.LOG_START_CREATE_KNOWHOW_XML));
		if (commencementPage.isSelectSubject()) {
			KnowhowManagement.setSubjectName(commencementPage.getSubject());
			// Initial data storage know-how of creating XML
			KnowhowManagement.unCheckWrite(file.getProject(), knowhowXmlFullPath,
					new ArrayList<PortabilityKnowhowListViewOperation>(), new ArrayList<EntryOperator>());
		} else {
			importKnowhowXml(confirmationPage.getImportKnowhowFile(), knowhowXmlFullPath);
		}
		MaintenanceKnowhowWizard.LOGGER.info(MessagePropertiesUtil
				.getMessage(MessagePropertiesUtil.LOG_STOP_CREATE_KNOWHOW_XML));
		PluginUtil.openMaintenanceKnowhowEditor(file);
		BasicNewProjectResourceWizard.updatePerspective(config);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canFinish() {
		if (!super.getContainer().getCurrentPage().equals(confirmationPage)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		if (commencementPage.isSelectSubject()) {
			if (CmnStringUtil.isEmpty(commencementPage.getSubject())) {
				nextPage = reSetupPage(page,
						MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_INPUT_SUBJECT_NAME));
			} else {
				confirmationPage.createInitialFileName(commencementPage);
			}
		} else if (commencementPage.isSelectURI()) {
			if (!CmnFileUtil.fileExists(commencementPage.getInputUri())) {
				nextPage = reSetupPage(page, MessagePropertiesUtil.getMessage(MessagePropertiesUtil.NOT_EXIST_FILE));
			} else if (!CmnFileUtil.isExtension(commencementPage.getInputUri(),
					ApplicationPropertiesUtil.getProperty(ApplicationPropertiesUtil.EXTENSION_PORTABILITYKNOWHOW))) {
				nextPage = reSetupPage(page, MessagePropertiesUtil.getMessage(MessagePropertiesUtil.SELECT_XML_FILE));
			} else if (!CmnFileUtil.isFileSize(commencementPage.getInputUri())) {
				nextPage = reSetupPage(page,
						MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_FILESIZE_IS_NULL));
			} else {
				confirmationPage.createInitialFileName(commencementPage);
			}
		}
		return nextPage;
	}

	/**
	 * Return of the page information in the case of re-setting.<br/>
	 * If re-setting, and return the page information that set the target string
	 * to outline.<br/>
	 * 
	 * @param page
	 *            Page
	 * @param desctiption
	 *            Summary message
	 * @return Page
	 */
	private IWizardPage reSetupPage(IWizardPage page, String desctiption) {
		((WizardPage) page).setMessage(desctiption, IMessageProvider.ERROR);
		return page;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		// Wizard generation of the start page
		this.commencementPage = new CommencementPage(ResourceUtil.commencementPage, selection);
		this.commencementPage.setDescription(MessagePropertiesUtil
				.getMessage(MessagePropertiesUtil.COMMENCEMENT_PAGE_DESCRIPTION));
		// Wizard ACK Generation page
		this.confirmationPage = new ConfirmationPage(ResourceUtil.confirmationPage, selection);
		this.confirmationPage.setDescription(MessagePropertiesUtil
				.getMessage(MessagePropertiesUtil.CONFIRMATION_PAGE_DESCRIPTION));

		super.addPage(commencementPage);
		super.addPage(confirmationPage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		this.config = config;
	}
}
