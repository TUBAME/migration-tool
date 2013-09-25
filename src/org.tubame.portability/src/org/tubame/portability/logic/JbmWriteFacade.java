/*
 * JbmWriteFacade.java
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
package org.tubame.portability.logic;

import java.util.List;

import org.tubame.portability.exception.JbmException;
import org.tubame.portability.logic.writer.JbmCsvWriter;
import org.tubame.portability.logic.writer.JbmWriter;
import org.tubame.portability.model.JbmEditorMigrationRow;

/**
 * Save the file jbm the data that is displayed on the screen.<br/>
 * jbm save: @ link JbmCsvWriter} reference.<br/>
 * 
 */
public class JbmWriteFacade {
    /**
     * Facade to Save the search results.<br/>
     * Generates each @ link JbmCsvWriter} , and save the results.<br/>
     * 
     * @param fileName
     *            File name
     * @param inputList
     *            Write data
     * @throws JbmException
     *             Write failure
     */
    public void update(String fileName, List<JbmEditorMigrationRow> inputList)
            throws JbmException {
        getJbmWriter().update(fileName, inputList);
    }

    /**
     * Facade you want to save the search results.<br/>
     * Generates each @ link JbmCsvWriter} , and save the results.<br/>
     * 
     * @param fileName
     *            File name
     * @param lineList
     *            String list data
     * @throws JbmException
     *             Write failure
     */
    public void save(String fileName, List<String> lineList)
            throws JbmException {
        getJbmWriter().save(fileName, lineList);
    }

    /**
     * Get the Writer of the search results.<br/>
     * 
     * @return {@link JbmCsvWriter}
     */
    protected JbmWriter getJbmWriter() {
        return new JbmCsvWriter();
    }
}
