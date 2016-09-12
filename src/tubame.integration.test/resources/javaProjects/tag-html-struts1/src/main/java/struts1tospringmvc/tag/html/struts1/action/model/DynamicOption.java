package struts1tospringmvc.tag.html.struts1.action.model;

/**
 * @author tanabe
 */
public class DynamicOption {

  private String label;
  private String value;

  public DynamicOption(String label, String value) {
    this.label = label;
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
