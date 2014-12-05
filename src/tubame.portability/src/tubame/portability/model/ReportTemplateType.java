package tubame.portability.model;

import tubame.portability.util.resource.MessageUtil;

public enum ReportTemplateType {

	APSERVER("ap",MessageUtil.REPORT_TPL_APSERVER),
	
	MVCFRAMWORK("mvc",MessageUtil.REPORT_TPL_MVCFRAMWORK),
	
	STRUTSFRAMWORK("struts",MessageUtil.REPORT_TPL_STRUTSFRAMWORK);
	
	
    /** Member variable of type String */
    private final String value;
    
	private String id;
    
    /**
     * Constructor.<br/>
     * 
     * @param v
     *            Enumeration data string
     */
    ReportTemplateType(String id,String val) {
        this.value = val;
        this.id = id;
    }

	public String getValue() {
		return value;
	}

	public String getId() {
		return id;
	}
    
    
}
