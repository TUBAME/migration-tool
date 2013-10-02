/*
 * CacheBase.java
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
package tubame.wsearch.biz.cache;

import java.io.File;

/**
 * It is the base class of the class that holds the cache information.<br/>
 * To match the type of cache, you can use it to create a subclass of this
 * class.<br/>
 */
public abstract class CacheBase {

    /**
     * It is a class of type that defines the type of cache.<br/>
     */
    public enum TYPE {
        ANALYZE("analyze.cache"), LIBRARY("library.cache");

        /**
         * Base of the directory that holds the cache
         */
        private String baseDirName;

        /**
         * Get the base of the directory that holds the cache.<br/>
         * 
         * @return Base of the directory that holds the cache
         */
        public String getBaseDirName() {
            return baseDirName;
        }

        /**
         * Constructor.<br/>
         * No operation.<br/>
         * 
         * @param baseDirName
         *            Base of the directory that holds the cache
         */
        private TYPE(String baseDirName) {
            this.baseDirName = baseDirName;
        }
    }

    /**
     * Path to the directory where you want to store the cache in general
     */
    protected String homePath;

    /**
     * Cache name
     */
    private String name;

    /**
     * Get the cache name.<br/>
     * 
     * @return Cache name
     */
    public String getName() {
        return name;
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param homePath
     *            Path to the directory where you want to store the cache in
     *            general
     * @param name
     *            Cache name
     */
    public CacheBase(String homePath, String name) {
        super();
        this.homePath = homePath;
        this.name = name;
    }

    /**
     * Get the path to the directory in which to store the cache in general.<br/>
     * 
     * @return Path to the directory where you want to store the cache in
     *         general
     */
    public String getHomePath() {
        return homePath;
    }

    /**
     * Determine file on the specified path exists.<br/>
     * 
     * @param path
     *            The path of the file to find out whether there
     * @return True if the file exists
     */
    protected boolean fileExist(String path) {
        File file = new File(path);
        return file.exists();
    }
}
