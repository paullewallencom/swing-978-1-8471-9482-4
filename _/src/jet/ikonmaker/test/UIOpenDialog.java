package jet.ikonmaker.test;

import jet.testtools.*;
import jet.ikonmaker.*;

import javax.swing.*;

/**
 * @author Tim Lavers
 */
public class UIOpenDialog {
    Cyborg robot = new Cyborg();
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();

    public JButton okButton() {
        return UI.findButtonWithText( us.label( IkonMakerUserStrings.OK ) );
    }

    public JButton cancelButton() {
        return UI.findButtonWithText( us.label( IkonMakerUserStrings.CANCEL ) );
    }

    public JList namesList() {
        return (JList) UI.findNamedComponent( VisualOpenDialog.NAMES_LIST_NAME );
    }

    public IkonName selected() {
        final Ikon[] result = new Ikon[1];
        Runnable r = new Runnable() {
            public void run() {
                result[0] = (Ikon) namesList().getSelectedValue();
            }
        };
        UI.runInEventThread( r );
        return result[0] == null ? null : result[0].name();
    }

    public void open( String name ) {
        final int[] resultHolder = new int[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = namesList().getModel().getSize();
            }
        } );
        int listSize = resultHolder[0];
        for (int i=0; i<listSize; i++) {
            robot.down();
            if (selected()== null) robot.down();//Sometimes the initial focus is not on the list. I don't know why.
            if (selected().toString().equals( name)) break;
        }
        ok();
    }

    public void ok() {
        robot.altChar(  us.mnemonic( IkonMakerUserStrings.OK ) );
    }

    public void cancel() {
        robot.altChar(  us.mnemonic( IkonMakerUserStrings.CANCEL ) );
    }
}
