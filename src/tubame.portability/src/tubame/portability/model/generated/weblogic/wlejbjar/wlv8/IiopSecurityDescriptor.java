//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.04.17 at 10:51:37 AM JST 
//

package tubame.portability.model.generated.weblogic.wlejbjar.wlv8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "transportRequirements",
        "clientAuthentication", "identityAssertion" })
@XmlRootElement(name = "iiop-security-descriptor")
public class IiopSecurityDescriptor {

    @XmlElement(name = "transport-requirements")
    protected TransportRequirements transportRequirements;
    @XmlElement(name = "client-authentication")
    protected String clientAuthentication;
    @XmlElement(name = "identity-assertion")
    protected String identityAssertion;

    /**
     * Gets the value of the transportRequirements property.
     * 
     * @return possible object is {@link TransportRequirements }
     * 
     */
    public TransportRequirements getTransportRequirements() {
        return transportRequirements;
    }

    /**
     * Sets the value of the transportRequirements property.
     * 
     * @param value
     *            allowed object is {@link TransportRequirements }
     * 
     */
    public void setTransportRequirements(TransportRequirements value) {
        this.transportRequirements = value;
    }

    /**
     * Gets the value of the clientAuthentication property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getClientAuthentication() {
        return clientAuthentication;
    }

    /**
     * Sets the value of the clientAuthentication property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setClientAuthentication(String value) {
        this.clientAuthentication = value;
    }

    /**
     * Gets the value of the identityAssertion property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getIdentityAssertion() {
        return identityAssertion;
    }

    /**
     * Sets the value of the identityAssertion property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setIdentityAssertion(String value) {
        this.identityAssertion = value;
    }

}