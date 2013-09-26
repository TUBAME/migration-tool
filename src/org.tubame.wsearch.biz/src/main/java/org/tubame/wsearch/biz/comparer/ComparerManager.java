/*
 * ComparerManager.java
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
package org.tubame.wsearch.biz.comparer;

import java.io.IOException;

/**
 * It is a class that provides an instance of the class comparison process as
 * necessary.<br/>
 * One each by holding an instance of the class comparison process corresponding
 * to each file type, and provides them as needed.<br/>
 */
public class ComparerManager {

    /**
     * Comparer of Java files
     */
    private JavaComparer javaComparer;
    /**
     * Comparer of jsp files
     */
    private JspComparer jspComparer;
    /**
     * Comparer of XML files
     */
    private XmlComparer xmlComparer;

    /**
     * The type that defines the type of the comparer.<br/>
     */
    public enum COMPARER_TYPE {
        JAVA, JSP, XML
    }

    /**
     * Constructor.<br/>
     * Private constructor, to use the singleton pattern.<br/>
     */
    private ComparerManager() {
    }

    /**
     * This is a class to hold the entity of ComparerManager.
     */
    private static class ComparerManagerHolder {
        /**
         * Entity of ComparerManager
         */
        private static final ComparerManager instance = new ComparerManager();
    }

    /**
     * Get the entity of this class.
     * 
     * @return Instance of class ComparerManager
     */
    public static ComparerManager getInstance() {
        return ComparerManagerHolder.instance;
    }

    /**
     * Initialize each comparer.<br/>
     * Comparer must be initialized to search each, because it has a library
     * information inside.<br/>
     */
    public void initialize() {
        this.javaComparer = null;
        this.jspComparer = null;
        this.xmlComparer = null;
    }

    /**
     * Get a comparer that according to the type.<br/>
     * 
     * @param type
     *            Type of comparer
     * @return comparer
     * @throws IOException
     *             If it fails to generate the comparer
     */
    public synchronized AbstractComparer getComparer(COMPARER_TYPE type)
            throws IOException {
        if (type == COMPARER_TYPE.JAVA) {
            if (this.javaComparer == null) {
                this.javaComparer = new JavaComparer();
            }
            return this.javaComparer;
        }
        if (type == COMPARER_TYPE.JSP) {
            if (this.jspComparer == null) {
                this.jspComparer = new JspComparer();
            }
            return this.jspComparer;
        }
        if (type == COMPARER_TYPE.XML) {
            if (this.xmlComparer == null) {
                this.xmlComparer = new XmlComparer();
            }
            return this.xmlComparer;
        }
        return null;
    }
}
