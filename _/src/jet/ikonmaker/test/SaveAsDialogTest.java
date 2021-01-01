package jet.ikonmaker.test;

import jet.ikonmaker.*;
import jet.testtools.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class SaveAsDialogTest {

    private JFrame frame;
    private SaveAsDialog sad;
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();
    private SortedSet<IkonName> names;
    private UISaveAsDialog ui;
    private ShowerThread shower;

    public boolean constructorTest() {
        init();

        //Check the title.
        assert UI.getTitle( ui.dialog() ).equals(
                us.label( IkonMakerUserStrings.SAVE_AS ) );

        //Check the size.
        Dimension size = UI.getSize( ui.dialog() );
        assert size.width > 60;
        assert size.width < 260;
        assert size.height > 20;
        assert size.height < 200;

        //Name field initially empty.
        assert UI.getText( ui.nameField() ).equals( "" );

    //Name field a sensible size.
    Dimension nameFieldSize = UI.getSize( ui.nameField() );
    assert nameFieldSize.width > 60;
    assert nameFieldSize.width < 260;
    assert nameFieldSize.height > 15;
    assert nameFieldSize.height < 25;

        //Ok not enabled.
        assert !UI.isEnabled( ui.okButton() );

        //Cancel enabled.
        assert UI.isEnabled( ui.cancelButton() );

        //Type in some text and check that the ok button is now enabled.
        ui.robot.type( "text" );
        assert UI.isEnabled( ui.okButton() );

        cleanup();
        return true;
    }

    public boolean wasCancelledTest() {
        //When the ok button has been pressed.
        init();
        assert !wasCancelled();
        ui.saveAs( "remus" );
        assert !UI.isShowing( ui.dialog() );
        assert !wasCancelled();
        cleanup();

        //Cancel before a name has been entered.
        init();
        ui.cancel();
        assert !UI.isShowing( ui.dialog() );
        assert wasCancelled();
        cleanup();

        //Cancel after a name has been entered.
        init();
        ui.robot.type( "remus" );
        ui.cancel();
        assert !UI.isShowing( ui.dialog() );
        assert wasCancelled();
        cleanup();
        return true;
    }

    public boolean showTest() {
        //show() is used in init() so is tested in all other tests,
        //so here we'll just check that it blocks the calling thread
        //which is awakened either by a cancel or ok.
        init();
        assert !shower.isAwakened();
        ui.cancel();
        assert shower.isAwakened();
        cleanup();

        init();
        assert !shower.isAwakened();
        ui.saveAs( "ikon" );
        assert shower.isAwakened();
        cleanup();
        return true;
    }

    public boolean validateDataTest() {
        init();
        //First check names that have illegal characters.
        checkOkButton( "  " );
        checkOkButton( "*" );
        checkOkButton( "/" );
        checkOkButton( "\\" );
        //Now names that are already there.
        checkOkButton( "albus" );
        checkOkButton( "Albus" );
        checkOkButton( "ALBUS" );
        checkOkButton( "MINERVA" );

        cleanup();
        return true;
    }

    public boolean nameTest() {
        init();
        assert enteredName() == null;
        ui.robot.type( "remus" );
        assert enteredName().equals( new IkonName( "remus" ) );
        ui.ok();
        assert enteredName().equals( new IkonName( "remus" ) );
        cleanup();
        return true;
    }

    public boolean usabilityTest() {
        //Check that 'escape' cancels.
        init();
        ui.robot.escape();
        assert !UI.isShowing( ui.dialog() );
        assert wasCancelled();
        cleanup();

        //Check activating the cancel button when it has focus.
        init();
        ui.robot.tab();//Only one tab needed as ok is not enabled.
        ui.robot.activateFocussedButton();
        assert !UI.isShowing( ui.dialog() );
        assert wasCancelled();
        cleanup();

        //Check that 'enter' is like 'ok'.
        init();
        ui.robot.type( "remus" );
        ui.robot.enter();
        assert !UI.isShowing( ui.dialog() );
        assert !wasCancelled();
        assert enteredName().equals( new IkonName( "remus" ) );
        cleanup();

        //Check activating the ok button when it is focused.
        init();
        ui.robot.type( "remus" );
        ui.robot.tab();
        ui.robot.activateFocussedButton();
        assert !UI.isShowing( ui.dialog() );
        assert !wasCancelled();
        Assert.equal( enteredName(), new IkonName( "remus" ) );
        cleanup();
        return true;
    }

    private void checkOkButton( String name ) {
        ui.enterName( name );
        assert !UI.isEnabled( ui.okButton() );
        ui.enterName( "remus" );
        assert UI.isEnabled( ui.okButton() );
    }

    private IkonName enteredName() {
        final IkonName[] resultHolder = new IkonName[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = sad.name();
            }
        } );
        return resultHolder[0];
    }

    private boolean wasCancelled() {
        final boolean[] resultHolder = new boolean[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = sad.wasCancelled();
            }
        } );
        return resultHolder[0];
    }

    private void init() {
        names = new TreeSet<IkonName>();
        names.add( new IkonName( "Albus" ) );
        names.add( new IkonName( "Minerva" ) );
        names.add( new IkonName( "Severus" ) );
        names.add( new IkonName( "Alastair" ) );

        Runnable creator = new Runnable() {
            public void run() {
                frame = new JFrame( "SaveAsDialogTest" );
                frame.setVisible( true );
                sad = new SaveAsDialog( frame, names );
            }
        };
        UI.runInEventThread( creator );

        //Start a thread to show the dialog (it is modal).
        shower = new ShowerThread();
        shower.start();

        //Wait for the dialog to be showing.
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return UI.findNamedDialog(
                        SaveAsDialog.DIALOG_NAME ) != null;
            }
        }, 1000 );
        ui = new UISaveAsDialog();
    }

    private void cleanup() {
        UI.disposeOfAllFrames();
    }

    private class ShowerThread extends Thread {
        private volatile boolean awake;

        public ShowerThread() {
            super( "Shower" );
            setDaemon( true );
        }

        public void run() {
            Runnable runnable = new Runnable() {
                public void run() {
                    sad.show();
                }
            };
            UI.runInEventThread( runnable );
            awake = true;
        }

        public boolean isAwakened() {
            return Waiting.waitFor( new Waiting.ItHappened() {
                public boolean itHappened() {
                    return awake;
                }
            }, 1000 );
        }
    }
}