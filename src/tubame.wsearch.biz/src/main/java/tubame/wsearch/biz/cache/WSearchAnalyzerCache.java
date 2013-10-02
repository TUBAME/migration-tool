/*
 * WSearchAnalyzerCache.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.wsearch.biz.WSPhaseService;
import tubame.wsearch.biz.ex.WSearchBizException;
import tubame.wsearch.biz.util.FileVisitor;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * It is a class that will handle the cache of the analysis result information
 * Migrated.<br/>
 */
public class WSearchAnalyzerCache extends CacheBase implements
        CacheProxy<WSearchAnalyzerCacheArgument> {

    /**
     * A proxy that read of cache, implementing an operation for of and
     * destruction
     */
    private CacheProxy<WSearchAnalyzerCacheArgument> delegate;

    /**
     * Cache data
     */
    Map<AnalyzerCacheType, Object> cacheData = null;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WSearchAnalyzerCache.class);

    /**
     * Set the cache data.<br/>
     * 
     * @param cacheData
     *            Cache data
     */
    public void setCacheData(Map<AnalyzerCacheType, Object> cacheData) {
        this.cacheData = cacheData;
    }

    /**
     * The type that defines the type of the cache.<br/>
     */
    public enum AnalyzerCacheType {
        PACKAGE("wsearch.packagename.cache"), CLASSNAME(
                "wsearch.classname.cache"), IMPORT("wsearch.import.cache");

        /**
         * Cache file name of this cache type
         */
        private String filename;

        /**
         * Get the cache file name.<br/>
         * 
         * @return Cache file name
         */
        public String getFileName() {
            return filename;
        }

        /**
         * Constructor.<br/>
         * No operation.<br/>
         * 
         * @param filename
         *            Cache file name
         */
        private AnalyzerCacheType(String filename) {
            this.filename = filename;
        }
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param homePath
     *            Directory path to store the cache in general
     */
    public WSearchAnalyzerCache(String homePath) {
        super(homePath, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object load(WSearchAnalyzerCacheArgument loaderArgument)
            throws WSearchBizException {
        // Only load files are present
        if (exist(loaderArgument)) {
            StringBuffer cachedir = new StringBuffer();
            cachedir.append(loaderArgument.getPath());
            cachedir.append(File.separator);
            cachedir.append(loaderArgument.getType().getBaseDirName());
            cachedir.append(File.separator);
            cachedir.append(loaderArgument.getSearchTargetPath());
            cachedir.append(File.separator);
            String packageCachePath = cachedir.toString()
                    + AnalyzerCacheType.PACKAGE.getFileName();
            String classCachePath = cachedir.toString()
                    + AnalyzerCacheType.CLASSNAME.getFileName();
            String importCachePath = cachedir.toString()
                    + AnalyzerCacheType.IMPORT.getFileName();
            Map<String, String> pkgNampMap = null;
            List<String> clsNameList = null;
            List<String> importList = null;

            try {
                pkgNampMap = readPackageCacheFile(packageCachePath);
                clsNameList = readClassNameCacheFile(classCachePath);
                importList = readImportCacheFile(importCachePath);
            } catch (Exception e) {
                throw new WSearchBizException(
                        WSPhaseService.TYPE.SRC_PARSER_AND_COMPARE,
                        MessageUtil.getResourceString(this.getClass().getName()
                                + ".err.loadfailed"), e.getCause());
            }
            Map<AnalyzerCacheType, Object> cacheData = new HashMap<AnalyzerCacheType, Object>();
            cacheData.put(AnalyzerCacheType.PACKAGE, pkgNampMap);
            cacheData.put(AnalyzerCacheType.CLASSNAME, clsNameList);
            cacheData.put(AnalyzerCacheType.IMPORT, importList);
            return cacheData;

        } else {
            // If no file exists, the file creation
            delegate = loaderArgument.getProxy();
            return delegate.load(loaderArgument);
        }
    }

    /**
     * Read the cache file of import information.<br/>
     * 
     * @param importCachePath
     *            The path of the cache file
     * @return List of import information read
     * @throws Exception
     *             If it fails to load
     */
    private List<String> readImportCacheFile(String importCachePath)
            throws Exception {
        List<String> importCacheList = new ArrayList<String>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            LOGGER.debug(MessageUtil.getResourceString("debug.msg.read.file")
                    + importCachePath);
            fis = new FileInputStream(importCachePath);
            reader = new BufferedReader(new InputStreamReader(fis));
            String value = null;
            do {
                value = reader.readLine();
                if (value == null) {
                    break;
                }
                importCacheList.add(value);
            } while (true);
        } catch (Exception e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return importCacheList;
    }

    /**
     * Read the cache file for the class name.<br/>
     * 
     * @param classCachePath
     *            The path of the cache file
     * @return List of class names that you read
     * @throws Exception
     *             If it fails to load
     */
    private List<String> readClassNameCacheFile(String classCachePath)
            throws Exception {
        List<String> clsCacheList = new ArrayList<String>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            LOGGER.debug(MessageUtil.getResourceString("debug.msg.read.file")
                    + classCachePath);
            fis = new FileInputStream(classCachePath);
            reader = new BufferedReader(new InputStreamReader(fis));
            String value = null;
            do {
                value = reader.readLine();
                if (value == null) {
                    break;
                }
                clsCacheList.add(value);
            } while (true);
        } catch (Exception e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return clsCacheList;
    }

    /**
     * Read the cache file name of the package.<br/>
     * 
     * @param packageCachePath
     *            The path of the cache file
     * @return List of package names that read
     * @throws Exception
     *             If it fails to load
     */
    private Map<String, String> readPackageCacheFile(String packageCachePath)
            throws Exception {
        Map<String, String> pkgCacheMap = new HashMap<String, String>();

        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            LOGGER.debug(MessageUtil.getResourceString("debug.msg.read.file")
                    + packageCachePath);
            fis = new FileInputStream(packageCachePath);
            reader = new BufferedReader(new InputStreamReader(fis));
            String record = null;
            String key = null;
            String value = null;
            do {
                record = reader.readLine();
                if (record == null) {
                    break;
                }
                String[] strings = record.split(",");
                key = strings[0];
                value = strings[1];
                pkgCacheMap.put(key, value);
            } while (true);
        } catch (Exception e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return pkgCacheMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unload(WSearchAnalyzerCacheArgument loaderArgument) {
        this.cacheData = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(WSearchAnalyzerCacheArgument loaderArgument) {
        if (exist(loaderArgument)) {
            StringBuffer cachedir = new StringBuffer();
            cachedir.append(loaderArgument.getPath());
            cachedir.append(File.separator);
            cachedir.append(loaderArgument.getType().getBaseDirName());
            cachedir.append(File.separator);
            cachedir.append(loaderArgument.getSearchTargetPath());
            File file = new File(cachedir.toString());
            if (file.exists()) {
                try {
                    FileVisitor.walkFileTree(file, new FileVisitor() {
                        @Override
                        public FileVisitResult visitFile(File file)
                                throws IOException {
                            file.delete();
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(File dir)
                                throws IOException {
                            dir.delete();
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    LOGGER.error(
                            MessageUtil.getResourceString(this.getClass()
                                    .getName() + ".err.delete"), e);
                }
            }
        }
    }

    /**
     * Get the data cache.<br/>
     * 
     * @return Cache data
     */
    public Map<AnalyzerCacheType, Object> getCacheData() {
        return cacheData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exist(WSearchAnalyzerCacheArgument loaderArgument) {
        StringBuffer cachedir = new StringBuffer();
        cachedir.append(loaderArgument.getPath());
        cachedir.append(File.separator);
        cachedir.append(loaderArgument.getType().getBaseDirName());
        cachedir.append(File.separator);
        cachedir.append(loaderArgument.getSearchTargetPath());
        cachedir.append(File.separator);
        String packageCachePath = cachedir.toString()
                + AnalyzerCacheType.PACKAGE.getFileName();
        String classCachePath = cachedir.toString()
                + AnalyzerCacheType.CLASSNAME.getFileName();
        if (!fileExist(packageCachePath) | !fileExist(classCachePath)) {
            // It is assumed that it does not exist if it is not even only one
            // of these
            return false;
        }

        return true;
    }
}
