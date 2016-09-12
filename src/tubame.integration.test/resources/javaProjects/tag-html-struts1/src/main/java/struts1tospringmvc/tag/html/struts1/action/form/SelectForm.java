package struts1tospringmvc.tag.html.struts1.action.form;

import org.apache.struts.action.ActionForm;

/**
 * @author tanabe
 */
public class SelectForm extends ActionForm {

  private String option;

  public String getOption() {
    return option;
  }

  public void setOption(String option) {
    this.option = option;
  }
}
