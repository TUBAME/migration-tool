/*
 * InitializeMarshallerJob.java
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
package tubame.knowhow.plugin.ui.dialog;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.logic.KnowhowManagement;

/**
 * Class to do in the background initial processing.<br/>
 */
public class InitializeMarshallerJob extends Job {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(InitializeMarshallerJob.class);

    /**
     * Constructor.<br/>
     * 
     * @param title
     *            Title
     */
    public InitializeMarshallerJob(String title) {
        super(title);
        InitializeMarshallerJob.LOGGER.debug("[title]" + title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        InitializeMarshallerJob.LOGGER.debug("[monitor]" + monitor);
        monitor.beginTask(
                MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.EXECUTE_INITIALIZE_PORTABILITY_KNOWHOW),
                IProgressMonitor.UNKNOWN);
        // Cancellation process
        if (monitor.isCanceled()) {
            InitializeMarshallerJob.LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.PRESS_CANCEL_BUTTON));
            return Status.CANCEL_STATUS;
        }
        KnowhowManagement.inistalizeAwait();
        monitor.done();
        return Status.OK_STATUS;
    }
}
