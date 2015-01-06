/*
 * AnalyzerCacheLoaderDelegate.java
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
package tubame.wsearch.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.wsearch.Activator;
import tubame.wsearch.biz.WSPhaseService;
import tubame.wsearch.biz.cache.CacheProxy;
import tubame.wsearch.biz.cache.ImportGetter;
import tubame.wsearch.biz.cache.WSearchAnalyzerCacheArgument;
import tubame.wsearch.biz.cache.WSearchAnalyzerCache.AnalyzerCacheType;
import tubame.wsearch.biz.ex.WSearchBizException;

/**
 * Load cache to cache objects to create and analyze the cache file processing.<br/>
 * In the case of the need to create a cache file of analyze processing, load
 * method is called.<br/>
 */
public class AnalyzerCacheLoaderDelegate implements
        CacheProxy<WSearchAnalyzerCacheArgument> {

    /**
     * Package cache analyze processing
     */
    private Map<String, String> pkgNameCache;

    /**
     * Class name of the cache analyze processing
     */
    private List<String> clsNameCache;

    /**
     * Import getter analyze processing
     */
    private ImportGetter importGetter;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AnalyzerCacheLoaderDelegate.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object load(WSearchAnalyzerCacheArgument loaderArgument)
            throws WSearchBizException {
        // Create a cache file and return the cache object.

        String root = ResourcesPlugin.getWorkspace().getRoot().getLocation()
                .toOSString();
        String target = root + File.separator
                + loaderArgument.getSearchTargetPath();

        this.pkgNameCache = new HashMap<String, String>();
        this.clsNameCache = new ArrayList<String>();

        // Output to file
        createCacheData((new Path(loaderArgument.getSearchTargetProjectPath())));

        // File output
        try {
            commit(loaderArgument);
            // Get the package that is import of business non-AP by using the
            // pkgNameCache
            this.importGetter.commit(loaderArgument, pkgNameCache);
        } catch (Exception e) {
            throw new WSearchBizException(
                    WSPhaseService.TYPE.SRC_PARSER_AND_COMPARE,
                    Activator
                            .getResourceString(AnalyzerCacheLoaderDelegate.class
                                    .getName() + ".err.msg.CacheGenerateErr"),
                    e.getCause());
        }

        Map<AnalyzerCacheType, Object> cacheData = new HashMap<AnalyzerCacheType, Object>();
        cacheData.put(AnalyzerCacheType.PACKAGE, this.pkgNameCache);
        cacheData.put(AnalyzerCacheType.CLASSNAME, this.clsNameCache);
        String[] imports = this.importGetter.getImports()
                .toArray(new String[0]);
        cacheData.put(AnalyzerCacheType.IMPORT, Arrays.asList(imports));

        return cacheData;
    }

    /**
     * The returns ImportGetter.<br/>
     * ImportGetter is a class that manages the information that you import the
     * application class. <br/>
     * Load from library search destination only packages that <br/>
     * belong to this import list in the initial process of taking porting
     * information.<br/>
     */
    public ImportGetter getImportGetter() {
        return importGetter;
    }

    /**
     * Default constructor.
     */
    public AnalyzerCacheLoaderDelegate() {
        super();
        importGetter = new ImportGetter();
    }

    /**
     * Create a cache file from the cache and data cache analyze argument class.<br/>
     * 
     * @param loaderArgument
     *            Analyze cache argument class
     * @throws Exception
     *             Exception
     */
    private void commit(WSearchAnalyzerCacheArgument loaderArgument)
            throws Exception {
        StringBuffer cachedir = new StringBuffer();
        cachedir.append(loaderArgument.getPath());
        cachedir.append(File.separator);
        cachedir.append(loaderArgument.getType().getBaseDirName());
        cachedir.append(File.separator);
        cachedir.append(loaderArgument.getSearchTargetPath());
        cachedir.append(File.separator);
        String cacheFileContainDir = cachedir.toString();

        File file = new File(cacheFileContainDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String packageCachePath = cachedir.toString()
                + AnalyzerCacheType.PACKAGE.getFileName();

        File cacheFile = new File(packageCachePath);

        FileOutputStream fos = null;
        PrintWriter writer = null;
        try {
            LOGGER.debug(Activator.getResourceString("debug.msg.write.file")
                    + cacheFile);
            fos = new FileOutputStream(cacheFile);
            writer = new PrintWriter(fos);
            Set<String> keySet = pkgNameCache.keySet();
            for (Iterator<String> iterator = keySet.iterator(); iterator
                    .hasNext();) {
                String key = iterator.next();
                String value = pkgNameCache.get(key);
                writer.println(key + "," + value);
            }
            writer.flush();
            fos.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (fos != null) {
                fos.close();
            }
        }

        String classNameCachePath = cachedir.toString()
                + AnalyzerCacheType.CLASSNAME.getFileName();
        cacheFile = new File(classNameCachePath);
        try {
            LOGGER.debug(Activator.getResourceString("debug.msg.write.file")
                    + cacheFile);
            fos = new FileOutputStream(cacheFile);
            writer = new PrintWriter(fos);
            for (String string : clsNameCache) {
                writer.println(string);
            }
            writer.flush();
            fos.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (fos != null) {
                fos.close();
            }
        }

    }

    /**
     * Cache from path information Analyze the specified target.<br/>
     * 
     * @param path
     *            Analyze path of the specified target
     * @throws WSearchBizException
     *             Exception
     */
    private void createCacheData(Path path) throws WSearchBizException {

        final File file = path.toFile();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                try {
                    createCacheData(new Path(child.getCanonicalPath()));
                } catch (IOException e) {
                    throw new WSearchBizException(
                            WSPhaseService.TYPE.SRC_PARSER_AND_COMPARE,
                            Activator
                                    .getResourceString(AnalyzerCacheLoaderDelegate.class
                                            .getName()
                                            + ".err.msg.CacheGenerateErr"),
                            e.getCause());
                }
            }
        } else {
            String packageName = null;
            String fullname = null;

            try {
                IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
                        .getFileForLocation(path);
                if ((iFile != null)
                        && ("java".equals(iFile.getFileExtension()))) {
                    final ICompilationUnit src = JavaCore
                            .createCompilationUnitFrom(iFile);
                    if (src.isStructureKnown()) {
                        for (IPackageDeclaration pDec : src
                                .getPackageDeclarations()) {
                            packageName = pDec.getElementName();
                            for (IType type : src.getAllTypes()) {
                                fullname = packageName + "."
                                        + type.getTypeQualifiedName();
                                if (!clsNameCache.contains(fullname)) {
                                    clsNameCache.add(fullname);
                                }
                            }
                            final String key = path.toString();
                            if (!pkgNameCache.containsKey(key)) {
                                pkgNameCache.put(key, packageName + ".");
                            }
                            // Adopt the name of the package that you obtained
                            // at the beginning
                            break;
                        }
                        IImportDeclaration[] imports = src.getImports();
                        for (IImportDeclaration declare : imports) {
                            String importName = declare.getElementName();
                            String importPackage = getPackageName(importName);
                            if (importPackage != null) {
                                this.importGetter.getImports().add(
                                        importPackage);
                            }
                        }
                    }
                }

            } catch (JavaModelException e) {
                // If it fails to interpret the Java file, processing continues
                // to only log
                Activator.log(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unload(WSearchAnalyzerCacheArgument loaderArgument) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(WSearchAnalyzerCacheArgument loaderArgument) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exist(WSearchAnalyzerCacheArgument loaderArgument) {
        return false;
    }

    /**
     * From the class string of nested, and extract the package name.<br/>
     * The return null if the package name is not found.<br/>
     * <br/>
     * Case1<br/>
     * Specification string: sample.AClass.BClass.CClass.TEST<br/>
     * Return string: sample<br/>
     * <br/>
     * Case 2<br/>
     * Specification string: sample.a.b.c.d<br/>
     * Return string: null<br/>
     * <br/>
     * Case 3<br/>
     * Specification string: sample.a.b.c.d.TEST<br/>
     * Return string: sample.a.b.c.d<br/>
     * <br/>
     * 
     * @param className
     *            Class Specification string
     * @return Package Name
     * @see #isLocalClassName(String)
     */
    private String getPackageName(final String className) {
        String old = null;
        String current = className;
        if (className != null && !className.endsWith(".")) {
            for (int i = current.lastIndexOf('.'); i > 0; i = current
                    .lastIndexOf('.')) {
                old = current;
                current = current.substring(0, i);
                // Can not be detected properly in the case of non-package names
                // in all lowercase
                if (current.equals(current.toLowerCase(Locale.getDefault()))
                        && isLocalClassName(old.substring(i + 1, old.length()))) {
                    return current;
                }
            }
        }
        return null;
    }

    /**
     * Check the specified string to see if it matches the rules of the class
     * name.<br/>
     * Check on the assumption that does not include the package name.<br/>
     * 
     * @param className
     *            String to check
     * @return True if the specified string matches the class name
     */
    private boolean isLocalClassName(final String className) {
        return className.length() > 2
                && className.matches("[A-Z][A-Z,a-z,0-9,_,$]*");
    }
}
