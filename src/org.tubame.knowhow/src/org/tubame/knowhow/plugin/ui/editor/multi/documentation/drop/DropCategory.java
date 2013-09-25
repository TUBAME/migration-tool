/*
 * DropCategory.java
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
package org.tubame.knowhow.plugin.ui.editor.multi.documentation.drop;

import org.tubame.knowhow.plugin.logic.convert.ConvertCategoryList;
import org.tubame.knowhow.plugin.model.editor.CategoryEntry;
import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.plugin.ui.editor.multi.documentation.EditorTreeViewerOperator;

/**
 * Make the processing of category when dragging.<br/>
 */
public class DropCategory extends AbstractDocumentationEditorDrop {

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            Tree view
     */
    public DropCategory(EditorTreeViewerOperator treeViewer) {
        super(treeViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EntryOperator getEntry(
            PortabilityKnowhowListViewOperation dragViewdata) {
        CategoryEntry category = new CategoryEntry();
        category.setKey(dragViewdata.getCategoryParentEntry()
                .getKnowhowViewType().getRegisterKey());
        category.setName(dragViewdata.getName());
        category.setEntryViewData(dragViewdata.getKnowhowViewType());

        ConvertCategoryList registerCategory = new ConvertCategoryList();
        category.setEntryChildren(registerCategory.convert(dragViewdata,
                category));

        return category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDropEnable(EntryOperator dropTargetEntry,
            PortabilityKnowhowListViewOperation dragViewdata) {
        return super.isDraggable(dropTargetEntry);
    }
}
