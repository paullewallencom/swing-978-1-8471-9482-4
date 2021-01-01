package jet.user;

import jet.testtools.*;
import org.grandtestauto.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * Minimal implementation of Client to demonstrate the pattern
 * in Chapter 15. Note that Client is meant to be created from
 * the event thread, see the main method.
 */
@DoesNotNeedTest( reason="Only to sketch a design pattern.")
public class Client {
    private JFrame frame = new JFrame();

    private Client() {
        final AddUser.Handler handler = new AddUser.Handler() {
            public JFrame frame() {
                return frame;
            }

            public void addUser( User user ) {
                //lookup the remote server e.g. via RMI
                UserManager userManager = new UserManager();
                userManager.addUser( user );
            }
        };

        Action addUserAction = new AbstractAction( "Add new user..." ) {
            public void actionPerformed( ActionEvent e ) {
                new AddUser( handler ).show();
            }
        };
        JMenu userMenu = new JMenu( "Manage users" );
        userMenu.add( new JMenuItem( addUserAction ) );
        JMenuBar menuBar = new JMenuBar();
        menuBar.add( userMenu );
        frame.setTitle( "Client application" );
        frame.setJMenuBar( menuBar );
        frame.pack();
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        UI.runInEventThread( new Runnable() {
            public void run() {
                new Client();
            }
        } );
    }
}
