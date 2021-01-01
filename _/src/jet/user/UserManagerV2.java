package jet.user;

import org.grandtestauto.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * See Chapter 15.
 */
@DoesNotNeedTest( reason="Only to sketch a design pattern.")
public class UserManagerV2 {
    private Set<UserV2> users = new HashSet<UserV2>();

    public JMenu menu( final JFrame frame ) {
        Action addUserAction = new AbstractAction( "Add new user..." ) {
            public void actionPerformed( ActionEvent e ) {
                users.add( UserV2.newUser( frame ) );
            }
        };

        JMenu userMenu = new JMenu( "Manage users" );
        userMenu.add( new JMenuItem( addUserAction ) );
        return userMenu;
    }

    //methods for persisting the collection of users, etc...
}
