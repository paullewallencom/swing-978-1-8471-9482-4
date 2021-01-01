package jet.ikonmaker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;

/**
 * The main class for the Ikon Do It application, which is
 * used as a source of examples throughout the book.
 * 
 * @author Tim Lavers
 */
public class IkonMaker {
    public static Color DEFAULT_BACKGROUND_COLOUR = new Color( 255, 255, 255, 255 );
    public static String DRAWING_CANVAS_NAME = "IkonMaker.DrawingCanvas";
    public static String FRAME_NAME = "IkonMaker.Frame";
    public static String PREVIEW_CANVAS_NAME = "IkonMaker.PreviewCanvas";
    public static String INSTRUCTIONS_AREA_NAME = "IkonMaker.InstructionsArea";
    public static final String PREFERENCES_KEY_LAST_EDITED_IKON = "LastEditedIkon";
    public static final String PREFERENCES_KEY_LAST_EXPORT_DIRECTORY = "LastExportDirectory";
    public static final String PREFERENCES_KEY_LAST_IMAGE_DIRECTORY = "LastImageDirectory";
    public static final String PREFERENCES_KEY_LOCATION_X = "LocationX";
    public static final String PREFERENCES_KEY_LOCATION_Y = "LocationY";
    public static final String PREFERENCES_KEY_WIDTH = "Width";
    public static final String PREFERENCES_KEY_HEIGHT = "Height";
    private JFrame frame;
    private Action exportIconAction;
    private Action saveAsAction;
    private Action fillWithImageAction;
    private Store store;
    private IkonMakerUserStrings userStrings = IkonMakerUserStrings.instance();
    private Ikon ikon;
    private IkonHistory history;
    private IkonCanvas ikonCanvas;
    private JColorChooser colourChooser;
    private Color currentlySelectedColour = Color.WHITE;
    private TransparencySelecter transparencySelecter;
    private JComponent ikonCanvasHolder = new JPanel();
    private JComponent previewCanvasHolder = new JPanel();
    private JTextArea explanationArea;
    private Preferences preferences;

    public static void main( String[] args ) {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    new IkonMaker( new File( System.getProperty( "user.dir" ) ) );
                }
            } );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IkonMaker( File storageDirectory ) {
        preferences = Preferences.userNodeForPackage( getClass() );
        store = new FlatFileStore( storageDirectory );
        frame = new JFrame( userStrings.message( IkonMakerUserStrings.FRAME_TITLE_MSG0 ) );
        frame.setName( FRAME_NAME );
        URL ikonUrl = ClassLoader.getSystemResource( "jet/ikonmaker/iota.png" );
        //When running this from ant, the url is null. I know not why, so put this silly workaround in.
        if (ikonUrl != null) {
            frame.setIconImage( Toolkit.getDefaultToolkit().getImage( ikonUrl ) );
        }
        frame.setLayout( new BorderLayout() );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                closing();
            }
        } );

        //Create the actions.
        Action createIconAction = new AbstractAction( userStrings.label( IkonMakerUserStrings.NEW ) ) {
            public void actionPerformed( ActionEvent e ) {
                createNewIkon();
            }
        };
        createIconAction.putValue( AbstractAction.MNEMONIC_KEY, userStrings.mnemonic( IkonMakerUserStrings.NEW ) );
        exportIconAction = new AbstractAction( userStrings.label( IkonMakerUserStrings.EXPORT ) ) {
            public void actionPerformed( ActionEvent e ) {
                exportIkon();
            }
        };
        exportIconAction.putValue( AbstractAction.MNEMONIC_KEY, userStrings.mnemonic( IkonMakerUserStrings.EXPORT ) );
        fillWithImageAction = new AbstractAction( userStrings.label( IkonMakerUserStrings.FILL_WITH_IMAGE ) ) {
            public void actionPerformed( ActionEvent e ) {
                fillWithImage();
            }
        };
        fillWithImageAction.putValue( AbstractAction.MNEMONIC_KEY, userStrings.mnemonic( IkonMakerUserStrings.FILL_WITH_IMAGE ) );
        Action openIconAction = new AbstractAction( userStrings.label( IkonMakerUserStrings.OPEN ) ) {
            public void actionPerformed( ActionEvent e ) {
                openIkon();
            }
        };
        openIconAction.putValue( AbstractAction.MNEMONIC_KEY, userStrings.mnemonic( IkonMakerUserStrings.OPEN ) );
        saveAsAction = new AbstractAction( userStrings.label( IkonMakerUserStrings.SAVE_AS ) ) {
            public void actionPerformed( ActionEvent e ) {
                saveAs();
            }
        };
        saveAsAction.putValue( AbstractAction.MNEMONIC_KEY, userStrings.mnemonic( IkonMakerUserStrings.SAVE_AS ) );

        //Initially, the actions particular to an icon are not enabled.
        exportIconAction.setEnabled( false );
        fillWithImageAction.setEnabled( false );
        saveAsAction.setEnabled( false );

        //Build the user interface.
        //The buttons and preview canvas.
        JComponent buttonBox = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets( 10, 0, 0, 0 );
        buttonBox.add( new JButton( createIconAction ), gbc );
        gbc.gridy = 1;
        buttonBox.add( new JButton( openIconAction ), gbc );
        gbc.gridy = 2;
        buttonBox.add( new JButton( fillWithImageAction ), gbc );
        gbc.gridy = 3;
        buttonBox.add( new JButton( saveAsAction ), gbc );
        gbc.gridy = 4;
        buttonBox.add( new JButton( exportIconAction ), gbc );
        gbc.gridy = 5;
        gbc.insets = new Insets( 100, 0, 10, 0 );
        buttonBox.add( previewCanvasHolder, gbc );
        frame.add( buttonBox, BorderLayout.EAST );

        //The editing canvas and instructions area.
        JPanel centrePanel = new JPanel( new BorderLayout() );
        centrePanel.add( ikonCanvasHolder, BorderLayout.CENTER );
        explanationArea = new JTextArea();
        explanationArea.setEditable( false );
        explanationArea.setLineWrap( true );
        explanationArea.setWrapStyleWord( true );
        explanationArea.setSize( 150, 50 );
        explanationArea.setBackground( frame.getBackground() );
        explanationArea.setForeground( new JLabel().getForeground() );
        explanationArea.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        //Set the initial explanation.
        setExplanation();
        centrePanel.add( explanationArea, BorderLayout.SOUTH );
        frame.add( centrePanel, BorderLayout.CENTER );

        //The colour chooser and transparency chooser.
        colourChooser = new JColorChooser();
        colourChooser.getSelectionModel().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                colourChosen();
            }
        } );
        JComponent colourAndTransparencyPanel = Box.createHorizontalBox();
        colourAndTransparencyPanel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 5 ) );
        colourAndTransparencyPanel.add( colourChooser );
        colourAndTransparencyPanel.add( Box.createHorizontalStrut( 20 ) );
        transparencySelecter = new TransparencySelecter();
        colourAndTransparencyPanel.add( transparencySelecter.ui() );
        frame.add( colourAndTransparencyPanel, BorderLayout.SOUTH );

        //Show the frame, and open the most recently edited ikon.
        int width = preferences.getInt( PREFERENCES_KEY_WIDTH, -1 );
        int height = preferences.getInt( PREFERENCES_KEY_HEIGHT, -1 );
        if (width > 0 && height > 0) {
            frame.setSize( width, height );
        } else {
            frame.pack();
        }
        int x = preferences.getInt( PREFERENCES_KEY_LOCATION_X, 0 );
        int y = preferences.getInt( PREFERENCES_KEY_LOCATION_Y, 0 );
        frame.setLocation( x, y );
        frame.setVisible( true );
        frame.toFront();
        String mroIkon = preferences.get( PREFERENCES_KEY_LAST_EDITED_IKON, null );
        if (mroIkon != null) {
            loadIkon( new IkonName( mroIkon ) );
        }
    }

    private void openIkon() {
        VisualOpenDialog od = new VisualOpenDialog( frame, store.storedIkons() );
        od.show();//Blocks.
        if (!od.wasCancelled()) {
            loadIkon( od.selectedName() );
        }
    }

    private void loadIkon( IkonName name ) {
        //Clean up the current ikon.
        if (history != null) {
            history.shuttingDown();
        }

        //Remove the current ikon displays.
        previewCanvasHolder.removeAll();
        ikonCanvasHolder.removeAll();

        //Create new ikon and history.
        history = store.historyOf( name );
        Dimension size = history.dimension();
        ikon = new Ikon( size.width, size.height, DEFAULT_BACKGROUND_COLOUR, name );

        //Display new ikon.
        IkonCanvas previewCanvas = new IkonCanvas( null, 1, ikon, false, PREVIEW_CANVAS_NAME, null );
        previewCanvasHolder.add( previewCanvas.component() );
        ikonCanvas = new IkonCanvas( new IkonCanvas.ColourSupplier() {
            public Color currentColour() {
                return new Color( currentlySelectedColour.getRed(), currentlySelectedColour.getGreen(), currentlySelectedColour.getBlue(), currentlySelectedTransparency() );
            }

            public void setCurrentColour( Color c ) {
                currentlySelectedColour = c;
                colourChooser.setColor( c );
                transparencySelecter.setValue( c.getAlpha() );
            }
        }, IkonCanvas.pixelSizeForOptimalDrawingSize( ikon.width(), ikon.height() ), ikon, true, DRAWING_CANVAS_NAME, null );
        ikonCanvasHolder.add( ikonCanvas.component() );

        //Register canvases as listeners.
        ikon.addListener( previewCanvas );
        ikon.addListener( ikonCanvas );

        //Load the new ikon.
        for (EditOperation op : history.allOperations()) {
            op.apply( ikon );
        }
        //Now that all the ops are done, add the history as a listener.
        ikon.addListener( history );

        //The editing image actions should now be enabled.
        exportIconAction.setEnabled( true );
        fillWithImageAction.setEnabled( true );
        saveAsAction.setEnabled( true );

        //Set the frame title.
        frame.setTitle( ikon.name().toString() + " - " + userStrings.message( IkonMakerUserStrings.FRAME_TITLE_MSG0 ) );

        //Re-set the explanation.
        setExplanation();

        //Refresh the display.
        frame.getContentPane().validate();

        //Remember this as the most recently opened ikon.
        preferences.put( PREFERENCES_KEY_LAST_EDITED_IKON, ikon.name().toString() );
    }

    private void createNewIkon() {
        NewIkonDialog nid = new NewIkonDialog( frame );
        nid.show();
        Ikon ike = nid.created();
        if (ike != null) {
            //Save it.
            store.saveNewIkon( ike );
            //Then load it.
            loadIkon( ike.name() );
        }
    }

    private void colourChosen() {
        currentlySelectedColour = colourChooser.getColor();
    }

    private int currentlySelectedTransparency() {
        return transparencySelecter.value();
    }

    private void saveAs() {
        SaveAsDialog sad = new SaveAsDialog( frame,
                store.storedIkonNames() );
        sad.show();
        if (!sad.wasCancelled()) {
            //Create a copy with the new name.
            IkonName newName = sad.name();
            Ikon saveAsIkon = ikon.copy( newName );
            //Save and then load the new ikon.
            store.saveNewIkon( saveAsIkon );
            loadIkon( newName );
        }
    }

    private void fillWithImage() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter( new FileFilter() {
            public boolean accept( File f ) {
                if (f.isDirectory()) return true;
                String nameLC = f.getName().toLowerCase();
                return nameLC.endsWith( ".png" ) || nameLC.endsWith( ".gif" ) || nameLC.endsWith( ".jpeg" ) || nameLC.endsWith( ".jpg" );
            }

            public String getDescription() {
                return userStrings.message( IkonMakerUserStrings.IMAGE_FILES_DESCRIPTION_MSG0 );
            }
        } );
        String lastImageDirPath = preferences.get( PREFERENCES_KEY_LAST_IMAGE_DIRECTORY, null );
        File lastImageDir = null;
        if (lastImageDirPath != null) {
            lastImageDir = new File( lastImageDirPath );
        }
        jfc.setCurrentDirectory( lastImageDir );
        jfc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
        int action = jfc.showDialog( frame, userStrings.label( IkonMakerUserStrings.FILL_WITH_IMAGE ) );
        if (action == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            if (file.exists() && file.isFile()) {
                preferences.put( PREFERENCES_KEY_LAST_IMAGE_DIRECTORY, file.getParent() );
                try {
                    BufferedImage bim = ImageIO.read( file );
                    ikon.fillWithImage( bim );
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    private void exportIkon() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter( new FileFilter() {
            public boolean accept( File f ) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith( ".png" );
            }

            public String getDescription() {
                return userStrings.message( IkonMakerUserStrings.PNG_DESCRIPTION_MSG0 );
            }
        } );
        String lastExportDirPath = preferences.get( PREFERENCES_KEY_LAST_EXPORT_DIRECTORY, null );
        File lastExportDir = null;
        if (lastExportDirPath != null) {
            lastExportDir = new File( lastExportDirPath );
        }
        jfc.setCurrentDirectory( lastExportDir );
        jfc.setDialogTitle( userStrings.message( IkonMakerUserStrings.EXPORT_MSG0 ) );
        jfc.showDialog( frame, userStrings.label( IkonMakerUserStrings.EXPORT ) );
        File file = jfc.getSelectedFile();
        if (file == null) return;

        //Add the ".png" extension if necessary.
        if (!file.getName().toLowerCase().endsWith( ".png" )) {
            file = new File( file.getAbsolutePath() + ".png" );
        }
        if (file.exists()) {
            if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog( frame, userStrings.message( IkonMakerUserStrings.CHECK_OVERWRITE_IN_EXPORT_MSG0 ), "", JOptionPane.YES_NO_OPTION ))
                return;
        }
        preferences.put( PREFERENCES_KEY_LAST_EXPORT_DIRECTORY, file.getParent() );
        try {
            ImageIO.write( ikonCanvas.ikon().asImage(), "png", file );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void setExplanation() {
        String key = ikon == null ? IkonMakerUserStrings.INSTRUCTIONS_WITH_NO_IKON_MSG0 : IkonMakerUserStrings.INSTRUCTIONS_WITH_IKON_MSG0;
        explanationArea.setText( userStrings.message( key ) );
    }

    private void closing() {
        if (history != null) history.shuttingDown();
        //Save the frame preferences.
        Point location = frame.getLocationOnScreen();
        preferences.putInt( PREFERENCES_KEY_LOCATION_X, location.x );
        preferences.putInt( PREFERENCES_KEY_LOCATION_Y, location.y );
        Dimension size = frame.getSize();
        preferences.putInt( PREFERENCES_KEY_WIDTH, size.width );
        preferences.putInt( PREFERENCES_KEY_HEIGHT, size.height );
        frame.dispose();
        if (!Boolean.getBoolean( "TESTING" )) {
            System.exit( 0 );
        }
    }
}
