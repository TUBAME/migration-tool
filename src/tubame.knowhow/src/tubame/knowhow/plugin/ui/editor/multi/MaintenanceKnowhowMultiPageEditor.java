/*
 * MaintenanceKnowhowMultiPageEditor.java
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
package tubame.knowhow.plugin.ui.editor.multi;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.editor.multi.checkitem.CheckItemFieldsPage;
import tubame.knowhow.plugin.ui.editor.multi.docbook.KnowhowDetailEditor;
import tubame.knowhow.plugin.ui.editor.multi.docbook.ReadOnlyKnowhowDetailEditor;
import tubame.knowhow.plugin.ui.editor.multi.documentation.DocumentationFormPage;
import tubame.knowhow.plugin.ui.editor.multi.documentation.KnowhowEditorTreeViewer;
import tubame.knowhow.plugin.ui.editor.multi.listener.KnowhowEditorPartListener;
import tubame.knowhow.plugin.ui.view.KnowhowEntryCheckItemView;
import tubame.knowhow.plugin.ui.view.KnowhowEntryView;
import tubame.knowhow.util.FileUtil;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.ViewUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Class to control the operation of each page editor of know-how (multi-page
 * editor).<br/>
 */
public class MaintenanceKnowhowMultiPageEditor extends FormEditor implements EditorDirty {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceKnowhowMultiPageEditor.class);
	/** Documentation page No */
	public static final int DOCIMENTATION_PAGE = 0;
	/** Know-how detail page No. */
	public static final int KNOWHOWDETAIL_PAGE = 1;
	/** Check items page No. */
	public static final int CHECKITAM_PAGE = 2;
	/** Tab page of documentation. */
	private DocumentationFormPage documentationFormPage;
	/** Tab page in the check item information. */
	private CheckItemFieldsPage checkItemFieldsPage;
	/** File path that is currently open. */
	private String fileLocationFullPath;
	/** Tab page of know-how detail information */
	private KnowhowDetailEditor knowhowDetailEditor;
	/** Know-how detail cache file */
	private String knowhowDetailTempFile;
	private boolean createdKnowhowEditor = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addPages() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.info(MessagePropertiesUtil
				.getMessage(MessagePropertiesUtil.LOG_OPEN_EDITOR));
		documentationFormPage = new DocumentationFormPage(this, null, ResourceUtil.documentation);
		checkItemFieldsPage = new CheckItemFieldsPage(this, null, ResourceUtil.checkItem);
		try {
			super.addPage(documentationFormPage);
			createDocBookPage(true);
			super.addPage(checkItemFieldsPage);
		} catch (IOException e) {
			JbmException.outputExceptionLog(LOGGER, e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_CREATE_KNOWHOW_EDITOR));
		} catch (PartInitException e) {
			JbmException.outputExceptionLog(LOGGER, e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_CREATE_KNOWHOW_EDITOR));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializePageSwitching() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		try {
			super.initializePageSwitching();
			createControlKnowhowEditor();
			KnowhowManagement.setKnowhowEditor(this);
			documentationFormPage.setKnowhowSubjectName();
		} catch (JbmException e) {
			JbmException.outputExceptionLog(LOGGER, e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_UPDATE_ENTRY_VIEW));
		}
	}

	/**
	 * Control and the know-how of data editor, data is reflection process
	 * know-how to the entry view.<br/>
	 * 
	 * @throws JbmException
	 *             JBM exception
	 */
	private void createControlKnowhowEditor() throws JbmException {

		refrectionKnowhowEntry();
		// Adding listeners
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService()
				.addPartListener(new KnowhowEditorPartListener());
		createdKnowhowEditor = true;
	}

	/**
	 * Reflecting the know-how to the data entry view, do the updates to heading
	 * data list of documentation.<br/>
	 * 
	 * @throws JbmException
	 *             JBM exception
	 */
	private void refrectionKnowhowEntry() throws JbmException {
		storageEditorInputFile();
		if (PluginUtil.getActivePage() != null) {
			ViewUtil.refrectionEntryViewData(fileLocationFullPath);
			updateDocumentationFormPage();
		}
	}

	/**
	 * Save a temporary relative path of know-how XML files to be used with
	 * know-how editor.<br/>
	 * 
	 */
	private void storageEditorInputFile() {
		IFileEditorInput fileInput = (IFileEditorInput) super.getEditorInput();
		super.setPartName(fileInput.getFile().getName());
		// Get the absolute path of the file
		fileLocationFullPath = fileInput.getFile().getLocation().toString();
		FileManagement.setPortabilityKnowhowFilePath(fileLocationFullPath);
	}

	/**
	 * Save temporarily the relative path of DocBook template file to be used in
	 * the know-how detail.<br/>
	 * 
	 * @throws IOException
	 *             IO exception
	 */
	private void storageKnowhowDetailInputFile() throws IOException {
		IFileEditorInput fileInput = (IFileEditorInput) super.getEditorInput();
		IContainer container = ResourcesPlugin.getWorkspace().getRoot();
		String workDir = fileInput.getFile().getProject().getName()
				+ ApplicationPropertiesUtil.getProperty(ApplicationPropertiesUtil.WORK_DIRECTORY);
		FileUtil.createWorkDir(container, workDir);
		knowhowDetailTempFile = workDir
				+ ApplicationPropertiesUtil.getProperty(ApplicationPropertiesUtil.DOCBOOK_TEMPORARY_PATH);
	}

	/**
	 * Insert know-how detail the (DocBook) editor page on the second page.<br/>
	 * 
	 * @param readOnly
	 *            Read-only (true: read-only)
	 * @throws PartInitException
	 *             PartInit exception
	 * @throws IOException
	 *             IO exception
	 */
	private void createDocBookPage(boolean readOnly) throws PartInitException, IOException {
		// Generation of Editor
		if (knowhowDetailEditor != null) {
			removePage(KNOWHOWDETAIL_PAGE);
		}
		knowhowDetailEditor = getStructuredTextEditor(readOnly);
		// IFile creation of know-how detail editor for editing
		IFile tempKnowhoDetailFile = createKnowhowDetailFile();
		// Refresh the DocBook template after creating a file
		// PluginUtil.refreshWorkSpace();
		addPage(KNOWHOWDETAIL_PAGE, knowhowDetailEditor, new FileEditorInput(tempKnowhoDetailFile));
		setPageText(KNOWHOWDETAIL_PAGE, ResourceUtil.knowhowDetail);
	}

	/**
	 * Generates know-how detail editor of read-only when read-only flag is
	 * true.<br/>
	 * 
	 * @param readOnly
	 *            Read-only flag
	 * @return Know-how detail page
	 */
	private KnowhowDetailEditor getStructuredTextEditor(boolean readOnly) {
		if (readOnly) {
			return new ReadOnlyKnowhowDetailEditor(this);
		}
		return new KnowhowDetailEditor(this);
	}

	/**
	 * Generate a template file of know-how detail for. <br/>
	 * Generate only object of IFile if it already exists.<br/>
	 * 
	 * @return IFile
	 * @throws IOException
	 *             IO exception
	 */
	private IFile createKnowhowDetailFile() throws IOException {
		IContainer container = ResourcesPlugin.getWorkspace().getRoot();
		// Get File information selected
		storageKnowhowDetailInputFile();
		if (knowhowDetailEditor.getKnowhowDetailType() != null) {
			clearKnowhowDetail();
		}
		if (CmnFileUtil.fileExists(getKnowhowDetailTempFile())) {
			return PluginUtil.createIFile(container, knowhowDetailTempFile, false);
		}
		IFile file = PluginUtil.createIFile(container, knowhowDetailTempFile, true);
		PluginUtil.refreshWorkSpace();
		return file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug("[monitor]" + monitor);
		MaintenanceKnowhowMultiPageEditor.LOGGER.info(MessagePropertiesUtil
				.getMessage(MessagePropertiesUtil.LOG_SAVE_EDITOR_START));
		this.checkItemFieldsPage.temporaryStorage();
		// The data reflect the view of know-how entry
		if (!PluginUtil.isTreeViewerDisposed()) {
			PluginUtil.getKnowhowEntryViewTreeViewer().refresh();
			doSaveCheck();
			try {
				KnowhowManagement.unCheckWrite(fileLocationFullPath, PluginUtil.getKnowhowEntryViewTreeViewer()
						.getInputEntry(), documentationFormPage.getTreeViewer().getInputEntry());
				super.commitPages(true);
				MaintenanceKnowhowMultiPageEditor.LOGGER.info(MessagePropertiesUtil
						.getMessage(MessagePropertiesUtil.LOG_PERFORMFINISH_KNOWHOW_EDITOR_SAVE));
			} catch (JbmException e) {
				openErrorDialog(e.getMessage(), e);
			}

			IViewPart opendView = PluginUtil.isOpendView(KnowhowEntryCheckItemView.ID);
			if (opendView != null) {
				KnowhowEntryCheckItemView checkItemEntryView = (KnowhowEntryCheckItemView) opendView;
				checkItemEntryView.refresh();
			}

		} else {
			openErrorDialog(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.OPEN_KNOWHOW_ENTRY_VIEW), null);
		}

	}

	/**
	 * syntax check along the DocBook specification and nest check of the
	 * Section to know-how XML tags when saving.<br/>
	 * 
	 * @return true:Normal false:Abnormality
	 */
	private boolean doSaveCheck() {
		if (!isKnowhowDetailStorage()) {
			return false;
		}
		if (!this.check()) {
			openErrorDialog(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.CHECK_NEST_SECTION_TAG), null);
			return false;
		}
		setDirty(false);
		return true;
	}

	/**
	 * Open the error dialog.<br/>
	 * 
	 * @param message
	 *            Message
	 * @param e
	 *            Exception
	 */
	private void openErrorDialog(String message, Exception e) {
		JbmException.outputExceptionLog(LOGGER, e, message);
		ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(), e, message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSaveAs() {
		// no operation
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSaveAsAllowed() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return false;
	}

	/**
	 * Get a file path that is selected.<br/>
	 * 
	 * @return File path
	 */
	public String getFileLocationFullPath() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return fileLocationFullPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDirty(boolean dirty) {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug("[dirty]" + dirty);
		if (createdKnowhowEditor) {
			documentationFormPage.setDirty(dirty);
			checkItemFieldsPage.setDirty(dirty);

		}
		// Edirot change notification
		super.editorDirtyStateChanged();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getDirty() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return checkItemFieldsPage.isDirty();
	}

	/**
	 * Get TreeViewer current.<br/>
	 * 
	 * @return TreeViewer
	 */
	public KnowhowEditorTreeViewer getTreeViewerOperator() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return documentationFormPage.getTreeViewer();
	}

	/**
	 * Updated the documentation page.<br/>
	 * Headings and list information, and update the subject name.<br/>
	 * 
	 * @throws JbmException
	 *             JBM exception
	 * 
	 */
	public void updateDocumentationFormPage() throws JbmException {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		this.getTreeViewerOperator().setEntryListData(documentationFormPage.createEntryOperation());
		knowhowDetailEditor.update();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return documentationFormPage.isDirty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug("[site]" + site + "[input]" + input);
		try {
			MaintenanceKnowhowMultiPageEditor.LOGGER.info(MessagePropertiesUtil
					.getMessage(MessagePropertiesUtil.LOG_OPEN_EDITOR));
			setUpEditor(site, input);
		} catch (JbmException e) {
			MaintenanceKnowhowMultiPageEditor.LOGGER.error(e.getMessage(), e.getCause());
			throw new PartInitException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Set up the initial processing.<br/>
	 * Throw JbmExcetpion If Setup fails.<br/>
	 * 
	 * @param site
	 *            IEditorSite
	 * @param input
	 *            IEditorInput
	 * @throws JbmException
	 *             Jbm exception
	 */
	private void setUpEditor(IEditorSite site, IEditorInput input) throws JbmException {
		// Check Editor of multiple display
		if (PluginUtil.isMultiOpenKnowhowEditor()) {
			throw new JbmException(
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_MULTI_OPEN_KNOWHOWEDITOR));
		}
		// Initializing process
		try {
			super.init(site, input);
			super.setPartName(((IFileEditorInput) super.getEditorInput()).getFile().getName());
			storageEditorInputFile();
			// For confirmation of whether the editor can be displayed, read in
			// advance the know-how XML
			KnowhowManagement.refresh();

		} catch (PartInitException e) {
			throw new JbmException(e.getMessage(), e);
		} catch (JbmException e) {
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean check() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		if (knowhowDetailEditor.getKnowhowDetailType() != null) {
			if (KnowhowManagement.isNestSection(knowhowDetailEditor.getKnowhowDetailType().getDocbookdata())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetFileName() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return fileLocationFullPath;
	}

	/**
	 * Controls the page to focus.<br/>
	 * 
	 * @param level
	 *            Focus level
	 */
	public void focusPageControl(int level) {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug("[level]" + level);
		switchKnowhowPageEditor(level);
		if (PortabilityKnowhowListViewData.LEVEL_SECOND == level) {
			super.setActivePage(MaintenanceKnowhowMultiPageEditor.KNOWHOWDETAIL_PAGE);
		} else if (PortabilityKnowhowListViewData.LEVEL_THIRD == level
				|| PortabilityKnowhowListViewData.LEVEL_FOURTH == level) {
			super.setActivePage(MaintenanceKnowhowMultiPageEditor.CHECKITAM_PAGE);
		} else if (PortabilityKnowhowListViewData.LEVEL_FIRST == level) {
			super.setActivePage(MaintenanceKnowhowMultiPageEditor.DOCIMENTATION_PAGE);
		}
	}

	/**
	 * When know-how is double-clicked in the entry view, switch to the editable
	 * know-how detail editor.<br/>
	 * 
	 * @param level
	 *            Focus level
	 */
	private void switchKnowhowPageEditor(int level) {
		try {
			if (PortabilityKnowhowListViewData.LEVEL_SECOND == level) {
				createDocBookPage(false);
			} else {
				clearKnowhowDetail();
				createDocBookPage(true);
			}
		} catch (PartInitException e) {
			JbmException.outputExceptionLog(LOGGER, e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_CREATE_KNOWHOW_EDITOR));
		} catch (IOException e) {
			JbmException.outputExceptionLog(LOGGER, e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_CREATE_KNOWHOW_EDITOR));
		}
	}

	/**
	 * Get a check item information page.<br/>
	 * 
	 * @return Check item information page
	 */
	public CheckItemFieldsPage getCheckItemFieldsPage() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return checkItemFieldsPage;
	}

	/**
	 * Clear information pages of documentation other than the creation page.<br/>
	 * 
	 */
	public void clearPageData() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		if (isKnowhowDetailStorage()) {
			clearKnowhowDetail();
		}
		this.checkItemFieldsPage.temporaryStorage();
		this.checkItemFieldsPage.clear();
	}

	/**
	 * Save the know-how detail information. <br/>
	 * The return true if stored properly.<br/>
	 * 
	 * @return true:Normal save false:Abnormal save
	 */
	public boolean isKnowhowDetailStorage() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return knowhowDetailEditor.temporaryStorageKnowhowDetailOnMemory();
	}

	/**
	 * Clear know-how detail editor.<br/>
	 * 
	 */
	public void clearKnowhowDetail() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		CmnFileUtil.clearFileContents(getKnowhowDetailTempFile());
		knowhowDetailEditor.setKnowhowDetailType(null);
	}

	/**
	 * Get knowhowDetailTempFile.<br/>
	 * 
	 * @return knowhowDetailTempFile
	 */
	public String getKnowhowDetailTempFile() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return PluginUtil.getFileFullPath(knowhowDetailTempFile);
	}

	/**
	 * Get the path relative to the project know-how of attachment.<br/>
	 * 
	 * @return Know-how attachment relative path
	 */
	public String getKnowhowDetailRelativeFilePath() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return knowhowDetailTempFile;
	}

	/**
	 * Get knowhowDetailEditor.<br/>
	 * 
	 * @return knowhowDetailEditor
	 */
	public KnowhowDetailEditor getKnowhowDetailEditor() {
		MaintenanceKnowhowMultiPageEditor.LOGGER.debug(CmnStringUtil.EMPTY);
		return knowhowDetailEditor;
	}
}
