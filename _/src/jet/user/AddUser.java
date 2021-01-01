package jet.user;

import org.grandtestauto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * See Chapter 15.
 */
public class AddUser {
    private Handler handler;

    public interface Handler {
        JFrame frame();

        void addUser( User user );
    }

    public AddUser( Handler handler ) {
        this.handler = handler;
    }

    public void show() {
        final JDialog dialog = new JDialog( handler.frame(),
                "Add new user", false );
        final JTextField nameField = new JTextField( 7 );
        final JTextField passwordField = new JPasswordField( 7 );
        JButton button = new JButton( "Add user" );
        button.setMnemonic( 'a' );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                User user = new User();
                user.setName( nameField.getText() );
                user.setPassword( passwordField.getText() );
                handler.addUser( user );
                dialog.dispose();
            }
        } );
        JPanel panel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
        panel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        JLabel nameLabel = new JLabel( "User name:" );
        JLabel passwordLabel = new JLabel( "Password:" );
        panel.add( nameLabel );
        panel.add( nameField );
        panel.add( passwordLabel );
        panel.add( passwordField );
        Box box = Box.createVerticalBox();
        box.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        box.add( button );
        box.add( Box.createVerticalGlue() );
        dialog.getContentPane().add( panel, BorderLayout.CENTER );
        dialog.getContentPane().add( box, BorderLayout.EAST );
        dialog.pack();
        dialog.setVisible( true );
    }
}
