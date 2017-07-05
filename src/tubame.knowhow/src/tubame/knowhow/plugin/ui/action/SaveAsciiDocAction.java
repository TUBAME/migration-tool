/*
 * UpDownItemAction.java
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
package tubame.knowhow.plugin.ui.action;

import java.io.InputStream;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.util.CmnFileUtil;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.logic.converter.AsciiDocConverter;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.ui.dialog.ConfirmDialog;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.plugin.ui.view.KnowhowEntryView;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Control class to move up or down the selected item in the knowledge entry
 * view.<br/>
 */
public class SaveAsciiDocAction implements IEditorActionDelegate {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveAsciiDocAction.class);

	private KnowhowEntryView knowhowEntryView;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IAction action) {
		SaveAsciiDocAction.LOGGER.debug("[action]" + action);
		MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil.getKnowhowEditor();
		if (isOutputFile(knowhowMultiPageEditor)) {
			String targetFilePath = knowhowMultiPageEditor.getFileLocationFullPath();
			String targetFileName = CmnFileUtil.getFileName(targetFilePath);
			FileDialog dialog = new FileDialog(PluginUtil.getActiveWorkbenchShell(), SWT.SAVE);
			saveAsciiDoc(dialog, targetFilePath, targetFileName);
		}

	}

	/**
	 * Save AsciiDoc.<br/>
	 * 
	 * @param dialog
	 *            Dialog
	 * @param targetFilePath
	 *            Save target file path
	 * @param targetFileName
	 *            Save target file name
	 * @return true:Successful save false:Save failure
	 */
	private boolean saveAsciiDoc(FileDialog dialog, String targetFilePath, String targetFileName) {
		SaveAsciiDocAction.LOGGER.info(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.LOG_SAVE_XML));
		// Settings dialog
		String[] exts = { ApplicationPropertiesUtil.getProperty("adoc") };
		dialog.setFilterExtensions(exts);
		dialog.setText(ResourceUtil.saveAsciiDoc);
		dialog.setFileName("tubame.adoc");
		String outputFilePath = dialog.open();
		if (outputFilePath != null) {
			SaveAsciiDocAction.LOGGER
					.info(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.LOG_START_SAVE_ASCIIDOC));
			try {
				ClassLoader cl = FileManagement.class.getClassLoader();
				InputStream xslInputStream = cl.getResourceAsStream(ResourceUtil.defaultKnowhowToAsciiDocXslPath);
				FileManagement.saveAsciiDoc(targetFilePath, outputFilePath, xslInputStream);
			} catch (Exception e) {
				String errorMsg ="";
				Throwable rootCause = getRootCause(e);
				if(!e.getMessage().equals(rootCause.getMessage())){
					errorMsg = rootCause.getMessage();
				}
					
				ErrorDialog.errorDialogWithStackTrace(PluginUtil.getActiveWorkbenchShell(),
						MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_SAVE_ASCIIDOC)+errorMsg,
						e);
				return false;
			} finally {
				FileManagement.removeTempFileForAsciiDoc(outputFilePath);
			}
			SaveAsciiDocAction.LOGGER
					.info(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.LOG_STOP_SAVE_ASCIIDOC));
			ConfirmDialog.openInformation(PluginUtil.getActiveWorkbenchShell(), ResourceUtil.saveAsciiDoc,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.PERFORM_SAVE_ASCII_DOC));
		}
		return true;
	}

	/**
	 * Determining whether the output file is possible.<br/>
	 * The warning dialog if the file unsaved at the time, and return
	 * false.<br/>
	 * 
	 * @param knowhowMultiPageEditor
	 *            Know-how editor
	 * @return true:Know-how save false:Know-how can not be saved
	 */
	private boolean isOutputFile(MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor) {
		if (knowhowMultiPageEditor.isDirty()) {
			JbmException.outputExceptionLog(LOGGER, null,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.SAVE_PROCESS_KNOWHOW_EDITOR));
			ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(), null,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.SAVE_PROCESS_KNOWHOW_EDITOR));
			return false;
		}
		return true;
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {

	}

	@Override
	public void setActiveEditor(IAction arg0, IEditorPart arg1) {

	}
	
	private Throwable getRootCause(Throwable e) {
	    Throwable cause = null; 
	    Throwable result = e;

	    while(null != (cause = result.getCause())  && (result != cause) ) {
	        result = cause;
	    }
	    return result;
	}

}
