/*
 * CommandViewSearchInfo.java
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
package tubame.knowhow.plugin.logic.command;

import java.util.Map;

import tubame.knowhow.biz.model.generated.knowhow.SearchInformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import tubame.knowhow.biz.model.EntryViewItemEnum;

import tubame.knowhow.plugin.logic.EntryItemManagement;
import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.model.view.SearchInfoViewType;
import tubame.knowhow.util.ViewUtil;

/**
 * Converted to a search of know-how information data entry view.<br/>
 * Convert to search information data entry view of know-how from the know-how
 * for XML information.<br/>
 * JDK7<br/>
 */
public class CommandViewSearchInfo {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommandViewSearchInfo.class);
    /** Search information level */
    private static final int SEARCH_LEVEL = PortabilityKnowhowListViewData.LEVEL_FOURTH;
    /** Map data know-how */
    private Map<String, SearchInformation> searchInfoMap;

    /**
     * Constructor.<br/>
     * 
     * @param portabilityKnowhowConverter
     *            Know-how XML information
     */
    public CommandViewSearchInfo(
            PortabilityKnowhowConverter portabilityKnowhowConverter) {
        searchInfoMap = portabilityKnowhowConverter.getSearchInfoMap();
    }

    /**
     * Convert to search information data for View display.<br/>
     * Convert to search information data for View display from know-how XML
     * data.<br/>
     * 
     * @param parentCheckItem
     *            Parent hierarchy
     * @param searchRefKey
     *            Search key information reference
     */
    public void command(PortabilityKnowhowListViewOperation parentCheckItem,
            String searchRefKey) {
        CommandViewSearchInfo.LOGGER.debug("[parentCheckItem]"
                + parentCheckItem + "[searchRefKey]" + searchRefKey);
        PortabilityKnowhowListViewData searchInfo = new PortabilityKnowhowListViewData(
                parentCheckItem, SEARCH_LEVEL,
                knowhowXmlToEntryView(ViewUtil.getSearchInfo(searchRefKey,
                        searchInfoMap)));
        EntryItemManagement.compareEntryItemId(searchInfo.getKnowhowViewType()
                .getRegisterKey(), EntryViewItemEnum.SearchInfo.getName());
        ViewUtil.insertChildKnowhowEntryViewData(parentCheckItem, searchInfo);
    }

    /**
     * Convert to the View display data.<br/>
     * Convert know-how XML data to the View display data.<br/>
     * 
     * @param searchInfo
     *            SearchInformation
     * @return AbstractSampleViewData AbstractSampleViewData
     */
    private AbstractViewType knowhowXmlToEntryView(SearchInformation searchInfo) {
        return new SearchInfoViewType(searchInfo);
    }

}
