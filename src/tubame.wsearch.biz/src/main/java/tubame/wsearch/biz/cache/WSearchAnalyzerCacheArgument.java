/*
 * WSearchAnalyzerCacheArgument.java
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

/**
 * This is a class to maintain the value to use when loading the cache of the
 * analysis result information Migrated.<br/>
 */
public class WSearchAnalyzerCacheArgument extends BasicCacheLoadArgument {

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param type
     *            Cache type
     * @param path
     *            Directory path to store the cache in general
     * @param searchTargetPath
     *            Path of cache file (relative path)
     * @param proxy
     *            A proxy that read of cache, implementing an operation for of
     *            and destruction
     */
    public WSearchAnalyzerCacheArgument(CacheBase.TYPE type, String path,
            String searchTargetPath,
            CacheProxy<WSearchAnalyzerCacheArgument> proxy) {
        super(type, path);
        this.searchTargetPath = searchTargetPath;
        this.proxy = proxy;
    }

    /**
     * Path of cache file (relative path)
     */
    private String searchTargetPath;

    /**
     * A proxy that read of cache, implementing an operation for of and
     * destruction
     */
    private CacheProxy<WSearchAnalyzerCacheArgument> proxy;

    /**
     * Get path to the cache file (relative path).<br/>
     * 
     * @return Path of cache file (relative path)
     */
    public String getSearchTargetPath() {
        return searchTargetPath;
    }

    /**
     * Get a proxy to read the cache, to implement the operation and
     * destruction.<br/>
     * 
     * @return A proxy that read of cache, implementing an operation for of and
     *         destruction
     */
    public CacheProxy<WSearchAnalyzerCacheArgument> getProxy() {
        return proxy;
    }
}
