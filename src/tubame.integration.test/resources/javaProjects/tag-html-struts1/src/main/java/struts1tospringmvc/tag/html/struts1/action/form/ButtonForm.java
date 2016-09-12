package struts1tospringmvc.tag.html.struts1.action.form;

import org.apache.struts.action.ActionForm;

/**
 * @author tanabe
 */
public class ButtonForm extends ActionForm {

    private String buttonFromForm;

    public String getButtonFromForm() {
        return buttonFromForm;
    }

    public void setButtonFromForm(String buttonFromForm) {
        this.buttonFromForm = buttonFromForm;
    }
}