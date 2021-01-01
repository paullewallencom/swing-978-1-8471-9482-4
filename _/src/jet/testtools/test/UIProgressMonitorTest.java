package jet.testtools.test;

import jet.testtools.UI;
import jet.testtools.Waiting;
import jet.testtools.UIProgressMonitor;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class UIProgressMonitorTest extends TestBase {
    private ProgressMonitor monitor;
    private UIProgressMonitor ui;

    public boolean constructorTest() {
        //We've already tested succesful construction
        //in every other test, so now test a failed
        //construction.
        String errorMessage = null;
        try {
            new UIProgressMonitor();
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.equals( "Could not find a bar!");
        return true;
    }

    public boolean barTest() {
        init( );
        assert ui.bar() != null;
        cleanup();
        return true;
    }

    public boolean getValueTest() {
        init();
        assert ui.getValue() == 8;
        cleanup();
        return true;
    }

    public boolean getMinimumTest() {
        init();
        assert ui.getMinimum() == 1;
        cleanup();
        return true;
    }

    public boolean getMaximumTest() {
        init();
        assert ui.getMaximum() == 32;
        cleanup();
        return true;
    }

    public boolean getNoteTest() {
        init();
        assert ui.getNote().equals( "Note" );
        cleanup();
        return true;
    }

    public boolean messageTest() {
        init();
        assert ui.message().equals( "Message" );
        cleanup();
        return true;
    }

    public boolean cancelTest() {
        init();
        assert !isCancelled();
        ui.cancel();
        assert isCancelled();
        cleanup();
        return true;
    }

    public boolean waitForProgressTest() {
        init();
        //First a wait that will time out.
        assert !ui.waitForProgress( 20, 2000 );
        //Now one that will succeed. Start a thread to
        //steadily increment the progress.
        new Thread( "ProgressMaker") {
            public void run() {
                for (int i=9; i<22; i++) {
                    Waiting.pause( 50 );
                    setProgress( i );
                }
            }
        }.start();
        assert ui.waitForProgress( 20, 3000 );

        //Check that the wait is over when the progress
        //is exactly that expected.
        setProgress( 25 );
        assert ui.waitForProgress( 25, 3000 );

        //Check that the wait is over when the progress
        //exceeds that expected.
        setProgress( 29 );
        assert ui.waitForProgress( 28, 3000 );
        cleanup();
        return true;
    }

    private boolean isCancelled() {
        final boolean[] resultHolder = new boolean[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = monitor.isCanceled();
            }
        } );
        return resultHolder[0];
    }

    void setProgress( final int n ) {
        UI.runInEventThread( new Runnable() {
            public void run() {
                monitor.setProgress( n );
            }
        } );
    }

    void init( ) {
        Runnable setupTask = new Runnable() {
            public void run() {
                JPanel panel = new JPanel();
                panel.setSize( 100, 100 );
                setupFrame( panel, new Dimension( 200, 200 ) );
                monitor = new ProgressMonitor( panel, "Message", "Note", 1, 32 );
            }
        };
        UI.runInEventThread( setupTask );
        //Create a thread that will create the ui object.
        Thread creator = new Thread( "UICreator" ) {
            public void run() {
                ui = new UIProgressMonitor();
            }
        };
        creator.start();
        //Do some progress. This will cause the monitor to show,
        //so that the UIProgressMonitor constructor can
        //find the JProgressBar.
        for (int i=1; i< 9; i++) {
            Waiting.pause( 100 );
            setProgress( i );
        }
        try {
            creator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
