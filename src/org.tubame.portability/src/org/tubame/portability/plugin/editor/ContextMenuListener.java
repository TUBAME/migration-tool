/*
 * ContextMenuListener.java
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
package org.tubame.portability.plugin.editor;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Point;
import org.tubame.portability.model.JbmEditorMigrationRow;
import org.tubame.portability.plugin.action.ConfirmItemChangeActionFactory;

/**
 * Right-click the mouse at the time, the control to display the context menu.<br/>
 * When visual inspection of (hearing) confirmation item, and display the
 * following menu.<br/>
 * Confirmed (migrated)<br/>
 * Confirmed (non-migrated)<br/>
 * To return to the unconfirmed state<br/>
 * 
 */
public class ContextMenuListener implements IMenuListener {
    /**
     * Access to the Editor
     */
    private final MigrationEditorOperation editor;

    /**
     * Constructor.<br/>
     * 
     * @param editor
     *            Editor
     * 
     */
    public ContextMenuListener(MigrationEditorOperation editor) {
        this.editor = editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuAboutToShow(IMenuManager manager) {
        manager.removeAll();
        if (this.editor.getSelectionObject() instanceof JbmEditorMigrationRow) {
            // If it is right click on the 1 or 2 or 3 level
            Point point = this.editor.getMouseClickPoint();
            ConfirmItemChangeActionFactory.setAction(manager,
                    this.editor.getTreeViewer(), point);
        }
    }
}
