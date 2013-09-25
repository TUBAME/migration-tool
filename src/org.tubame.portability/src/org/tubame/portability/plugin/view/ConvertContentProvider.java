/*
 * ConvertContentProvider.java
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.tubame.portability.model.generated.model.MigrationItem;

/**
 * Content providers transform status view screen.<br/>
 * Based on the data of {@link MigrationItem}, <br/>
 * the content provider to determine the value to be displayed in the conversion
 * status view.<br/>
 */
public class ConvertContentProvider implements ITreeContentProvider {

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getElements(Object inputElement) {
        @SuppressWarnings("unchecked")
        List<MigrationItem> list = (ArrayList<MigrationItem>) inputElement;
        return list.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        MigrationItem row = (MigrationItem) parentElement;
        return row.getMigrationTargets().getMigrationTarget().toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParent(Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof MigrationItem) {
            MigrationItem row = (MigrationItem) element;
            if (row.getMigrationTargets().getMigrationTarget().size() >= 1) {
                return true;
            }
        }
        return false;
    }
}
