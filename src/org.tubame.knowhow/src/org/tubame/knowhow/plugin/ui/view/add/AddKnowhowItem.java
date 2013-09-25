/*
 * AddKnowhowItem.java
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
package org.tubame.knowhow.plugin.ui.view.add;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * Concrete class of know-how added.<br/>
 */
public class AddKnowhowItem extends AbstractAddItemStrategy {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AddKnowhowItem.class);

    /**
     * Constructor.<br/>
     * 
     * @param originalItemlist
     *            All Items List
     * @param categoryList
     *            Category list
     * @param knowhowList
     *            Know-how list
     */
    public AddKnowhowItem(
            List<PortabilityKnowhowListViewOperation> originalItemlist,
            List<PortabilityKnowhowListViewOperation> categoryList,
            List<PortabilityKnowhowListViewOperation> knowhowList) {
        super(originalItemlist, categoryList, knowhowList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addItem(PortabilityKnowhowListViewOperation addEntry) {
        AddKnowhowItem.LOGGER.debug("[addEntry]" + addEntry);
        originalItemlist.clear();
        originalItemlist.addAll(knowhowList);
        addSplitItem(originalItemlist, categoryList);
        originalItemlist.add(addEntry);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReferenceKey(
            PortabilityKnowhowListViewOperation addParentEntry,
            PortabilityKnowhowListViewOperation addEntry) {
        // no operation
    }

}
