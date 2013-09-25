/*
 * MigrationRowDelete.java
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
package org.tubame.portability.plugin.action;

import org.tubame.portability.model.MigrationEditorRow;
import org.tubame.portability.plugin.editor.MigrationEditorOperation;

/**
 * Delete line interface of the search results screen editing.<br/>
 * Each edit screen implemented this interface.<br/>
 */
public interface MigrationRowDelete {

    /**
     * The decision whether to delete each layer for each.<br/>
     * 
     * @param level
     *            Level
     * @return true:Delete OK false:Delete NG
     */
    public boolean isDeleteExecutor(int level);

    /**
     * Remove the specified object from the tree view object (@see
     * JbmEditorMigrationRow).<br/>
     * After deleting the specified object.<br/>
     * 
     * @param editor
     *            Editor
     * @param deleteRow
     *            Deleted row data
     */
    public void removeRowItem(MigrationEditorOperation editor,
            MigrationEditorRow deleteRow);

}
