/*
 * PortabilityKnowhowListViewData.java
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

import java.util.LinkedList;
import java.util.List;

/**
 * Line data entry class know-how of view.<br/>
 * Class that defines the model of each item of know-how entry view.<br/>
 */
public class PortabilityKnowhowListViewData implements
        PortabilityKnowhowListViewOperation {

    /** Serial ID */
    private static final long serialVersionUID = -6717446166301184199L;
    /** First level (category) */
    public static final int LEVEL_FIRST = 0;
    /** Second level (know-how) */
    public static final int LEVEL_SECOND = 1;
    /** Third layer (check item information) */
    public static final int LEVEL_THIRD = 2;
    /** The fourth level (information retrieval) */
    public static final int LEVEL_FOURTH = 3;
    /** Level */
    private int level;
    /** Parent element */
    private PortabilityKnowhowListViewOperation parent;
    /** Display data */
    private AbstractViewType viewType;
    /** Display data */
    private List<PortabilityKnowhowListViewOperation> childList = new LinkedList<PortabilityKnowhowListViewOperation>();

    /**
     * Constructor.<br/>
     * 
     * @param parent
     *            Parent element
     * @param level
     *            Level
     * @param knowhowData
     *            Registration information
     */
    public PortabilityKnowhowListViewData(
            PortabilityKnowhowListViewOperation parent, int level,
            AbstractViewType knowhowData) {
        super();
        this.parent = parent;
        this.level = level;
        this.viewType = knowhowData;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        return level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return viewType.getRegisterName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PortabilityKnowhowListViewOperation> getChildList() {
        return childList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortabilityKnowhowListViewOperation getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortabilityKnowhowListViewOperation getKnowhowParentEntry() {
        return getParentSamePortabilityKnowhowListViewOperation(
                PortabilityKnowhowListViewData.LEVEL_SECOND, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortabilityKnowhowListViewOperation getCategoryParentEntry() {
        return getParentSamePortabilityKnowhowListViewOperation(
                PortabilityKnowhowListViewData.LEVEL_FIRST, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractViewType getKnowhowViewType() {
        return viewType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCheckItem() {
        if (PortabilityKnowhowListViewData.LEVEL_THIRD == getLevel()) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isKnowhow() {
        if (PortabilityKnowhowListViewData.LEVEL_SECOND == getLevel()) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCategory() {
        if (PortabilityKnowhowListViewData.LEVEL_FIRST == getLevel()) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSearchInfo() {
        if (PortabilityKnowhowListViewData.LEVEL_FOURTH == getLevel()) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent(
            PortabilityKnowhowListViewOperation knowhowListViewOperation) {
        this.parent = knowhowListViewOperation;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSubjectName(String subjectName) {
        this.viewType.setRegisterName(subjectName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setKnowhowViewType(AbstractViewType abstractViewType) {
        this.viewType = abstractViewType;

    }

    /**
     * Get parent Entry.<br/>
     * Get than the specified Entry (specified) the parent of the Entry level.<br/>
     * 
     * @param level
     *            Level
     * @param entry
     *            Entry
     * @return Parent Entry
     */
    private PortabilityKnowhowListViewOperation getParentSamePortabilityKnowhowListViewOperation(
            int level, PortabilityKnowhowListViewOperation entry) {
        if (level == entry.getLevel()) {
            return entry;
        }
        if (level > entry.getLevel()) {
            return null;
        }
        PortabilityKnowhowListViewOperation parentData = entry.getParent();
        return getParentSamePortabilityKnowhowListViewOperation(level,
                parentData);
    }

}
