/*
 * CSVUtil.java
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
package tubame.wsearch.util;

import java.util.LinkedList;
import java.util.List;

import tubame.common.util.CmnStringUtil;

/**
 * This is a utility class that the CSV operation.<br/>
 * It is a utility for saving and reading etc. in CSV format.<br/>
 */
public class CSVUtil {

    /**
     * Constructor.<br/>
     * It can not be instantiated from another class.<br/>
     */
    private CSVUtil() {
        super();
    }

    /**
     * Analyze the CSV.<br/>
     * 
     * @param data
     *            Analyzed data
     * @return Analysis result
     */
    public static List<List<String>> parseCsv(List<String> data) {
        return CSVUtil.parseCsv(data, CmnStringUtil.CSV_DELIMITER);
    }

    /**
     * Analyze the CSV.<br/>
     * 
     * @param data
     *            Analyzed data
     * @param delimiter
     *            Delimiter
     * @return Analysis result
     */
    public static List<List<String>> parseCsv(List<String> data,
            String delimiter) {
        StringBuilder buffer = null;
        List<List<String>> list = new LinkedList<List<String>>();
        List<String> line = null;
        for (String text : data) {
            if (CmnStringUtil.isEmpty(text)) {
                continue;
            }
            if (line == null) {
                line = new LinkedList<String>();
            }
            String[] splits = text.split(delimiter);
            for (String value : splits) {
                if (buffer == null) {
                    if (value.startsWith("\"")) {
                        if (value.endsWith("\"")) {
                            line.add(value.substring(1, (value.length() - 1)));
                        } else {
                            buffer = new StringBuilder(value.substring(1));
                        }
                    } else {
                        line.add(value);
                    }
                } else {
                    if (value.endsWith("\"")) {
                        buffer.append(delimiter);
                        buffer.append(value.substring(0, (value.length() - 1)));
                        line.add(buffer.toString());
                        buffer = null;
                    } else {
                        buffer.append(delimiter);
                        buffer.append(value);
                    }
                }
            }
            if (buffer == null) {
                list.add(line);
                line = null;
            }
        }
        return list;
    }
}