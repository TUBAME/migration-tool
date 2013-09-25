/*
 * ConfirmItemStatusEnum.java
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
package org.tubame.portability.model;

/**
 * Enumeration of visual / hearing confirmation item status.<br/>
 * Manage the following data.<br/>
 * 
 * 0 :Not, not <br/>
 * 1 :visually confirmed, not<br/>
 * 2 :visually confirmed (non-transplant item), not<br/>
 * 3 :Not, hearing confirmed (transplant item)<br/>
 * 4 :Not, hearing confirmed (non-transplant item)<br/>
 * 5 :Visually confirmed (transplant item), hearing confirmed (transplant item)<br/>
 * 6 :Visually confirmed (transplant item), hearing confirmed (non-transplant
 * item)<br/>
 * 7 :Visually confirmed (non-transplant item), hearing confirmed (transplant
 * item)<br/>
 * 8 :Visually confirmed (non-transplant item), hearing confirmed
 * (non-transplant item)<br/>
 * 
 */
public enum ConfirmItemStatusEnum {

    /**
     * 0: Not, not
     */
    NON_NON(0),
    /**
     * 1:Visually confirmed (transplant item), not
     */
    OK_NON(1),
    /**
     * 2:Visually confirmed (non-transplant item), not
     */
    NG_NON(2),
    /**
     * 3:Not, hearing confirmed (transplant item)
     */
    NON_OK(3),
    /**
     * 4:Not, hearing confirmed (non-transplant item)
     */
    NON_NG(4),
    /**
     * 5:Visually confirmed (transplant item), hearing confirmed (transplant
     * item)
     */
    OK_OK(5),
    /**
     * 6:Visually confirmed (transplant item), hearing confirmed (non-transplant
     * item)
     */
    OK_NG(6),
    /**
     * 7:Visually confirmed (non-transplant item), hearing confirmed (transplant
     * item)
     */
    NG_OK(7),
    /**
     * 8:Visually confirmed (non-transplant item), hearing confirmed
     * (non-transplant item)
     */
    NG_NG(8);

    /**
     * Status
     */
    private int status;

    /**
     * Constructor.<br/>
     * 
     * @param status
     *            Status
     */
    private ConfirmItemStatusEnum(int status) {
        this.status = status;
    }

    /**
     * Get status.<br/>
     * 
     * @return Status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get ConfirmItemStatusEnum.<br/>
     * If not present in the enumerated and return NULL.<br/>
     * 
     * @param status
     *            Target status
     * @return ConfirmItemStatusEnum
     */
    public static ConfirmItemStatusEnum get(int status) {
        for (ConfirmItemStatusEnum temp : ConfirmItemStatusEnum.values()) {
            if (temp.status == status) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Get ConfirmItemStatusEnum.<br/>
     * If not present in the enumerated and return NULL.<br/>
     * 
     * @param status
     *            Target status
     * @return ConfirmItemStatusEnum
     */
    public static ConfirmItemStatusEnum getForString(String status) {
        return ConfirmItemStatusEnum.get(Integer.valueOf(status).intValue());
    }

    /**
     * Create a confirmation status from visual / hearing confirmation item.<br/>
     * 
     * @param checkStatus
     *            Visual confirmation status
     * @param hearingStatus
     *            Confirmation hearing status
     * 
     * @return Check status
     */
    public static ConfirmItemStatusEnum getStatus(String checkStatus,
            String hearingStatus) {
        ConfirmItemEnum checkConfirmItemEnum = ConfirmItemEnum
                .getForString(checkStatus);
        ConfirmItemEnum hearingConfirmItemEnum = ConfirmItemEnum
                .getForString(hearingStatus);
        if (isStatusNon(checkConfirmItemEnum, hearingConfirmItemEnum)) {
            return ConfirmItemStatusEnum.NON_NON;
        }
        if (ConfirmItemEnum.STATUS_OK.equals(checkConfirmItemEnum)
                && isNon(hearingConfirmItemEnum)) {
            return ConfirmItemStatusEnum.OK_NON;
        }
        if (ConfirmItemEnum.STATUS_NG.equals(checkConfirmItemEnum)
                && isNon(hearingConfirmItemEnum)) {
            return ConfirmItemStatusEnum.NG_NON;
        }
        if (isNon(checkConfirmItemEnum)
                && ConfirmItemEnum.STATUS_OK.equals(hearingConfirmItemEnum)) {
            return ConfirmItemStatusEnum.NON_OK;
        }
        if (isNon(checkConfirmItemEnum)
                && ConfirmItemEnum.STATUS_NG.equals(hearingConfirmItemEnum)) {
            return ConfirmItemStatusEnum.NON_NG;
        }
        if (ConfirmItemEnum.STATUS_OK.equals(checkConfirmItemEnum)
                && ConfirmItemEnum.STATUS_OK.equals(hearingConfirmItemEnum)) {
            return ConfirmItemStatusEnum.OK_OK;
        }
        if (ConfirmItemEnum.STATUS_OK.equals(checkConfirmItemEnum)
                && ConfirmItemEnum.STATUS_NG.equals(hearingConfirmItemEnum)) {
            return ConfirmItemStatusEnum.OK_NG;
        }
        if (ConfirmItemEnum.STATUS_NG.equals(checkConfirmItemEnum)
                && ConfirmItemEnum.STATUS_OK.equals(hearingConfirmItemEnum)) {
            return ConfirmItemStatusEnum.NG_OK;
        }
        if (ConfirmItemEnum.STATUS_NG.equals(checkConfirmItemEnum)
                && ConfirmItemEnum.STATUS_NG.equals(hearingConfirmItemEnum)) {
            return ConfirmItemStatusEnum.NG_NG;
        }
        return null;
    }

    /**
     * The decision is the status or not.<br/>
     * 
     * @param item
     *            Check status
     * @return true:NON status false:Excepting
     */
    private static boolean isNon(ConfirmItemEnum item) {
        if (item == null || ConfirmItemEnum.STATUS_NON.equals(item)) {
            return true;
        }
        return false;
    }

    /**
     * The decision is the status or not.<br/>
     * 
     * @param checkConfirmItemEnum
     *            Visual confirmation status
     * @param hearingConfirmItemEnum
     *            Confirmation hearing status
     * @return true:NON status false:Excepting
     */
    private static boolean isStatusNon(ConfirmItemEnum checkConfirmItemEnum,
            ConfirmItemEnum hearingConfirmItemEnum) {
        if (checkConfirmItemEnum == null && hearingConfirmItemEnum == null) {
            return true;
        }
        if (ConfirmItemEnum.STATUS_NON.equals(checkConfirmItemEnum)
                && ConfirmItemEnum.STATUS_NON.equals(hearingConfirmItemEnum)) {
            return true;
        }
        return false;
    }
}
