package jet.webservice;

import org.grandtestauto.*;

import javax.jws.*;
import javax.xml.ws.*;
import java.io.*;
import static java.lang.Runtime.*;

/**
 * Example from Chapter 17.
 */
@WebService
@DoesNotNeedTest( reason="Just an example - see the function test." )
public class Hello {

    public static final String WEB_SERVICE_ADDRESS =
            "http://localhost:8090/Hello?WSDL";

    private static String classDir = "../classes";
    private static String srcDir = "../src";
    private static String jawsTestPackage = "jet.webservice.test.jaxws";

    private Endpoint endpoint;

    @WebMethod
    public Response query( Request request ) {
        Response result = new Response();
        result.setMessage( "Hello " + request.getName() );
        return result;
    }

    public Hello() {
        endpoint = Endpoint.publish( WEB_SERVICE_ADDRESS, this );
    }

    public void stop() {
        endpoint.stop();
    }

    public static void main( String[] args ) {
        Hello hello = new Hello();
        try {
            Process process = getRuntime().exec(
                    "wsimport -d " + classDir + " -s " + srcDir +
                            " -p " + jawsTestPackage +
                            " -verbose " + WEB_SERVICE_ADDRESS );
            showOutputFrom( process );
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        hello.stop();
    }

    private static void showOutputFrom( Process process )
            throws IOException {
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader( is ) );
        String line = reader.readLine();
        while (line != null) {
            System.out.println( line );
            line = reader.readLine();
        }
    }
}
                                                        