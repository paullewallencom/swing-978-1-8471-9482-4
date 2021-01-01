package jet.ikonmaker;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.SortedSet;

/**
 * Presents a list of existing ikons to the user, with
 * thumbnails of each.
 * 
 * @author Tim Lavers
 */
public class VisualOpenDialog {
    public static Dimension MINIMUM_SIZE = new Dimension( 200, 180 );
    public static Dimension MAXIMUM_SIZE = new Dimension( 800, 600 );
    public static String NAMES_LIST_NAME = "OpenDialog.NamesList";
    private JDialog dialog;
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();
    private JButton okButton;
    private JList namesList;
    private boolean wasCancelled = false;

    public VisualOpenDialog( JFrame owningFrame, SortedSet<Ikon> storedIkons ) {
        namesList = new JList();
        DefaultListModel dlm = new DefaultListModel();
        for (Ikon ikon : storedIkons) {
            dlm.addElement( ikon );
        }
        namesList.setModel( dlm );
        namesList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        namesList.setName( NAMES_LIST_NAME );
        namesList.setLayoutOrientation( JList.VERTICAL_WRAP );
        namesList.setCellRenderer( new ListCellRenderer() {
            public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
                Ikon ikon = (Ikon) value;
                Box panel = Box.createHorizontalBox();
                IkonCanvas ic = new IkonCanvas( null, 1, ikon, false, "VOD:" + ikon.name().toString(), null );
                panel.add( ic.component() );
                panel.add( Box.createHorizontalStrut( 5 ) );
                String labelText = ikon.name().toString();
                if (labelText.length() > 18) {
                    labelText = labelText.substring( 0, 15 ) + "...";
                }
                Color labelAndBorderColour = isSelected ? Color.BLUE : Color.BLACK;
                JLabel label = new JLabel( labelText );
                label.setForeground( labelAndBorderColour );
                panel.add( label );
                panel.setToolTipText( ikon.name().toString() );
                int borderThickness = isSelected ? 2 : 1;
                panel.setBorder( BorderFactory.createLineBorder( labelAndBorderColour, borderThickness ) );
                return panel;
            }
        } );

        //Ok and cancel buttons in a box.
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add( Box.createHorizontalGlue() );
        AbstractAction okAction = new AbstractAction() {
            public void actionPerformed( ActionEvent a ) {
                dialog.dispose();
            }
        };
        okButton = new JButton( okAction );
        okButton.setName( IkonMakerUserStrings.OK );
        okButton.setText( us.label( IkonMakerUserStrings.OK ) );
        okButton.setMnemonic( us.mnemonic( IkonMakerUserStrings.OK ) );
        okButton.setEnabled( false );
        buttonBox.add( okButton );

        AbstractAction cancelAction = new AbstractAction() {
            public void actionPerformed( ActionEvent a ) {
                wasCancelled = true;
                dialog.dispose();
            }
        };
        JButton cancelButton = new JButton( cancelAction );
        cancelButton.setName( IkonMakerUserStrings.CANCEL );
        cancelButton.setText( us.label( IkonMakerUserStrings.CANCEL ) );
        cancelButton.setMnemonic( us.mnemonic( IkonMakerUserStrings.CANCEL ) );
        buttonBox.add( Box.createHorizontalStrut( 5 ) );
        buttonBox.add( cancelButton );

        //Listen for selection events, so that the OK button can be enabled when something is selected.
        namesList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                okButton.setEnabled( true );
            }
        } );

        dialog = new JDialog( owningFrame, us.message( IkonMakerUserStrings.OPEN_IKON_MSG0 ), true );
        dialog.getContentPane().setLayout( new BorderLayout() );
        JScrollPane scrollPane = new JScrollPane( namesList );
        scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        dialog.getContentPane().add( scrollPane, BorderLayout.CENTER );
        dialog.getContentPane().add( buttonBox, BorderLayout.SOUTH );

        //Key bindings.
        String keyName = "escape";
        cancelButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( "pressed " + keyName.toUpperCase() ), keyName );
        cancelButton.getActionMap().put( keyName, cancelAction );
        keyName = "enter";
        okButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( "pressed " + keyName.toUpperCase() ), keyName );
        okButton.getActionMap().put( keyName, okAction );

        int numIkons = namesList.getModel().getSize();
        int rowColLength = (int) Math.sqrt( numIkons ) + 1;
        int width = rowColLength * 80;
        width = Math.max( MINIMUM_SIZE.width, width );
        width = Math.min( MAXIMUM_SIZE.width, width );
        int height = rowColLength * 50;
        height = Math.max( MINIMUM_SIZE.height, height );
        height = Math.min( MAXIMUM_SIZE.height, height );
        dialog.setSize( new Dimension( width, height ) );
    }

    public void show() {
        dialog.setVisible( true );
    }

    public boolean wasCancelled() {
        return wasCancelled;
    }

    public IkonName selectedName() {
        Object selectedValue = namesList.getSelectedValue();
        return selectedValue == null ? null : ((Ikon) selectedValue).name();
    }

    public JDialog dialog() {
        return dialog;
    }
}