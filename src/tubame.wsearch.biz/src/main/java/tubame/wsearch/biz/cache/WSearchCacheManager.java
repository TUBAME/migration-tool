/*
 * WSearchCacheManager.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tubame.wsearch.biz.cache.WSearchAnalyzerCache.AnalyzerCacheType;
import tubame.wsearch.biz.ex.WSearchBizException;

/**
 * This is the class that provides the functions related to the cache to be used
 * in general-purpose search.<br/>
 * It provides features and destroy cache loading, such as call data.<br/>
 */
public class WSearchCacheManager {

    /**
     * Map that holds the cache information
     */
    private Map<CacheBase.TYPE, CacheBase> cacheMap;

    /**
     * Constructor.<br/>
     * In order to use the singleton pattern, the constructor and private.<br/>
     */
    private WSearchCacheManager() {
        cacheMap = new HashMap<CacheBase.TYPE, CacheBase>();
    }

    /**
     * This is a class to hold the substance of WSearchCacheManager class.<br/>
     */
    private static class WSearchCacheManagerHolder {
        /**
         * Entity WSearchCacheManager class
         */
        private static final WSearchCacheManager instance = new WSearchCacheManager();
    }

    /**
     * Get an instance of this class.<br/>
     * 
     * @return Instance of class WSearchCacheManager
     */
    public static WSearchCacheManager getInstance() {
        return WSearchCacheManagerHolder.instance;
    }

    /**
     * Load the cache information specified.<br/>
     * 
     * @param type
     *            Type of cache
     * @param cacheLoadArgument
     *            Argument class that contains the information of the cache load
     * @throws WSearchBizException
     *             If it fails to load the cache
     */
    @SuppressWarnings("unchecked")
    public synchronized void load(CacheBase.TYPE type,
            BasicCacheLoadArgument cacheLoadArgument)
            throws WSearchBizException {
        if (type == CacheBase.TYPE.ANALYZE) {
            WSearchAnalyzerCacheArgument argument = (WSearchAnalyzerCacheArgument) cacheLoadArgument;
            WSearchAnalyzerCache analyzerCache = new WSearchAnalyzerCache(
                    argument.getPath());
            Map<AnalyzerCacheType, Object> cacheMap = (Map<AnalyzerCacheType, Object>) analyzerCache
                    .load(argument);
            analyzerCache.setCacheData(cacheMap);
            this.cacheMap.put(CacheBase.TYPE.ANALYZE, analyzerCache);
        } else if (type == CacheBase.TYPE.LIBRARY) {
            WSearchLibraryCacheArgument argument = (WSearchLibraryCacheArgument) cacheLoadArgument;
            WSearchLibraryCache libraryCache = (WSearchLibraryCache) this.cacheMap
                    .get(type);
            if (libraryCache == null) {
                libraryCache = new WSearchLibraryCache(argument.getPath());
            }
            libraryCache.load(argument);
            this.cacheMap.put(CacheBase.TYPE.LIBRARY, libraryCache);
        }
    }

    /**
     * Destroy the cache information specified type.<br/>
     * 
     * @param type
     *            Type of cache to be destroyed
     */
    public synchronized void unload(CacheBase.TYPE type) {
        if (cacheMap.containsKey(type)) {
            cacheMap.put(type, null);
        }
    }

    /**
     * Get the cache of information loaded.<br/>
     * Cache information of the target or was not loaded, and returns null if
     * you are abandoned.<br/>
     * 
     * @param type
     *            Type of cache that are obtained
     * @return Cache data
     */
    public CacheBase getCache(CacheBase.TYPE type) {
        if (cacheMap.containsKey(type)) {
            return cacheMap.get(type);
        }
        return null;

    }

    /**
     * Get a map of the package name from the cache information Migrated.<br/>
     * 
     * @return Map of the package name
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getPackageNameMap() {
        WSearchAnalyzerCache cache = (WSearchAnalyzerCache) getCache(CacheBase.TYPE.ANALYZE);
        if (cache != null) {
            return (Map<String, String>) cache.getCacheData().get(
                    AnalyzerCacheType.PACKAGE);
        }
        return null;
    }

    /**
     * Get a list of class names from the cache information Migrated.<br/>
     * 
     * @return List of class names
     */
    @SuppressWarnings("unchecked")
    public List<String> getClassNameList() {
        WSearchAnalyzerCache cache = (WSearchAnalyzerCache) getCache(CacheBase.TYPE.ANALYZE);
        if (cache != null) {
            return (List<String>) cache.getCacheData().get(
                    AnalyzerCacheType.CLASSNAME);
        }
        return null;
    }

    /**
     * Get a list of import packages from the cache information Migrated.<br/>
     * 
     * @return List of import package
     */
    @SuppressWarnings("unchecked")
    public List<String> getImportList() {
        WSearchAnalyzerCache cache = (WSearchAnalyzerCache) getCache(CacheBase.TYPE.ANALYZE);
        if (cache != null) {
            return (List<String>) cache.getCacheData().get(
                    AnalyzerCacheType.IMPORT);
        }
        return null;
    }

    /**
     * Determine the cache file information specified exists.<br/>
     * 
     * @param argument
     *            Argument class that contains the cache information to find out
     *            whether there
     * @param type
     *            Cache type
     * @return True if cache file exists
     */
    public boolean exist(BasicCacheLoadArgument argument, CacheBase.TYPE type) {
        if (type == CacheBase.TYPE.ANALYZE) {
            WSearchAnalyzerCache cache = (WSearchAnalyzerCache) getCache(type);
            if (cache == null) {
                cache = new WSearchAnalyzerCache(argument.getPath());
            }
            return cache.exist((WSearchAnalyzerCacheArgument) argument);
        } else if (type == CacheBase.TYPE.LIBRARY) {
            WSearchLibraryCache cache = (WSearchLibraryCache) getCache(type);
            if (cache == null) {
                cache = new WSearchLibraryCache(argument.getPath());
            }
            return cache.exist((WSearchLibraryCacheArgument) argument);
        }
        return false;
    }
}
