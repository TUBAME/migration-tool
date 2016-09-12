package struts1tospringmvc.tag.html.struts1.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tanabe
 */
public class MessagesResultAction extends Action {

  public ActionForward execute(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {

    String name = "Struts";

    ActionMessages messages = new ActionMessages();
    ActionMessage success1 = new ActionMessage("success.msg1", name);
    ActionMessage success2 = new ActionMessage("success.msg2");
    messages.add(ActionMessages.GLOBAL_MESSAGE, success1);
    messages.add(ActionMessages.GLOBAL_MESSAGE, success2);

    ActionMessage another = new ActionMessage("another.msg");
    messages.add("ANOTHER_MESSAGE", another);

    saveMessages(request, messages);

    ActionMessages errors = new ActionMessages();
    ActionMessage error1 = new ActionMessage("error.msg1", name);
    errors.add(ActionMessages.GLOBAL_MESSAGE, error1);
    saveErrors(request, errors);

    return mapping.findForward("messages-result");

  }

}
