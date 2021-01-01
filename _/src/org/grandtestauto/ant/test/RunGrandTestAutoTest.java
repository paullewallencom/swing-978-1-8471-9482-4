/****************************************************************************
 *
 * Name: RunGrandTestAutoTest.java
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
package org.grandtestauto.ant.test;

import jet.testtools.test.org.grandtestauto.*;
import org.apache.tools.ant.*;
import org.grandtestauto.ant.*;
import org.grandtestauto.test.*;

import java.io.*;

/**
 * Unit test for <code>RunGrandTestAuto</code>.
 *
 * @author Tim Lavers
 */
public class RunGrandTestAutoTest {

    private static String pingString;

    public static void ping( Class c ) {
        pingString = c.getName();     
    }

    public boolean constructorTest() {
        //The single constructor is tested quite
        //effectively in executeTest().
        return true;
    }

    public boolean executeTest() throws Exception {
        boolean result = true;
        //This test works by expanding a configured class hierarchy
        //into the testing temp dir. A RunGrandTestAuto is created
        //and set to run GTA on this dir. The test class is defined to
        //call ping(). The test will pass if ping() has been seen
        //to be called.
        Helpers.setupForZip( Grandtestauto.test9_zip );
        RunGrandTestAuto rgta = new RunGrandTestAuto();
        File settingsFile = new File( Helpers.tempDirectory(), "TestSettings.txt");
        rgta.setSettingsFileName( settingsFile.getAbsolutePath() );
        rgta.execute();
        result &= pingString.equals( "a9.test.XTest" );

        //Note that the above test checks that if the tests pass,
        //the execute method returns normally. Now we check that
        //if the tests return false, a BuildException is thrown.
        Helpers.setupForZip( Grandtestauto.test10_zip );
        rgta = new RunGrandTestAuto();
        try {
            rgta.execute();
            //If we got here, no exception was thrown.
            result = false;
            assert false : "RunGrandTestAuto should have thrown an exception";
        } catch (BuildException be) {
            //The expected error message.
//			String str =  Messages.message( Messages.OPK_GTA_RESULT, Messages.passOrFail( false ) );
//			if (!be.getMessage().equals( str )) {
//				result = false;
//				assert false : "Wrong error message";
//			}
        }

        return result;
    }

    public boolean setSettingsFileNameTest() {
        //This is tested quite effectively in executeTest().
        return true;
    }
}