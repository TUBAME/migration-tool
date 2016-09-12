package struts1tospringmvc.tag.html.struts1.action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import struts1tospringmvc.tag.html.struts1.action.form.HiddenIndexedForm;
import struts1tospringmvc.tag.html.struts1.action.model.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanabe
 */
public class HiddenIndexedAction extends Action {

  public ActionForward execute(ActionMapping mapping, ActionForm form,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {


    HiddenIndexedForm hiddenIndexedForm = (HiddenIndexedForm) form;

    List<Product> products = new ArrayList<>();
    products.add(new Product(100, "product1"));
    products.add(new Product(200, "product2"));
    products.add(new Product(300, "product3"));
    hiddenIndexedForm.setProducts(products);

    return mapping.findForward("hidden-indexed");

  }

}
