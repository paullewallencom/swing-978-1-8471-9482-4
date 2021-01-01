package org.grandtestauto.test;

import jet.testtools.*;
import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;

import java.io.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class SettingsTest {

    private File tempDir = Helpers.tempDirectory();
    private Settings settings;

    public boolean constructorTest() throws IOException {
        //Just check that a sensible properties example can be produced.
        settings = new Settings();
        String value = settings.commentedPropertiesWithTheseValues();
        checkPropertiesString( value );
        return true;
    }

    public boolean constructor_String_Test() {
        return true;
    }

    public boolean summaryTest() {
        settings = new Settings();
        String summary = settings.summary();
        StringBuilder expected = new StringBuilder( );
        addKeyValues( expected, Messages.SK_WILL_RUN_UNIT_TESTS, Messages.SK_WILL_RUN_FUNCTION_TESTS, Messages.SK_WILL_RUN_LOAD_TESTS );
        assert expected.toString().equals( summary );

        init( true, true, false, true, true );
        summary = settings.summary();
        expected = new StringBuilder( );
        addKeyValues( expected, Messages.SK_WILL_RUN_UNIT_TESTS, Messages.SK_WILL_RUN_FUNCTION_TESTS, Messages.SK_WILL_NOT_RUN_LOAD_TESTS );
        assert expected.toString().equals( summary );

        init( true, false, false, true, true );
        summary = settings.summary();
        expected = new StringBuilder( );
        addKeyValues( expected, Messages.SK_WILL_RUN_UNIT_TESTS, Messages.SK_WILL_NOT_RUN_FUNCTION_TESTS, Messages.SK_WILL_NOT_RUN_LOAD_TESTS );
        assert expected.toString().equals( summary );

        init( false, false, false, true, true );
        summary = settings.summary();
        expected = new StringBuilder( );
        addKeyValues( expected, Messages.SK_WILL_NOT_RUN_UNIT_TESTS, Messages.SK_WILL_NOT_RUN_FUNCTION_TESTS, Messages.SK_WILL_NOT_RUN_LOAD_TESTS );
        assert expected.toString().equals( summary );

        init( true, true, true, false, false, null, "ip", null, null, null, null, null, null, null, null, null );
        summary = settings.summary();
        expected = new StringBuilder( );
        addKeyValues( expected, Messages.SK_WILL_RUN_UNIT_TESTS, Messages.SK_WILL_RUN_FUNCTION_TESTS, Messages.SK_WILL_RUN_LOAD_TESTS );
        expected.append( new NameFilter( NameFilter.Type.PACKAGE, "ip", null, null ).loggingMessage() );
        expected.append( Settings.NL );
        assert expected.toString().equals( summary )  : "Expected: '" + expected + "'\n but got: '" + summary + "'";

        init( true, true, true, false, false, null, "ip", null, "sp", null, "ic", "fc", null, null, null, null );
        summary = settings.summary();
        expected = new StringBuilder( );
        addKeyValues( expected, Messages.SK_WILL_RUN_UNIT_TESTS, Messages.SK_WILL_RUN_FUNCTION_TESTS, Messages.SK_WILL_RUN_LOAD_TESTS );
        expected.append( new NameFilter( NameFilter.Type.PACKAGE, "ip", null, "sp" ).loggingMessage() );
        expected.append( Settings.NL );
        expected.append( new NameFilter( NameFilter.Type.CLASS, "ic", "fc", null ).loggingMessage() );
        expected.append( Settings.NL );
        assert expected.toString().equals( summary )  : "Expected: '" + expected + "'\n but got: '" + summary + "'";

        init( true, true, true, false, false, null, null, null, "sp", null, "ic", "fc", "sc", "im", "fm", null );
        summary = settings.summary();
        expected = new StringBuilder( );
        addKeyValues( expected, Messages.SK_WILL_RUN_UNIT_TESTS, Messages.SK_WILL_RUN_FUNCTION_TESTS, Messages.SK_WILL_RUN_LOAD_TESTS );
        expected.append( new NameFilter( NameFilter.Type.PACKAGE, null, null, "sp" ).loggingMessage() );
        expected.append( Settings.NL );
        expected.append( new NameFilter( NameFilter.Type.CLASS, "ic", "fc", "sc" ).loggingMessage() );
        expected.append( Settings.NL );
        expected.append( new NameFilter( NameFilter.Type.METHOD, "im", "fm", null ).loggingMessage() );
        expected.append( Settings.NL );
        assert expected.toString().equals( summary )  : "Expected: '" + expected + "'\n but got: '" + summary + "'";

        init( true, true, true, false, false, null, null, null, "sp", true, "ic", "fc", "sc", "im", "fm", null );
        summary = settings.summary();
        expected = new StringBuilder( );
        addKeyValues( expected, Messages.SK_WILL_RUN_UNIT_TESTS, Messages.SK_WILL_RUN_FUNCTION_TESTS, Messages.SK_WILL_RUN_LOAD_TESTS );
        expected.append( new NameFilter( NameFilter.Type.PACKAGE, null, null, "sp" ).loggingMessage() );
        expected.append( Settings.NL );
        expected.append( new NameFilter( NameFilter.Type.CLASS, "ic", "fc", "sc" ).loggingMessage() );
        expected.append( Settings.NL );
        expected.append( new NameFilter( NameFilter.Type.METHOD, "im", "fm", null ).loggingMessage() );
        expected.append( Settings.NL );
        expected.append( Messages.message( Messages.SK_FAIL_FAST ) );
        expected.append( Settings.NL );
        assert expected.toString().equals( summary )  : "Expected: '" + expected + "'\n but got: '" + summary + "'";
        return true;
    }

    private void addKeyValues(StringBuilder sb, String ...  keys ) {
        for (String key : keys) {
            sb.append( Messages.message( key ) );
            sb.append( Settings.NL );
        }
    }

    public boolean classesDirTest() throws Exception {
        init( true, true, true, true, true );
        assert settings.classesDir().equals( tempDir );
        return true;
    }

    public boolean valuesAreTrimmedTest() throws Exception {
        init( true, true, true, true, true, " log file  ", "initial package   ", " final package ", null, null );
        assert settings.resultsFileName().equals( "log file" );
        assert settings.packageNameFilter().equals( nf( NameFilter.Type.PACKAGE, "initial package", "final package", null ) );
        init( true, true, true, true, true, " log file  ", "initial package   ", " final package ", "single package  ", null );
        assert settings.packageNameFilter().equals( new NameFilter( NameFilter.Type.PACKAGE, "initial package", "final package","single package" ) );
        return true;
    }

    public boolean packageNameFilterTest() throws Exception {
        String initialPackageName = null;
        String finalPackageName = null;
        String singlePackageName = null;
        init( true, true, true, true, true, null, initialPackageName, finalPackageName, singlePackageName, null );
        assert settings.packageNameFilter().accept( "a" );
        assert settings.packageNameFilter().accept( "b" );
        assert settings.packageNameFilter().accept( "m" );
        assert settings.packageNameFilter().accept( "y" );
        assert settings.packageNameFilter().accept( "z" );

        initialPackageName = "b";
        finalPackageName = null;
        singlePackageName = null;
        init( true, true, true, true, true, null, initialPackageName, finalPackageName, singlePackageName, null );
        assert !settings.packageNameFilter().accept( "a" );
        assert settings.packageNameFilter().accept( "b" );
        assert settings.packageNameFilter().accept( "m" );
        assert settings.packageNameFilter().accept( "y" );
        assert settings.packageNameFilter().accept( "z" );

        initialPackageName = null;
        finalPackageName = "y";
        singlePackageName = null;
        init( true, true, true, true, true, null, initialPackageName, finalPackageName, singlePackageName, null );
        assert settings.packageNameFilter().accept( "a" );
        assert settings.packageNameFilter().accept( "b" );
        assert settings.packageNameFilter().accept( "m" );
        assert settings.packageNameFilter().accept( "y" );
        assert !settings.packageNameFilter().accept( "z" );

        initialPackageName = "b";
        finalPackageName = "y";
        singlePackageName = null;
        init( true, true, true, true, true, null, initialPackageName, finalPackageName, singlePackageName, null );
        assert !settings.packageNameFilter().accept( "a" );
        assert settings.packageNameFilter().accept( "b" );
        assert settings.packageNameFilter().accept( "m" );
        assert settings.packageNameFilter().accept( "y" );
        assert !settings.packageNameFilter().accept( "z" );

        initialPackageName = "b";
        finalPackageName = "y";
        singlePackageName = "m";
        init( true, true, true, true, true, null, initialPackageName, finalPackageName, singlePackageName, null );
        assert !settings.packageNameFilter().accept( "a" );
        assert !settings.packageNameFilter().accept( "b" );
        assert settings.packageNameFilter().accept( "m" );
        assert !settings.packageNameFilter().accept( "y" );
        assert !settings.packageNameFilter().accept( "z" );

        //Blanks are like null.
        initialPackageName = "";
        finalPackageName = "";
        singlePackageName = "";
        init( true, true, true, true, true, null, initialPackageName, finalPackageName, singlePackageName, null );
        assert settings.packageNameFilter().accept( "a" );
        assert settings.packageNameFilter().accept( "b" );
        assert settings.packageNameFilter().accept( "m" );
        assert settings.packageNameFilter().accept( "y" );
        assert settings.packageNameFilter().accept( "z" );
        return true;
    }

    public boolean classNameFilterTest() throws Exception {
        String singlePackageName = null;
        String initialClassName = null;
        String finalClassName = null;
        String singleClassName = null;

        //If the single package property is null, the class name filter is null.
        init( true, true, true, true, true, null, null, null, singlePackageName, null, initialClassName, finalClassName, singleClassName, null, null, null );
        assert settings.classNameFilter().equals( new NameFilter( NameFilter.Type.CLASS, null, null, null ));

        initialClassName = "M";
        finalClassName = "R";
        singleClassName = null;
        init( true, true, true, true, true, null, null, null, singlePackageName, null, initialClassName, finalClassName, singleClassName, null, null, null );
        assert settings.classNameFilter().equals( new NameFilter( NameFilter.Type.CLASS, null, null, null ));

        singlePackageName = "x.y";
        init( true, true, true, true, true, null, null, null, singlePackageName, null, initialClassName, finalClassName, singleClassName, null, null, null );
        assert !settings.classNameFilter().accept( "L" );
        assert settings.classNameFilter().accept( "M" );
        assert settings.classNameFilter().accept( "N" );
        assert settings.classNameFilter().accept( "R" );
        assert !settings.classNameFilter().accept( "S" );

        //Initial value only.
        initialClassName = "M";
        finalClassName = null;
        init( true, true, true, true, true, null, null, null, singlePackageName, null, initialClassName, finalClassName, singleClassName, null, null, null );
        assert !settings.classNameFilter().accept( "L" );
        assert settings.classNameFilter().accept( "M" );
        assert settings.classNameFilter().accept( "N" );
        assert settings.classNameFilter().accept( "R" );
        assert settings.classNameFilter().accept( "S" );
        assert settings.classNameFilter().accept( "Z" );

        //Final value only.
        initialClassName = null;
        finalClassName = "R";
        init( true, true, true, true, true, null, null, null, singlePackageName, null, initialClassName, finalClassName, singleClassName, null, null, null );
        assert settings.classNameFilter().accept( "L" );
        assert settings.classNameFilter().accept( "A" );
        assert settings.classNameFilter().accept( "M" );
        assert settings.classNameFilter().accept( "N" );
        assert settings.classNameFilter().accept( "R" );
        assert !settings.classNameFilter().accept( "S" );
        assert !settings.classNameFilter().accept( "Z" );

        //Single class name over-rides the initial and final values.
        initialClassName = "M";
        finalClassName = "R";
        singleClassName = "Z";
        init( true, true, true, true, true, null, null, null, singlePackageName, null, initialClassName, finalClassName, singleClassName, null, null, null );
        assert !settings.classNameFilter().accept( "L" );
        assert !settings.classNameFilter().accept( "M" );
        assert !settings.classNameFilter().accept( "N" );
        assert !settings.classNameFilter().accept( "R" );
        assert !settings.classNameFilter().accept( "S" );
        assert settings.classNameFilter().accept( "Z" );
        return true;
    }

    public boolean methodNameFilterTest() throws Exception {
        String singlePackageName = null;
        String singleClassName = null;
        String initialMethodName = null;
        String finalMethodName = null;
        String singleMethodName = null;

        //If the single package property is null, or the single class property is null, the method name filter is built of nulls..
        init( true, true, true, true, true, null, null, null, singlePackageName, null, null, null, singleClassName, initialMethodName, finalMethodName, singleMethodName );
        assert settings.methodNameFilter().equals( new NameFilter( NameFilter.Type.METHOD, null, null, null ));

        initialMethodName = "M";
        finalMethodName = "R";
        init( true, true, true, true, true, null, null, null, singlePackageName, null, null, null, singleClassName, initialMethodName, finalMethodName, singleMethodName );
        assert settings.methodNameFilter().equals( new NameFilter( NameFilter.Type.METHOD, null, null, null ));

        singlePackageName = "x.y";
        init( true, true, true, true, true, null, null, null, singlePackageName, null, null, null, singleClassName, initialMethodName, finalMethodName, singleMethodName );
        assert settings.methodNameFilter().equals( new NameFilter( NameFilter.Type.METHOD, null, null, null ));

        singleClassName = "A";
        init( true, true, true, true, true, null, null, null, singlePackageName, null, null, null, singleClassName, initialMethodName, finalMethodName, singleMethodName );
        assert !settings.methodNameFilter().accept( "L" );
        assert settings.methodNameFilter().accept( "M" );
        assert settings.methodNameFilter().accept( "N" );
        assert settings.methodNameFilter().accept( "R" );
        assert !settings.methodNameFilter().accept( "S" );

        //Initial value only.
        initialMethodName = "M";
        finalMethodName = null;
        init( true, true, true, true, true, null, null, null, singlePackageName, null, null, null, singleClassName, initialMethodName, finalMethodName, singleMethodName );
        assert !settings.methodNameFilter().accept( "L" );
        assert settings.methodNameFilter().accept( "M" );
        assert settings.methodNameFilter().accept( "N" );
        assert settings.methodNameFilter().accept( "R" );
        assert settings.methodNameFilter().accept( "S" );
        assert settings.methodNameFilter().accept( "Z" );

        //Final value only.
        initialMethodName = null;
        finalMethodName = "R";
        init( true, true, true, true, true, null, null, null, singlePackageName, null, null, null, singleClassName, initialMethodName, finalMethodName, singleMethodName );
        assert settings.methodNameFilter().accept( "A" );
        assert settings.methodNameFilter().accept( "L" );
        assert settings.methodNameFilter().accept( "M" );
        assert settings.methodNameFilter().accept( "N" );
        assert settings.methodNameFilter().accept( "R" );
        assert !settings.methodNameFilter().accept( "S" );
        assert !settings.methodNameFilter().accept( "Z" );

        //Single method name over-rides the initial and final values.
        initialMethodName = "M";
        finalMethodName = "R";
        singleMethodName = "R";
        init( true, true, true, true, true, null, null, null, singlePackageName, null, null, null, singleClassName, initialMethodName, finalMethodName, singleMethodName );
        assert !settings.methodNameFilter().accept( "L" );
        assert !settings.methodNameFilter().accept( "M" );
        assert !settings.methodNameFilter().accept( "N" );
        assert settings.methodNameFilter().accept( "R" );
        assert !settings.methodNameFilter().accept( "S" );
        return true;
    }

    public boolean runFunctionTestsTest() throws Exception {
        init( true, true, true, true, true );
        assert settings.runFunctionTests();
        init( true, false, true, true, true );
        assert !settings.runFunctionTests();
        return true;
    }

    public boolean runLoadTestsTest() throws Exception {
        init( true, true, true, true, true );
        assert settings.runLoadTests();
        init( true, true, false, true, true );
        assert !settings.runLoadTests();
        return true;
    }

    public boolean runUnitTestsTest() throws Exception {
        init( true, true, true, true, true );
        assert settings.runUnitTests();
        init( false, true, true, true, true );
        assert !settings.runUnitTests();
        return true;
    }

    public boolean logToConsoleTest() throws Exception {
        init( true, true, true, false, true );
        assert !settings.logToConsole();
        init( false, true, true, true, true );
        assert settings.logToConsole();
        return true;
    }

    public boolean abbreviationsTest() throws IOException {
        Properties props = new Properties();
        props.put( Settings.CLASSES_ROOT, Helpers.tempDirectory().getAbsolutePath() );
        init( props );
        assert settings.unknownKeys().isEmpty();
        props = new Properties();
        props.put( Settings.LOG_TO_CONSOLE, "t" );
        props.put( Settings.STOP_AT_FIRST_FAILURE, "T" );
        props.put( Settings.RUN_FUNCTION_TESTS, "f" );
        props.put( Settings.RUN_LOAD_TESTS, "F" );
        init( props );
        assert settings.logToConsole();
        assert settings.stopAtFirstFailure();
        assert !settings.runFunctionTests();
        assert !settings.runLoadTests();

        return true;
    }
    
    public boolean resultsFileNameTest() {
        //logToFile is false and no log file specified.
        init( true, true, true, true, false );
        assert settings.resultsFileName() == null;

        //logToFile is true, no log file specified. Use default.
        init( true, true, true, true, true );
        File expected = new File( Settings.DEFAULT_LOG_FILE_NAME );
        Helpers.assertEqual( new File( settings.resultsFileName() ).getAbsolutePath(), expected.getAbsolutePath() );

        //logToFile is true, and a file is specified.
        File resultsFile = new File( tempDir, "ResultsFile.txt" );
        init( true, true, true, true, true, resultsFile.getAbsolutePath() );
        assert new File( settings.resultsFileName() ).getAbsolutePath() .equals( resultsFile.getAbsolutePath() );
//@todo add a test for when the settings file is blank
        return true;
    }

    public boolean collatedResultsFileNameTest() throws IOException {
        init( true, true, true, true, false );
        assert settings.collatedResultsFileName().equals( Settings.DEFAULT_COLLATED_RESULTS_FILE_NAME );

        Settings s = new Settings( Grandtestauto.TestSettings1_txt );
        assert s.collatedResultsFileName().equals( "FileInWhichToCollateResults.txt" );
        return true;
    }

    public boolean lessVerboseLoggingTest() throws IOException {
        //This file has no value set.
        Settings s = new Settings( Grandtestauto.TestSettings1_txt );
        assert !s.lessVerboseLogging();

        //This file has value set to true.
        s = new Settings( Grandtestauto.TestSettings2_txt );
        assert s.lessVerboseLogging();

        //This file has value set to false.
        s = new Settings( Grandtestauto.TestSettings3_txt );
        assert !s.lessVerboseLogging();
        return true;
    }

    public boolean distributorPortTest() throws IOException {
        //This file has no value set.
        Settings s = new Settings( Grandtestauto.TestSettings1_txt );
        assert s.distributorPort() == Settings.DEFAULT_DISTRIBUTOR_PORT;

        //This file has value set to 12345.
        s = new Settings( Grandtestauto.TestSettings2_txt );
        assert s.distributorPort() == 12345;

        return true;
    }

    public boolean stopAtFirstFailureTest() {
        init( true, true, true, true, true, null, null, null, "singlePackage", Boolean.FALSE );
        assert !settings.stopAtFirstFailure();
        init( true, true, true, true, true, null, null, null, "singlePackage", Boolean.TRUE );
        assert settings.stopAtFirstFailure();
        return true;
    }

    public boolean unknownKeysTest() throws IOException {
        Properties props = new Properties();
        props.put( Settings.CLASSES_ROOT, Helpers.tempDirectory().getAbsolutePath() );
        init( props );
        assert settings.unknownKeys().isEmpty();
        props = new Properties();
        props.put( Settings.FINAL_CLASS_WITHIN_SINGLE_PACKAGE, "" );
        props.put( Settings.FINAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, "" );
        props.put( Settings.FINAL_PACKAGE, "" );
        props.put( Settings.INITIAL_CLASS_WITHIN_SINGLE_PACKAGE, "" );
        props.put( Settings.INITIAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, "" );
        props.put( Settings.INITIAL_PACKAGE, "" );
        props.put( Settings.LOG_FILE_NAME, "" );
        props.put( Settings.LOG_TO_CONSOLE, "" );
        props.put( Settings.LOG_TO_FILE, "" );
        props.put( Settings.RUN_FUNCTION_TESTS, "" );
        props.put( Settings.RUN_LOAD_TESTS, "" );
        props.put( Settings.RUN_UNIT_TESTS, "" );
        props.put( Settings.SINGLE_CLASS_WITHIN_SINGLE_PACKAGE, "" );
        props.put( Settings.SINGLE_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, "" );
        props.put( Settings.SINGLE_PACKAGE, "" );
        props.put( Settings.STOP_AT_FIRST_FAILURE, "" );
        init( props );
        assert settings.unknownKeys().isEmpty();

        HashSet<String> expected = new HashSet<String>();
        expected.add( "junk" );
        props.put( "junk", "junk" );
        init( props );
        assert settings.unknownKeys().equals( expected ) : "Got: " + settings.unknownKeys();

        //The DEFAULT_... constant is not a key.
        expected.add( Settings.DEFAULT_LOG_FILE_NAME );
        props.put( Settings.DEFAULT_LOG_FILE_NAME, "jrwejri" );
        init( props );
        assert settings.unknownKeys().equals( expected ) : "Got: " + settings.unknownKeys();
        return true;
    }

    public boolean commentedPropertiesWithTheseValuesTest() throws IOException {
        Properties props = new Properties();
        props.put( Settings.CLASSES_ROOT, Helpers.tempDirectory().getAbsolutePath() );
        init( props );
        String value = settings.commentedPropertiesWithTheseValues();
        checkPropertiesString( value );

        props.put( Settings.FINAL_CLASS_WITHIN_SINGLE_PACKAGE, "aaa" );
        props.put( Settings.FINAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, "aaa" );
        props.put( Settings.FINAL_PACKAGE, "sddsd" );
        props.put( Settings.INITIAL_CLASS_WITHIN_SINGLE_PACKAGE, "sda" );
        props.put( Settings.INITIAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, "vvvv" );
        props.put( Settings.INITIAL_PACKAGE, "lsdfs" );
        props.put( Settings.LOG_FILE_NAME, "ppmsf.txt" );
        props.put( Settings.LOG_TO_CONSOLE, "false" );
        props.put( Settings.LOG_TO_FILE, "false" );
        props.put( Settings.RUN_FUNCTION_TESTS, "false" );
        props.put( Settings.RUN_LOAD_TESTS, "false" );
        props.put( Settings.RUN_UNIT_TESTS, "false" );
        props.put( Settings.SINGLE_CLASS_WITHIN_SINGLE_PACKAGE, "erwer" );
        props.put( Settings.SINGLE_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, "wwerew" );
        props.put( Settings.SINGLE_PACKAGE, "werwe" );
        props.put( Settings.STOP_AT_FIRST_FAILURE, "true" );
        init( props );
        value = settings.commentedPropertiesWithTheseValues();
        checkPropertiesString( value );
        return true;
    }

    private void checkPropertiesString( String value ) throws IOException {
        //Check that a Settings created from this value has the same values as our settings instance variable.
        File file = new File( Helpers.tempDirectory(), "Settings" + System.currentTimeMillis() + ".txt" );
        Files.writeIntoFile( value, file );
        Settings other = new Settings( file.getAbsolutePath() );
        checkEqual( settings.classesDir(), other.classesDir() );
        checkEqual( settings.classNameFilter(), other.classNameFilter() );
        checkEqual( settings.logToConsole(), other.logToConsole() );
        checkEqual( settings.methodNameFilter(), other.methodNameFilter() );
        checkEqual( settings.packageNameFilter(), other.packageNameFilter() );
        checkEqual( settings.resultsFileName(), other.resultsFileName() );
        checkEqual( settings.runFunctionTests(), other.runFunctionTests() );
        checkEqual( settings.runLoadTests(), other.runLoadTests() );
        checkEqual( settings.stopAtFirstFailure(), other.stopAtFirstFailure() );

        //Check that each the key-value pair is preceded by the correct comment.
        ResourceBundle commentsRB = PropertyResourceBundle.getBundle( Settings.class.getName() );
        String[] asLines = value.split( "\\n" );
        checkCommentPrecedesLineContaining( asLines, Settings.CLASSES_ROOT, commentsRB );
        checkCommentPrecedesLineContaining( asLines, Settings.FINAL_CLASS_WITHIN_SINGLE_PACKAGE, commentsRB );

        //Check that all of the keys are there.
    }

    private void checkEqual( Object o1, Object o2 ) {
        if (o1 == null) {
            assert o2 == null : "Got: null, " + o2;
        } else {
            assert o1.equals( o2 ) : "Got: " + o1 + ", " + o2;
        }
    }

    private void checkCommentPrecedesLineContaining( String[] lines, String key, ResourceBundle commentsRB ) {
        String comment = "#" + commentsRB.getString( key );
        int pos = 0;
        for (String line : lines) {
            if (line.trim().equals( comment )) break;
            pos++;
        }
        assert lines[pos + 1].trim().startsWith( key );
    }


    private void init( Properties p ) throws IOException {
        Helpers.cleanTempDirectory();
        File propsFile = new File( Helpers.tempDirectory(), "SettingsTest.txt" );
        BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( propsFile ) );
        p.store( bos, "Settings test, " + new Date() );
        bos.close();
        settings = new Settings( propsFile.getAbsolutePath() );
    }

    private void init( boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, boolean logToConsole, boolean logToFile ) {
        init( runUnitTests, runFunctionTests, runLoadTests, logToConsole, logToFile, null );
    }

    private void init( boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, boolean logToConsole, boolean logToFile, String logFileName ) {
        init( runUnitTests, runFunctionTests, runLoadTests, logToConsole, logToFile, logFileName, null, null, null, null );
    }

    private void init( boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, boolean logToConsole, boolean logToFile, String logFileName, String initialPackageName, String finalPackageName, String singlePackageName, Boolean failFast ) {
        init( runUnitTests, runFunctionTests, runLoadTests, logToConsole, logToFile, logFileName, initialPackageName, finalPackageName, singlePackageName, failFast, null, null, null, null, null, null );
    }

    private void init( boolean runUnitTests, boolean runFunctionTests, boolean runLoadTests, boolean logToConsole, boolean logToFile, String logFileName, String initialPackageName, String finalPackageName, String singlePackageName, Boolean failFast, String initialClassName, String finalClassName, String singleClassName, String initialMethod, String finalMethod, String singleMethod ) {
        Helpers.cleanTempDirectory();
        try {
            settings = new Settings( Helpers.writeSettingsFile( tempDir, runUnitTests, runFunctionTests, runLoadTests, initialPackageName, finalPackageName, singlePackageName, logToConsole, logToFile, logFileName, failFast, initialClassName, finalClassName, singleClassName, initialMethod, finalMethod, singleMethod ) );
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Could not write settings file, see exception above.";
        }
    }

    private static NameFilter nf( NameFilter.Type type, String lower, String upper, String exact ) {
        return new NameFilter( type, lower, upper, exact );
    }
}