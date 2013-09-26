/*
 * AbstractJbmEditorCommandButton.java
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
package org.tubame.portability.plugin.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.tubame.portability.plugin.editor.MigrationEditorOperation;
import org.tubame.portability.util.PluginUtil;

/**
 * As defined by Class of command buttons in the search results editing screen. <br/>
 * Get the Editor of the search edit screen.<br/>
 * If the command button on the search screen editing is implemented, <br/>
 * inherit this class, implement the execute.<br/>
 */
public abstract class AbstractJbmEditorCommandButton implements
        IWorkbenchWindowActionDelegate {

    /**
     * Class that performs the buttons in the search results editing screen, <br/>
     * define the processing in this method.<br/>
     * 
     * @param editor
     *            Search results edit screen
     */
    abstract void commandButtonExecute(MigrationEditorOperation editor);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        // Get the selected element
        MigrationEditorOperation editor = getEditor();
        if (editor != null) {
            commandButtonExecute(editor);
        }
    }

    /**
     * Get search results editing screen operation information. <br/>
     * Currently, if it is not in Active, and returns a NULL.<br/>
     * 
     * @return Editor
     */
    protected MigrationEditorOperation getEditor() {
        IEditorPart editorPart = PluginUtil.getActiveEditor();
        if (editorPart instanceof MigrationEditorOperation) {
            return (MigrationEditorOperation) editorPart;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbenchWindow window) {
        // no operation
    }
}
