package jet.testtools.test;

import jet.testtools.*;
import jet.testtools.test.testtools.*;

import java.io.*;

public class TestHelperTest {

    public boolean pdfFileContentsTest() throws Exception {
        File tempDir = Files.cleanedTempDir();
        assert tempDir.listFiles().length == 0;
        File pdf = new File( Testtools.Pdf1_pdf );
        String pdfContents = TestHelper.pdfFileContents( pdf ).trim();
        assert pdfContents.equals( "This is a pdf file." ) : "Got: '" + pdfContents + "'";
        //Check that the temp file is gone.
        assert tempDir.listFiles().length == 0;
        return true;
    }

    public boolean spreadsheetToArrayTest() throws Exception {
        //First sheet.
        String[][] data = TestHelper.spreadsheetToArray( new File( Testtools.Spreadsheet_xls ), 0 );
        assert data.length == 8;
        assert data[0].length == 3;
        assert data[0][0].equals( "Name" );
        assert data[0][1].equals( "Species" );
        assert data[0][2].equals( "Goody" );
        assert data[1].length == 3;
        assert data[1][0].equals( "Gandalf" );
        assert data[1][1].equals( "Wizard" );
        assert data[1][2].equals( "T" );
        assert data[2].length == 3;
        assert data[2][0].equals( "Saruman" );
        assert data[2][1].equals( "Wizard" );
        assert data[2][2].equals( "F" );
        assert data[3].length == 3;
        assert data[3][0].equals( "Radagast" );
        assert data[3][1].equals( "Wizard" );
        assert data[3][2].equals( "U" );

        assert data[4].length == 3;
        assert data[4][0].equals( "Tom Bombadil" );
        assert data[4][1].equals( "Tree Hugger" );
        assert data[4][2].equals( "U" );

        assert data[5].length == 3;
        assert data[5][0].equals( "Quickbeam" );
        assert data[5][1].equals( "Ent" );
        assert data[5][2].equals( "T" );

        assert data[6].length == 3;
        assert data[6][0].equals( "Shelob" );
        assert data[6][1].equals( "Giant Spider" );
        assert data[6][2].equals( "F" );

        assert data[7].length == 3;
        assert data[7][0].equals( "Galadriel" );
        assert data[7][1].equals( "Elf" );
        assert data[7][2].equals( "T" );

        //Second sheet.
        data = TestHelper.spreadsheetToArray( new File( Testtools.Spreadsheet_xls ), 1 );
        assert data.length == 5;
        assert data[0].length == 3;
        assert data[0][0].equals( "Name" );
        assert data[0][1].equals( "Species" );
        assert data[0][2].equals( "Side" );
        assert data[1].length == 3;
        assert data[1][0].equals( "Arwen" );
        assert data[1][1].equals( "Elf" );
        assert data[1][2].equals( "Goodies" );
        assert data[2].length == 3;
        assert data[2][0].equals( "" );
        assert data[2][1].equals( "Balrog" );
        assert data[2][2].equals( "Baddies" );
        assert data[3].length == 3;
        assert data[3][0].equals( "Sauron" );
        assert data[3][1].equals( "" );
        assert data[3][2].equals( "Baddies" );

        assert data[4].length == 3;
        assert data[4][0].equals( "Gimli" );
        assert data[4][1].equals( "Dwarf" );
        assert data[4][2].equals( "Goodies" );
        return true;
    }

    public boolean arrayToSpreadsheetTest() throws Exception {
        File dir = Files.cleanedTempDir();
        File file = new File( dir, "S1.xls" );
        assert !file.exists();
        String[][] data = {{"a", "b", "c"}, {"d", "e", "f"}, {"g", "h", "i"}};
        TestHelper.arrayToSpreadsheet( file, 0, data );
        String[][] got = TestHelper.spreadsheetToArray( file, 0 );
        assert got.length == 3;
        assert got[0].length == 3;
        assert got[0][0].equals( "a" );
        assert got[0][1].equals( "b" );
        assert got[0][2].equals( "c" );
        assert got[1].length == 3;
        assert got[1][0].equals( "d" );
        assert got[1][1].equals( "e" );
        assert got[1][2].equals( "f" );
        assert got[2].length == 3;
        assert got[2][0].equals( "g" );
        assert got[2][1].equals( "h" );
        assert got[2][2].equals( "i" );

        String[][] data2 = {{"a2", "b2", "c2"}, {"d2", "e2", "f2"}};
        TestHelper.arrayToSpreadsheet( file, 0, data2 );
        String[][] got2 = TestHelper.spreadsheetToArray( file, 0 );
        assert got2.length == 2;
        assert got2[0].length == 3;
        assert got2[0][0].equals( "a2" );
        assert got2[0][1].equals( "b2" );
        assert got2[0][2].equals( "c2" );
        assert got2[1].length == 3;
        assert got2[1][0].equals( "d2" );
        assert got2[1][1].equals( "e2" );
        assert got2[1][2].equals( "f2" );
        return true;
    }

    public boolean trimAndCompactWhitespacesTest() {
        Assert.equal( TestHelper.trimAndCompactWhitespaces( "" ), "" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( "nospaces" ), "nospaces" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( " leading" ), "leading" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( " \nleading" ), "leading" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( " \tleading" ), "leading" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( " leading" ), "leading" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( "trailing " ), "trailing" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( "within text" ), "within text" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( "within  text" ), "within text" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( "within      text" ), "within text" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( "within\t\ttext" ), "within text" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( "within\t\ntext" ), "within text" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( " mixed around  " ), "mixed around" );
        Assert.equal( TestHelper.trimAndCompactWhitespaces( " mixed   \t\t\n\naround  " ), "mixed around" );

        return true;
    }

    public boolean namesOfActiveThreadsTest() throws Exception {
        String name = "Waldstein";
        NamedThread nt = new NamedThread( name );
        nt.start();
        assert TestHelper.namesOfActiveThreads().contains( name );

        nt.flagToStop = true;
        nt.join( 1000 );
        assert !TestHelper.namesOfActiveThreads().contains( name );

        return true;
    }

    public boolean printActiveThreadsTest() throws Exception {
        //Here we replace System.put with a stream we'll
        //be able to check. See AssertTest
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream( byteOut );
        PrintStream oldOut = System.out;
        System.setOut( newOut );

        String name = "Waldstein";
        NamedThread nt = new NamedThread( name );
        nt.start();
        TestHelper.printActiveThreads();
        newOut.flush();
        String out = byteOut.toString();
        boolean result = out.contains( name );
        nt.flagToStop = true;
        nt.join( 1000 );

        byteOut.reset();
        TestHelper.printActiveThreads();
        newOut.flush();
        out = byteOut.toString();
        result &= !out.contains( name );

        //Reset System.out.
        System.setOut( oldOut );
        return result;
    }

    public boolean waitForNamedThreadToFinishTest() throws Exception {
        //One where the thread finishes within the expected period.
        NamedThread aubrey = new NamedThread( "Aubrey" );
        ThreadInWhichToCallWaitFor waiter
                = new ThreadInWhichToCallWaitFor( "Aubrey", 1000 );
        aubrey.start();
        waiter.start();
        Waiting.pause( 100 );
        aubrey.flagToStop = true;
        waiter.join();
        assert waiter.result;

        //One where the thread does not finish
        //within the expected period.
        NamedThread maturin = new NamedThread( "Maturin" );
        waiter = new ThreadInWhichToCallWaitFor( "Maturin", 1000 );
        maturin.start();
        waiter.start();
        waiter.join();
        assert !waiter.result;
        //Try again. This is partly just so that maturin stops.
        waiter = new ThreadInWhichToCallWaitFor( "Maturin", 1000 );
        waiter.start();
        maturin.flagToStop = true;
        waiter.join();
        assert waiter.result;
        return true;
    }

    private class ThreadInWhichToCallWaitFor extends Thread {
        boolean result;
        String threadName;
        long timeout;

        public ThreadInWhichToCallWaitFor(
                String threadName, long timeout ) {
            super( "WaiterFor: " + threadName );
            this.threadName = threadName;
            this.timeout = timeout;
        }

        public void run() {
            result = TestHelper.waitForNamedThreadToFinish(
                    threadName, timeout );
        }
    }

    private class NamedThread extends Thread {
        volatile boolean flagToStop;

        public NamedThread( String name ) {
            super( name );
        }

        public void run() {
            while (!flagToStop) {
                Waiting.pause( 10 );
            }
        }
    }
}
