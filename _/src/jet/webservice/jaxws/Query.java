
package jet.webservice.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "query", namespace = "http://webservice.jet/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "query", namespace = "http://webservice.jet/")
public class Query {

    @XmlElement(name = "arg0", namespace = "")
    private jet.webservice.Request arg0;

    /**
     * 
     * @return
     *     returns Request
     */
    public jet.webservice.Request getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(jet.webservice.Request arg0) {
        this.arg0 = arg0;
    }

}
