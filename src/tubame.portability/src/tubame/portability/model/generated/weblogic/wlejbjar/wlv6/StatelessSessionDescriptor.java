//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.04.17 at 10:51:36 AM JST 
//

package tubame.portability.model.generated.weblogic.wlejbjar.wlv6;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "pool", "statelessClustering" })
@XmlRootElement(name = "stateless-session-descriptor")
public class StatelessSessionDescriptor {

    protected Pool pool;
    @XmlElement(name = "stateless-clustering")
    protected StatelessClustering statelessClustering;

    /**
     * Gets the value of the pool property.
     * 
     * @return possible object is {@link Pool }
     * 
     */
    public Pool getPool() {
        return pool;
    }

    /**
     * Sets the value of the pool property.
     * 
     * @param value
     *            allowed object is {@link Pool }
     * 
     */
    public void setPool(Pool value) {
        this.pool = value;
    }

    /**
     * Gets the value of the statelessClustering property.
     * 
     * @return possible object is {@link StatelessClustering }
     * 
     */
    public StatelessClustering getStatelessClustering() {
        return statelessClustering;
    }

    /**
     * Sets the value of the statelessClustering property.
     * 
     * @param value
     *            allowed object is {@link StatelessClustering }
     * 
     */
    public void setStatelessClustering(StatelessClustering value) {
        this.statelessClustering = value;
    }

}