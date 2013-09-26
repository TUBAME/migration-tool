/*
 * WSearchLibraryCache.java
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.wsearch.biz.WSPhaseService;
import org.tubame.wsearch.biz.ex.WSearchBizException;
import org.tubame.wsearch.biz.model.LibraryModel;
import org.tubame.wsearch.biz.model.LibraryModel.TARGET_TYPE;
import org.tubame.wsearch.biz.util.FileVisitor;
import org.tubame.wsearch.biz.util.PomReader;
import org.tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * It is a class that will handle the cache of library information.<br/>
 */
public class WSearchLibraryCache extends CacheBase implements
        CacheProxy<WSearchLibraryCacheArgument> {

    /**
     * This is a class to store the information in the library.<br/>
     */
    public static class LibraryMetaData {
        /**
         * Model that contains basic information about the library
         */
        private LibraryModel model;
        /**
         * Set of POM file reader described the library information
         */
        private Set<PomReader> readerSet;

        /**
         * Map of packages that are in the library
         */
        private Map<String, PackageInfo> packages;
        /**
         * List of the XSD in the library
         */
        private Map<String, Set<String>> xsdList;
        /**
         * List of XML namespace in the library
         */
        private Map<String, Set<String>> namespaceList;
        /**
         * List of the DTD in the library
         */
        private Set<String> dtdList;
        /**
         * List of TLD files in the library
         */
        private Set<String> tldList;

        /**
         * Constructor.<br/>
         * No operation.<br/>
         * 
         * @param model
         *            Model that contains basic information about the library
         */
        private LibraryMetaData(LibraryModel model) {
            this.model = model;
            this.readerSet = new HashSet<PomReader>();
            this.packages = new HashMap<String, PackageInfo>();
            this.xsdList = new HashMap<String, Set<String>>();
            this.namespaceList = new HashMap<String, Set<String>>();
            this.dtdList = new HashSet<String>();
            this.tldList = new HashSet<String>();
        }

        /**
         * Get a model that contains basic information about the library.<br/>
         * 
         * @return Model that contains basic information about the library
         */
        public LibraryModel getModel() {
            return this.model;
        }

        /**
         * Get a map of the packages that are in the library.<br/>
         * 
         * @return Map of packages that are in the library
         */
        public Set<String> getPackages() {
            return this.packages.keySet();
        }

        /**
         * Get a set of classes that exist in the specified package.<br/>
         * 
         * @param packageName
         *            Package Name
         * @return Class set
         */
        public Set<String> getClasses(String packageName) {
            PackageInfo info = this.packages.get(packageName);
            if (info == null) {
                return new HashSet<String>();
            }
            return info.getClasses();
        }

        /**
         * Get a set of deprecated class in no package specified.<br/>
         * 
         * @param packageName
         *            Package Name
         * @return Deprecated class set
         */
        public Set<String> getDeprecated(String packageName) {
            PackageInfo info = this.packages.get(packageName);
            if (info == null) {
                return new HashSet<String>();
            }
            return info.getDeprecated();
        }

        /**
         * Get a list of the XSD in the library.<br/>
         * 
         * @return List of the XSD in the library
         */
        public Map<String, Set<String>> getXsdList() {
            return this.xsdList;
        }

        /**
         * Get a list of XML namespace in the library.<br/>
         * 
         * @return List of XML namespace in the library
         */
        public Map<String, Set<String>> getNamespaceList() {
            return this.namespaceList;
        }

        /**
         * Get a list of the DTD in the library.<br/>
         * 
         * @return List of the DTD in the library
         */
        public Set<String> getDtdList() {
            return this.dtdList;
        }

        /**
         * Get a list of TLD that exist in the library.<br/>
         * 
         * @return List of TLD files in the library
         */
        public Set<String> getTldList() {
            return this.tldList;
        }
    }

    /**
     * It is a class that contains the package information.<br/>
     */
    private static class PackageInfo {

        /**
         * Reader of POM file for which read the package information
         */
        private PomReader pomReader;

        /**
         * Package Name
         */
        private String name;
        /**
         * Set of classes that exist in the package
         */
        private Set<String> classes = null;
        /**
         * Set of deprecated class in the package
         */
        private Set<String> deprecated = null;

        /**
         * Constructor.<br/>
         * No operation.<br/>
         * 
         * @param pomReader
         *            Reader of POM file for which read the package information
         * @param name
         *            Package Name
         */
        public PackageInfo(PomReader pomReader, String name) {
            this.pomReader = pomReader;
            this.name = name;
        }

        /**
         * Get a set of class names that exist in the package.<br/>
         * The class information, it is not loaded when you create a class
         * PackageInfo, holds by reading from POM reader when the method is
         * called.<br/>
         * The second and subsequent calls, and returns the class name set held
         * in the first.<br/>
         * 
         * @return Set of class name
         */
        public Set<String> getClasses() {
            if (this.classes == null) {
                this.classes = pomReader.getClasses(this.name);
            }
            return this.classes;
        }

        /**
         * get a set of deprecated class name in the package.<br/>
         * The deprecated information, it is not loaded when you create a class
         * PackageInfo, holds by reading from POM reader when the method is
         * called.<br/>
         * The second and subsequent calls, and returns the deprecated class
         * name set held in the first.<br/>
         * 
         * @return Set of deprecated class name
         */
        public Set<String> getDeprecated() {
            if (this.deprecated == null) {
                this.deprecated = pomReader.getDeprecated(this.name);
            }
            return this.deprecated;
        }
    }

    /**
     * The type that defines the parts of the XSD file name.<br/>
     * Define version or part, the name or part.<br/>
     */
    public enum XSD_PARTS {
        NAME, VERSION;
    }

    /**
     * Map of the library
     */
    private Map<String, LibraryMetaData> libraryMap;

    /**
     * It is not treated as a version which do not conform to this pattern
     */
    private static Pattern versionPattern = Pattern.compile("^[0-9.]+$");

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WSearchLibraryCache.class);

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param homePath
     *            Directory path to store the cache in general
     */
    public WSearchLibraryCache(String homePath) {
        super(homePath, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object load(WSearchLibraryCacheArgument loaderArgument)
            throws WSearchBizException {
        if (this.libraryMap == null) {
            this.libraryMap = new HashMap<String, LibraryMetaData>();
        }
        LibraryModel library = loaderArgument.getLibrary();
        libraryMap.put(library.getLibName(), new LibraryMetaData(library));
        try {
            findAndLoadPoms(library, loaderArgument.getPath());
        } catch (IOException e) {
            throw new WSearchBizException(
                    WSPhaseService.TYPE.DESTLIB_METADATA_GENERATE,
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".err.pomfailed"), e);
        }
        loadPackage(library.getLibName(), library.getTargetType());
        loadXsd(library.getLibName());
        loadDtd(library.getLibName());
        loadTld(library.getLibName());
        return libraryMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unload(WSearchLibraryCacheArgument loaderArgument) {
        this.libraryMap = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(WSearchLibraryCacheArgument loaderArgument) {
        try {
            LibraryModel library = loaderArgument.getLibrary();
            File target = new File(loaderArgument.getPath(),
                    library.getLibName());
            FileVisitor.walkFileTree(target, new FileVisitor() {
                @Override
                public FileVisitResult visitFile(File file) throws IOException {
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
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".err.delete"), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exist(WSearchLibraryCacheArgument loaderArgument) {
        LibraryModel library = loaderArgument.getLibrary();
        File dir = new File(loaderArgument.getPath(), library.getLibName());
        if (!dir.exists()) {
            return false;
        }
        if (!new File(dir.toString(), "pom.xml").exists()) {
            return false;
        }
        return true;
    }

    /**
     * Get a map of the library.<br/>
     * 
     * @return Map of the library
     */
    public Map<String, LibraryMetaData> getLibraryMap() {
        return this.libraryMap;
    }

    /**
     * Read the POM file that exists as a cache for the specified library.<br/>
     * As a library information, search the POM multiple files in a directory
     * hierarchy in a particular directory under, you can read each.<br/>
     * 
     * @param library
     *            Model that contains basic information about the library
     * @param basePath
     *            Directory path to store the cache in general
     * @throws IOException
     *             If it fails to load the POM file
     */
    private void findAndLoadPoms(LibraryModel library, String basePath)
            throws IOException {
        final LibraryMetaData metaData = libraryMap.get(library.getLibName());
        File dir = new File(basePath, library.getLibName());
        if (dir.exists()) {
            FileVisitor.walkFileTree(dir, new FileVisitor() {
                @Override
                public FileVisitResult visitFile(File file) throws IOException {
                    String fileName = file.getName();
                    int dotIndex = fileName.lastIndexOf(".");
                    if (dotIndex >= 0) {
                        String extension = fileName.substring(dotIndex + 1);
                        if ("pom".equals(extension)) {
                            try {
                                PomReader reader = new PomReader();
                                reader.loadPomFile(file.toString());
                                metaData.readerSet.add(reader);
                            } catch (XmlPullParserException e) {
                                throw new IOException(e);
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * Read the package list information from the POM file.<br/>
     * 
     * @param libraryName
     *            Library name
     * @param type
     *            Type of whether porting library or transplantation based
     *            library
     * @throws WSearchBizException
     *             If an error occurs when reading information
     */
    private void loadPackage(String libraryName, TARGET_TYPE type)
            throws WSearchBizException {
        LOGGER.debug(MessageUtil.getResourceString(WSearchLibraryCache.class
                .getName() + ".debug.loadpackage.library")
                + libraryName);

        LibraryMetaData metaData = this.libraryMap.get(libraryName);
        metaData.packages = new HashMap<String, PackageInfo>();

        List<String> cachedPackages = WSearchCacheManager.getInstance()
                .getImportList();
        for (PomReader reader : metaData.readerSet) {
            for (String packageName : reader.getPackages()) {
                LOGGER.debug(MessageUtil
                        .getResourceString(WSearchLibraryCache.class.getName()
                                + ".debug.loadpackage.package")
                        + packageName);
                PackageInfo packageInfo = new PackageInfo(reader, packageName);
                metaData.packages.put(packageName, packageInfo);
                if (cachedPackages != null) {
                    for (String cached : cachedPackages) {
                        if (cached.endsWith(".")) {
                            cached = cached.substring(0, (cached.length() - 1));
                        }
                        if ("java.lang".equals(packageName)
                                || cached.equals(packageName)) {
                            packageInfo.getClasses();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Reads XSD list information, the XML namespace list information from the
     * POM file.<br/>
     * Holds by separating the version information when reading.<br/>
     * 
     * @param libraryName
     *            Library name
     */
    private void loadXsd(String libraryName) {
        LOGGER.debug(MessageUtil.getResourceString(WSearchLibraryCache.class
                .getName() + ".debug.loadxsd.library")
                + libraryName);

        LibraryMetaData metaData = this.libraryMap.get(libraryName);
        metaData.xsdList = new HashMap<String, Set<String>>();
        metaData.namespaceList = new HashMap<String, Set<String>>();
        for (PomReader reader : metaData.readerSet) {
            for (String fileName : reader.getXsdList().keySet()) {
                String namespace = reader.getXsdList().get(fileName);
                if (namespace != null) {
                    Map<XSD_PARTS, String> namespaceMap = getNamespaceVersion(namespace);
                    LOGGER.debug(MessageUtil
                            .getResourceString(WSearchLibraryCache.class
                                    .getName() + ".debug.loadxsd.namespace")
                            + namespaceMap.get(XSD_PARTS.NAME));
                    LOGGER.debug(MessageUtil
                            .getResourceString(WSearchLibraryCache.class
                                    .getName()
                                    + ".debug.loadxsd.namespaceversion")
                            + namespaceMap.get(XSD_PARTS.VERSION));
                    if (namespaceMap.get(XSD_PARTS.VERSION) == null) {
                        if (!metaData.namespaceList.containsKey(namespaceMap
                                .get(XSD_PARTS.NAME))) {
                            metaData.namespaceList.put(
                                    namespaceMap.get(XSD_PARTS.NAME),
                                    new HashSet<String>());
                        }
                    } else {
                        if (metaData.namespaceList.containsKey(namespaceMap
                                .get(XSD_PARTS.NAME))) {
                            metaData.namespaceList.get(
                                    namespaceMap.get(XSD_PARTS.NAME)).add(
                                    namespaceMap.get(XSD_PARTS.VERSION));
                        } else {
                            Set<String> versionSet = new HashSet<String>();
                            versionSet.add(namespaceMap.get(XSD_PARTS.VERSION));
                            metaData.namespaceList.put(
                                    namespaceMap.get(XSD_PARTS.NAME),
                                    versionSet);
                        }
                    }
                }

                Map<XSD_PARTS, String> versionMap = getVersion(fileName);
                LOGGER.debug(MessageUtil
                        .getResourceString(WSearchLibraryCache.class.getName()
                                + ".debug.loadxsd.xsd")
                        + versionMap.get(XSD_PARTS.NAME));
                LOGGER.debug(MessageUtil
                        .getResourceString(WSearchLibraryCache.class.getName()
                                + ".debug.loadxsd.xsdversion")
                        + versionMap.get(XSD_PARTS.VERSION));
                if (versionMap.get(XSD_PARTS.VERSION) == null) {
                    if (!metaData.xsdList.containsKey(versionMap
                            .get(XSD_PARTS.NAME))) {
                        metaData.xsdList.put(versionMap.get(XSD_PARTS.NAME),
                                new HashSet<String>());
                    }
                } else {
                    if (metaData.xsdList.containsKey(versionMap
                            .get(XSD_PARTS.NAME))) {
                        metaData.xsdList.get(versionMap.get(XSD_PARTS.NAME))
                                .add(versionMap.get(XSD_PARTS.VERSION));
                    } else {
                        Set<String> versionSet = new HashSet<String>();
                        versionSet.add(versionMap.get(XSD_PARTS.VERSION));
                        metaData.xsdList.put(versionMap.get(XSD_PARTS.NAME),
                                versionSet);
                    }
                }
            }
        }
    }

    /**
     * Read a DTD list information from the POM file.<br/>
     * 
     * @param libraryName
     *            Library name
     */
    private void loadDtd(String libraryName) {
        LibraryMetaData metaData = this.libraryMap.get(libraryName);
        metaData.dtdList = new HashSet<String>();
        for (PomReader reader : metaData.readerSet) {
            metaData.dtdList.addAll(reader.getDtdList());
        }
    }

    /**
     * Read the TLD list information from the POM file.<br/>
     * 
     * @param libraryName
     *            Library name
     */
    private void loadTld(String libraryName) {
        LibraryMetaData metaData = this.libraryMap.get(libraryName);
        metaData.tldList = new HashSet<String>();
        for (PomReader reader : metaData.readerSet) {
            metaData.tldList.addAll(reader.getTldList());
        }
    }

    /**
     * Split the version information from the file name.<br/>
     * 
     * @param fileName
     *            File name
     * @return Map that contains the version name, the file name without the
     *         version part
     */
    public static Map<XSD_PARTS, String> getVersion(String fileName) {
        Map<XSD_PARTS, String> map = new HashMap<XSD_PARTS, String>();
        String noExtension = fileName.substring(0, (fileName.length() - 4));
        int lastHyphen = noExtension.lastIndexOf("-");
        int index = noExtension.lastIndexOf("_");
        if (index < lastHyphen) {
            index = lastHyphen;
        }
        if (index > 0) {
            String version = noExtension.substring(index + 1);
            if (versionPattern.matcher(version).matches()) {
                String xsdName = noExtension.substring(0, index);
                map.put(XSD_PARTS.NAME, xsdName);
                map.put(XSD_PARTS.VERSION, version);
                return map;
            }
        }
        map.put(XSD_PARTS.NAME, noExtension);
        map.put(XSD_PARTS.VERSION, null);
        return map;
    }

    /**
     * Split the version information from the XML namespace name.<br/>
     * 
     * @param namespace
     *            Namespace name
     * @return Map that contains the version name, the namespace name without
     *         the version part
     */
    public static Map<XSD_PARTS, String> getNamespaceVersion(String namespace) {
        Map<XSD_PARTS, String> map = new HashMap<XSD_PARTS, String>();
        int index = namespace.lastIndexOf(":");
        if (index > 0) {
            String version = namespace.substring(index + 1);
            if (versionPattern.matcher(version).matches()) {
                String xsdName = namespace.substring(0, index);
                map.put(XSD_PARTS.NAME, xsdName);
                map.put(XSD_PARTS.VERSION, version);
                return map;
            }
        }
        map.put(XSD_PARTS.NAME, namespace);
        map.put(XSD_PARTS.VERSION, null);
        return map;
    }
}
