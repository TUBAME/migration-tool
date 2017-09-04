/*
 * KnowhowEntryView.java
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
package tubame.knowhow.plugin.ui.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.CheckItemViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.dialog.ConfirmDialog;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.ViewUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Know-how entry view class.<br/>
 */
public class KnowhowEntryView extends ViewPart implements ViewRefresh {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(KnowhowEntryView.class);

	/** Tree view */
	private KnowhowEntryTreeViewer knowhowEntryTreeViewer;

	/** open view action */
	private Action openViewAction;

	private Action importAsciidocAction;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		LOGGER.info(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.LOG_CREATE_KNOWHOW_ENTRY_VIEW));

		knowhowEntryTreeViewer = new KnowhowEntryTreeViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);

		makeActions();
		contributeToActionBars();
		// Setting the display data
		knowhowEntryTreeViewer.addDoubleClickListener(new KnowhowEntryViewClickListener());
		setData();
	}

	/**
	 * Set the item information on entry view.<br/>
	 * The display reads, in TreeViewer know-how XML file.<br/>
	 * Also performs together reflecting to know the editor.<br/>
	 * 
	 */
	private void setData() {
		KnowhowManagement.setKnowhowEntryTreeViewer(knowhowEntryTreeViewer);
		if (isReflectionKnowhow()) {
			MaintenanceKnowhowMultiPageEditor knowhowMultiEditor = KnowhowManagement.getKnowhowEditor();
			try {
				ViewUtil.refrectionEntryViewData(FileManagement.getPortabilityKnowhowFilePath());
				knowhowMultiEditor.updateDocumentationFormPage();
				knowhowMultiEditor.setDirty(false);
			} catch (JbmException e) {
				JbmException.outputExceptionLog(LOGGER, e,
						MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_INITIAL_READING));
			}
		}
	}

	/**
	 * Determine whether it is possible to reflect to heading the list. <br/>
	 * It returns false if not reflected.<br/>
	 * 
	 * @return true:Reflect possible false:Reflect not
	 */
	private boolean isReflectionKnowhow() {
		if (FileManagement.getPortabilityKnowhowFilePath().equals(CmnStringUtil.EMPTY)) {
			return false;
		}
		if (KnowhowManagement.getKnowhowEditor() == null) {
			return false;
		}
		if (KnowhowManagement.getKnowhowEditor().getTreeViewerOperator().getTree().isDisposed()) {
			return false;
		}
		return true;
	}

	/**
	 * Refresh the view by setting the data entry view to know-how.<br/>
	 * 
	 * @param knowhowListViewOperations
	 *            List<PortabilityKnowhowListViewOperation>
	 */
	public void setPortabilityKnowhowListViewData(List<PortabilityKnowhowListViewOperation> knowhowListViewOperations) {
		knowhowEntryTreeViewer.setInput(knowhowListViewOperations);
		knowhowEntryTreeViewer.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		// no operation
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		setData();
		IViewPart opendView = PluginUtil.isOpendView(KnowhowEntryCheckItemView.ID);
		if (opendView != null) {
			KnowhowEntryCheckItemView checkItemEntryView = (KnowhowEntryCheckItemView) opendView;
			checkItemEntryView.refresh();
		}
	}

	/**
	 * Get treeViewer.<br/>
	 * 
	 * @return treeViewer
	 */
	public KnowhowEntryTreeViewer getTreeViewer() {
		return knowhowEntryTreeViewer;
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	private void makeActions() {
		openViewAction = new Action() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void run() {
				try {
					// open view
					KnowhowEntryCheckItemView checkItemView = (KnowhowEntryCheckItemView) PluginUtil.getActivePage()
							.showView(KnowhowEntryCheckItemView.ID);
					checkItemView.refresh();
					KnowhowEntryTreeViewer knowhowEntryViewTreeViewer = PluginUtil.getKnowhowEntryViewTreeViewer();

					PortabilityKnowhowListViewOperation oneSelection = knowhowEntryViewTreeViewer.getOneSelection();
					if (oneSelection != null) {
						AbstractViewType knowhowViewType = oneSelection.getKnowhowViewType();
						if (knowhowViewType != null) {
							if (knowhowViewType instanceof CheckItemViewType) {
								checkItemView.selectElement(oneSelection);
							}
						}
					}

				} catch (PartInitException e) {
					JbmException.outputExceptionLog(LOGGER, e,
							MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_OPEN_VIEW));
				}
			}
		};
		openViewAction
				.setText(ApplicationPropertiesUtil.getProperty(ApplicationPropertiesUtil.OPEN_CHECKITEM_ENTRY_VIEW));

		importAsciidocAction = new Action() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void run() {
				MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil.getKnowhowEditor();
				if(knowhowMultiPageEditor == null){
					ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(),null,
							MessagePropertiesUtil.getMessage(MessagePropertiesUtil.REQUIRED_ACTIVE_EDITOR_FOR_ADOC_IMPORT));
					return;
				}
				if (isDirty(knowhowMultiPageEditor)) {
					String knowhowXmlPath = knowhowMultiPageEditor.getFileLocationFullPath();
					FileDialog dialog = new FileDialog(PluginUtil.getActiveWorkbenchShell(), SWT.SAVE);

					try {
						IFile knowhowDetailTempIFile = knowhowMultiPageEditor.getKnowhowDetailTempIFile();
						String projectTempdir = knowhowDetailTempIFile.getParent().getLocation().toOSString();

						if (addKnowhowFromAsciiDoc(dialog, knowhowXmlPath, projectTempdir)) {
							knowhowMultiPageEditor.close(false);
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

							IFileEditorInput editorInput = (IFileEditorInput) knowhowMultiPageEditor.getEditorInput();
							IFile file = editorInput.getFile();
							

							MessageDialog.openInformation(PluginUtil.getActiveWorkbenchShell(),
									ResourceUtil.addKnowhowFromAsciiDoc,
									MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FINISH_IMPORT_ADOC) + "\n"
											+ MessagePropertiesUtil
													.getMessage(MessagePropertiesUtil.BACKUP_KNOWHOW_FOR_IMPORT_ADOC));
							
							if (file != null) {
								IDE.openEditor(page, file,
										"tubame.knowhow.maintenance.portability.editors.multi.KnowhowMultiEditor");
							}
						}

					} catch (Exception e) {
						ErrorDialog.errorDialogWithStackTrace(PluginUtil.getActiveWorkbenchShell(),
								MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERR_IMPORT_ASCIIDOC), e);
					}

				}
			}
		};

		importAsciidocAction
				.setText(ApplicationPropertiesUtil.getProperty(ApplicationPropertiesUtil.LABEL_IMPORT_ASCIIDOC));

	}

	private boolean addKnowhowFromAsciiDoc(FileDialog dialog, String knowhowXmlFilePath, String projectTempdir)
			throws Exception {
		// Settings dialog
		String[] exts = { "adoc" };
		dialog.setFilterExtensions(exts);
		dialog.setText(ResourceUtil.addKnowhowFromAsciiDoc);
		dialog.setFileName("*.adoc");
		String adocFilePath = dialog.open();
		String adocFileName = dialog.getFileName();
		String convertedFileName = adocFileName.substring(0, adocFileName.length() - 5) + ".xml";
		if (adocFilePath != null) {

			boolean valid = FileManagement.validAsciidocHeaderForImport(adocFilePath);
			if (!valid) {
				ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(), null,
						MessagePropertiesUtil.getMessage(MessagePropertiesUtil.REQUIRED_ASCIIDOC_HEADER)+"\n\nTitle\n=====\nUserName");
				return false;
			}
			try {
				FileManagement.addKnowhowFromAsciidoc(adocFilePath, knowhowXmlFilePath, projectTempdir);
			} catch (Exception e) {
				FileManagement.deleteTmpFileForAsciidocImport(projectTempdir, adocFileName, convertedFileName);
				throw e;
			}
			boolean importOk = ConfirmDialog.openConfirm(PluginUtil.getActiveWorkbenchShell(),
					ResourceUtil.addKnowhowFromAsciiDoc,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.PERFORM_ADD_KNOWHOW_FROM_ASCIIDOC));
			if (importOk) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String timeStamp = sdf.format(new Date());
				try {
					FileManagement.backupAndAppendKnowhowForImportAdoc(knowhowXmlFilePath, projectTempdir,timeStamp);
					FileManagement.refresh();
				} catch (Exception e) {
					// restore
					FileManagement.restoreKnowhowUsingBackup(projectTempdir, knowhowXmlFilePath,timeStamp);
					FileManagement.refresh();
					FileManagement.deleteTmpFileForAsciidocImport(projectTempdir, adocFileName, convertedFileName);
					throw e;
				}
			}else{
				return false;
			}
			FileManagement.deleteTmpFileForAsciidocImport(projectTempdir, adocFileName, convertedFileName);
		}
		return true;
	}

	private void fillLocalPullDown(IMenuManager menuManager) {
		menuManager.add(openViewAction);
		menuManager.add(importAsciidocAction);
	}

	private boolean isDirty(MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor) {
		if (knowhowMultiPageEditor.isDirty()) {
			JbmException.outputExceptionLog(LOGGER, null,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.SAVE_PROCESS_KNOWHOW_EDITOR));
			ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(), null,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.SAVE_PROCESS_KNOWHOW_EDITOR));
			return false;
		}
		return true;
	}

}
