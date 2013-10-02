/*
 * ConfirmItemEnum.java
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
package tubame.portability.model;

import org.eclipse.swt.graphics.Color;

import tubame.portability.util.ColorUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * An enumeration type class that represents the display color and <br/>
 * display text of visual confirmation behavior.<br/>
 * Type is as follows.<br/>
 * 
 * Unconfirmed Confirmed (on transplantation) Confirmed (transplant unnecessary)
 */
public enum ConfirmItemEnum {

    /**
     * NO
     */
    STATUS_NON(0),
    /**
     * OK
     */
    STATUS_OK(1),
    /**
     * NG
     */
    STATUS_NG(2);

    /**
     * Type (0: not 1: Confirmed 2: Confirmed (non-))
     */
    private int type;

    /**
     * Constructor.<br/>
     * Can not be instantiated from outside.<br/>
     * 
     * @param type
     *            Type (0: not 1: Confirmed 2: Confirmed (non-))
     */
    private ConfirmItemEnum(int type) {
        this.type = type;
    }

    /**
     * Get ConfirmItemEnum the specified instance.<br/>
     * If the type does not exist, it returns a NULL.<br/>
     * 
     * @param type
     *            Code
     * @return ConfirmItemEnum
     */
    public static ConfirmItemEnum get(int type) {
        for (ConfirmItemEnum temp : ConfirmItemEnum.values()) {
            if (temp.getType() == type) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Get ConfirmItemEnum the specified instance.<br/>
     * If the type does not exist, it returns a NULL.<br/>
     * 
     * @param type
     *            Code
     * @return ConfirmItemEnum
     */
    public static ConfirmItemEnum getForString(String type) {
        if (StringUtil.isEmpty(type)) {
            return null;
        }
        return ConfirmItemEnum.get(Integer.valueOf(type).intValue());
    }

    /**
     * Get the type.
     * 
     * @return Type
     */
    public int getType() {
        return this.type;
    }

    /**
     * Set the type.<br/>
     * 
     * @param type
     *            Type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Get the color of the text that belongs to the type.<br/>
     * 
     * @return Screen display text color
     */
    public Color getColor() {
        if (ConfirmItemEnum.STATUS_OK.equals(this)) {
            return ColorUtil.getYellowGreen();
        }
        if (ConfirmItemEnum.STATUS_NG.equals(this)) {
            return ColorUtil.getYellowGreen();
        }
        return ColorUtil.getWhite();
    }

    /**
     * Get the text belonged to type.<br/>
     * 
     * @return Screen display text
     */
    public String getStatusText() {
        if (ConfirmItemEnum.STATUS_OK.equals(this)) {
            return ResourceUtil.CONFIRM_STATUS_OK;
        }
        if (ConfirmItemEnum.STATUS_NG.equals(this)) {
            return ResourceUtil.CONFIRM_STATUS_NG;
        }
        return ResourceUtil.CONFIRM_STATUS_NON;
    }

    /**
     * The change in the status value of the display for the confirmation
     * hearing status.<br/>
     * 
     * @param confirmItemStatusEnum
     *            Confirmation hearing status
     * @return Display confirmation status
     */
    public static ConfirmItemEnum createHiaringStatus(
            ConfirmItemStatusEnum confirmItemStatusEnum) {
        ConfirmItemEnum result = null;
        switch (confirmItemStatusEnum) {
        case NON_NON:
        case OK_NON:
        case NG_NON:
            result = ConfirmItemEnum.STATUS_NON;
            break;
        case NON_OK:
        case OK_OK:
        case NG_OK:
            result = ConfirmItemEnum.STATUS_OK;
            break;
        case NON_NG:
        case OK_NG:
        case NG_NG:
            result = ConfirmItemEnum.STATUS_NG;
            break;
        }
        return result;
    }

    /**
     * The change in the status value of the display for visual confirmation
     * status.<br/>
     * 
     * @param confirmItemStatusEnum
     *            Visual confirmation status
     * @return Display confirmation status
     */
    public static ConfirmItemEnum createCheckStatus(
            ConfirmItemStatusEnum confirmItemStatusEnum) {
        ConfirmItemEnum result = null;
        switch (confirmItemStatusEnum) {
        case NON_NON:
        case NON_OK:
        case NON_NG:
            result = ConfirmItemEnum.STATUS_NON;
            break;
        case OK_NON:
        case OK_OK:
        case OK_NG:
            result = ConfirmItemEnum.STATUS_OK;
            break;
        case NG_NON:
        case NG_OK:
        case NG_NG:
            result = ConfirmItemEnum.STATUS_NG;
            break;
        }
        return result;
    }
}
