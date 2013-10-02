/*
 * AbstractAddItemStrategy.java
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
package tubame.knowhow.plugin.ui.view.add;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * Abstract class to add items to the know-how entry view.<br/>
 */
public abstract class AbstractAddItemStrategy {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractAddItemStrategy.class);
    /** All Items list */
    List<PortabilityKnowhowListViewOperation> originalItemlist;
    /** Category list */
    List<PortabilityKnowhowListViewOperation> categoryList;
    /** Category list */
    List<PortabilityKnowhowListViewOperation> knowhowList;

    /**
     * Constructor.<br/>
     * 
     * @param originalItemlist
     *            All Items List
     * @param categoryList
     *            Category list
     * @param knowhowList
     *            Category list know-how list
     */
    public AbstractAddItemStrategy(
            List<PortabilityKnowhowListViewOperation> originalItemlist,
            List<PortabilityKnowhowListViewOperation> categoryList,
            List<PortabilityKnowhowListViewOperation> knowhowList) {
        AbstractAddItemStrategy.LOGGER.debug("[originalItemlist]"
                + originalItemlist + "[categoryList]" + categoryList
                + "[knowhowList]" + knowhowList);
        this.originalItemlist = originalItemlist;
        this.categoryList = categoryList;
        this.knowhowList = knowhowList;
    }

    /**
     * Add to entry know-how view the registration items.<br/>
     * 
     * @param addEntry
     *            Registration items
     */
    public abstract void addItem(PortabilityKnowhowListViewOperation addEntry);

    /**
     * Add to item list all the items that have been split.<br/>
     * 
     * @param originalItemlist
     *            All Items List
     * @param itemList
     *            Additional items
     */
    protected void addSplitItem(
            List<PortabilityKnowhowListViewOperation> originalItemlist,
            List<PortabilityKnowhowListViewOperation> itemList) {
        for (PortabilityKnowhowListViewOperation operation : itemList) {
            originalItemlist.add(operation);
        }
    }

    /**
     * Set reference key of each item unique.<br/>
     * 
     * @param addParentEntry
     *            Registration destination items
     * @param addEntry
     *            Registration items
     */
    public abstract void setReferenceKey(
            PortabilityKnowhowListViewOperation addParentEntry,
            PortabilityKnowhowListViewOperation addEntry);
}
