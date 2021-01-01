package jet.ikonmaker;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Simple user interface component for getting a name for
 * a copy of an exising ikon, and used as an example in
 * Chapter 9.
 * 
 * @author Tim Lavers
 */
public class SaveAsDialog {

    public static final String DIALOG_NAME = "SaveAsDialog.dialog";
    private JDialog dialog;
    private JTextField nameField;
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();
    private JButton okButton;
    private SortedSet<IkonName> existingNames;
    private boolean wasCancelled = false;

    public SaveAsDialog( JFrame owningFrame,
                         SortedSet<IkonName> existingNames ) {
        this.existingNames = existingNames;
        JPanel inputPanel = new JPanel( new GridBagLayout() );
        inputPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 30, 10 ) );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets( 5, 5, 0, 0 );
        gbc.anchor = GridBagConstraints.NORTHWEST;
        //First row: name label and field.
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel( us.label( IkonMakerUserStrings.NAME ) );
        nameLabel.setDisplayedMnemonic( us.mnemonic( IkonMakerUserStrings.NAME ) );
        inputPanel.add( nameLabel, gbc );
        nameField = new JTextField( 12 );
        nameField.setName( IkonMakerUserStrings.NAME );
        nameLabel.setLabelFor( nameField );
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add( nameField, gbc );

        //Ok and cancel buttons in a box.
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add( Box.createHorizontalGlue() );
        AbstractAction okAction = new AbstractAction() {
            public void actionPerformed( ActionEvent a ) {
                dialog.dispose();
            }
        };
        okButton = us.createJButton( okAction, IkonMakerUserStrings.OK );
        okButton.setEnabled( false );
        buttonBox.add( okButton );

        AbstractAction cancelAction = new AbstractAction() {
            public void actionPerformed( ActionEvent a ) {
                wasCancelled = true;
                dialog.dispose();
            }
        };
        JButton cancelButton = us.createJButton( cancelAction,
                IkonMakerUserStrings.CANCEL );
        buttonBox.add( Box.createHorizontalStrut( 5 ) );
        buttonBox.add( cancelButton );

        //Listen for changes to the text field.
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate( DocumentEvent e ) {
                someDocumentChanged();
            }

            public void removeUpdate( DocumentEvent e ) {
                someDocumentChanged();
            }

            public void changedUpdate( DocumentEvent e ) {
                someDocumentChanged();
            }

            private void someDocumentChanged() {
                okButton.setEnabled( name() != null );
            }
        };
        nameField.getDocument().addDocumentListener( dl );

        //Key bindings.
        String keyName = "escape";
        cancelButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( "pressed " + keyName.toUpperCase() ), keyName );
        cancelButton.getActionMap().put( keyName, cancelAction );
        keyName = "enter";
        okButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( "pressed " + keyName.toUpperCase() ), keyName );
        okButton.getActionMap().put( keyName, okAction );

        dialog = new JDialog( owningFrame, us.label( IkonMakerUserStrings.SAVE_AS ), true );
        dialog.setName( DIALOG_NAME );
        dialog.getContentPane().setLayout( new BorderLayout() );
        dialog.getContentPane().add( inputPanel, BorderLayout.CENTER );
        dialog.getContentPane().add( buttonBox, BorderLayout.SOUTH );
        dialog.pack();
    }

    /**
     * Show the dialog, blocking until ok or cancel is activated.
     */
    public void show() {
        dialog.setVisible( true );
    }

    /**
     * The most recently entered name.
     */
    public IkonName name() {
        String rawName = nameField.getText().trim();
        if (rawName.length() < 1) return null;

        IkonName proposedName;
        try {
            proposedName = new IkonName( rawName );
        } catch (IllegalArgumentException e) {
            //That's ok, it's an invalid name, so return null.
            return null;
        }
        if (existingNames.contains( proposedName )) return null;
        return proposedName;
    }

    /**
     * Returns true if the dialog was cancelled.
     */
    public boolean wasCancelled() {
        return wasCancelled;
    }
}