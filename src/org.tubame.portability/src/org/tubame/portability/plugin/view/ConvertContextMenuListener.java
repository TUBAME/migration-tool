/*
 * ConvertContextMenuListener.java
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
package org.tubame.portability.plugin.view;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.tubame.portability.model.generated.model.MigrationTarget;
import org.tubame.portability.plugin.action.FileJumpAction;

/**
 * Context menu listener status screen, right-click when the conversion result.<br/>
 * Line after selecting any, as long as the line of conversion OK, <br/>
 * and display a menu of "Open file after conversion".<br/>
 */
public class ConvertContextMenuListener implements IMenuListener {
    /**
     * Access to the Editor
     */
    private final ConvertView part;

    /**
     * Constructor.<br/>
     * 
     * @param editor
     *            Editor
     * 
     */
    public ConvertContextMenuListener(ConvertView editor) {
        part = editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuAboutToShow(IMenuManager manager) {
        MigrationTarget target = getMigrationTarget(part);
        if (target != null) {
            if (target.isConvert()) {
                manager.add(new FileJumpAction(target.getConvertedFile()));
            }
        }
    }

    /**
     * If {@link MigrationTarget}, selected on the retrieve the object. <br/>
     * 
     * @param editor
     *            Editor
     * @return !NULL:{@link MigrationTarget} NULL:Target outside the selected
     *         row
     */
    private MigrationTarget getMigrationTarget(ConvertView editor) {
        Object object = part.getSelectionObject();
        if (object instanceof TreeSelection) {
            TreeSelection treeSelection = (TreeSelection) object;
            object = treeSelection.getFirstElement();
            if (object instanceof MigrationTarget) {
                return (MigrationTarget) object;
            }
        }
        return null;

    }
}
