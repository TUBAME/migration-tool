/*
 * CommandDocBook.java
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
package tubame.knowhow.plugin.logic.command;

import java.util.Map.Entry;

import tubame.knowhow.biz.model.generated.knowhow.DocBook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.view.KnowhowDetailType;

/**
 * Convert to the editor know-how for display DocBook data.<br/>
 * Convert to the editor know-how for display DocBook data that are defined in
 * the know-how XML.<br/>
 * JDK7
 */
public class CommandDocBook {
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommandDocBook.class);

    /**
     * Convert the data.<br/>
     * Convert the data in the know-how for the DocBook editor data.<br/>
     * 
     * @param Entry
     *            DocBook data Map
     * @return Know how tab display data
     */
    public KnowhowDetailType command(Entry<String, DocBook> entry) {
        CommandDocBook.LOGGER.debug("[DocBookMap]" + entry);
        KnowhowDetailType knowhowDetailType = new KnowhowDetailType();
        knowhowDetailType.setKnowhowDetailId(entry.getKey());
        knowhowDetailType.setDocbookdata(entry.getValue().getArticle());
        return knowhowDetailType;
    }

}
