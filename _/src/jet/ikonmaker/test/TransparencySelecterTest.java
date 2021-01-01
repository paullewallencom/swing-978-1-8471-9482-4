package jet.ikonmaker.test;

import jet.ikonmaker.TransparencySelecter;
import jet.testtools.*;

import javax.swing.*;

/**
 * Note that the UITransparencySelecter checks that the spinner and slider agree.
 *
 * @author Tim Lavers
 */
public class TransparencySelecterTest {

    private JFrame frame;
    private TransparencySelecter ts;
    private UITransparencySelecter ui;

    public boolean constructorTest() {
        init();

        //Initial value 255.
        assert ui.value() == 255;

        //Down arrow.
        for (int i = 1; i <= 255; i++) {
            ui.robot.down();
            assert ui.value() == (255 - i);
        }

        //Up arrow.
        for (int i = 1; i <= 255; i++) {
            ui.robot.up();
            assert ui.value() == i;
        }

        cleanup();
        return true;
    }

    public boolean uiTest() {
        init();
        assert ts.ui() instanceof JPanel;
        cleanup();
        return true;
    }

    public boolean valueTest() {
        init();
        assert value() == 255;

        ui.setValue( 34 );
        assert value() == 34;

        ui.setValue( 90 );
        assert value() == 90;

        cleanup();
        return true;
    }

    public boolean setValueTest() {
        init();

        setValue( 34 );
        assert ui.value() == 34;
        assert value() == 34;

        ui.setValue( 90 );
        assert value() == 90;
        assert ui.value() == 90;

        cleanup();
        return true;
    }

    private int value() {
        final int[] valueHolder = new int[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                valueHolder[0] = ts.value();
            }
        } );
        return valueHolder[0];
    }

    private void setValue( final int value ) {
        UI.runInEventThread( new Runnable() {
            public void run() {
                ts.setValue( value );
            }
        } );
    }

    private void init() {
        Runnable creater = new Runnable() {
            public void run() {
                ts = new TransparencySelecter();
                frame = new JFrame( "TransparencySelecterTest" );
                frame.add( ts.ui() );
                frame.pack();
            }
        };
        UI.runInEventThread( creater );
        UI.showFrame( frame );
        ui = new UITransparencySelecter();
    }

    private void cleanup() {
        UI.runInEventThread( new Runnable() {
            public void run() {
                frame.dispose();
            }
        } );
    }
}