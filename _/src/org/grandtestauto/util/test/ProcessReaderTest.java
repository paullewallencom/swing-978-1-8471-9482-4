package org.grandtestauto.util.test;

import jet.testtools.test.org.grandtestauto.util.*;
import org.apache.commons.io.*;
import org.grandtestauto.util.*;

import java.io.*;

/**
 * @author Tim Lavers
 */
public class ProcessReaderTest {
    public boolean readProcessTest() throws IOException {
        String classPath = System.getProperty( "java.class.path" );
        StringBuilder cmd = new StringBuilder();
        cmd.append( "java.exe " );
        //The classpath.
        cmd.append( " -cp \"" );
        cmd.append( classPath );
        cmd.append( "\"" );
        //The main class and its arguments, which are the file names.
        cmd.append( " org.grandtestauto.util.test.ClassToRunInProcess \"" );
        cmd.append( Util.sout_txt );
        cmd.append( "\" \"" );
        cmd.append( Util.serr_txt );
        cmd.append( "\"" );

        Process p = Runtime.getRuntime().exec( cmd.toString() );
        String[] results = ProcessReader.readProcess( p );
        //The ClassToRunInProcess program appends an extraneous line separator.
        String nl = System.getProperty( "line.separator" );
        String expectedSout = FileUtils.readFileToString( new File( Util.sout_txt ) ) + nl;
        String expectedSerr = FileUtils.readFileToString( new File( Util.serr_txt ) ) + nl;
        assert results[0].equals( expectedSout ) : "Got: '" + results[0] + "'";
        assert results[1].equals( expectedSerr ) : "Got: '" + results[1] + "'";

        return true;
    }
}
