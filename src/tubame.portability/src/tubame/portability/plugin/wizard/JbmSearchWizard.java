/*
 * JbmSearchWizard.java
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.Activator;
import tubame.portability.exception.JbmException;
import tubame.portability.logic.CheckListInformationFactory;
import tubame.portability.logic.KnowhowXmlConvertFactory;
import tubame.portability.logic.search.SearchToolWithProgress;
import tubame.portability.plugin.dialog.ErrorDialog;
import tubame.portability.util.CsvUtil;
import tubame.portability.util.FileUtil;
import tubame.portability.util.FileVisitor;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.PythonUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Wizard class of search input screen.<br/>
 * Take the following pages.<br/>
 * {@link AbstractJbmSelectionPage}
 */
public class JbmSearchWizard extends Wizard implements INewWizard {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JbmSearchWizard.class);

	/**
	 * Screen the Wizard uses
	 */
	private final JbmSearchSelectionPage jbmSearchSelectionPage;

	/**
	 * Get the search screen.<br/>
	 * 
	 * @return jbmSearchSelectionPage
	 */
	public JbmSearchSelectionPage getjbmSearchSelectionPage() {
		return jbmSearchSelectionPage;
	}

	/**
	 * Get the title of the screen.<br/>
	 * 
	 * @return Title of the screen
	 */
	public String getDialogTitle() {
		return ResourceUtil.DIALOG_INFO_SEARCH;
	}

	/**
	 * Get the error terms of the cancellation.<br/>
	 * 
	 * @return Error message of cancellation
	 */
	public String getErrorRunCancel() {
		return MessageUtil.INF_SEARCH_CANCELED;
	}

	/**
	 * Get the error terms of the failure.<br/>
	 * 
	 * @return Error message on failure
	 */
	public String getErrorRunFalse() {
		return MessageUtil.ERR_SEARCH_FAILED;
	}

	/**
	 * Get the wording of the processing is completed.<br/>
	 * 
	 * @return Completion of message processing
	 */
	public String getRunComplete() {
		return MessageUtil.INF_SEARCH_COMPLETE;
	}

	/**
	 * Constructor.<br/>
	 * 
	 * @param window
	 *            Window
	 * @param resource
	 *            Target resource
	 */
	public JbmSearchWizard(IWorkbenchWindow window, IResource resource) {
		super();
		jbmSearchSelectionPage = new JbmSearchSelectionPage(resource);
		super.setWindowTitle(ResourceUtil.DIALOG_SEARCH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		LOGGER.info(String.format(MessageUtil.LOG_INFO_ACTION_P, ResourceUtil.DIALOG_SEARCH,
				MessageUtil.LOG_INFO_BTN_NAME_FINISH, "TargetText=[" + jbmSearchSelectionPage.getTargetText()
						+ "] KnowhowXmlFilePath=[" + jbmSearchSelectionPage.getKnowhowXmlFilePath()
						+ "] OutJbmFileText=[" + jbmSearchSelectionPage.getOutJbmFileText() + "]"));
		boolean validate = jbmSearchSelectionPage.checkFileUpdate();
		// False when it was stuck to validate
		if (!validate) {
			LOGGER.info(String.format(MessageUtil.LOG_INFO_ACTION, jbmSearchSelectionPage.getDialogTitle(),
					MessageUtil.LOG_INFO_BTN_NAME_NO));
			return false;
		}
		try {
			LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_START, MessageUtil.LOG_INFO_PROC_NAME_SEARCH));

			IFile jbmFile = jbmSearchSelectionPage.getJbmFile();
			CheckListInformationFactory.setCheckListInformationPath(jbmFile);

			// Close the file with the same name that is already open
			// TODO: classcast発生するの...closeエディターなので影響なし？
			// PluginUtil.closeEditor(jbmSearchSelectionPage.getOutJbmFileText());

			if(!Activator.isSupportPyPlatform()){
				String pythonExePath = PythonUtil.getPythonExePath();
				if (!new File(pythonExePath).exists()) {
					String message = Activator.getResourceString(JbmSearchWizard.class.getName()
							+ ".errmsg.notfound.python");
					ErrorDialog.openErrorDialog(Activator.getActiveWorkbenchShell(), new Exception(message), message);
					return false;
				}
			}

			// Convert an existing format file knowhowXML
			// knowhow.xml > keywordSearch.csv
			// knowhow.xml > checkListInformation.xml
			KnowhowXmlConvertFactory.getKnowhowXmlConvertFacade().convertSearchFiles(
					jbmSearchSelectionPage.getRealKnowhowXmlPath());

			// ファイル拡張子がxmlで、checkListInformation.xmlとノウハウXMLをリストで取得
			List<String> ignoreFileList = findChecklistInformationXmlAndKnowhowXml(jbmSearchSelectionPage
					.getSelectedTarget().getPath());

			createIgnoreFile(ignoreFileList);

			// 検索キーワードファイルより検索進捗管理するために必要なマップ(PythonUtil.PY_SEARCH_PROGRESS_STATUS_MAP)を作成
			String keywordPath = PluginUtil.getPluginDir() + ApplicationPropertyUtil.SEARCH_KEYWORD_FILE;
			this.createProgressStatusMapForFile(keywordPath);

			// Search process
			final SearchToolWithProgress progress = new SearchToolWithProgress(jbmSearchSelectionPage
					.getSelectedTarget().getPath(),
					PythonUtil.getSearchKeywordFilePath(ApplicationPropertyUtil.SEARCH_KEYWORD_FILE),
					jbmSearchSelectionPage.getRealOutJbmFilePath(), jbmSearchSelectionPage.getSelectedProject());
			final IFile selectedJbmFile = jbmSearchSelectionPage.getSelectedProject().getFile(
					jbmSearchSelectionPage.getOutJbmFilePathExcludeProjectName());
			final IWorkbenchPage activePage = PluginUtil.getActiveWorkbenchWindow().getActivePage();
			Job job = new Job(MessageUtil.INF_START_KNOWHOW_SEARCH) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						progress.run(monitor);
					} catch (InterruptedException e) {
						// Cancellation
						return Status.CANCEL_STATUS;
					} catch (InvocationTargetException e) {
						LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END,
								MessageUtil.LOG_INFO_PROC_NAME_SEARCH), e);
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getErrorRunFalse()
								+ StringUtil.LINE_SEPARATOR + e.getMessage());
					}
					return Status.OK_STATUS;
				}
			};
			
			job.addJobChangeListener(new IJobChangeListener() {

				@Override
				public void sleeping(IJobChangeEvent arg0) {
				}

				@Override
				public void scheduled(IJobChangeEvent arg0) {
				}

				@Override
				public void running(IJobChangeEvent arg0) {
				}

				@Override
				public void done(IJobChangeEvent arg0) {
					IStatus result = arg0.getResult();
					showResults(result,progress, selectedJbmFile);
				}

				@Override
				public void awake(IJobChangeEvent arg0) {
				}

				@Override
				public void aboutToRun(IJobChangeEvent arg0) {
				}
			});
			job.setUser(true);
			job.schedule();

		} 
		catch (IOException e) {
			LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END, MessageUtil.LOG_INFO_PROC_NAME_SEARCH), e);
			PluginUtil.viewErrorDialog(getDialogTitle(), getErrorRunFalse(), e);
			return false;
		}
		catch (JbmException e) {
			LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END, MessageUtil.LOG_INFO_PROC_NAME_SEARCH), e);
			// Conversion failure to existing form of know-how XML file
			PluginUtil.viewErrorDialog(getDialogTitle(),
					getErrorRunFalse() + StringUtil.LINE_SEPARATOR + e.getMessage(), e);
			return false;
		}
		return true;
	}

	protected void showResults(final IStatus result, final SearchToolWithProgress searchToolWithProgress, final IFile selectedJbmFile) {

		Display.getDefault().asyncExec(new Runnable() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void run() {
				if(result.equals(Status.CANCEL_STATUS)){
					LOGGER.debug(MessageUtil.INF_CANCEL);
					LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_CANCEL,
							MessageUtil.LOG_INFO_PROC_NAME_SEARCH));
					PluginUtil.viewInfoDialog(getDialogTitle(), getErrorRunCancel());
					
				}else if(result.getSeverity() ==Status.ERROR){
					PluginUtil.viewErrorDialog(getDialogTitle(),
							getErrorRunFalse() + StringUtil.LINE_SEPARATOR + result.getMessage(),result.getException());
				}else if (result.equals(Status.OK_STATUS) && searchToolWithProgress.isFileOut()) {
					// Open Perspective
					try {
						
						selectedJbmFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
						PluginUtil.openSeachPerspective();
						// Refresh
						PluginUtil.openEditor(selectedJbmFile, PluginUtil.getSearchEditorId());
						LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_END, MessageUtil.LOG_INFO_PROC_NAME_SEARCH));
						PluginUtil.viewInfoDialog(getDialogTitle(), getRunComplete());
					} catch (WorkbenchException e) {
						LOGGER.error(String.format(MessageUtil.LOG_ERR_PROC_ABNORMAL_END_P,
								MessageUtil.LOG_INFO_PROC_NAME_SEARCH, MessageUtil.ERR_SEARCH_PERSPECTIVE_OPEN), e);
						// Failure in perspective related
						PluginUtil.viewErrorDialog(getDialogTitle(), MessageUtil.ERR_SEARCH_PERSPECTIVE_OPEN, e);
					} catch (CoreException e) {
						PluginUtil.viewErrorDialog(getDialogTitle(), MessageUtil.ERR_SEARCH_PERSPECTIVE_OPEN, e);
					}

				} else {
					LOGGER.info(String.format(MessageUtil.LOG_INFO_PROC_END_P, MessageUtil.LOG_INFO_PROC_NAME_SEARCH,
							MessageUtil.INF_SEARCH_NON));
					PluginUtil.viewInfoDialog(getDialogTitle(), MessageUtil.INF_SEARCH_NON);
				}
			}
		});

	}


	private void createIgnoreFile(List<String> ignoreFileList) {
		File parentFile = null;
		try {
			parentFile = new File(PythonUtil.getPythonSearchModulePath()).getParentFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		new File(parentFile, "ignore.list").deleteOnExit();

		if (ignoreFileList.size() == 0) {
			return;
		}
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(new File(parentFile, "ignore.list")));
			for (String filepath : ignoreFileList) {
				bufferedWriter.write(filepath);
				bufferedWriter.newLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.flush();
					bufferedWriter.close();
				} catch (IOException e) {
					LOGGER.error("createIgnoreFile error",e);
					;
				}
			}
		}
	}

	private List<String> findChecklistInformationXmlAndKnowhowXml(final String searchTargetPath) throws IOException {
		final Pattern compile = Pattern.compile("\\.xml");
		final List<String> ignoreList = new ArrayList();

		final File searchTargetFile = new File(searchTargetPath);
		FileVisitor.walkFileTree(searchTargetFile, new FileVisitor() {
			@Override
			public FileVisitResult visitFile(File file) throws IOException {
				String targetFile = null;
				if (isXml(file.getName())) {
					if (isCheckListInformation(file.getName())) {
						targetFile = getRelative(searchTargetFile.getParentFile().getPath() + File.separator, file);
						LOGGER.debug("ignore targetfile:" + targetFile);
						ignoreList.add(targetFile);
					} else if (FileUtil.isKnowHowXml(file)) {
						targetFile = getRelative(searchTargetFile.getParentFile().getPath() + File.separator, file);
						LOGGER.debug("ignore targetfile:" + targetFile);
						ignoreList.add(targetFile);
					}
				}
				return super.visitFile(file);
			}

			private boolean isXml(String fileName) {
				Matcher matcher = compile.matcher(fileName);
				return matcher.find();
			}

			private boolean isCheckListInformation(String fileName) {
				if (fileName.equals(".checkListInformation.xml")) {
					return true;
				} else if (fileName.equals(".checkListInformation_ja.xml")) {
					return true;
				}
				return false;
			}

			private String getRelative(String basePath, File file) {
				int length = basePath.length();
				return file.getAbsolutePath().substring(length);
			}

		});
		return ignoreList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performCancel() {
		LOGGER.info(String.format(MessageUtil.LOG_INFO_ACTION, ResourceUtil.DIALOG_SEARCH,
				MessageUtil.LOG_INFO_BTN_NAME_CANCEL));
		return super.performCancel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		super.addPage(jbmSearchSelectionPage);
		super.addPages();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no oparation.
	}

	private void createProgressStatusMapForFile(String filePath) throws JbmException, IOException {
		Map<String, Integer> progressStatusMap;
		BufferedReader bufferedReader = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(filePath);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, CsvUtil.CHAR_SET);
			bufferedReader = new BufferedReader(inputStreamReader);
			int lineCount = 0;
			progressStatusMap = new HashMap<String, Integer>();
			while (true) {
				lineCount++;
				String line = bufferedReader.readLine();
				if (line == null) {
					break;
				}
				List<String> tokenList = CsvUtil.parseCsv(line);
				String number = tokenList.get(0);
				if (number != null) {
					progressStatusMap.put(number, lineCount);
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
		PythonUtil.PY_SEARCH_PROGRESS_STATUS_MAP = progressStatusMap;
	}
}
