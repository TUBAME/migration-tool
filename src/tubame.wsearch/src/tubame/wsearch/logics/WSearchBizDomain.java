/*
 * WSearchBizDomain.java
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
package tubame.wsearch.logics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.WorkbenchException;
import tubame.wsearch.Activator;
import tubame.wsearch.biz.cache.CacheBase;
import tubame.wsearch.biz.cache.WSearchAnalyzerCacheArgument;
import tubame.wsearch.biz.cache.WSearchCacheManager;
import tubame.wsearch.biz.cache.CacheBase.TYPE;
import tubame.wsearch.biz.cache.WSearchAnalyzerCache.AnalyzerCacheType;
import tubame.wsearch.biz.comparer.AbstractComparer;
import tubame.wsearch.biz.ex.WSearchBizException;
import tubame.wsearch.biz.model.Extension;
import tubame.wsearch.biz.model.LibraryMetaDataGenerationInput;
import tubame.wsearch.biz.model.LibraryModel;
import tubame.wsearch.biz.model.PhaseOutbound;
import tubame.wsearch.biz.model.SearchFilter;
import tubame.wsearch.biz.model.WSPackage;
import tubame.wsearch.biz.model.LibraryModel.LIBRARY_TYPE;
import tubame.wsearch.biz.model.LibraryModel.TARGET_TYPE;
import tubame.wsearch.logics.job.JavaAnalyzeAndCompareJob;
import tubame.wsearch.logics.job.JspAnalyzeAndCompareJob;
import tubame.wsearch.logics.job.LibraryMetaInfoGenerateJob;
import tubame.wsearch.logics.job.XmlAnalyzeAndCompareJob;
import tubame.wsearch.models.LibraryRepository;
import tubame.wsearch.ui.dialog.ErrorDialog;
import tubame.wsearch.ui.view.WSearchResultView;
import tubame.wsearch.util.PluginUtil;
import tubame.wsearch.util.resource.ResourceUtil;

/**
 * Domain class that is accessed from the Biz UI.<br/>
 */
public class WSearchBizDomain {

    /**
     * Save directory name for the library cache information
     */
    private static final String LIB_CACHE_TEMP_DIRNAME = "library.temp";

    /**
     * Map of processing results and Migrated source analysis job
     */
    private Map<Job, IStatus> analyzeJobsMap = new HashMap<Job, IStatus>();

    /**
     * Output directory path result
     */
    private String outputDirPath;

    /**
     * Generate data cache.<br/>
     * 
     * @param cacheLoadArg
     *            Analyze data cache Input
     * @param resource
     *            Resource
     * @throws WSearchBizException
     */
    public void cacheInit(WSearchAnalyzerCacheArgument cacheLoadArg,
            IResource resource) throws WSearchBizException {

        if (Activator.getWorkSpaceIResourceChangeListener() != null) {

            // If the listener is registered, because if you check the update
            // flag,
            // has been updated to cache re-created, if work project name is the
            // same as last time,
            // cache discarded
            if (Activator.getWorkSpaceIResourceChangeListener()
                    .getProjectName().equals(resource.getProject().getName())) {

                if (Activator.getWorkSpaceIResourceChangeListener().isUpdate()) {
                    // Cache destroyed
                    removeCacheDir(Activator.getAnalyzeCacheHomeDir());
                }
            } else {
                // Because if the listener is registered,
                // if the project name is different to re-create cache, cache
                // destroyed
                removeCacheDir(Activator.getAnalyzeCacheHomeDir());
            }
        }
        try {
            WSearchCacheManager.getInstance().load(TYPE.ANALYZE, cacheLoadArg);
        } catch (WSearchBizException e) {
            Activator.logWithDialog(
                    e,
                    Activator.getResourceString(this.getClass().getName()
                            + ".err.msg.CacheLoadFailed"));
            throw e;
        }
    }

    /**
     * Delete the cache directory.<br/>
     * 
     * @param analyzeCacheHomeDir
     *            Directory to be deleted
     */
    private void removeCacheDir(String analyzeCacheHomeDir) {
        File dir = new File(analyzeCacheHomeDir);
        delete(dir);
    }

    /**
     * After deleting all the inside of the directory, and delete (or file)
     * directory.<br/>
     * 
     * @param f
     *            Deleted
     */
    private void delete(File f) {
        if (f.isFile()) {
            f.delete();
        }

        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                delete(files[i]);
            }
            f.delete();
        }
    }

    /**
     * After creating a cache for monitoring the project and set the listener.<br/>
     * 
     * @param projectName
     *            Project name
     */
    public void setResourceChangeListener(final String projectName) {

        WorkSpaceIResourceChangeListener workSpaceIResourceChangeListener = null;
        workSpaceIResourceChangeListener = new WorkSpaceIResourceChangeListener(
                projectName);
        Activator
                .setWorkSpaceIResourceChangeListener(workSpaceIResourceChangeListener);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(
                Activator.getWorkSpaceIResourceChangeListener());

    }

    /**
     * Create a porting library metadata information.<br/>
     * 
     * @param libraryRepositories
     *            List of the repository that holds the library information
     * @param cacheLoadArg
     *            Argument that contains the information Migrated source
     *            analysis cache
     * @param stateDir
     *            Destination directory for the library information cache data
     * @param outputDirPath
     *            Output directory path
     * @return Created as a result
     */
    public PhaseOutbound generateDestLibMetaData(
            Collection<LibraryRepository> libraryRepositories,
            WSearchAnalyzerCacheArgument cacheLoadArg, String stateDir,
            final String outputDirPath) {
        this.outputDirPath = outputDirPath;

        List<LibraryModel> libModels = new ArrayList<LibraryModel>();
        LibraryModel model = null;

        boolean enableChk = false;
        for (LibraryRepository libraryRepository : libraryRepositories) {
            model = new LibraryModel();
            model.setLibName(libraryRepository.getName());
            if (libraryRepository.isPluginEmbedded()) {
                String pluginDirectory = PluginUtil.getPluginDir();
                if (System.getProperty("os.name").contains("Windows")
                        && pluginDirectory.startsWith("/")) {
                    pluginDirectory = pluginDirectory.substring(1);
                }
                model.setPath(pluginDirectory + libraryRepository.getUrl());
            } else {
                model.setPath(libraryRepository.getUrl());
            }
            if (libraryRepository.isEnabled()) {
                model.setTargetType(TARGET_TYPE.DEST);
                enableChk = true;
            } else {
                model.setTargetType(TARGET_TYPE.SRC);
            }
            if (libraryRepository.isMavenType()) {
                model.setType(LIBRARY_TYPE.MAVEN);
            } else {
                model.setType(LIBRARY_TYPE.NOT_MAVEN);
            }
            if (libraryRepository.isPluginEmbedded()
                    || libraryRepository.isZipCache()) {
                model.setCache(true);
            } else {
                model.setCache(false);
            }
            libModels.add(model);
        }

        if (!enableChk) {
            String resourceString = Activator.getResourceString(this.getClass()
                    .getName() + ".err.msg.LibEnableERR");
            Activator.logWithDialog(
                    new IllegalArgumentException(resourceString),
                    resourceString);
            return null;
        }
        String importCacheFilePath = createImportCacheFilePath(cacheLoadArg);

        LibraryMetaDataGenerationInput metaDataGenerationInput = new LibraryMetaDataGenerationInput(
                libModels, importCacheFilePath, stateDir + File.separator
                        + CacheBase.TYPE.LIBRARY.getBaseDirName(),
                !Activator.useLibraryCache());

        Set<SearchFilter> destSearchFilters = Activator.getDestSearchFilters();

        destSearchFilters = createEnableLibrarySearchFilters(destSearchFilters);
        metaDataGenerationInput.setLibrarySearchFilters(destSearchFilters);
        metaDataGenerationInput.setOutputDir(outputDirPath);
        metaDataGenerationInput.setLibraryCacheTempPath(stateDir
                + File.separator + LIB_CACHE_TEMP_DIRNAME);
        LibraryMetaInfoGenerateJob job = new LibraryMetaInfoGenerateJob(
                Activator.getResourceString(WSearchBizDomain.class.getName()
                        + ".title.LibGenrationJob"), metaDataGenerationInput);

        job.addJobChangeListener(new IJobChangeListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void sleeping(IJobChangeEvent event) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void scheduled(IJobChangeEvent event) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void running(IJobChangeEvent event) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void done(IJobChangeEvent event) {
                IStatus status = event.getJob().getResult();
                if (status.equals(Status.OK_STATUS)) {
                    Set<Job> jobs = analyzeJobsMap.keySet();
                    if (jobs.size() > 0) {
                        try {
                            AbstractComparer.openOutputStream(outputDirPath
                                    + File.separator
                                    + ResourceUtil.RESULT_FILE_NAME);
                        } catch (IOException e) {
                            // Error should not occur here as we have already
                            // done a check in advance
                            Activator.log(e, Activator.getResourceString(this
                                    .getClass().getName()
                                    + ".err.msg.ResultFileWriteErr"));
                        }
                    }
                    for (Job job : jobs) {
                        job.setUser(true);
                        job.schedule();
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void awake(IJobChangeEvent event) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void aboutToRun(IJobChangeEvent event) {

            }
        });

        job.setUser(true);
        job.schedule();

        return null;
    }

    /**
     * Get a set of valid library information filter.<br/>
     * 
     * @param destSearchFilters
     *            Set of library information filter
     * @return Set of valid library information filter
     */
    private Set<SearchFilter> createEnableLibrarySearchFilters(
            Set<SearchFilter> destSearchFilters) {
        Set<SearchFilter> filterSet = new HashSet<SearchFilter>();
        for (SearchFilter searchFilter : destSearchFilters) {
            if (searchFilter.isEnabled()) {
                filterSet.add(searchFilter);
            }
        }
        return filterSet;
    }

    /**
     * Create a job to compare and analysis of transplant target source.<br/>
     * 
     * @param searchInputRoot
     *            Root path analysis of the target directory
     * @param srcSearchFilters
     *            Filter to be used when parsing
     * @param outputDirPath
     *            Output directory path
     * @return Processing result
     */
    public boolean createAnalyzeAndCompareJobs(IResource searchInputRoot,
            Set<SearchFilter> srcSearchFilters, String outputDirPath) {
        final List<String> enableJavaSearchExtensions = getEnableSearchExtensions("java/java");
        final List<String> enableJspSearchExtensions = getEnableSearchExtensions("jsp/jsp");
        final List<String> enableXmlSearchExtensions = getEnableSearchExtensions("xml/xml");

        final Map<String, List<String>> javaSearchTargetsMap = new HashMap<String, List<String>>();
        final Map<String, List<String>> jspSearchTargetsMap = new HashMap<String, List<String>>();
        final Map<String, List<String>> xmlSearchTargetsMap = new HashMap<String, List<String>>();

        // Correspondence of bugs ut bug filter information is not filtered
        // properly
        final boolean javaSearchEnable = isEnableExtension("java");
        final boolean jspSearchEnable = isEnableExtension("jsp");
        final boolean xmlSearchEnable = isEnableExtension("xml");
        searchInputRoot.getProject();
        try {
            searchInputRoot.accept(new IResourceVisitor() {
                public boolean visit(IResource resource) throws CoreException {
                    if (IResource.FILE == resource.getType()) {
                        IFile file = (IFile) resource;
                        if (javaSearchEnable
                                && enableJavaSearchExtensions.contains(resource
                                        .getFileExtension())) {
                            putSearchTargetMap(javaSearchTargetsMap, resource
                                    .getFileExtension(), file.getLocation()
                                    .toOSString());
                        } else if (jspSearchEnable
                                && enableJspSearchExtensions.contains(resource
                                        .getFileExtension())) {
                            putSearchTargetMap(jspSearchTargetsMap, resource
                                    .getFileExtension(), file.getLocation()
                                    .toOSString());
                        } else if (xmlSearchEnable
                                && enableXmlSearchExtensions.contains(resource
                                        .getFileExtension())) {
                            putSearchTargetMap(xmlSearchTargetsMap, resource
                                    .getFileExtension(), file.getLocation()
                                    .toOSString());
                        }
                    }
                    return true;
                }

                private void putSearchTargetMap(
                        Map<String, List<String>> targetsMap, String ext,
                        String targetFile) {
                    if (!targetsMap.containsKey(ext)) {
                        List<String> list = new ArrayList<String>();
                        list.add(targetFile);
                        targetsMap.put(ext, list);
                    } else {
                        List<String> list = targetsMap.get(ext);
                        list.add(targetFile);
                    }
                }

            });
        } catch (CoreException e) {
            Activator.logWithDialog(
                    e,
                    Activator.getResourceString(this.getClass().getName()
                            + ".err.msg.ResouceVisitErr"));

            return false;
        }

        if (javaSearchTargetsMap.isEmpty() && jspSearchTargetsMap.isEmpty()
                && xmlSearchTargetsMap.isEmpty()) {
            MessageDialog.openInformation(
                    Activator.getActiveWorkbenchShell(),
                    Activator.getResourceString(WSearchBizDomain.class
                            .getName() + ".title.NoTarget"),
                    Activator.getResourceString(WSearchBizDomain.class
                            .getName() + ".label.NoTarget"));
            return false;
        }

        // Check the file is writable
        File outputFile = new File(outputDirPath, ResourceUtil.RESULT_FILE_NAME);
        if (outputFile.exists()) {
            if (outputFile.isDirectory()) {
                Activator.logWithDialog(
                        null,
                        Activator.getResourceString(this.getClass().getName()
                                + ".err.msg.ResultFileWriteErr"));
                return false;
            }
            if (!outputFile.canWrite()) {
                Activator.logWithDialog(
                        null,
                        Activator.getResourceString(this.getClass().getName()
                                + ".err.msg.ResultFileWriteErr"));
                return false;
            }
        }

        AnalyzeAndCompareJobListener analyzeAndCompareJobListener = new AnalyzeAndCompareJobListener(
                searchInputRoot);
        Job job = null;
        try {
            if (!javaSearchTargetsMap.isEmpty()) {
                job = new JavaAnalyzeAndCompareJob(
                        Activator.getResourceString(WSearchBizDomain.class
                                .getName() + ".title.JavaJob"),
                        enableJavaSearchExtensions, javaSearchTargetsMap,
                        getEnableSearchFilters("java",
                                enableJavaSearchExtensions), outputDirPath);
                job.addJobChangeListener(analyzeAndCompareJobListener);
                this.analyzeJobsMap.put(job, null);
            }

            if (!jspSearchTargetsMap.isEmpty()) {
                job = new JspAnalyzeAndCompareJob(
                        Activator.getResourceString(WSearchBizDomain.class
                                .getName() + ".title.JspJob"),
                        enableJspSearchExtensions,
                        jspSearchTargetsMap,
                        getEnableSearchFilters("jsp", enableJspSearchExtensions),
                        outputDirPath);
                job.addJobChangeListener(analyzeAndCompareJobListener);
                this.analyzeJobsMap.put(job, null);
            }

            if (!xmlSearchTargetsMap.isEmpty()) {
                job = new XmlAnalyzeAndCompareJob(
                        Activator.getResourceString(WSearchBizDomain.class
                                .getName() + ".title.XmlJob"),
                        enableXmlSearchExtensions,
                        xmlSearchTargetsMap,
                        getEnableSearchFilters("xml", enableXmlSearchExtensions),
                        outputDirPath);
                job.addJobChangeListener(analyzeAndCompareJobListener);
                this.analyzeJobsMap.put(job, null);
            }
        } catch (Exception e) {
            Activator.logWithDialog(e, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Get a map of the valid target transplant source analysis filter.<br/>
     * 
     * @param parentName
     *            Parent element name
     * @param enableJavaSearchExtensions
     *            List of valid extension
     * @return Map of the filter
     */
    private Map<String, List<Pattern>> getEnableSearchFilters(
            String parentName, List<String> enableJavaSearchExtensions) {
        Map<String, List<Pattern>> filterMap = new HashMap<String, List<Pattern>>();
        for (String ext : enableJavaSearchExtensions) {
            SearchFilter searchFilter = getSearchFiterByName(parentName + "/"
                    + ext);
            if (searchFilter != null) {
                List<WSPackage> packages = searchFilter.getPackages();
                List<Pattern> filters = new LinkedList<Pattern>();
                for (WSPackage wsPackage : packages) {
                    if (wsPackage.isEnabled()) {
                        filters.add(Pattern.compile(wsPackage.getName()));
                    }
                }
                filterMap.put(ext, filters);
            }
        }

        return filterMap;

    }

    /**
     * Get a filter by name.<br/>
     * 
     * @param name
     *            Filter name
     * @return Filter
     */
    private SearchFilter getSearchFiterByName(String name) {
        Set<SearchFilter> srcSearchFilters = Activator.getSrcSearchFilters();
        for (SearchFilter searchFilter : srcSearchFilters) {
            if (searchFilter.getName().equals(name)) {
                return searchFilter;
            }
        }
        return null;
    }

    /**
     * Create a path of import information cache file Migrated source.<br/>
     * 
     * @param argument
     *            Argument that contains the cache information
     * @return File Path
     */
    private String createImportCacheFilePath(
            WSearchAnalyzerCacheArgument argument) {
        StringBuffer cachepath = new StringBuffer();
        cachepath.append(argument.getPath());
        cachepath.append(File.separator);
        cachepath.append(argument.getType().getBaseDirName());
        cachepath.append(File.separator);
        cachepath.append(argument.getSearchTargetPath());
        cachepath.append(File.separator);
        cachepath.append(AnalyzerCacheType.IMPORT.getFileName());
        return cachepath.toString();

    }

    /**
     * Get a list of extensions to be searched.<br/>
     * 
     * @param filterName
     *            Filter name
     * @return List of extensions
     */
    private List<String> getEnableSearchExtensions(String filterName) {
        List<String> sets = new ArrayList<String>();
        Set<SearchFilter> srcSearchFilters = Activator.getSrcSearchFilters();
        for (SearchFilter searchFilter : srcSearchFilters) {
            if (!searchFilter.isParent()
                    && searchFilter.getName().equals(filterName)) {
                List<Extension> targets = searchFilter.getTargets();
                for (Extension extension : targets) {
                    sets.add(extension.getName());
                }
                return sets;
            }
        }
        return sets;

    }

    /**
     * Determine whether a valid filter.<br/>
     * 
     * @param filterName
     *            Filter name
     * @return Valid filter discrimination
     */
    private boolean isEnableExtension(String filterName) {
        Set<SearchFilter> srcSearchFilters = Activator.getSrcSearchFilters();
        for (SearchFilter searchFilter : srcSearchFilters) {
            if (searchFilter.isParent()
                    && searchFilter.getName().equals(filterName)) {
                return searchFilter.isEnabled();
            }
        }
        return false;
    }

    /**
     * Listener class to monitor the job of comparison processing and analysis
     * of transplant target source.<br/>
     */
    private class AnalyzeAndCompareJobListener implements IJobChangeListener {

        /**
         * The root element of the processed
         */
        private IResource resource;

        /**
         * Constructor.<br/>
         * 
         * @param resource
         *            The root element of the processed
         */
        public AnalyzeAndCompareJobListener(IResource resource) {
            super();
            this.resource = resource;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void aboutToRun(IJobChangeEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void awake(IJobChangeEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void done(IJobChangeEvent event) {
            IStatus result = event.getJob().getResult();
            if (result.equals(Status.OK_STATUS)) {
                analyzeJobsMap.put(event.getJob(), Status.OK_STATUS);
            } else if (result.equals(Status.CANCEL_STATUS)) {
                analyzeJobsMap.put(event.getJob(), Status.CANCEL_STATUS);
            } else {
                analyzeJobsMap
                        .put(event.getJob(),
                                new Status(
                                        IStatus.ERROR,
                                        Activator.PLUGIN_ID,
                                        Activator.getResourceString(WSearchBizDomain.class
                                                .getName()
                                                + ".err.msg.ExceptionOccurred")));
            }
            if (isAllJobFinishied()) {
                try {
                    AbstractComparer.closeOutputStream();
                } catch (IOException e) {
                    // Only log output
                    Activator.log(e);
                }
                try {
                    resource.getProject().refreshLocal(
                            IResource.DEPTH_INFINITE, null);
                } catch (CoreException e) {
                    ErrorDialog.openErrorDialog(
                            Activator.getActiveWorkbenchShell(), e,
                            e.getMessage());
                }

                // Process we're done,
                // set the listener that there are no changes to the project to
                // conduct the monitoring settings of the workspace,
                // it was conducted by general-purpose search
                setResourceChangeListener(resource.getProject().getName());

                showResults();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void running(IJobChangeEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void scheduled(IJobChangeEvent event) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void sleeping(IJobChangeEvent event) {
        }

        /**
         * The comparison determines whether the job has completed and all
         * analysis.<br/>
         * 
         * @return Compare job discrimination been completed and all analysis
         */
        private boolean isAllJobFinishied() {
            Set<Job> keySet = analyzeJobsMap.keySet();
            for (Job job : keySet) {
                IStatus iStatus = analyzeJobsMap.get(job);
                if (iStatus == null) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Listener to monitor the change of the project.<br/>
     */
    public static class WorkSpaceIResourceChangeListener implements
            IResourceChangeListener {

        /**
         * Project name
         */
        private String projectName;

        /**
         * Project is whether it has been updated
         */
        private boolean update;

        /**
         * Constructor.<br/>
         * 
         * @param projectName
         */
        public WorkSpaceIResourceChangeListener(String projectName) {
            this.projectName = projectName;
        }

        /**
         * Get the project name.<br/>
         * 
         * @return Project name
         */
        public String getProjectName() {
            return projectName;
        }

        /**
         * The project is determining whether it has been updated.<br/>
         * 
         * @return Determine if the project has been updated
         */
        public boolean isUpdate() {
            return update;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void resourceChanged(IResourceChangeEvent event) {
            try {
                event.getDelta().accept(new IResourceDeltaVisitor() {

                    @Override
                    public boolean visit(IResourceDelta delta)
                            throws CoreException {
                        if (delta.getKind() == IResourceDelta.CHANGED) {
                            IResourceDelta[] affectedChildren = delta
                                    .getAffectedChildren(IResourceDelta.CHANGED);
                            for (IResourceDelta iResourceDelta : affectedChildren) {
                                String workedProjectName = iResourceDelta
                                        .getResource().getName();
                                if (projectName.equals(workedProjectName)) {
                                    update = true;
                                }
                            }
                        }
                        return false;
                    }
                });
            } catch (CoreException e) {
                ErrorDialog.openErrorDialog(
                        Activator.getActiveWorkbenchShell(), e, e.getMessage());
            }
        }

    }

    /**
     * View the results of the general-purpose search.<br/>
     * 
     */
    public void showResults() {
        Display.getDefault().asyncExec(new Runnable() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                // Open the editor
                try {
                    String filePath = outputDirPath + File.separator
                            + ResourceUtil.RESULT_FILE_NAME;
                    IPath path = new Path(filePath);
                    IWorkspace ws = ResourcesPlugin.getWorkspace();
                    filePath = path.makeRelativeTo(ws.getRoot().getLocation())
                            .toString();
                    for (IViewReference ref : PluginUtil.getActivePage()
                            .getViewReferences()) {
                        if (WSearchResultView.ID.equals(ref.getId())
                                && filePath.equals(ref.getSecondaryId())) {
                            PluginUtil.getActivePage().hideView(ref);
                        }
                    }
                    PluginUtil.getActivePage().showView(WSearchResultView.ID,
                            filePath, IWorkbenchPage.VIEW_ACTIVATE);
                } catch (WorkbenchException e) {
                    Activator.log(e);
                }
            }
        });
    }
}
