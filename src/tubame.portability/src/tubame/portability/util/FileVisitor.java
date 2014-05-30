/*
 * FileVisitor.java
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
package tubame.portability.util;

import java.io.File;
import java.io.IOException;

/**
 * This is a class for handling files recursively under the directory or file.<br/>
 * Please take advantage and to override the method inherits this class
 * depending on the action you want to implement.<br/>
 */
public class FileVisitor {

    /**
     * Result type of FileVisitor.<br/>
     */
    public static enum FileVisitResult {
        CONTINUE, SKIP_SIBLINGS, TERMINATE
    };

    /**
     * Access recursively under the directory or file specified.<br/>
     * 
     * @param target
     *            File or directory to be accessed
     * @param visitor
     *            Class that implements the method of file access
     * @return Access result
     * @throws IOException
     *             If the I / O error occurs
     */
    public static void walkFileTree(File target, FileVisitor visitor)
            throws IOException {
        walkFileTree(target, visitor, 0);
    }

    /**
     * Access recursively under the directory or file specified.<br/>
     * 
     * @param target
     *            File or directory to be accessed
     * @param visitor
     *            Class that implements the method of file access
     * @param depth
     *            Depth
     * @return Access result
     * @throws IOException
     *             If the I / O error occurs
     */
    private static FileVisitResult walkFileTree(File target,
            FileVisitor visitor, int depth) throws IOException {
        if (!target.isDirectory()) {
            return visitor.visitFile(target);
        }
        FileVisitResult result = visitor.preVisitDirectory(target);
        if (result != FileVisitResult.CONTINUE) {
            return result;
        }
        for (File subTarget : target.listFiles()) {
            result = walkFileTree(subTarget, visitor, depth + 1);
            switch (result) {
            case SKIP_SIBLINGS:
            case TERMINATE:
                return result;
            }
        }
        return visitor.postVisitDirectory(target);
    }

    /**
     * implement a process to access the file.<br/>
     * The default is to return the {@link CONTINUE}.<br/>
     * 
     * @param file
     *            Files to be accessed
     * @return Access result
     * @throws IOException
     *             If the I / O error occurs
     */
    public FileVisitResult visitFile(File file) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    /**
     * implement the previous action to access the directory.<br/>
     * The default is to return the {@link CONTINUE}.<br/>
     * 
     * @param dir
     *            Directory to be accessed
     * @return Access result
     * @throws IOException
     *             If the I / O error occurs
     */
    public FileVisitResult preVisitDirectory(File dir) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    /**
     * implements the process immediately after you have access to the
     * directory.<br/>
     * The default is to return the {@link CONTINUE}.<br/>
     * 
     * @param dir
     *            Directory to be accessed
     * @return Access result
     * @throws IOException
     *             If the I / O error occurs
     */
    public FileVisitResult postVisitDirectory(File dir) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
