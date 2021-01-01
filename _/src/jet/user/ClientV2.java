package jet.user;

import jet.testtools.*;

import javax.swing.*;

import org.grandtestauto.*;

/**
 * See Chapter 15.
 */
@DoesNotNeedTest( reason="Only to sketch a design pattern.")
public class ClientV2 {
    private JFrame frame = new JFrame();
    public UserManagerV2 userManager;

    public ClientV2() {
        //Lookup the remote server's UserManager e.g. via RMI
        userManager = new UserManagerV2();

        JMenuBar menuBar = new JMenuBar();
        menuBar.add( userManager.menu( frame ) );

        frame.setTitle( "Client application" );
        frame.setJMenuBar( menuBar );
        frame.pack();
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        UI.runInEventThread( new Runnable() {
            public void run() {
                new ClientV2();
            }
        } );
    }
}
               