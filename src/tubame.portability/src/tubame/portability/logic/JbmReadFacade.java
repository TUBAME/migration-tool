/*
 * JbmReadFacade.java
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
package tubame.portability.logic;

import java.util.List;

import tubame.portability.exception.JbmException;
import tubame.portability.logic.reader.JbmCsvReader;
import tubame.portability.logic.reader.JbmReader;
import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.model.generated.model.JbossMigrationConvertTool;

/**
 * Jbm read Facade class.<br/>
 * The Read the XML format object from the conversion status or results file.<br/>
 */
public class JbmReadFacade {
    /**
     * Reads the JBM file and generate {@link JbmEditorMigrationRow}.<br/>
     * {@link JbmCsvReader}<br/>
     * 
     * @param filePath
     *            Read the target file path
     * @return {@link JbmEditorMigrationRow}
     * @throws JbmException
     *             JBM file read failure
     */
    public List<JbmEditorMigrationRow> read(String filePath)
            throws JbmException {
        return this.createReaderFactory().read(filePath);
    }

    /**
     * Reads the JBM file and generate {@link JbossMigrationConvertTool}.<br/>
     * {@link JbmCsvReader}<br/>
     * 
     * @param filePath
     *            Read the target file path
     * @return {@link JbmEditorMigrationRow}
     * @throws JbmException
     *             JBM file read failure
     */
    public JbossMigrationConvertTool readToJbossMigrationConvertTool(
            String filePath) throws JbmException {
        return this.createReaderFactory().readToJbossMigrationConvertTool(
                filePath);
    }

    /**
     * Generate JBM read Reader.<br/>
     * Factory method for generating a @ link JbmCsvReader} .<br/>
     * 
     * @return {@link JbmCsvReader}
     */
    protected JbmReader createReaderFactory() {
        return new JbmCsvReader();
    }
}
