/*
 * DocBookConvert.java
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
package tubame.knowhow.plugin.logic.convert;

import java.util.List;

import tubame.knowhow.biz.model.generated.knowhow.DocBook;
import tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow.DocBookList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.view.KnowhowDetailType;

/**
 * Classes for converting the DocBook data.<br/>
 * Class that converts DocBook data input by the editor expertise to the
 * Know-how XML data.<br/>
 * JDK7
 */
public class DocBookConvert {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocBookConvert.class);
    /** DocBook list */
    private DocBookList docBookList = new DocBookList();

    /**
     * Converted to the know-how XML data.<br/>
     * Converted the data registered in the know-how editor to the know-how XML
     * data.<br/>
     * 
     * @param knowhowDetailTypes
     *            Know how editor list
     */
    public void convert(List<KnowhowDetailType> knowhowDetailTypes) {
        DocBookConvert.LOGGER
                .debug("[knowhowDetailTypes]" + knowhowDetailTypes);
        for (KnowhowDetailType knowhowDetailType : knowhowDetailTypes) {
            DocBook docBook = new DocBook();
            docBook.setArticleId(knowhowDetailType.getKnowhowDetailId());
            docBook.setArticle(knowhowDetailType.getDocbookdata());

            docBookList.getDocBooks().add(docBook);
        }
    }

    /**
     * Get DocBookList.<br/>
     * 
     * @return DocBookList DocBookList
     */
    public DocBookList getDocBookList() {
        return docBookList;
    }

}
