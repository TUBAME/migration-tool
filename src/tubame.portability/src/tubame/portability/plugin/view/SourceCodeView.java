/*
 * HtmlGuideView.java
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
package tubame.portability.plugin.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import tubame.portability.util.PluginUtil;

/**
 * View guide display HTML view.<br/>
 * View the document at the supplied URL.<br/>
 */
public class SourceCodeView extends ViewPart {
    /**
     * Guide HTML browser
     */
    private Browser browser;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite arg0) {
        browser = new Browser(arg0, SWT.BORDER);
    }

    /**
     * Set a URL.<br/>
     * 
     * @param url
     *            URL to open
     */
    public void setURL(String url) {
        browser.setUrl(url);
        browser.setFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        // no operation
    }
    
    
    public static void view(String url){
    	IWorkbenchPage workBenchPage = PluginUtil.getActivePage();
        try {
        	SourceCodeView viewPart = (SourceCodeView) workBenchPage
			        .showView(PluginUtil.getSourceCodeViewId());
			viewPart.setURL(url);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
