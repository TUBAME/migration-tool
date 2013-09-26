/*
 * EntryLabelProvider.java
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
package org.tubame.knowhow.plugin.ui.editor.multi.documentation;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.plugin.model.editor.AbstractEntry;
import org.tubame.knowhow.plugin.model.editor.CategoryEntry;
import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.util.ImageUtil;

/**
 * EntryLavelProvider class of heading the list tree view.<br/>
 * Data format is based on the assumption that the use of {@link AbstractEntry}.<br/>
 */
public class EntryLabelProvider extends ColumnLabelProvider {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntryLabelProvider.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(Object element) {
        LOGGER.debug("[element]" + element);

        String text = CmnStringUtil.EMPTY;
        if (element instanceof AbstractEntry) {
            EntryOperator row = (EntryOperator) element;
            text = row.getTreeViewString();
            // Remove the line breaks code
            text = text.replaceAll(CmnStringUtil.LINE_SEPARATOR,
                    CmnStringUtil.EMPTY);
        }
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage(Object element) {
        LOGGER.debug("[element]" + element);

        if (element instanceof AbstractEntry) {
            EntryOperator row = (EntryOperator) element;
            // First level (heading)
            if (row.isChapter()) {
                return ImageUtil.getChapterImage();
            }
            // Second level (category)
            if (row.isCategory()) {
                Image image = grantImageType(row);
                if (image != null) {
                    return image;
                }
                return ImageUtil.getCategoryImage();
            }
        }
        return null;
    }

    /**
     * Gives you the number of lines recorded / non-recorded image of (category)
     * child hierarchy of headings.<br/>
     * 
     * @param entryOperator
     *            Line data
     * @return Number of lines recorded / non-recorded image
     */
    private Image grantImageType(EntryOperator entryOperator) {

        if (entryOperator instanceof CategoryEntry) {
            CategoryEntry category = (CategoryEntry) entryOperator;
            if (!category.isAppropriation()) {
                return ImageUtil.getCategoryAppropriateImage();
            } else {
                return grantImageType(entryOperator.getParentEntry());
            }

        }
        return null;
    }

}
