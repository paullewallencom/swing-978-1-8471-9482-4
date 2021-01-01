package jet.testtools.test;

import jet.testtools.*;

import java.io.*;

public class ExcelTest {
public boolean writeToSpreadSheetTest() throws Exception {
    //write the spreadsheet using the production method.
    File spreadSheetFile = writeToSpreadSheet();

    //check whether the retrieved values of the
    //spreadsheet are as expected.
    int sheetIndex = 0;
    String[][] retrieved = TestHelper.spreadsheetToArray(
            spreadSheetFile, sheetIndex );
    String[][] expected = new String[][]{
            {"00", "01"},
            {"10", "11"}};
    //Check the retrieved data.
    checkData( expected, retrieved );
    return true;
}

public boolean readFromSpreadSheetTest() throws Exception {
    //Write test data into a spreadsheet.
    String[][] expected = new String[][]{
            {"00", "01"},
            {"10", "11"}};
    File spreadSheetFile = new File( "Spreadsheet.xls" );
    int sheetIndex = 0;
    TestHelper.arrayToSpreadsheet(
            spreadSheetFile, sheetIndex, expected );

    //Read the spreadsheet using the production method.
    String[][] retrieved = readFromSpreadSheet( spreadSheetFile );

    //Check that the retrieved values of the spreadsheet
    //are as expected.
    checkData( expected, retrieved );
    return true;
}

private static void checkData( String[][] expected,
                               String[][] retrieved ) {
    //Check the number of rows.
    assert retrieved.length == expected.length;
    for (int row = 0; row < expected.length; row++) {
        //Check the number of columns.
        assert retrieved[row].length == expected[row].length;
        for (int col = 0; col < expected[row].length; col++) {
            //Check the data in each cell.
            assert retrieved[row][col].equals( expected[row][col] );
        }
    }
}

    private String[][] readFromSpreadSheet( File spreadSheetFile ) throws Exception {
        int sheetIndex = 0;
        return TestHelper.spreadsheetToArray( spreadSheetFile, sheetIndex );
    }

    private File writeToSpreadSheet() throws Exception {
        File file = new File( "Spreadsheet.xls" );
        String[][] values = new String[][]{{"00", "01"}, {"10", "11"}};
        int sheetIndex = 0;
        TestHelper.arrayToSpreadsheet( file, sheetIndex, values );
        return file;
    }
}
