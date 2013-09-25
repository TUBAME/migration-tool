/*
 * BrowseFileButtonSelectionListener.java
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
package org.tubame.portability.plugin.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
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
import org.tubame.portability.util.FileUtil;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.StringUtil;
import org.tubame.portability.util.resource.ResourceUtil;

/**
 * Reference File button listener.<br/>
 * Listener class that displays a file selection dialog.<br/>
 */
public class BrowseFileButtonSelectionListener implements SelectionListener {
    /** Search target path of the relative path */
    private final Text searchTargetText;
    /**
     * Page
     */
    private final FileBrowseButton page;
    /**
     * Extension
     */
    private final String extension;

    /**
     * Constructor.<br/>
     * 
     * @param page
     *            Page
     * @param searchTargetText
     *            Text to be displayed in the file selection after
     * @param extension
     *            Extension
     */
    public BrowseFileButtonSelectionListener(FileBrowseButton page,
            Text searchTargetText, String extension) {
        this.searchTargetText = searchTargetText;
        this.page = page;
        this.extension = extension;
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
        IFile searchTargetFile = fileBrowse(extension);
        if (searchTargetFile != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(searchTargetFile.getProject().getName());
            sb.append(FileUtil.FILE_SEPARATOR);
            sb.append(searchTargetFile.getProjectRelativePath());
            searchTargetText.setText(sb.toString());
        }
    }

    /**
     * View the file selection dialog.<br/>
     * is called when you press the search tool input file name "Browse" button. <br/>
     * Set the path of the selected file to display a tree of dialog can be
     * selected only csv file.<br/>
     * 
     * @param extension
     *            Extension
     * @return File
     */
    protected IFile fileBrowse(final String extension) {
        String filePath = page.getOutFilePath();

        // Create dialog
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
                null, new WorkbenchLabelProvider(),
                new WorkbenchContentProvider());

        // Set the resources that are displayed in the dialog
        IProject project = PluginUtil.getProject(filePath);
        dialog.setInput(getDialogInput(project));
        dialog.setMessage(getDialogMessage(project));
        dialog.setTitle(ResourceUtil.FILE_SELECT_TITLE);
        dialog.setHelpAvailable(false);

        // Acquisition Check to see if the file has been entered in the text
        // box, and if it is filled
        if (!StringUtil.isEmpty(filePath)) {
            IContainer container = ResourcesPlugin.getWorkspace().getRoot();
            dialog.setInitialSelection(PluginUtil.createIFile(container,
                    filePath));
        }
        // Add ViewerFileter
        dialog.addFilter(new ViewerFilter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                    Object element) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    String ext = file.getFileExtension();
                    if (ext == null) {
                        return false;
                    }
                    if (!ext.equals(extension)) {
                        return false;
                    }
                }
                return true;
            }
        });

        // Open the dialog.
        if (dialog.open() == Window.OK) {
            Object result = dialog.getFirstResult();
            if (result instanceof IFile) {
                return (IFile) result;
            }
        }
        return null;
    }

    /**
     * Get the resources to be input in the dialog. <br/>
     * The return of the workspace under if it is not already selected, <br/>
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
        return String.format(ResourceUtil.FILE_SELECT_MESSAGE, projectName);
    }
}
