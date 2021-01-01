/****************************************************************************
*
* Name: ResultsFormatter.java
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

import java.util.logging.*;


/**
 * Formats log messages into a format suitable for display to the user.
 *
 * @author Tim Lavers
 */
public class ResultsFormatter extends Formatter {

	/**
	 * Returns <code>record.getMessage()</code> with a newline appended.
	 */
	public String format( LogRecord record ) {
	    StringBuffer result = new StringBuffer();
		result.append( record.getMessage() );
		result.append( Messages.nl() );
        Throwable thrown = record.getThrown();
        if (thrown != null) {
            result.append( thrown );
            result.append( Messages.nl() );
            for (StackTraceElement ste : thrown.getStackTrace()) {
                result.append( "\t at ");
                result.append( ste);
                result.append( Messages.nl() );
            }
        }
        return result.toString();
	}
}