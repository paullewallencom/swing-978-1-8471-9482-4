package jet.testtools;

import jxl.*;
import jxl.write.*;
import org.apache.commons.io.*;

import java.io.*;
import java.util.*;

/**
 * The methods in this class are discussed in Chapter 14.
 */
public class TestHelper {

    /**
     * Private constructor as we don't want to instantiate this.
     */
    private TestHelper() {
    }

    /**
     * Writes an array of strings into the first sheet of the given spreadsheet file
     */
    public static void arrayToSpreadsheet( File spreadSheetFile, int sheetIndex, String[][] dataforSheet ) throws Exception {
        WritableWorkbook spreadSheet = Workbook.createWorkbook( spreadSheetFile );
        WritableSheet sheet = spreadSheet.createSheet( "sheet" + sheetIndex, sheetIndex );
        for (int row = 0; row < dataforSheet.length; row++) {
            for (int col = 0; col < dataforSheet[row].length; col++) {
                //NOTE the (column, row) indexing of cells.
                sheet.addCell( new jxl.write.Label( col, row, dataforSheet[row][col] ) );
            }
        }
        spreadSheet.write();
        spreadSheet.close();
    }

    /**
     * Converts the first sheet of the given spreadsheet file into an array of strings.
     */
    public static String[][] spreadsheetToArray( File spreadSheetFile, int sheetIndex ) throws Exception {
        Workbook spreadSheet = Workbook.getWorkbook( spreadSheetFile );
        Sheet sheet = spreadSheet.getSheet( sheetIndex );
        int numRows = sheet.getRows();
        int numCols = sheet.getColumns();
        String[][] result = new String[numRows][numCols];
        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                //NOTE the (column, row) indexing of cells.
                result[row][col] = sheet.getCell( col, row ).getContents();
            }
        }
        return result;
    }

    public static Set<String> namesOfActiveThreads() {
        Set<String> result = new HashSet<String>();
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate( threads );
        for (Thread thread : threads) {
            if (thread != null) {
                result.add( thread.getName() );
            }
        }
        return result;
    }

    public static void printActiveThreads() {
        int activeCount = namesOfActiveThreads().size();
        System.out.println( "Number of active Threads: " + activeCount );
        for (String name : namesOfActiveThreads()) {
            System.out.println( name );
        }
    }

    public static boolean waitForNamedThreadToFinish(
            final String threadName, long timeoutMillis ) {
        Waiting.ItHappened ih = new Waiting.ItHappened() {
            public boolean itHappened() {
                return !namesOfActiveThreads().contains( threadName );
            }
        };
        return Waiting.waitFor( ih, timeoutMillis );
    }

    /**
     * Removes leading and trailing whitespace from the given string and
     * replaces multiple whitespaces with the first char of the multiple
     * whitespace substring.
     *
     * @param str The string in which all whitespace is to be compacted
     * @return str.replaceAll( "[\\s]+", " ").trim();
     */
    public static String trimAndCompactWhitespaces( String str ) {
        return str.replaceAll( "[\\s]+", " " ).trim();
    }

    public static String pdfFileContents( File pdf ) throws Exception {
        //Use the PDFBox tool to write the text to a file.
        long now = System.currentTimeMillis();
        String tempFileName = "PDFTemp" + now + ".txt";
        File temp = new File( Files.tempDir(), tempFileName );
        org.pdfbox.ExtractText.main( new String[]{
                pdf.getAbsolutePath(),
                temp.getAbsolutePath()} );
        //Use the Apache commons-io tool to read the
        //text file, then deleted it.
        String result = FileUtils.readFileToString( temp );
        temp.delete();
        return result;
    }
}