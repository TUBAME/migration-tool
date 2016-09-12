package struts1tospringmvc.tag.html.struts1.action.form;

import org.apache.struts.action.ActionForm;
import struts1tospringmvc.tag.html.struts1.action.model.Product;

import java.util.List;

/**
 * @author tanabe
 */
public class HiddenIndexedForm extends ActionForm {

  private List<Product> products;

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }

  public Product get(int index) {
    return products.get(index);
  }

}
