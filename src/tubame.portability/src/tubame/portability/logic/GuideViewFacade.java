/*
 * GuideViewFacade.java
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
package tubame.portability.logic;

import java.io.File;
import java.io.IOException;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.plugin.view.HtmlGuideView;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Only if the HTML guide is present in a specific directory, <br/>
 * guide chapter number after clicking View guidelines HTML.<br/>
 * Make the page jump to guide chapter number HTML display, is clicked.<br/>
 * 
 */
public class GuideViewFacade {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(GuideViewFacade.class);

    /**
     * Guide HTML file path
     */
    private static final String GUIDE_FILE_PATH = ApplicationPropertyUtil.GUIDE_FILE_PATH;

    /**
     * Constructor.<br/>
     * It can not be instantiated from another class.<br/>
     * 
     */
    private GuideViewFacade() {
        // no operation
    }

    /**
     * Generate an HTML file guide URLPath.<br/>
     * 
     * @param guideFilePath
     *            Guide HTML file full path
     * @param guideNo
     *            Hyperlinked
     * @return HTML guide path
     */
    private static String createUrlPath(String guideFilePath, String guideNo) {
        StringBuilder sb = new StringBuilder();
        sb.append("file://"); //$NON-NLS-1$
        sb.append(guideFilePath);
        sb.append("#tubame_"); //$NON-NLS-1$
        sb.append(guideNo);
        if (!guideNo.endsWith(StringUtil.PERIOD)) {
            sb.append(StringUtil.PERIOD);
        }
        return sb.toString();
    }

    /**
     * If the guide HTML file exists, it displays a guide HTML.<br/>
     * 
     * @param guideNo
     *            No guide
     * @throws IOException
     *             IO exception
     */
    public static void view(String guideNo) throws IOException {
        // Guide chapter numbers get chosen
        String url = GuideViewFacade.createUrlPath(PluginUtil.getPluginDir()
                + GuideViewFacade.GUIDE_FILE_PATH, guideNo);
        if (new File(PluginUtil.getPluginDir()
                + GuideViewFacade.GUIDE_FILE_PATH).exists()) {
            try {
                IWorkbenchPage workBenchPage = PluginUtil.getActivePage();
                HtmlGuideView viewPart = (HtmlGuideView) workBenchPage
                        .showView(PluginUtil.getHtmlViewId());
                viewPart.setURL(url);
            } catch (PartInitException e) {
                JbmException.outputExceptionLog(e, LOGGER, ERROR_LEVEL.ERROR,
                        new String[] { MessageUtil.ERR_GUID_VIEW });
            }
        }
    }
}
