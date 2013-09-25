/*
 * CategoryViewType.java
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
package org.tubame.knowhow.plugin.model.view;

import java.util.ArrayList;
import java.util.List;

import org.tubame.knowhow.biz.model.generated.knowhow.Category;
import org.tubame.knowhow.plugin.ui.wizard.register.AbstractCreateKnowhowPage;
import org.tubame.knowhow.plugin.ui.wizard.register.CreateCategoryPage;

/**
 * Category information class know-how of entry view.<br/>
 * JDK7<br/>
 */
public class CategoryViewType extends AbstractViewType {

    /** Serial ID */
    private static final long serialVersionUID = 3158626793040995777L;
    /** Number of lines recorded flag. */
    private boolean appropriateFlag;
    /** Reference key know-how */
    private List<String> knowhowRefKey = new ArrayList<String>();

    /**
     * Constructor.<br/>
     * 
     * @param knowhowPageData
     *            Entry registration page information
     * @param keyValue
     *            Item ID
     */
    public CategoryViewType(AbstractCreateKnowhowPage knowhowPageData,
            String keyValue) {
        CreateCategoryPage categoryPage = (CreateCategoryPage) knowhowPageData;
        super.setRegisterName(knowhowPageData.getRegisteredName());
        super.setRegisterKey(keyValue);
        appropriateFlag = categoryPage.isAppropriateLine();

    }

    /**
     * Constructor.<br/>
     * 
     * @param category
     *            Know-how XML information (category)
     */
    public CategoryViewType(Category category) {
        super.setRegisterName(category.getCategoryName());
        super.setRegisterKey(category.getCategoryId());
        this.appropriateFlag = category.isAppropriate();
    }

    /**
     * Get the line number calculation.<br/>
     * 
     * @return Line number calculation
     */
    public boolean isAppropriateFlag() {
        return this.appropriateFlag;
    }

    /**
     * Set the line number calculation.<br/>
     * 
     * @param appropriateFlag
     *            Line number calculation
     */
    public void setAppropriateFlag(boolean appropriateFlag) {
        this.appropriateFlag = appropriateFlag;
    }

    /**
     * Get the know-how key reference list.<br/>
     * 
     * @return Know-how key reference list
     */
    public List<String> getKnowhowRefKeies() {
        return knowhowRefKey;
    }

    /**
     * Add a reference key know-how to know-how key reference list.<br/>
     * 
     * @param key
     *            Reference know-how key
     */
    public void addKnowhowRefKey(String key) {
        knowhowRefKey.add(key);
    }

    /**
     * Reference key know-how.<br/>
     * 
     */
    public void clearKnowhowRefKey() {
        knowhowRefKey.clear();
    }
}
