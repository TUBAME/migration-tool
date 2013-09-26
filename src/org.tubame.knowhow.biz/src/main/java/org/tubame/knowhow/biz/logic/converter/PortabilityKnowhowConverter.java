/*
 * PortabilityKnowhowConverter.java
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
package org.tubame.knowhow.biz.logic.converter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.model.generated.knowhow.Category;
import org.tubame.knowhow.biz.model.generated.knowhow.Chapter;
import org.tubame.knowhow.biz.model.generated.knowhow.DocBook;
import org.tubame.knowhow.biz.model.generated.knowhow.EntryCategory;
import org.tubame.knowhow.biz.model.generated.knowhow.KnowhowInfomation;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow;
import org.tubame.knowhow.biz.model.generated.knowhow.SearchInfomation;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.CategoryList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.ChapterList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.DocBookList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.EntryViewList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.KnowhowList;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.SearchInfomationList;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

/**
 * Conversion to the data to be used by knowledge of expertise GUI XML data.<br/>
 */
public class PortabilityKnowhowConverter {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PortabilityKnowhowConverter.class);
    /** Transplant title know-how */
    private String portabilityKnowhowTitle;
    /** Heading tag information list */
    private List<Chapter> chapterList = new LinkedList<Chapter>();
    /** Know-how view information entry list */
    private List<EntryCategory> entryList = new LinkedList<EntryCategory>();
    /** Map of the category list */
    private Map<String, Category> categoryMap = new LinkedHashMap<String, Category>();
    /** Map of know-how list */
    private Map<String, KnowhowInfomation> knowhowMap = new LinkedHashMap<String, KnowhowInfomation>();
    /** Map of DocBook list */
    private Map<String, DocBook> docBookMap = new LinkedHashMap<String, DocBook>();
    /** Map of search information */
    private Map<String, SearchInfomation> searchInfoMap = new LinkedHashMap<String, SearchInfomation>();
    /** Know-how XML information */
    private PortabilityKnowhow portabilityKnowhow;

    /**
     * Constructor.<br/>
     * 
     * @param portabilityKnowhow
     *            PortabilityKnowhow
     */
    public PortabilityKnowhowConverter(PortabilityKnowhow portabilityKnowhow) {
        this.portabilityKnowhow = portabilityKnowhow;
    }

    /**
     * Split each information know-how from XML data, set to member variable.<br/>
     * 
     */
    public void createProtabilityKnowhow() {
        LOGGER.debug(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_PARAMA));
        portabilityKnowhowTitle = portabilityKnowhow
                .getPortabilityKnowhowTitle();
        createCategoryMap(portabilityKnowhow.getCategoryList());
        createChapterList(portabilityKnowhow.getChapterList());
        createDocBookMap(portabilityKnowhow.getDocBookList());
        createEntryList(portabilityKnowhow.getEntryViewList());
        createKnowhowMap(portabilityKnowhow.getKnowhowList());
        createSearchInfoMap(portabilityKnowhow.getSearchInfomationList());
    }

    /**
     * Create a category list map.<br/>
     * 
     * @param categoryList
     *            CategoryList
     */
    private void createCategoryMap(CategoryList categoryList) {
        for (Category category : categoryList.getCategories()) {
            categoryMap.put(category.getCategoryId(), category);
        }
    }

    /**
     * Create a map list know-how.<br/>
     * 
     * @param knowhowList
     *            KnowhowList
     */
    private void createKnowhowMap(KnowhowList knowhowList) {
        for (KnowhowInfomation knowhow : knowhowList.getKnowhowInfomations()) {
            knowhowMap.put(knowhow.getKnowhowId(), knowhow);
        }
    }

    /**
     * Create a search list information map.<br/>
     * 
     * @param searchInfomationList
     *            SearchInfomationList
     */
    private void createSearchInfoMap(SearchInfomationList searchInfomationList) {
        for (SearchInfomation searchInfo : searchInfomationList
                .getSearchInfomations()) {
            searchInfoMap.put(searchInfo.getSearchInfoId(), searchInfo);
        }
    }

    /**
     * Create a list Know how information map.<br/>
     * 
     * @param docBookList
     *            DocBookList
     */
    private void createDocBookMap(DocBookList docBookList) {
        for (DocBook docBook : docBookList.getDocBooks()) {
            docBookMap.put(docBook.getArticleId(), docBook);
        }
    }

    /**
     * Create a list heading.<br/>
     * 
     * @param chapterList
     *            ChapterList
     */
    private void createChapterList(ChapterList chapterList) {
        for (Chapter chapter : chapterList.getChapters()) {
            this.chapterList.add(chapter);
        }
    }

    /**
     * Create a know-how entry list view.<br/>
     * 
     * @param entryViewList
     *            EntryViewList
     */
    private void createEntryList(EntryViewList entryViewList) {
        for (EntryCategory entryCategory : entryViewList.getEntryCategories()) {
            entryList.add(entryCategory);
        }
    }

    /**
     * Get chapterList.<br/>
     * 
     * @return chapterList
     */
    public List<Chapter> getChapterList() {
        return chapterList;
    }

    /**
     * Set chapterList.<br/>
     * 
     * @param chapterList
     *            chapterList
     */
    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }

    /**
     * Get entryList.<br/>
     * 
     * @return entryList
     */
    public List<EntryCategory> getEntryList() {
        return entryList;
    }

    /**
     * Set entryList.<br/>
     * 
     * @param entryList
     *            entryList
     */
    public void setEntryList(List<EntryCategory> entryList) {
        this.entryList = entryList;
    }

    /**
     * Get categoryMap.<br/>
     * 
     * @return categoryMap
     */
    public Map<String, Category> getCategoryMap() {
        return categoryMap;
    }

    /**
     * Set categoryMap.<br/>
     * 
     * @param categoryMap
     *            categoryMap
     */
    public void setCategoryMap(Map<String, Category> categoryMap) {
        this.categoryMap = categoryMap;
    }

    /**
     * Get knowhowMap.<br/>
     * 
     * @return knowhowMap
     */
    public Map<String, KnowhowInfomation> getKnowhowMap() {
        return knowhowMap;
    }

    /**
     * Set knowhowMap.<br/>
     * 
     * @param knowhowMap
     *            knowhowMap
     */
    public void setKnowhowMap(Map<String, KnowhowInfomation> knowhowMap) {
        this.knowhowMap = knowhowMap;
    }

    /**
     * Get docBookMap.<br/>
     * 
     * @return docBookMap
     */
    public Map<String, DocBook> getDocBookMap() {
        return docBookMap;
    }

    /**
     * Set docBookMap.<br/>
     * 
     * @param docBookMap
     *            docBookMap
     */
    public void setDocBookMap(Map<String, DocBook> docBookMap) {
        this.docBookMap = docBookMap;
    }

    /**
     * Get searchInfoMap.<br/>
     * 
     * @return searchInfoMap
     */
    public Map<String, SearchInfomation> getSearchInfoMap() {
        return searchInfoMap;
    }

    /**
     * Set searchInfoMap.<br/>
     * 
     * @param searchInfoMap
     *            searchInfoMap
     */
    public void setSearchInfoMap(Map<String, SearchInfomation> searchInfoMap) {
        this.searchInfoMap = searchInfoMap;
    }

    /**
     * Get portabilityKnowhowTitle.<br/>
     * 
     * @return portabilityKnowhowTitle
     */
    public String getPortabilityKnowhowTitle() {
        return portabilityKnowhowTitle;
    }

    /**
     * Set portabilityKnowhowTitle.<br/>
     * 
     * @param portabilityKnowhowTitle
     *            portabilityKnowhowTitle
     */
    public void setPortabilityKnowhowTitle(String portabilityKnowhowTitle) {
        this.portabilityKnowhowTitle = portabilityKnowhowTitle;
    }

}
