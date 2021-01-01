package jet.ikonmaker.test;

import jet.ikonmaker.*;
import jet.testtools.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author Tim Lavers
 */
public class UISaveAsDialog {
    Cyborg robot = new Cyborg();
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();
    protected Dialog namedDialog;

    public UISaveAsDialog() {
        namedDialog = UI.findNamedDialog( SaveAsDialog.DIALOG_NAME );
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return nameField().hasFocus();
            }
        }, 1000 );

    }

    public JButton okButton() {
        return (JButton) UI.findNamedComponent(
                IkonMakerUserStrings.OK );
    }

    public Dialog dialog() {
        return namedDialog;
    }

    public JButton cancelButton() {
        return (JButton) UI.findNamedComponent(
                IkonMakerUserStrings.CANCEL );
    }

    public JTextField nameField() {
        return (JTextField) UI.findNamedComponent(
                IkonMakerUserStrings.NAME );
    }

    public void saveAs( String newName ) {
        enterName( newName );
        robot.enter();
    }

    public void enterName( String newName ) {
        robot.selectAllText();
        robot.type( newName );
    }

    public void ok() {
        robot.altChar( us.mnemonic( IkonMakerUserStrings.OK ) );
    }

    public void cancel() {
        robot.altChar( us.mnemonic( IkonMakerUserStrings.CANCEL ) );
    }
}
