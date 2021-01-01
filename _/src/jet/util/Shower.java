package jet.util;

import jet.testtools.UI;
import jet.testtools.Waiting;

/**
 * A thread that can be used to show modal dialogs and frames.
 */
public class Shower<T> extends Thread {

    public static interface Showable<T> {
        public T show();
    }

    private volatile boolean isAwakened;
    private Showable<T> showable;
    private boolean showIsSwingThreadSafe;
    private T returnedValue;

    public Shower(Showable<T> showable, boolean showIsSwingThreadSafe ) {
        super( "Shower" );
        setDaemon( true );
        this.showable = showable;
        this.showIsSwingThreadSafe = showIsSwingThreadSafe;
    }

    public void run() {
        if (showIsSwingThreadSafe) {
            returnedValue = showable.show();
        } else {
            UI.runInEventThread( new Runnable() {
                public void run() {
                    returnedValue = showable.show();
                }
            } );
        }
        isAwakened = true;
    }

    public T returnedValue() {
        return returnedValue;
    }

    public boolean waitForShowToFinish() {
        return Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return isAwakened;
            }
        }, 1000 );
    }
}
