package jet.user.test;

import jet.testtools.*;
import jet.user.*;

import javax.swing.*;
import java.util.concurrent.atomic.*;

public class AddUserTest {
    private JFrame frame;
    private AtomicReference<User> userFromGui;

    public boolean showTest() {
        frame = UI.createFrame( "AddUserTest" );
        AddUser.Handler handler = new AddUser.Handler() {
            public JFrame frame() {
                return frame;
            }

            public void addUser( User user ) {
                userFromGui = new AtomicReference<User>( user );
            }

            public User userFromGUI() {
                return userFromGui.get();
            }
        };
        final AddUser addUser = new AddUser( handler );
        UI.runInEventThread( new Runnable() {
            public void run() {
                addUser.show();
            }
        } );
        //enter a name and password
        Cyborg cyborg = new Cyborg();
        String userName = "Harry Potter";
        cyborg.type( userName );
        cyborg.tab();
        String password = "password1234";
        cyborg.type( password );
        //press the "AddUser" button
        cyborg.tab();
        cyborg.space();
        //check that the handler method has been called with the expected user details
        Assert.equal( userFromGui.get().name(), userName );
        Assert.equal( userFromGui.get().password(), password );
        UI.disposeOfAllFrames();
        return true;
    }

    //The AddUser class is only to demonstrate a design pattern.
    public boolean constructorTest() {
        return true;
    }
}
