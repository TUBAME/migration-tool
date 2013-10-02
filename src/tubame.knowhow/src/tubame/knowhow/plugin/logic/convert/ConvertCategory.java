/*
 * ConvertCategory.java
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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.editor.CategoryEntry;
import tubame.knowhow.plugin.model.editor.EntryOperator;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * Convert items that exist in the category under to the know-how for XML data.<br/>
 * JDK7<br/>
 */
public class ConvertCategory {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConvertCategory.class);

    /**
     * Get all of the Category.<br/>
     * Get the Category that exist in CategoryList tag under.<br/>
     * 
     * @param portabilityKnowhowList
     *            Know-how item list
     * @param entryOperator
     *            EntryOperator
     * @return Entry list
     */
    public List<EntryOperator> convert(
            List<PortabilityKnowhowListViewOperation> portabilityKnowhowList,
            EntryOperator entryOperator) {
        ConvertCategory.LOGGER.debug("[portabilityKnowhowList]"
                + portabilityKnowhowList + "[entryOperator]" + entryOperator);

        List<EntryOperator> entryOperators = new ArrayList<EntryOperator>();
        for (PortabilityKnowhowListViewOperation knowhowListViewOperation : portabilityKnowhowList) {
            if (knowhowListViewOperation.isCategory()) {
                CategoryEntry category = new CategoryEntry();
                category.setKey(knowhowListViewOperation.getKnowhowViewType()
                        .getRegisterKey());
                category.setName(knowhowListViewOperation.getName());
                category.setLevel(knowhowListViewOperation.getLevel());
                category.setEntryViewData(knowhowListViewOperation
                        .getKnowhowViewType());
                category.setEntryChildren(this.convert(
                        knowhowListViewOperation.getChildList(), category));
                category.setParentEntry(entryOperator);
                entryOperators.add(category);
            }
        }
        return entryOperators;
    }
}
