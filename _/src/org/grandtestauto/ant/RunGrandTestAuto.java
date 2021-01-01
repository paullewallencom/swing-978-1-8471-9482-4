/****************************************************************************
 *
 * Name: RunGrandTestAuto.java
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
package org.grandtestauto.ant;

import org.apache.tools.ant.*;
import org.grandtestauto.*;

/**
 * Ant task to run <code>GrandTestAuto</code>.
 * <p/>
 * The parameters are:
 * <table border="1" cellpadding="2" cellspacing="0">
 * <tr>
 * <td><b>Attribute</b></td>
 * <td><b>Description</b></td>
 * <td><b>Required</b></td>
 * </tr>
 * <tr>
 * <td>classesRoot</td>
 * <td>The directory containing the class files of the classes to be tested. Typically,
 * this is the output directory of the compilation step.</td>
 * <td>No. Default value is <code>System.getProperty( "user.dir" )</code></td>
 * <tr>
 * <tr>
 * <td>settingsFileName</td>
 * <td>If set, then test results will be logged to the named file. If not set,
 * test results will not be logged to file.</td>
 * <td>No.</td>
 * <tr>
 * <table>
 *
 * @author Tim Lavers
 */
public class RunGrandTestAuto extends Task {

    private String settingsFileName;

    /**
     * Sets the <code>settingsFileName</code> property.
     *
     * @param str name of the settings file.
     */
    public void setSettingsFileName( String str ) {
        settingsFileName = str;
    }

    /**
     * Creates a <code>GrandTestAuto</code> and runs it. If the tests do not pass,
     * a <code>BuildException</code> is thrown. The message in the exception
     * indicates this.
     *
     * @throws BuildException to indicate either that the tests could not be run or detected a bug.
     */
    public void execute() throws BuildException {
        try {
            Settings s = new Settings( settingsFileName );
            GrandTestAuto gta = new GrandTestAuto( s );
            boolean result = gta.runAllTests();
            String msg = Messages.message( Messages.OPK_OVERALL_GTA_RESULT, Messages.passOrFail( result ) );
            //If we're running this in its unit tests, the underlying project
            //is null, causing log() to fail. So do a check here.
            if (this.getProject() != null) {
                log( msg );
            }
            //Throw a build exception if the unit tests fail.
            if (!result) {
                throw new BuildException( msg );
            }
        } catch (Exception e) {
            throw new BuildException( e );
		}
	}
}