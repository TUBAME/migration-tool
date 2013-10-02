/*
 * ImportGetter.java
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
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.wsearch.biz.cache.WSearchAnalyzerCache.AnalyzerCacheType;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * It is a class that manages the import of the information package.<br/>
 */
public class ImportGetter {

    /**
     * Set of packages that are imported
     */
    private Set<String> imports;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ImportGetter.class);

    /**
     * Constructor.<br/>
     * No operation.<br/>
     */
    public ImportGetter() {
        super();
        imports = new TreeSet<String>();
    }

    /**
     * Get a set of packages that are imported.<br/>
     * 
     * @return Set of packages that are imported
     */
    public Set<String> getImports() {
        return imports;
    }

    /**
     * Write to the cache file information package being imported.<br/>
     * 
     * @param argument
     *            Argument class that contains the information of the cache
     *            write
     * @param packageNameMap
     *            Map of the package name
     * @throws Exception
     *             If an error occurs when writing to cache file
     */
    public void commit(WSearchAnalyzerCacheArgument argument,
            Map<String, String> packageNameMap) throws Exception {
        Set<String> compareSet = new HashSet<String>();

        List<String> delTargets = new ArrayList<String>();
        // Get the cache of the package name
        for (Map.Entry<String, String> entry : packageNameMap.entrySet()) {
            compareSet.add(entry.getValue());
        }

        StringBuffer cachedir = new StringBuffer();
        cachedir.append(argument.getPath());
        cachedir.append(File.separator);
        cachedir.append(argument.getType().getBaseDirName());
        cachedir.append(File.separator);
        cachedir.append(argument.getSearchTargetPath());
        cachedir.append(File.separator);
        String cacheFileContainDir = cachedir.toString();

        File file = new File(cacheFileContainDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String packageCachePath = cachedir.toString()
                + AnalyzerCacheType.IMPORT.getFileName();

        File cacheFile = new File(packageCachePath);

        FileOutputStream fos = null;
        PrintWriter writer = null;
        try {
            LOGGER.debug(MessageUtil.getResourceString("debug.msg.write.file")
                    + cacheFile);
            fos = new FileOutputStream(cacheFile);
            writer = new PrintWriter(fos);
            for (String value : imports) {
                if (!compareSet.contains(value)) {
                    writer.println(value);
                } else {
                    delTargets.add(value);
                }
            }
            writer.flush();
            fos.flush();
            for (String string : delTargets) {
                this.imports.remove(string);
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
}
