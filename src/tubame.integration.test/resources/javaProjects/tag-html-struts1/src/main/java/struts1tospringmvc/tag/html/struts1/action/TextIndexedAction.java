package struts1tospringmvc.tag.html.struts1.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import struts1tospringmvc.tag.html.struts1.action.form.TextIndexedForm;
import struts1tospringmvc.tag.html.struts1.action.model.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @author tanabe
 */
public class TextIndexedAction extends Action {

  public ActionForward execute(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {

    TextIndexedForm textIndexedForm = (TextIndexedForm) form;

    List<Product> products = Arrays.asList(
      new Product(100, "product1", 1),
      new Product(200, "product2", 2),
      new Product(300, "product3", 3));
    textIndexedForm.setProducts(products);

    return mapping.findForward("text-indexed");

  }

}
