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
package tubame.knowhow.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.Activator;
import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.ui.editor.multi.EditorDirty;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.plugin.ui.view.KnowhowEntryCheckItemView;
import tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewer;
import tubame.knowhow.plugin.ui.view.KnowhowEntryView;

/**
 * Utility class know-how of creating XML.
 */
@SuppressWarnings("restriction")
public final class PluginUtil {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PluginUtil.class);
    /** Know-how entry view ID */
    private static final String KNOWHOW_ENTRY_VIEW_ID = "tubame.knowhow.maintenance.portability.ui.view.KnowhowEntryView";
    /** Transplant expertise editor ID */
    private static final String MAINTENANCE_KNOWHOW_EDITOR_ID = "tubame.knowhow.maintenance.portability.editors.multi.KnowhowMultiEditor";

    /**
     * Constructor.<br/>
     * 
     */
    private PluginUtil() {
        super();
    }

    /**
     * Open the transplant expertise Editor. <br/>
     * The return false if fails to display.<br/>
     * 
     * @param iFile
     *            File Path
     * @return true:Editor display success false:Editor display failure
     */
    public static boolean openMaintenanceKnowhowEditor(IFile iFile) {
        boolean result = true;
        try {
            IWorkbenchPage page = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();
            
            try {
				iFile.refreshLocal(IResource.DEPTH_ZERO, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            IDE.openEditor(page, iFile,
                    PluginUtil.getMaintenanceKnowhowEditorId());
        } catch (PartInitException e) {
            // Editor display failure
            JbmException
                    .outputExceptionLog(
                            LOGGER,
                            e,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.ERROR_OPEN_KNOWHOWEDITOR));
        }
        return result;
    }

    /**
     * Judgment expertise editor is open already. <br/>
     * And return true if it is already open.<br/>
     * 
     * @return ture:already open false:Other
     */
    public static boolean isMultiOpenKnowhowEditor() {
        if (PluginUtil.getActivePage() != null
                && PluginUtil.getOpenEditors().size() > 1) {
            return true;
        }
        return false;
    }
    
    public static void errorDialogWithStackTrace(Shell shell, String msg, Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);

        final String trace = sw.toString(); // stack trace as a string
        
        // Temp holder of child statuses
        List<Status> childStatuses = new ArrayList();

        // Split output by OS-independend new-line
        for (String line : trace.split(System.getProperty("line.separator"))) {
            // build & add status
            childStatuses.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, line));
        }

        MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
                childStatuses.toArray(new Status[] {}), // convert to array of statuses
                t.getLocalizedMessage(), t);
        org.eclipse.jface.dialogs.ErrorDialog.openError(shell, null,  msg, ms);	
    }
    

    /**
     * Get the know-how transplant editor ID.<br/>
     * 
     * @return Transplant expertise editor ID
     */
    public static String getMaintenanceKnowhowEditorId() {
        return PluginUtil.MAINTENANCE_KNOWHOW_EDITOR_ID;
    }

    /**
     * Get the know-how entry view ID.<br/>
     * 
     * @return Know-how entry view ID
     */
    public static String getKnowhowEntryViewId() {
        return PluginUtil.KNOWHOW_ENTRY_VIEW_ID;
    }

    /**
     * Get the know-how entry view.
     * 
     * @return Know-how entry view
     * @throws JbmException
     *             Failed to get the workspace
     */
    public static IViewPart getKnowhowEntryView() throws JbmException {
        return PluginUtil.getViewPart(PluginUtil.getKnowhowEntryViewId());
    }

    public static IViewPart getCheckItemEntryView() throws JbmException {
        return PluginUtil.getViewPart(KnowhowEntryCheckItemView.ID);
    }    
    /**
     * Get the view. The return a Null if it fails to take.<br/>
     * 
     * @param id
     *            View ID
     * @return View
     * @throws JbmException
     *             Failed to get the workspace
     */
    private static IViewPart getViewPart(String id) throws JbmException {
        try {
            IViewPart iViewPart = PluginUtil.isOpendView(id);
            if (iViewPart != null) {
                return iViewPart;
            } else if (PluginUtil.getActivePage() != null) {
                return PluginUtil.getActivePage().showView(id);
            }
        } catch (WorkbenchException e) {
            throw new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.ERROR_GET_WORKSPACE));
        }
        return null;
    }

    /**
     * The decision whether or open the specified view. And return true if it is
     * already open.<br/>
     * 
     * @param viewId
     *            Check view ID
     * @return true:already open false:Not open
     */
    public static IViewPart isOpendView(String viewId) {
        if (PluginUtil.getActivePage() != null) {
            IViewReference[] views = PluginUtil.getActivePage()
                    .getViewReferences();
            for (IViewReference view : views) {
                if (viewId.equals(view.getId())) {
                    return view.getView(false);
                }
            }
        }
        return null;
    }

    /**
     * Get TreeViewer that is displayed in the entry view know-how.<br/>
     * The return a Null if it fails to take.<br/>
     * 
     * @return TreeViewer
     */
    public static KnowhowEntryTreeViewer getKnowhowEntryViewTreeViewer() {
        try {
            if (KnowhowManagement.getKnowhowEntryTreeViewer() != null) {
                return KnowhowManagement.getKnowhowEntryTreeViewer();
            } else if (PluginUtil.getKnowhowEntryView() != null) {
                KnowhowEntryView knowhowEntryView = (KnowhowEntryView) PluginUtil
                        .getKnowhowEntryView().getViewSite().getPart();
                return knowhowEntryView.getTreeViewer();
            }
        } catch (JbmException e) {
            JbmException
                    .outputExceptionLog(
                            LOGGER,
                            e,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.ERROR_OPEN_KNOWHOWEDITOR));
        }
        return null;
    }

    /**
     * Of the editor of all that is currently open, get the list only transplant
     * expertise editor.<br/>
     * 
     * @return List of know-how transplant editor that is currently open
     */
    public static List<EditorDirty> getOpenEditors() {
        List<EditorDirty> result = new ArrayList<EditorDirty>();
        if (PluginUtil.getActivePage() != null) {
            for (IEditorReference editor : PluginUtil.getActivePage()
                    .getEditorReferences()) {
                if (MAINTENANCE_KNOWHOW_EDITOR_ID.equals(editor.getId())) {
                    if (isErrorEditor(editor.getEditor(false))) {
                        result.add((EditorDirty) editor.getEditor(false));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Editor that is opened determines whether the ErrorEditorPart.<br/>
     * The return false if it is ErrorEditorPart.<br/>
     * 
     * @param editor
     *            IEditorPart
     * @return true:Not ErrorEditorPart false:ErrorEditorPart
     */
    private static boolean isErrorEditor(IEditorPart editor) {
        if (editor instanceof ErrorEditorPart) {
            return false;
        }
        return true;
    }

    /**
     * Get ActiveWorkbenchPage. The return a Null if the active page is not
     * present.<br/>
     * 
     * @return IWorkbenchPage
     */
    public static IWorkbenchPage getActivePage() {
        if (Activator.getDefault() != null) {
            return Activator.getDefault().getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();
        }
        return null;
    }

    /**
     * Get the full path to the file path from the workspace root.<br/>
     * 
     * @param filePath
     *            File Path
     * @return String
     */
    public static String getFileFullPath(String filePath) {
        return PluginUtil.getWorkspaceRoot() + CmnFileUtil.FILE_SEPARATOR
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
     * Get ActiveWorkbenchWindow.<br/>
     * 
     * @return IWorkbenchWindow
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    /**
     * Get ActiveWorkbenchShell.<br/>
     * 
     * @return shell
     */
    public static Shell getActiveWorkbenchShell() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        return window != null ? window.getShell() : getStandardDisplay()
                .getActiveShell();
    }

    /**
     * Thread is calling this method if you are in possession of the relevant
     * display get the associated display, <br/>
     * and get the default display if not in possession.<br/>
     * 
     * @return Display
     */
    private static Display getStandardDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;
    }

    /**
     * Get the Plugin directory. <br/>
     * 
     * @return Self Plugin directory
     */
    public static String getPluginDir() {
        String pluginDirectory = CmnStringUtil.EMPTY;
        try {
            URL entry = Activator.getDefault().getBundle()
                    .getEntry(CmnFileUtil.FILE_SEPARATOR);
            pluginDirectory = FileLocator.resolve(entry).getPath();
        } catch (IOException e) {
            JbmException.outputExceptionLog(LOGGER, e, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.ERROR_GET_WORKSPACE));

        }
        return pluginDirectory;
    }

    /**
     * Get the real path Plugin directory.<br/>
     * 
     * @return Real self-Plugin directory path
     */
    public static String getRealPluginDirPath() {
        String path = PluginUtil.getPluginDir();
        File file = new File(path);
        return file.getPath();
    }

    /**
     * Deep copy of the object. Object to be copied is that it implements the
     * Serializable.
     * 
     * @param <T>
     *            Type
     * @param obj
     *            Copy objects
     * @return After copying objects
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopy(T obj) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Object rtn = null;
        try {
            // Convert to a byte array object
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            // The re-converted to an object the byte sequence
            ois = new ObjectInputStream(new ByteArrayInputStream(
                    baos.toByteArray()));
            rtn = ois.readObject();

        } catch (IOException e) {
            JbmException.outputExceptionLog(LOGGER, e, e.getMessage());
        } catch (ClassNotFoundException e) {
            JbmException.outputExceptionLog(LOGGER, e, e.getMessage());
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                JbmException.outputExceptionLog(LOGGER, e, e.getMessage());
            }
        }
        return (T) rtn;
    }

    /**
     * Do (refresh) file synchronization.<br/>
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
            JbmException.outputExceptionLog(LOGGER, e, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.EMPTY_MESSAGE));
        }
    }

    /**
     * Do (refresh) synchronization of workspace.<br/>
     * 
     */
    public static void refreshWorkSpace() {
        try {
            ResourcesPlugin
                    .getWorkspace()
                    .getRoot()
                    .refreshLocal(IResource.DEPTH_INFINITE,
                            PluginUtil.getProgressMonitor());
        } catch (CoreException e) {
            JbmException.outputExceptionLog(LOGGER, e, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.EMPTY_MESSAGE));
        }
    }

    /**
     * Get the know-how editor.
     * 
     * @return Know-how editor
     */
    public static MaintenanceKnowhowMultiPageEditor getKnowhowEditor() {
        for (EditorDirty editor : PluginUtil.getOpenEditors()) {
            if (editor instanceof MaintenanceKnowhowMultiPageEditor) {
                return (MaintenanceKnowhowMultiPageEditor) editor;
            }
        }
        return null;
    }

    /**
     * Create a IProject from the project name, acquisition.<br/>
     * 
     * @param projectName
     *            Project name
     * @return IProject
     */
    public static IProject getProject(String projectName) {
        if (CmnStringUtil.isEmpty(projectName)) {
            return null;
        }
        return ResourcesPlugin.getWorkspace().getRoot()
                .getProject(PluginUtil.getProjectName(projectName));
    }

    /**
     * Get the project name from the full path from the workspace root.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @return Project name
     */
    private static String getProjectName(String filePath) {
        String temp = filePath;
        temp = temp.replaceAll("\\\\", CmnStringUtil.SLASH);
        if (!temp.contains(CmnStringUtil.SLASH)) {
            return temp;
        }
        return temp.substring(0, temp.indexOf(CmnStringUtil.SLASH));
    }

    /**
     * Generate IFile than relative to the container and IContainer.<br/>
     * 
     * @param container
     *            IContainer
     * @param target
     *            Relative to the container
     * @param createFile
     *            Presence or absence of file creation
     * @return IFile
     * @throws IOException
     *             IO exception
     */
    public static IFile createIFile(IContainer container, String target,
            boolean createFile) throws IOException {
        if (!CmnStringUtil.isEmpty(target)) {
            Path path = new Path(target);
            File targetFile = new File(container.getLocationURI().getPath()
                    + CmnFileUtil.FILE_SEPARATOR + path.toOSString());
            if (createFile) {
                targetFile.createNewFile();
            }
            if (targetFile.exists() && targetFile.isFile()) {
                return container.getFile(path);
            }
        }
        return null;
    }

    /**
     * Acquisition projects included in the file path from the root workspace is
     * currently open or.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @return True If open,returns false if not open
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
     * Viewed in a Web browser of Eclipse standard path that you specify.<br/>
     * 
     * @param url
     *            URL
     * @throws JbmException
     *             Jbm exception
     */
    public static void openWebBrowzer(String url) throws JbmException {
        IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
                .getBrowserSupport();
        try {
            browserSupport.createBrowser(CmnStringUtil.EMPTY).openURL(
                    new URL(url));
        } catch (PartInitException e) {
            throw new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.FAIL_OPEN_WEB_BROWSER),
                    e);
        } catch (IOException e) {
            throw new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.FAIL_OPEN_WEB_BROWSER),
                    e);
        }
    }

    /**
     * Get IProgressMonitor.<br/>
     * 
     * @return IProgressMonitor
     */
    public static IProgressMonitor getProgressMonitor() {
        WorkbenchWindow workbenchWindow = (WorkbenchWindow) PluginUtil
                .getActiveWorkbenchWindow();
        IActionBars bars = workbenchWindow.getActionBars();
        IStatusLineManager lineManager = bars.getStatusLineManager();
        return lineManager.getProgressMonitor();
    }

    /**
     * Check project name in the file path from the workspace root exists.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @return True if if present,returns false if not exist
     */
    public static boolean projectExists(String filePath) {
        String projectName = PluginUtil.getProjectName(filePath);
        // Path of the workspace If the received empty
        if (projectName.equals(CmnStringUtil.EMPTY)) {
            return false;
        }
//        if (!ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)
//                .exists()) {
//            return false;
//        }
        return true;
    }

    /**
     * Judgment Tree of know-how data entry view are located.<br/>
     * Judgment Tree data are located. The return True if it is not placed.<br/>
     * 
     * @return true:Not placed false:Other
     */
    public static boolean isTreeViewerDisposed() {
        return PluginUtil.getKnowhowEntryViewTreeViewer().getTree()
                .isDisposed();
    }
}
