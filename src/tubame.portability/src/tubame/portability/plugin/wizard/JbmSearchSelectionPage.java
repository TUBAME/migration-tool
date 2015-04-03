/*
 * JbmSearchSelectionPage.java
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

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.Activator;
import tubame.portability.plugin.dialog.KnowhowImportDialog;
import tubame.portability.plugin.preferences.KnowledgePreferencePage;
import tubame.portability.util.FileUtil;
import tubame.portability.util.FileVisitor;
import tubame.portability.util.GithubClient;
import tubame.portability.util.GithubClient.GithubClientResponce;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Wizard page class of search input screen.<br/>
 * Define screen label message page.<br/>
 */
public class JbmSearchSelectionPage extends AbstractJbmSelectionPage {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(JbmSearchSelectionPage.class);
	
	/**
	 * Resources to be processed
	 */
	private IResource resource;

	/**
	 * Jbm path of the file to be output
	 */
	private Text outJbmFileText;

	/**
	 * Know-how XML file path
	 */
	private Text knowhowText;

	private File selectedTarget;

	private IProject selectedProject;

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createControl(parent);
		Boolean booleanFromPreferences = Activator
				.getBooleanFromPreferences(KnowledgePreferencePage.PREF_KEY_AUTO_IMPORT_USE,Boolean.TRUE);
		if (booleanFromPreferences == null || booleanFromPreferences == Boolean.TRUE) {
			try {
				if (!existKnowledge()) {
					downloadKnowledge();
				}
			} catch (IOException e) {
				LOGGER.warn(MessageUtil.WARN_KNOWLEDGE_IMPORT_ERR,e);
			}
		}

	}

	/**
	 * Constructor.<br/>
	 * Initializes the resource to be processed.<br/>
	 * 
	 * @param resource
	 *            Resources to be processed
	 */
	public JbmSearchSelectionPage(IResource resource) {
		this.resource = resource;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTargetText() {
		return resource.getProject().getName() + FileUtil.FILE_SEPARATOR + resource.getProjectRelativePath();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConfirmString() {
		return MessageUtil.QUE_SEARCH_UPDATE_CONFIRM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKnowhowXmlFileLabelString() {
		return ResourceUtil.SELECT_KNOWHOW_XML_FILE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getReferenceButtonLabelString() {
		return ResourceUtil.REFERENCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorFileUpdateConfirm() {
		return MessageUtil.INF_SEARCH_CANCELED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOutFilePath() {
		return StringUtil.getText(outJbmFileText);
	}

	/**
	 * Get the know-how XML file path.<br/>
	 * 
	 * @return knowhowXmlText Know-how XML file path
	 */
	public String getKnowhowXmlFilePath() {
		return StringUtil.getText(knowhowText);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitleMessage() {
		return ResourceUtil.WIZ_TITLE_SEARCH;
	}

	/**
	 * Get the path of jbm file to be output.<br/>
	 * 
	 * @return outJbmFileText Jbm path of the file to be output
	 */
	public String getOutJbmFileText() {
		return StringUtil.getText(outJbmFileText);
	}

	/**
	 * Get jbm default file name.<br/>
	 * 
	 * @return Jbm default file name
	 */
	public String getDefaultJbm() {
		return ApplicationPropertyUtil.DEFAULT_JBM_NAME;
	}

	/**
	 * Get the know-how default XML file name.<br/>
	 * 
	 * @return Jbm default file name
	 */
	public String getDefaultXml() {
		return ApplicationPropertyUtil.DEFAULT_KNOWHOW_FILE_NAME;
	}

	/**
	 * Get the extension of the search results file.<br/>
	 * 
	 * @return Extension name of the search results file
	 */
	public String getSearchExtension() {
		return FileUtil.getJbmSuffix();
	}

	/**
	 * Get the extension of know-how xml file.<br/>
	 * 
	 * @return Extension name of the search results file
	 */
	public String getKnowhowExtension() {
		return FileUtil.getXmlSuffix();
	}

	/**
	 * Get the wording of the error If you do not want to search results file
	 * output destination specified.<br/>
	 * 
	 * @return Error message when you do not want to search results file output
	 *         destination specified
	 */
	public String getErrorFileNotValueString() {
		return MessageUtil.ERR_JBM_NOT_ENTERED;
	}

	/**
	 * Get the wording of the error if you do not specify the know-how XML file.<br/>
	 * 
	 * @return Error message when you do not specify the know-how XML file
	 */
	public String getErrorFileNotValueStringKnowhowXml() {
		return MessageUtil.ERR_KNOWHOW_XML_NOT_ENTERED;
	}

	/**
	 * Get the wording of the error if the file format of the search results
	 * file output destination is not correct.<br/>
	 * 
	 * @return Error message if the file format of the search results file
	 *         output destination is not correct
	 */
	public String getErrorFileNotJbm() {
		return MessageUtil.ERR_JBM_NOT_EXTENSION;
	}

	/**
	 * Get the wording of the error if the file format of know-how XML file is
	 * incorrect.<br/>
	 * 
	 * @return Error message if the file format of know-how XML file is
	 *         incorrect
	 */
	public String getErrorFileNotKnowhowXml() {
		return MessageUtil.ERR_KNOWHOW_NOT_EXTENSION;
	}

	/**
	 * Get the wording of the error if the project name in the search results
	 * file output destination does not exist.<br/>
	 * 
	 * @return Error message if the project name in the search results file
	 *         output destination does not exist
	 */
	public String getErrorProjectNotValue() {
		return MessageUtil.ERR_PROJECT_NOT_VALUE;
	}

	/**
	 * Get the wording of the error if the project name of know-how XML file
	 * does not exist.<br/>
	 * 
	 * @return Error message if the project name of know-how XML file does not
	 *         exist
	 */
	public String getErrorProjectNotValueKnowhowXml() {
		return MessageUtil.ERR_PROJECT_NOT_VALUE_KNOWHOW_XML;
	}

	/**
	 * Get the wording of the error if the project of the search results file
	 * output destination is not open.<br/>
	 * 
	 * @return Error message when the project of the search results file output
	 *         destination is not open
	 */
	public String getErrorProjectNotOpen() {
		return MessageUtil.ERR_PROJECT_NOT_OPEN;
	}

	/**
	 * Get the wording of the error if the project know-how of the XML file is
	 * not open.<br/>
	 * 
	 * @return Error message when project know-how of the XML file is not open
	 */
	public String getErrorProjectNotOpenKnowhowXml() {
		return MessageUtil.ERR_PROJECT_NOT_OPEN_KNOWHOW_XML;
	}

	/**
	 * File path of the search results file destination is get the wording of
	 * the error invalid.<br/>
	 * 
	 * @return File path of the search results file destination is the wording
	 *         of the error invalid
	 */
	public String getErrorFilePathInvalid() {
		return MessageUtil.ERR_FILEPATH_INVALID;
	}

	/**
	 * Get the wording of the error if the know-how XML file does not exist.<br/>
	 * 
	 * @return The wording of the error if the know-how XML file does not exist
	 */
	public String getErrorFileNotFoundKnowhowXml() {
		return MessageUtil.ERR_READ_KNOWHOW_XML;
	}

	/**
	 * Get search results file selection label string to be displayed in this
	 * screen.<br/>
	 * 
	 * @return Search result file selection label string
	 */
	public String getOutFileLabelString() {
		return ResourceUtil.SELECT_FILE_SEARCH_RESULT;
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
	 * {@inheritDoc}
	 */
	@Override
	public void addControl(Composite composite) {
		// Know-how XML selection area
		setKnowhowXmlFileArea(composite);

		// Search result output destination selection area
		setOutJbmFileArea(composite);

		setPageComplete(false);
		super.textValidate();
	}

	/**
	 * Create a search result file output destination.<br/>
	 * 
	 * @param container
	 *            Container
	 */
	private void setOutJbmFileArea(Composite container) {
		Group group = new Group(container, SWT.SHADOW_NONE);
		group.setText(getOutFileLabelString());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));

		// Search results file output destination selection text
		outJbmFileText = new Text(group, SWT.BORDER);
		outJbmFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Get the project
		outJbmFileText.setText(resource.getProject().getName() + FileUtil.FILE_SEPARATOR + getDefaultJbm());
		addModifiListener(outJbmFileText);

		// Search results file output destination selection button
		Button button = new Button(group, SWT.NULL);
		button.setText(getReferenceButtonLabelString());

		button.addSelectionListener(new BrowseOutputJbmButtonSelectionListener(outJbmFileText));

	}

	/**
	 * Create a know-how XML file selection area.<br/>
	 * 
	 * @param container
	 *            Container
	 */
	private void setKnowhowXmlFileArea(Composite container) {
		Group group = new Group(container, SWT.SHADOW_NONE);
		group.setText(getKnowhowXmlFileLabelString());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(3, false));

		// XML know-how selected text
		GridData gridDataDirectory = new GridData(GridData.FILL_HORIZONTAL);
		knowhowText = new Text(group, SWT.BORDER);
		knowhowText.setLayoutData(gridDataDirectory);

		// Set initial value to the project name that is selected in the
		// Explorer currently
		knowhowText.setText(resource.getProject().getName() + FileUtil.FILE_SEPARATOR + getDefaultXml());
		addModifiListener(knowhowText);

		// Search target folder selection button
		Button Button = new Button(group, SWT.NULL);
		Button.setText(getReferenceButtonLabelString());
		// Set the operation when the button is pressed
		Button.addSelectionListener(new BrowseFileButtonSelectionListener(this, knowhowText, getKnowhowExtension()));

	
		
		// Search target folder selection button
		Button importButton = new Button(group, SWT.NULL);
		importButton.setText(ResourceUtil.KNOWHOW_IMPORT_LABEL);
		final String pjName = this.resource.getProject().getName();
		// Set the operation when the button is pressed
		importButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				KnowhowImportDialog knowhowImportDialog = new KnowhowImportDialog(getShell(), selectedProject);
				int open = knowhowImportDialog.open();
				if (open == Window.OK) {
					File importedFile = knowhowImportDialog.getImportedFile();
					try {
						String relative = getRelative(PluginUtil.getKnowledgeDir(), importedFile);
						knowhowText.setText(pjName + File.separator + relative);
					} catch (IOException e1) {
						throw new IllegalStateException(e1);
					}
				}
			}

			private String getRelative(String basePath, File file) {
				int length = basePath.length();
				return file.getAbsolutePath().substring(length);
			}

		});
		
		File knowledgeDir = null;
		try {
			knowledgeDir = new File(PluginUtil.getResolvedPluginDir()+ ApplicationPropertyUtil.KNOWLEDGE_DIR);
		} catch (IOException e2) {
			;
		}
		
		if(knowledgeDir!= null && !knowledgeDir.exists()){
			importButton.setVisible(false);
		}

		IPath rawLocation = resource.getRawLocation();
		if (rawLocation != null) {
			selectedTarget = rawLocation.toFile();
		} else {
			// 一般プロジェクトで、プロジェクト選択している場合は、nullになる場合があるので、
			if (resource instanceof IProject) {
				IProject project = (IProject) resource;
				selectedTarget = project.getLocation().toFile();
			}
		}
		selectedProject = resource.getProject();

	}

	public File getSelectedTarget() {
		return selectedTarget;
	}

	public void setSelectedTarget(File selectedTarget) {
		this.selectedTarget = selectedTarget;
	}

	public IProject getSelectedProject() {
		return selectedProject;
	}

	public void setSelectedProject(IProject selectedProject) {
		this.selectedProject = selectedProject;
	}

	public File getSelectedProjectFile() {
		return selectedTarget;
	}

	public void setSelectedProjectFile(File selectedProjectFile) {
		this.selectedTarget = selectedProjectFile;
	}

	public String getRealKnowhowXmlPath() {
		IFile file = null;
		if (knowhowText != null) {
			// コンポジットに設定されているのはプロジェクト名が余計に付与しているので、プロジェクト名を削除したものを取得する
			String knowhowXmlExcludeProjectName = getPathExcludeProjectName(knowhowText);
			file = selectedProject.getFile(knowhowXmlExcludeProjectName);
		}
		if (file != null) {
			return file.getLocation().toFile().getPath();
		}
		return null;
	}

	public String getRealOutJbmFilePath() {
		IFile file = null;
		if (outJbmFileText != null) {
			// コンポジットに設定されているのはプロジェクト名が余計に付与しているので、プロジェクト名を削除したものを取得する
			String outJbmFilePathExcludeProjectName = getPathExcludeProjectName(outJbmFileText);
			file = selectedProject.getFile(outJbmFilePathExcludeProjectName);
		}
		if (file != null) {
			return file.getLocation().toFile().getPath();
		}
		return null;
	}

	private String getPathExcludeProjectName(Text knowhowFileName) {
		String projectName = selectedProject.getName();
		return knowhowFileName.getText().split(projectName)[1];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addTextvalidate() {
		// Check of know-how XML file /////////////////////////////////////
		// Not entered check
		if (StringUtil.isEmpty(getKnowhowXmlFilePath())) {
			setErrorMessage(getErrorFileNotValueStringKnowhowXml());
			return false;
		}
		// Extension check
		if (!StringUtil.isFileExtension(getKnowhowXmlFilePath(), getKnowhowExtension())) {
			setErrorMessage(getErrorFileNotKnowhowXml());
			return false;
		}
		// Project reality check
		if (!PluginUtil.projectExists(getKnowhowXmlFilePath())) {
			setErrorMessage(getErrorProjectNotValueKnowhowXml());
			return false;
		}
		// Project open check
		if (!isProjectOpen(getKnowhowXmlFilePath(), getErrorProjectNotOpenKnowhowXml())) {
			return false;
		}
		// Search results file existence check
		String realKnowhowXmlPath = this.getRealKnowhowXmlPath();

		if (!new File(realKnowhowXmlPath).exists()) {
			setErrorMessage(getErrorProjectNotValueKnowhowXml());
			return false;
		}
		// if (!FileUtil.fileExists(realKnowhowXmlPath)) {
		// setErrorMessage(getErrorFileNotFoundKnowhowXml());
		// return false;
		// }

		// Check the search results file output destination
		// //////////////////////////////////
		// Not entered check
		if (StringUtil.isEmpty(getOutJbmFileText())) {
			setErrorMessage(getErrorFileNotValueString());
			return false;
		}
		// Extension check
		if (!StringUtil.isFileExtension(getOutJbmFileText(), getSearchExtension())) {
			setErrorMessage(getErrorFileNotJbm());
			return false;
		}
		// Project reality check
		if (!PluginUtil.projectExists(getOutJbmFileText())) {
			setErrorMessage(getErrorProjectNotValue());
			return false;
		}
		// Project open check
		if (!isProjectOpen(getOutJbmFileText(), getErrorProjectNotOpen())) {
			return false;
		}

		// Search results file existence check
		if (!FileUtil.getFile(getOutJbmFileText()).exists()) {
			// If the file path is invalid
			if (!FileUtil.isInputFileNormal(getOutJbmFileText())) {
				setErrorMessage(getErrorFilePathInvalid());
				return false;
			}
		}
		return true;
	}

	/**
	 * If the output destination entered already exists, overwrite confirmation
	 * dialog is displayed.<br/>
	 * If the output destination entered already exists, you can view the
	 * overwrite confirmation dialog.<br/>
	 * The return false if the selected No, true if it is bur selected in the
	 * dialog.<br/>
	 * The return false if Cancel is selected.<br/>
	 * 
	 * @return true:It was bur selected in the dialog false:No is selected
	 */
	public boolean checkFileUpdate() {
		// Overwrite warning
		if (FileUtil.getFile(getOutJbmFileText()).exists()) {
			if (!PluginUtil.viewQuestionDialog(getDialogTitle(), getConfirmString())) {
				setErrorMessage(getErrorFileUpdateConfirm());
				return false;
			} else {
				PluginUtil.closeEditor(FileUtil.getFile(getOutJbmFileText()).getPath());
				FileUtil.getFile(getOutJbmFileText()).delete();

			}
		}
		return true;
	}

	public IFile getJbmFile() {
		String jbmFilePath = this.outJbmFileText.getText();
		String projectName = PluginUtil.getProjectName(this.outJbmFileText.getText());
		IProject project = PluginUtil.getProject(projectName);
		String substring = jbmFilePath.substring(projectName.length() + 1);
		IFile file = project.getFile(substring);
		return file;
	}

	public String getOutJbmFilePathExcludeProjectName() {
		return this.getPathExcludeProjectName(this.outJbmFileText);
	}

	private void downloadKnowledge() throws IOException {
		final String donwloadEndpoint = ApplicationPropertyUtil.MIGRATION_KNOWLEDGE_DOWNLOAD_ENDPOINT_URL;
		GithubClientResponce resp = null;
		try {
			resp = new GithubClient().execute(donwloadEndpoint, GithubClient.SUPPORTED_CONTENT.ZIP);
			if (resp != null && resp.getInputStream() != null) {
				migrationKnowledgeUnzip((ZipInputStream) resp.getInputStream());
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (resp != null && resp.getInputStream() != null) {
				try {
					resp.getInputStream().close();
				} catch (IOException e) {
					;
				}
			}
		}
	}

	private void migrationKnowledgeUnzip(ZipInputStream zipInputStream) throws IOException {
		do {
			ZipEntry zipentry = zipInputStream.getNextEntry();
			if (zipentry == null) {
				break;
			} else {
				String lang = ApplicationPropertyUtil.KNOWLEDGE_DIR.split("/")[2] + "/";
				int langLength = lang.length();
				if (zipentry.getName().contains(lang) && !zipentry.getName().contains("programlisting/")) {
					int indexOf = zipentry.getName().indexOf(lang);
					File targetFile = new File(PluginUtil.getResolvedPluginDir()
							+ ApplicationPropertyUtil.KNOWLEDGE_DIR, zipentry.getName().substring(indexOf + langLength,
							zipentry.getName().length()));
					if (zipentry.isDirectory()) {
						targetFile.mkdirs();
					} else {
						FileUtil.copyZipFile(zipInputStream, targetFile);
					}
				}
			}
		} while (true);
	}

	private boolean existKnowledge() throws IOException  {
		Boolean booleanFromPreferences = Activator
				.getBooleanFromPreferences(KnowledgePreferencePage.PREF_KEY_AUTO_IMPORTED_CACHE_USE,Boolean.TRUE);
		if (booleanFromPreferences == Boolean.FALSE) {
				// キャッシュ無効のため、キャッシュディレクトリを削除する。
				deleteKnowledgeCacheDir();
				return false;
		}
		
		return new File(PluginUtil.getKnowledgeDir()).exists();
	}

	private void deleteKnowledgeCacheDir() throws IOException {
		File dir = new File(PluginUtil.getResolvedPluginDir()+ ApplicationPropertyUtil.KNOWLEDGE_DIR);
		if(dir.exists()){
			FileVisitor.walkFileTree(new File(PluginUtil.getKnowledgeDir()), new FileVisitor() {

				final @Override public FileVisitResult visitFile(File file) throws IOException {
					file.delete();
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(File dir) throws IOException {
					return FileVisitResult.CONTINUE;
				}

			});
			
		}
		
	}

}
