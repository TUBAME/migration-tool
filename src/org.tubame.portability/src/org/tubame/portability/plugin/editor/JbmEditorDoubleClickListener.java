/*
 * JbmEditorDoubleClickListener.java
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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.tubame.portability.model.JbmEditorMigrationRow;
import org.tubame.portability.util.PluginUtil;

/**
 * Search results edit screen double click listener class.<br/>
 * View the file in the editor that second-level three double-click after the
 * specified.<br/>
 * Further, setting various markers.
 */
public class JbmEditorDoubleClickListener extends AbstractDoubleClickListener {

    /**
     * Constructor.<br/>
     * 
     * @param menuOperation
     *            Editor
     */
    public JbmEditorDoubleClickListener(MigrationEditorOperation menuOperation) {
        super(menuOperation);
    }

    /**
     * Cast the selection of the target.<br/>
     * Cast in Row data that is displayed on the screen.<br/>
     * 
     * @param event
     *            Double-click event
     * @return !=null Line data
     */
    private JbmEditorMigrationRow castJbmEditorMigrationRow(
            DoubleClickEvent event) {
        Object element = ((StructuredSelection) event.getSelection())
                .getFirstElement();
        if (element instanceof JbmEditorMigrationRow) {
            return (JbmEditorMigrationRow) element;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createMarker(IFile file, String targetFile)
            throws CoreException {

        for (JbmEditorMigrationRow row : getJbmEditorMigrationRowList()) {
            // Degree of difficulty
            String code = row.getDifficulty();
            for (JbmEditorMigrationRow child : row.getChildList()) {
                // If the filename is the same
                if (targetFile.equals(child.getFileName())) {
                    // Marker added in line number
                    for (JbmEditorMigrationRow grandChild : child
                            .getChildList()) {
                        super.addMarker(file, PluginUtil
                                .getDifficultyMarkerId(code),
                                Integer.valueOf(grandChild.getRowNo())
                                        .intValue());
                    }
                }
            }
        }
    }

    /**
     * Get row data from the screen.<br/>
     * 
     * @return Line list
     */
    @SuppressWarnings("unchecked")
    private List<JbmEditorMigrationRow> getJbmEditorMigrationRowList() {
        return (List<JbmEditorMigrationRow>) super.getEditor().getTreeViewer()
                .getTree().getData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doubleClick(DoubleClickEvent event) {
        JbmEditorMigrationRow row = castJbmEditorMigrationRow(event);
        if (row != null) {
            // If double-click the second level
            if (JbmEditorMigrationRow.LEVEL_SECOND == row.getLevel()) {
                // Get a file path from the second level
                super.editorOpen(row.getFileName(), 0);
            }
            // If double-click the three-tier
            if (JbmEditorMigrationRow.LEVEL_THIRD == row.getLevel()) {
                super.editorOpen(row.getFileName(),
                        Integer.valueOf(row.getRowNo()).intValue());
            }
        }

    }
}
