/*
 * SearchFilterAction.java
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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.plugin.editor.MigrationEditorOperation;
import tubame.portability.util.resource.MessageUtil;

/**
 * Action class of the display switching button.<br/>
 * The display switching button is pressed for the first time, <br/>
 * and the search results editor only items that hit the search.<br/>
 * The display switching button press the next, and display all items. In
 * addition, <br/>
 * repeat the following order.<br/>
 * [Search hit Item] Display > [all items] Display<br/>
 */
public class SearchFilterAction extends AbstractJbmEditorCommandButton {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SearchFilterAction.class);

    private boolean isChange = false;
    private TreeViewer treeViewer = null;
    private SearchFilter filter = null;

    /**
     * {@inheritDoc}
     */
    @Override
    void commandButtonExecute(MigrationEditorOperation editor) {
        LOGGER.info(MessageUtil.INF_CHANGE_SEARCH_FILTER);

        treeViewer = editor.getTreeViewer();
        if (!isChange) {
            filter = new SearchFilter();
            addFilter(filter);
            isChange = true;
        } else {
            removeFilter(filter);
            isChange = false;
        }

    }

    /**
     * Add a filter to the view.<br/>
     * 
     * @param filter
     *            Filter
     */
    private void addFilter(ViewerFilter filter) {

        treeViewer.getTree().setRedraw(false);
        treeViewer.collapseAll();
        treeViewer.addFilter(filter);
        treeViewer.expandAll();
        treeViewer.getTree().setRedraw(true);

    }

    /**
     * 
     * The removal of the filter hanging on in the search results Editor.<br/>
     * 
     * @param filter
     */
    private void removeFilter(ViewerFilter filter) {
        treeViewer.getTree().setRedraw(false);
        treeViewer.collapseAll();
        treeViewer.removeFilter(filter);
        treeViewer.expandAll();
        treeViewer.getTree().setRedraw(true);
    }

    /**
     * Class filter search results.<br/>
     * Inner class to control the filter of the search results.<br/>
     */
    private static class SearchFilter extends ViewerFilter {
        @Override
        public boolean select(Viewer viewer, Object parent, Object e) {
            JbmEditorMigrationRow row = (JbmEditorMigrationRow) e;
            if (row.getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
                if (row.getHitNum() >= 1) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }
    }
}
