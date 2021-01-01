package jet.testtools.help.test;

import jet.testtools.help.*;
import jet.testtools.test.testtools.help.Help;

import java.io.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class LinkCheckerTest {

    public boolean invalidLinksTest() throws Exception {
        File file = new File( Help.FileWithBrokenLinks_txt );
        Set<String> got = LinkChecker.invalidLinks( file );
        assert got.contains( "http://wwwww.non-existent-domain.commmm" );
        assert got.contains( "NonExistent1.html" );
        assert got.contains( "Red.PNG" );//Uppercase extensions always regarded as a broken link.
        assert got.contains( "Puce.png" );
        assert !got.contains( "ExampleTopic.html" );
        assert !got.contains( "Red.png" );
//        assert !got.contains( "http://java.sun.com" ) : "Is there an internet connection?";
        return true;
    }
}
