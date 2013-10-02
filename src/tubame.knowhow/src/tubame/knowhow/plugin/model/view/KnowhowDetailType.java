/*
 * KnowhowDetailType.java
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

import org.docbook.ns.docbook.Article;

/**
 * Class that defines the model of know-how detailed data.<br/>
 * JDK7
 */
public class KnowhowDetailType {

    /** know-how detail ID */
    private String knowhowDetailId;
    /** DocBook data */
    private Article docbookdata;

    /**
     * Get know-how detail ID.<br/>
     * 
     * @return know-how detail ID
     */
    public String getKnowhowDetailId() {
        return knowhowDetailId;
    }

    /**
     * Set the Know how detail ID.<br/>
     * 
     * @param knowhowDetailId
     *            Know-how detail ID
     */
    public void setKnowhowDetailId(String knowhowDetailId) {
        this.knowhowDetailId = knowhowDetailId;
    }

    /**
     * Get DocBook data.<br/>
     * 
     * @return DocBook data
     */
    public Article getDocbookdata() {
        return docbookdata;
    }

    /**
     * Set DocBook data.<br/>
     * 
     * @param docbookdata
     *            DocBook data
     */
    public void setDocbookdata(Article docbookdata) {
        this.docbookdata = docbookdata;
    }

}
