/*
 * CommandEditorChapter.java
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

import org.tubame.knowhow.biz.model.generated.knowhow.Category;
import org.tubame.knowhow.biz.model.generated.knowhow.Chapter;
import org.tubame.knowhow.biz.model.generated.knowhow.ChildChapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import org.tubame.knowhow.plugin.model.editor.AbstractEntry;
import org.tubame.knowhow.plugin.model.editor.CategoryEntry;
import org.tubame.knowhow.plugin.model.editor.ChapterEntry;
import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.plugin.model.view.CategoryViewType;
import org.tubame.knowhow.util.ViewUtil;

/**
 * Convert the data in the heading for the list from the know-how XML.<br/>
 * Convert the documentation tag data editor of know-how, the information of
 * chapter tag of know-how XML.<br/>
 * JDK7
 */
public class CommandEditorChapter {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommandEditorChapter.class);
    /** Know-how XML information */
    private PortabilityKnowhowConverter portabilityKnowhowConverter;

    /**
     * Constructor.<br/>
     * 
     * @param portabilityKnowhowConverter
     *            Know-how XML information
     */
    public CommandEditorChapter(
            PortabilityKnowhowConverter portabilityKnowhowConverter) {
        this.portabilityKnowhowConverter = portabilityKnowhowConverter;
    }

    /**
     * Convert the heading data.<br/>
     * Convert the documentation tag data editor of know-how.<br/>
     * 
     * @param chapter
     *            Heading tag information
     * @return EntryOperator Documentation tag data
     */
    public EntryOperator command(Chapter chapter) {
        CommandEditorChapter.LOGGER.debug("[chapter]" + chapter);
        ChapterEntry chapterEntry = new ChapterEntry();
        chapterEntry.setName(chapter.getChapterName());
        chapterEntry.setKey(chapter.getChapterNo());
        chapterEntry.setLevel(AbstractEntry.LEVEL_ZERO);
        commandCategoryChapter(chapter.getChildChapters(), chapterEntry);
        return chapterEntry;
    }

    /**
     * Convert the data in the child hierarchy heading.<br/>
     * Convert the documentation tag data editor of know-how.<br/>
     * 
     * @param childChapters
     *            Child hierarchy heading tag information
     * @param chapterEntry
     *            Documentation display data
     */
    private void commandCategoryChapter(List<ChildChapter> childChapters,
            AbstractEntry chapterEntry) {
        for (ChildChapter childChapter : childChapters) {
            CategoryEntry categoryEntry = new CategoryEntry();
            Category category = ViewUtil.getCategory(
                    childChapter.getChapterCategoryRefKey(),
                    portabilityKnowhowConverter.getCategoryMap());
            categoryEntry.setName(category.getCategoryName());
            categoryEntry.setKey(category.getCategoryId());
            categoryEntry.setEntryViewData(new CategoryViewType(category));
            categoryEntry.setLevel(AbstractEntry.LEVEL_FIRST);
            categoryEntry.setParentEntry(chapterEntry);
            if (!childChapter.getChildChapters().isEmpty()) {
                commandCategoryChapter(childChapter.getChildChapters(),
                        categoryEntry);
            }
            chapterEntry.addChildren(categoryEntry);
        }
    }

}
