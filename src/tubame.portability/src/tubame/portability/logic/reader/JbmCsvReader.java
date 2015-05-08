/*
 * JbmCsvReader.java
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
package tubame.portability.logic.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.model.ConfirmItemEnum;
import tubame.portability.model.ConfirmItemStatusEnum;
import tubame.portability.model.JbmCsvEnum;
import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.util.CsvUtil;
import tubame.portability.util.PythonUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * (JBM) file read, converted to @ link JbmEditorMigrationRow} object search
 * results. <br/>
 */
public class JbmCsvReader implements JbmReader {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JbmCsvReader.class);

    /**
     * Number Pattern
     */
    private static final Pattern PATTERN_INT = Pattern.compile("[0-9]+"); //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JbmEditorMigrationRow> read(String filePath)
            throws JbmException {
        LOGGER.debug("[filePath]" + filePath);

        // Read the file
        List<String> rowList = createListForFile(filePath);
        return convert(rowList);
    }

    /**
     * Reads the results file, to create a string list.<br/>
     * Throw the JbmException format check, the IO abnormal.<br/>
     * 
     * @param filePath
     *            File Path
     * @return Line list data
     * @throws JbmException
     *             IO error or format check
     */
    public List<String> createListForFile(String filePath) throws JbmException {
        LOGGER.debug("[filePath]" + filePath);
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            inputStreamReader = new InputStreamReader(fileInputStream,
                    CsvUtil.CHAR_SET);
            bufferedReader = new BufferedReader(inputStreamReader);
            List<String> rowList = new ArrayList<String>();
            int lineCount = 0;
            Map<String,Integer> progressStatusMap = new HashMap<String,Integer>();
            
            while (true) {
                lineCount++;
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                List<String> tokenList = CsvUtil.parseCsv(line);
                allFormatCheck(lineCount, tokenList);
                String number = tokenList.get(0);
                if( number!= null){
                	progressStatusMap.put(number, lineCount);
                }
                rowList.add(line);
            }
            PythonUtil.PY_SEARCH_PROGRESS_STATUS_MAP = progressStatusMap;
            
            return rowList;
        } catch (FileNotFoundException e) {
            throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR, new String[] {
                    MessageUtil.ERR_JBM_NOT_FOUND, filePath });
        } catch (IOException e) {
            throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR, new String[] {
                    MessageUtil.ERR_JBM_IO, filePath });
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    JbmException.outputExceptionLog(e, LOGGER,
                            ERROR_LEVEL.ERROR, new String[] {
                                    MessageUtil.ERR_JBM_IO, filePath });
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    JbmException.outputExceptionLog(e, LOGGER,
                            ERROR_LEVEL.ERROR, new String[] {
                                    MessageUtil.ERR_JBM_IO, filePath });
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    JbmException.outputExceptionLog(e, LOGGER,
                            ERROR_LEVEL.ERROR, new String[] {
                                    MessageUtil.ERR_JBM_IO, filePath });
                }
            }
        }
    }


	/**
     * Check format of one line of the CSV.<br/>
     * Number, line number format check of Delimiter.<br/>
     * 
     * @param line
     *            Number of lines
     * @param tokenList
     *            Line data (delimiter separated)
     * @throws JbmException
     *             Format abnormal
     */
    private void allFormatCheck(int line, List<String> tokenList)
            throws JbmException {
        checkCsvDelimiterNum(tokenList, line);
        checkRowLineNo(tokenList.get(JbmCsvEnum.TOKEN_INDEX_ROW_NO.getCode()),
                line);
       
    }

    /**
     * Inspecting the line number in the CSV file search results.<br/>
     * If Uri format and line number is not a (number), <br>
     * exception through the {link JbmException}.<br/>
     * 
     * @param rowNo
     *            Line number
     * @param lineCount
     *            Number of lines
     * @throws JbmException
     *             Format check abnormal
     */
    private void checkRowLineNo(String rowNo, int lineCount)
            throws JbmException {
        for (String temp : Arrays.asList(rowNo.split(StringUtil.BLANK))) {
            if (!PATTERN_INT.matcher(temp).matches()
                    && !StringUtil.isEmpty(temp)) {
                throw new JbmException(null, LOGGER, ERROR_LEVEL.ERROR,
                        new String[] { String.format(
                                MessageUtil.ERR_JBM_FORMAT_CHECK_ROW_NUMBER,
                                lineCount) });
            }
        }
    }

    /**
     * Inspecting the format of the CSV file search results.<br/>
     * Investigated the number format of one line of (delimiter separated), <br/>
     * expected value (@see {@link JbmCsvEnum}) except for the exception through
     * the {@link JbmException}.<br/>
     * 
     * @param listToken
     *            One row of data
     * @param lineCount
     *            Number of lines
     * @throws JbmException
     *             Format check abnormal
     */
    private void checkCsvDelimiterNum(List<String> listToken, int lineCount)
            throws JbmException {
        for (String temp : listToken) {
            LOGGER.debug(temp);
        }
        LOGGER.debug(String.valueOf(listToken.size()));

        if (listToken.size() != JbmCsvEnum.CSV_COLUMN_NUM) {
            throw new JbmException(null, LOGGER, ERROR_LEVEL.ERROR,
                    new String[] { String.format(
                            MessageUtil.ERR_JBM_FORMAT_CHECK_SIZE, lineCount,
                            JbmCsvEnum.CSV_COLUMN_NUM) });
        }
    }

    /**
     * Convert to {@link JbmEditorMigrationRow} the line list data.<br/>
     * The conversion was a hierarchical structure to
     * {@link JbmEditorMigrationRow}.<br/>
     * 
     * @param rowData
     *            Line data
     * @return {@link JbmEditorMigrationRow} list
     */
    private List<JbmEditorMigrationRow> convert(List<String> rowData) {
        List<JbmEditorMigrationRow> rowList = new ArrayList<JbmEditorMigrationRow>();
        for (String row : rowData) {
            // Set a value to each variable from the row data
            List<String> tokenList = CsvUtil.parseCsv(row);
            JbmEditorMigrationRow item = new JbmEditorMigrationRow();
            // No.
            item.setNo(tokenList.get(JbmCsvEnum.TOKEN_INDEX_NO.getCode()));
            // Search for file name
            item.setFileName(tokenList
                    .get(JbmCsvEnum.TOKEN_INDEX_TARGET_FILE_PATH.getCode()));
            // Hits
            item.setCountNo(tokenList.get(JbmCsvEnum.TOKEN_INDEX_COUNT_NO
                    .getCode()));
            // Search corresponding line
            item.setRowNo(tokenList.get(JbmCsvEnum.TOKEN_INDEX_ROW_NO.getCode()));
            // Degree of difficulty
            item.setDifficulty(tokenList.get(JbmCsvEnum.TOKEN_INDEX_DIFFICULTY
                    .getCode()));
            // Guide chapter number
            item.setChapterNo(tokenList.get(JbmCsvEnum.TOKEN_INDEX_CHAPTER_NO
                    .getCode()));
            // Conversion flag
            item.setConvert(tokenList.get(JbmCsvEnum.TOKEN_INDEX_CHANGE_ITEM
                    .getCode()));
            // Check status
            String confirmItem = tokenList
                    .get(JbmCsvEnum.TOKEN_INDEX_CONFIRM_ITEM_KEY.getCode());
            // Set the value of the check list
            // Porting factor
            String factor = tokenList.get(JbmCsvEnum.TOKEN_INDEX_FACTOR
                    .getCode());
            if (factor.length() == 0) {
                factor = null;
            }
            item.setLineFactor(factor);
            // Difficulty details
            String detail = tokenList.get(JbmCsvEnum.TOKEN_INDEX_DEGREE_DETAIL
                    .getCode());
            if (detail.length() == 0) {
                detail = null;
            }
            item.setDegreeDetail(detail);
            // Number of lines
            String lineNum = tokenList.get(JbmCsvEnum.TOKEN_INDEX_LINE_NUMBER
                    .getCode());
            if (lineNum != null && lineNum.length() == 0) {
                lineNum = null;
            }
            item.setLineNumber(lineNum);
            // Line number basis
            String lineNumContents = tokenList
                    .get(JbmCsvEnum.TOKEN_INDEX_LINE_NUMBER_CONTENTS.getCode());
            if (lineNum != null && lineNum.length() == 0) {
                lineNum = null;
            }
            item.setLineNumberContents(lineNumContents);

            // First layer
            JbmEditorMigrationRow first = findTargetRow(rowList, item.getNo(),
                    JbmEditorMigrationRow.LEVEL_FIRST);
            if (first == null) {
                // New
                first = copyItem(item);
                first.setLevel(JbmEditorMigrationRow.LEVEL_FIRST);
                rowList.add(first);
            }
            // Second layer
            JbmEditorMigrationRow second = findTargetRow(first.getChildList(),
                    item.getFileName(), JbmEditorMigrationRow.LEVEL_SECOND);
            if (second == null) {
                // New
                second = copyItem(item);
                second.setLevel(JbmEditorMigrationRow.LEVEL_SECOND);
                second.setParent(first);
                first.addChild(second);
            }
            // Third layer
            if (StringUtil.isEmpty(item.getRowNo())) {
                first.setHasLine(false);
            } else {
                first.setHasLine(true);
                String[] confirmItems = confirmItem.split(StringUtil.BLANK);
                int confirmItemCount = 0;
                for (String third : Arrays.asList(item.getRowNo().split(
                        StringUtil.BLANK))) {
                    item.setRowNo(third);
                    if (confirmItemCount < confirmItems.length) {
                        second.addChild(createThird(second, item,
                                confirmItems[confirmItemCount]));
                    } else {
                        second.addChild(createThird(second, item, "0"));
                    }
                    confirmItemCount++;
                }
            }
        }
        return rowList;
    }

    /**
     * Search and line data of the specified level (hierarchy) from the row
     * data.<br/>
     * Get the target data in the comparison of the data in the following.<br/>
     * In the case of the first level: no, the case of the second hierarchy:
     * Search file name<br/>
     * 
     * @param targetList
     *            Line list
     * @param findString
     *            Discovery target string
     * @param level
     *            Level
     * @return If it is found, JbmEditorMigrationRow. The return NULL if it can
     *         not be found
     */
    private JbmEditorMigrationRow findTargetRow(
            List<JbmEditorMigrationRow> targetList, String findString, int level) {
        for (JbmEditorMigrationRow row : targetList) {
            // In the case of the first level
            if (JbmEditorMigrationRow.LEVEL_FIRST == level) {
                if (findString.equals(row.getNo())) {
                    return row;
                }
            }
            // The case of the second level
            if (JbmEditorMigrationRow.LEVEL_SECOND == level) {
                if (findString.equals(row.getFileName())) {
                    return row;
                }
            }
        }
        return null;
    }

    /**
     * Create a {@link JbmEditorMigrationRow}.<br/>
     * 
     * @param item
     *            Target row
     * @return {@link JbmEditorMigrationRow}
     */
    private JbmEditorMigrationRow copyItem(JbmEditorMigrationRow item) {
        JbmEditorMigrationRow row = new JbmEditorMigrationRow();
        row.setNo(item.getNo());
        row.setFileName(item.getFileName());
        row.setCountNo(item.getCountNo());
        row.setRowNo(item.getRowNo());
        row.setDifficulty(item.getDifficulty());
        row.setChapterNo(item.getChapterNo());
        row.setConvert(item.getConvert());
        row.setLineFactor(item.getLineFactor());
        row.setDegreeDetail(item.getDegreeDetail());
        row.setLineNumber(item.getLineNumber());
        row.setLineNumberContents(item.getLineNumberContents());

        return row;
    }

/**
     * Create a third layer of {@link JbmEditorMigrationRow.<br/>
     * 
     * @param second Second level
     * @param copyItem Line data
     * @param convertKey Conversion flag
     * @return {@link JbmEditorMigrationRow}
     */
    private JbmEditorMigrationRow createThird(JbmEditorMigrationRow second,
            JbmEditorMigrationRow copyItem, String convertKey) {
        JbmEditorMigrationRow third = copyItem(copyItem);
        third.setLevel(JbmEditorMigrationRow.LEVEL_THIRD);
        ConfirmItemStatusEnum item = ConfirmItemStatusEnum
                .getForString(convertKey);
        if (item != null) {
            ConfirmItemEnum confirmItemEnum = ConfirmItemEnum
                    .createCheckStatus(item);
            if (confirmItemEnum != null) {
                third.setCheckEyeStatus(String.valueOf(confirmItemEnum
                        .getType()));
            }
            confirmItemEnum = ConfirmItemEnum.createHiaringStatus(item);
            if (confirmItemEnum != null) {
                third.setHearingStatus(String.valueOf(confirmItemEnum.getType()));
            }
        }
        third.setParent(second);
        return third;
    }

    
}
