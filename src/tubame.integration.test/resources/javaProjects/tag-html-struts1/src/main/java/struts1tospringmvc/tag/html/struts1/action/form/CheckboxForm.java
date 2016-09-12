package struts1tospringmvc.tag.html.struts1.action.form;

import org.apache.struts.action.ActionForm;

/**
 * @author tanabe
 */
public class CheckboxForm extends ActionForm {

  private boolean checked1 = true;
  private String checked2;
  private boolean checked3;
  private boolean checked4;
  private boolean checked5;

  public boolean isChecked1() {
    return checked1;
  }

  public void setChecked1(boolean checked1) {
    this.checked1 = checked1;
  }

  public String getChecked2() {
    return checked2;
  }

  public void setChecked2(String checked2) {
    this.checked2 = checked2;
  }

  public boolean isChecked3() {
    return checked3;
  }

  public void setChecked3(boolean checked3) {
    this.checked3 = checked3;
  }

  public boolean isChecked4() {
    return checked4;
  }

  public void setChecked4(boolean checked4) {
    this.checked4 = checked4;
  }

  public boolean isChecked5() {
    return checked5;
  }

  public void setChecked5(boolean checked5) {
    this.checked5 = checked5;
  }
}