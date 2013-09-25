/*
 * CommandKnowhowDataList.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tubame.common.util.CmnStringUtil;
import org.tubame.knowhow.biz.model.generated.knowhow.Chapter;
import org.tubame.knowhow.biz.model.generated.knowhow.DocBook;
import org.tubame.knowhow.biz.model.generated.knowhow.EntryCategory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.plugin.model.view.KnowhowDetailType;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * Class to be converted to display data of know-how know-how from the GUI XML.<br/>
 * JDK7<br/>
 */
public class CommandKnowhowDataList {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommandKnowhowDataList.class);
    /** Know-how data entry view */
    private List<PortabilityKnowhowListViewOperation> knowhowListViewOperations = new ArrayList<PortabilityKnowhowListViewOperation>();
    /** Transplant title know-how */
    private String portabilityKnowhowTitle;
    /** Heading the list data */
    private List<EntryOperator> entryOperators = new ArrayList<EntryOperator>();
    /** Know how detail information */
    private List<KnowhowDetailType> knowhowDetailTypes = new ArrayList<KnowhowDetailType>();
    /** Category Conversion command object */
    private CommandViewCategory commandCategory;
    /** Heading conversion command object */
    private CommandEditorChapter commandChapter;
    /** DocBook conversion command object */
    private CommandDocBook commandDocBook;
    /** Know-how XML information */
    private PortabilityKnowhowConverter portabilityKnowhowConverter;

    /**
     * Constructor.<br/>
     * 
     * @param portabilityKnowhowConverter
     *            Know-how XML information
     */
    public CommandKnowhowDataList(
            PortabilityKnowhowConverter portabilityKnowhowConverter) {
        this.portabilityKnowhowConverter = portabilityKnowhowConverter;
        this.portabilityKnowhowTitle = portabilityKnowhowConverter
                .getPortabilityKnowhowTitle();
        this.commandCategory = new CommandViewCategory(
                portabilityKnowhowConverter);
        this.commandChapter = new CommandEditorChapter(
                portabilityKnowhowConverter);
        this.commandDocBook = new CommandDocBook();

    }

    /**
     * Convert the data in the know-how for GUI display.<br/>
     * Convert the data in the GUI display for know-how know-how from XML data.<br/>
     * 
     */
    public void convert() {
        CommandKnowhowDataList.LOGGER.debug(CmnStringUtil.EMPTY);
        // Creating the index data
        convertEntryOperators(portabilityKnowhowConverter.getChapterList());
        // Creation of know-how entry list
        convertKnowhowListViewOperations(portabilityKnowhowConverter
                .getEntryList());
        convertDocBookList(portabilityKnowhowConverter.getDocBookMap());

    }

    /**
     * Convert the know-how more information.<br/>
     * Convert the Know how detail information of data Know-how for the GUI from
     * the know-how XML data.<br/>
     * 
     * @param docBookMap
     *            Map<String, DocBook
     */
    private void convertDocBookList(Map<String, DocBook> docBookMap) {
        for (Map.Entry<String, DocBook> entry : docBookMap.entrySet()) {
            knowhowDetailTypes.add(commandDocBook.command(entry));
        }
    }

    /**
     * Convert to the display of data entry view for know-how.<br/>
     * 
     * @param entryCategories
     *            List<EntryCategory>
     */
    private void convertKnowhowListViewOperations(
            List<EntryCategory> entryCategories) {
        for (EntryCategory entryCategory : entryCategories) {
            knowhowListViewOperations.add(commandCategory
                    .command(entryCategory));
        }
    }

    /**
     * Convert to the display data (editor) for heading the list.<br/>
     * 
     * @param chapterList
     *            List<Chapter>
     */
    private void convertEntryOperators(List<Chapter> chapterList) {

        for (Chapter chapter : chapterList) {
            entryOperators.add(commandChapter.command(chapter));
        }

    }

    /**
     * get knowhowListViewOperations.et knowhowListViewOperations.<br/>
     * 
     * @return knowhowListViewOperations
     */
    public List<PortabilityKnowhowListViewOperation> getKnowhowListViewOperations() {
        return knowhowListViewOperations;
    }

    /**
     * get entryOperators.<br/>
     * 
     * @return entryOperators
     */
    public List<EntryOperator> getEntryOperators() {
        return entryOperators;
    }

    /**
     * Get the know-how more information.<br/>
     * 
     * @return List<KnowhowDetailType>
     */
    public List<KnowhowDetailType> getKnowhowDetailTypes() {
        return knowhowDetailTypes;
    }

    /**
     * Get portabilityKnowhowTitle.<br/>
     * 
     * @return portabilityKnowhowTitle
     */
    public String getPortabilityKnowhowTitle() {
        return portabilityKnowhowTitle;
    }

}
