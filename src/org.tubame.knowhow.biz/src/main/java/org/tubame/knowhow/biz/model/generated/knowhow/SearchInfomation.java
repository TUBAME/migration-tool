
package org.tubame.knowhow.biz.model.generated.knowhow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchInfomation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchInfomation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FileType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SearchKey1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SearchKey2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PythonModule" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LineNumberInfomation" type="{http://generated.model.biz.knowhow.tubame.org/knowhow}LineNumberInfomation"/>
 *         &lt;element name="Appropriate" type="{http://generated.model.biz.knowhow.tubame.org/knowhow}Appropriate"/>
 *       &lt;/sequence>
 *       &lt;attribute name="searchInfoId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchInfomation", propOrder = {
    "fileType",
    "searchKey1",
    "searchKey2",
    "pythonModule",
    "lineNumberInfomation",
    "appropriate"
})
public class SearchInfomation {

    @XmlElement(name = "FileType", required = true)
    protected String fileType;
    @XmlElement(name = "SearchKey1", required = true)
    protected String searchKey1;
    @XmlElement(name = "SearchKey2", required = true)
    protected String searchKey2;
    @XmlElement(name = "PythonModule", required = true)
    protected String pythonModule;
    @XmlElement(name = "LineNumberInfomation", required = true)
    protected LineNumberInfomation lineNumberInfomation;
    @XmlElement(name = "Appropriate", required = true)
    protected Appropriate appropriate;
    @XmlAttribute(name = "searchInfoId", required = true)
    protected String searchInfoId;

    /**
     * Gets the value of the fileType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Sets the value of the fileType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileType(String value) {
        this.fileType = value;
    }

    /**
     * Gets the value of the searchKey1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchKey1() {
        return searchKey1;
    }

    /**
     * Sets the value of the searchKey1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchKey1(String value) {
        this.searchKey1 = value;
    }

    /**
     * Gets the value of the searchKey2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchKey2() {
        return searchKey2;
    }

    /**
     * Sets the value of the searchKey2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchKey2(String value) {
        this.searchKey2 = value;
    }

    /**
     * Gets the value of the pythonModule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPythonModule() {
        return pythonModule;
    }

    /**
     * Sets the value of the pythonModule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPythonModule(String value) {
        this.pythonModule = value;
    }

    /**
     * Gets the value of the lineNumberInfomation property.
     * 
     * @return
     *     possible object is
     *     {@link LineNumberInfomation }
     *     
     */
    public LineNumberInfomation getLineNumberInfomation() {
        return lineNumberInfomation;
    }

    /**
     * Sets the value of the lineNumberInfomation property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineNumberInfomation }
     *     
     */
    public void setLineNumberInfomation(LineNumberInfomation value) {
        this.lineNumberInfomation = value;
    }

    /**
     * Gets the value of the appropriate property.
     * 
     * @return
     *     possible object is
     *     {@link Appropriate }
     *     
     */
    public Appropriate getAppropriate() {
        return appropriate;
    }

    /**
     * Sets the value of the appropriate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Appropriate }
     *     
     */
    public void setAppropriate(Appropriate value) {
        this.appropriate = value;
    }

    /**
     * Gets the value of the searchInfoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchInfoId() {
        return searchInfoId;
    }

    /**
     * Sets the value of the searchInfoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchInfoId(String value) {
        this.searchInfoId = value;
    }

}
