/*
 * CommandViewCategory.java
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
package org.tubame.knowhow.plugin.logic.command;

import java.util.List;
import java.util.Map;

import org.tubame.knowhow.biz.model.generated.knowhow.Category;
import org.tubame.knowhow.biz.model.generated.knowhow.ChildEntry;
import org.tubame.knowhow.biz.model.generated.knowhow.EntryCategory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import org.tubame.knowhow.biz.model.EntryViewItemEnum;
import org.tubame.knowhow.plugin.logic.EntryItemManagement;
import org.tubame.knowhow.plugin.model.view.AbstractViewType;
import org.tubame.knowhow.plugin.model.view.CategoryViewType;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.util.ViewUtil;

/**
 * Converted to category data from the know-how XML.<br/>
 * converted to category data entry view of know-how from the know-how for XML.<br/>
 * JDK7<br/>
 */
public class CommandViewCategory {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommandViewCategory.class);
    /** Category level */
    private static final int CATEGORY_LEVEL = PortabilityKnowhowListViewData.LEVEL_FIRST;
    /** Map data category */
    private Map<String, Category> categoryMap;
    /** Know-how conversion command object */
    private CommandViewKnowhow commandViewKnowhow;

    /**
     * Constructor.<br/>
     * 
     * @param portabilityKnowhowConverter
     *            Know-how XML information
     */
    public CommandViewCategory(
            PortabilityKnowhowConverter portabilityKnowhowConverter) {
        this.categoryMap = portabilityKnowhowConverter.getCategoryMap();
        commandViewKnowhow = new CommandViewKnowhow(portabilityKnowhowConverter);
    }

    /**
     * Converted to category data for View display.<br/>
     * Converted know-how XML data to category data in the View for display.<br/>
     * 
     * @param entryCategory
     *            Entry category tag information
     * @return PortabilityKnowhowListViewOperation Category data
     */
    public PortabilityKnowhowListViewOperation command(
            EntryCategory entryCategory) {
        CommandViewCategory.LOGGER.debug("[entryCategory]" + entryCategory);
        Category category = getCateogry(entryCategory, null);

        // Generate the category of Top hierarchy
        PortabilityKnowhowListViewData topCategory = new PortabilityKnowhowListViewData(
                null, CATEGORY_LEVEL, knowhowXmlToEntryView(category));
        EntryItemManagement.compareEntryItemId(topCategory.getKnowhowViewType()
                .getRegisterKey(), EntryViewItemEnum.Category.getName());
        // Know-how generation of category under
        commandKnowhow(category, topCategory);
        commandChildentry(topCategory, entryCategory.getChildEntries());

        return topCategory;
    }

    /**
     * Convert the category tag under.<br/>
     * Convert the know-how of information under category tag.<br/>
     * 
     * @param category
     *            Category tag data
     * @param pearentCategory
     *            Category under data
     */
    private void commandKnowhow(Category category,
            PortabilityKnowhowListViewData pearentCategory) {
        commandViewKnowhow.command(pearentCategory,
                category.getKnowhowRefKeies());
    }

    /**
     * Convert the category of the category under the top hierarchy.<br/>
     * Convert the child category hierarchy of categories under the top
     * hierarchy.<br/>
     * 
     * @param topCategory
     *            Top level category
     * @param childEntries
     *            Child hierarchy category list
     */
    private void commandChildentry(
            PortabilityKnowhowListViewOperation topCategory,
            List<ChildEntry> childEntries) {

        for (ChildEntry childEntry : childEntries) {
            Category category = getCateogry(null, childEntry);
            // Category generation of child hierarchy
            PortabilityKnowhowListViewData childCategory = new PortabilityKnowhowListViewData(
                    topCategory, CATEGORY_LEVEL,
                    knowhowXmlToEntryView(category));
            EntryItemManagement.compareEntryItemId(childCategory
                    .getKnowhowViewType().getRegisterKey(),
                    EntryViewItemEnum.Category.getName());
            // Know-how generation of category under
            commandKnowhow(category, childCategory);
            commandChildentry(ViewUtil.insertChildKnowhowEntryViewData(
                    topCategory, childCategory), childEntry.getChildEntries());
        }

    }

    /**
     * Get the category that matches the reference key.<br/>
     * 
     * @param entryCategory
     *            Entry tag information
     * @param childEntry
     *            Child hierarchy entry information
     * @return Category tag information
     */
    private Category getCateogry(EntryCategory entryCategory,
            ChildEntry childEntry) {

        if (entryCategory != null) {
            return ViewUtil.getCategory(entryCategory.getEntryCategoryRefKey(),
                    categoryMap);
        }
        if (childEntry != null) {
            return ViewUtil.getCategory(childEntry.getEntryCategoryRefKey(),
                    categoryMap);
        }
        return null;
    }

    /**
     * Generate the View display data.<br/>
     * Generate the View display data from the know-how XML data.<br/>
     * 
     * @param Category
     *            Category tag information
     * @return Display data
     */
    private AbstractViewType knowhowXmlToEntryView(Category category) {
        return new CategoryViewType(category);
    }
}
