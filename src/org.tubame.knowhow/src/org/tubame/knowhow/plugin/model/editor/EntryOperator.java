/*
 * EntryOperator.java
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
package org.tubame.knowhow.plugin.model.editor;

import java.util.List;

/**
 * An interface to access the Entry.<br/>
 * JDK7
 */
public interface EntryOperator {
    /**
     * Get entryChildren.<br/>
     * 
     * @return Child hierarchy entry
     */
    public List<EntryOperator> getEntryChildren();

    /**
     * get the parent Entry of the specified level by the specified Entry.<br/>
     * 
     * @param level
     *            Level
     * @return Entry found
     */
    public EntryOperator getParentSameEntry(int level);

    /**
     * Get the key.<br/>
     * 
     * @return key
     */
    public String getKey();

    /**
     * Get the parent entry.<br/>
     * 
     * @return Parent entry
     */
    public EntryOperator getParentEntry();

    /**
     * Get the string to be displayed in the TreeViewer.<br/>
     * 
     * @return TreeViewer display name
     */
    public abstract String getTreeViewString();

    /**
     * Get the entry level.<br/>
     * 
     * @return Entry level
     */
    public abstract int getLevel();

    /**
     * Set the parent entry.<br/>
     * 
     * @param parentEntry
     *            Parent entry
     */
    public void setParentEntry(EntryOperator parentEntry);

    /**
     * Add a child element.<br/>
     * 
     * @param entry
     *            Child element
     */
    public void addChildren(EntryOperator entry);

    /**
     * Do a consistency check for the value.<br/>
     * 
     * @return Check result true:OK false:NG
     */
    public abstract boolean isValidate();

    /**
     * Find the Entry to be added.<br/>
     * Find child hierarchy, grandchild hierarchy to the Entry to be added.<br/>
     * 
     * @param entryList
     *            Search list
     * @return Discovery Entry
     */
    public EntryOperator getFindEntry(List<EntryOperator> entryList);

    /**
     * The specified instance determine whether category.<br/>
     * 
     * @return true:Category false:Category other than
     */
    public boolean isCategory();

    /**
     * The specified instance to determine whether heading.<br/>
     * 
     * @return true:Heading false:Non-heading
     */
    public boolean isChapter();

    /**
     * Set the name.<br/>
     * 
     * @param name
     *            Name
     */
    public void setName(String name);

}
