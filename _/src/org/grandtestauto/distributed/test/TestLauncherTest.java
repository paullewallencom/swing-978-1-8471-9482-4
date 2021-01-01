package org.grandtestauto.distributed.test;

import jet.testtools.*;
import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;
import org.grandtestauto.distributed.*;
import org.grandtestauto.test.*;
import org.grandtestauto.util.*;

import java.io.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class TestLauncherTest {

    private File baseDir;
    private File classesDir;
    private List<String> logged;
    private C controller;

    private void init( String zipName ) {
        File zip = new File( zipName );
        Helpers.cleanTempDirectory();
        File temp = Helpers.tempDirectory();
        //Note that we are putting spaces into the dir names, just to check that they are handled properly.
        baseDir = new File( temp, "base dir" );
        baseDir.mkdirs();
        classesDir = new File( temp, "classes dir" );
        classesDir.mkdirs();
        Helpers.expandZipTo( zip, classesDir );
        logged = new LinkedList<String>( );
        controller = new C();
    }

    public boolean constructorTest() throws IOException {
        //This first basic test checks that the classpath is handled properly,
        //that the settings file is created properly and that the base dir
        //is handled properly and cleaned.
        init( Grandtestauto.test1_zip );
        String classPath = classesDir.getAbsolutePath();
        //Write a file into the basedir so that we can check this dir is cleaned.
        File junkDir = new File( baseDir, "junk" );
        junkDir.mkdirs();
        File junkFile = new File( baseDir, "Junk.txt" );
        Files.writeIntoFile( "This is some junk text.", junkFile );
        assert junkFile.exists();
        TestLauncher launcher = new TestLauncher( controller, classPath, baseDir, "" );
        assert !junkFile.exists() : "Base dir not cleaned, still contains '" + junkFile.getAbsolutePath() + "'";
        //There should be a GTASettings file written - but not yet.
        File settings = new File( baseDir, "GTASettings.txt" );
        assert !settings.exists();
        Process p = launcher.startTestJVM( "a1.b", classesDir );
        assert settings.exists();
        String gtaContents = Files.contents( settings );
        assert gtaContents.contains( Settings.SINGLE_PACKAGE + "=a1.b" );
        String[] streams = ProcessReader.readProcess( p );
        String expectedMessage = Messages.message( Messages.TPK_UNIT_TEST_PACK_RESULTS, "a1.b", "failed" );
        assert streams[1].contains( expectedMessage );
        assert streams[0].isEmpty();

        return true;
    }

    public boolean startTestJVMTest() throws IOException {
        //Check that if the wrong classpath is given,
        //the test doesn't run.
        init( Grandtestauto.test1_zip );
        String classPath = baseDir.getAbsolutePath();//Deliberately wrong.
        TestLauncher launcher = new TestLauncher( controller, classPath, baseDir, "" );
        Process p = launcher.startTestJVM( "a1.b", classesDir );
        String[] streams = ProcessReader.readProcess( p );
        String message = Messages.message( Messages.OPK_COULD_NOT_FIND_CLASS, "a1.b.X" );
        assert streams[1].contains( message );

        //Now check that we can choose which package to run using the package argument.
        init( Grandtestauto.test1_zip );
        classPath = classesDir.getAbsolutePath();//Now correct.
        launcher = new TestLauncher(controller, classPath, baseDir, "" );
        p = launcher.startTestJVM( "a1.b", classesDir );
        streams = ProcessReader.readProcess( p );
        message = Messages.message( Messages.TPK_UNIT_TEST_PACK_RESULTS, "a1.b", "failed" );
        assert streams[1].contains( message );

        //Now check that the jvm options list is honoured.
        //One of the unit tests in a94.test has these two lines
        //of code:
        //String property = System.getProperty( "PropertySetInTestLauncherTest" );
        //System.out.println( property );
        init( Grandtestauto.test94_zip );
        classPath = classesDir.getAbsolutePath();//Now correct.
        launcher = new TestLauncher( controller, classPath, baseDir, "-DPropertySetInTestLauncherTest=PropertySetInTestLauncherTestValue" );
        p = launcher.startTestJVM( "a94", classesDir );
        streams = ProcessReader.readProcess( p );
        assert streams[0].contains( "PropertySetInTestLauncherTestValue" );

        return true;
    }

    private class C implements TestLauncher.Controller {
        public void log( String str ) {
            logged.add(str);
        }
    }
}
