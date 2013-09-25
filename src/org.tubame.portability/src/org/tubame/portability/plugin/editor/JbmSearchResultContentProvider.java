/*
 * JbmSearchResultContentProvider.java
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.tubame.portability.model.JbmEditorMigrationRow;
import org.tubame.portability.model.MigrationEditorRow;

/**
 * Content provider of search results editing screen.<br/>
 * Tree for content providers of search results editing screen.<br/>
 * Class of the data and {@link JbmEditorMigrationRow} reference.<br/>
 */
public class JbmSearchResultContentProvider implements ITreeContentProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        MigrationEditorRow row = (MigrationEditorRow) parentElement;
        return row.getChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParent(Object element) {
        MigrationEditorRow row = (MigrationEditorRow) element;
        return row.getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren(Object element) {
        MigrationEditorRow row = (MigrationEditorRow) element;
        return row.hasChildren();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
        List<MigrationEditorRow> list = (ArrayList<MigrationEditorRow>) inputElement;
        return list.toArray();
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
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // no operation
    }
}
