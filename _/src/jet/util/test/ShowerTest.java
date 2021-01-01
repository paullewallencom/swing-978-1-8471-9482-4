package jet.util.test;

import jet.testtools.Waiting;
import jet.util.Shower;

import javax.swing.*;

public class ShowerTest {
    private DummyShowable ds;
    private Shower shower;

    public boolean constructorTest() {
        init( true );
        assert shower.isDaemon();
        assert shower.getName().equals( "Shower" );
        return true;
    }

    public boolean waitForShowToFinishTest() {
        init( false );
        //Not started so times out.
        assert !shower.waitForShowToFinish();
        shower.start();
        //Wait is over in 100 ms.
        assert shower.waitForShowToFinish();

        init( false, 500 );
        shower.start();
        //Wait is over in 500 ms.
        assert shower.waitForShowToFinish();

        init( false, 1500 );
        shower.start();
        //Wait times out.
        assert !shower.waitForShowToFinish();

        init( true );
        //Not started so times out.
        assert !shower.waitForShowToFinish();
        shower.start();
        //Wait is over in 100 ms.
        assert shower.waitForShowToFinish();

        init( true, 500 );
        shower.start();
        //Wait is over in 500 ms.
        assert shower.waitForShowToFinish();

        init( true, 1500 );
        shower.start();
        //Wait times out.
        assert !shower.waitForShowToFinish();
        return true;
    }

    public boolean returnedValueTest() {
        init( false );
        ds.returnValue = new Integer( 55 );
        assert shower.returnedValue() == null;
        shower.start();
        assert shower.returnedValue() == null;
        shower.waitForShowToFinish();
        assert shower.returnedValue() == ds.returnValue;
        return true;
    }

    public boolean runTest() {
        init( false );
        shower.start();
        shower.waitForShowToFinish();
        assert ds.runWasInEventThread;

        init( true );
        shower.start();
        shower.waitForShowToFinish();
        assert !ds.runWasInEventThread;
        return true;
    }

    private void init( boolean isThreadSafe ) {
        init( isThreadSafe, 100 );
    }

    private void init( boolean isThreadSafe, long pause ) {
        ds = new DummyShowable( pause );
        shower = new Shower<Object>( ds, isThreadSafe );
    }

    class DummyShowable implements Shower.Showable<Object> {
        Object returnValue;
        Object lock = new Object();
        boolean runWasInEventThread = false;

        public DummyShowable( long pause ) {
            this.pause = pause;
        }

        long pause;

        public Object show() {
            runWasInEventThread = SwingUtilities.isEventDispatchThread();
            Waiting.pause( pause );
            return returnValue;
        }
    }
}
