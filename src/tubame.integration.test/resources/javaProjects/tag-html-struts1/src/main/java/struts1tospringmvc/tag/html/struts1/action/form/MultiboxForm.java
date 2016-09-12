package struts1tospringmvc.tag.html.struts1.action.form;

import org.apache.struts.action.ActionForm;

/**
 * @author tanabe
 */
public class MultiboxForm extends ActionForm {

  private String checked1;
  private String checked2;
  private String checked3;
  private String checked4;
  private String checked5;
  private String[] checked6 = new String[]{"hoge", "fuga"};

  public String getChecked1() {
    return checked1;
  }

  public void setChecked1(String checked1) {
    this.checked1 = checked1;
  }

  public String getChecked2() {
    return checked2;
  }

  public void setChecked2(String checked2) {
    this.checked2 = checked2;
  }

  public String getChecked3() {
    return checked3;
  }

  public void setChecked3(String checked3) {
    this.checked3 = checked3;
  }

  public String getChecked4() {
    return checked4;
  }

  public void setChecked4(String checked4) {
    this.checked4 = checked4;
  }

  public String getChecked5() {
    return checked5;
  }

  public void setChecked5(String checked5) {
    this.checked5 = checked5;
  }

  public String[] getChecked6() {
    return checked6;
  }

  public void setChecked6(String[] checked6) {
    this.checked6 = checked6;
  }

}
