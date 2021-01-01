package jet.webservice;

import org.grandtestauto.*;

/**
 * Example from Chapter 17.
 */
@DoesNotNeedTest( reason="Just an example - see the function test." )
public class Response {
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }
}
