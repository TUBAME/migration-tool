
package tubame.knowhow.biz.model.generated.knowhow;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CheckItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CheckItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CheckItemNo" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="CheckItemName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SearchProcess" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PortabilityFactor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PortabilityDegree" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DegreeDetail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VisualConfirm" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HearingConfirm" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="checkItemId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="searchRefKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="searchExistance" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckItem", propOrder = {
    "checkItemNo",
    "checkItemName",
    "searchProcess",
    "portabilityFactor",
    "portabilityDegree",
    "degreeDetail",
    "visualConfirm",
    "hearingConfirm"
})
public class CheckItem {

    @XmlElement(name = "CheckItemNo", required = true)
    protected BigInteger checkItemNo;
    @XmlElement(name = "CheckItemName", required = true)
    protected String checkItemName;
    @XmlElement(name = "SearchProcess", required = true)
    protected String searchProcess;
    @XmlElement(name = "PortabilityFactor", required = true)
    protected String portabilityFactor;
    @XmlElement(name = "PortabilityDegree", required = true)
    protected String portabilityDegree;
    @XmlElement(name = "DegreeDetail", required = true)
    protected String degreeDetail;
    @XmlElement(name = "VisualConfirm", required = true)
    protected String visualConfirm;
    @XmlElement(name = "HearingConfirm", required = true)
    protected String hearingConfirm;
    @XmlAttribute(name = "checkItemId")
    protected String checkItemId;
    @XmlAttribute(name = "searchRefKey")
    protected String searchRefKey;
    @XmlAttribute(name = "searchExistance", required = true)
    protected boolean searchExistance;

    /**
     * Gets the value of the checkItemNo property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCheckItemNo() {
        return checkItemNo;
    }

    /**
     * Sets the value of the checkItemNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCheckItemNo(BigInteger value) {
        this.checkItemNo = value;
    }

    /**
     * Gets the value of the checkItemName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckItemName() {
        return checkItemName;
    }

    /**
     * Sets the value of the checkItemName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckItemName(String value) {
        this.checkItemName = value;
    }

    /**
     * Gets the value of the searchProcess property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchProcess() {
        return searchProcess;
    }

    /**
     * Sets the value of the searchProcess property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchProcess(String value) {
        this.searchProcess = value;
    }

    /**
     * Gets the value of the portabilityFactor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortabilityFactor() {
        return portabilityFactor;
    }

    /**
     * Sets the value of the portabilityFactor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortabilityFactor(String value) {
        this.portabilityFactor = value;
    }

    /**
     * Gets the value of the portabilityDegree property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortabilityDegree() {
        return portabilityDegree;
    }

    /**
     * Sets the value of the portabilityDegree property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortabilityDegree(String value) {
        this.portabilityDegree = value;
    }

    /**
     * Gets the value of the degreeDetail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDegreeDetail() {
        return degreeDetail;
    }

    /**
     * Sets the value of the degreeDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDegreeDetail(String value) {
        this.degreeDetail = value;
    }

    /**
     * Gets the value of the visualConfirm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisualConfirm() {
        return visualConfirm;
    }

    /**
     * Sets the value of the visualConfirm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisualConfirm(String value) {
        this.visualConfirm = value;
    }

    /**
     * Gets the value of the hearingConfirm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHearingConfirm() {
        return hearingConfirm;
    }

    /**
     * Sets the value of the hearingConfirm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHearingConfirm(String value) {
        this.hearingConfirm = value;
    }

    /**
     * Gets the value of the checkItemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheckItemId() {
        return checkItemId;
    }

    /**
     * Sets the value of the checkItemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheckItemId(String value) {
        this.checkItemId = value;
    }

    /**
     * Gets the value of the searchRefKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchRefKey() {
        return searchRefKey;
    }

    /**
     * Sets the value of the searchRefKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchRefKey(String value) {
        this.searchRefKey = value;
    }

    /**
     * Gets the value of the searchExistance property.
     * 
     */
    public boolean isSearchExistance() {
        return searchExistance;
    }

    /**
     * Sets the value of the searchExistance property.
     * 
     */
    public void setSearchExistance(boolean value) {
        this.searchExistance = value;
    }

}
