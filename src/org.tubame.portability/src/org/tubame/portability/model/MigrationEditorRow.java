/*
 * MigrationEditorRow.java
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
package org.tubame.portability.model;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * The text display interface of the search results screen editing.<br/>
 * In displaying the data line of the search results edit, and implement this
 * interface.<br/>
 */
public interface MigrationEditorRow {
    /**
     * Get the string to be displayed in the editor.<br/>
     * Get the string to be displayed in each column different.<br/>
     * 
     * @param columnIndex
     *            Index column number
     * @return Editor display string
     */
    public String getColumnText(int columnIndex);

    /**
     * Get the image you want to display in the editor.<br/>
     * Get the image to be displayed in each column different.<br/>
     * 
     * @param columnIndex
     *            Index column number
     * @return Editor display image
     */
    public Image getColumnImage(int columnIndex);

    /**
     * Get a Font you want to display in the editor.<br/>
     * Get the Font for the row.<br/>
     * 
     * @return Font
     */
    public Font getFont();

    /**
     * Get the background color to be set in the editor.<br/>
     * Get the background color to be displayed in each column different.<br/>
     * 
     * @param columnIndex
     *            Index column number
     * @return Editor display background color
     */
    public Color getBackground(int columnIndex);

    /**
     * Get over fore ground color to be set in the editor.<br/>
     * 
     * @param columnIndex
     *            Index column number
     * @return Editor display fore over ground color
     */
    public Color getForeground(int columnIndex);

    /**
     * The decision whether to have the individual elements.<br/>
     * 
     * @return true:There are individual elements false:No individual element
     */
    public boolean hasChildren();

    /**
     * Get a list of the individual elements.<br/>
     * 
     * @return Each array element
     */
    public Object[] getChildren();

    /**
     * Get the (level) hierarchy.<br/>
     * 
     * @return Level
     */
    public int getLevel();

    /**
     * Get a first layer itself belongs.<br/>
     * 
     * @return First layer
     */
    public MigrationEditorRow getMoreParent();

    /**
     * Get the parent hierarchy.<br/>
     * 
     * @return Parent hierarchy
     */
    public MigrationEditorRow getParent();

    /**
     * Set the data of one line statement in the CSV in the first hierarchical
     * model.<br/>
     * 
     */
    public void updateWriteData();
}
