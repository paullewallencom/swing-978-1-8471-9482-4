package jet.wizard;

import javax.swing.*;
import java.awt.event.*;

/**
 * Example from Chapter 6.
 */
public class StepV2 {
    private int stepNumber;
    private JTextField textField = new JTextField( 20 );
    private Handler handler;

    public interface Handler {
        void nextPressed( String userInputForStep );

        JFrame frame();
    }

    public StepV2( Handler handler, int stepNumber ) {
        this.handler = handler;
        this.stepNumber = stepNumber;
    }

    public void show() {
        String stepName = "Step " + stepNumber;
        final JDialog dialog = new JDialog(
                handler.frame(), stepName, false );
        JButton nextButton = new JButton( "Next" );
        nextButton.setMnemonic( 'n' );
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handler.nextPressed( textField.getText() );
                dialog.dispose();
            }
        } );
        JLabel label = new JLabel(
                "Enter info for " + stepName + ":" );
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
