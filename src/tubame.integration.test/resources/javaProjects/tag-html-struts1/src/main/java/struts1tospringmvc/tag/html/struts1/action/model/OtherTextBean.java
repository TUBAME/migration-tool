package struts1tospringmvc.tag.html.struts1.action.model;

/**
 * @author tanabe
 */
public class OtherTextBean {

    private String otherTextProperty;

    public OtherTextBean() {}

    public OtherTextBean(String otherTextProperty) {
        this.otherTextProperty = otherTextProperty;
    }

    public String getOtherTextProperty() {
        return otherTextProperty;
    }

    public void setOtherTextProperty(String otherTextProperty) {
        this.otherTextProperty = otherTextProperty;
    }

}