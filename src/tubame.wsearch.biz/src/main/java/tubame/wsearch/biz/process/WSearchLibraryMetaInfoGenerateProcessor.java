/*
 * WSearchLibraryMetaInfoGenerateProcessor.java
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
package tubame.wsearch.biz.process;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.wsearch.biz.WSPhaseService;
import tubame.wsearch.biz.WSPhaseService.TYPE;
import tubame.wsearch.biz.cache.CacheBase;
import tubame.wsearch.biz.cache.WSearchCacheManager;
import tubame.wsearch.biz.cache.WSearchLibraryCacheArgument;
import tubame.wsearch.biz.comparer.ComparerManager;
import tubame.wsearch.biz.ex.WSearchBizException;
import tubame.wsearch.biz.model.AbstractWSPhaseTask;
import tubame.wsearch.biz.model.LibraryMetaDataGenerationInput;
import tubame.wsearch.biz.model.LibraryModel;
import tubame.wsearch.biz.model.PhaseCountDownLatch;
import tubame.wsearch.biz.model.PhaseInbound;
import tubame.wsearch.biz.model.PhaseOutbound;
import tubame.wsearch.biz.model.LibraryModel.LIBRARY_TYPE;
import tubame.wsearch.biz.process.obsvr.AbstractProcessObserver;
import tubame.wsearch.biz.util.FileVisitor;
import tubame.wsearch.biz.util.ZipUtil;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * It is a process that generates class porting, the porting based library
 * information.<br/>
 */
public class WSearchLibraryMetaInfoGenerateProcessor extends
        AbstractProcessor<LibraryMetaDataGenerationInput, String> {

    /**
     * Task to be processed
     */
    private AbstractWSPhaseTask libraryMetaInfoGenerateTask;

    /**
     * Processed map of each library
     */
    private Map<String, Map<File, File>> targetMaps;

    /**
     * Input value
     */
    private PhaseInbound<LibraryMetaDataGenerationInput> inbound;

    /**
     * Format of the date
     */
    private SimpleDateFormat dateFormat;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WSearchLibraryMetaInfoGenerateProcessor.class);

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param threadCnt
     *            Number of processed
     * @param starter
     *            Latch to support the synchronization of the initial processing
     * @param countDownLatch
     *            Latch to support synchronization
     */
    public WSearchLibraryMetaInfoGenerateProcessor(int threadCnt,
            CountDownLatch starter, PhaseCountDownLatch countDownLatch) {
        super(threadCnt);
        dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        libraryMetaInfoGenerateTask = new LibraryMetaInfoGenerateTask(starter,
                countDownLatch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String process(
            PhaseInbound<LibraryMetaDataGenerationInput> phaseInbound) {
        this.inbound = phaseInbound;
        executor.submit(libraryMetaInfoGenerateTask);
        return null;
    }

    /**
     * It is the task class that generates porting, the porting based library
     * information.<br/>
     */
    class LibraryMetaInfoGenerateTask extends AbstractWSPhaseTask {

        /**
         * Constructor.<br/>
         * No operation.<br/>
         * 
         * @param starter
         *            Latch to support the synchronization of the initial
         *            processing
         * @param countDownLatch
         *            Latch to support synchronization
         */
        public LibraryMetaInfoGenerateTask(CountDownLatch starter,
                PhaseCountDownLatch countDownLatch) {
            super(WSPhaseService.TYPE.DESTLIB_METADATA_GENERATE, starter,
                    countDownLatch);
        }

        /**
         * {@inheritDoc}
         */
        public Object call() {
            LOGGER.info(MessageUtil
                    .getResourceString("debug.msg.start.process")
                    + this.getClass().getName() + "#call()");
            try {
                targetMaps = new HashMap<String, Map<File, File>>();
                int targetCount = 0;
                LibraryMetaDataGenerationInput input = inbound.getValue();
                List<LibraryModel> libraries = input.getLibraryModels();
                for (LibraryModel library : libraries) {
                    WSearchLibraryCacheArgument argument = new WSearchLibraryCacheArgument(
                            CacheBase.TYPE.LIBRARY,
                            input.getLibraryCacheRootPath(), library);
                    if (input.isOverwrite()
                            || !WSearchCacheManager.getInstance().exist(
                                    argument, CacheBase.TYPE.LIBRARY)) {
                        File target = new File(library.getPath());
                        library.setUnziped(false);
                        File libraryPath = new File(
                                input.getLibraryCacheRootPath(),
                                library.getLibName());
                        if (libraryPath.exists()) {
                            FileVisitor.walkFileTree(libraryPath,
                                    new FileVisitor() {
                                        @Override
                                        public FileVisitResult visitFile(
                                                File file) {
                                            file.delete();
                                            return FileVisitResult.CONTINUE;
                                        }

                                        @Override
                                        public FileVisitResult postVisitDirectory(
                                                File dir) throws IOException {
                                            dir.delete();
                                            return FileVisitResult.CONTINUE;
                                        }
                                    });
                        }

                        if (library.isCache()) {
                            File cacheDir = new File(
                                    input.getLibraryCacheRootPath());
                            ZipUtil.unzip(target, cacheDir);
                            continue;
                        }

                        String fileName = target.getName();
                        try {
                            // Unzip If the path that was passed was not a
                            // directory and so should be zip.
                            if (!target.isDirectory()) {
                                String tempName = library.getLibName()
                                        + dateFormat.format(Calendar
                                                .getInstance().getTime());
                                File tempDir = new File(
                                        input.getLibraryCacheTempPath(),
                                        tempName);
                                ZipUtil.unzip(target, tempDir);
                                library.setUnziped(true);
                                library.setPath(tempDir.getAbsolutePath());
                            }

                            targetMaps.put(library.getLibName(),
                                    scanDir(target, library.getType()));
                            targetCount += targetMaps.get(library.getLibName())
                                    .size();
                        } catch (IOException e) {
                            // Because of the progress impossible exception,
                            // cancellation process.
                            LOGGER.error(
                                    MessageUtil.getResourceString(this
                                            .getClass().getName()
                                            + ".exception.unzip"), e);
                            AbstractWSPhaseTask.CurrentStatus status = new AbstractWSPhaseTask.CurrentStatus(
                                    AbstractWSPhaseTask.STATUS.ERR_EXIT,
                                    fileName, null);
                            status.setException(e);
                            this.listener.update(null, status);
                            this.listener.execptionHandle(e);
                            cancel();
                            while (this.getCountDownLatch().getCount() > 0) {
                                this.getCountDownLatch().countDown();
                            }
                            if (this.starter != null) {
                                while (this.starter.getCount() > 0) {
                                    starter.countDown();
                                }
                            }
                            return null;
                        }
                    }

                    if (this.listener.isCanceled()) {
                        cancel();
                        while (this.getCountDownLatch().getCount() > 0) {
                            this.getCountDownLatch().countDown();
                        }
                        if (this.starter != null) {
                            while (this.starter.getCount() > 0) {
                                starter.countDown();
                            }
                        }
                        return null;
                    }
                }

                if (this.starter != null) {
                    this.listener.update(null,
                            new AbstractWSPhaseTask.CurrentStatus(
                                    AbstractWSPhaseTask.STATUS.INIT,
                                    targetCount));
                    this.starter.countDown();
                }

                if (targetCount > 0) {
                    for (LibraryModel library : libraries) {
                        Map<File, File> targetMap = targetMaps.get(library
                                .getLibName());
                        if (targetMap == null) {
                            this.getCountDownLatch().countDown();
                            continue;
                        }
                        File target = new File(library.getPath());
                        if (library.getType() == LIBRARY_TYPE.MAVEN) {
                            for (Map.Entry<File, File> entry : targetMap
                                    .entrySet()) {
                                listener.update(null,
                                        new AbstractWSPhaseTask.CurrentStatus(
                                                STATUS.START, entry.getKey()
                                                        .toString(), null));
                                library.setCurrentPom(entry.getValue());
                                library.setCurrentTarget(entry.getKey());
                                library.setTargetDirectory(target
                                        .toURI()
                                        .relativize(
                                                entry.getKey().getParentFile()
                                                        .toURI()).getPath());
                                input.setCurrentLibrary(library);
                                PhaseOutbound result = coreService
                                        .execute(inbound);
                                AbstractWSPhaseTask.CurrentStatus status = new AbstractWSPhaseTask.CurrentStatus(
                                        ((STATUS) result.getValue()), entry
                                                .getKey().toString(), null);
                                if (STATUS.ERR_EXIT.equals(result.getValue())) {
                                    status.setException(new WSearchBizException(
                                            getType(),
                                            MessageUtil
                                                    .getResourceString(WSearchLibraryMetaInfoGenerateProcessor.class
                                                            .getName()
                                                            + ".err.execute")));
                                    listener.update(null, status);
                                    cancel();
                                    while (this.getCountDownLatch().getCount() > 0) {
                                        this.getCountDownLatch().countDown();
                                    }
                                    return null;
                                }
                                listener.update(null, status);
                                if (this.listener.isCanceled()) {
                                    cancel();
                                    while (this.getCountDownLatch().getCount() > 0) {
                                        this.getCountDownLatch().countDown();
                                    }
                                    return null;
                                }
                            }
                        } else {
                            for (File dir : targetMap.keySet()) {
                                listener.update(null,
                                        new AbstractWSPhaseTask.CurrentStatus(
                                                STATUS.START, dir.toString(),
                                                null));
                                library.setCurrentPom(null);
                                library.setCurrentTarget(dir);
                                library.setTargetDirectory(target.toURI()
                                        .relativize(dir.toURI()).getPath());
                                input.setCurrentLibrary(library);
                                PhaseOutbound result = coreService
                                        .execute(inbound);
                                listener.update(null,
                                        new AbstractWSPhaseTask.CurrentStatus(
                                                ((STATUS) result.getValue()),
                                                dir.toString(), null));
                                if (result.getValue() == null) {
                                    cancel();
                                    while (this.getCountDownLatch().getCount() > 0) {
                                        this.getCountDownLatch().countDown();
                                    }
                                    return null;
                                }
                                if (this.listener.isCanceled()) {
                                    cancel();
                                    while (this.getCountDownLatch().getCount() > 0) {
                                        this.getCountDownLatch().countDown();
                                    }
                                    return null;
                                }
                            }
                        }

                        if (library.isUnziped()) {
                            try {
                                cleanTemporary(target);
                            } catch (IOException e) {
                                // Because of the progress possible exception,
                                // to continue to notify the observer to record
                                // in the log.
                                LOGGER.error(MessageUtil.getResourceString(this
                                        .getClass().getName()
                                        + ".exception.clean"), e);
                                listener.update(null,
                                        new AbstractWSPhaseTask.CurrentStatus(
                                                STATUS.ERR_CONTINUE, target
                                                        .getName().toString(),
                                                null));
                            }
                        }

                        this.getCountDownLatch().countDown();

                        if (this.listener.isCanceled()) {
                            cancel();
                            while (this.getCountDownLatch().getCount() > 0) {
                                this.getCountDownLatch().countDown();
                            }
                            return null;
                        }
                    }
                } else {
                    while (this.getCountDownLatch().getCount() > 1) {
                        this.getCountDownLatch().countDown();
                    }
                }

                if (this.listener.isCanceled()) {
                    cancel();
                    while (this.getCountDownLatch().getCount() > 0) {
                        this.getCountDownLatch().countDown();
                    }
                    return null;
                }

                for (LibraryModel library : libraries) {
                    WSearchLibraryCacheArgument argument = new WSearchLibraryCacheArgument(
                            CacheBase.TYPE.LIBRARY,
                            input.getLibraryCacheRootPath(), library);
                    try {
                        WSearchCacheManager.getInstance().load(
                                CacheBase.TYPE.LIBRARY, argument);
                    } catch (WSearchBizException e) {
                        // Because of the progress impossible exception,
                        // cancellation process.
                        LOGGER.error(
                                MessageUtil.getResourceString(this.getClass()
                                        .getName() + ".exception.load"), e);
                        AbstractWSPhaseTask.CurrentStatus status = new AbstractWSPhaseTask.CurrentStatus(
                                AbstractWSPhaseTask.STATUS.ERR_EXIT,
                                library.getLibName(), null);
                        status.setException(e);
                        this.listener.update(null, status);
                        cancel();
                        while (this.getCountDownLatch().getCount() > 0) {
                            this.getCountDownLatch().countDown();
                        }
                        return null;
                    }

                    if (this.listener.isCanceled()) {
                        cancel();
                        while (this.getCountDownLatch().getCount() > 0) {
                            this.getCountDownLatch().countDown();
                        }
                        return null;
                    }
                }
                // It should initialize the Konpeara After loading the cache.
                ComparerManager.getInstance().initialize();

                this.getCountDownLatch().countDown();

                LOGGER.info(MessageUtil
                        .getResourceString("debug.msg.end.process")
                        + this.getClass().getName() + "#call()");
                return null;
            } catch (Exception e) {
                // Catch here all if an exception such as RuntimeExceptio
                // occurs.
                LOGGER.error(
                        MessageUtil.getResourceString(this.getClass().getName()
                                + ".exception.load"), e);
                this.listener.execptionHandle(e);
                cancel();
                while (this.getCountDownLatch().getCount() > 0) {
                    this.getCountDownLatch().countDown();
                }
                if (this.starter != null) {
                    while (this.starter.getCount() > 0) {
                        starter.countDown();
                    }
                }
                return null;
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TYPE getType() {
        return this.libraryMetaInfoGenerateTask.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObserver(AbstractProcessObserver observer) {
        this.libraryMetaInfoGenerateTask.setListener(observer);

    }

    /**
     * It scans the directory to be processed, to detect and directories jar to
     * be processed concrete.<br/>
     * 
     * @param target
     *            Processing target directory
     * @param type
     *            Whether Maven library
     * @return Map of processed concrete
     * @throws IOException
     *             If you fail to scan the directory
     */
    private Map<File, File> scanDir(File target, LIBRARY_TYPE type)
            throws IOException {
        final Map<File, File> targetMap = new HashMap<File, File>();
        if (type == LIBRARY_TYPE.MAVEN) {
            FileVisitor.walkFileTree(target, new FileVisitor() {
                @Override
                public FileVisitResult visitFile(File file) {
                    String fileName = file.getName();
                    int dotIndex = fileName.lastIndexOf(".");
                    if (dotIndex >= 0) {
                        String extension = fileName.substring(dotIndex + 1);
                        if ("pom".equals(extension)) {
                            String jarName = fileName.substring(0, dotIndex)
                                    + ".jar";
                            File jar = new File(file.getParentFile(), jarName);
                            if (jar.exists()) {
                                targetMap.put(jar, file);
                            } else {
                                jarName = fileName.substring(0, dotIndex)
                                        + ".jar.pack";
                                jar = new File(file.getParentFile(), jarName);
                                if (jar.exists()) {
                                    targetMap.put(jar, file);
                                }
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            FileVisitor.walkFileTree(target, new FileVisitor() {
                @Override
                public FileVisitResult preVisitDirectory(File dir) {
                    targetMap.put(dir, null);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return targetMap;
    }

    /**
     * Delete the extracted files to a temporary directory.<br/>
     * 
     * @param target
     *            Directory to be deleted
     * @throws IOException
     *             If fail to remove
     */
    private void cleanTemporary(File target) throws IOException {
        FileVisitor.walkFileTree(target, new FileVisitor() {
            @Override
            public FileVisitResult visitFile(File file) {
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
