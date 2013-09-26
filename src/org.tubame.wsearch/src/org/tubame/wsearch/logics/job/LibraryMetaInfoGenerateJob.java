/*
 * LibraryMetaInfoGenerateJob.java
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
package org.tubame.wsearch.logics.job;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.WorkbenchException;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.biz.WSPhaseService;
import org.tubame.wsearch.biz.cache.WSearchLibraryCache;
import org.tubame.wsearch.biz.cache.WSearchLibraryCacheArgument;
import org.tubame.wsearch.biz.cache.CacheBase.TYPE;
import org.tubame.wsearch.biz.ex.WSearchBizException;
import org.tubame.wsearch.biz.model.LibraryMetaDataGenerationInput;
import org.tubame.wsearch.biz.model.LibraryModel;
import org.tubame.wsearch.biz.model.PhaseCountDownLatch;
import org.tubame.wsearch.biz.model.PhaseInbound;
import org.tubame.wsearch.biz.model.PomSetting;
import org.tubame.wsearch.biz.process.WSearchLibraryMetaInfoGenerateProcessor;
import org.tubame.wsearch.biz.util.FileVisitor;
import org.tubame.wsearch.biz.util.PomWriter;
import org.tubame.wsearch.logics.obsvr.WSearchLibMetaInfoGenProcessObserver;
import org.tubame.wsearch.models.LibraryRepository;

/**
 * Jobs EclipsePlatform to create a library metadata from porting library.<br/>
 */
public class LibraryMetaInfoGenerateJob extends Job {

    /**
     * Library meta-data creation for input
     */
    private LibraryMetaDataGenerationInput metaDataGenerationInput;

    /**
     * Constructor.<br/>
     * 
     * @param name
     *            Job name
     * @param metaDataGenerationInput
     *            Library meta-data creation for input
     */
    public LibraryMetaInfoGenerateJob(String name,
            LibraryMetaDataGenerationInput metaDataGenerationInput) {
        super(name);
        this.metaDataGenerationInput = metaDataGenerationInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {

        // Core side is set in the Init event PhaseCountDownLatch,
        // there is no need to PhaseCountDownLatch the UI side
        // Normally, setting the UI side PhaseCountDownLatch
        final PhaseCountDownLatch phaseCountDownLatch = new PhaseCountDownLatch(
                this.metaDataGenerationInput.getLibraryModels().size() + 1);

        CountDownLatch starter = new CountDownLatch(1);
        WSearchLibraryMetaInfoGenerateProcessor processor = new WSearchLibraryMetaInfoGenerateProcessor(
                1, starter, phaseCountDownLatch);
        WSearchLibMetaInfoGenProcessObserver observer = new WSearchLibMetaInfoGenProcessObserver(
                starter, phaseCountDownLatch, processor, monitor);
        processor.setObserver(observer);

        PhaseInbound<LibraryMetaDataGenerationInput> phaseInbound = new PhaseInbound<LibraryMetaDataGenerationInput>(
                this.metaDataGenerationInput);
        processor.process(phaseInbound);

        // Screen update processing is performed in the Observer receives the
        // event notification from the core
        try {
            // Wait until the core side to notify the INIT.
            // In response in the observer, INIT to perform beginTask
            starter.await();
        } catch (InterruptedException e) {
            String message = Activator
                    .getResourceString(LibraryMetaInfoGenerateJob.class
                            .getName() + ".err.msg.StarterErr");
            Activator.log(e, message);
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
        }

        try {
            phaseCountDownLatch.await();
        } catch (InterruptedException e) {
            String message = Activator
                    .getResourceString(LibraryMetaInfoGenerateJob.class
                            .getName() + ".err.msg.InterruptedErr");
            Activator.log(e, message);
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
        }

        monitor.done();
        if (observer.exceptionOccurred()) {
            rollback();
            String message = Activator
                    .getResourceString(LibraryMetaInfoGenerateJob.class
                            .getName() + ".err.msg.ExceptionOccurred");
            MultiStatus multiStatus = new MultiStatus(Activator.PLUGIN_ID,
                    IStatus.ERROR, null, new WSearchBizException(
                            WSPhaseService.TYPE.DESTLIB_METADATA_GENERATE,
                            message));
            if (observer.getExceptionList() != null) {
                for (Throwable e : observer.getExceptionList()) {
                    MultiStatus status = new MultiStatus(Activator.PLUGIN_ID,
                            IStatus.ERROR, null, e);
                    if (e != null && e.getStackTrace() != null) {
                        for (StackTraceElement element : e.getStackTrace()) {
                            status.add(new Status(IStatus.ERROR,
                                    Activator.PLUGIN_ID, element.toString()));
                        }
                    }
                    multiStatus.add(status);
                }
            }

            return multiStatus;
        }
        if (monitor.isCanceled()) {
            rollback();
            return Status.CANCEL_STATUS;
        }
        if (this.metaDataGenerationInput.getLibraryModels().size() != 0) {
            // Only if there is a being created, to perform the commit
            // processing
            try {
                commit();
            } catch (WorkbenchException e) {
                String message = Activator
                        .getResourceString(LibraryMetaInfoGenerateJob.class
                                .getName() + ".err.msg.CommitErr");
                MultiStatus multiStatus = new MultiStatus(Activator.PLUGIN_ID,
                        IStatus.ERROR, null, new WSearchBizException(
                                WSPhaseService.TYPE.DESTLIB_METADATA_GENERATE,
                                message));
                MultiStatus status = new MultiStatus(Activator.PLUGIN_ID,
                        IStatus.ERROR, null, e);
                if (e != null && e.getStackTrace() != null) {
                    for (StackTraceElement element : e.getStackTrace()) {
                        status.add(new Status(IStatus.ERROR,
                                Activator.PLUGIN_ID, element.toString()));
                    }
                }
                multiStatus.add(status);
                return multiStatus;
            }
        }

        return Status.OK_STATUS;
    }

    /**
     * Rollback of a failed attempt to create the library information.<br/>
     * 
     */
    private void rollback() {
        List<LibraryModel> libraryModels = this.metaDataGenerationInput
                .getLibraryModels();
        WSearchLibraryCache cache = new WSearchLibraryCache("");

        for (LibraryModel libraryModel : libraryModels) {
            WSearchLibraryCacheArgument argument = new WSearchLibraryCacheArgument(
                    TYPE.LIBRARY,
                    this.metaDataGenerationInput.getLibraryCacheRootPath(),
                    libraryModel);
            if (!cache.exist(argument)) {
                try {
                    deleteCacheDir(
                            this.metaDataGenerationInput
                                    .getLibraryCacheRootPath(),
                            libraryModel);
                } catch (IOException e) {
                    // Wreckage of the cache directory is left, but only the log
                    // output does not affect the processing
                    Activator.log(e);
                }
            }
        }
    }

    /**
     * Commit processing in the case of a successful library information
     * created.<br/>
     * 
     * @throws WorkbenchException
     *             Throw if it fails to commit processing
     */
    private void commit() throws WorkbenchException {
        List<LibraryModel> libraryModels = this.metaDataGenerationInput
                .getLibraryModels();

        boolean existAvailableLibrary = false;
        for (LibraryModel libraryModel : libraryModels) {
            // Skip was when zip cache and embedded cache
            if (libraryModel.isCache()) {
                existAvailableLibrary = true;
                continue;
            }

            // pom.xml is present and if it's not overwritten
            if (!this.metaDataGenerationInput.isOverwrite()) {
                String pomFile = this.metaDataGenerationInput
                        .getLibraryCacheRootPath()
                        + File.separator
                        + libraryModel.getLibName()
                        + File.separator
                        + "pom.xml";
                if (new File(pomFile).exists()) {
                    existAvailableLibrary = true;
                    continue;
                }
            }

            PomSetting pomSetting = new PomSetting();
            pomSetting.setArtifactId(libraryModel.getLibName());
            pomSetting.setGroupId("org.tubame.wsearch.cache");
            pomSetting.setVersion("1.0.0");
            LibraryRepository libraryFromPref = getLibraryFromPref(libraryModel
                    .getLibName());
            if (libraryFromPref != null) {
                if (libraryFromPref.getDescription() != null) {
                    pomSetting.setDescription(libraryFromPref.getDescription());
                }
                PomWriter pomWriter = new PomWriter();
                String cacheTargetDir = metaDataGenerationInput
                        .getLibraryCacheRootPath()
                        + File.separator
                        + libraryFromPref.getName();
                if (new File(cacheTargetDir).exists()) {
                    // Directory because there it is assumed that the cache
                    // information is successfully created
                    // (If the information such as the jar does not exist,
                    // creates the cache does not exist,
                    // the directory may not be created)
                    existAvailableLibrary = true;
                    String createPomFile = cacheTargetDir + File.separator
                            + "pom.xml";
                    pomWriter.setPomSetting(pomSetting);
                    try {
                        pomWriter.write(createPomFile,
                                libraryFromPref.getUrl(),
                                libraryFromPref.isMavenType(),
                                libraryFromPref.isPluginEmbedded());
                    } catch (IOException e) {
                        throw new WorkbenchException(e.getMessage(),
                                e.getCause());
                    }
                    libraryFromPref.setCache(true);
                }
            }
        }
        if (!existAvailableLibrary) {
            // If a valid cache did not exist even one, to interrupt the
            // process.
            Exception exception = new IllegalArgumentException(
                    Activator
                            .getResourceString(LibraryMetaInfoGenerateJob.class
                                    .getName() + ".err.msg.AvailableLibraryErr"));
            throw new WorkbenchException(exception.getMessage());
        }
        Activator.saveLibraryRepositories();
    }

    /**
     * Get a library repository information.<br/>
     * 
     * @param name
     *            Library name
     * @return Library repository information
     * @throws WorkbenchException
     *             Throw if it fails to get the information repository.
     */
    private LibraryRepository getLibraryFromPref(String name)
            throws WorkbenchException {
        return Activator.getLibraryRepositories().get(name);
    }

    /**
     * Delete the cache directory.<br/>
     * 
     * @param cacheRootDir
     *            Root directory of the cache
     * @param libraryModel
     *            Model that contains basic information about the library
     * @throws IOException
     *             It is to Throw If the deletion fails.
     */
    private void deleteCacheDir(String cacheRootDir, LibraryModel libraryModel)
            throws IOException {
        File delTarget = new File(cacheRootDir, libraryModel.getLibName());
        FileVisitor.walkFileTree(delTarget, new FileVisitor() {
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
    }

}
