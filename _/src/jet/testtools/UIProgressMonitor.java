package jet.testtools;

import javax.swing.*;
import java.awt.*;

/**
 * UI Wrapper class for a Swing progress monitor, as discussed
 * in Chapter 10. This is a good example of reading the state
 * of a widget that we don't have access to.
 *
 * @author Tim Lavers
 */
public class UIProgressMonitor {

    private JProgressBar bar;
    private JLabel messageLabel;
    private JLabel noteLabel;

    public UIProgressMonitor() {
        final JProgressBar[] barHolder = new JProgressBar[1];
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                barHolder[0] =
                        UI.findProgressBarThatIsCurrentlyShowing();
                return barHolder[0] != null;
            }
        }, 10000 );
        bar = barHolder[0];
        assert (bar != null) : "Could not find a bar!";

        //From the JProgressBar we can climb the containment
        //tree to the parent, which directly contains the
        //labels in which we are interested.
        Container parent = bar.getParent();
        Component[] allComponents = parent.getComponents();
        messageLabel = (JLabel) allComponents[0];
        noteLabel = (JLabel) allComponents[1];
    }

    public JProgressBar bar() {
        return bar;
    }

    public void cancel() {
        new Cyborg().activateFocussedButton();
    }

    public boolean waitForProgress( final int value, final long timeout ) {
        return Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return getValue() >= value;
            }
        }, timeout );
    }

    public String getNote() {
        final String[] resultHolder = new String[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = noteLabel.getText();
            }
        } );
        return resultHolder[0];
    }

    public String message() {
        final String[] resultHolder = new String[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = messageLabel.getText();
            }
        } );
        return resultHolder[0];
    }

    public int getValue() {
        final int[] resultHolder = new int[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = bar.getValue();
            }
        } );
        return resultHolder[0];
    }

    public int getMinimum() {
        final int[] resultHolder = new int[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = bar.getMinimum();
            }
        } );
        return resultHolder[0];
    }

    public int getMaximum() {
        final int[] resultHolder = new int[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = bar.getMaximum();
            }
        } );
        return resultHolder[0];
    }
}
