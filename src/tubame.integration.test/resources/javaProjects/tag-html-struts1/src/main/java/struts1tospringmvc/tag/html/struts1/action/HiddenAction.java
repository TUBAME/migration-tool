package struts1tospringmvc.tag.html.struts1.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import struts1tospringmvc.tag.html.struts1.action.model.OtherHiddenBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanabe
 */
public class HiddenAction extends Action {

  public ActionForward execute(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {

    OtherHiddenBean otherHiddenBean = new OtherHiddenBean();
    request.setAttribute("otherHiddenBean", otherHiddenBean);
    return mapping.findForward("hidden");

  }

}
