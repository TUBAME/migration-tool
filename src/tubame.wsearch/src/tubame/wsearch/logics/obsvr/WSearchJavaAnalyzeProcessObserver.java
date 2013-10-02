/*
 * WSearchJavaAnalyzeProcessObserver.java
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
package tubame.wsearch.logics.obsvr;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import tubame.wsearch.Activator;
import tubame.wsearch.biz.WSPhaseService.TYPE;
import tubame.wsearch.biz.comparer.ComparerManager;
import tubame.wsearch.biz.ex.WSearchBizException;
import tubame.wsearch.biz.model.PhaseCountDownLatch;
import tubame.wsearch.biz.process.AbstractProcessor;
import tubame.wsearch.logics.analyzer.JavaAnalyzer;

/**
 * Monitoring role of observer class comparison process and analyze Java.<br/>
 */
public class WSearchJavaAnalyzeProcessObserver extends WSearchProcessObserver {

    /**
     * Exception occurrence or non-occurrence
     */
    private boolean exceptionOccurred = false;

    /**
     * Constructor.<br/>
     * 
     */
    public WSearchJavaAnalyzeProcessObserver() {
        super();
    }

    /**
     * Constructor.<br/>
     * 
     * @param starter
     *            Latch countdown for the start
     * @param phaseCountDownLatch
     *            Countdown Latch of each phase processing
     * @param processor
     *            Processor
     * @param monitor
     *            Monitor progress
     */
    public WSearchJavaAnalyzeProcessObserver(CountDownLatch starter,
            PhaseCountDownLatch phaseCountDownLatch,
            AbstractProcessor<?, ?> processor, IProgressMonitor monitor) {
        super(starter, phaseCountDownLatch, processor, monitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logicPrepare(TYPE type, Object value)
            throws WSearchBizException {

        try {
            String path = (String) value;
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IPath location = Path
                    .fromOSString(new File(path).getAbsolutePath());
            IFile ifile = workspace.getRoot().getFileForLocation(location);

            if (this.ignoreList == null) {
                this.phaseCountDownLatch.setAnalyzer(new JavaAnalyzer(ifile));
            } else {
                this.phaseCountDownLatch.setAnalyzer(new JavaAnalyzer(ifile,
                        ignoreList));
            }

            this.phaseCountDownLatch.setComparer(ComparerManager.getInstance()
                    .getComparer(ComparerManager.COMPARER_TYPE.JAVA));
        } catch (Exception e) {
            throw new WSearchBizException(type, e.getMessage(), e.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execptionHandle(Exception e) {
        if (this.exceptionList == null) {
            this.exceptionList = new ArrayList<Throwable>();
        }
        this.exceptionList.add(e);
        Activator.log(e, Activator
                .getResourceString(WSearchJavaAnalyzeProcessObserver.class
                        .getName() + ".err.msg.ExceptionOccurred"));
        this.exceptionOccurred = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exceptionOccurred() {
        return this.exceptionOccurred;
    }
}
