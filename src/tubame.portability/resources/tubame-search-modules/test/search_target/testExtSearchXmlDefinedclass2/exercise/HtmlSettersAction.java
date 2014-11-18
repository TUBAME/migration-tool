/*
 * $Id: HtmlSettersAction.java 421488 2006-07-13 03:43:08Z wsmoak $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.struts.webapp.exercise;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * Do-nothing action that accepts the changes made automatically in our form
 * bean, and then returns control to the input form (if "Save" was pressed)
 * or the main menu (if "Cancel" was pressed).
 *
 * @version $Rev: 421488 $ $Date: 2006-07-12 22:43:08 -0500 (Wed, 12 Jul 2006) $
 */

public class HtmlSettersAction extends Action {


    /**
     * Forward to the input form if "Save" was pressed or the main menu
     * if "Cancel" was pressed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception Exception if business logic throws an exception
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws Exception {

        if (isCancelled(request))
            return (mapping.findForward("redirect-default"));
        else
            return (mapping.findForward("input"));

    }


}
