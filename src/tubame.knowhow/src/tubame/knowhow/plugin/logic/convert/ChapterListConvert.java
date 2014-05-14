/*
 * ChapterListConvert.java
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
package tubame.knowhow.plugin.logic.convert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.model.generated.knowhow.Chapter;
import tubame.knowhow.biz.model.generated.knowhow.ChildChapter;
import tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.ChapterList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.editor.CategoryEntry;
import tubame.knowhow.plugin.model.editor.EntryOperator;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Converted to the know-how XML data.<br/>
 * Converts heading list information that you created in know-how GUI to
 * know-how XML data.<br/>
 * JDK7
 */
public class ChapterListConvert {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ChapterListConvert.class);

    /**
     * Converted to the know-how XML data.<br/>
     * Converted to the know-how XML data from the header information.<br/>
     * 
     * @param inputChapterEntry
     *            Heading information
     * @return Heading the list
     */
    public ChapterList convert(List<EntryOperator> inputChapterEntry) {
        ChapterListConvert.LOGGER.debug("[inputChapterEntry]"
                + inputChapterEntry);
        ChapterList chapterList = new ChapterList();
        StringBuilder topChapterNo = new StringBuilder();
        topChapterNo.append(ResourceUtil.initialChapterNo);
        for (EntryOperator entryOperator : inputChapterEntry) {
            topChapterNo = createChapterNo(topChapterNo);
            Chapter chapter = new Chapter();
            chapter.setChapterNo(topChapterNo.toString());
            chapter.setChapterName(entryOperator.getTreeViewString());
            convertChapterCategory(entryOperator, chapter.getChildChapters(),
                    topChapterNo);
            chapterList.getChapters().add(chapter);
        }
        return chapterList;
    }

    /**
     * Converted header information to the know-how XML data.<br/>
     * Heading information is converted heading data of child hierarchy
     * following into know-how XML data.<br/>
     * 
     * @param entryOperator
     *            EntryOperator
     * @param childChapters
     *            List<ChildChapter>
     * @param chapterNo
     *            Chapter number
     */
    private void convertChapterCategory(EntryOperator entryOperator,
            List<ChildChapter> childChapters, StringBuilder chapterNo) {
        StringBuilder chapterNoBuilder = new StringBuilder();
        chapterNoBuilder.append(chapterNo);
        chapterNoBuilder.append(ResourceUtil.initialChapterNo);

        for (EntryOperator categoryOperator : entryOperator.getEntryChildren()) {
            chapterNoBuilder = createChapterNo(chapterNoBuilder);
            ChildChapter childChapter = new ChildChapter();
            CategoryEntry category = (CategoryEntry) categoryOperator;
            childChapter.setChildChapterNo(chapterNoBuilder.toString());
            childChapter.setChapterCategoryRefKey(category.getKey());
            this.convertChapterCategory(categoryOperator,
                    childChapter.getChildChapters(), chapterNoBuilder);
            childChapters.add(childChapter);
        }
    }

    /**
     * Generate the chapter number.<br/>
     * 
     * @param chapterNo
     *            Chapter number
     * @return chapterNo Chapter number
     */
    private StringBuilder createChapterNo(StringBuilder chapterNo) {
        String[] tempStrList = chapterNo.toString().split(
                CmnStringUtil.SPRITTING_PERIOD);
        Map<Integer, String> tempStrMap = new LinkedHashMap<Integer, String>();
        int tempStrKey = 0;
        for (String str : tempStrList) {
            tempStrMap.put(tempStrKey, str);
            tempStrKey++;
        }
        replaceIncrementNo(tempStrMap, tempStrKey - 1);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, String> entry : tempStrMap.entrySet()) {
            sb.append(entry.getValue());
            sb.append(CmnStringUtil.PERIOD);
        }
        return sb;
    }

    /**
     * Increment the number of terminal number.<br/>
     * 
     * @param tempStrMap
     *            Map<Integer, String>
     * @param tempStrKey
     *            End number key
     */
    private void replaceIncrementNo(Map<Integer, String> tempStrMap,
            int tempStrKey) {
        int numbering = Integer.parseInt(tempStrMap.get(tempStrKey));
        numbering = numbering + 1;
        tempStrMap.put(tempStrKey, String.valueOf(numbering));
    }
}
