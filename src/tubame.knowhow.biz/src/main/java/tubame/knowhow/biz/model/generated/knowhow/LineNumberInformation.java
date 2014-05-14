
package tubame.knowhow.biz.model.generated.knowhow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LineNumberInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LineNumberInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LineNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LineNumberContents" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Investigation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineNumberInformation", propOrder = {
    "lineNumber",
    "lineNumberContents",
    "investigation"
})
public class LineNumberInformation {

    @XmlElement(name = "LineNumber", required = true)
    protected String lineNumber;
    @XmlElement(name = "LineNumberContents", required = true)
    protected String lineNumberContents;
    @XmlElement(name = "Investigation", required = true)
    protected String investigation;

    /**
     * Gets the value of the lineNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the value of the lineNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineNumber(String value) {
        this.lineNumber = value;
    }

    /**
     * Gets the value of the lineNumberContents property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineNumberContents() {
        return lineNumberContents;
    }

    /**
     * Sets the value of the lineNumberContents property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineNumberContents(String value) {
        this.lineNumberContents = value;
    }

    /**
     * Gets the value of the investigation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvestigation() {
        return investigation;
    }

    /**
     * Sets the value of the investigation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvestigation(String value) {
        this.investigation = value;
    }

}
