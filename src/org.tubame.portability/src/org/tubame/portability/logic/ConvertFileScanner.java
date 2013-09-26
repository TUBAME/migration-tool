/*
 * ConvertFileScanner.java
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
package org.tubame.portability.logic;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import org.tubame.portability.util.FileUtil;
import org.tubame.portability.util.resource.ResourceUtil;

/**
 * Confirmation TODO exists in the file from the file list.<br/>
 * Than the file list, to get the file data, determine that there is TODO
 * string.<br/>
 */
public class ConvertFileScanner {
    /**
     * File path list
     */
    private final List<String> filePathList;

    /**
     * Constructor.<br/>
     * 
     * @param filePathList
     *            File path list
     */
    public ConvertFileScanner(List<String> filePathList) {
        this.filePathList = filePathList;
    }

    /**
     * Confirmation TODO exists in the file from the file list.<br/>
     * Than the file list, to get the file data, and returns TRUE if there are
     * TODO string.<br/>
     * Returns FALSE if there is no TODO string.<br/>
     * 
     * @return TRUE:There TODO string FALSE:None TODO string
     * @throws FileNotFoundException
     *             FileNotFound exception
     */
    public boolean scanFileData() throws FileNotFoundException {
        Scanner scanner = null;
        for (String filePath : filePathList) {
            try {
                scanner = new Scanner(FileUtil.getFile(filePath));
                scanner.useDelimiter(ResourceUtil.TODO);
                scanner.hasNext();
                if (scanner.hasNext()) {
                    return true;
                }
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        }
        return false;
    }
}
