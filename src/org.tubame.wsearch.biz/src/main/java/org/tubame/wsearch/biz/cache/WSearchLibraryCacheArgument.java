/*
 * WSearchLibraryCacheArgument.java
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
package org.tubame.wsearch.biz.cache;

import org.tubame.wsearch.biz.cache.CacheBase.TYPE;
import org.tubame.wsearch.biz.model.LibraryModel;

/**
 * This is a class to maintain the value to use when loading the cache of
 * library information.<br/>
 */
public class WSearchLibraryCacheArgument extends BasicCacheLoadArgument {

    /**
     * Model that contains basic information about the library
     */
    private LibraryModel library;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param type
     *            Cache type
     * @param path
     *            Directory path to store the cache in general
     * @param library
     *            Model that contains basic information about the library
     */
    public WSearchLibraryCacheArgument(TYPE type, String path,
            LibraryModel library) {
        super(type, path);
        this.library = library;
    }

    /**
     * Get a model that contains basic information about the library.<br/>
     * 
     * @return Model that contains basic information about the library
     */
    public LibraryModel getLibrary() {
        return this.library;
    }
}
