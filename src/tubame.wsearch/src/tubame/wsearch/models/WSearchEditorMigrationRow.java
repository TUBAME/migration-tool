/*
 * WSearchEditorMigrationRow.java
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
package tubame.wsearch.models;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.WorkbenchException;
import tubame.wsearch.Activator;
import tubame.wsearch.biz.comparer.AbstractComparer;
import tubame.wsearch.util.resource.ResourceUtil;

/**
 * It is the Row information to be displayed in the search results view.<br/>
 */
public class WSearchEditorMigrationRow implements MigrationEditorRow {

    /**
     * The type that defines the hierarchy level.<br/>
     */
    public enum LEVEL {
        FIRST(0), SECOND(1), THIRD(2), FOURTH(3), FIFTH(4), SIXTH(5);

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         * 
         * @param level
         *            Hierarchical level
         */
        LEVEL(int level) {
            this.level = level;
        }

        /**
         * Hierarchical level
         */
        private int level;

        /**
         * Get the hierarchy level.<br/>
         * 
         * @return Hierarchical level
         */
        public int getLevel() {
            return level;
        }
    }

    /**
     * The lowest level
     */
    public final static LEVEL LOWEST_LEVEL = LEVEL.FIFTH;

    /**
     * (Represents the hierarchy) level
     */
    private int level;

    /**
     * Result
     */
    private String status;

    /**
     * Category
     */
    private String category;

    /**
     * Package
     */
    private String packageName;

    /**
     * Porting the original library
     */
    private String srcLibrary;

    /**
     * Porting library
     */
    private String destLibrary;

    /**
     * Class
     */
    private String clazzName;

    /**
     * Hits
     */
    private String hitNum;

    /**
     * Line number
     */
    private String rowNumber;

    /**
     * Migrated files
     */
    private String files;

    /**
     * Result detail
     */
    private String detail;

    /**
     * Remarks
     */
    private String note;

    /**
     * Subelement
     */
    private List<WSearchEditorMigrationRow> childList = new ArrayList<WSearchEditorMigrationRow>();

    /**
     * Parent hierarchy
     */
    private WSearchEditorMigrationRow parent;

    /**
     * Error icon
     */
    private static Image errorIcon;

    /**
     * Warning icon
     */
    private static Image warnIcon;

    static {
        try {
            URL pluginURL = Activator.getDefault().getBundle()
                    .getEntry(CmnStringUtil.SLASH);
            errorIcon = ImageDescriptor.createFromURL(
                    new URL(pluginURL, ResourceUtil.ERROR_ICON)).createImage();
            warnIcon = ImageDescriptor.createFromURL(
                    new URL(pluginURL, ResourceUtil.WARN_ICON)).createImage();
        } catch (MalformedURLException e) {
            Activator.log(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        return level;
    }

    /**
     * Set up a hierarchy level.<br/>
     * 
     * @param level
     *            Hierarchical level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Get child hierarchy level.<br/>
     * 
     * @return Child hierarchy level
     */
    public List<WSearchEditorMigrationRow> getChildList() {
        return childList;
    }

    /**
     * Set the child hierarchy level.<br/>
     * 
     * @param childList
     *            Child hierarchy level
     */
    public void setChildList(List<WSearchEditorMigrationRow> childList) {
        this.childList = childList;
    }

    /**
     * Add it to the child hierarchy level.<br/>
     * 
     * @param item
     *            Additional data
     */
    public void addChild(WSearchEditorMigrationRow item) {
        childList.add(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSearchEditorMigrationRow getParent() {
        return parent;
    }

    /**
     * Set the parent hierarchy.<br/>
     * 
     * @param parent
     *            Parent hierarchy
     */
    public void setParent(WSearchEditorMigrationRow parent) {
        this.parent = parent;
    }

    /**
     * Get the status.<br/>
     * 
     * @return Status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status.<br/>
     * 
     * @param status
     *            Status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the category.<br/>
     * 
     * @return Category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Get the details.<br/>
     * 
     * @return Detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Set the details.<br/>
     * 
     * @param detail
     *            Detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * Get remarks.<br/>
     * 
     * @return Remarks
     */
    public String getNote() {
        return note;
    }

    /**
     * Set the remarks.<br/>
     * 
     * @param note
     *            Remarks
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Set the category.<br/>
     * 
     * @param category
     *            Category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Get the package.<br/>
     * 
     * @return Package name
     */
    public String getPackage() {
        return packageName;
    }

    /**
     * Set the package.<br/>
     * 
     * @param packageName
     *            Package name
     */
    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Get the transplant original library.<br/>
     * 
     * @return Porting the original library name
     */
    public String getSrcLibrary() {
        return srcLibrary;
    }

    /**
     * Set the transplant original library.<br/>
     * 
     * @param library
     *            Porting the original library name
     */
    public void setSrcLibrary(String library) {
        this.srcLibrary = library;
    }

    /**
     * Get the porting library.<br/>
     * 
     * @return Porting library name
     */
    public String getDestLibrary() {
        return destLibrary;
    }

    /**
     * Set the porting library.<br/>
     * 
     * @param library
     *            Porting library name
     */
    public void setDestLibrary(String library) {
        this.destLibrary = library;
    }

    /**
     * Get clazzName.<br/>
     * 
     * @return clazzName
     */
    public String getClazz() {
        return clazzName;
    }

    /**
     * Set the clazzName.<br/>
     * 
     * @param clazzName
     *            clazzName
     */
    public void setClazz(String clazzName) {
        this.clazzName = clazzName;
    }

    /**
     * Get hitNum.<br/>
     * 
     * @return hitNum
     */
    public String getHitNum() {
        return hitNum;
    }

    /**
     * Set the hitNum.<br/>
     * 
     * @param hitNum
     *            hitNum
     */
    public void setHitNum(String hitNum) {
        this.hitNum = hitNum;
    }

    /**
     * Get rowNumber.<br/>
     * 
     * @return rowNumber
     */
    public String getRowNumber() {
        return rowNumber;
    }

    /**
     * Set the rowNumber.<br/>
     * 
     * @param rowNumber
     *            rowNumber
     */
    public void setRowNumber(String rowNumber) {
        this.rowNumber = rowNumber;
    }

    /**
     * Get the files.<br/>
     * 
     * @return files
     */
    public String getFiles() {
        return files;
    }

    /**
     * Set the files.<br/>
     * 
     * @param files
     *            files
     */
    public void setFiles(String files) {
        this.files = files;
    }

    /**
     * Get the text to be displayed by specifying the section number of the
     * column.<br/>
     * 
     * @param columnIndex
     *            No. of column
     * @return Text to be displayed
     */
    public String getColumnText(int columnIndex) {
        return getColumnText(columnIndex, true);
    }

    /**
     * Get the text to be displayed by specifying the element level and the
     * numbers in column.<br/>
     * 
     * @param columnIndex
     *            No. of column
     * @param affectedByLevel
     *            Element level
     * @return Text to be displayed
     */
    public String getColumnText(int columnIndex, boolean affectedByLevel) {
        String text = CmnStringUtil.EMPTY;
        switch (WSearchEditorEnum.get(columnIndex)) {
        case INDEX_STATUS:
            if (!affectedByLevel || (LEVEL.FIRST.getLevel() == getLevel())) {
                text = getStatus();
            }
            break;
        case INDEX_CATEGORY:
            if (!affectedByLevel || (LEVEL.SECOND.getLevel() == getLevel())) {
                text = getCategory();
            }
            break;
        case INDEX_PACKAGE:
            if (!affectedByLevel || (LEVEL.THIRD.getLevel() == getLevel())) {
                text = getPackage();
                if (CmnStringUtil.isEmpty(text)) {
                    text = CmnStringUtil.HYPHEN;
                }
            }
            break;
        case INDEX_SRC_LIBRARY:
            if (!affectedByLevel || (LEVEL.FOURTH.getLevel() == getLevel())) {
                text = getSrcLibrary();
                if (text.contains(",")) {
                    List<String> libraries = Arrays.asList(text.split(","));
                    Collections.sort(libraries, new LibraryComparetor());
                    StringBuilder string = new StringBuilder();
                    for (String value : libraries) {
                        string.append(value + ",");
                    }
                    text = string.toString();
                    text = text.substring(0, (text.length() - 1));
                }
            }
            break;
        case INDEX_DEST_LIBRARY:
            if (!affectedByLevel || (LEVEL.FOURTH.getLevel() == getLevel())) {
                text = getDestLibrary();
                if (text.contains(",")) {
                    List<String> libraries = Arrays.asList(text.split(","));
                    Collections.sort(libraries, new LibraryComparetor());
                    StringBuilder string = new StringBuilder();
                    for (String value : libraries) {
                        string.append(value + ",");
                    }
                    text = string.toString();
                    text = text.substring(0, (text.length() - 1));
                }
            }
            break;
        case INDEX_CLAZZ:
            if (!affectedByLevel || (LEVEL.FOURTH.getLevel() == getLevel())) {
                text = getClazz();
            }
            break;
        case INDEX_HIT_NUM:
            if (LEVEL.FIRST.getLevel() == getLevel()) {
                int count = 0;
                for (WSearchEditorMigrationRow row : getChildList()) {
                    count += Integer.valueOf(row
                            .getColumnText(WSearchEditorEnum.INDEX_HIT_NUM
                                    .getIndex()));
                }
                text = String.valueOf(count);
            }
            if (LEVEL.SECOND.getLevel() == getLevel()) {
                int count = 0;
                for (WSearchEditorMigrationRow row : getChildList()) {
                    String temp = row
                            .getColumnText(WSearchEditorEnum.INDEX_HIT_NUM
                                    .getIndex());
                    if (!CmnStringUtil.isEmpty(temp)) {
                        count += Integer.valueOf(temp);
                    }
                }
                text = String.valueOf(count);
            }
            if (LEVEL.THIRD.getLevel() == getLevel()) {
                int count = 0;
                for (WSearchEditorMigrationRow row : getChildList()) {
                    String temp = row
                            .getColumnText(WSearchEditorEnum.INDEX_HIT_NUM
                                    .getIndex());
                    if (!CmnStringUtil.isEmpty(temp)) {
                        count += Integer.valueOf(temp);
                    }
                }
                text = String.valueOf(count);
            }
            if (LEVEL.FOURTH.getLevel() == getLevel()) {
                int count = getChildList().size();
                text = String.valueOf(count);
            }
            break;
        case INDEX_FILES:
            if (!affectedByLevel || (LEVEL.FIFTH.getLevel() == getLevel())) {
                text = getFiles();
            }
            break;
        case INDEX_LINE:
            if (!affectedByLevel || (LEVEL.FIFTH.getLevel() == getLevel())) {
                text = getRowNumber();
                if (CmnStringUtil.isEmpty(text)) {
                    text = CmnStringUtil.HYPHEN;
                }
            }
            break;
        case INDEX_DETAIL:
            if (!affectedByLevel || (LEVEL.FIFTH.getLevel() == getLevel())) {
                text = getDetail();
            }
            break;
        case INDEX_NOTE:
            if (!affectedByLevel || (LEVEL.FIFTH.getLevel() == getLevel())) {
                text = getNote();
                if (text.contains(",")) {
                    List<String> libraries = Arrays.asList(text.split(","));
                    Collections.sort(libraries, new LibraryComparetor());
                    StringBuilder string = new StringBuilder();
                    for (String value : libraries) {
                        string.append(value + ",");
                    }
                    text = string.toString();
                    text = text.substring(0, (text.length() - 1));
                }
            }
            break;
        }
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getColumnImage(int columnIndex) {
        switch (WSearchEditorEnum.get(columnIndex)) {
        case INDEX_STATUS:
            if (LEVEL.FIRST.getLevel() == getLevel()) {
                if (AbstractComparer.STATUS.ERROR.getValue()
                        .equals(getStatus())) {
                    return errorIcon;
                } else if (AbstractComparer.STATUS.WARN.getValue().equals(
                        getStatus())) {
                    return warnIcon;
                }
            }
            break;
        case INDEX_DETAIL:
            if (LEVEL.FIFTH.getLevel() == getLevel()) {
                if (AbstractComparer.STATUS.ERROR.getValue()
                        .equals(getStatus())) {
                    return errorIcon;
                } else if (AbstractComparer.STATUS.WARN.getValue().equals(
                        getStatus())) {
                    return warnIcon;
                }
            }
            break;
        default:
            break;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Font getFont() {
        // no operation
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getBackground(int columnIndex) {
        // no operation
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
    public List<WSearchEditorMigrationRow> getChildren() {
        return getChildList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(getStatus());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getCategory());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getPackage());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getSrcLibrary());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getDestLibrary());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getClazz());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getHitNum());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getFiles());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getRowNumber());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getDetail());
        sb.append("\"");
        sb.append(CmnStringUtil.CSV_DELIMITER);
        sb.append("\"");
        sb.append(getNote());
        sb.append("\"");
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSearchEditorMigrationRow getMoreParent() {
        if (getLevel() == LEVEL.FIRST.getLevel()) {
            return this;
        }
        if (getLevel() == LEVEL.SECOND.getLevel()) {
            return getParent();
        }
        if (getLevel() == LEVEL.THIRD.getLevel()) {
            return getParent().getParent();
        }
        if (getLevel() == LEVEL.FOURTH.getLevel()) {
            return getParent().getParent().getParent();
        }
        return getParent().getParent().getParent().getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWriteData() {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getForeground(int columnIndex) {
        // no operation
        return null;
    }

    /**
     * Library comparison class.<br/>
     * Class to be compared to be the order of in the library configuration<br/>
     */
    private static class LibraryComparetor implements Comparator<String>,
            Serializable {
        /**
         * Serialization key
         */
        private static final long serialVersionUID = -7251919997296472324L;

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(String o1, String o2) {
            int order1 = Integer.MAX_VALUE;
            int order2 = Integer.MAX_VALUE;
            try {
                Map<String, LibraryRepository> map = Activator
                        .getLibraryRepositories();
                if (map.containsKey(o1)) {
                    order1 = map.get(o1).getOrder();
                }
                if (map.containsKey(o2)) {
                    order2 = map.get(o2).getOrder();
                }
            } catch (WorkbenchException e) {
                Activator.log(e);
            }
            return order1 - order2;
        }
    }
}