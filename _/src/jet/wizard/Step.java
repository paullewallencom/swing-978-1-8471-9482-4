package jet.wizard;

import javax.swing.*;
import java.awt.event.*;

/**
 * Example from Chapter 6.
 */
public class Step {
    private Wizard wizard;
    private int stepNumber;
    private JTextField textField = new JTextField( 20 );

    public Step( Wizard wizard, int stepNumber ) {
        this.wizard = wizard;
        this.stepNumber = stepNumber;
    }

    public void show() {
        String stepName = "Step " + stepNumber;
        final JDialog dialog =
                new JDialog( wizard.frame(), stepName, false );
        JButton nextButton = new JButton( "Next" );
        nextButton.setMnemonic( 'n' );
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                wizard.nextPressed( textField.getText() );
                dialog.dispose();
            }
        } );
        JLabel label = new JLabel( "Enter info for " + stepName + ":" );
        Box box = Box.createHorizontalBox();
        box.add( label );
        box.add( textField );
        box.add( nextButton );
        dialog.getContentPane().add( box );
        dialog.pack();
        dialog.setName( stepName );
        dialog.setVisible( true );
    }
}
