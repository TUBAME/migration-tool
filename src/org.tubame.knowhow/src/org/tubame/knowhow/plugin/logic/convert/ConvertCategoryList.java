/*
 * ConvertCategoryList.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * Convert category list to the know-how for XML data.<br/>
 * Convert categories that exist in the list of categories under to the know-how
 * for XML data.<br/>
 * JDK7
 */
public class ConvertCategoryList {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConvertCategoryList.class);

    /**
     * Convert the category list.<br/>
     * 
     * @param dragViewItem
     *            Drag items
     * @param entryOperator
     *            EntryOperator
     * @return Drag items
     */
    public List<EntryOperator> convert(
            PortabilityKnowhowListViewOperation dragViewItem,
            EntryOperator entryOperator) {
        ConvertCategoryList.LOGGER.debug("[dragViewItem]" + dragViewItem
                + "[entryOperator]" + entryOperator);
        ConvertCategory convertCategory = new ConvertCategory();
        return convertCategory.convert(dragViewItem.getChildList(),
                entryOperator);
    }
}
