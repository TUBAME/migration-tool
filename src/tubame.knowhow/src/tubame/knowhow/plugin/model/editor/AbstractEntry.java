/*
 * AbstractEntry.java
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Entry display base class.<br/>
 * Display Entry class of know-how documentation editor.<br/>
 * 
 */
public abstract class AbstractEntry implements EntryOperator {

    /** Classes that inherit from this class. */
    private static final List<AbstractEntry> INSTANCELIST = Arrays.asList(
            new ChapterEntry(), new CategoryEntry());
    /** 0th level */
    public static final int LEVEL_ZERO = 0;
    /** First level */
    public static final int LEVEL_FIRST = 1;
    /** Category key */
    public static final String KNOWHOW_CATEGORY_KEY = "category";
    /** Chapter key */
    public static final String KNOWHOW_CHAPTER_KEY = "chapter";
    /** Level */
    private int level;
    /** key */
    private String key;
    /** Parent information */
    private EntryOperator parentEntry;
    /** Child list */
    private List<EntryOperator> entryChildren = new ArrayList<EntryOperator>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChildren(EntryOperator entry) {
        entryChildren.add(entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryOperator getFindEntry(List<EntryOperator> entryList) {
        for (EntryOperator entry : entryList) {
            if (equals(entry)) {
                return entry;
            }
            EntryOperator temp = getFindEntry(entry.getEntryChildren());
            if (temp != null) {
                return temp;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryOperator getParentSameEntry(int level) {
        if (level == getLevel()) {
            return this;
        }
        if (level > getLevel()) {
            return null;
        }
        EntryOperator parent = getParentEntry();
        return parent.getParentSameEntry(level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * Set the key.
     * 
     * @param key
     *            key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryOperator getParentEntry() {
        return parentEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParentEntry(EntryOperator parentEntry) {
        this.parentEntry = parentEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EntryOperator> getEntryChildren() {
        return entryChildren;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        return level;
    }

    /**
     * Set the entryChildren.<br/>
     * 
     * @param entryChildren
     *            entryChildren
     */
    public void setEntryChildren(List<EntryOperator> entryChildren) {
        this.entryChildren = entryChildren;
    }

    /**
     * Set the level.<br/>
     * 
     * @param level
     *            level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Get instancelist.<br/>
     * 
     * @return instancelist
     */
    public static List<AbstractEntry> getInstancelist() {
        return AbstractEntry.INSTANCELIST;
    }

    /**
     * Get the Entry of the same key in the list Entry.<br/>
     * Return a Null if they do not match.<br/>
     * 
     * @param entryList
     *            List of Entry
     * @param key
     *            Key string
     * @return !=Null DiscoveryEntry ==NULL Undiscovered
     */
    public static EntryOperator getKeyEntry(List<EntryOperator> entryList,
            String key) {
        if (entryList != null) {
            for (EntryOperator entry : entryList) {
                if (key.equals(entry.getKey())) {
                    return entry;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isChapter() {
        return isInstanceEntry(AbstractEntry.getInstancelist().get(
                AbstractEntry.LEVEL_ZERO));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCategory() {
        return isInstanceEntry(AbstractEntry.getInstancelist().get(
                AbstractEntry.LEVEL_FIRST));
    }

    /**
     * Return Entry to be compared with the comparison source is whether they
     * match.<br/>
     * 
     * @param comparisonEntry
     *            Compared Entry
     * @return true:Match false:Mismatch
     */
    private boolean isInstanceEntry(EntryOperator comparisonEntry) {
        if (comparisonEntry != null && getLevel() == comparisonEntry.getLevel()) {
            return true;
        }
        return false;
    }

    /**
     * Do the Validate the child hierarchy of categories below.<br/>
     * Do Validate (know-how, check items, search information) of the child
     * hierarchy of categories below.<br/>
     * 
     * @param entryList
     *            List of Entry
     * @return Validate the results true:OK false:NG
     */
    protected boolean isChildValidate(List<EntryOperator> entryList) {
        boolean result = true;
        for (EntryOperator child : entryList) {
            if (!child.isValidate()) {
                result = false;
            }
            if (!isChildValidate(child.getEntryChildren())) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Check child hierarchy exists.<br/>
     * Check child hierarchy is whether there is more than one.<br/>
     * 
     * @return Check result true:OK false:NG
     */
    protected boolean hasChildrenEntry() {
        if (getEntryChildren() == null || getEntryChildren().size() == 0) {
            return false;
        }
        return true;
    }
}
