/*
 * BrowseDirectoryButtonSelectionListener.java
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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import tubame.portability.util.FileUtil;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Listener class that displays a folder selection dialog.<br/>
 */
public class BrowseDirectoryButtonSelectionListener implements
        SelectionListener {

    /** Search target path of the relative path */
    private final Text searchTargetText;

    /**
     * Constructor.<br/>
     * 
     * @param searchTargetText
     *            Text to be displayed in the folder selected after
     */
    public BrowseDirectoryButtonSelectionListener(Text searchTargetText) {
        this.searchTargetText = searchTargetText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        // Specify from the browse button search folder, and create a path
        IFolder searchTargetIFolder = folderBrowse();
        if (searchTargetIFolder != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(searchTargetIFolder.getProject().getName());
            sb.append(FileUtil.FILE_SEPARATOR);
            sb.append(searchTargetIFolder.getProjectRelativePath());
            searchTargetText.setText(sb.toString());
        }
    }

    /**
     * View directory selection dialog.<br/>
     * It is called when you press p> [Detail] search folder the "Browse"
     * button.<br/>
     * Set the path to the folder you want to display a tree dialog to select a
     * folder, the selected.<br/>
     * 
     * @return Folder path
     */
    private IFolder folderBrowse() {
        String directryPath = StringUtil.getText(searchTargetText);

        // Create dialog
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
                null, new WorkbenchLabelProvider(),
                new WorkbenchContentProvider());

        // Set the resources that are displayed in the dialog
        IProject project = PluginUtil.getProject(directryPath);
        dialog.setInput(getDialogInput(project));
        dialog.setMessage(getDialogMessage(project));
        dialog.setTitle(ResourceUtil.DIR_SELECT_TITLE);
        dialog.setHelpAvailable(false);

        // If the folder name has already been entered into the textbox initial
        // value
        if (!StringUtil.isEmpty(directryPath)) {
            IContainer container = ResourcesPlugin.getWorkspace().getRoot();
            dialog.setInitialSelection(PluginUtil.createIFolder(container,
                    directryPath));
        }
        // Add ViewerFileter
        dialog.addFilter(new ViewerFilter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                    Object element) {
                if (element instanceof IFolder) {
                    return true;
                } else if (element instanceof IFile) {
                    return false;
                }
                return true;
            }
        });

        // Open the dialog.
        if (dialog.open() == Window.OK) {
            Object result = dialog.getFirstResult();
            if (result instanceof IFolder) {
                return (IFolder) result;
            }
        }
        return null;

    }

    /**
     * Get the resources to be input in the dialog. <br/>
     * The return of the workspace under if it is not already selected,<br/>
     * the project under if the project is (or input) selection.<br/>
     * 
     * @param project
     *            Project that is (or input) selection
     * @return Resources to be input in the dialog
     */
    private IResource getDialogInput(IProject project) {
        if (project != null) {
            return project;
        } else {
            return (IResource) ResourcesPlugin.getWorkspace();
        }
    }

    /**
     * Get the message to be displayed at the top of the dialog. <br/>
     * If the project is (or input) is selected, <br/>
     * the display in addition to the project name.<br/>
     * 
     * @param project
     *            Project that is (or input) selection
     * @return Message
     */
    private String getDialogMessage(IProject project) {
        String projectName = StringUtil.EMPTY;
        if (project != null) {
            projectName = project.getName();
        }
        return String.format(ResourceUtil.DIR_SELECT_MESSAGE, projectName);
    }
}
