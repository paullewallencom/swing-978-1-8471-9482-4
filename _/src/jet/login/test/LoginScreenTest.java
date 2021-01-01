package jet.login.test;

import jet.login.*;
import jet.testtools.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.atomic.*;

public class LoginScreenTest {

    private TestHandler handler;
    private LoginScreen ls;
    private UILoginScreen uils;
    private Launcher launcher;
    private LoginUserStrings us = LoginUserStrings.instance();

    /**
     * Checks the state when the LoginScreen is first shown.
     */
    public boolean constructorTest() {
        init();
        //Check the inital ApplicationChoice selection.
        assert UI.isSelected( uils.buttonForChoice( ApplicationChoice.VALIDATOR ) );
        assert !UI.isSelected( uils.buttonForChoice( ApplicationChoice.ADMINISTRATOR ) );
        assert !UI.isSelected( uils.buttonForChoice( ApplicationChoice.AUDITOR ) );
        assert !UI.isSelected( uils.buttonForChoice( ApplicationChoice.AUDITOR_KNOWLEDGE_BUILDER ) );
        assert !UI.isSelected( uils.buttonForChoice( ApplicationChoice.CLINICAL_KNOWLEDGE_BUILDER ) );

        //The text fields are empty.
        assert UI.getText( uils.nameField() ).equals( "" );
        assert UI.getText( uils.passwordField() ).equals( "" );

        //The ok button is not enabled, but the cancel buttone is.
        assert !UI.isEnabled( uils.okButton() );
        assert UI.isEnabled( uils.cancelButton() );

        //Check that the radio buttons are all in a single button group.
        ButtonGroup bg = buttonGroup( ApplicationChoice.ADMINISTRATOR );
        assert bg.getButtonCount() == 5;
        assert bg == buttonGroup( ApplicationChoice.VALIDATOR );
        assert bg == buttonGroup( ApplicationChoice.CLINICAL_KNOWLEDGE_BUILDER );
        assert bg == buttonGroup( ApplicationChoice.AUDITOR );
        assert bg == buttonGroup( ApplicationChoice.AUDITOR_KNOWLEDGE_BUILDER );

        cleanup();
        return true;
    }

    /**
     * Check that the OK button is enabled if and only if there is
     * text in both the user name and password fields.
     */
    public boolean okButtonEnabledStateTest() {
        init();
        assert !UI.isEnabled( uils.okButton() );
        uils.enterName( "a" );
        assert !UI.isEnabled( uils.okButton() );
        uils.enterPassword( "a" );
        assert UI.isEnabled( uils.okButton() );
        uils.enterPassword( "" );
        assert !UI.isEnabled( uils.okButton() );
        uils.enterPassword( "a" );
        assert UI.isEnabled( uils.okButton() );
        uils.enterName( "" );
        assert !UI.isEnabled( uils.okButton() );
        uils.enterName( "a" );
        assert UI.isEnabled( uils.okButton() );

        cleanup();
        return true;
    }

    /**
     * Checks that when 'Ok' is activated, the entered data
     * are passed to the handler and the screen vanishes
     * and the launching thread is awakened.
     */
    public boolean okTest() {
        init();
        uils.chooseApplication( ApplicationChoice.ADMINISTRATOR );
        uils.enterName( "Harry Potter" );
        uils.enterPassword( "Firebolt" );
        uils.ok();
        handler.checkData( "Harry Potter", "Firebolt", ApplicationChoice.ADMINISTRATOR );
        assert !UI.isShowing( uils.frame() );
        checkLauncherIsAwakened();
        cleanup();
        return true;
    }

    /**
     * Check that the application choice, whatever it is,
     * is passed to the handler.
     */
    public boolean anyChoiceTest() {
        for (ApplicationChoice choice : ApplicationChoice.values()) {
            init();
            uils.login( "a", "b", choice );
            handler.checkData( "a", "b", choice );
            cleanup();
        }
        return true;
    }

    /**
     * Check that activating the cancel button closes
     * the frame and wakes the launching thread, but no
     * data is passed on.
     */
    public boolean cancelTest() {
        init();
        uils.enterName( "a" );
        uils.enterPassword( "a" );
        uils.cancel();
        assert handler.enteredData == null;
        checkLauncherIsAwakened();
        cleanup();
        return true;
    }

    /**
     * Check that 'escape' cancels the login.
     */
    public boolean escapeTest() {
        init();
        uils.cyborg().escape();
        assert !UI.isShowing( uils.frame() );
        assert handler.enteredData == null;
        checkLauncherIsAwakened();
        cleanup();
        return true;
    }

    /**
     * Check that 'enter' activates the ok button.
     */
    public boolean enterTest() {
        init();
        uils.enterName( "a" );
        uils.enterPassword( "a" );
        uils.cyborg().enter();
        assert !UI.isShowing( uils.frame() );
        handler.checkData( "a", "a", ApplicationChoice.VALIDATOR );
        checkLauncherIsAwakened();
        cleanup();
        return true;
    }

    public boolean invalidUserNameTest() {
        init();
        //Set up the handler to fail because the user is unknown.
        handler.result = LoginScreen.LoginResult.UNKNOWN_USER;
        uils.login( "a", "a", ApplicationChoice.ADMINISTRATOR );
        //Sanity check that the data has been passed in.
        handler.checkData( "a", "a", ApplicationChoice.ADMINISTRATOR );
        //Check that the expected error message is showing.
        String expectedError = us.message( LoginUserStrings.UNKNOWN_USER_MSG1, "a" );
        assert UI.findLabelShowingText( expectedError ) != null;
        //Acknowledge the error message.
        uils.cyborg().activateFocussedButton();
        //Check that the error message is no longer showing.
        assert UI.findLabelShowingText( expectedError ) == null;
        //Check that the user name field has the incorrect name all selected.
        //First check the text value.
        assert UI.getText( uils.nameField() ).equals( "a" );
        //Now prove it must be selected by just entering another name.
        uils.cyborg().type( "b" );
        uils.cyborg().enter();
        handler.checkData( "b", "a", ApplicationChoice.ADMINISTRATOR );
        expectedError = us.message( LoginUserStrings.UNKNOWN_USER_MSG1, "b" );
        assert UI.findLabelShowingText( expectedError ) != null;
        cleanup();
        return true;
    }

    public boolean invalidPasswordTest() {
        init();
        //Set up the handler to fail because the password is wrong.
        handler.result = LoginScreen.LoginResult.WRONG_PASSWORD;
        uils.login( "a", "a", ApplicationChoice.ADMINISTRATOR );
        //Sanity check that the data has been passed in.
        handler.checkData( "a", "a", ApplicationChoice.ADMINISTRATOR );
        //Check that the expected error message is showing.
        String expectedError = us.message( LoginUserStrings.WRONG_PASSWORD_MSG0 );
        assert UI.findLabelShowingText( expectedError ) != null;
        //Acknowledge the error message.
        uils.cyborg().activateFocussedButton();
        //Check that the error message is no longer showing.
        assert UI.findLabelShowingText( expectedError ) == null;
        //Check that a new password can be entered just by typing
        //the value, in particular there is no need to select the
        //password field or select all text.
        uils.cyborg().type( "99" );
        uils.cyborg().enter();
        handler.checkData( "a", "99", ApplicationChoice.ADMINISTRATOR );
        assert UI.findLabelShowingText( expectedError ) != null;
        cleanup();
        return true;
    }

    public boolean invalidAdministratorLogin2Test() {
        init();
        //Set up the handler to fail because the user does
        //not have privileges for the chosen application.
        handler.result = LoginScreen.LoginResult.INSUFFICIENT_PRIVILEGES;

        //Attempt a login as Administrator.
        uils.login( "Harry", "Firebolt", ApplicationChoice.ADMINISTRATOR );

        //Check that the expected error message is showing.
        String expectedError = us.message(
                LoginUserStrings.INSUFFICIENT_PRIVILEGES_MSG2,
                "Harry",
                us.label( ApplicationChoice.ADMINISTRATOR.toString() ) );
        assert UI.findLabelShowingText( expectedError ) != null;

        //Acknowledge the error message.
        uils.cyborg().activateFocussedButton();

        //Check that the error message is no longer showing.
        assert UI.findLabelShowingText( expectedError ) == null;

        //Check that the Login Screen is still showing, with the
        //same data entered. Do this by activating ok and checking
        //the passed in data.
        handler.enteredData = null;//Clear out previous values first.
        uils.ok();
        handler.checkData( "Harry", "Firebolt",
                ApplicationChoice.ADMINISTRATOR );

        //Check that the correct error message is given again.
        assert UI.findLabelShowingText( expectedError ) != null;

        cleanup();
        return true;
    }

    public boolean invalidAdministratorLogin1Test() {
        init();
        //Set up the handler to fail because the user does
        //not have privileges for the chosen application.
        handler.result = LoginScreen.LoginResult.INSUFFICIENT_PRIVILEGES;
        uils.enterName( "Harry" );
        uils.enterPassword( "Firebolt" );
        uils.chooseApplication( ApplicationChoice.ADMINISTRATOR );
        uils.ok();

        //Sanity check that the data has been passed in.
        handler.checkData( "Harry", "Firebolt",
                ApplicationChoice.ADMINISTRATOR );

        //Check that the expected error message is showing.
        String expectedError = us.message(
                LoginUserStrings.INSUFFICIENT_PRIVILEGES_MSG2,
                "Harry",
                us.label( ApplicationChoice.ADMINISTRATOR.toString() ) );
        assert UI.findLabelShowingText( expectedError ) != null;

        //Acknowledge the error message.
        uils.cyborg().activateFocussedButton();

        //Check that the error message is no longer showing.
        assert UI.findLabelShowingText( expectedError ) == null;

        //Check that the Login Screen is still showing, with the
        //same data entered. Do this by activation ok and checking
        //the passed in data.
        handler.enteredData = null;//Clear out previous values first.
        uils.ok();
        handler.checkData( "Harry", "Firebolt",
                ApplicationChoice.ADMINISTRATOR );

        //Check that the correct error message is given again.
        assert UI.findLabelShowingText( expectedError ) != null;

        cleanup();
        return true;
    }

    public boolean insufficientPrivilegesTest() {
        for (ApplicationChoice ac : ApplicationChoice.values()) {
            checkLoginWithInsufficientPrivilegesForApp( ac );
        }
        return true;
    }

    private void checkLoginWithInsufficientPrivilegesForApp(
            ApplicationChoice ac ) {
        init();
        //Set up the handler to fail because the user does
        //not have privileges for the chosen application.
        handler.result = LoginScreen.LoginResult.INSUFFICIENT_PRIVILEGES;
        uils.login( "Harry", "Firebolt", ac );

        //Sanity check that the data has been passed in.
        handler.checkData( "Harry", "Firebolt", ac );

        //Check that the expected error message is showing.
        String expectedError = us.message(
                LoginUserStrings.INSUFFICIENT_PRIVILEGES_MSG2,
                "Harry",
                us.label( ac.toString() ) );
        assert UI.findLabelShowingText( expectedError ) != null;

        //Acknowledge the error message.
        uils.cyborg().activateFocussedButton();

        //Check that the error message is no longer showing.
        assert UI.findLabelShowingText( expectedError ) == null;

        //Check that the Login Screen is still showing, with the
        //same data entered. Do this by activation ok and checking
        //the passed in data.
        handler.enteredData = null;//Clear out previous values first.
        uils.ok();
        handler.checkData( "Harry", "Firebolt", ac );

        //Check that the correct error message is given again.
        assert UI.findLabelShowingText( expectedError ) != null;

        cleanup();
    }

    public boolean exceptionThrownTest() {
        init();
        handler.toThrow.set( new RuntimeException( "Throw me!" ) );
        uils.login( "a", "a", ApplicationChoice.ADMINISTRATOR );
        String expectedError = us.message( LoginUserStrings.LOGIN_EXCEPTION_MSG0 );
        assert UI.findLabelShowingText( expectedError ) != null;
        uils.cyborg().activateFocussedButton();//Acknowledge the error message.
        //The ok button should be in focus. If we press it,
        //we should get the error message again.
        uils.cyborg().activateFocussedButton();

        JLabel text = UI.findLabelShowingText( expectedError );
        assert text != null;
        uils.cyborg().activateFocussedButton();//Acknowledge the error message.
        uils.cancel();
        checkLauncherIsAwakened();
        cleanup();
        return true;
    }

    private void checkLauncherIsAwakened() {
        boolean wakes = Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return launcher.isAwakened;
            }
        }, 1000 );
        if (!wakes) Waiting.stop();
        assert wakes : "Launcher not woken!";
    }

    public boolean showTest() {
        //Every other test tests this.
        return true;
    }

    private ButtonGroup buttonGroup( ApplicationChoice choice ) {
        return ((DefaultButtonModel) uils.buttonForChoice( choice ).getModel()).getGroup();
    }

    private void init() {
        handler = new TestHandler();
        handler.result = LoginScreen.LoginResult.ACCEPTED;
        ls = new LoginScreen( handler );
        launcher = new Launcher();
        launcher.start();
        //We're waiting for the frame to show and for at least the
        //ok button to be shown within it. It might be safer to wait
        //for all components.
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return UI.findNamedFrame( LoginScreen.FRAME_NAME ) != null &&
                        frameReady( (JFrame) UI.findNamedFrame( LoginScreen.FRAME_NAME ) );// &&
//                        UI.findNamedComponent( LoginUserStrings.OK ) != null &&              ????
//                        UI.findNamedComponent( LoginUserStrings.USER_NAME ) != null &&
//                        UI.findNamedComponent( LoginUserStrings.USER_NAME ).hasFocus() &&
//                        UI.findNamedComponent( LoginUserStrings.CANCEL ) != null &&
//                        UI.findNamedComponent( LoginUserStrings.CANCEL ).isEnabled();
            }
        }, 200 );
        //Now that the LoginScreen is showing, we can create its ui delegate.
        uils = new UILoginScreen();
    }

    private boolean frameReady( JFrame frame ) {
        return "CANCEL".equals( frame.getRootPane().getInputMap().get( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ) ) );
    }

    private void cleanup() {
        //Close the login screen if it's still up.
        UI.disposeOfAllFrames();
    }

    private class TestHandler implements LoginScreen.Handler {
        //The data most recently passed in by the LoginScreen.
        LoginInfo enteredData;
        //The value for login() to return.
        LoginScreen.LoginResult result;
        //The exception to be thrown by login()
        AtomicReference<RuntimeException> toThrow = new AtomicReference<RuntimeException>();

        public LoginScreen.LoginResult login( LoginInfo info ) {
            enteredData = info;
            if (toThrow.get() != null) throw toThrow.get();
            return result;
        }

        void checkData( String name, String password,
                        ApplicationChoice choice ) {
            assert enteredData.userName().equals( name ) :
                    "Expected: " + name + ", got: "
                            + enteredData.userName();
            assert enteredData.password().equals( password ) :
                    "Expected: " + password + ", got: "
                            + enteredData.password();
            assert enteredData.chosenApplication().equals( choice ) :
                    "Expected: " + choice + ", got: "
                            + enteredData.chosenApplication();
        }
    }

    private class Launcher extends Thread {
        public volatile boolean isAwakened;

        public Launcher() {
            super( "Launcher" );
            setDaemon( true );
        }

        public void run() {
            ls.show();//This is Swing thread-safe.
            isAwakened = true;
        }
    }
}
