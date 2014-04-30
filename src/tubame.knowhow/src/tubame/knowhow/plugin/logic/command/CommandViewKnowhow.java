/*
 * CommandViewKnowhow.java
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

import java.util.List;
import java.util.Map;

import tubame.knowhow.biz.model.generated.knowhow.KnowhowInformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import tubame.knowhow.biz.model.EntryViewItemEnum;
import tubame.knowhow.plugin.logic.EntryItemManagement;
import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.KnowhowViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.util.ViewUtil;

/**
 * Converted to the know-how of the data entry view for know-how.<br/>
 * Converted to the know-how of the data entry view for know-how from know-how
 * XML information.<br/>
 * JDK7<br/>
 */
public class CommandViewKnowhow {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommandViewKnowhow.class);
    /** Know-how level */
    private static final int KNNOWHOW_LEVEL = PortabilityKnowhowListViewData.LEVEL_SECOND;
    /** Map data know-how */
    private Map<String, KnowhowInformation> knowhowMap;
    /** Check items conversion command object */
    private CommandViewCheckItem commandViewCheckItem;

    /**
     * Constructor.<br/>
     * 
     * @param portabilityKnowhowConverter
     *            Know-how XML information
     */
    public CommandViewKnowhow(
            PortabilityKnowhowConverter portabilityKnowhowConverter) {
        this.knowhowMap = portabilityKnowhowConverter.getKnowhowMap();
        commandViewCheckItem = new CommandViewCheckItem(
                portabilityKnowhowConverter);
    }

    /**
     * Converted to the know-how of the View data for display.<br/>
     * Convert the data know-how of the View display from know-how XML data.<br/>
     * 
     * @param parentCategory
     *            Parent hierarchical data
     * @param knowhowRefKeies
     *            Know-how key reference list
     */
    public void command(PortabilityKnowhowListViewOperation parentCategory,
            List<String> knowhowRefKeies) {
        CommandViewKnowhow.LOGGER.debug("[parentCategory]" + parentCategory
                + "[knowhowRefKeies]" + knowhowRefKeies);
        for (String knowhowRefKey : knowhowRefKeies) {
            KnowhowInformation knowhowInformation = ViewUtil.getKnowhow(
                    knowhowRefKey, knowhowMap);
            PortabilityKnowhowListViewOperation knowhow = new PortabilityKnowhowListViewData(
                    parentCategory, KNNOWHOW_LEVEL,
                    knowhowXmlToEntryView(knowhowInformation));
            EntryItemManagement.compareEntryItemId(knowhow.getKnowhowViewType()
                    .getRegisterKey(), EntryViewItemEnum.Knowhow.getName());
            // Sign up EntryItemManagement Know how the know-how of key
            // information
            EntryItemManagement.compareEntryItemId(
                    knowhowInformation.getKnowhowDetailRefKey(),
                    EntryViewItemEnum.KnowhowDetail.getName());
            commandCheckItem(ViewUtil.insertChildKnowhowEntryViewData(
                    parentCategory, knowhow), knowhowInformation);
        }
    }

    /**
     * Convert the check items of know-how under.<br/>
     * Converts items check all information present in the know-how under to
     * display data.<br/>
     * 
     * @param parentKnowhow
     *            Parent hierarchical data
     * @param knowhowInformation
     *            Know-how information
     */
    private void commandCheckItem(
            PortabilityKnowhowListViewOperation parentKnowhow,
            KnowhowInformation knowhowInformation) {
        commandViewCheckItem.command(parentKnowhow,
                knowhowInformation.getCheckItems());
    }

    /**
     * Convert to the View display data.<br/>
     * convert know-how XML data to the View display data.<br/>
     * 
     * @param knowhowInformation
     *            KnowhowInformation
     * @return AbstractSampleViewData
     */
    private AbstractViewType knowhowXmlToEntryView(
            KnowhowInformation knowhowInformation) {
        return new KnowhowViewType(knowhowInformation);
    }
}
