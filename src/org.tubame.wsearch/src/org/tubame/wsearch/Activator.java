/*
 * Activator.java
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
package org.tubame.wsearch;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import org.tubame.common.logging.CmnJbmToolsLoggingUtil;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.wsearch.biz.cache.CacheBase;
import org.tubame.wsearch.biz.cache.WSearchLibraryCache;
import org.tubame.wsearch.biz.cache.WSearchLibraryCacheArgument;
import org.tubame.wsearch.biz.model.Extension;
import org.tubame.wsearch.biz.model.LibraryModel;
import org.tubame.wsearch.biz.model.SearchFilter;
import org.tubame.wsearch.biz.model.WSPackage;
import org.tubame.wsearch.biz.util.FileVisitor;
import org.tubame.wsearch.biz.util.PomReader;
import org.tubame.wsearch.biz.util.ZipUtil;
import org.tubame.wsearch.biz.util.resource.MessageUtil;
import org.tubame.wsearch.biz.util.resource.ResourceUtil;
import org.tubame.wsearch.logics.WSearchBizDomain.WorkSpaceIResourceChangeListener;
import org.tubame.wsearch.models.LibraryRepository;
import org.tubame.wsearch.ui.dialog.ErrorDialog;

/**
 * Management of the life cycle of the Plugin.<br/>
 * It is called initialization and run, at the timing of the end of the Plugin.<br/>
 * 
 * @author Copyright (C) 2013 Nippon Telegraph and Telephone Corporation,All
 *         rights reserved.
 */
public class Activator extends AbstractUIPlugin {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "org.tubame.wsearch"; //$NON-NLS-1$

    /**
     * The shared instance
     */
    private static Activator plugin;

    /**
     * Library group of the setting screen for
     */
    private static Map<String, LibraryRepository> libraryRepositories;

    /**
     * Search filter group of the setting screen for (Migrated filter)
     */
    private static Set<SearchFilter> searchFilters;

    /**
     * Search filter group of the setting screen for (porting filter)
     */
    private static Set<SearchFilter> destSearchFilters;

    /**
     * Persistence key for Plugin setting for Libraries
     */
    private static final String LIBRARY_REPOSITORIES = "libraryRepositories";
    /**
     * Key name:libraryRepository
     */
    private static final String LIBRARY_REPOSITORY = "libraryRepository";
    /**
     * Key name:name
     */
    private static final String NAME = "name";
    /**
     * Key name:url
     */
    private static final String URL = "url";
    /**
     * Key name:description
     */
    private static final String DESCRIPTION = "description";
    /**
     * Key name:enabled
     */
    private static final String ENABLED = "enabled";
    /**
     * Key name:mavenType
     */
    private static final String MAVEN_TYPE = "mavenType";
    /**
     * Key name:pluginEmbedded
     */
    private static final String PLUGIN_EMBEDDED = "pluginEmbedded";
    /**
     * Key name:cached
     */
    private static final String CACHED = "cached";
    /**
     * Key name:cacheUse
     */
    private static final String CACHE_USE = "cacheUse";
    /**
     * Key name:order
     */
    private static final String ORDER = "order";
    /**
     * Key name:zipCache
     */
    private static final String ZIP_CACHE = "zipCache";
    /**
     * And whether or not to make use of the library cache
     */
    private static boolean cacheUse = true;

    /**
     * Persistence key for Plugin Settings (Search filter)
     */
    private static final String SRC_SEARCH_FILTERS = "srcSearchFilters";
    /**
     * Key name:srcSearchFilter
     */
    private static final String SRC_SEARCH_FILTER = "srcSearchFilter";
    /**
     * Key name:destSearchFilters
     */
    private static final String DEST_SEARCH_FILTERS = "destSearchFilters";
    /**
     * Key name:destSearchFilter
     */
    private static final String DEST_SEARCH_FILTER = "destSearchFilter";
    /**
     * Key name:filterExtentionTargets
     */
    private static final String FILTER_EXTENTION_TARGETS = "filterExtentionTargets";
    /**
     * Key name:extention
     */
    private static final String EXTENTION_TARGET = "extention";
    /**
     * Key name:filterPackageTargets
     */
    private static final String FILTER_PACKAGE_TARGETS = "filterPackageTargets";
    /**
     * Key name:package
     */
    private static final String PACKAGE_TARGET = "package";
    /**
     * Key name:parent
     */
    private static final String FILTER_PARENT = "parent";
    /**
     * Key name:child_ref
     */
    private static final String FILTER_CHILD_REF = "child_ref";
    /**
     * Key name:user_defined
     */
    private static final String FILTER_USER_DEFINED = "user_defined";

    /**
     * Directory that contains the default cache
     */
    protected static final String CACHE_DIRECTORY = "/resources/defaultCacheZips";

    /**
     * Resource bundle for message internationalization
     */
    private ResourceBundle resourceBundle;

    /**
     * Listener to monitor the change of the workspace
     */
    private static WorkSpaceIResourceChangeListener workspaceChangeListener;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Activator.class);

    /**
     * Constructor.
     */
    public Activator() {
    }

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        try {
            this.resourceBundle = PropertyResourceBundle
                    .getBundle("resources.properties.message");
        } catch (MissingResourceException x) {
            this.resourceBundle = null;
        }
        CmnJbmToolsLoggingUtil.configureLoggerForPlugin(PLUGIN_ID, Activator
                .getDefault().getStateLocation().toFile(),
                getResources("/logback_template.xml"));

        LOGGER.debug(getResourceString("debug.msg.start") + "Activator#start");

        MessageUtil
                .setBundle(getResources(
                        org.tubame.wsearch.util.resource.ResourceUtil.MESSAGE_PROPERTIES_PATH)
                        .openStream());
        ResourceUtil
                .setBundle(getResources(
                        org.tubame.wsearch.util.resource.ResourceUtil.RESOURCE_PROPERTIES_PATH)
                        .openStream());

        getLibraryRepositories();
        extractEmededCache();
        // Delete cache.temp under
        deleteCacheTemp();

        ResourcesPlugin.getWorkspace().addResourceChangeListener(
                new IResourceChangeListener() {

                    @Override
                    public void resourceChanged(IResourceChangeEvent event) {
                        try {

                            event.getDelta().accept(
                                    new IResourceDeltaVisitor() {

                                        public boolean visit(
                                                IResourceDelta delta) {
                                            return false;
                                        }
                                    });
                        } catch (CoreException e) {
                            ErrorDialog.openErrorDialog(
                                    getActiveWorkbenchShell(), e,
                                    e.getMessage());
                        }

                    }
                });

        LOGGER.debug(getResourceString("debug.msg.end") + "Activator#start");
    }

    /**
     * Delete all the resources of cache create temporary directory.<br/>
     * 
     * @throws IOException
     *             Input and output exception
     * 
     */
    private void deleteCacheTemp() {
        LOGGER.debug(getResourceString(Activator.class.getName()
                + ".debug.temp.delete"));
        String stateDir = Activator.getDefault().getStateLocation()
                .toOSString();
        File tempDir = new File(stateDir, "library.temp");
        if (tempDir.exists()) {
            return;
        }
        try {
            FileVisitor.walkFileTree(tempDir, new FileVisitor() {
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
            LOGGER.warn(getResourceString(Activator.class.getName()
                    + ".warn.temp.delete"), e);
        }
    }

    /**
     * Remove the cache of embedded plug-in.<br/>
     * 
     * @throws Exception
     *             It is Throw if it fails to removal of cache data
     */
    private void extractEmededCache() throws Exception {
        Bundle pluginBundle = Activator.getDefault().getBundle();
        Enumeration<String> entryPaths = pluginBundle
                .getEntryPaths(CACHE_DIRECTORY);

        if (entryPaths != null) {
            String stateDir = Activator.getDefault().getStateLocation()
                    .toOSString();

            String libraryCacheHomeDir = stateDir + File.separator
                    + CacheBase.TYPE.LIBRARY.getBaseDirName();
            File libCacheHomeDir = new File(libraryCacheHomeDir);
            if (!libCacheHomeDir.exists()) {
                libCacheHomeDir.mkdir();
            }
            while (entryPaths.hasMoreElements()) {
                String cacheZipPath = (String) entryPaths.nextElement();

                URL entryUrl = pluginBundle.getEntry(cacheZipPath);
                File path = new File(cacheZipPath);
                String libraryName = path.getName();
                libraryName = libraryName.substring(0,
                        libraryName.lastIndexOf("."));

                String file = FileLocator.toFileURL(entryUrl).getFile();

                if (cacheZipPath.endsWith("zip")) {
                    LibraryModel libraryModel = new LibraryModel();
                    libraryModel.setPath(cacheZipPath);
                    libraryModel.setLibName(libraryName);
                    WSearchLibraryCacheArgument argument = new WSearchLibraryCacheArgument(
                            CacheBase.TYPE.LIBRARY, libraryCacheHomeDir,
                            libraryModel);
                    WSearchLibraryCache cache = new WSearchLibraryCache("");
                    if (libraryRepositories.containsKey(libraryName)
                            && cache.exist(argument)) {
                        continue;
                    }
                    extractCacheDir(new File(file), argument, true);
                }
            }
            saveLibraryRepositories();
        }

    }

    /**
     * Add cache embedded plug-in from the cache folder.<br/>
     * 
     * @throws IOException
     *             It is Throw Failure to file input and output
     * @throws XmlPullParserException
     *             It is Throw Failure to interpret the XML
     */
    private static void addEmbeddedCacheFromCacheFolder() throws IOException,
            XmlPullParserException {
        String stateDir = Activator.getDefault().getStateLocation()
                .toOSString();

        String libraryCacheHomeDir = stateDir + File.separator
                + CacheBase.TYPE.LIBRARY.getBaseDirName();
        File cacheHomeDir = new File(libraryCacheHomeDir);
        if (cacheHomeDir.exists()) {
            File[] listFiles = cacheHomeDir.listFiles();
            PomReader pomReader = null;
            for (int i = 0; i < listFiles.length; i++) {
                File cacheDir = listFiles[i];
                if (cacheDir.isDirectory()) {
                    String cacheTopFile = cacheDir.toString() + File.separator
                            + "pom.xml";
                    pomReader = new PomReader();
                    pomReader.loadPomFile(cacheTopFile);
                    if (pomReader.isEmbeddedCache()) {
                        // Change to enable true thing that matched the name of
                        // the library of embedded
                        Activator.addRepositoryEnableCache(pomReader
                                .getPomSetting().getArtifactId(), pomReader
                                .getLibraryPath(), pomReader.getPomSetting()
                                .getDescription(), true, pomReader
                                .isMavenType(), true);
                    }
                }
            }
        }
    }

    /**
     * Check cache data that the user has made to read whether there is.<br/>
     * 
     * @return Cache data to confirm the presence of
     * @throws IOException
     *             It is Throw Failure to file input and output
     * @throws XmlPullParserException
     *             It is Throw Failure to interpret the XML
     */
    public static List<LibraryRepository> checkExistUserCacheFromCacheFolder()
            throws IOException, XmlPullParserException {

        List<LibraryRepository> repos = new LinkedList<LibraryRepository>();
        String stateDir = Activator.getDefault().getStateLocation()
                .toOSString();

        String libraryCacheHomeDir = stateDir + File.separator
                + CacheBase.TYPE.LIBRARY.getBaseDirName();
        File cacheHomeDir = new File(libraryCacheHomeDir);
        if (cacheHomeDir.exists()) {
            File[] listFiles = cacheHomeDir.listFiles();
            PomReader pomReader = null;

            for (int i = 0; i < listFiles.length; i++) {
                File cacheDir = listFiles[i];
                if (cacheDir.isDirectory()) {
                    String cacheTopFile = cacheDir.toString() + File.separator
                            + "pom.xml";
                    if (new File(cacheTopFile).exists()) {
                        pomReader = new PomReader();
                        pomReader.loadPomFile(cacheTopFile);
                        LibraryRepository repository = new LibraryRepository(
                                pomReader.getPomSetting().getArtifactId(),
                                pomReader.getLibraryPath(), pomReader
                                        .getPomSetting().getDescription(),
                                false, pomReader.isMavenType(),
                                pomReader.isEmbeddedCache());
                        repository.setCache(true);
                        repos.add(repository);
                    }
                }
            }
        }
        return repos;
    }

    /**
     * Remove the cache directory from a ZIP file.<br/>
     * 
     * @param targetZipPath
     *            ZIP file extraction target
     * @param argument
     *            Argument class that contains the cache information
     * @param isEmbedded
     *            Whether embedded plug-in
     * @throws IOException
     *             It is Throw Failure to file input and output
     */
    public static void extractCacheDir(File targetZipPath,
            WSearchLibraryCacheArgument argument, boolean isEmbedded)
            throws IOException {

        File file = new File(argument.getLibrary().getPath());
        String name = file.getName().split("\\.zip")[0];
        File createDestDir = new File(argument.getPath().toString());

        if (!createDestDir.exists()) {
            createDestDir.mkdir();
        }
        ZipUtil.unzip(targetZipPath, createDestDir);
        if (isEmbedded) {
            // If it is registered and will not be registered in the repository
            // only zip deployment
            if (libraryRepositories.containsKey(name)) {
                return;
            }

            addRepositoryEnableCache(name, argument.getLibrary().getPath()
                    .toString(), "", true, true, isEmbedded);
        }
    }

    /**
     * Get a directory on which to base the library cache information.<br/>
     * 
     * @return Directory as the base of library information cache
     */
    public static String getLibraryCacheHomeDir() {
        String stateDir = Activator.getDefault().getStateLocation()
                .toOSString();
        return stateDir + File.separator
                + CacheBase.TYPE.LIBRARY.getBaseDirName();
    }

    /**
     * Get a directory on which to base Migrated source information cache.<br/>
     * 
     * @return Directory that will be based Migrated source information cache
     */
    public static String getAnalyzeCacheHomeDir() {
        String stateDir = Activator.getDefault().getStateLocation()
                .toOSString();
        return stateDir + File.separator
                + CacheBase.TYPE.ANALYZE.getBaseDirName();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        LOGGER.debug(getResourceString("debug.msg.end") + "Activator#stop");
        plugin = null;
        super.stop(context);
    }

    /**
     * Get a shared instance of Activator.<br/>
     * 
     * @return Instance of the Activator
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Get ImageDescriptor from the image file that is specified by the relative
     * path of the plug-in.<br/>
     * 
     * @param path
     *            Relative path to an image file
     * @return Instance of ImageDescriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * Get a set of Plugin.
     * 
     * @return Setting Plugin
     */
    public static IEclipsePreferences getPreferences() {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
        return prefs;
    }

    /**
     * Save the settings of Plugin.<br/>
     * 
     */
    public void savePreferences() {
        IEclipsePreferences prefs = getPreferences();
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            String message = getResourceString(Activator.class.getName()
                    + ".err.msg.PrefSaveFailed");
            log(e, message);
            ErrorDialog.openErrorDialog(getActiveWorkbenchShell(), e, message);
        }
    }

    /**
     * Get a library group.<br/>
     * 
     * @return Library group
     * @throws WorkbenchException
     *             Workbench exception
     */
    public static Map<String, LibraryRepository> getLibraryRepositories()
            throws WorkbenchException {
        if (libraryRepositories == null) {
            try {
                libraryRepositories = loadLibraryRepositoriesFromPreferences();
                if (libraryRepositories == null) {
                    getDefaultRepositories();
                }
            } catch (IOException e) {
                throw new WorkbenchException(e.getMessage(), e);
            } catch (XmlPullParserException e) {
                throw new WorkbenchException(e.getMessage(), e);
            }
        }
        return libraryRepositories;
    }

    /**
     * Get a library group from the configuration of the Plugin.<br/>
     * 
     * @return Library group
     * @throws WorkbenchException
     *             Workbench exception
     */
    private static Map<String, LibraryRepository> loadLibraryRepositoriesFromPreferences()
            throws WorkbenchException {
        try {
            String repositories = getPreferences().get(LIBRARY_REPOSITORIES,
                    null);
            if (repositories == null || repositories.isEmpty()) {
                return null;
            }

            Reader reader = new StringReader(repositories);
            XMLMemento memento = XMLMemento.createReadRoot(reader);
            if (memento.getBoolean(CACHE_USE)) {
                cacheUse = true;
            } else {
                cacheUse = false;
            }
            IMemento[] nodes = memento.getChildren(LIBRARY_REPOSITORY);
            for (IMemento node : nodes) {
                if (libraryRepositories == null) {
                    libraryRepositories = new HashMap<String, LibraryRepository>();
                }
                String name = node.getString(NAME);
                String url = node.getString(URL);
                String description = node.getString(DESCRIPTION);
                boolean enabled = node.getBoolean(ENABLED);
                boolean mavenType = node.getBoolean(MAVEN_TYPE);
                boolean pluginEmbedded = node.getBoolean(PLUGIN_EMBEDDED);
                Integer order = node.getInteger(ORDER);
                if (order == null) {
                    order = libraryRepositories.size() + 1;
                }

                LibraryRepository repository = new LibraryRepository(name, url,
                        description, enabled, mavenType, pluginEmbedded);
                repository.setCache(node.getBoolean(CACHED));
                repository.setZipCache(node.getBoolean(ZIP_CACHE));
                repository.setOrder(order);
                libraryRepositories.put(name, repository);
            }
        } catch (Exception e) {
            logWithDialog(e, getResourceString(Activator.class.getName()
                    + ".err.msg.PrefLoadFailed"));
            libraryRepositories = null;
        }
        return libraryRepositories;
    }

    /**
     * Library reading group at the time of default.<br/>
     * 
     * @return Library group
     * @throws IOException
     *             Input and output exception
     * @throws XmlPullParserException
     *             Exception of XML parsing
     */
    public static Map<String, LibraryRepository> getDefaultRepositories()
            throws IOException, XmlPullParserException {
        cacheUse = true;
        libraryRepositories = new HashMap<String, LibraryRepository>();
        // Detects the built-in cache from the cache folder, is registered as
        // library to pref
        addEmbeddedCacheFromCacheFolder();

        return libraryRepositories;
    }

    /**
     * Register the library repository.<br/>
     * 
     * @param name
     *            Name
     * @param url
     *            Path
     * @param description
     *            Detail
     * @param enabled
     *            Porting library identification flag
     * @param mavenType
     *            maven library recognition flag
     * @param pluginEmbedded
     *            Flag embedded plug-in
     */
    private static void addRepositoryEnableCache(String name, String url,
            String description, boolean enabled, boolean mavenType,
            boolean pluginEmbedded) {
        LibraryRepository repository = new LibraryRepository(name, url,
                description, enabled, mavenType, pluginEmbedded);
        repository.setCache(true);
        repository.setOrder(libraryRepositories.size() + 1);
        libraryRepositories.put(name, repository);
    }

    /**
     * Set the library repository.<br/>
     * 
     * @param libraryRepositories
     *            Library group
     */
    public static void setLibraryRepositories(
            Map<String, LibraryRepository> libraryRepositories) {
        Activator.libraryRepositories = libraryRepositories;
    }

    /**
     * Save Plugin to set the library group.<br/>
     */
    public static void saveLibraryRepositories() {
        if (libraryRepositories == null || libraryRepositories.size() == 0) {
            return;
        }
        XMLMemento memento = XMLMemento.createWriteRoot(LIBRARY_REPOSITORIES);
        if (memento != null) {
            memento.putBoolean(CACHE_USE, cacheUse);
            Writer writer = null;
            try {
                writer = new StringWriter();
                for (LibraryRepository repository : libraryRepositories
                        .values()) {
                    IMemento repositoryNode = memento
                            .createChild(LIBRARY_REPOSITORY);
                    repositoryNode.putString(NAME, repository.getName());
                    repositoryNode.putString(URL, repository.getUrl());
                    repositoryNode.putString(DESCRIPTION,
                            repository.getDescription());
                    repositoryNode.putBoolean(ENABLED, repository.isEnabled());
                    repositoryNode.putBoolean(MAVEN_TYPE,
                            repository.isMavenType());
                    repositoryNode.putBoolean(PLUGIN_EMBEDDED,
                            repository.isPluginEmbedded());
                    repositoryNode.putBoolean(CACHED, repository.hasCache());
                    repositoryNode.putInteger(ORDER, repository.getOrder());
                    repositoryNode.putBoolean(ZIP_CACHE,
                            repository.isZipCache());
                }

                memento.save(writer);
                writer.flush();
                String repositories = writer.toString();
                getPreferences().put(LIBRARY_REPOSITORIES, repositories);
                getPreferences().flush();
            } catch (Exception e) {
                log(e, Activator.getResourceString(Activator
                        .getResourceString(Activator.class.getName()
                                + ".err.msg.LibPrefSaveFailed")));
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Get Plugin setting than the porting filter information.<br/>
     * 
     * @return Porting filter group
     */
    public static Set<SearchFilter> getDestSearchFilters() {
        if (destSearchFilters == null) {
            try {
                destSearchFilters = loadDestSearchFiltersFromPreferences();
            } catch (WorkbenchException e) {
                log(e);
            }
            if (destSearchFilters == null) {
                getDefaultDestSearchFilters();
            }
        }
        return destSearchFilters;
    }

    /**
     * Get Plugin setting than the porting filter information.<br/>
     * 
     * @return Porting filter group
     * @throws WorkbenchException
     *             Workbench exception
     */
    private static Set<SearchFilter> loadDestSearchFiltersFromPreferences()
            throws WorkbenchException {
        String filters = getPreferences().get(DEST_SEARCH_FILTERS, null);
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        Reader reader = new StringReader(filters);
        XMLMemento memento = XMLMemento.createReadRoot(reader);
        IMemento[] nodes = memento.getChildren(DEST_SEARCH_FILTER);
        for (IMemento node : nodes) {
            if (destSearchFilters == null) {
                destSearchFilters = new LinkedHashSet<SearchFilter>();
            }
            String name = node.getString(NAME);
            boolean enabled = node.getBoolean(ENABLED);
            boolean parent = node.getBoolean(FILTER_PARENT);
            IMemento child = node.getChild(FILTER_EXTENTION_TARGETS);
            List<Extension> targets = null;
            if (child != null) {
                targets = new ArrayList<Extension>();
                IMemento[] children = child.getChildren(EXTENTION_TARGET);
                for (IMemento iMemento : children) {
                    Extension extension = new Extension(iMemento.getID(),
                            iMemento.getBoolean(ENABLED), false);
                    String childRef = iMemento.getString(FILTER_CHILD_REF);
                    if (childRef != null) {
                        extension.setChildRef(childRef);
                    }

                    targets.add(extension);
                }
            }
            IMemento child2 = node.getChild(FILTER_PACKAGE_TARGETS);
            List<WSPackage> wsPackages = null;
            if (child2 != null) {
                wsPackages = new ArrayList<WSPackage>();
                IMemento[] children2 = child2.getChildren(PACKAGE_TARGET);
                for (IMemento iMemento : children2) {
                    wsPackages.add(new WSPackage(name, iMemento.getID(),
                            iMemento.getBoolean(ENABLED)));
                }
            }
            SearchFilter filter = new SearchFilter(name, targets, wsPackages,
                    enabled);
            filter.setParent(parent);
            destSearchFilters.add(filter);
        }
        return destSearchFilters;
    }

    /**
     * Read the porting filter group at default.<br/>
     * 
     * @return Porting filter group
     */
    public static Set<SearchFilter> getDefaultDestSearchFilters() {
        destSearchFilters = new LinkedHashSet<SearchFilter>();

        List<Extension> jarTargets = new ArrayList<Extension>();
        jarTargets.add(new Extension("jar", true));
        List<WSPackage> jarPackages = new ArrayList<WSPackage>();

        // jar parent setting
        addDestSearchFilter("jar", jarTargets, jarPackages, true, true);

        List<Extension> xmlTargets = new ArrayList<Extension>();
        xmlTargets.add(new Extension("xml", true));
        List<WSPackage> xmlPackages = new ArrayList<WSPackage>();

        // xml parent setting
        addDestSearchFilter("xml", xmlTargets, xmlPackages, true, true);

        // jar sub fiter setting
        List<Extension> jarSubTargets = new ArrayList<Extension>();
        jarSubTargets.add(new Extension("jar", true));
        jarSubTargets.add(new Extension("jar.pack", true));

        List<WSPackage> jarSubPackages = new ArrayList<WSPackage>();
        addDestSearchFilter("jar/jar", jarSubTargets, jarSubPackages, true,
                false);

        // xml sub filter
        List<Extension> xmlsubTargets = new ArrayList<Extension>();
        xmlsubTargets.add(new Extension("xml", true));
        List<WSPackage> xmlsubPackages = new ArrayList<WSPackage>();
        addDestSearchFilter("xml/xml", xmlsubTargets, xmlsubPackages, true,
                false);

        return destSearchFilters;
    }

    /**
     * Add to Plugin set the porting filter.<br/>
     * 
     * @param name
     *            Name
     * @param targets
     *            Search extension group
     * @param wsPackages
     *            Search package group
     * @param enabled
     *            Enable flag
     * @param parent
     *            Parent flag
     */
    private static void addDestSearchFilter(String name,
            List<Extension> targets, List<WSPackage> wsPackages,
            boolean enabled, boolean parent) {
        SearchFilter filter = new SearchFilter(name, targets, wsPackages,
                enabled);
        filter.setParent(parent);
        destSearchFilters.add(filter);
    }

    /**
     * Set porting filter information.<br/>
     * 
     * @param searchFilters
     *            Porting filter group
     */
    public static void setDestSearchFilters(Set<SearchFilter> searchFilters) {
        Activator.destSearchFilters = searchFilters;
    }

    /**
     * Save Plugin to set the porting filter information.<br/>
     */
    public static void saveDestSearchFilters() {
        if (destSearchFilters == null || destSearchFilters.size() == 0) {
            return;
        }
        XMLMemento memento = XMLMemento.createWriteRoot(DEST_SEARCH_FILTERS);
        Writer writer = null;
        try {
            for (SearchFilter filter : destSearchFilters) {
                IMemento filterNode = memento.createChild(DEST_SEARCH_FILTER);
                filterNode.putString(NAME, filter.getName());
                filterNode.putBoolean(ENABLED, filter.isEnabled());
                filterNode.putBoolean(FILTER_PARENT, filter.isParent());
                List<Extension> targets = filter.getTargets();
                if (targets != null) {
                    IMemento filterExtTargets = filterNode
                            .createChild(FILTER_EXTENTION_TARGETS);
                    for (Extension extenion : targets) {
                        IMemento extTarget = filterExtTargets.createChild(
                                EXTENTION_TARGET, extenion.getName());

                        extTarget.putBoolean(ENABLED, extenion.isEnabled());
                        extTarget.putBoolean(FILTER_USER_DEFINED,
                                extenion.isUserDefined());
                        if (filter.isParent()) {
                            extTarget
                                    .putString(
                                            FILTER_CHILD_REF,
                                            filter.getName() + "/"
                                                    + extenion.getName());
                        }
                    }
                }
                List<WSPackage> wsPackages = filter.getPackages();
                if (wsPackages != null) {
                    IMemento filterPackageTargets = filterNode
                            .createChild(FILTER_PACKAGE_TARGETS);
                    for (WSPackage elem : wsPackages) {
                        IMemento packageTarget = filterPackageTargets
                                .createChild(PACKAGE_TARGET, elem.getName());
                        packageTarget.putBoolean(ENABLED, elem.isEnabled());
                        packageTarget.putBoolean(FILTER_USER_DEFINED,
                                elem.isUserDefined());
                    }
                }
            }
            writer = new StringWriter();
            memento.save(writer);
            writer.flush();
            String filters = writer.toString();
            getPreferences().put(DEST_SEARCH_FILTERS, filters);
            getPreferences().flush();
        } catch (IOException e) {
            String message = Activator.getResourceString(Activator.class
                    .getName() + ".err.msg.DestPrefSaveFailed");
            log(e, Activator.getResourceString(message));
            ErrorDialog.openErrorDialog(getActiveWorkbenchShell(), e, message);
        } catch (BackingStoreException e) {
            String message = Activator.getResourceString(Activator.class
                    .getName() + ".err.msg.DestPrefSaveFailed");
            log(e, Activator.getResourceString(message));
            ErrorDialog.openErrorDialog(getActiveWorkbenchShell(), e, message);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Get Plugin from the transplant setting target filter information.<br/>
     * 
     * @return Migrated filter group
     */
    public static Set<SearchFilter> getSrcSearchFilters() {
        if (searchFilters == null) {
            try {
                searchFilters = loadSrcSearchFiltersFromPreferences();
            } catch (WorkbenchException e) {
                log(e);
            }
            if (searchFilters == null) {
                searchFilters = getDefaultSrcSearchFilters();
            }
        }
        return searchFilters;
    }

    /**
     * Get the filter information to be used in transplantation target source
     * analysis at the time.<br/>
     * 
     * @param filterKeyNames
     *            Key of the filter
     * @return Filter information
     */
    public static Set<SearchFilter> getSrcSearchFilters(
            List<String> filterKeyNames) {
        if (searchFilters == null) {
            try {
                searchFilters = loadSrcSearchFiltersFromPreferences();
            } catch (WorkbenchException e) {
                log(e);
            }
            if (searchFilters == null) {
                searchFilters = getDefaultSrcSearchFilters();
            }
        }
        Set<SearchFilter> set = new LinkedHashSet<SearchFilter>();
        for (SearchFilter filter : searchFilters) {
            if (filterKeyNames.contains(filter.getName())) {
                set.add(filter);
            }
        }
        return set;
    }

    /**
     * Get the filter information to be used for porting library when parsing.<br/>
     * 
     * @param filterKeyNames
     *            Key of the filter
     * @return Filter information
     */
    public static Set<SearchFilter> getDestSearchFilters(
            List<String> filterKeyNames) {
        if (destSearchFilters == null) {
            try {
                destSearchFilters = loadDestSearchFiltersFromPreferences();
            } catch (WorkbenchException e) {
                log(e);
            }
            if (destSearchFilters == null) {
                destSearchFilters = getDefaultDestSearchFilters();
            }
        }
        Set<SearchFilter> set = new LinkedHashSet<SearchFilter>();
        for (SearchFilter filter : destSearchFilters) {
            if (filterKeyNames.contains(filter.getName())) {
                set.add(filter);
            }
        }
        return set;
    }

    /**
     * Get Plugin than setting a target filter transplant group.<br/>
     * 
     * @return Migrated filter group
     * @throws WorkbenchException
     *             Workbench exception
     */
    private static Set<SearchFilter> loadSrcSearchFiltersFromPreferences()
            throws WorkbenchException {
        String filters = getPreferences().get(SRC_SEARCH_FILTERS, null);
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        Reader reader = new StringReader(filters);
        XMLMemento memento = XMLMemento.createReadRoot(reader);

        IMemento[] nodes = memento.getChildren(SRC_SEARCH_FILTER);
        for (IMemento node : nodes) {
            if (searchFilters == null) {
                searchFilters = new LinkedHashSet<SearchFilter>();
            }
            String name = node.getString(NAME);
            boolean enabled = node.getBoolean(ENABLED);
            boolean parent = (node.getBoolean(FILTER_PARENT) == null) ? false
                    : node.getBoolean(FILTER_PARENT);
            IMemento child = node.getChild(FILTER_EXTENTION_TARGETS);
            List<Extension> targets = null;
            if (child != null) {
                targets = new ArrayList<Extension>();
                IMemento[] children = child.getChildren(EXTENTION_TARGET);
                for (IMemento iMemento : children) {
                    Extension extension = new Extension(iMemento.getID(),
                            iMemento.getBoolean(ENABLED), false);
                    String childRef = iMemento.getString(FILTER_CHILD_REF);
                    if (childRef != null) {
                        extension.setChildRef(childRef);
                    }
                    boolean userDefined = (iMemento
                            .getBoolean(FILTER_USER_DEFINED) == null) ? false
                            : iMemento.getBoolean(FILTER_USER_DEFINED);
                    extension.setUserDefined(userDefined);

                    targets.add(extension);
                }
            }
            IMemento child2 = node.getChild(FILTER_PACKAGE_TARGETS);
            List<WSPackage> wsPackages = null;
            if (child2 != null) {
                wsPackages = new ArrayList<WSPackage>();
                IMemento[] children2 = child2.getChildren(PACKAGE_TARGET);
                for (IMemento iMemento : children2) {
                    WSPackage wsPackage = new WSPackage(name, iMemento.getID(),
                            iMemento.getBoolean(ENABLED));
                    boolean userDefined = (iMemento
                            .getBoolean(FILTER_USER_DEFINED) == null) ? false
                            : iMemento.getBoolean(FILTER_USER_DEFINED);
                    wsPackage.setUserDefined(userDefined);
                    wsPackages.add(wsPackage);
                }
            }
            SearchFilter filter = new SearchFilter(name, targets, wsPackages,
                    enabled);
            filter.setParent(parent);
            searchFilters.add(filter);
        }
        return searchFilters;
    }

    /**
     * Get Plugin from the transplant setting target filter information.<br/>
     * 
     * @return Migrated filter group
     */
    public static Set<SearchFilter> getDefaultSrcSearchFilters() {
        Set<SearchFilter> searchFilters = new LinkedHashSet<SearchFilter>();
        List<Extension> javaTargets = new ArrayList<Extension>();
        Extension extension = new Extension("java", true);
        extension.setChildRef("java/java");
        javaTargets.add(extension);
        List<WSPackage> wsPackages = new ArrayList<WSPackage>();
        addSrcSearchFilter(searchFilters, "java", javaTargets, wsPackages,
                true, true);

        // jsp filter
        List<Extension> jspTargets = new ArrayList<Extension>();
        extension = new Extension("jsp", true);
        extension.setChildRef("jsp/jsp");
        jspTargets.add(extension);
        List<WSPackage> jspPackages = new ArrayList<WSPackage>();
        addSrcSearchFilter(searchFilters, "jsp", jspTargets, jspPackages, true,
                true);

        // xml filter
        List<Extension> xmlTargets = new ArrayList<Extension>();
        extension = new Extension("xml", true);
        extension.setChildRef("xml/xml");
        xmlTargets.add(extension);
        List<WSPackage> xmlPackages = new ArrayList<WSPackage>();
        addSrcSearchFilter(searchFilters, "xml", xmlTargets, xmlPackages, true,
                true);

        // java sub fiter
        List<Extension> javasubTargets = new ArrayList<Extension>();
        javasubTargets.add(new Extension("java", true));

        List<WSPackage> javaSubPackages = new ArrayList<WSPackage>();
        javaSubPackages.add(new WSPackage("java/java", "^java\\..*", true));
        javaSubPackages.add(new WSPackage("java/java", "^javax\\..*", true));
        javaSubPackages
                .add(new WSPackage("java/java", "^org\\.omg\\..*", true));
        javaSubPackages.add(new WSPackage("java/java", "^org\\.w3c\\.dom\\..*",
                true));
        javaSubPackages.add(new WSPackage("java/java",
                "^org\\.ietf\\.jgss\\..*", true));
        javaSubPackages.add(new WSPackage("java/java", "^org\\.xml\\.sax\\..*",
                true));
        addSrcSearchFilter(searchFilters, "java/java", javasubTargets,
                javaSubPackages, true, false);

        // jsp sub filter
        List<Extension> jspsubTargets = new ArrayList<Extension>();
        jspsubTargets.add(new Extension("jsp", true));
        List<WSPackage> jspsubPackages = new ArrayList<WSPackage>();
        jspsubPackages.add(new WSPackage("jsp/jsp", "^java\\..*", true));
        jspsubPackages.add(new WSPackage("jsp/jsp", "^javax\\..*", true));
        jspsubPackages.add(new WSPackage("jsp/jsp", "^org\\.omg\\..*", true));
        jspsubPackages.add(new WSPackage("jsp/jsp", "^org\\.w3c\\.dom\\..*",
                true));
        jspsubPackages.add(new WSPackage("jsp/jsp", "^org\\.ietf\\.jgss\\..*",
                true));
        jspsubPackages.add(new WSPackage("jsp/jsp", "^org\\.xml\\.sax\\..*",
                true));
        jspsubPackages.add(new WSPackage("jsp/jsp",
                "^http://java\\.sun\\.com/.*", true));
        addSrcSearchFilter(searchFilters, "jsp/jsp", jspsubTargets,
                jspsubPackages, true, false);

        // xml sub filter
        List<Extension> xmlsubTargets = new ArrayList<Extension>();
        xmlsubTargets.add(new Extension("xml", true));
        List<WSPackage> xmlsubPackages = new ArrayList<WSPackage>();
        xmlsubPackages.add(new WSPackage("xml/xml",
                "^http://java\\.sun\\.com/.*", true));
        addSrcSearchFilter(searchFilters, "xml/xml", xmlsubTargets,
                xmlsubPackages, true, false);

        return searchFilters;
    }

    /**
     * Sign up porting target filter list to be portable target filter
     * information.<br/>
     * 
     * @param searchFilters
     *            Migrated filter list to be added
     * @param name
     *            Name
     * @param targets
     *            Filter extension
     * @param wsPackages
     *            Filter package
     * @param enabled
     *            Enable flag
     * @param parent
     *            Parent flag
     */
    private static void addSrcSearchFilter(Set<SearchFilter> searchFilters,
            String name, List<Extension> targets, List<WSPackage> wsPackages,
            boolean enabled, boolean parent) {
        SearchFilter filter = new SearchFilter(name, targets, wsPackages,
                enabled);
        filter.setParent(parent);
        searchFilters.add(filter);
    }

    /**
     * Set in the Activator transplant target filter information.<br/>
     * 
     * @param searchFilters
     */
    public static void setSrcSearchFilters(Set<SearchFilter> searchFilters) {
        Activator.searchFilters = searchFilters;
    }

    /**
     * Save Plugin to set the target transplant filter information.<br/>
     */
    public static void saveSrcSearchFilters() {
        if (searchFilters == null || searchFilters.size() == 0) {
            return;
        }
        XMLMemento memento = XMLMemento.createWriteRoot(SRC_SEARCH_FILTERS);
        Writer writer = null;
        try {
            for (SearchFilter filter : searchFilters) {
                IMemento filterNode = memento.createChild(SRC_SEARCH_FILTER);
                filterNode.putString(NAME, filter.getName());
                filterNode.putBoolean(ENABLED, filter.isEnabled());
                filterNode.putBoolean(FILTER_PARENT, filter.isParent());
                List<Extension> targets = filter.getTargets();
                if (targets != null) {
                    IMemento filterExtTargets = filterNode
                            .createChild(FILTER_EXTENTION_TARGETS);
                    for (Extension extenion : targets) {
                        IMemento extTarget = filterExtTargets.createChild(
                                EXTENTION_TARGET, extenion.getName());

                        extTarget.putBoolean(ENABLED, extenion.isEnabled());
                        extTarget.putBoolean(FILTER_USER_DEFINED,
                                extenion.isUserDefined());
                        if (filter.isParent()) {
                            extTarget
                                    .putString(
                                            FILTER_CHILD_REF,
                                            filter.getName() + "/"
                                                    + extenion.getName());
                        }
                    }
                }
                List<WSPackage> wsPackages = filter.getPackages();
                if (wsPackages != null) {
                    IMemento filterPackageTargets = filterNode
                            .createChild(FILTER_PACKAGE_TARGETS);
                    for (WSPackage elem : wsPackages) {
                        IMemento packageTarget = filterPackageTargets
                                .createChild(PACKAGE_TARGET, elem.getName());
                        packageTarget.putBoolean(ENABLED, elem.isEnabled());
                        packageTarget.putBoolean(FILTER_USER_DEFINED,
                                elem.isUserDefined());
                    }
                }
            }
            writer = new StringWriter();
            memento.save(writer);
            writer.flush();
            String filters = writer.toString();
            getPreferences().put(SRC_SEARCH_FILTERS, filters);
            getPreferences().flush();
        } catch (IOException e) {
            log(e, Activator.getResourceString(Activator
                    .getResourceString(Activator.class.getName()
                            + ".err.msg.SrcPrefSaveFailed")));
        } catch (BackingStoreException e) {
            log(e, Activator.getResourceString(Activator
                    .getResourceString(Activator.class.getName()
                            + ".err.msg.SrcPrefSaveFailed")));
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Log output.<br/>
     * 
     * @param e
     *            Exception
     * @param message
     *            Message
     */
    public static void log(Exception e, String message) {
        IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, message, e);
        plugin.getLog().log(status);
        LOGGER.error(message, e);
    }

    /**
     * With log output, it displays an error dialog on the screen.<br/>
     * 
     * @param e
     *            Exception
     * @param message
     *            Message
     */
    public static void logWithDialog(Exception e, String message) {
        ErrorDialog.openErrorDialog(Activator.getActiveWorkbenchShell(), e,
                message);
        IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, message, e);
        plugin.getLog().log(status);
        LOGGER.error(message, e);
    }

    /**
     * Get the shell of the active workbench.<br/>
     * 
     * @return shell Shell of workbench active
     */
    public static Shell getActiveWorkbenchShell() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        return window != null ? window.getShell() : getStandardDisplay()
                .getActiveShell();
    }

    /**
     * Get the window of the workbench active.<br/>
     * 
     * @return IWorkbenchWindow Window of the workbench active
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    /**
     * Get the display.<br/>
     * If the thread that is calling this method is in possession of the
     * relevant display, <br/>
     * get the related display, and get the default display if not in
     * possession.<br/>
     * 
     * @return Display
     */
    private static Display getStandardDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;
    }

    /**
     * The log output to the error log.<br/>
     * 
     * @param e
     *            Exception
     */
    public static void log(Throwable e) {
        IStatus status = new Status(IStatus.ERROR, PLUGIN_ID,
                e.getLocalizedMessage(), e);
        plugin.getLog().log(status);
        LOGGER.error(e.getMessage(), e);
    }

    /**
     * Get the value corresponding to the key from the message file by the
     * resource bundle mechanism.<br/>
     * 
     * @param key
     *            Message key
     * @return Message
     */
    public static String getResourceString(String key) {
        try {
            return plugin.resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Get the listener to monitor changes in the workspace.<br/>
     * 
     * @return Listener to monitor the change of the workspace
     */
    public static WorkSpaceIResourceChangeListener getWorkSpaceIResourceChangeListener() {
        return workspaceChangeListener;
    }

    /**
     * Set a listener to monitor the change of the workspace.<br/>
     * 
     * @param listener
     *            Listener to monitor the change of the workspace
     */
    public static void setWorkSpaceIResourceChangeListener(
            WorkSpaceIResourceChangeListener listener) {
        workspaceChangeListener = listener;
    }

    /**
     * Determine whether to use the library cache.<br/>
     * 
     * @return Determine whether to use the library cache
     */
    public static boolean useLibraryCache() {
        return cacheUse;
    }

    /**
     * Set whether or not to make use of the library cache.<br/>
     * 
     * @param use
     *            Determine whether to use the library cache
     */
    public static void setUseLibraryCache(boolean use) {
        cacheUse = use;
    }

    /**
     * load the resource file.<br/>
     * 
     * @param key
     *            Resource Key name
     */
    private static URL getResources(String key) {
        return Activator.getDefault().getBundle().getResource(key);
    }
}
