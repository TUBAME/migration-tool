/*
 * ShowConvertViewAction.java
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
package tubame.portability.plugin.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import tubame.portability.exception.JbmException;
import tubame.portability.logic.ConvertAccessFactory;
import tubame.portability.model.generated.model.JbossMigrationConvertTool;
import tubame.portability.model.generated.model.MigrationItem;
import tubame.portability.plugin.view.ConvertView;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Action that opens the conversion result view from the right-click menu.<br/>
 * class action to open the conversion result view from the right-click menu of
 * files cxml extension.<br/>
 */
public class ShowConvertViewAction implements IObjectActionDelegate {

    /**
     * The selected element
     */
    private IStructuredSelection selectionElement = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {

        // Get a file that is right-click
        String filePath = getSelectionFilePath();

        try {
            // File read
            List<MigrationItem> list = readConvertFile(PluginUtil
                    .getFileFullPath(filePath));

            // Open the View
            ConvertView view = PluginUtil.getConvertView();
            view.update(list, PluginUtil.getWorkspaceRoot() + filePath);

        } catch (JbmException e) {
            PluginUtil.viewErrorDialog(ResourceUtil.CONVERT_VIEW_RESULT,
                    MessageUtil.ERR_CONVERT_READ + StringUtil.LINE_SEPARATOR
                            + e.getMessage(), e);
        }
    }

    /**
     * The acquisition of cxml file that is right click, the path from the
     * workspace root.<br/>
     * 
     * @return String
     */
    private String getSelectionFilePath() {
        IFile file = (IFile) selectionElement.getFirstElement();
        return file.getFullPath().toString();
    }

    /**
     * To read the conversion status file, and generates display data.<br/>
     * 
     * @param filePath
     *            Conversion status file
     * @return Display data
     * @throws JbmException
     *             Conversion status file read failure
     */
    private List<MigrationItem> readConvertFile(String filePath)
            throws JbmException {
        JbossMigrationConvertTool top = ConvertAccessFactory
                .getConvertReadFacade().read(filePath);
        if (top != null) {
            return top.getMigrationItems().getMigrationItem();
        }
        return new ArrayList<MigrationItem>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // Get the element selection turns
        selectionElement = (IStructuredSelection) selection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        // no oparation
    }
}
