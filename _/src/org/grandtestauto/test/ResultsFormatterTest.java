package org.grandtestauto.test;

import java.util.logging.*;

import org.grandtestauto.*;

/**
 * Unit test for <code>ResultsFormatter</code>.
 *
 * @author Tim Lavers
 */
public class ResultsFormatterTest {

	public boolean formatTest() throws InterruptedException {
        ResultsFormatter rf = new ResultsFormatter();
        String str = "I met a traveller";
        LogRecord lr = new LogRecord( Level.FINE, str );
        Helpers.assertEqual(  rf.format( lr ),  str + Helpers.NL );

        //Create a throwable in a new thread so that we've got a pretty good handle on what its stack trace is.
        final Throwable[] thr = new Throwable[1];
        Thread throwableCreator = new Thread( "ThrowableCreator") {
            public void run() {
                thr[0] = new Throwable( "T");
            }
        };
        throwableCreator.start();
        throwableCreator.join();
        lr = new LogRecord( Level.SEVERE, str );
        lr.setThrown( thr[0]);
        String formatted = rf.format( lr );
        String prefix = str + Helpers.NL + "java.lang.Throwable: T";
        prefix += Helpers.NL + "\t at " + getClass().getName() + "$1";
        assert formatted.startsWith( prefix );

        return true;
    }

	public boolean constructorTest() {
		//Constructor is exercised by formatTest(),
		//so just return true.
		return true;
	}
}