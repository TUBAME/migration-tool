/*
 * EntryItemManagement.java
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
package tubame.knowhow.plugin.logic;

import tubame.common.util.CmnStringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.model.EntryViewItemEnum;

import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * This is the class that controls the items that have been registered.<br/>
 * the class that controls the ID information of the item being registered.<br/>
 * JDK7
 */
public final class EntryItemManagement {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntryItemManagement.class);
    /** Category ID */
    private static String categoryId;
    /** Know-how ID */
    private static String knowhowId;
    /** Check item ID */
    private static String checkItemId;
    /** Search information ID */
    private static String searchInfoId;
    /** Know how ID */
    private static String knowhowDetailId;

    /**
     * Constructor.
     * 
     */
    private EntryItemManagement() {
        // no operation
    }

    /**
     * Initialize the data of this class.<br/>
     * 
     */
    public static void initializationItemData() {
        EntryItemManagement.LOGGER.debug(CmnStringUtil.EMPTY);

        EntryItemManagement.categoryId = null;
        EntryItemManagement.knowhowId = null;
        EntryItemManagement.checkItemId = null;
        EntryItemManagement.searchInfoId = null;
        EntryItemManagement.knowhowDetailId = null;
    }

    /**
     * Compare the item ID, and update.<br/>
     * Compare the past ID / new ID of each item ID, and update.<br/>
     * 
     * @param itemId
     *            Item ID
     * @param type
     *            Entry Type
     */
    public static void compareEntryItemId(String itemId, String type) {
        EntryItemManagement.LOGGER.debug("[itemId]" + itemId + "[type]" + type);

        String targetItemId = null;
        if (EntryViewItemEnum.Category.getName().equals(type)) {
            targetItemId = EntryItemManagement.compareItemId(
                    EntryItemManagement.categoryId, itemId);
            EntryItemManagement.categoryId = targetItemId;
        }
        if (EntryViewItemEnum.Knowhow.getName().equals(type)) {
            targetItemId = EntryItemManagement.compareItemId(
                    EntryItemManagement.knowhowId, itemId);
            EntryItemManagement.knowhowId = targetItemId;
        }
        if (EntryViewItemEnum.KnowhowDetail.getName().equals(type)) {
            targetItemId = EntryItemManagement.compareItemId(
                    EntryItemManagement.knowhowDetailId, itemId);
            EntryItemManagement.knowhowDetailId = targetItemId;
        }
        if (EntryViewItemEnum.CheckItem.getName().equals(type)) {
            targetItemId = EntryItemManagement.compareItemId(
                    EntryItemManagement.checkItemId, itemId);
            EntryItemManagement.checkItemId = targetItemId;
        }
        if (EntryViewItemEnum.SearchInfo.getName().equals(type)) {
            targetItemId = EntryItemManagement.compareItemId(
                    EntryItemManagement.searchInfoId, itemId);
            EntryItemManagement.searchInfoId = targetItemId;
        }
    }

    /**
     * Numbered the ID of the item.<br/>
     * 
     * @param itemId
     *            Item ID
     * @return Item ID
     */
    private static String numberingItemId(String itemId) {
        String[] tempId = itemId.split(CmnStringUtil.UNDERLINE);
        StringBuilder sb = new StringBuilder();
        sb.append(tempId[0]);
        sb.append(CmnStringUtil.UNDERLINE);
        sb.append(EntryItemManagement.numbering(tempId[1]));
        return sb.toString();
    }

    /**
     * Compare the ID before and after the changes.<br/>
     * Against ID larger number, Return as the item ID it.<br/>
     * 
     * @param beforeId
     *            Change before ID
     * @param afterId
     *            After changing ID
     * @return Item ID
     */
    private static String compareItemId(String beforeId, String afterId) {
        // Comparison of old ID
        if (beforeId != null
                && CmnStringUtil.getTerminalNumber(beforeId) > CmnStringUtil
                        .getTerminalNumber(afterId)) {
            return beforeId;
        }
        return afterId;
    }

    /**
     * Numbered the last number of ID.<br/>
     * return the value 1 increase in ID.<br/>
     * 
     * @param id
     *            ID
     * @return ID
     */
    private static String numbering(String id) {
        int numbering = Integer.parseInt(id);
        numbering = numbering + 1;
        return String.valueOf(numbering);
    }

    /**
     * replace the key items of entry information.<br/>
     * 
     * @param key
     *            Item
     * @param Entry
     *            Entry information
     * @return entry Entry information
     */
    public static AbstractViewType replaceViewTypeKey(String key,
            PortabilityKnowhowListViewOperation entry) {
        EntryItemManagement.LOGGER.debug("[key]" + key + "[entry]" + entry);

        AbstractViewType viewType = entry.getKnowhowViewType();
        viewType.setRegisterKey(key);
        return viewType;
    }

    /**
     * Numbered the category Id.<br/>
     * 
     * @return Category Id.
     */
    public static String categoryNumbering() {
        EntryItemManagement.LOGGER.debug(CmnStringUtil.EMPTY);

        if (EntryItemManagement.categoryId != null) {
            EntryItemManagement.compareEntryItemId(EntryItemManagement
                    .numberingItemId(EntryItemManagement.categoryId),
                    EntryViewItemEnum.Category.getName());
        } else {
            EntryItemManagement.categoryId = ResourceUtil.initialCategoryID;
        }
        return EntryItemManagement.categoryId;
    }

    /**
     * Numbered the know-how Id.<br/>
     * 
     * @return Know-how Id
     */
    public static String knowhowNumbering() {
        EntryItemManagement.LOGGER.debug(CmnStringUtil.EMPTY);

        if (EntryItemManagement.knowhowId != null) {
            EntryItemManagement.compareEntryItemId(EntryItemManagement
                    .numberingItemId(EntryItemManagement.knowhowId),
                    EntryViewItemEnum.Knowhow.getName());
        } else {
            EntryItemManagement.knowhowId = ResourceUtil.initialKnowhowID;
        }
        return EntryItemManagement.knowhowId;
    }

    /**
     * Numbered the detailed know-how Id.<br/>
     * 
     * @return Detailed know-how Id.
     */
    public static String knowhowDetailNumbering() {
        EntryItemManagement.LOGGER.debug(CmnStringUtil.EMPTY);

        if (EntryItemManagement.knowhowDetailId != null) {
            EntryItemManagement.compareEntryItemId(EntryItemManagement
                    .numberingItemId(EntryItemManagement.knowhowDetailId),
                    EntryViewItemEnum.KnowhowDetail.getName());
        } else {
            EntryItemManagement.knowhowDetailId = ResourceUtil.initialKnowhowDetailID;
        }
        return EntryItemManagement.knowhowDetailId;
    }

    /**
     * Numbered the check item Id.<br/>
     * 
     * @return Check item Id.
     */
    public static String checkItemNumbering() {
        EntryItemManagement.LOGGER.debug(CmnStringUtil.EMPTY);

        if (EntryItemManagement.checkItemId != null) {
            EntryItemManagement.compareEntryItemId(EntryItemManagement
                    .numberingItemId(EntryItemManagement.checkItemId),
                    EntryViewItemEnum.CheckItem.getName());
        } else {
            EntryItemManagement.checkItemId = ResourceUtil.initialCheckItemID;
        }
        return EntryItemManagement.checkItemId;
    }

    /**
     * Numbered the search information Id.<br/>
     * 
     * @return Search information ID
     */
    public static String searchInfoNumbering() {
        EntryItemManagement.LOGGER.debug(CmnStringUtil.EMPTY);

        if (EntryItemManagement.searchInfoId != null) {
            EntryItemManagement.compareEntryItemId(EntryItemManagement
                    .numberingItemId(EntryItemManagement.searchInfoId),
                    EntryViewItemEnum.SearchInfo.getName());
        } else {
            EntryItemManagement.searchInfoId = ResourceUtil.initialSearchInfoID;
        }
        return EntryItemManagement.searchInfoId;
    }
}
