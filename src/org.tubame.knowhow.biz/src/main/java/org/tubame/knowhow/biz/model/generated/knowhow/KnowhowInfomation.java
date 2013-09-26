
package org.tubame.knowhow.biz.model.generated.knowhow;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KnowhowInfomation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KnowhowInfomation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="KnowhowNo" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="KnowhowName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CheckItem" type="{http://generated.model.biz.knowhow.tubame.org/knowhow}CheckItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="knowhowId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="knowhowDetailRefKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KnowhowInfomation", propOrder = {
    "knowhowNo",
    "knowhowName",
    "checkItems"
})
public class KnowhowInfomation {

    @XmlElement(name = "KnowhowNo", required = true)
    protected BigInteger knowhowNo;
    @XmlElement(name = "KnowhowName", required = true)
    protected String knowhowName;
    @XmlElement(name = "CheckItem")
    protected List<CheckItem> checkItems;
    @XmlAttribute(name = "knowhowId", required = true)
    protected String knowhowId;
    @XmlAttribute(name = "knowhowDetailRefKey")
    protected String knowhowDetailRefKey;

    /**
     * Gets the value of the knowhowNo property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getKnowhowNo() {
        return knowhowNo;
    }

    /**
     * Sets the value of the knowhowNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setKnowhowNo(BigInteger value) {
        this.knowhowNo = value;
    }

    /**
     * Gets the value of the knowhowName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKnowhowName() {
        return knowhowName;
    }

    /**
     * Sets the value of the knowhowName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKnowhowName(String value) {
        this.knowhowName = value;
    }

    /**
     * Gets the value of the checkItems property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the checkItems property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCheckItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CheckItem }
     * 
     * 
     */
    public List<CheckItem> getCheckItems() {
        if (checkItems == null) {
            checkItems = new ArrayList<CheckItem>();
        }
        return this.checkItems;
    }

    /**
     * Gets the value of the knowhowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKnowhowId() {
        return knowhowId;
    }

    /**
     * Sets the value of the knowhowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKnowhowId(String value) {
        this.knowhowId = value;
    }

    /**
     * Gets the value of the knowhowDetailRefKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKnowhowDetailRefKey() {
        return knowhowDetailRefKey;
    }

    /**
     * Sets the value of the knowhowDetailRefKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKnowhowDetailRefKey(String value) {
        this.knowhowDetailRefKey = value;
    }

}
