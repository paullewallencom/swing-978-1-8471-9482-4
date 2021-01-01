/****************************************************************************
 *
 * Name: Settings.java
 *
 * Synopsis: See javadoc class comments.
 *
 * Description: See javadoc class comments.
 *
 * Copyright 2002 Timothy Gordon Lavers (Australia)
 *
 *                          The Wide Open License (WOL)
 *
 * Permission to use, copy, modify, distribute and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice and this license appear in all source copies.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT EXPRESS OR IMPLIED WARRANTY OF
 * ANY KIND. See http://www.dspguru.com/wol.htm for more information.
 *
 *****************************************************************************/
package org.grandtestauto;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * The parameters for the invocation of <code>GrandTestAuto</code>.
 * These may set in a properties file, with default values used for
 * missing properties, or set at construction time.
 * The properties and their defaults are:
 * <p/>
 * <table border="2" cellpadding="2" width="80%">
 * <tr>
 * <th align="left">Property</th>
 * <th align="left">Purpose</th>
 * <th align="left">Default value</th>
 * </tr>
 * <tr>
 * <td>{@link #CLASSES_ROOT} </td>
 * <td>The name of the root of the classes directory
 * containing the classes to be tested. </td>
 * <td><code>System.getProperty( &quot;user.dir&quot; )</code>
 * </td>
 * </tr>
 * <tr>
 * <td>{@link #LOG_TO_FILE} </td>
 * <td>Whether the results should be logged to a file (as
 * well as to the console) </td>
 * <td>true </td>
 * </tr>
 * <tr>
 * <td>{@link #LOG_FILE_NAME} </td>
 * <td>The name of the file to which results are logged. </td>
 * <td>{@link #DEFAULT_LOG_FILE_NAME} </td>
 * </tr>
 * </table>
 * <br>
 *
 * @author Tim Lavers
 */
public class Settings {

    /**
     * Key in the settings file for the root of the classes.
     */
    public static final String CLASSES_ROOT = "CLASSES_ROOT";

    /**
     * Key in the settings file for whether to log to file.
     */
    public static final String LOG_TO_FILE = "LOG_TO_FILE";

    /**
     * Key in the settings file for whether to log to the console.
     */
    public static final String LOG_TO_CONSOLE = "LOG_TO_CONSOLE";

    /**
     * Key in the settings file for the name of the log file.
     */
    public static final String LOG_FILE_NAME = "LOG_FILE_NAME";

    /**
     * Key in the settings file for the name of the package from which to run the tests.
     */
    public static final String INITIAL_PACKAGE = "FIRST_PACKAGE";

    /**
     * Key in the settings file for the name of the package to  which to run the tests.
     */
    public static final String FINAL_PACKAGE = "LAST_PACKAGE";

    /**
     * Key in the settings file for the name of the single test package to run.
     */
    public static final String SINGLE_PACKAGE = "SINGLE_PACKAGE";

    /**
     * Key in the settings file for whether or not to run unit tests.
     */
    public static final String RUN_UNIT_TESTS = "RUN_UNIT_TESTS";

    /**
     * Key in the settings file for whether or not to run load tests.
     */
    public static final String RUN_LOAD_TESTS = "RUN_LOAD_TESTS";

    /**
     * Key in the settings file for whether to stop testing when a UnitTester or AutoLoadTest fails..
     */
    public static final String RUN_FUNCTION_TESTS = "RUN_FUNCTION_TESTS";

    /**
     * Key in the settings file for whether to stop testing after the first UnitTester or AutoLoadTest to return false or throw an exception..
     */
    public static final String STOP_AT_FIRST_FAILURE = "FAIL_FAST";

    /**
     * Key in the settings file for the name of the first test to be run within a single package of tests to be run.
     */
    public static final String INITIAL_CLASS_WITHIN_SINGLE_PACKAGE = "FIRST_CLASS";

    /**
     * Key in the settings file for the name of the last test to be run within a single package of tests to be run.
     */
    public static final String FINAL_CLASS_WITHIN_SINGLE_PACKAGE = "LAST_CLASS";

    /**
     * Key in the settings file for the name of the single test to be run within a single package of tests to be run.
     */
    public static final String SINGLE_CLASS_WITHIN_SINGLE_PACKAGE = "SINGLE_CLASS";

    /**
     * Key in the settings file for the name of the first test method to be run within a single unit to be run.
     */
    public static final String INITIAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS = "FIRST_METHOD";

    /**
     * Key in the settings file for the name of the last test method to be run within a single unit to be run.
     */
    public static final String FINAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS = "LAST_METHOD";

    /**
     * Key in the settings file for the name of the single test method to be run withing a single unit test to be run.
     */
    public static final String SINGLE_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS = "SINGLE_METHOD";

    /**
     * Key to select a less verbose form of logging used by the distributed GTA system.
     */
    public static final String LESS_VERBOSE = "LESS_VERBOSE";

    /**
     * Key in the settings file for the name of the file into which all results are collated in the distributed GTA system.
     */
    public static final String COLLATED_RESULTS_FILE_NAME = "COLLATED_RESULTS_FILE_NAME";

    /**
     * The name for the collated results file, if not set explicitly.
     */
    public static final String DEFAULT_COLLATED_RESULTS_FILE_NAME = "CollatedResults.txt";

    /**
     * The key for the port on which to export the GTADistributor, if doing distributed testing.
     */
    public static final String DISTRIBUTOR_PORT= "DISTRIBUTOR_PORT";

    /**
     * The distributor service port, if not set explicitly.
     */
    public static final int DEFAULT_DISTRIBUTOR_PORT = 8062;

    /**
     * The name for the results file, if not set explicitly.
     */
    public static final String DEFAULT_LOG_FILE_NAME = "GTAResults.txt";

    private static Set<String> LEGITIMATE_KEYS;

    public static String NL = System.getProperty( "line.separator" );

    static {
        LEGITIMATE_KEYS = new HashSet<String>();
        Field[] fields = Settings.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals( "DEFAULT_LOG_FILE_NAME" )) continue;
            int m = field.getModifiers();
            if (Modifier.isFinal( m ) && Modifier.isPublic( m ) && Modifier.isStatic( m )) {
                try {
                    LEGITIMATE_KEYS.add( field.get( null ).toString() );
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    //These are all public. This will not happen.
                }
            }
        }
    }

    /**
     * The classes root.
     */
    private File classesRoot;

    /**
     * Log to file?
     */
    private boolean logToFile;

    /**
     * Log to the console?
     */
    private boolean logToConsole;

    /**
     * The name of the file to which we are logging results.
     */
    private String resultsFileName = DEFAULT_LOG_FILE_NAME;
    private String initialPackageName;
    private String finalPackageName;
    private boolean runUnitTests = true;
    private boolean runFunctionTests = true;
    private boolean runLoadTests = true;
    private String singlePackageName;
    private boolean stopAtFirstFailure;
    private NameFilter packageNameFilter = new NameFilter( NameFilter.Type.PACKAGE, null, null, null );
    private NameFilter classNameFilter = new NameFilter( NameFilter.Type.CLASS, null, null,null );
    private NameFilter methodNameFilter = new NameFilter( NameFilter.Type.METHOD, null, null, null );
    private SortedSet<Name> packagesAsNames;
    private Set<String> unknownKeys = new HashSet<String>();
    private String initialClassWithinSinglePackage;
    private String finalClassWithinSinglePackage;
    private String singleClassWithinSinglePackage;
    private String initialMethod;
    private String finalMethod;
    private String singleMethod;
    private boolean lessVerboseLogging = false;
    private String collatedResultsFileName = DEFAULT_COLLATED_RESULTS_FILE_NAME;
    private int distributorPort= DEFAULT_DISTRIBUTOR_PORT;

    /**
     * A <code>Settings</code> that can be used to produce model settings
     * using {@link  #commentedPropertiesWithTheseValues()},  for showing to the user.
     */
    public Settings() {
        classesRoot = new File( System.getProperty( "user.dir" ) );
        packageNameFilter = new NameFilter( NameFilter.Type.PACKAGE, null, null, null );
    }

    /**
     * Creates a <code>Settings</code> that gets its parameters
     * from the named file. Defauts are used for missing properties.
     *
     * @throws IOException if the settings file can't be read or if the log file is needed but cannot be opened.
     */
    public Settings( String settingsFileName ) throws IOException {
        Properties props = new Properties();
        File f = new File( settingsFileName );
        InputStream is = new BufferedInputStream( new FileInputStream( f ) );
        props.load( is );
        //Load the instance variables.
        classesRoot = new File( props.getProperty( CLASSES_ROOT, System.getProperty( "user.dir" ) ) );
        packagesAsNames = Name.subDirectoriesAsNames( classesRoot );
        logToFile = valueOf( props.getProperty( LOG_TO_FILE, "true" ) );
        logToConsole = valueOf( props.getProperty( LOG_TO_CONSOLE, "true" ) );
        resultsFileName = props.getProperty( LOG_FILE_NAME, DEFAULT_LOG_FILE_NAME ).trim();
        initialPackageName = getFirstMatchingPackage( getStringValue( INITIAL_PACKAGE, props ) );
        finalPackageName = getFirstMatchingPackage( getStringValue( FINAL_PACKAGE, props ) );
        singlePackageName = getFirstMatchingPackage( getStringValue( SINGLE_PACKAGE, props ) );
        packageNameFilter = new NameFilter( NameFilter.Type.PACKAGE, initialPackageName, finalPackageName, singlePackageName );
        if (singlePackageName != null) {
            initialClassWithinSinglePackage = getStringValue( INITIAL_CLASS_WITHIN_SINGLE_PACKAGE, props );
            finalClassWithinSinglePackage = getStringValue( FINAL_CLASS_WITHIN_SINGLE_PACKAGE, props );
            singleClassWithinSinglePackage = getStringValue( SINGLE_CLASS_WITHIN_SINGLE_PACKAGE, props );
            classNameFilter = new NameFilter( NameFilter.Type.CLASS, initialClassWithinSinglePackage, finalClassWithinSinglePackage, singleClassWithinSinglePackage );
            if (singleClassWithinSinglePackage != null) {
                initialMethod = getStringValue( INITIAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, props );
                finalMethod = getStringValue( FINAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, props );
                singleMethod = getStringValue( SINGLE_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS, props );
                methodNameFilter = new NameFilter( NameFilter.Type.METHOD, initialMethod, finalMethod, singleMethod );
            }
        }
        runUnitTests = valueOf( props.getProperty( RUN_UNIT_TESTS, "true" ) );
        runFunctionTests = valueOf( props.getProperty( RUN_FUNCTION_TESTS, "true" ) );
        runLoadTests = valueOf( props.getProperty( RUN_LOAD_TESTS, "true" ) );
        stopAtFirstFailure = valueOf( props.getProperty( STOP_AT_FIRST_FAILURE, "false" ) );
        lessVerboseLogging = valueOf( props.getProperty( LESS_VERBOSE, "false" ) );
        collatedResultsFileName = props.getProperty( COLLATED_RESULTS_FILE_NAME, DEFAULT_COLLATED_RESULTS_FILE_NAME ).trim();
        try {
            distributorPort = Integer.parseInt( props.getProperty( DISTRIBUTOR_PORT ) );
        } catch (Exception e) {
            //Doesn't matter, use default.
        }

        //Make sure that there are no unknown keys in the properties file.
        //A mis-spelt key can waste a lot of time.
        for (Object key : props.keySet()) {
            if (!LEGITIMATE_KEYS.contains( key.toString() )) {
                unknownKeys.add( key.toString() );
            }
        }
    }

    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append( Messages.message( runUnitTests ? Messages.SK_WILL_RUN_UNIT_TESTS : Messages.SK_WILL_NOT_RUN_UNIT_TESTS ) );
        sb.append( NL );
        sb.append( Messages.message( runFunctionTests ? Messages.SK_WILL_RUN_FUNCTION_TESTS : Messages.SK_WILL_NOT_RUN_FUNCTION_TESTS ) );
        sb.append( NL );
        sb.append( Messages.message( runLoadTests ? Messages.SK_WILL_RUN_LOAD_TESTS : Messages.SK_WILL_NOT_RUN_LOAD_TESTS ) );
        sb.append( NL );
        addToSummary( packageNameFilter.loggingMessage(), sb );
        addToSummary( classNameFilter.loggingMessage(), sb );
        addToSummary( methodNameFilter.loggingMessage(), sb );
        if (stopAtFirstFailure) {
            sb.append( Messages.message( Messages.SK_FAIL_FAST ) );
            sb.append( NL );
        }
        return sb.toString();
    }

    private void addToSummary( String valueOrEmpty, StringBuilder sb ) {
        if (valueOrEmpty.length() > 0 ) {
            sb.append( valueOrEmpty );
            sb.append( NL );
        }
    }

    /**
     * Accepts the names of classes based on the values for <code>INITIAL_CLASS_WITHIN_SINGLE_PACKAGE</CODE>,
     * <code>FINAL_CLASS_WITHIN_SINGLE_PACKAGE</CODE> and <code>SINGLE_CLASS_WITHIN_SINGLE_PACKAGE</code>.
     */
    public NameFilter classNameFilter() {
        return classNameFilter;
    }

    /**
     * Accepts the names of tests based on the values for <code>INITIAL_PACKAGE</CODE>,
     * <code>FINAL_PACKAGE</code> and <code>SINGLE_PACKAGE</code>.
     */
    public NameFilter packageNameFilter() {
        return packageNameFilter;
    }

    /**
     * Determines which methods are run, based on, based on the values of <code>INITIAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS</CODE>,
     * <code>FINAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS</code> and <code>SINGLE_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS</code>,
     * will be null unless  <code>SINGLE_PACKAGE</code> and  <code>SINGLE_CLASS_WITHIN_SINGLE_PACKAGE</code> are set (as well
     * as at least one of the method properties).
     */
    public NameFilter methodNameFilter() {
        return methodNameFilter;
    }

    /**
     * The directory containing all of the classes in the project being tested.
     */
    public File classesDir() {
        return classesRoot;
    }

    /**
     * Should results be logged to the console?
     */
    public boolean logToConsole() {
        return logToConsole;
    }

    /**
     * Should execution of tests cease after a UnitTester or AutoLoadTest returns
     * false or throws an Exception?
     */
    public boolean stopAtFirstFailure() {
        return stopAtFirstFailure;
    }

    /**
     * Null if the log to file value is false, else either the value for the log file
     * property, or the default value.
     */
    public String resultsFileName() {
        return logToFile ? resultsFileName : null;
    }

    /**
     * Are the unit tests to be run?
     */
    public boolean runUnitTests() {
        return runUnitTests;
    }

    /**
     * Are the function tests to be run?
     */
    public boolean runFunctionTests() {
        return runFunctionTests;
    }

    /**
     * Are the load tests to be run?
     */
    public boolean runLoadTests() {
        return runLoadTests;
    }

    /**
     * Any keys in the properties file from which this was created, that are not
     * known keys, are stored here, so that they can be shown to the user.
     */
    public Set<String> unknownKeys() {
        return unknownKeys;
    }

    /**
     * Should the logging exclude the GTA introduction and other verbose productions.
     * @return the value of the key <code>LESS_VERBOSE</code>.
     */
    public boolean lessVerboseLogging() {
        return lessVerboseLogging;
    }

    /**
     * The name of the file into which results are collated in the distributed system.
     * @return the value of the key <code>COLLATED_RESULTS_FILE_NAME</code> or
     * <code>DEFAULT_COLLATED_RESULTS_FILE_NAME</code> if not specified.
     */
    public String collatedResultsFileName() {
        return collatedResultsFileName;
    }

    /**
     * The port on which the GTADistributor will be exported.
     */
    public int distributorPort() {
        return distributorPort;
    }

    public String commentedPropertiesWithTheseValues() {
        ResourceBundle commentsRB = PropertyResourceBundle.getBundle( getClass().getName() );
        StringBuilder sb = new StringBuilder();
        sb.append( commentsRB.getString( "INTRO1" ) );
        sb.append( NL );
        sb.append( commentsRB.getString( "INTRO2" ) );

        addCommentAndKey( sb, commentsRB, CLASSES_ROOT );
        String pathStr = "";
        try {
            sb.append( classesDir().getCanonicalPath().replace( '\\', '/' ) );
        } catch (Exception e) {
            //Don't care.
        }
        sb.append( pathStr );

        addCommentAndKey( sb, commentsRB, LOG_TO_FILE );
        sb.append( logToFile );

        addCommentAndKey( sb, commentsRB, LOG_TO_CONSOLE );
        sb.append( logToConsole );

        addCommentAndKey( sb, commentsRB, LOG_FILE_NAME );
        sb.append( resultsFileName );

        addCommentAndKey( sb, commentsRB, INITIAL_PACKAGE );
        sb.append( valueOrBlank( initialPackageName ) );

        addCommentAndKey( sb, commentsRB, FINAL_PACKAGE );
        sb.append( valueOrBlank( finalPackageName ) );

        addCommentAndKey( sb, commentsRB, SINGLE_PACKAGE );
        sb.append( valueOrBlank( singlePackageName ) );

        addCommentAndKey( sb, commentsRB, RUN_UNIT_TESTS );
        sb.append( runUnitTests );

        addCommentAndKey( sb, commentsRB, RUN_FUNCTION_TESTS );
        sb.append( runFunctionTests );

        addCommentAndKey( sb, commentsRB, RUN_LOAD_TESTS );
        sb.append( runLoadTests );

        addCommentAndKey( sb, commentsRB, STOP_AT_FIRST_FAILURE );
        sb.append( stopAtFirstFailure );

        addCommentAndKey( sb, commentsRB, INITIAL_CLASS_WITHIN_SINGLE_PACKAGE );
        sb.append( valueOrBlank( initialClassWithinSinglePackage ) );

        addCommentAndKey( sb, commentsRB, FINAL_CLASS_WITHIN_SINGLE_PACKAGE );
        sb.append( valueOrBlank( finalClassWithinSinglePackage ) );

        addCommentAndKey( sb, commentsRB, SINGLE_CLASS_WITHIN_SINGLE_PACKAGE );
        sb.append( valueOrBlank( singleClassWithinSinglePackage ) );

        addCommentAndKey( sb, commentsRB, INITIAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS );
        sb.append( valueOrBlank( initialMethod ) );

        addCommentAndKey( sb, commentsRB, FINAL_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS );
        sb.append( valueOrBlank( finalMethod ) );

        addCommentAndKey( sb, commentsRB, SINGLE_METHOD_WITHIN_SINGLE_UNIT_TEST_CLASS );
        sb.append( valueOrBlank( singleMethod ) );
        return sb.toString();
    }

    private String valueOrBlank( String str ) {
        return str != null ? str : "";
    }

    private void addCommentAndKey( StringBuilder sb, ResourceBundle commentsRB, String key ) {
        sb.append( NL );
        sb.append( NL );
        sb.append( '#' );
        sb.append( commentsRB.getString( key ) );
        sb.append( NL );
        sb.append( key );
        sb.append( '=' );
    }

    private String getFirstMatchingPackage( String propertiesValue ) {
        if (propertiesValue == null) return null;
        Name name = new Name( propertiesValue );
        for (Name packageName : packagesAsNames) {
            if (name.matches( packageName )) return packageName.toString();
        }
        return propertiesValue;
    }

    private String getStringValue( String key, Properties props ) {
        String result = props.getProperty( key, null );
        if ("".equals( result )) result = null;
        if (result != null) result = result.trim();
        return result;
    }

    private static boolean valueOf( String s ) {
        return s.toLowerCase().equals( "t" ) || Boolean.valueOf( s );
    }
}