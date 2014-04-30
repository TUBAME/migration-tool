/*
 * KnowhowViewType.java
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

import java.math.BigInteger;

import tubame.knowhow.biz.model.generated.knowhow.KnowhowInformation;

import tubame.knowhow.plugin.ui.wizard.register.AbstractCreateKnowhowPage;

/**
 * Know-how information class know-how of entry view.<br/>
 * JDK7
 */
public class KnowhowViewType extends AbstractViewType {

    /** Serial ID */
    private static final long serialVersionUID = 47341130352309938L;
    /** know-how detail reference key */
    private String knowhowDetailRefKey;
    /** Know-how No */
    private BigInteger knowhowNo;

    /**
     * Constructor.<br/>
     * 
     * @param knowhowPageData
     *            Know-how information
     * @param keyValue
     *            Item ID
     */
    public KnowhowViewType(AbstractCreateKnowhowPage knowhowPageData,
            String keyValue) {
        super.setRegisterName(knowhowPageData.getRegisteredName());
        super.setRegisterKey(keyValue);
    }

    /**
     * Constructor.<br/>
     * 
     * @param knowhowInformation
     *            Know-how XML (know-how)
     */
    public KnowhowViewType(KnowhowInformation knowhowInformation) {
        super.setRegisterName(knowhowInformation.getKnowhowName());
        super.setRegisterKey(knowhowInformation.getKnowhowId());
        this.knowhowDetailRefKey = knowhowInformation.getKnowhowDetailRefKey();
        knowhowNo = knowhowInformation.getKnowhowNo();
    }

    /**
     * Get knowhowDetailRefKey.<br/>
     * 
     * @return knowhowDetailRefKey
     */
    public String getKnowhowDetailRefKey() {
        return knowhowDetailRefKey;
    }

    /**
     * Get knowhowNo.<br/>
     * 
     * @return knowhowNo
     */
    public BigInteger getKnowhowNo() {
        return knowhowNo;
    }

    /**
     * Set the know-how detail reference key.<br/>
     * 
     * @param knowhowDetailRefKey
     *            Reference key know-how
     */
    public void setKnowhowDetailRefKey(String knowhowDetailRefKey) {
        this.knowhowDetailRefKey = knowhowDetailRefKey;
    }

}
