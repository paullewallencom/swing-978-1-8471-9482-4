package jet.login.test;

import jet.login.ApplicationChoice;
import jet.login.LoginScreen;
import jet.login.LoginUserStrings;
import jet.testtools.*;

import javax.swing.*;

public class UILoginScreen {
    private JFrame frame;
    private LoginUserStrings us = LoginUserStrings.instance();
    private Cyborg cyborg = new Cyborg();

    public UILoginScreen() {
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return UI.findNamedFrame( LoginScreen.FRAME_NAME ) != null;
            }
        }, 2000 );
        frame = (JFrame) UI.findNamedFrame( LoginScreen.FRAME_NAME );
    }

    public Cyborg cyborg() {
        return cyborg;
    }

    public void chooseApplication( ApplicationChoice choice ) {
        cyborg.altChar( us.mnemonic( choice.toString() ) );
    }

    public void enterName( String name ) {
        enterNameOrPassword( name, LoginUserStrings.USER_NAME );
    }

    public void enterPassword( String password ) {
        enterNameOrPassword( password, LoginUserStrings.PASSWORD );
    }

    public void ok() {
        cyborg.altChar( us.mnemonic( LoginUserStrings.OK ) );
    }

    public void cancel() {
        cyborg.altChar( us.mnemonic( LoginUserStrings.CANCEL ) );
    }

    public void login( String name, String password,
                           ApplicationChoice choice ) {
        enterName( name );
        enterPassword( password );
        chooseApplication( choice );
        ok();
    }

    public JTextField nameField() {
        return (JTextField) UI.findNamedComponent(
                LoginUserStrings.USER_NAME );
    }

    public JPasswordField passwordField() {
        return (JPasswordField) UI.findNamedComponent(
                LoginUserStrings.PASSWORD );
    }

    public JButton okButton() {
        return (JButton) UI.findNamedComponent(
                LoginUserStrings.OK );
    }

    public JButton cancelButton() {
        return (JButton) UI.findNamedComponent(
                LoginUserStrings.CANCEL );
    }

    public JRadioButton buttonForChoice( ApplicationChoice choice ) {
        return (JRadioButton) UI.findNamedComponent(
                choice.toString() );
    }

    public JFrame frame() {
        return frame;
    }

    private void enterNameOrPassword( String str, String key ) {
        //Get focus to the name field.
        cyborg.altChar( us.mnemonic( key ) );
        //All should now be selected, so just type in the data.
        if (str.equals( "" )) {
            cyborg.delete();//Just delete what is already there.
        } else {
            cyborg.type( str );
        }
    }
}