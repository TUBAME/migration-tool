package struts1tospringmvc.tag.html.struts1.action.form;

import org.apache.struts.action.ActionForm;

/**
 * @author tanabe
 */
public class HiddenForm extends ActionForm {

    private String hidden1 = "hidden1-default";
    private String hidden2;

    public String getHidden1() {
        return hidden1;
    }

    public void setHidden1(String hidden1) {
        this.hidden1 = hidden1;
    }

    public String getHidden2() {
        return hidden2;
    }

    public void setHidden2(String hidden2) {
        this.hidden2 = hidden2;
    }

}