/*
 * Mark.java
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tubame.wsearch.biz.analyzer;

/**
 * Mark represents a point in the JSP input.
 * 
 * #Modified By Nippon Telegraph and Telephone Corporation 2013/3/15
 * 
 * @author Anil K. Vijendran
 */
public class Mark {

    // position within current stream
    public int cursor, line, col;

    // directory of file for current stream
    protected String baseDir;

    // current stream
    protected char[] stream = null;

    // fileid of current stream
    private int fileId;

    // name of the current file
    private String fileName;

    /**
     * Constructor
     * 
     * @param inStream
     *            current stream for this mark
     * @param fileId
     *            id of requested jsp file
     * @param name
     *            JSP file name
     * @param inBaseDir
     *            base directory of requested jsp file
     */
    protected Mark(final char[] inStream, final int fileId, final String name,
            final String inBaseDir) {
        this.stream = inStream;
        this.cursor = 0;
        this.line = 1;
        this.col = 1;
        this.fileId = fileId;
        this.fileName = name;
        this.baseDir = inBaseDir;
    }

    /**
     * Constructor
     */
    protected Mark(final Mark other) {
        init(other, false);
    }

    /**
     * Update the mark position.<br/>
     * 
     * @param cursor
     *            Cursor
     * @param line
     *            Line
     * @param col
     *            Column
     */
    protected void update(final int cursor, final int line, final int col) {
        this.cursor = cursor;
        this.line = line;
        this.col = col;
    }

    /**
     * Initializes a mark.<br/>
     * 
     * @param other
     *            Mark
     * @param singleFile
     *            Single file flag
     */
    protected void init(final Mark other, final boolean singleFile) {
        this.cursor = other.cursor;
        this.line = other.line;
        this.col = other.col;

        if (!singleFile) {
            this.stream = other.stream;
            this.fileId = other.fileId;
            this.fileName = other.fileName;
            this.baseDir = other.baseDir;
        }
    }

    /**
     * Constructor
     */
    protected Mark(final String filename, final int line, final int col) {
        this.stream = null;
        this.cursor = 0;
        this.line = line;
        this.col = col;
        this.fileId = -1;
        this.fileName = filename;
        this.baseDir = "le-basedir";
    }

    /**
     * Sets this mark's state to a new stream. It will store the current stream
     * in it's includeStack.
     * 
     * @param inStream
     *            new stream for mark
     * @param inFileId
     *            id of new file from which stream comes from
     * @param name
     *            file name
     * @param inBaseDir
     *            directory of file
     */
    public void pushStream(final char[] inStream, final int inFileId,
            final String name, final String inBaseDir) {
        // set new variables
        cursor = 0;
        line = 1;
        col = 1;
        fileId = inFileId;
        fileName = name;
        baseDir = inBaseDir;
        stream = inStream;
    }

    /**
     * Restores this mark's state to a previously stored stream.
     * 
     * @return The previous Mark instance when the stream was pushed, or null if
     *         there is no previous stream
     */
    public Mark popStream() {
        return this;
    }

    // -------------------- Locator interface --------------------

    /**
     * Get the number of lines.<br/>
     * 
     * @return Line
     */
    public int getLineNumber() {
        return line;
    }

    /**
     * Get the number of columns.<br/>
     * 
     * @return Number of columns
     */
    public int getColumnNumber() {
        return col;
    }

    /**
     * Get the system ID.<br/>
     * 
     * @return System ID
     */
    public String getSystemId() {
        return getFile();
    }

    /**
     * Get the public ID.<br/>
     * 
     * @return Public ID
     */
    public String getPublicId() {
        // The return a Null always(method that is not used)
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getFile() + "(" + line + "," + col + ")";
    }

    /**
     * Get the file name.<br/>
     * 
     * @return File name
     */
    public String getFile() {
        return this.fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + fileId;
        result = 37 * result + cursor;
        result = 37 * result + line;
        result = 37 * result + col;
        result = 37 * result + baseDir.hashCode();
        result = 37 * result + fileName.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Mark) {
            final Mark mark = (Mark) other;
            return this.fileId == mark.fileId && this.cursor == mark.cursor
                    && this.line == mark.line && this.col == mark.col;
        }
        return false;
    }
}
