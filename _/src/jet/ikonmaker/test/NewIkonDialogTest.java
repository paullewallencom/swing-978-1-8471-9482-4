package jet.ikonmaker.test;

import javax.swing.*;
import jet.ikonmaker.*;
import jet.testtools.*;

import java.awt.*;

/**
 * @author Tim Lavers
 */
public class NewIkonDialogTest {

    private JFrame frame;
    private NewIkonDialog nid;
    private UINewIkonDialog ui;

    public boolean constructorTest() {
        init();
        //Check the appearance.
        JTextField nameField = (JTextField) UI.findNamedComponent( IkonMakerUserStrings.NAME );
        assert UI.isEnabled( nameField );
        assert UI.getText( nameField ).equals( "" );
        Dimension size = UI.getSize( nameField );
        assert size.width > 50;
        assert size.height > 10;

        //The ok button should not be enabled.
        assert !UI.isEnabled( ui.okButton() );

        //The cancel button should be enabled.
        assert UI.isEnabled( ui.cancelButton() );

        cleanup();
        return true;
    }

    public boolean widthValidationTest() {
        init();
        ui.setName( "ike");
        ui.setHeight( 89 );
        assert !ui.okButton().isEnabled();
        assert UI.getText( ui.widthField() ).equals( "" );
        ui.typeIntoWidthField( "five" );
        assert !UI.isEnabled( ui.okButton() );
        ui.robot.tab();
        assert UI.getText( ui.widthField() ).equals( "" );
        ui.typeIntoWidthField( "5" );
        assert UI.isEnabled( ui.okButton() );
        cleanup();
        return true;
    }

    public boolean heightValidationTest() {
        init();
        ui.setName( "ike");
        ui.setWidth( 89 );
        assert !UI.isEnabled( ui.okButton() );
        assert UI.getText(  ui.heightField() ).equals( "" );
        ui.typeIntoHeightField( "five" );
        assert !UI.isEnabled( ui.okButton() );
        ui.robot.tab();
        assert UI.getText( ui.heightField() ).equals( "" );
        ui.typeIntoHeightField( "5" );
        assert UI.isEnabled( ui.okButton() );
        cleanup();
        return true;
    }

    public boolean createSimpleTest() {
        init();
        ui.setHeight( 12 );
        ui.setWidth( 10 );
        ui.setName( "ThisName" );
        ui.ok();
        Ikon created = created();
        assert created.height() == 12;
        assert created.width() == 10;
        assert created.name().equals( new IkonName( "ThisName" ) );
        cleanup();
        return true;
    }

    public boolean enterTest() {
        init();
        ui.setHeight( 12 );
        ui.setWidth( 10 );
        ui.setName( "ThisName" );
        ui.robot.enter();
        Ikon created = created();
        assert created.height() == 12;
        assert created.width() == 10;
        assert created.name().equals( new IkonName( "ThisName" ) );
        cleanup();
        return true;
    }

    public boolean escapeTest() {
        init();
        ui.robot.escape();
        assert !UI.isShowing( nid.dialog() );
        cleanup();
        return true;
    }

    private Ikon created() {
        final Ikon[] resultHolder = new Ikon[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = nid.created();
            }
        } );
        return resultHolder[0];
    }

    public boolean createdTest() {
        //Here check when dialog first shown and also when cancelled.
        //When ok is pressed is checked in createSimpleTest.
        init();
        assert created() == null;
        ui.setName( "dfsdf" );
        ui.setWidth( 12 );
        ui.setHeight( 12 );
        assert created() == null;
        ui.cancel();
        assert created() == null;
        cleanup();
        return true;
    }

    public boolean dialogTest() {
        init();
        assert UI.isShowing( nid.dialog() );
        cleanup();
        return true;
    }

    public boolean showTest() {
        //show() is used in init(), so is tested elsewhere.
        return true;
    }

    private void cleanup() {
        UI.disposeOfAllFrames();
    }

    private void init() {
        //Create and show the frame and create the NewIkonDialog.
        //This all needs to be done in the swing event thread,
        UI.runInEventThread( new Runnable() {
            public void run() {
                frame = new JFrame( "NewIkonDialogTest" );
                frame.setVisible( true );
                nid = new NewIkonDialog( frame );
            }
        });
        //Start a thread to show the dialog (it is modal).
        new Thread( "DialogShower" ) {
            public void run() {
                nid.show();
            }
        }.start();
        //Wait for the dialog to be showing.
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return nid.dialog().isShowing();
            }
        }, 1000 );
        //Create the UI test delegate,
        ui = new UINewIkonDialog();
    }
}
