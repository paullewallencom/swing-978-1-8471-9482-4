
package jet.webservice.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "queryResponse", namespace = "http://webservice.jet/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "queryResponse", namespace = "http://webservice.jet/")
public class QueryResponse {

    @XmlElement(name = "return", namespace = "")
    private jet.webservice.Response _return;

    /**
     * 
     * @return
     *     returns Response
     */
    public jet.webservice.Response get_return() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void set_return(jet.webservice.Response _return) {
        this._return = _return;
    }

}
