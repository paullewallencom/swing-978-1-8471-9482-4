package jet.webservice;

import org.grandtestauto.*;

/**
 * Example from Chapter 17.
 */
@DoesNotNeedTest( reason="Just an example - see the function test." )
public class Request {
    String name;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }
}
