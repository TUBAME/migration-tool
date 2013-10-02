/*
 * CategoryEntry.java
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
package tubame.knowhow.plugin.model.editor;

import tubame.common.util.CmnStringUtil;

import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.CategoryViewType;

/**
 * Display category Entry class.<br/>
 * Display category data class know-how Editor (documentation created).<br/>
 * JDK7<br/>
 * 
 */
public class CategoryEntry extends AbstractEntry {

    /** Name */
    private String name;
    /** Category data entry */
    private CategoryViewType categoryViewData;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        return AbstractEntry.LEVEL_FIRST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTreeViewString() {
        if (getKey().startsWith(AbstractEntry.KNOWHOW_CATEGORY_KEY)) {
            return getName();
        }
        return CmnStringUtil.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidate() {
        boolean result = true;
        if (!isChildValidate(getEntryChildren())) {
            result = false;
        }
        return result;
    }

    /**
     * Get the name.<br/>
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the line number calculation.<br/>
     * 
     * @return true:calculate the number of lines false:Not line number
     *         calculation
     */
    public boolean isAppropriation() {
        return categoryViewData.isAppropriateFlag();
    }

    /**
     * Set the data entry category.<br/>
     * 
     * @param entryViewData
     *            Entry data
     */
    public void setEntryViewData(AbstractViewType entryViewData) {
        this.categoryViewData = (CategoryViewType) entryViewData;
    }

}
