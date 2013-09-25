/*
 * JbmCsvWriter.java
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

import java.util.ArrayList;
import java.util.List;

import org.tubame.portability.exception.JbmException;
import org.tubame.portability.model.JbmEditorMigrationRow;
import org.tubame.portability.util.CsvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write results to file (csv) search results.<br/>
 */
public class JbmCsvWriter implements JbmWriter {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JbmCsvWriter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(String fileName, List<String> lineList)
            throws JbmException {
        LOGGER.debug("[fileName]" + fileName + "[lineList]" + lineList);
        CsvUtil.save(lineList, fileName, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(String fileName, List<JbmEditorMigrationRow> inputList)
            throws JbmException {
        LOGGER.debug("[fileName]" + fileName + "[inputList]" + inputList);
        List<String> list = new ArrayList<String>();
        for (JbmEditorMigrationRow first : inputList) {
            list.add(first.getWriteData());
        }
        CsvUtil.save(list, fileName, false);
    }
}
