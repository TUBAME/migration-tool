/*
 * JbmEditorMigrationRow.java
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.tubame.portability.logic.CheckListInformationFactory;
import org.tubame.portability.plugin.action.ConfirmItemChangeAction;
import org.tubame.portability.util.ColorUtil;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.StringUtil;
import org.tubame.portability.util.resource.ResourceUtil;

/**
 * Row information to be displayed in the Editor.<br/>
 * Row data to be displayed in the search results editing screen.<br/>
 */
public class JbmEditorMigrationRow implements MigrationEditorRow {
    /**
     * Font name of the first layer
     */
    private static final Font CHARACTOR = PluginUtil.getJbmEditorFont();
    /**
     * Hierarchical level parent
     */
    public static final int LEVEL_FIRST = 0;

    /**
     * Hierarchical level child
     */
    public static final int LEVEL_SECOND = 1;

    /**
     * Hierarchical level grandson
     */
    public static final int LEVEL_THIRD = 2;

    /**
     * No
     */
    private String no;

    /**
     * Count the number of file name or line number held by the hierarchy one
     * eye
     */
    private String countNo;

    /**
     * File name
     */
    private String fileName;

    /**
     * Line number
     */
    private String rowNo;

    /**
     * Degree of difficulty
     */
    private String difficulty;

    /**
     * Guide chapter number
     */
    private String chapterNo;

    /**
     * Visual confirmation item
     */
    private String visualConfirmationItem;

    /**
     * Visual confirmation status
     */
    private String checkEyeStatus;

    /**
     * Hearing status
     */
    private String hearingStatus;

    /**
     * Level (represents the hierarchy)
     */
    private int level;

    /**
     * Large Item Description
     */
    private String bigMessage;

    /**
     * Middle Item Description
     */
    private String middleMessage;

    /**
     * Visual confirmation description
     */
    private String checkEyeMessage;

    /**
     * Confirmation hearing descriptionS
     */
    private String hearingMessage;

    /**
     * It determines whether the hierarchy that holds the line number (true:
     * false third layer is line number retention: Display up to the second
     * level (file name search))
     */
    private boolean hasLine;

    /**
     * Conversion flag
     */
    private String convert;

    /**
     * Number of lines
     */
    private String lineNumber;

    /**
     * Line number basis
     */
    private String lineNumberContents;

    /**
     * Porting factor
     */
    private String lineFactor;

    /**
     * Difficulty details
     */
    private String degreeDetail;

    /**
     * Parent element
     */
    private JbmEditorMigrationRow parent;
    /**
     * Child JbmEditorMigrationRow
     */
    private List<JbmEditorMigrationRow> childList = new ArrayList<JbmEditorMigrationRow>();

    /**
     * One row of data in CSV format (hold the value only the first level)
     */
    private String writeData;

    /**
     * Get No.<br/>
     * 
     * @return No
     */
    public String getNo() {
        return no;
    }

    /**
     * Set No.<br/>
     * 
     * @param no
     *            No
     */
    public void setNo(String no) {
        this.no = no;
    }

    /**
     * Get the count of the file name or line number held by the hierarchy one
     * eye.<br/>
     * 
     * @return Count the number of file name or line number held by the
     *         hierarchy one eye
     */
    public String getCountNo() {
        return countNo;
    }

    /**
     * Set the number of counts in the file name or line number held by the
     * hierarchy one eye.<br/>
     * 
     * @param countNo
     *            Count the number of file name or line number held by the
     *            hierarchy one eye
     */
    public void setCountNo(String countNo) {
        this.countNo = countNo;
    }

    /**
     * Get a file name.<br/>
     * 
     * @return File name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the file name.<br/>
     * 
     * @param fileName
     *            File name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the line number.<br/>
     * 
     * @return Line number
     */
    public String getRowNo() {
        return rowNo;
    }

    /**
     * Set the line number.<br/>
     * 
     * @param rowNo
     *            Line number
     */
    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
    }

    /**
     * Get the difficulty.<br/>
     * 
     * @return Degree of difficulty
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Set the difficulty.<br/>
     * 
     * @param difficulty
     *            Degree of difficulty
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Get the guide chapter number.<br/>
     * 
     * @return Guide chapter number
     */
    public String getChapterNo() {
        return chapterNo;
    }

    /**
     * Set the guide chapter number.<br/>
     * 
     * @param chapterNo
     *            Guide chapter number
     */
    public void setChapterNo(String chapterNo) {
        this.chapterNo = chapterNo;
    }

    /**
     * Get a visual confirmation item.<br/>
     * 
     * @return Visual confirmation item
     */
    public String getVisualConfirmationItem() {
        return visualConfirmationItem;
    }

    /**
     * Set visual confirmation item.<br/>
     * 
     * @param visualConfirmationItem
     *            Visual confirmation item
     */
    public void setVisualConfirmationItem(String visualConfirmationItem) {
        this.visualConfirmationItem = visualConfirmationItem;
    }

    /**
     * Get a visual confirmation status.<br/>
     * 
     * @return Visual confirmation status
     */
    public String getCheckEyeStatus() {
        return checkEyeStatus;
    }

    /**
     * Set the visual confirmation status.<br/>
     * 
     * @param checkEyeStatus
     *            Visual confirmation status
     */
    public void setCheckEyeStatus(String checkEyeStatus) {
        this.checkEyeStatus = checkEyeStatus;
    }

    /**
     * Get hearing status.<br/>
     * 
     * @return Hearing status
     */
    public String getHearingStatus() {
        return hearingStatus;
    }

    /**
     * Set the status hearing.<br/>
     * 
     * @param hearingStatus
     *            Hearing status
     */
    public void setHearingStatus(String hearingStatus) {
        this.hearingStatus = hearingStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        return level;
    }

    /**
     * Set level.<br/>
     * 
     * @param level
     *            Level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Get the Child JbmEditorMigrationRow list.<br/>
     * 
     * @return Child JbmEditorMigrationRow list
     */
    public List<JbmEditorMigrationRow> getChildList() {
        return childList;
    }

    /**
     * Set the Child JbmEditorMigrationRow list.<br/>
     * 
     * @param childList
     *            Child JbmEditorMigrationRow list
     */
    public void setChildList(List<JbmEditorMigrationRow> childList) {
        this.childList = childList;
    }

    /**
     * Add register the Child JbmEditorMigrationRow.<br/>
     * 
     * @param child
     *            Child JbmEditorMigrationRow
     */
    public void addChild(JbmEditorMigrationRow child) {
        childList.add(child);
    }

    /**
     * Determine whether visual confirmation item target row.<br/>
     * If there are one or more characters visually check Description, visual
     * check.<br/>
     * 
     * @return true:There is visual confirmation false:Without visual
     *         confirmation
     */
    public boolean isCheckEye() {
        return !StringUtil.isEmpty(CheckListInformationFactory
                .getCheckListInformationFacade()
                .getCheckEyeDescription(getNo()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JbmEditorMigrationRow getParent() {
        return parent;
    }

    /**
     * Set the parent element. <br/>
     * 
     * @param parent
     *            Parent element
     */
    public void setParent(JbmEditorMigrationRow parent) {
        this.parent = parent;
    }

    /**
     * Get the conversion flag.<br/>
     * 
     * @return convert Conversion flag
     */
    public String getConvert() {
        return convert;
    }

    /**
     * Set the conversion flag.<br/>
     * 
     * @param convert
     *            Conversion flag
     */
    public void setConvert(String convert) {
        this.convert = convert;
    }

    /**
     * Determine whether the hearing item subject line.<br/>
     * If there are one or more characters hearing Item Description, and hearing
     * check.<br/>
     * 
     * @return true:Hearing item false:Hearing fee
     */
    public boolean isHearing() {
        return !StringUtil
                .isEmpty(CheckListInformationFactory
                        .getCheckListInformationFacade().getHearingDescription(
                                getNo()));
    }

    /**
     * Number of lines is determined whether the unknown target row.<br/>
     * If "unknown", line number unknown.<br/>
     * 
     * @return true:Unknown false:Unknown except
     */
    public boolean isLineUnKnown() {
        return CheckListInformationFactory.getCheckListInformationFacade()
                .getLineNumberDescription(getNo())
                .equals(ResourceUtil.WORKVIEW_MESSAGE_UNKNOWN);
    }

    /**
     * Number of lines TODO: determine whether the subject line of the SE manual
     * calculation item.<br/>
     * Case of the "SE manual calculation item", SE and manual calculation item.<br/>
     * 
     * @return true:SE manual calculation item false:SE manual calculation fee
     */
    public boolean isLineToDoSe() {
        return CheckListInformationFactory.getCheckListInformationFacade()
                .getLineNumberDescription(getNo())
                .equals(ResourceUtil.WORKVIEW_MESSAGE_TODO_SE);
    }

    /**
     * The return of the major items.<br/>
     * If have obtained information from the check list file,<br/>
     * and return the value that is already held.<br/>
     * 
     * @return Large category
     */
    public String getBigMessage() {
        if (bigMessage == null) {
            bigMessage = CheckListInformationFactory
                    .getCheckListInformationFacade().getBigDescription(getNo());
        }
        return bigMessage;
    }

    /**
     * If you have obtained information from the check list file,<br/>
     * return a value that already holds return the items.<br/>
     * 
     * @return middle item
     */
    public String getMiddleMessage() {
        if (middleMessage == null) {
            middleMessage = CheckListInformationFactory
                    .getCheckListInformationFacade().getMiddleDescription(
                            getNo());
        }
        return middleMessage;
    }

    /**
     * The return of the visual content confirmation.<br/>
     * If have obtained information from the check list file,<br/>
     * and return the value that is already held.<br/>
     * 
     * @return Visual Confirmation
     */
    public String getCheckEyeMessage() {
        if (checkEyeMessage == null) {
            checkEyeMessage = CheckListInformationFactory
                    .getCheckListInformationFacade().getCheckEyeDescription(
                            getNo());
        }
        return checkEyeMessage;
    }

    /**
     * The return of the hearing Confirmation.<br/>
     * If you have obtained information from the check list file, <br/>
     * return the value that is already held.<br/>
     * 
     * @return Visual Confirmation
     */
    public String getHiaringMessage() {
        if (hearingMessage == null) {
            hearingMessage = CheckListInformationFactory
                    .getCheckListInformationFacade().getHearingDescription(
                            getNo());
        }
        return hearingMessage;
    }

    /**
     * Return the number of lines.<br/>
     * If you have obtained information from the check list file,<br/>
     * I will return the value that is already held.<br/>
     * 
     * @return Number of lines
     */
    public String getLineNumber() {
        if (lineNumber == null) {
            lineNumber = CheckListInformationFactory
                    .getCheckListInformationFacade().getLineNumberDescription(
                            getNo());
        }
        return lineNumber;
    }

    /**
     * 
     * Set the number of lines<br/>
     * 
     * @param lineNumber
     *            Number of lines
     */
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * It returns the number of lines basis.<br/>
     * If you have obtained information from the check list file, <br/>
     * I will return the value that is already held.<br/>
     * 
     * @return Line number basis
     */
    public String getLineNumberContents() {
        if (lineNumberContents == null) {
            lineNumberContents = CheckListInformationFactory
                    .getCheckListInformationFacade()
                    .getLineNumberContentsDescription(getNo());
        }
        return lineNumberContents;
    }

    /**
     * 
     * Set the number of lines basis.<br/>
     * 
     * @param lineNumberContents
     *            Line number basis
     */
    public void setLineNumberContents(String lineNumberContents) {
        this.lineNumberContents = lineNumberContents;
    }

    /**
     * To return the total number of lines.<br/>
     * 
     * @return Total number of lines
     */
    public String getTotalLine() {
        String totalLine;
        long sum;
        try {
            Integer lineNum = Integer.valueOf(getLineNumber());
            Integer hitNum = Integer.valueOf(getHitNum());
            if (getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
                sum = lineNum.longValue() * hitNum.longValue();
            } else {
                sum = lineNum.longValue();
            }
            totalLine = String.valueOf(sum);
        } catch (NumberFormatException e) {
            totalLine = "0";
        }
        return totalLine;
    }

    /**
     * The return of the transplant factor.<br/>
     * If you have obtained information from the check list file, <br/>
     * and return the value that is already held.<br/>
     * 
     * @return Porting factor
     */
    public String getLineFactor() {
        if (lineFactor == null) {
            lineFactor = CheckListInformationFactory
                    .getCheckListInformationFacade().getFactorDescription(
                            getNo());
        }
        return lineFactor;
    }

    /**
     * 
     * Set the transplant factor.<br/>
     * 
     * @param factor
     *            Porting factor
     */
    public void setLineFactor(String factor) {
        lineFactor = factor;
    }

    /**
     * The return more difficulty.<br/>
     * If obtained information from the check list file, <br/>
     * return the value that is already held.<br/>
     * 
     * @return Difficulty details
     */
    public String getDegreeDetail() {
        if (degreeDetail == null) {
            degreeDetail = CheckListInformationFactory
                    .getCheckListInformationFacade().getDegreeDescription(
                            getNo());
        }
        return degreeDetail;
    }

    /**
     * 
     * Set more difficulty.<br/>
     * 
     * @param detail
     *            Difficulty details
     */
    public void setDegreeDetail(String detail) {
        degreeDetail = detail;
    }

    /**
     * Get whether to hold the results of the line number.<br/>
     * Only the first hierarchy.
     * 
     * @return true:Third layer is line number holding false:Display up to the
     *         second level (file name search)
     */
    public boolean isHasLine() {
        return hasLine;
    }

    /**
     * Set whether to hold the results of the line number.<br/>
     * 
     * @param isHasLine
     *            Line number holding existence (true: false third layer is line
     *            number retention: Display up to the second level (file name
     *            search))
     */
    public void setHasLine(boolean isHasLine) {
        hasLine = isHasLine;
    }

    /**
     * To return the number of hits.<br/>
     * 
     * @return Hits
     */
    public int getHitNum() {
        int hitNum = 0;
        hitNum = 0;

        if (getRowNo().equals("0")) {
            // Case of 0 1 search hits, hits returns 0.
            return hitNum;
        }

        if (isHasLine()) {
            for (JbmEditorMigrationRow child : getChildList()) {
                hitNum += child.getChildList().size();
            }
        } else {
            hitNum += getChildList().size();
        }
        return hitNum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnText(int columnIndex) {
        String text = StringUtil.EMPTY;
        switch (JbmEditorEnum.get(columnIndex)) {
        case INDEX_NO:
            // No
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getNo();
            }
            break;
        case HIT_NUM:
            // Hits
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = String.valueOf(getHitNum());
            }
            break;
        case BIG_ITEM:
            // Large category
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getBigMessage();
            }
            break;
        case MIDDLE_ITEM:
            // Middle item
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getMiddleMessage();
            }
            break;
        case TARGET_FILE_PATH:
            // File name
            if (JbmEditorMigrationRow.LEVEL_SECOND == getLevel()) {
                text = getFileName();
            }
            break;
        case ROW_NO:
            // Line number
            if (JbmEditorMigrationRow.LEVEL_THIRD == getLevel()) {
                text = getRowNo();
            }
            break;
        case DIFFICULTY:
            // Degree of difficulty
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getDifficulty();
            }
            break;
        case CHAPTER_NO:
            // Guide chapter number
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getChapterNo();
            }
            break;
        case VISUAL_CONFIRM_ITEM:
            // Visual confirmation item
            text = getCheckEyeMessage();
            break;
        case HIARING_ITEM:
            // Confirmation hearing content
            text = getHiaringMessage();
            break;
        case VISUAL_CONFIRM_STATSU_ITEM:
            // Visual confirmation status items
            text = getHiaringStatusItem(getLevel(), false);
            break;
        case HIARING_STATUS:
            // Hearing confirmation item
            text = getHiaringStatusItem(getLevel(), true);
            break;
        case LINE_NUM:
            // Number of lines
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getLineNumber();
            }
            break;
        case LINE_NUM_BASIS:
            // Line number basis
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getLineNumberContents();
            }
            break;
        case TOTAL_LINE:
            // Total line
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getTotalLine();
            }
            break;
        case LINE_FACTOR:
            // Porting factor
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getLineFactor();
            }
            break;
        case DEGREE_DETAIL:
            // Difficulty details
            if (JbmEditorMigrationRow.LEVEL_FIRST == getLevel()) {
                text = getDegreeDetail();
            }
            break;
        }
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getForeground(int index) {
        // Third level only get the color
        String temp = getColumnText(index);
        if (ConfirmItemEnum.STATUS_NON.getStatusText().equals(temp)
                || ConfirmItemChangeAction.NG.equals(temp)) {
            return ColorUtil.getRed();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getBackground(int index) {
        // Third level only get the color
        if (JbmEditorMigrationRow.LEVEL_THIRD == getLevel()) {
            // Visual confirmation
            String temp = getColumnText(index);
            if (ConfirmItemEnum.STATUS_NON.getStatusText().equals(temp)) {
                return ColorUtil.getConfirmItemStatusNonColor();
            }
            if (ConfirmItemEnum.STATUS_OK.getStatusText().equals(temp)) {
                return ColorUtil.getConfirmItemStatusOkColor();
            }
            if (ConfirmItemEnum.STATUS_NG.getStatusText().equals(temp)) {
                return ColorUtil.getConfirmItemStatusNgColor();
            }
        }
        return null;
    }

    /**
     * Get a string of check items to display on the screen.<br/>
     * 
     * @param level
     *            Hierarchical level
     * @param hiaring
     *            Whether hearing item
     * @return Display character First level: O or X Second level: O or X Third
     *         level: Confirmed (transplant unnecessary) or unconfirmed or
     *         confirmed (on transplantation)
     */
    private String getHiaringStatusItem(int level, boolean hiaring) {
        boolean type;
        if (hiaring) {
            type = isHearing();
        } else {
            type = isCheckEye();
        }
        if (type) {
            if (JbmEditorMigrationRow.LEVEL_FIRST == level) {
                // (Show O or X) first level
                for (JbmEditorMigrationRow secount : getChildList()) {
                    if (ConfirmItemChangeAction.NG.equals(getTopStatus(
                            secount.getChildList(), hiaring))) {
                        return ConfirmItemChangeAction.NG;
                    }
                }
                return ConfirmItemChangeAction.OK;
            }
            if (JbmEditorMigrationRow.LEVEL_SECOND == level) {
                // second tier (Show O or X)
                return getTopStatus(getChildList(), hiaring);
            }
            // Third layer
            ConfirmItemEnum status;
            if (hiaring) {
                status = ConfirmItemEnum.getForString(getHearingStatus());
            } else {
                status = ConfirmItemEnum.getForString(getCheckEyeStatus());
            }
            return status.getStatusText();
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get the display string in the first layer / second layer of visual
     * confirmation item.<br/>
     * If the unidentified item exists at least one of the status of the Child
     * hierarchy than themselves,<br/>
     * The return of the ConfirmItemChangeAction.NG.<br/>
     * 
     * 
     * @param rowList
     *            Inspection list
     * @param hiaring
     *            Whether hearing item
     * @return Display character
     */
    private String getTopStatus(List<JbmEditorMigrationRow> rowList,
            boolean hiaring) {
        ConfirmItemEnum confirmItemEnum;
        for (JbmEditorMigrationRow row : rowList) {
            if (hiaring) {
                confirmItemEnum = ConfirmItemEnum.getForString(row
                        .getHearingStatus());
            } else {
                confirmItemEnum = ConfirmItemEnum.getForString(row
                        .getCheckEyeStatus());
            }
            if (ConfirmItemEnum.STATUS_NON.equals(confirmItemEnum)) {
                return ConfirmItemChangeAction.NG;
            }
        }
        return ConfirmItemChangeAction.OK;
    }

    /**
     * Create a confirmation status from visual / hearing confirmation item.<br/>
     * 
     * @return Check status
     */
    public String getConfirmStatus() {
        String result = String.valueOf(ConfirmItemStatusEnum.NON_NON
                .getStatus());
        if (isCheckEye() || isHearing()) {
            ConfirmItemStatusEnum confirmItemStatusEnum = ConfirmItemStatusEnum
                    .getStatus(getCheckEyeStatus(), getHearingStatus());
            if (confirmItemStatusEnum != null) {
                result = String.valueOf(confirmItemStatusEnum.getStatus());
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getNo());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getFileName());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getMoreParent().getCountNo());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getRowNo());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getDifficulty());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getConvert());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getChapterNo());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getConfirmStatus());
        return sb.toString();
    }

    /**
     * ToString to be specified line number, the confirmation status.<br/>
     * 
     * @param lineNumberList
     *            Line number list
     * @param confirmList
     *            Checklist
     * @param count
     *            Number of third level
     * @return String
     */
    public String toStringParamRowAndStatus(String lineNumberList,
            String confirmList, int count) {
        StringBuilder sb = new StringBuilder();
        sb.append(getNo());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getFileName());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(count);
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(lineNumberList.trim());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getDifficulty());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getConvert());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(getChapterNo());
        sb.append(StringUtil.CSV_DELIMITER);
        sb.append(confirmList.trim());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getColumnImage(int columnIndex) {
        if (columnIndex == JbmEditorEnum.DIFFICULTY.getCode()
                && getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
            DifficultyEnum difficultyEnum = DifficultyEnum.get(getDifficulty());
            if (difficultyEnum != null) {
                return difficultyEnum.getImage();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Font getFont() {
        if (getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
            return JbmEditorMigrationRow.CHARACTOR;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren() {
        if (getChildList() != null && getChildList().size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getChildren() {
        return getChildList().toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JbmEditorMigrationRow getMoreParent() {
        if (getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
            return this;
        }
        if (getLevel() == JbmEditorMigrationRow.LEVEL_SECOND) {
            return getParent();
        }
        return getParent().getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWriteData() {
        JbmEditorMigrationRow top = getMoreParent();
        StringBuilder sb = new StringBuilder();
        for (JbmEditorMigrationRow second : top.getChildList()) {
            String totalLine = "";
            if (second.getChildList().size() == 0) {
                sb.append(second.toString());
            }
            if (second.getChildList().size() > 0) {
                StringBuilder comfirmStatus = new StringBuilder();
                StringBuilder lineNumber = new StringBuilder();
                int countNo = 0;
                for (JbmEditorMigrationRow third : second.getChildList()) {
                    comfirmStatus.append(third.getConfirmStatus());
                    comfirmStatus.append(StringUtil.BLANK);
                    lineNumber.append(third.getRowNo());
                    lineNumber.append(StringUtil.BLANK);
                    if (Integer.parseInt(top.getCountNo()) != 0) {
                        countNo++;
                    }
                }
                sb.append(second
                        .getChildList()
                        .get(0)
                        .toStringParamRowAndStatus(lineNumber.toString(),
                                comfirmStatus.toString(), countNo));
                totalLine = getCsvTotalLine(top, countNo);
            }

            // Set the contents of the checklist
            sb.append(StringUtil.CSV_DELIMITER);
            sb.append(top.getLineFactor());
            sb.append(StringUtil.CSV_DELIMITER);
            sb.append(top.getDegreeDetail());
            sb.append(StringUtil.CSV_DELIMITER);
            sb.append(top.getLineNumber());
            sb.append(StringUtil.CSV_DELIMITER);

            // Edit line number basis in the CSV output
            String lineNumContents = top.getLineNumberContents();
            // Escaped if it contains a double quote
            lineNumContents = lineNumContents.replaceAll("\"", "\"\"");
            // Enclosed in double quotes if it contains a comma, newline, double
            // Fort
            if (lineNumContents.contains(StringUtil.CSV_DELIMITER)
                    || lineNumContents.contains(StringUtil.LINE_SEPARATOR)
                    || lineNumContents.contains("\"")) {
                lineNumContents = "\"" + lineNumContents + "\"";
            }
            sb.append(lineNumContents);

            sb.append(StringUtil.CSV_DELIMITER);
            sb.append(totalLine);
            sb.append(StringUtil.LINE_SEPARATOR);
        }
        top.setWriteData(sb.toString());
    }

    /**
     * Get the CSV output for the total number of lines.<br/>
     * 
     * @param top
     *            Top hierarchy
     * @param count
     *            Number of counts
     * @return CSV output for the total number of lines
     */
    private String getCsvTotalLine(JbmEditorMigrationRow top, int count) {
        String totalLine;
        long sum;
        try {
            Integer lineNum = Integer.valueOf(top.getLineNumber());
            Integer hitNum = Integer.valueOf(count);
            if (getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
                sum = lineNum.longValue() * hitNum.longValue();
            } else {
                sum = lineNum.longValue();
            }
            totalLine = String.valueOf(sum);
        } catch (NumberFormatException e) {
            totalLine = "0";
        }
        return totalLine;
    }

    /**
     * Get (to hold the value only the first level) one row of data in CSV
     * format.<br/>
     * 
     * @return One row of data in CSV format (hold the value only the first
     *         level)
     */
    public String getWriteData() {
        return writeData;
    }

    /**
     * Set (to hold the value only the first level) one row of data in CSV
     * format.<br/>
     * 
     * @param writeData
     *            one row of data in CSV format (To hold the value only the
     *            first level)
     */
    public void setWriteData(String writeData) {
        this.writeData = writeData;
    }
}
