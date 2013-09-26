/*
 * JbmWriter.java
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
package org.tubame.portability.logic.writer;

import java.util.List;

import org.tubame.portability.exception.JbmException;
import org.tubame.portability.model.JbmEditorMigrationRow;

/**
 * If create a search result file, implement this interface.<br/>
 */
public interface JbmWriter {
    /**
     * Create a search result file.<br/>
     * 
     * @param fileName
     *            File full path name
     * @param lineList
     *            Write data
     * @throws JbmException
     *             File write failure
     */
    public void save(String fileName, List<String> lineList)
            throws JbmException;

    /**
     * Overwrite file search results.
     * 
     * @param fileName
     *            File full path name
     * @param inputList
     *            Write data
     * @throws JbmException
     *             File write failure
     */
    public void update(String fileName, List<JbmEditorMigrationRow> inputList)
            throws JbmException;
}
