
package tubame.knowhow.biz.model.generated.knowhow;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChildChapter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChildChapter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ChildChapterNo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ChapterCategoryRefKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ChildChapter" type="{http://generated.model.biz.knowhow.tubame/knowhow}ChildChapter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChildChapter", propOrder = {
    "childChapterNo",
    "chapterCategoryRefKey",
    "childChapters"
})
public class ChildChapter {

    @XmlElement(name = "ChildChapterNo", required = true)
    protected String childChapterNo;
    @XmlElement(name = "ChapterCategoryRefKey", required = true)
    protected String chapterCategoryRefKey;
    @XmlElement(name = "ChildChapter")
    protected List<ChildChapter> childChapters;

    /**
     * Gets the value of the childChapterNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildChapterNo() {
        return childChapterNo;
    }

    /**
     * Sets the value of the childChapterNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildChapterNo(String value) {
        this.childChapterNo = value;
    }

    /**
     * Gets the value of the chapterCategoryRefKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChapterCategoryRefKey() {
        return chapterCategoryRefKey;
    }

    /**
     * Sets the value of the chapterCategoryRefKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChapterCategoryRefKey(String value) {
        this.chapterCategoryRefKey = value;
    }

    /**
     * Gets the value of the childChapters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the childChapters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildChapters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChildChapter }
     * 
     * 
     */
    public List<ChildChapter> getChildChapters() {
        if (childChapters == null) {
            childChapters = new ArrayList<ChildChapter>();
        }
        return this.childChapters;
    }

}
