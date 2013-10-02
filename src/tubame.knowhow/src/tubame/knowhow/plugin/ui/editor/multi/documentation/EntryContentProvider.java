/*
 * EntryContentProvider.java
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
package tubame.knowhow.plugin.ui.editor.multi.documentation;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.editor.AbstractEntry;
import tubame.knowhow.plugin.model.editor.EntryOperator;

/**
 * ContentProvider class of heading the list tree view.<br/>
 * Data format is based on the assumption that the use of {@link AbstractEntry}.<br/>
 */
public class EntryContentProvider implements ITreeContentProvider {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntryContentProvider.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        LOGGER.debug("[parentElement]" + parentElement);
        EntryOperator row = (EntryOperator) parentElement;
        return row.getEntryChildren().toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParent(Object element) {
        LOGGER.debug("[element]" + element);
        EntryOperator row = (EntryOperator) element;
        return row.getParentEntry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren(Object element) {
        LOGGER.debug("[element]" + element);
        EntryOperator row = (EntryOperator) element;
        if (row.getEntryChildren().size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
        LOGGER.debug("[inputElement]" + inputElement);
        List<AbstractEntry> list = (List<AbstractEntry>) inputElement;
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
