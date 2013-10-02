/*
 * KnowhowEntryViewContentProvider.java
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
package tubame.knowhow.plugin.ui.view;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * Content providers of know-how entry View.<br/>
 * Tree for content providers of know-how entry View.<br/>
 * Class of the data and {@link PortabilityKnowhowListViewOperation} reference.<br/>
 */
public class KnowhowEntryViewContentProvider implements ITreeContentProvider {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowEntryViewContentProvider.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        KnowhowEntryViewContentProvider.LOGGER.debug("[parentElement]"
                + parentElement);
        PortabilityKnowhowListViewOperation row = (PortabilityKnowhowListViewOperation) parentElement;
        return row.getChildList().toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParent(Object element) {
        KnowhowEntryViewContentProvider.LOGGER.debug("[element]" + element);
        PortabilityKnowhowListViewOperation row = (PortabilityKnowhowListViewOperation) element;
        return row.getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren(Object element) {
        KnowhowEntryViewContentProvider.LOGGER.debug("[element]" + element);
        PortabilityKnowhowListViewOperation row = (PortabilityKnowhowListViewOperation) element;
        if (row.getChildList().size() == 0) {
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
        KnowhowEntryViewContentProvider.LOGGER.debug("[inputElement]"
                + inputElement);
        List<PortabilityKnowhowListViewOperation> list = (List<PortabilityKnowhowListViewOperation>) inputElement;
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
