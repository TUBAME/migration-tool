/*
 * EntryToKnowhowXmlConvert.java
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
package org.tubame.knowhow.plugin.logic.convert;

import java.util.List;

import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.CategoryList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.ChapterList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.DocBookList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.EntryViewList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.KnowhowList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.SearchInfomationList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.plugin.model.view.KnowhowDetailType;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * Convert to XML data from the know-how know-how GUI display data.<br/>
 * JDK7
 */
public class EntryToKnowhowXmlConvert {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntryToKnowhowXmlConvert.class);
    /** Heading information conversion object */
    private ChapterListConvert chapterListConvert = new ChapterListConvert();
    /** Know-how entry information conversion object */
    private EntryViewListConvert entryViewListConvert = new EntryViewListConvert();
    /** Know-how information conversion object */
    private KnowhowDataConvert knowhowDataConvert = new KnowhowDataConvert();
    /** Know how convert object */
    private DocBookConvert docBookConvert = new DocBookConvert();

    /**
     * Convert to the know-how XML data.<br/>
     * Create a know-how XML data from know-how GUI display data.<br/>
     * 
     * @param subjectName
     *            Subject Name
     * @param inputKnowhowEntry
     *            Know-how information list
     * @param inputChapterEntry
     *            Heading list information list
     * @param knowhowDetailTypes
     *            Know-how detail
     * @return Know-how XML data
     */
    public PortabilityKnowhow convertKnowhowEntry(String subjectName,
            List<PortabilityKnowhowListViewOperation> inputKnowhowEntry,
            List<EntryOperator> inputChapterEntry,
            List<KnowhowDetailType> knowhowDetailTypes) {
        EntryToKnowhowXmlConvert.LOGGER.debug("[subjectName]" + subjectName
                + "[inputKnowhowEntry]" + inputKnowhowEntry
                + "[inputChapterEntry]" + inputChapterEntry
                + "[knowhowDetailTypes]" + knowhowDetailTypes);

        PortabilityKnowhow portabilityKnowhow = new PortabilityKnowhow();
        // Set the transplant title know-how
        portabilityKnowhow.setPortabilityKnowhowTitle(subjectName);
        // Generate a list heading list
        ChapterList chapterList = chapterListConvert.convert(inputChapterEntry);

        // Generate a category list
        EntryViewList entryViewList = entryViewListConvert
                .convert(inputKnowhowEntry);

        // Generate category
        knowhowDataConvert.convert(inputKnowhowEntry);
        CategoryList categoryList = knowhowDataConvert.getCategoryList();
        KnowhowList knowhowList = knowhowDataConvert.getKnowhowList();
        SearchInfomationList searchInfomationList = knowhowDataConvert
                .getSearchInfomationList();

        docBookConvert.convert(knowhowDetailTypes);
        DocBookList docBookList = docBookConvert.getDocBookList();

        // Settings for each data
        portabilityKnowhow.setChapterList(chapterList);
        portabilityKnowhow.setEntryViewList(entryViewList);

        portabilityKnowhow.setCategoryList(categoryList);
        portabilityKnowhow.setKnowhowList(knowhowList);
        portabilityKnowhow.setSearchInfomationList(searchInfomationList);
        portabilityKnowhow.setDocBookList(docBookList);

        return portabilityKnowhow;
    }
}
