/*
 * JspReader.java
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
package org.tubame.wsearch.biz.analyzer;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * JspReader is an input buffer for the JSP parser. It should allow unlimited
 * lookahead and pushback. It also has a bunch of parsing utility methods for
 * understanding htmlesque thingies.
 * 
 * #Modified By Nippon Telegraph and Telephone Corporation 2013/3/15
 * 
 * @author Anil K. Vijendran
 * @author Anselm Baird-Smith
 * @author Harish Prabandham
 * @author Rajiv Mordani
 * @author Mandar Raje
 * @author Danno Ferrin
 * @author Kin-man Chung
 * @author Shawn Bayern
 * @author Mark Roth
 */
public class JspReader {

    private static final Logger logger = LoggerFactory
            .getLogger(JspReader.class);

    /**
     * The current spot in the file.
     */
    private Mark current;

    /**
     * What is this?
     */
    private String master;

    /**
     * The list of source files.
     */
    private List<String> sourceFiles;

    /**
     * The current file ID (-1 indicates an error or no file).
     */
    private int currFileId;

    /**
     * Seems redundant.
     */

    /**
     * Set to true when using the JspReader on a single file where we read up to
     * the end and reset to the beginning many times. (as in
     * ParserController.figureOutJspDocument()).
     */
    private boolean singleFile;

    /**
     * Constructor: same as above constructor but with initialized reader to the
     * file given.
     */
    public JspReader(final String fname, final InputStreamReader reader) {
        sourceFiles = new ArrayList<String>();
        currFileId = 0;
        singleFile = false;
        pushFile(fname, reader);
    }

    /**
     * Returns the file at the given position in the list.
     * 
     * @param fileid
     *            The file position in the list
     * @return The file at that position, if found, null otherwise
     */
    protected String getFile(final int fileid) {
        return sourceFiles.get(fileid);
    }

    /**
     * Checks if the current file has more input.
     * 
     * @return True if more reading is possible
     */
    public boolean hasMoreInput() {
        if (current.cursor >= current.stream.length) {
            if (singleFile) {
                return false;
            }
            while (popFile()) {
                if (current.cursor < current.stream.length) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Get the character next character.<br/>
     * 
     * @return chara
     */
    public int nextChar() {
        if (!hasMoreInput()) {
            return -1;
        }
        final int chara = current.stream[current.cursor];
        current.cursor++;
        if (chara == '\n') {
            current.line++;
            current.col = 0;
        } else {
            current.col++;
        }
        return chara;
    }

    /**
     * A faster approach than calling {@link #mark()} & {@link #nextChar()}.
     * However, this approach is only safe if the mark is only used within the
     * JspReader.
     */
    private int nextChar(final Mark mark) {
        if (!hasMoreInput()) {
            return -1;
        }
        final int chara = current.stream[current.cursor];
        mark.init(current, singleFile);
        current.cursor++;
        if (chara == '\n') {
            current.line++;
            current.col = 0;
        } else {
            current.col++;
        }
        return chara;
    }

    /**
     * Search the given character, If it was found, then mark the current cursor
     * and the cursor point to next character.
     */
    private Boolean indexOf(final char targetChara, final Mark mark) {
        if (!hasMoreInput()) {
            return Boolean.FALSE;
        }
        final int end = current.stream.length;
        int chara;
        int line = current.line;
        int col = current.col;
        int index = current.cursor;
        for (; index < end; index++) {
            chara = current.stream[index];

            if (chara == targetChara) {
                mark.update(index, line, col);
            }
            if (chara == '\n') {
                line++;
                col = 0;
            } else {
                col++;
            }
            if (chara == targetChara) {
                current.update(index + 1, line, col);
                return Boolean.TRUE;
            }
        }
        current.update(index, line, col);
        return Boolean.FALSE;
    }

    /**
     * Back up the current cursor by one char, assumes current.cursor > 0, and
     * that the char to be pushed back is not '\n'.
     */
    public void pushChar() {
        current.cursor--;
        current.col--;
    }

    /**
     * Get the text.<br/>
     * 
     * @param start
     *            Start
     * @param stop
     *            stop
     * @return Text string
     */
    public String getText(final Mark start, final Mark stop) {
        final Mark oldstart = mark();
        reset(start);
        final CharArrayWriter caw = new CharArrayWriter();
        while (!markEquals(stop)) {
            caw.write(nextChar());
        }
        caw.close();
        setCurrent(oldstart);
        return caw.toString();
    }

    /**
     * Get the character character of the peek.<br/>
     * 
     * @return Character character of peek
     */
    public int peekChar() {
        if (!hasMoreInput()) {
            return -1;
        }
        return current.stream[current.cursor];
    }

    /**
     * Mark.
     * 
     * @return Mark
     */
    public Mark mark() {
        return new Mark(current);
    }

    /**
     * This method avoids a call to {@link #mark()} when doing comparison.
     */
    private boolean markEquals(final Mark another) {
        return another.equals(current);
    }

    /**
     * Reset the mark.<br/>
     * 
     * @param mark
     *            Mark
     */
    public void reset(final Mark mark) {
        current = new Mark(mark);
    }

    /**
     * Similar to {@link #reset(Mark)} but no new Mark will be created.
     * Therefore, the parameter mark must NOT be used in other places.
     */
    private void setCurrent(final Mark mark) {
        current = mark;
    }

    /**
     * search the stream for a match to a string
     * 
     * @param string
     *            The string to match
     * @return <strong>true</strong> is one is found, the current position in
     *         stream is positioned after the search string, <strong>
     *         false</strong> otherwise, position in stream unchanged.
     */
    public boolean matches(final String string) {
        final int len = string.length();
        final int cursor = current.cursor;
        final int streamSize = current.stream.length;
        if (cursor + len < streamSize) { // Try to scan in memory
            int line = current.line;
            int col = current.col;
            int chara;
            int index = 0;
            for (; index < len; index++) {
                chara = current.stream[index + cursor];
                if (string.charAt(index) != chara) {
                    return false;
                }
                if (chara == '\n') {
                    line++;
                    col = 0;
                } else {
                    col++;
                }
            }
            current.update(index + cursor, line, col);
        } else {
            final Mark mark = mark();
            int chara = 0;
            int index = 0;
            do {
                chara = nextChar();
                if (((char) chara) != string.charAt(index++)) {
                    setCurrent(mark);
                    return false;
                }
            } while (index < len);
        }
        return true;
    }

    /**
     * Determine End tag or matched.<br/>
     * 
     * @param tagName
     *            Tag name
     * @return If true match
     */
    public boolean matchesETag(final String tagName) {
        final Mark mark = mark();
        if (!matches("</" + tagName)) {
            return false;
        }
        skipSpaces();
        if (nextChar() == '>') {
            return true;
        }
        setCurrent(mark);
        return false;
    }

    /**
     * Determine whether or No Less Than End tag.<br/>
     * 
     * @param tagName
     *            Tag name
     * @return If true match
     */
    public boolean matchesETagWithoutLessThan(final String tagName) {
        final Mark mark = mark();
        if (!matches("/" + tagName)) {
            return false;
        }
        skipSpaces();
        if (nextChar() == '>') {
            return true;
        }
        setCurrent(mark);
        return false;
    }

    /**
     * Looks ahead to see if there are optional spaces followed by the given
     * String. If so, true is returned and those spaces and characters are
     * skipped. If not, false is returned and the position is restored to where
     * we were before.
     */
    protected boolean matchesOptionalSpacesFollowedBy(final String s) {
        final Mark mark = mark();
        skipSpaces();
        final boolean result = matches(s);
        if (!result) {
            setCurrent(mark);
        }
        return result;
    }

    /**
     * Skip the space.<br/>
     * 
     * @return index
     */
    public int skipSpaces() {
        int index = 0;
        while (hasMoreInput() && isSpace()) {
            index++;
            nextChar();
        }
        return index;
    }

    /**
     * Skip until the given string is matched in the stream. When returned, the
     * context is positioned past the end of the match.
     * 
     * @param limit
     *            The String to match.
     * @return A non-null <code>Mark</code> instance (positioned immediately
     *         before the search string) if found, <strong>null</strong>
     *         otherwise.
     */
    public Mark skipUntil(final String limit) {
        final Mark ret = mark();
        final int limlen = limit.length();
        final char firstChar = limit.charAt(0);
        Mark restart = null;
        skip: while (indexOf(firstChar, ret)) {
            if (restart == null) {
                restart = mark();
            } else {
                restart.init(current, singleFile);
            }
            for (int i = 1; i < limlen; i++) {
                if (peekChar() == limit.charAt(i)) {
                    nextChar();
                } else {
                    setCurrent(restart);
                    continue skip;
                }
            }
            return ret;
        }
        return null;
    }

    /**
     * Skip until the given string is matched in the stream, but ignoring chars
     * initially escaped by a '\'. When returned, the context is positioned past
     * the end of the match.
     * 
     * @param limit
     *            The String to match.
     * @return A non-null <code>Mark</code> instance (positioned immediately
     *         before the search string) if found, <strong>null</strong>
     *         otherwise.
     */
    public Mark skipUntilIgnoreEsc(final String limit) {
        final Mark ret = mark();
        final int limlen = limit.length();
        int chara;
        int prev = 'x'; // Doesn't matter
        final char firstChar = limit.charAt(0);
        skip: for (chara = nextChar(ret); chara != -1; prev = chara, chara = nextChar(ret)) {
            if (chara == '\\' && prev == '\\') {
                chara = 0; // Double \ is not an escape char anymore
            } else if (chara == firstChar && prev != '\\') {
                for (int i = 1; i < limlen; i++) {
                    if (peekChar() == limit.charAt(i)) {
                        nextChar();
                    } else {
                        continue skip;
                    }
                }
                return ret;
            }
        }
        return null;
    }

    /**
     * Skip until the given end tag is matched in the stream. When returned, the
     * context is positioned past the end of the tag.
     * 
     * @param tag
     *            The name of the tag whose ETag (</tag>) to match.
     * @return A non-null <code>Mark</code> instance (positioned immediately
     *         before the ETag) if found, <strong>null</strong> otherwise.
     */
    public Mark skipUntilETag(final String tag) {
        Mark ret = skipUntil("</" + tag);
        if (ret != null) {
            skipSpaces();
            if (nextChar() != '>') {
                ret = null;
            }
        }
        return ret;
    }

    protected boolean isSpace() {
        // Note: If this logic changes, also update Node.TemplateText.rtrim()
        return peekChar() <= ' ';
    }

    /**
     * Parse a space delimited token. If quoted the token will consume all
     * characters up to a matching quote, otherwise, it consumes up to the first
     * delimiter character.
     * 
     * @param quoted
     *            If <strong>true</strong> accept quoted strings.
     */
    public String parseToken(final boolean quoted) {
        final StringBuilder buf = new StringBuilder();
        skipSpaces();
        buf.setLength(0);
        if (!hasMoreInput()) {
            return "";
        }
        int chara = peekChar();
        if (quoted) {
            if (chara == '"' || chara == '\'') {
                final char endQuote = chara == '"' ? '"' : '\'';
                // Consume the open quote:
                chara = nextChar();
                for (chara = nextChar(); chara != -1 && chara != endQuote; chara = nextChar()) {
                    if (chara == '\\') {
                        chara = nextChar();
                    }
                    buf.append((char) chara);
                }
                // Check end of quote, skip closing quote:
                if (chara == -1) {
                    throw new IllegalStateException(
                            MessageUtil.getJspErrorMessage(mark().toString(),
                                    this.getClass().getName()
                                            + ".err.unterminated"));
                }
            } else {
                throw new IllegalStateException(MessageUtil.getJspErrorMessage(
                        mark().toString(), this.getClass().getName()
                                + ".err.noquoted"));
            }
        } else {
            if (!isDelimiter()) {
                // Read value until delimiter is found:
                do {
                    chara = nextChar();
                    // Take care of the quoting here.
                    if (chara == '\\'
                            && (peekChar() == '"' || peekChar() == '\''
                                    || peekChar() == '>' || peekChar() == '%')) {
                        chara = nextChar();
                    }
                    buf.append((char) chara);
                } while (!isDelimiter());
            }
        }
        return buf.toString();
    }

    /**
     * Set the decision of whether or not a single file.<br/>
     * 
     * @param val
     *            True if a single file
     */
    protected void setSingleFile(final boolean val) {
        singleFile = val;
    }

    /**
     * Parse utils - Is current character a token delimiter ? Delimiters are
     * currently defined to be =, &gt;, &lt;, ", and ' or any any space
     * character as defined by <code>isSpace</code>.
     * 
     * @return A boolean.
     */
    private boolean isDelimiter() {
        if (isSpace()) {
            return true;
        } else {
            int chara = peekChar();
            // Look for a single-char work delimiter:
            if (chara == '=' || chara == '>' || chara == '"' || chara == '\''
                    || chara == '/') {
                return true;
            }
            // Look for an end-of-comment or end-of-tag:
            if (chara == '-') {
                final Mark mark = mark();
                if (((chara = nextChar()) == '>')
                        || ((chara == '-') && (nextChar() == '>'))) {
                    setCurrent(mark);
                    return true;
                } else {
                    setCurrent(mark);
                    return false;
                }
            }
            return false;
        }
    }

    /**
     * Register a new source file. This method is used to implement file
     * inclusion. Each included file gets a unique identifier (which is the
     * index in the array of source files).
     * 
     * @return The index of the now registered file.
     */
    private int registerSourceFile(final String file) {
        if (sourceFiles.contains(file)) {
            return -1;
        }
        sourceFiles.add(file);
        return sourceFiles.size() - 1;
    }

    /**
     * Unregister the source file. This method is used to implement file
     * inclusion. Each included file gets a unique identifier (which is the
     * index in the array of source files).
     * 
     * @return The index of the now registered file.
     */
    private int unregisterSourceFile(final String file) {
        if (!sourceFiles.contains(file)) {
            return -1;
        }
        sourceFiles.remove(file);
        return sourceFiles.size() - 1;
    }

    /**
     * Push a file (and its associated Stream) on the file stack. THe current
     * position in the current file is remembered.
     */
    private void pushFile(final String file, final InputStreamReader reader) {
        // Register the file
        final String longName = file;
        final int fileid = registerSourceFile(longName);
        if (fileid == -1) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Without the need for handling
                }
            }
            throw new IllegalStateException(MessageUtil.getJspErrorMessage(
                    mark().toString(), this.getClass().getName()
                            + ".err.recursive", file));
        }

        currFileId = fileid;
        try {
            final CharArrayWriter caw = new CharArrayWriter();
            final char buf[] = new char[1024];
            for (int i = 0; (i = reader.read(buf)) != -1;) {
                caw.write(buf, 0, i);
            }
            caw.close();
            if (current == null) {
                current = new Mark(caw.toCharArray(), fileid, getFile(fileid),
                        master);
            } else {
                current.pushStream(caw.toCharArray(), fileid, getFile(fileid),
                        longName);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            // Pop state being constructed:
            popFile();
            throw new IllegalStateException(MessageUtil.getJspErrorMessage(
                    mark().toString(), this.getClass().getName()
                            + ".err.canntread", file));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    // Without the need for handling
                }
            }
        }
    }

    /**
     * Pop a file from the file stack. The field "current" is retored to the
     * value to point to the previous files, if any, and is set to null
     * otherwise.
     * 
     * @return true is there is a previous file on the stack. false otherwise.
     */
    private boolean popFile() {
        // Is stack created ? (will happen if the Jsp file we're looking at is
        // missing.
        if (current == null || currFileId < 0) {
            return false;
        }

        // Restore parser state:
        final String fName = getFile(currFileId);
        currFileId = unregisterSourceFile(fName);
        if (currFileId < -1) {
            throw new IllegalStateException(MessageUtil.getJspErrorMessage(
                    mark().toString(), this.getClass().getName()
                            + ".err.notseen", fName));
        }

        final Mark previous = current.popStream();
        master = current.baseDir;
        current = previous;
        return true;
    }
}
