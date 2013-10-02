/*
 * BasicCacheLoadArgument.java
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

import tubame.wsearch.biz.cache.CacheBase.TYPE;

/**
 * It is the base class of the class that contains the value to be passed to the
 * load when the cache.<br/>
 * You can use it to create a subclass of this class for the type of cache, to
 * store the value to be passed.<br/>
 */
public class BasicCacheLoadArgument {

    /**
     * Type of cache
     */
    CacheBase.TYPE type;

    /**
     * Cache is arranged to pass (deployed)
     */
    String path;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param type
     *            Type of cache
     * @param path
     *            Cache is arranged to pass (deployed)
     */
    public BasicCacheLoadArgument(TYPE type, String path) {
        super();
        this.type = type;
        this.path = path;
    }

    /**
     * Get the type of the cache.<br/>
     * 
     * @return Type of cache
     */
    public CacheBase.TYPE getType() {
        return type;
    }

    /**
     * Get cache is located a path (deployed).<br/>
     * 
     * @return Cache is arranged to pass (deployed)
     */
    public String getPath() {
        return path;
    }
}
