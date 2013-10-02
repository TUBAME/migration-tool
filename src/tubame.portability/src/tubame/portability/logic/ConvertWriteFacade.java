/*
 * ConvertWriteFacade.java
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
import tubame.portability.logic.writer.ConvertWriter;
import tubame.portability.logic.writer.ConvertXMLWriter;
import tubame.portability.logic.writer.JbmCsvWriter;
import tubame.portability.model.generated.model.MigrationItem;

/**
 * Facade you want to save the file conversion result.<br/>
 * Conversion result file save process, {@link JbmCsvWriter} reference.<br/>
 */
public class ConvertWriteFacade {
    /**
     * Save the file conversion status. Generates each {@link ConvertXMLWriter},
     * and save the results.<br/>
     * 
     * @param migrationItemList
     *            MigrationItem list
     * @param filePath
     *            File path to create
     * @throws JbmException
     *             Plug-in exception
     */
    public void save(List<MigrationItem> migrationItemList, String filePath)
            throws JbmException {
        getConvertWrite().write(migrationItemList, filePath);
    }

    /**
     * Get the conversion status file write implementation class.<br/>
     * 
     * @return @see ConvertXMLWriter
     */
    protected ConvertWriter getConvertWrite() {
        return new ConvertXMLWriter();
    }

}
