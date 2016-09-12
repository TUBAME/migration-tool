package struts1tospringmvc.tag.html.struts1.action.form;

import org.apache.struts.action.ActionForm;

/**
 * @author tanabe
 */
public class HelloWorldForm extends ActionForm {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}