/*
 * PluginUtil.java
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
package tubame.portability.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import tubame.portability.Activator;
import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.model.DifficultyEnum;
import tubame.portability.plugin.view.CheckListInformationView;
import tubame.portability.plugin.view.ConvertView;
import tubame.portability.plugin.view.WorkStatusView;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Utility class that specializes in Plugin.<br/>
 * Set or get operation Plugin properties etc.<br/>
 * 
 */
public class PluginUtil {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PluginUtil.class);

    /**
     * ID of the HTML view
     */
    private static final String VIEW_HTML_ID = "HtmlGuideView"; //$NON-NLS-1$

    /**
     * ID of the check list view
     */
    private static final String VIEW_CHECKLIST_ID = "CheckListInformationView"; //$NON-NLS-1$

    /**
     * ID of work status view
     */
    private static final String VIEW_WORKSTATUS_ID = "WorkStatusView"; //$NON-NLS-1$

    /**
     * ID of the conversion status view
     */
    private static final String VIEW_CONVERT_STATUS_ID = "ConvertView"; //$NON-NLS-1$

    /**
     * ID of the search editor
     */
    private static final String EDITOR_SEARCH_ID = "JbmEditorPart";

    /**
     * Search Perspective
     */
    private static final String PERSPECTIVE_SEARCH = "SearchPerspective";
    /**
     * Difficulty low marker
     */
    private static final String DIFFICULTY_LOW_MARKER = "tubame.portability.DifficultyLowMarker"; //$NON-NLS-1$

    /**
     * Difficulty middle marker
     */
    private static final String DIFFICULTY_MIDDLE_MARKER = "tubame.portability.DifficultyMiddleMarker"; //$NON-NLS-1$

    /**
     * Difficulty high marker
     */
    private static final String DIFFICULTY_HIGH_MARKER = "tubame.portability.DifficultyHighMarker"; //$NON-NLS-1$

    /**
     * Unknown marker difficulty
     */
    private static final String DIFFICULTY_UNKNOWN_MARKER = "tubame.portability.DifficultyUnknownMarker"; //$NON-NLS-1$

    /**
     * Other marker difficulty
     */
    private static final String DIFFICULTY_NOTTRN_MARKER = "tubame.portability.DifficultyNotTrnMarker"; //$NON-NLS-1$

    /**
     * Error Log view
     */
    private static final String ERROR_LOG_VIEW_ID = "org.eclipse.pde.runtime.LogView"; //$NON-NLS-1$

    /**
     * Default constructor.<br/>
     * Disable the instance generation from other class.<br/>
     * 
     */
    private PluginUtil() {
        // no operation
    }

    /**
     * Get the HTML view ID.<br/>
     * 
     * @return ID of the HTML view
     */
    public static String getHtmlViewId() {
        return PluginUtil.VIEW_HTML_ID;
    }

    /**
     * Get the checklist information view ID.<br/>
     * 
     * @return ID of the HTML view
     */
    public static String getCheckListViewId() {
        return PluginUtil.VIEW_CHECKLIST_ID;
    }

    /**
     * Get the conversion status view ID.<br/>
     * 
     * @return ID of the conversion status view
     */
    public static String getConvertStatusViewId() {
        return PluginUtil.VIEW_CONVERT_STATUS_ID;
    }

    /**
     * Get work status information view ID.<br/>
     * 
     * @return ID of the HTML view
     */
    public static String getWorkStatusViewId() {
        return PluginUtil.VIEW_WORKSTATUS_ID;
    }

    /**
     * Get a search editor ID.<br/>
     * 
     * @return ID of the search editor
     */
    public static String getSearchEditorId() {
        return PluginUtil.EDITOR_SEARCH_ID;
    }

    /**
     * Get the conversion status view.<br/>
     * 
     * @return Conversion status view
     * @throws JbmException
     *             Perspective if open fails
     */
    public static ConvertView getConvertView() throws JbmException {
        return (ConvertView) PluginUtil
                .getViewPart(PluginUtil.VIEW_CONVERT_STATUS_ID);
    }

    /**
     * Show the conversion status view.<br/>
     */
    public static void showConvertView() {
        try {
            PluginUtil.getActivePage().showView(
                    PluginUtil.VIEW_CONVERT_STATUS_ID);
        } catch (PartInitException e) {
            JbmException.outputExceptionLog(e, LOGGER, ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.ERR_WORK_STATUS_VIEW_OPEN,
                            PluginUtil.VIEW_CONVERT_STATUS_ID });
        }
    }

    /**
     * Get the checklist view.<br/>
     * 
     * @return Check list view
     * @throws JbmException
     *             Perspective if open fails
     */
    public static CheckListInformationView getCheckListInformationView()
            throws JbmException {
        return (CheckListInformationView) PluginUtil.getViewPart(PluginUtil
                .getCheckListViewId());
    }

    /**
     * Get the work situation view.<br/>
     * 
     * @return Work status view
     * @throws JbmException
     *             Perspective if open fails
     */
    public static WorkStatusView getWordStatusView() throws JbmException {
        return (WorkStatusView) PluginUtil.getViewPart(PluginUtil
                .getWorkStatusViewId());
    }

    /**
     * View error log view.<br/>
     * 
     * @throws JbmException
     *             Perspective if open fails
     */
    public static void showErrorLogView() throws JbmException {

        PluginUtil.getViewPart(PluginUtil.ERROR_LOG_VIEW_ID);
    }

    /**
     * Get the view.<br/>
     * 
     * @param id
     *            View ID
     * 
     * @return View
     * @throws JbmException
     *             Perspective if open fails
     */
    private static IViewPart getViewPart(String id) throws JbmException {
        try {
            IViewPart iViewPart = PluginUtil.isOpendView(id);
            if (iViewPart != null) {
                return iViewPart;
            }
            return PluginUtil.getActivePage().showView(id);
        } catch (WorkbenchException e) {
            throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR, new String[] {
                    MessageUtil.ERR_WORK_STATUS_VIEW_OPEN, id });
        }
    }

    /**
     * The decision whether or open the specified view.<br/>
     * 
     * @param viewId
     *            Check view ID
     * @return true:It is already open false:Not open
     */
    private static IViewPart isOpendView(String viewId) {
        IViewReference[] views = PluginUtil.getActivePage().getViewReferences();
        for (IViewReference view : views) {
            if (viewId.equals(view.getId())) {
                return view.getView(false);
            }
        }
        return null;
    }

    /**
     * Get the Plugin directory.<br/>
     * 
     * @return Self Plugin directory
     * @throws IOException
     *             IO exception
     */
    public static String getPluginDir() throws IOException {
        String pluginDirectory = StringUtil.EMPTY;
        try {
            URL entry = Activator.getDefault().getBundle()
                    .getEntry(FileUtil.FILE_SEPARATOR);
            pluginDirectory = FileLocator.resolve(entry).getPath();
        } catch (IOException e) {
            JbmException
                    .outputExceptionLog(
                            e,
                            LOGGER,
                            ERROR_LEVEL.ERROR,
                            new String[] { MessageUtil.ERR_PLUGINUTIL_PLUGIN_DIRECTORY_GET });
            throw e;
        }
        return pluginDirectory;
    }

    /**
     * Get the Plugin directory in consideration of the OS.<br/>
     * On Windows, you get the path that you have deleted the relevant part for
     * "/" to enter the front of the device name.<br/>
     * 
     * @return Self Plugin directory
     * @throws IOException
     *             IO exception
     */
    public static String getResolvedPluginDir() throws IOException {
        String pluginDirectory = getPluginDir();
        if (System.getProperty("os.name").contains("Windows")
                && pluginDirectory.startsWith("/")) {
            pluginDirectory = pluginDirectory.substring(1);
        }
        return pluginDirectory;
    }
    
    

    /**
     * Get ActiveWorkbenchWindow.
     * 
     * @return IWorkbenchWindow
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    /**
     * Get ActiveWorkbenchPage.
     * 
     * @return IWorkbenchPage
     */
    public static IWorkbenchPage getActivePage() {
        return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
                .getActivePage();
    }

    /**
     * Get ActiveWorkbenchShell.
     * 
     * @return shell
     */
    public static Shell getActiveWorkbenchShell() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        return window != null ? window.getShell() : getStandardDisplay()
                .getActiveShell();
    }

    /**
     * Get the associated display thread are you calling this method <br/>
     * if you are in possession of the relevant display,<br/>
     * you get the default display if not in possession.<br/>
     * 
     * @return Display
     */
    public static Display getStandardDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;
    }

    /**
     * Display the Wizard screen.<br/>
     * 
     * @param wizard
     *            Wizard screen
     */
    public static void showWizardDialog(Wizard wizard) {
        WizardDialog dialog = new WizardDialog(
                PluginUtil.getActiveWorkbenchShell(), wizard);
        dialog.setHelpAvailable(false);
        dialog.open();
    }

    /**
     * Generate IFile than relative to the container and IContainer.<br/>
     * 
     * @param container
     *            IContainer
     * @param target
     *            Relative to the container
     * @return IFile
     */
    public static IFile createIFile(IContainer container, String target) {
        if (!StringUtil.isEmpty(target)) {
            Path path = new Path(target);
            if (container.exists(path)) {
                File targetFile = new File(container.getLocationURI().getPath()
                        + FileUtil.FILE_SEPARATOR + path.toOSString());
                if (targetFile.exists() && targetFile.isFile()) {
                    return container.getFile(path);
                }
            }
        }
        return null;
    }

    /**
     * Generate IFolder than relative to the container and IContainer.<br/>
     * 
     * @param container
     *            IContainer
     * @param target
     *            Relative to the container
     * @return IFolder
     */
    public static IFolder createIFolder(IContainer container, String target) {
        Path path = new Path(target);
        if (container.exists(path)) {
            File targetFile = new File(container.getLocationURI().getPath()
                    + FileUtil.FILE_SEPARATOR + path.toOSString());
            if (targetFile.exists() && targetFile.isDirectory()) {
                if (!PluginUtil.isProject(target)) {
                    return container.getFolder(path);
                }
            }
        }
        return null;
    }

    /**
     * Get the difficulty marker ID corresponding to the degree of difficulty
     * code.<br/>
     * 
     * @param code
     *            Difficulty Code
     * @return Difficulty marker ID
     */
    public static String getDifficultyMarkerId(String code) {
        String marker = StringUtil.EMPTY;
        switch (DifficultyEnum.get(code)) {
        case HIGH:
            marker = PluginUtil.DIFFICULTY_HIGH_MARKER;
            break;
        case LOW:
            marker = PluginUtil.DIFFICULTY_LOW_MARKER;
            break;
        case MIDDLE:
            marker = PluginUtil.DIFFICULTY_MIDDLE_MARKER;
            break;
        case UNKNOWN:
            marker = PluginUtil.DIFFICULTY_UNKNOWN_MARKER;
            break;
        case NOT_TRN:
            marker = PluginUtil.DIFFICULTY_NOTTRN_MARKER;
            break;
        default:
            return marker;
        }
        return marker;
    }

    /**
     * Get the selected project. If the project you have selected does not
     * exist, return null.<br/>
     * 
     * @return Project selection
     */
    public static IProject getSelectedProject() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        ISelection selection = window.getActivePage().getSelection();
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection)
                    .getFirstElement();
            if (element instanceof IResource) {
                return ((IResource) element).getProject();
            }
        }
        return null;

    }

    /**
     * Get the editor Active.<br/>
     * 
     * @return Editor Active
     */
    public static IEditorPart getActiveEditor() {
        return PluginUtil.getActivePage().getActiveEditor();
    }

    /**
     * It displays an error message.<br/>
     * 
     * @param title
     *            Title of the dialog
     * @param message
     *            Message of the dialog
     * @param e
     *            Throwable
     */
    public static void viewErrorDialog(String title, String message, Throwable e) {
        MessageDialog.openError(getActiveWorkbenchShell(), title, message);
    }

    /**
     * Show the information message.<br/>
     * 
     * @param title
     *            Title of the dialog
     * @param message
     *            Message of the dialog
     */
    public static void viewInfoDialog(String title, String message) {
        MessageDialog
                .openInformation(getActiveWorkbenchShell(), title, message);
    }

    /**
     * View question dialog for selecting the OK / Cancel button.<br/>
     * 
     * @param title
     *            Title of the dialog
     * @param message
     *            Message of the dialog
     * @return boolean true:OK false:Cancellation
     */
    public static boolean viewQuestionDialog(String title, String message) {
        if (MessageDialog.openQuestion(getActiveWorkbenchShell(), title,
                message)) {
            return true;
        }
        return false;
    }

    /**
     * It displays a warning message.<br/>
     * 
     * @param title
     *            Title of the dialog
     * @param message
     *            Message of the dialog
     */
    public static void viewWarningDialog(String title, String message) {
        MessageDialog.openWarning(getActiveWorkbenchShell(), title, message);
    }

    /**
     * Get the full path to the file path from the workspace root.<br/>
     * 
     * @param filePath
     *            File Path
     * @return String
     */
    public static String getFileFullPath(String filePath) {
        return PluginUtil.getWorkspaceRoot() + FileUtil.FILE_SEPARATOR
                + filePath;
    }

    /**
     * Get the full path to the file path from the workspace root.<br/>
     * 
     * @return String
     */
    public static String getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot().getLocation()
                .toOSString();
    }

    /**
     * Check project name in the file path from the workspace root exists.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @return True if if present,It returns false if not exist
     */
    public static boolean projectExists(String filePath) {
        String projectName = PluginUtil.getProjectName(filePath);
        // Path of the workspace If the received empty
        if (projectName.equals(StringUtil.EMPTY)) {
            return false;
        }
        if (!ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)
                .exists()) {
            return false;
        }
        return true;
    }

    /**
     * Get the project name from the full path from the workspace root.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @return Project name
     */
    public static String getProjectName(String filePath) {
        String temp = filePath;
        temp = temp.replaceAll("\\\\", StringUtil.SLASH);
        if (!temp.contains(StringUtil.SLASH)) {
            return temp;
        }
        return temp.substring(0, temp.indexOf(StringUtil.SLASH));
    }

    /**
     * Create a IProject from the project name, acquisition.<br/>
     * 
     * @param projectName
     *            Project name
     * @return IProject
     */
    public static IProject getProject(String projectName) {
        if (StringUtil.isEmpty(projectName)) {
            return null;
        }
        return ResourcesPlugin.getWorkspace().getRoot()
                .getProject(PluginUtil.getProjectName(projectName));
    }

    /**
     * Acquisition projects included in the file path from the root workspace is
     * currently open or.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @return True or so if you have open,It returns false if not open
     */
    public static boolean isProjectOpen(String filePath) {
        if (ResourcesPlugin.getWorkspace().getRoot()
                .getProject(PluginUtil.getProjectName(filePath)).isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Acquisition whether equivalent to the project name from the workspace
     * root.<br/>
     * 
     * @param str
     *            Comparison string
     * @return True if if equivalence,False if equivalence
     */
    public static boolean isProject(String str) {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
                .getProjects();
        for (IProject project : projects) {
            if (project.getName().equals(str)) {
                return true;
            }
            if ((project.getName() + FileUtil.FILE_SEPARATOR).equals(str)) {
                return true;
            }
            if ((project.getName() + StringUtil.SLASH).equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Open search results perspective.<br/>
     * 
     * @throws WorkbenchException
     *             Perspective Open failure
     */
    public static void openSeachPerspective() throws WorkbenchException {
        PluginUtil.openPerspective(PluginUtil.PERSPECTIVE_SEARCH);
    }

    /**
     * Open the perspective that you specify.<br/>
     * 
     * @param perspectiveId
     *            Perspective ID
     * @throws WorkbenchException
     *             Perspective Open failure
     */
    private static void openPerspective(String perspectiveId)
            throws WorkbenchException {
        // Open Perspective
        PlatformUI.getWorkbench().showPerspective(perspectiveId,
                getActiveWorkbenchWindow());
    }

    /**
     * Open the file in the editor ID specified.<br/>
     * 
     * @param filePath
     *            File path (relative to the workspace root)
     * @param editorId
     *            ID of the editor
     * @throws PartInitException
     *             Exception
     */
    public static void openEditor(String filePath, String editorId)
            throws PartInitException {

        IFile file = PluginUtil.createIFile(ResourcesPlugin.getWorkspace()
                .getRoot(), filePath);
        // Open the editor
        IDE.openEditor(PluginUtil.getActiveWorkbenchWindow().getActivePage(),
                file, editorId);
    }

    public static void openEditor(IFile filePath, String editorId)
            throws PartInitException {
        // Open the editor
        IDE.openEditor(PluginUtil.getActiveWorkbenchWindow().getActivePage(),
        		filePath, editorId);
    }
    
    /**
     * Close the editor that is currently open the specified file.<br/>
     * 
     * @param filePath
     *            File path (relative to the workspace root)
     */
    public static void closeEditor(String filePath) {
        String filePathReplacedBySlash = StringUtil.SLASH
                + filePath.replace(FileUtil.FILE_SEPARATOR, StringUtil.SLASH);

        // Get the editor of all open
        IEditorReference[] editors = PluginUtil.getOpenEditors();
        for (IEditorReference editor : editors) {

            try {
                String openedFile = ((IFileEditorInput) editor.getEditorInput())
                        .getFile().getFullPath().toString();
                // Close the editor If a file with the same name is already open

                if (filePathReplacedBySlash.equals(openedFile)) {
                    PluginUtil.getActiveWorkbenchWindow().getActivePage()
                            .closeEditor(editor.getEditor(false), false);
                }
            } catch (PartInitException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get an array of the editor of all currently open.<br/>
     * 
     * @return IEditorReference[]
     */
    public static IEditorReference[] getOpenEditors() {
        return PluginUtil.getActivePage().getEditorReferences();
    }

    /**
     * Retrieve files that you specify whether you have already opened in the
     * editor.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @return boolean True as long as open,False if you do not open
     */
    public static boolean isOpenWithEditor(String filePath) {
        String filePathReplacedBySlash = StringUtil.SLASH
                + filePath.replace(FileUtil.FILE_SEPARATOR, StringUtil.SLASH);

        // Get the editor of all open
        for (IEditorReference editor : PluginUtil.getOpenEditors()) {

            try {
                String openedFile = ((IFileEditorInput) editor.getEditorInput())
                        .getFile().getFullPath().toString();
                if (filePathReplacedBySlash.equals(openedFile)) {
                    return true;
                }
            } catch (PartInitException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    /**
     * Editor of the file name passed if opened, acquired in IEditorReference
     * object.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @return IEditorInput
     */
    public static IEditorReference getEditorReference(String filePath) {
        for (IEditorReference editor : PluginUtil.getOpenEditors()) {
            try {
                if (filePath.equals(editor.getEditorInput().getToolTipText())) {
                    return editor;
                }
            } catch (PartInitException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * And registered in the error log the Exception information.<br/>
     * 
     * @param throwable
     *            Exception
     */
    public static void logException(Throwable throwable) {
        Throwable e = throwable;
        if (e instanceof InvocationTargetException) {
            e = ((InvocationTargetException) e).getTargetException();
        }
        IStatus status = null;
        if (e instanceof CoreException) {
            status = ((CoreException) e).getStatus();
        } else {
            String message = e.getMessage();
            if (message == null) {
                message = e.toString();
            }
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.OK,
                    message, e);
        }
        Bundle bundle = Platform.getBundle("org.eclipse.update.ui");
        Platform.getLog(bundle).log(status);
    }

    /**
     * File synchronization (refresh).<br/>
     * 
     * @param fileFullPath
     *            File path to refresh
     * @param monitor
     *            Monitor
     */
    public static void refreshFile(String fileFullPath, IProgressMonitor monitor) {
        IPath location = Path.fromOSString(fileFullPath);
        IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
                .getFileForLocation(location);
        try {
            if (iFile != null) {
                iFile.refreshLocal(IResource.DEPTH_ZERO, monitor);
            }
        } catch (CoreException e) {
            JbmException.outputExceptionLog(e, LOGGER, ERROR_LEVEL.INFO);
        }
    }

    /**
     * Synchronization of workspace (refresh).<br/>
     * 
     * @param monitor
     *            Monitor
     */
    public static void refreshWorkSpace(IProgressMonitor monitor) {
        try {
            ResourcesPlugin.getWorkspace().getRoot()
                    .refreshLocal(IResource.DEPTH_INFINITE, monitor);
        } catch (CoreException e) {
            JbmException.outputExceptionLog(e, LOGGER, ERROR_LEVEL.INFO);
        }
    }

    /**
     * Get the Font to be used in the search results editing screen.<br/>
     * 
     * @return Font
     */
    public static Font getJbmEditorFont() {
        return new Font(null, "Tahoma", 9, SWT.BOLD);
    }

	/**
	 * 
	 * @return
	 */
	public static Map<String,File> getKnowledgeNamesFromPluginResource() {
		final Map<String,File> map = new HashMap();
		
		try {
			String knowledgeDir = PluginUtil.getKnowledgeDir();
			FileVisitor.walkFileTree(new File(knowledgeDir), new FileVisitor() {
	            @Override
	            public FileVisitResult visitFile(File file) throws IOException {
	            	
	            	if(file.getName().endsWith("xml")){
	            		if(FileUtil.isKnowHowXml(file)){
	            			try {
								String portabilityKnowhowTitle = getPortabilityKnowhowTitle(file);
								if(!map.containsKey(portabilityKnowhowTitle)){
									map.put(portabilityKnowhowTitle, file);
								}
							} catch (Exception e) {
								throw new IllegalStateException(e);
							}
	            		}
	            	}
	                return FileVisitResult.CONTINUE;
	            }

				@Override
	            public FileVisitResult preVisitDirectory(File dir)
	                    throws IOException {
	                return FileVisitResult.CONTINUE;
	            }
				
				private String getPortabilityKnowhowTitle(File file) throws Exception {
					DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document doc = builder.parse(file);
					XPath xpath = XPathFactory.newInstance().newXPath();
					Node evaluate = (Node) xpath.evaluate("//*[local-name()='PortabilityKnowhow']/*[local-name()='PortabilityKnowhowTitle']", doc,XPathConstants.NODE);
					if(evaluate==null){
						return null;
					}
					return evaluate.getFirstChild().getNodeValue();
				}
	        });	
			
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return map;
	}
	
	public static String getKnowledgeDir() throws IOException{
		return new File(PluginUtil.getResolvedPluginDir()+ ApplicationPropertyUtil.KNOWLEDGE_DIR).getCanonicalPath();
	}
}
