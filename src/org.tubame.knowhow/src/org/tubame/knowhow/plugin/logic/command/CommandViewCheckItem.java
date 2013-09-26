/*
 * CommandViewCheckItem.java
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

import org.tubame.knowhow.biz.model.generated.knowhow.CheckItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import org.tubame.knowhow.biz.model.EntryViewItemEnum;
import org.tubame.knowhow.plugin.logic.EntryItemManagement;
import org.tubame.knowhow.plugin.model.view.AbstractViewType;
import org.tubame.knowhow.plugin.model.view.CheckItemViewType;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.util.ViewUtil;

/**
 * Convert to check items of know-how data entry view.<br/>
 * convert to Check item data entry view of know-how from the know-how for XML
 * information.<br/>
 * JDK7
 */
public class CommandViewCheckItem {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommandViewCheckItem.class);
    /** Know-how level */
    private static final int CHECKITEM_LEVEL = PortabilityKnowhowListViewData.LEVEL_THIRD;
    /** Search information conversion command object */
    private CommandViewSearchInfo commandViewSearchInfo;

    /**
     * Constructor.
     * 
     * @param portabilityKnowhowConverter
     *            PortabilityKnowhowConverter
     */
    public CommandViewCheckItem(
            PortabilityKnowhowConverter portabilityKnowhowConverter) {
        commandViewSearchInfo = new CommandViewSearchInfo(
                portabilityKnowhowConverter);
    }

    /**
     * Convert the check data items.<br/>
     * Convert to check items of know-how data entry view display.<br/>
     * 
     * @param parentKnowhow
     *            PortabilityKnowhowListViewOperation
     * @param checkItems
     *            Check item list
     */
    public void command(PortabilityKnowhowListViewOperation parentKnowhow,
            List<CheckItem> checkItems) {
        CommandViewCheckItem.LOGGER.debug("[parentKnowhow]" + parentKnowhow
                + "[checkItems]" + checkItems);
        for (CheckItem checkItem : checkItems) {
            PortabilityKnowhowListViewData checkItemData = new PortabilityKnowhowListViewData(
                    parentKnowhow, CHECKITEM_LEVEL,
                    knowhowXmlToEntryView(checkItem));
            EntryItemManagement.compareEntryItemId(checkItemData
                    .getKnowhowViewType().getRegisterKey(),
                    EntryViewItemEnum.CheckItem.getName());

            commandSearchInfo(ViewUtil.insertChildKnowhowEntryViewData(
                    parentKnowhow, checkItemData), checkItem);
        }
    }

    /**
     * Convert the search information on the check items under.<br/>
     * Convert all to display data search information that exists in the check
     * items under.<br/>
     * 
     * @param parentCheckItem
     *            Parent hierarchical data
     * @param checkItem
     *            Check Item Information
     */
    private void commandSearchInfo(
            PortabilityKnowhowListViewOperation parentCheckItem,
            CheckItem checkItem) {
        if (checkItem.getSearchRefKey() != null) {
            commandViewSearchInfo.command(parentCheckItem,
                    checkItem.getSearchRefKey());
        }
    }

    /**
     * Convert to the View display data.<br/>
     * Convert know-how XML data to the View display data.<br/>
     * 
     * @param checkItem
     *            CheckItem
     * @return AbstractSampleViewData
     */
    private AbstractViewType knowhowXmlToEntryView(CheckItem checkItem) {
        return new CheckItemViewType(checkItem);
    }

}
