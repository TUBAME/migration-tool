package struts1tospringmvc.tag.html.struts1.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import struts1tospringmvc.tag.html.struts1.action.model.DynamicOption;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanabe
 */
public class SelectAction extends Action {

  public ActionForward execute(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {

    List<DynamicOption> options = new ArrayList<>();
    options.add(new DynamicOption("DynamicOption1", "dynamic-option1"));
    options.add(new DynamicOption("DynamicOption2", "dynamic-option2"));
    options.add(new DynamicOption("DynamicOption3", "dynamic-option3"));

    request.setAttribute("options", options);

    return mapping.findForward("select");

  }

}
