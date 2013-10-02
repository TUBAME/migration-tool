/*
 * ParserResult.java
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
package tubame.wsearch.biz.analyzer;

/**
 * It is a class that contains the result information to analyze a variety of
 * source code.<br/>
 * If you need additional information they need as the analysis result, you need
 * to add a getter and member variables as appropriate.<br/>
 */
public class ParserResult {

    /** Parsed data(Class Name, URI, etc...) */
    private String value;

    /** Parsed data(Target Object) */
    private Object context;

    /** Parsed data(line) */
    private int line;

    /** Parsed data(column) */
    private int column;

    /** Parsed data(file path) */
    private String filepath;

    /**
     * Constructor.<br/>
     * Initialize the ParseResult.<br/>
     * 
     * @param value
     *            Parsed data(Class Name, URI, etc...)
     * @param filepath
     *            File path
     * @param line
     *            Parsed data(line)
     * @param column
     *            Parsed data(column)
     * @param context
     *            Parsed data(CtClass information)
     */
    public ParserResult(String value, String filepath, int line, int column,
            Object context) {
        this.value = value;
        this.filepath = filepath;
        this.line = line;
        this.column = column;
        this.context = context;
    }

    /**
     * Constructor.<br/>
     * Initialize the ParseResult.<br/>
     * Line number, column number to set the "-1".<br/>
     * 
     * @param value
     *            Parsed data(Class Name, URI, etc...)
     * @param filepath
     *            File path
     * @param context
     *            Parsed data(CtClass information)
     */
    public ParserResult(final String value, String filepath, Object context) {
        this(value, filepath, -1, -1, context);
    }

    /**
     * Return the parsed data(Class Name, URI, etc...).<br/>
     * 
     * @return Parsed data(Class Name, URI, etc...)
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Return the parsed data(ASTNode, Attributes, CtClass).<br/>
     * 
     * @return Parsed data(ASTNode, Attributes, CtClass)
     */
    public Object getContext() {
        return this.context;
    }

    /**
     * Return the parsed data(line).<br/>
     * 
     * @return Parsed data(line)
     */
    public int getLineNumber() {
        return this.line;
    }

    /**
     * Return the parsed data(column).<br/>
     * 
     * @return Parsed data(column)
     */
    public int getColumnNumber() {
        return this.column;
    }

    /**
     * Return the file path.<br/>
     * 
     * @return File path
     */
    public String getFilePath() {
        return this.filepath;
    }

    /**
     * Return hash code.<br/>
     * The hash code is defined to be the result of the following calculation.<br/>
     * 
     * <pre>
     * final int multiplier = 37;
     * int result = 17;
     * result = multiplier * result + line;
     * result = multiplier * result + column;
     * result = multiplier * result + fileName.hashCode();
     * result = multiplier * result + context.hashCode();
     * </pre>
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + line;
        result = 37 * result + column;
        result = 37 * result + filepath.hashCode();
        result = 37 * result + context.hashCode();
        return result;
    }

    /**
     * Compares the specified object for equality with this instance.<br/>
     * The given object is in the same class if and only if the member variable
     * is equal to all, and then return true.<br/>
     * The hash code is defined to be the result of the following calculation.<br/>
     * 
     * @return True if the specified object is equal to this instance
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ParserResult other = (ParserResult) obj;
        if (line != other.line) {
            return false;
        }
        if (column != other.column) {
            return false;
        }
        if (context == null) {
            if (other.getContext() != null) {
                return false;
            }
        } else if (!context.equals(other.getContext())) {
            return false;
        }
        if (context == null) {
            if (other.getContext() != null) {
                return false;
            }
        } else if (!context.equals(other.getContext())) {
            return false;
        }
        return true;
    }

    /**
     * Return a string representation of this instance.<br/>
     * The given object is in the same class if and only if the member variable
     * is equal to all, and then return true.<br/>
     * The string representation is as follows.<br/>
     * fileName + line + ',' + column + '[' + value + ']'<br/>
     * 
     * @return String representation of this instance
     */
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        if (filepath != null) {
            buf.append(filepath);
        }
        if (this.line >= 0) {
            buf.append(":" + this.line);
        }
        if (this.column >= 0) {
            buf.append("," + this.column);
        }
        if (this.value != null) {
            buf.append("[" + this.value + "]");
        }
        return buf.toString();
    }
}
