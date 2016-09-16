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
package tubame.wsearch.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import tubame.wsearch.Activator;

/**
 * This is a utility class that specializes in the Plugin.<br/>
 * Make acquired and set operation Plugin properties etc..<br/>
 */
public class PluginUtil {

	/**
	 * Default constructor.<br/>
	 * Prohibits the instantiation from other classes.<br/>
	 */
	private PluginUtil() {
		// no operation
	}

	/**
	 * Get the workbench window active.<br/>
	 * 
	 * @return Workbench window active
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Get the workbench window active.<br/>
	 * 
	 * @return Workbench window active
	 */
	public static IWorkbenchPage getActivePage() {
		return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	/**
	 * Get the workbench shell active.<br/>
	 * 
	 * @return Workbench shell active
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		return window != null ? window.getShell() : getStandardDisplay().getActiveShell();
	}

	/**
	 * Get the display.<br/>
	 * Thread is calling this method if in possession of the relevant display<br/>
	 * Get the associated display, get the default display if not in possession.<br/>
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
	 * Get the projects selected.<br/>
	 * Get the projects selected. If the project selected does not exist, then
	 * return null.<br/>
	 * 
	 * @return Project selection
	 */
	public static IProject getSelectedProject() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IResource) {
				return ((IResource) element).getProject();
			} else if (element instanceof IJavaElement) {
				String projectName = ((IJavaElement) element).getJavaProject().getElementName();
				return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			}
		}
		return null;
	}

	public static IProject findIProjectByPath(String targetFullPath) {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			if (targetFullPath.startsWith(project.getLocation().toOSString())) {
				return project;
			}
		}
		return null;

	}
	
	public static String getProjectPath(String targetFullPath) {
		List<String> target = new ArrayList();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			String projectPath = project.getLocation().toOSString();
			if(targetFullPath.startsWith(projectPath)){
				target.add(projectPath);
			}
		}
		return getMostLengthPath(target);

	}

	private static String getMostLengthPath(List<String> target) {
		String mostNestPath = null;
		for (String path : target) {
			if (mostNestPath == null){
				mostNestPath = path;
			}else{
				if (mostNestPath.length()< path.length()){
					mostNestPath = path;
				}
			}
		}
		return mostNestPath;
	}

	/**
	 * Display an error message.<br/>
	 * 
	 * @param title
	 *            Title of the dialog
	 * @param message
	 *            Message of the dialog
	 * @param e
	 *            Exception
	 */
	public static void viewErrorDialog(String title, String message, Throwable e) {
		MessageDialog.openError(getActiveWorkbenchShell(), title, message);
	}

	/**
	 * Get the project name from the file path.<br/>
	 * Get the project name from the full path from the workspace root.<br/>
	 * 
	 * @param filePath
	 *            File path from the workspace root
	 * @return Project name
	 */
	public static String getProjectName(String filePath) {
		String temp = filePath;
		temp = temp.replaceAll("\\\\", CmnStringUtil.SLASH);
		if (!temp.contains(CmnStringUtil.SLASH)) {
			return temp;
		}
		return temp.substring(0, temp.indexOf(CmnStringUtil.SLASH));
	}

	/**
	 * Get the IProject from the project name.<br/>
	 * Create a IProject from the project name, get.<br/>
	 * 
	 * @param projectName
	 *            Project name
	 * @return IProject
	 */
	public static IProject getProject(String projectName) {
		if (CmnStringUtil.isEmpty(projectName)) {
			return null;
		}
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PluginUtil.getProjectName(projectName));
	}

	/**
	 * Get projects included in the file path is currently open.<br/>
	 * get projects included in the file path from the root workspace is
	 * currently open or.<br/>
	 * 
	 * @param filePath
	 *            File path from the workspace root
	 * @return True if open, return false if not open
	 */
	public static boolean isProjectOpen(String filePath) {
		if (ResourcesPlugin.getWorkspace().getRoot().getProject(PluginUtil.getProjectName(filePath)).isOpen()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get the Plugin directory.<br/>
	 * 
	 * @return Own Plugin directory
	 */
	public static String getPluginDir() {
		String pluginDirectory = CmnStringUtil.EMPTY;
		try {
			URL entry = Activator.getDefault().getBundle().getEntry(CmnFileUtil.FILE_SEPARATOR);
			pluginDirectory = FileLocator.resolve(entry).getPath();
		} catch (IOException e) {

		}
		return pluginDirectory;
	}

	public static String getRealOutputDirPath(IProject selectedProject, String outputDirPath) {
		String outputPathExcludeProjectName = getPathExcludeProjectName(selectedProject.getName(), outputDirPath);
		if (outputPathExcludeProjectName == null) {
			return selectedProject.getLocation().toOSString();
		}
		return selectedProject.getFile(outputPathExcludeProjectName).getLocation().toOSString();
	}

	private static String getPathExcludeProjectName(String projectName, String target) {
		if (projectName.equals(target)) {
			return null;
		} else {
			return target.substring(projectName.length());
		}
	}

	public static String getRelativePathBaseProject(String targetFullPath) {
		return targetFullPath.substring(PluginUtil.getSelectedProject().getLocation().toOSString().length());
	}

	public static String getCanonicalPath(String target) {
		String str = null;
		File file = new File(target);
		try {
			str = file.getCanonicalPath();
			return str;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}