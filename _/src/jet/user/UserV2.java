package jet.user;

import org.grandtestauto.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * See Chapter 15.
 */
@DoesNotNeedTest( reason="Only to sketch a design pattern.")
public class UserV2 {
    private String name;
    private String password;

    public String name() {
        return name;
    }

    public String password() {
        return password;
    }

    public static UserV2 newUser( JFrame frame ) {
        final JDialog dialog = new JDialog( frame, "Add new user", false );
        final JTextField nameField = new JTextField( 7 );
        final JTextField passwordField = new JPasswordField( 7 );
        final UserV2 user = new UserV2();
        JButton button = new JButton( "Add user" );
        button.setMnemonic( 'a' );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                user.name = nameField.getText();
                user.password = passwordField.getText();
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
        return user;
    }
}
