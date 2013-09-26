/*
 * CsvUtil.java
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
package org.tubame.portability.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tubame.portability.exception.JbmException;
import org.tubame.portability.exception.JbmException.ERROR_LEVEL;
import org.tubame.portability.util.resource.ApplicationPropertyUtil;
import org.tubame.portability.util.resource.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for saving and reading etc. in CSV format.<br/>
 */
public class CsvUtil {

    /**
     * Character set
     */
    public static final String CHAR_SET = ApplicationPropertyUtil.CHARSET_CSV;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtil.class);

    /**
     * Constructor.<br/>
     * It can not be instantiated from another class.<br/>
     * 
     */
    private CsvUtil() {
        super();
    }

    /**
     * It returned in List format a comma-delimited string of csv format that is
     * specified in the argument.<br/>
     * 
     * @param text
     *            Target string
     * @return List
     */
    public static List<String> parseCsv(String text) {
        return CsvUtil.parseCsv(text, StringUtil.CSV_DELIMITER);
    }

    /**
     * It returned in List format is divided at the specified string of csv
     * format that is specified in the argument.<br/>
     * 
     * @param text
     *            Target string
     * @param delimiter
     *            Delimiter
     * @return List
     */
    public static List<String> parseCsv(String text, String delimiter) {
        return CsvUtil.splitLine(text, delimiter);
    }

    /**
     * Get specific string from line of CSV.<br/>
     * 
     * @param text
     *            Target string
     * @param column
     *            Number of columns
     * @return Specific character string
     */
    public static String getSpecificColumn(String text, int column) {
        return CsvUtil.parseCsv(text, StringUtil.CSV_DELIMITER).get(column);
    }

    /**
     * Save in CSV format.<br/>
     * 
     * @param list
     *            Line data
     * @param fileName
     *            Save file name (full path)
     * @param isLineSeparator
     *            Whether to add a new line fu code
     * @throws JbmException
     *             File IO error
     */
    public static void save(List<String> list, String fileName,
            boolean isLineSeparator) throws JbmException {
        // Save the model to the specified file
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName, false), CsvUtil.CHAR_SET));
            for (String line : list) {
                writer.write(line);
                if (isLineSeparator) {
                    writer.write(StringUtil.LINE_SEPARATOR);
                }
            }
        } catch (IOException e) {
            throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR, new String[] {
                    MessageUtil.ERR_JBM_IO, fileName });
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    JbmException.outputExceptionLog(e, LOGGER,
                            ERROR_LEVEL.ERROR, new String[] {
                                    MessageUtil.ERR_FILE_CLOSE, fileName });
                }
            }
        }
    }

    /**
     * To an array of field by dividing the line of text.<br/>
     * 
     * @param line
     *            Text data of one line.
     * @param delimiter
     *            CSV delimiter
     * @return Field list
     */
    private static List<String> splitLine(String line, String delimiter) {
        String[] columns = line.split(delimiter);
        ArrayList<String> fieldList = new ArrayList<String>();

        for (int index = 0; index < columns.length; index++) {
            String column = columns[index];

            if (!column.startsWith("\"")) {
                // If the first is a quote other than added as one field
                fieldList.add(CsvUtil.unescapeQuote(column));

            } else {
                // Count the number of quotes
                int quoteCnt = countQuote(column.substring(1));

                if (column.endsWith("\"") && (quoteCnt % 2) != 0) {
                    // If the leading and trailing quotes, added as one field
                    // inside the quotation marks
                    fieldList.add(CsvUtil.unescapeQuote(column.substring(1,
                            column.lastIndexOf("\""))));

                } else {
                    // If the first is in quotation marks, and added as one
                    // field to the closing quote
                    index = CsvUtil.joinColumns(column.substring(1), columns,
                            index + 1, delimiter, fieldList);
                }
            }
        }
        return fieldList;
    }

    /**
     * Connected the column enclosed in quotation marks.<br/>
     * 
     * @param column
     *            Consolidated destination column
     * @param columns
     *            All columns
     * @param columnIndex
     *            The index of the column to be consolidated
     * @param delimiter
     *            CSV delimiter
     * @param joinList
     *            After ligation column store list
     * @return The index of the column was connected to the last
     */
    private static int joinColumns(String column, String[] columns,
            int columnIndex, String delimiter, List<String> joinList) {
        // If there is no closing quote
        if (columns.length <= columnIndex) {
            joinList.add(CsvUtil.unescapeQuote(column));
            return columnIndex - 1;
        }
        String joinColumn = columns[columnIndex];
        int quoteCnt = CsvUtil.countQuote(column)
                + CsvUtil.countQuote(joinColumn);
        if (joinColumn.endsWith("\"") && (quoteCnt % 2) != 0) {
            // If the quotes, the end of the column to be concatenated
            // consolidated up to 1 character before the quotation marks
            StringBuilder sb = new StringBuilder(1024);
            sb.append(column);
            sb.append(delimiter);
            if (joinColumn.length() > 1) {
                sb.append(joinColumn.substring(0, joinColumn.lastIndexOf("\"")));
            } else {
                sb.append(StringUtil.EMPTY);
            }
            joinList.add(CsvUtil.unescapeQuote(sb.toString()));
        } else {
            // If the end of the column to be consolidated is a quote other than
            // the self-recursive call method because of the continued
            columnIndex = joinColumns(column + delimiter + joinColumn, columns,
                    columnIndex + 1, delimiter, joinList);
        }
        return columnIndex;
    }

    /**
     * Cancels escape double quotes.<br/>
     * 
     * @param src
     *            Based string
     * @return String is released the escape double quotes
     */
    private static String unescapeQuote(String src) {
        return src.replaceAll("\"\"", "\"");
    }

    /**
     * Count the number of double quotes.<br/>
     * 
     * @param src
     *            Test string
     * @return Number of double quotes
     */
    private static int countQuote(String src) {
        int quoteCnt = 0;
        Pattern p = Pattern.compile("\"");
        Matcher m = p.matcher(src);
        while (m.find()) {
            quoteCnt++;
        }
        return quoteCnt;
    }
}
