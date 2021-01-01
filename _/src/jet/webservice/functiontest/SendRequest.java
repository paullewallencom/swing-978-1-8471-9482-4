package jet.webservice.functiontest;

import jet.testtools.*;
import jet.webservice.Hello;
import jet.webservice.test.jaxws.*;
import jet.webservice.test.jaxws.Request;
import org.grandtestauto.*;

public class SendRequest implements AutoLoadTest {
    public boolean runTest() throws Exception {
        //1. Start the Hello service
        Hello hello = new Hello();

        //2. Construct a Request generated from the WSDL.
        jet.webservice.test.jaxws.Request request = new Request();

        //3. Set the name in the Request to be "John Smith"
        request.setName( "John Smith" );

        //4. Make a query on the service with this Request
        jet.webservice.test.jaxws.Hello proxyHello =
                new HelloService().getHelloPort();
        jet.webservice.test.jaxws.Response response =
                proxyHello.query( request );

        //5. Check that the message in the
        //Response is "Hello John Smith"
        Assert.equal( response.getMessage(), "Hello John Smith" );

        //Cleanup.
        hello.stop();
        return true;
    }
}
