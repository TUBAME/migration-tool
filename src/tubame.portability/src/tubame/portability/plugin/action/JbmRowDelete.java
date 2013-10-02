/*
 * JbmRowDelete.java
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

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.model.MigrationEditorRow;
import tubame.portability.plugin.editor.MigrationEditorOperation;
import tubame.portability.plugin.view.WorkStatusView;

/**
 * Delete the selected rows in the search results.<br/>
 */
public class JbmRowDelete implements MigrationRowDelete {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeleteExecutor(int level) {
        // First hierarchy is deleted outside
        if (JbmEditorMigrationRow.LEVEL_SECOND == level) {
            // Second level
            return true;
        }
        if (JbmEditorMigrationRow.LEVEL_THIRD == level) {
            // Third level
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRowItem(MigrationEditorOperation editor,
            MigrationEditorRow deleteJbmEditorRow) {
        TreeViewer treeViewer = editor.getTreeViewer();
        @SuppressWarnings("unchecked")
        List<JbmEditorMigrationRow> editorList = (List<JbmEditorMigrationRow>) treeViewer
                .getTree().getData();

        for (JbmEditorMigrationRow first : editorList) {
            if (deleteJbmEditorRow.equals(first)) {
                editorList.remove(first);
                WorkStatusView.out(false, editorList);
                return;
            }
            for (JbmEditorMigrationRow second : first.getChildList()) {
                if (deleteJbmEditorRow.equals(second)) {
                    first.getChildList().remove(second);
                    // If the number of the second hierarchy after you remove
                    // the second level,
                    // he or she belongs is 0 items, also want to remove the
                    // first level
                    deleteFirstLevel(editorList, first);
                    first.updateWriteData();
                    WorkStatusView.out(false, editorList);
                    return;
                }
                for (JbmEditorMigrationRow third : second.getChildList()) {
                    if (deleteJbmEditorRow.equals(third)) {
                        second.getChildList().remove(third);
                        // If the number of the second hierarchy after deleting
                        // the third level,
                        // he had belonged is 0 items, also want to remove the
                        // second level
                        deleteSecoundLevelSince(first, second);
                        // If the number of the first level after deleting the
                        // second level,
                        // he or she belongs is 0 items, also want to remove the
                        // first level
                        deleteFirstLevel(editorList, first);
                        first.updateWriteData();
                        WorkStatusView.out(false, editorList);
                        return;
                    }
                }
            }
        }
    }

    /**
     * If the size of the second level is 0, Delete the second level.<br/>
     * The third level removed after Call this method.<br/>
     * 
     * @param second
     *            The first level
     * @param third
     *            Second level
     */
    private void deleteSecoundLevelSince(JbmEditorMigrationRow second,
            JbmEditorMigrationRow third) {
        if (third.getChildList().size() == 0) {
            second.getChildList().remove(third);
        }
    }

    /**
     * If the size of the the first level is 0, and remove the the first level.<br/>
     * Delete the second level after Call this method.<br/>
     * 
     * @param top
     *            0th level
     * @param secound
     *            The first level
     */
    private void deleteFirstLevel(List<JbmEditorMigrationRow> top,
            JbmEditorMigrationRow secound) {
        if (secound.getChildList().size() == 0) {
            top.remove(secound);
        }
    }
}
