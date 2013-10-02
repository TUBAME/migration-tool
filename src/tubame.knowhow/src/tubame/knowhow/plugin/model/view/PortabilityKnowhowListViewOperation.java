/*
 * PortabilityKnowhowListViewOperation.java
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
package tubame.knowhow.plugin.model.view;

import java.io.Serializable;
import java.util.List;

/**
 * Interface of row operations know-how of transplant list view.<br/>
 * JDK7
 */
public interface PortabilityKnowhowListViewOperation extends Serializable {

    /**
     * Get hierarchy.<br/>
     * 
     * @return Hierarchy
     */
    public int getLevel();

    /**
     * Get the display name.<br/>
     * 
     * @return Display Name
     */
    public String getName();

    /**
     * Get the child element.<br/>
     * 
     * @return Subelement
     */
    public List<PortabilityKnowhowListViewOperation> getChildList();

    /**
     * Get the parent element.<br/>
     * 
     * @return Parent element
     */
    public PortabilityKnowhowListViewOperation getParent();

    /**
     * Set the parent element.<br/>
     * 
     * @param knowhowListViewOperation
     *            PortabilityKnowhowListViewOperation
     */
    public void setParent(
            PortabilityKnowhowListViewOperation knowhowListViewOperation);

    /**
     * Get the parent item of know-how.<br/>
     * 
     * @return PortabikityKnowhowListViewOperation
     */
    public PortabilityKnowhowListViewOperation getKnowhowParentEntry();

    /**
     * Get the parent item of the category.<br/>
     * 
     * @return PortabikityKnowhowListViewOperation
     */
    public PortabilityKnowhowListViewOperation getCategoryParentEntry();

    /**
     * Get registration information items.<br/>
     * 
     * @return AbstractViewType
     */
    public AbstractViewType getKnowhowViewType();

    /**
     * Set the registration information items.<br/>
     * 
     * @param abstractViewType
     *            AbstractViewType
     */
    public void setKnowhowViewType(AbstractViewType abstractViewType);

    /**
     * Set the subject name.<br/>
     * 
     * @param subjectName
     *            Subject Name
     */
    public void setSubjectName(String subjectName);

    /**
     * Determine whether check items for the specified instance.<br/>
     * 
     * @return true:Check items false:Check items other than
     */
    public boolean isCheckItem();

    /**
     * Determine whether the know-how for the specified instance.<br/>
     * 
     * @return true:Know-how false:Know-how
     */
    public boolean isKnowhow();

    /**
     * Determine whether the category for a given instance.<br/>
     * 
     * @return true:Category false:Category other than
     */
    public boolean isCategory();

    /**
     * Determine whether the search information about the specified instance.<br/>
     * 
     * @return true:Search information false:Search information
     */
    public boolean isSearchInfo();

}
