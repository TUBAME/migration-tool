/*
 * CacheProxy.java
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

import tubame.wsearch.biz.ex.WSearchBizException;

/**
 * The interface the underlying class that reads the cache, to provide operation
 * and destruction.<br/>
 * To match the type of cache, you can use it to create an implementation of
 * this interface.<br/>
 */
public interface CacheProxy<T extends BasicCacheLoadArgument> {

    /**
     * Load the cache from the files, etc. that is specified by the argument.<br/>
     * 
     * @param loaderArgument
     *            Argument class that contains the information of the cache load
     * @return Cache information loaded
     * @throws WSearchBizException
     *             If it fails to load the cache
     */
    Object load(T loaderArgument) throws WSearchBizException;

    /**
     * Discard the cache information loaded.<br/>
     * 
     * @param loaderArgument
     *            Argument class that contains the cache information of the
     *            target to be destroyed
     */
    void unload(T loaderArgument);

    /**
     * Delete the file that stores the cache information.<br/>
     * 
     * @param loaderArgument
     *            Argument class that contains the cache information from which
     *            to remove
     */
    void delete(T loaderArgument);

    /**
     * Determine file that stores the cache information specified exists.
     * 
     * @param loaderArgument
     *            Argument class that contains the cache information that is to
     *            be tested to see whether there
     * @return True if cache file exists
     */
    boolean exist(T loaderArgument);
}
