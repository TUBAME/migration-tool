package struts1tospringmvc.tag.html.struts1.action.model;

/**
 * @author tanabe
 */
public class OtherMultiboxBean {

    private String otherMultiboxProperty;

    public OtherMultiboxBean() {}

    public OtherMultiboxBean(String otherMultiboxProperty) {
       this.otherMultiboxProperty = otherMultiboxProperty;
    }

  public String getOtherMultiboxProperty() {
        return otherMultiboxProperty;
    }

    public void setOtherMultiboxProperty(String otherMultiboxProperty) {
        this.otherMultiboxProperty = otherMultiboxProperty;
    }

}