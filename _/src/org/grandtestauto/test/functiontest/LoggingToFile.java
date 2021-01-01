package org.grandtestauto.test.functiontest;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.util.*;

/**
 * See the GrandTestAuto test specification.
 */
public class LoggingToFile extends FTBase {
    public boolean runTest() throws IOException {
        ExceptionHandling.classNameToErrorKeyToThrow = new HashMap<String, String>();
        ExceptionHandling.classNameToErrorKeyToThrow.put( "a44.a.test.UnitTester", "NPE First error" );
        ExceptionHandling.classNameToErrorKeyToThrow.put( "a44.a.functiontest.FT", "NPE for function tests" );
        ExceptionHandling.classNameToErrorKeyToThrow.put( "a44.a.loadtest.LT", "NPE for load tests" );
        Helpers.setupForZip( new File( Grandtestauto.test44_zip ), true, true, true ).runAllTests();
        String logFileContents = Helpers.logFileContents();
        //What is logged is the configured results file interleaved with stack trace info.
        BufferedReader br = new BufferedReader( new FileReader( new File(Grandtestauto.LogForLoggingToFile_txt)));
        String line = br.readLine();
        int pos = 0;
        while (line != null) {
            assert logFileContents.indexOf( line, pos ) >= pos;
            pos += line.length();
            line = br.readLine();
        }
        br.close();
        return true;
    }
}
