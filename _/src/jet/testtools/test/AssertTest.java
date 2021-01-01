package jet.testtools.test;

import jet.testtools.*;

import java.io.*;

public class AssertTest {
    public boolean equalTest() throws IOException {
        //Check some objects that are equal.
        Assert.equal( "", "" );
        Assert.equal( "string", "string" );
        Assert.equal( Boolean.TRUE, Boolean.TRUE );
        Assert.equal( 123, 123 );

        //Now check some that are different.
        //Check that an error is thrown, and also what
        //is printed to System.out.
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream( byteOut );
        PrintStream oldOut = System.out;
        System.setOut( newOut );

        boolean result = true;
        try {
            Assert.equal( "", null );
            result = false;//Should have got an exception
        } catch (Exception e) {
            //Ignore.
        }
        newOut.flush();
        String message = byteOut.toString();
        String expected = "1st Object: ''" + StringUtils.NL +
                "2nd Object: 'null'" + StringUtils.NL;
        result &= expected.equals( message );

        byteOut.reset();
        try {
            Assert.equal( "", "not blank" );
            result = false;//Should have got an exception
        } catch (Exception e) {
            //Ignore.
        }
        newOut.flush();
        message = byteOut.toString();
        expected = "1st Object: ''" + StringUtils.NL +
                "2nd Object: 'not blank'" + StringUtils.NL;
        result &= expected.equals( message );

        byteOut.reset();
        try {
            Assert.equal( true, false );
            result = false;//Should have got an exception
        } catch (Exception e) {
            //Ignore.
        }
        newOut.flush();
        message = byteOut.toString();
        expected = "1st Object: 'true'" + StringUtils.NL +
                "2nd Object: 'false'" + StringUtils.NL;
        result &= expected.equals( message );

        byteOut.reset();
        try {
            Assert.equal( "", new StringBuffer() );
            result = false;//Should have got an exception
        } catch (Exception e) {
            //Ignore.
        }
        newOut.flush();
        message = byteOut.toString();
        expected = "1st Object: ''" + StringUtils.NL +
            "2nd Object: ''" + StringUtils.NL +
            "Class of o1: class java.lang.String" + StringUtils.NL +
            "Class of o2: class java.lang.StringBuffer"+ StringUtils.NL;
        result &= expected.equals( message );
        //Reset System.out.
        System.setOut( oldOut );
        return result;
    }
}