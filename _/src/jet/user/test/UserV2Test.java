package jet.user.test;

import jet.testtools.*;
import jet.user.*;

import javax.swing.*;
import java.util.concurrent.atomic.*;

public class UserV2Test {
    private JFrame frame;
    private AtomicReference<UserV2> userFromGui;

    public boolean showTest() {
        frame = UI.createAndShowFrame( "UserV2Test");
        UI.runInEventThread( new Runnable() {
            public void run() {
                userFromGui = new AtomicReference<UserV2>( UserV2.newUser( frame ) );
            }
        } );
        //enter a name and password
        Cyborg cyborg = new Cyborg();
        String userName = "Harry Potter";
        cyborg.type( userName );
        cyborg.tab();
        String password = "firebolt";
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
}
