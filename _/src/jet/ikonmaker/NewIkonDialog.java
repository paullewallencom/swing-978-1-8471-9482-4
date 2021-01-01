package jet.ikonmaker;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

/**
 * Simple user interface component for getting the data
 * needed to construct a new ikon.
 * 
 * @author Tim Lavers
 */
public class NewIkonDialog {

    private JDialog dialog;
    private JTextField nameField;
    private JFormattedTextField widthField;
    private JFormattedTextField heightField;
    private Ikon toReturn;
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();
    private JButton cancelButton;
    private JButton okButton;

    public NewIkonDialog( JFrame owningFrame ) {
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
        
        //Second row: Width label and field.
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel widthLabel = new JLabel( us.label( IkonMakerUserStrings.WIDTH ) );
        widthLabel.setDisplayedMnemonic( us.mnemonic( IkonMakerUserStrings.WIDTH ) );
        Format numberFormat = new DecimalFormat( "###" );
        widthField = createNumberField( IkonMakerUserStrings.WIDTH, numberFormat );
        inputPanel.add( widthLabel, gbc );
        gbc.gridx = 1;
        inputPanel.add( widthField, gbc );
        widthLabel.setLabelFor( widthField );
        
        //Third row: Height label and field.
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel heightLabel = new JLabel( us.label( IkonMakerUserStrings.HEIGHT ) );
        heightLabel.setDisplayedMnemonic( us.mnemonic( IkonMakerUserStrings.HEIGHT ) );
        gbc.gridx = 0;
        inputPanel.add( heightLabel, gbc );
        heightField = createNumberField( IkonMakerUserStrings.HEIGHT, numberFormat );
        heightLabel.setLabelFor( heightField );
        gbc.gridx = 1;
        inputPanel.add( heightField, gbc );

        //Ok and cancel buttons in a box.
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add( Box.createHorizontalGlue() );
        okButton = new JButton( new AbstractAction() {
            public void actionPerformed( ActionEvent a ) {
                toReturn = createIkonFromEnteredData();
                dialog.dispose();
            }
        } );
        okButton.setName( IkonMakerUserStrings.OK );
        okButton.setText( us.label( IkonMakerUserStrings.OK ) );
        okButton.setMnemonic( us.mnemonic( IkonMakerUserStrings.OK ) );
        okButton.setEnabled( false );
        buttonBox.add( okButton );

        cancelButton = new JButton( new AbstractAction() {
            public void actionPerformed( ActionEvent a ) {
                toReturn = null;
                dialog.dispose();
            }
        } );
        cancelButton.setName( IkonMakerUserStrings.CANCEL );
        cancelButton.setText( us.label( IkonMakerUserStrings.CANCEL ) );
        cancelButton.setMnemonic( us.mnemonic( IkonMakerUserStrings.CANCEL ) );
        buttonBox.add( Box.createHorizontalStrut( 5 ) );
        buttonBox.add( cancelButton );

        //Listen for changes to the text fields.
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
        };
        widthField.getDocument().addDocumentListener( dl );
        heightField.getDocument().addDocumentListener( dl );
        nameField.getDocument().addDocumentListener( dl );

        dialog = new JDialog( owningFrame, us.label( IkonMakerUserStrings.NEW ), true );
        dialog.getContentPane().setLayout( new BorderLayout() );
        dialog.getContentPane().add( inputPanel, BorderLayout.CENTER );
        dialog.getContentPane().add( buttonBox, BorderLayout.SOUTH );

        //Make ok the default button.
        dialog.getRootPane().setDefaultButton( okButton );

        //Allow 'escape' to cancel the dialog.
        String cancel = "CANCEL";
        dialog.getRootPane().getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), cancel);
        dialog.getRootPane().getActionMap().put( cancel, cancelButton.getAction() );
    }

    public void show() {
        dialog.pack();
        dialog.setVisible( true );
    }

    public Ikon created() {
        return toReturn;
    }

    public JDialog dialog() {
        return dialog;
    }

    private void someDocumentChanged() {
        okButton.setEnabled( createIkonFromEnteredData() != null );
    }

    private JFormattedTextField createNumberField( String key, Format format ) {
        JFormattedTextField field = new JFormattedTextField( format );
        field.setColumns( 3 );
        field.setName( key );
        return field;
    }

    private int width() {
        return Integer.parseInt( widthField.getText() );
    }

    private int height() {
        return Integer.parseInt( heightField.getText() );
    }

    private IkonName name() {
        String rawName = nameField.getText().trim();
        if (rawName.length() == 0) return null;
        return new IkonName( rawName );
    }

    private Ikon createIkonFromEnteredData() {
        IkonName name = name();
        if (name == null) return null;

        try {
            return new Ikon( width(), height(), IkonMaker.DEFAULT_BACKGROUND_COLOUR, name );
        } catch (Exception e) {
            return null;//Data somehow invalid.
        }
    }
}
