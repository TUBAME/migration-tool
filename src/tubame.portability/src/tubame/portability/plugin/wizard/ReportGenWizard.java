/*
 * JbmConvertWizard.java
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.logic.search.ReportGenSearchToolWithProgress;
import tubame.portability.model.ReportTemplateType;
import tubame.portability.util.FileUtil;
import tubame.portability.util.FileVisitor;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.PythonUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Wizard class in the transformation input screen.<br/>
 * Take the following pages.<br/>
 * {@link JbmConvertSelectionPage}<br/>
 * 
 */
public class ReportGenWizard extends Wizard implements INewWizard {
	
	private static final String REPORT_TPL_FILENAME= ".report_tpl.json";
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReportGenWizard.class);

	/**
	 * Screen the Wizard uses
	 */
	private final ReportGenDirSelectionPage reportGenDirSelectionPage;

	/**
	 * Get the title of the screen.<br/>
	 * 
	 * @return Title of the screen
	 */
	public String getDialogTitle() {
		return ResourceUtil.DIALOG_REPORTGEN;
	}

	/**
	 * Get the error terms of the cancellation.<br/>
	 * 
	 * @return Error message of cancellation
	 */
	public String getErrorRunCancel() {
		return MessageUtil.INF_REPORTGEN_CANCEL;
	}

	/**
	 * Get the error terms of the failure.<br/>
	 * 
	 * @return Error message on failure
	 */
	public String getErrorRunFalse() {
		return MessageUtil.ERR_REPORTGEN_FAILED;
	}

	/**
	 * Get the wording of the processing is completed.<br/>
	 * 
	 * @return Completion of message processing
	 */
	public String getRunComplete() {
		return MessageUtil.INF_REPORTGEN_COMPLETE;
	}

	/**
	 * Constructor.<br/>
	 * 
	 * @param window
	 *            Window
	 * @param resource
	 *            Target resource
	 */
	public ReportGenWizard(IWorkbenchWindow window, IResource resource) {
		super();
		reportGenDirSelectionPage = new ReportGenDirSelectionPage(resource);
		super.setWindowTitle(getDialogTitle());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		LOGGER.info(String.format(
				MessageUtil.LOG_INFO_ACTION_P,
				ResourceUtil.DIALOG_REPORTGEN,
				MessageUtil.LOG_INFO_BTN_NAME_FINISH,
				"TargetText=[" + reportGenDirSelectionPage.getTargetText()
						+ "] OutSourceFolderText=["
						+ reportGenDirSelectionPage.getOutSourceFolderText()
						+ "]"));
		
		boolean validate = reportGenDirSelectionPage.textValidate();
		// False if you get caught in the validate
		if (!validate) {
			return false;
		}
		
		String jbmFilePath = null;
		try {
			LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_START,
					MessageUtil.LOG_INFO_PROC_NAME_REPORTGEN));

//			String target = PluginUtil
//					.getFileFullPath(reportGenDirSelectionPage.getTargetText());
			
			String target = reportGenDirSelectionPage.getSearchTargetDirPath();
			existCheckListInformationForReportGen(target);
			
			// Search process
			ReportGenSearchToolWithProgress progress = new ReportGenSearchToolWithProgress(
					target,
					PythonUtil
							.getSearchKeywordFilePath(ApplicationPropertyUtil.SEARCH_REPORTGEN_KEYWORD_FILE),
					jbmFilePath,reportGenDirSelectionPage.getSelectedProject());
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(
					PluginUtil.getActiveWorkbenchShell());

			PluginUtil.getPluginDir();
			createReportTemplateTypeFile(
					this.reportGenDirSelectionPage.getReportTemplateCombo().getText(),
					PythonUtil.getReportTplPath());
			
			dialog.run(true, true, progress);
			if (progress.isFileOut()) {
				String reportGenerationPath = PythonUtil.getReportGenerationPath();
				
				// copy reportGenerationPath 
				copyReport(reportGenerationPath,reportGenDirSelectionPage.getOutputFullPath());
				// Refresh
				PluginUtil.refreshWorkSpace(dialog.getProgressMonitor());
				// // Open Perspective
				// PluginUtil.openSeachPerspective();
				// // Open the editor
				// PluginUtil.openEditor(
				// jbmSearchSelectionPage.getOutJbmFileText(),
				// PluginUtil.getSearchEditorId());
				LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_END,
						MessageUtil.LOG_INFO_PROC_NAME_REPORTGEN));
				PluginUtil.viewInfoDialog(getDialogTitle(), getRunComplete());
			} else {
				LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_END_P,
						MessageUtil.LOG_INFO_PROC_NAME_REPORTGEN,
						MessageUtil.INF_SEARCH_REPORTGEN_NON));
				PluginUtil.viewInfoDialog(getDialogTitle(),
						MessageUtil.INF_SEARCH_REPORTGEN_NON);
				return false;
			}

		} catch (InterruptedException e) {
			// Cancellation
			LOGGER.debug(MessageUtil.INF_CANCEL);
			LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_CANCEL,
					MessageUtil.LOG_INFO_PROC_NAME_REPORTGEN));
			PluginUtil.viewInfoDialog(getDialogTitle(), getErrorRunCancel());
			return false;
		} catch (InvocationTargetException e) {
			LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
					MessageUtil.LOG_INFO_PROC_NAME_REPORTGEN), e);
			PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse()
					+ StringUtil.LINE_SEPARATOR + e.getMessage(), e);
			return false;
		}catch (Exception e) {
			LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
					MessageUtil.LOG_INFO_PROC_NAME_REPORTGEN), e);
			PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse()+ StringUtil.LINE_SEPARATOR + e.getMessage(), e);
			return false;
		} 
		
		return true;
		// catch (WorkbenchException e) {
		// LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END_P,
		// MessageUtil.LOG_INFO_PROC_NAME_REPORTGEN,
		// MessageUtil.ERR_SEARCH_PERSPECTIVE_OPEN), e);
		// // Failure in perspective related
		// PluginUtil.viewErrorDialog(getDialogTitle(),
		// MessageUtil.ERR_SEARCH_PERSPECTIVE_OPEN, e);
		// return false;
		// }
		// catch (JbmException e) {
		// LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
		// MessageUtil.LOG_INFO_PROC_NAME_SEARCH), e);
		// // Conversion failure to existing form of know-how XML file
		// PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse()
		// + StringUtil.LINE_SEPARATOR + e.getMessage(), e);
		// return false;
		// }

	}

	private void createReportTemplateTypeFile(String reportTypeVal, String outputFullPath) {
		File targetFile = new File(outputFullPath);
		if(targetFile.exists()){
			targetFile.delete();
		}
		 String reportTemplateId = getReportTemplateId(reportTypeVal);
		 if (reportTemplateId== null){
			 throw new IllegalStateException(MessageUtil.ERR_GET_REPORT_TPL);
		 }

		 BufferedOutputStream bufferedOutputStream = null;
		 try {
			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("{ \"template\" : \"" +reportTemplateId + "\"  }");
			byte[] bytes = stringBuffer.toString().getBytes();
			bufferedOutputStream.write(bytes);
			
		} catch (Exception e) {
			throw new IllegalStateException(MessageUtil.ERR_GET_REPORT_TPL,e);
		}finally{
			if (bufferedOutputStream !=null){
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					;
				}
			}
		}
	}
	
	
	private void deleteReportTemplateTypeFile(String outputFullPath){
		File file = new File(outputFullPath);
		if(file.exists()){
			file.delete();
		}
	}

	private String getReportTemplateId(String reportTypeVal) {
		 ReportTemplateType[] values = ReportTemplateType.values();
		 for (ReportTemplateType reportTemplateType : values) {
			if(reportTypeVal.equals(reportTemplateType.getValue())){
				return reportTemplateType.getId();
			}
		}
		return null;
	}

	private void copyReport(final String reportGenerationPath, String target) throws IOException {
		LOGGER.info("reportGenerationPath:"+reportGenerationPath);
		File tubameReportDir = new File(target + File.separator +"tubame-report");
		final String tubameReportDirFullPath = tubameReportDir.getAbsolutePath();
		if(tubameReportDir.exists()){
			removeDir(tubameReportDir.getAbsolutePath(),true);
		}
		
		FileVisitor.walkFileTree(new File(reportGenerationPath), new FileVisitor() {
            @Override
            public FileVisitResult visitFile(File file) throws IOException {
            	String relative = getRelative(reportGenerationPath, file);
            	LOGGER.info("copy to:"+tubameReportDirFullPath+ relative);
            	FileUtil.copyFile(file, new File(tubameReportDirFullPath+ relative));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(File dir)
                    throws IOException {
            	String relative = getRelative(reportGenerationPath, dir);
            	LOGGER.info("make dir:"+tubameReportDirFullPath+relative);
            	File file = new File(tubameReportDirFullPath+relative);
            	file.mkdir();
                return FileVisitResult.CONTINUE;
            }
            
            private String getRelative(String basePath, File file){
            	int length = basePath.length();
            	return file.getAbsolutePath().substring(length);
            }
        });		
	}
	
	
	private void existCheckListInformationForReportGen(String inputDir) throws IOException {
			FileVisitor.walkFileTree(new File(inputDir), new FileVisitor() {
			    @Override
			    public FileVisitResult visitFile(File file) throws IOException {
			    	String name = file.getName();
			    	if(name.matches(".*\\.jbm$")){
			    		File parentFile = file.getParentFile();
			    		File checkTarget = new File(parentFile,ApplicationPropertyUtil.CHECK_LIST_INFORMATION_FILE_PATH);
			    		if(!checkTarget.exists()){
			    			throw new IOException(String.format(MessageUtil.ERR_REPORTGEN_CHKLISTXML_NON,ApplicationPropertyUtil.CHECK_LIST_INFORMATION_FILE_PATH));
			    		}
			    	}
			    	
			        return FileVisitResult.CONTINUE;
			    }

			});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performCancel() {
		LOGGER.info(String.format(MessageUtil.LOG_INFO_ACTION,
				ResourceUtil.DIALOG_REPORTGEN,
				MessageUtil.LOG_INFO_BTN_NAME_CANCEL));
		return super.performCancel();
	}

	private void removeDir(String dirName, boolean flg) {
		File file = new File(dirName);
			File[] fileArray = file.listFiles();
			for (int i = 0; i < fileArray.length; i++) {
				if (!fileArray[i].delete()) {
					removeDir(fileArray[i].getAbsolutePath(), true);
				}
			}
			if (flg) {
				file.delete();
			}
	}
	
	

	// /**
	// * Create a message string result.<br/>
	// *
	// * @param convertProgress
	// * Conversion result information
	// * @param runMessage
	// * Message on the first line
	// * @return String
	// */
	// private String createRunMessage(ConvertWithProgress convertProgress,
	// String runMessage) {
	// int okCount = 0;
	// int ngCount = 0;
	// if (convertProgress != null) {
	// List<MigrationItem> itemList = convertProgress.getTargetList();
	// for (MigrationItem item : itemList) {
	// for (MigrationTarget migrationTarget : item
	// .getMigrationTargets().getMigrationTarget()) {
	// if (migrationTarget.isConvert()) {
	// okCount++;
	// } else {
	// ngCount++;
	// }
	// }
	// }
	// }
	// StringBuilder sb = new StringBuilder();
	// sb.append(runMessage);
	// sb.append(StringUtil.LINE_SEPARATOR);
	// sb.append(String.format(MessageUtil.INF_CONVERT_OUT_MESSAGE, okCount,
	// ngCount));
	// return sb.toString();
	// }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no operation
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		super.addPage(this.reportGenDirSelectionPage);
		super.addPages();
	}
}
