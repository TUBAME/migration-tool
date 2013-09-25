/*
 * WSearchCsvReader.java
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
package org.tubame.wsearch.logics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.models.WSearchCsvEnum;
import org.tubame.wsearch.models.WSearchEditorMigrationRow;
import org.tubame.wsearch.ui.ex.WSearchPluginException;
import org.tubame.wsearch.util.CSVUtil;
import org.tubame.wsearch.util.resource.ResourceUtil;

/**
 * File reading class results.<br/>
 */
public class WSearchCsvReader implements WSearchReader {

    /**
     * Dot(.)
     */
    public static final String DOT = "\\."; //$NON-NLS-1$

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WSearchCsvReader.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WSearchEditorMigrationRow> read(String filePath)
            throws WSearchPluginException {
        // Read the file
        List<String> rowList = createListForFile(filePath);
        return convert(rowList);
    }

    /**
     * Reads the results file, to create a string list.<br/>
     * Throw the WSearchPluginException format check, the IO abnormal.<br/>
     * 
     * @param filePath
     *            File Path
     * @return Line list data
     * @throws WSearchPluginException
     *             IO error or format check
     */
    private List<String> createListForFile(String filePath)
            throws WSearchPluginException {
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            LOGGER.debug(Activator.getResourceString("debug.msg.read.file")
                    + filePath);
            fileInputStream = new FileInputStream(filePath);
            inputStreamReader = new InputStreamReader(fileInputStream,
                    ResourceUtil.DEFAULT_CSV_CHARACTER_CODE);
            bufferedReader = new BufferedReader(inputStreamReader);
            List<String> rowList = new ArrayList<String>();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                rowList.add(line);
            }
            return rowList;
        } catch (IOException e) {
            throw new WSearchPluginException(
                    Activator.getResourceString(WSearchCsvReader.class
                            .getName() + ".err.msg.FileReadErr"), e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new WSearchPluginException(
                            Activator.getResourceString(WSearchCsvReader.class
                                    .getName() + ".err.msg.FileReadErr"), e);
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    throw new WSearchPluginException(
                            Activator.getResourceString(WSearchCsvReader.class
                                    .getName() + ".err.msg.FileReadErr"), e);
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new WSearchPluginException(
                            Activator.getResourceString(WSearchCsvReader.class
                                    .getName() + ".err.msg.FileReadErr"), e);
                }
            }
        }
    }

    /**
     * The conversion was a hierarchical structure to
     * {@link WSearchEditorMigrationRow}.<br/>
     * 
     * @param rowData
     *            Line data
     * @return {@link WSearchEditorMigrationRow} list
     * @throws WSearchPluginException
     *             Format abnormal
     */
    private List<WSearchEditorMigrationRow> convert(List<String> rowData)
            throws WSearchPluginException {
        List<List<String>> rows = CSVUtil.parseCsv(rowData);

        // As the default order, sorted in order of preference result category
        // package name, class name
        try {
            Collections.sort(rows, new Comparator<List<String>>() {

                @Override
                public int compare(List<String> arg0, List<String> arg1) {
                    int result = arg0.get(
                            WSearchCsvEnum.TOKEN_INDEX_STATUS.getIndex())
                            .compareTo(
                                    arg1.get(WSearchCsvEnum.TOKEN_INDEX_STATUS
                                            .getIndex()));
                    if (result == 0) {
                        result = arg0
                                .get(WSearchCsvEnum.TOKEN_INDEX_CATEGORY
                                        .getIndex())
                                .compareTo(
                                        arg1.get(WSearchCsvEnum.TOKEN_INDEX_CATEGORY
                                                .getIndex()));
                    }
                    if (result == 0) {
                        result = arg0
                                .get(WSearchCsvEnum.TOKEN_INDEX_PACKAGE
                                        .getIndex())
                                .compareTo(
                                        arg1.get(WSearchCsvEnum.TOKEN_INDEX_PACKAGE
                                                .getIndex()));
                    }
                    if (result == 0) {
                        result = arg0
                                .get(WSearchCsvEnum.TOKEN_INDEX_CLAZZ
                                        .getIndex())
                                .compareTo(
                                        arg1.get(WSearchCsvEnum.TOKEN_INDEX_CLAZZ
                                                .getIndex()));
                    }
                    return result;
                }

            });

            List<WSearchEditorMigrationRow> rowList = new ArrayList<WSearchEditorMigrationRow>(
                    rows.size());

            int rootSegment = ResourcesPlugin.getWorkspace().getRoot()
                    .getLocation().segmentCount();

            for (List<String> tokenList : rows) {
                // Set a value to each variable from the row data
                WSearchEditorMigrationRow item = new WSearchEditorMigrationRow();

                // Result
                item.setStatus(tokenList.get(WSearchCsvEnum.TOKEN_INDEX_STATUS
                        .getIndex()));
                // Category
                item.setCategory(tokenList
                        .get(WSearchCsvEnum.TOKEN_INDEX_CATEGORY.getIndex()));
                // Package
                item.setPackage(tokenList
                        .get(WSearchCsvEnum.TOKEN_INDEX_PACKAGE.getIndex()));
                // Porting the original library
                item.setSrcLibrary(tokenList
                        .get(WSearchCsvEnum.TOKEN_INDEX_SRC_LIBRARY.getIndex()));
                // Porting library
                item.setDestLibrary(tokenList
                        .get(WSearchCsvEnum.TOKEN_INDEX_DEST_LIBRARY.getIndex()));
                // Class
                item.setClazz(tokenList.get(WSearchCsvEnum.TOKEN_INDEX_CLAZZ
                        .getIndex()));
                // Hits
                item.setHitNum("1");
                // Porting the original file name
                IPath path = new Path(
                        tokenList.get(WSearchCsvEnum.TOKEN_INDEX_FILES
                                .getIndex()));
                String file = path.removeFirstSegments(rootSegment + 1)
                        .toString();
                if (path.getDevice() == null) {
                    item.setFiles(file);
                } else {
                    item.setFiles(file.substring(path.getDevice().length()));
                }
                // Line
                item.setRowNumber(tokenList
                        .get(WSearchCsvEnum.TOKEN_INDEX_HIT_LINE.getIndex()));
                // Result detail
                item.setDetail(tokenList.get(WSearchCsvEnum.TOKEN_INDEX_DETAIL
                        .getIndex()));
                // Result detail
                item.setNote(tokenList.get(WSearchCsvEnum.TOKEN_INDEX_NOTE
                        .getIndex()));

                // First level: results
                WSearchEditorMigrationRow first = findTargetRow(rowList,
                        item.getStatus(),
                        WSearchEditorMigrationRow.LEVEL.FIRST.getLevel());
                if (first == null) {
                    // New
                    first = copyItem(item);
                    first.setLevel(WSearchEditorMigrationRow.LEVEL.FIRST
                            .getLevel());
                    rowList.add(first);
                }
                // Second level : Category
                WSearchEditorMigrationRow second = findTargetRow(
                        first.getChildList(), item.getCategory(),
                        WSearchEditorMigrationRow.LEVEL.SECOND.getLevel());
                if (second == null) {
                    // New
                    second = copyItem(item);
                    second.setLevel(WSearchEditorMigrationRow.LEVEL.SECOND
                            .getLevel());
                    second.setParent(first);
                    first.addChild(second);
                }
                // Third layer : Package
                WSearchEditorMigrationRow third = findTargetRow(
                        second.getChildList(), item.getPackage(),
                        WSearchEditorMigrationRow.LEVEL.THIRD.getLevel());
                if (third == null) {
                    // New
                    third = copyItem(item);
                    third.setLevel(WSearchEditorMigrationRow.LEVEL.THIRD
                            .getLevel());
                    third.setParent(second);
                    second.addChild(third);
                }
                // The fourth level:Class
                WSearchEditorMigrationRow fourth = findTargetRow(
                        third.getChildList(), item.getClazz(),
                        WSearchEditorMigrationRow.LEVEL.FOURTH.getLevel());
                if (fourth == null) {
                    // New
                    fourth = copyItem(item);
                    fourth.setLevel(WSearchEditorMigrationRow.LEVEL.FOURTH
                            .getLevel());
                    fourth.setParent(third);
                    third.addChild(fourth);
                }
                // Fifth level
                // New
                WSearchEditorMigrationRow fifth = copyItem(item);
                fifth.setLevel(WSearchEditorMigrationRow.LEVEL.FIFTH.getLevel());
                fifth.setParent(fourth);
                fourth.addChild(fifth);
            }

            return rowList;

        } catch (Exception e) {
            throw new WSearchPluginException(e);
        }
    }

    /**
     * Create a {@link WSearchEditorMigrationRow}.<br/>
     * 
     * @param item
     *            Target row
     * @return {@link WSearchEditorMigrationRow}
     */
    private WSearchEditorMigrationRow copyItem(WSearchEditorMigrationRow item) {
        WSearchEditorMigrationRow row = new WSearchEditorMigrationRow();
        row.setStatus(item.getStatus());
        row.setCategory(item.getCategory());
        row.setPackage(item.getPackage());
        row.setSrcLibrary(item.getSrcLibrary());
        row.setDestLibrary(item.getDestLibrary());
        row.setClazz(item.getClazz());
        row.setHitNum(item.getHitNum());
        row.setFiles(item.getFiles());
        row.setRowNumber(item.getRowNumber());
        row.setDetail(item.getDetail());
        row.setNote(item.getNote());
        return row;
    }

    /**
     * What's more, you can search and line data of the specified level
     * (hierarchy) from the row data.<br/>
     * 
     * @param targetList
     *            Line list
     * @param findString
     *            Discovery target string
     * @param level
     *            Hierarchy
     * @return If could find,NewJbmEditorMigrationRow. The return NULL if it can
     *         not be found
     */
    private WSearchEditorMigrationRow findTargetRow(
            List<WSearchEditorMigrationRow> targetList, String findString,
            int level) {
        for (WSearchEditorMigrationRow row : targetList) {
            // In the case of the first level:Result
            if (WSearchEditorMigrationRow.LEVEL.FIRST.getLevel() == level) {
                if (findString.equals(row.getStatus())) {
                    return row;
                }
            }
            // The case of the second hierarchy:Category
            if (WSearchEditorMigrationRow.LEVEL.SECOND.getLevel() == level) {
                if (findString.equals(row.getCategory())) {
                    return row;
                }
            }
            // For the third layer:Package
            if (WSearchEditorMigrationRow.LEVEL.THIRD.getLevel() == level) {
                if (findString.equals(row.getPackage())) {
                    return row;
                }
            }
            // For the fourth level:Class
            if (WSearchEditorMigrationRow.LEVEL.FOURTH.getLevel() == level) {
                if (findString.equals(row.getClazz())) {
                    return row;
                }
            }
        }
        return null;
    }
}
