package jet.ikonmaker.test;

import jet.ikonmaker.*;
import jet.testtools.*;
import jet.testtools.test.ikonmaker.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;

/**
 * @author Tim Lavers
 */
public class IkonMakerTest extends TestBase {

    private Cyborg cyborg = new Cyborg();
    private UIIkonMaker ui;
    private String exportWarning;

    {
        System.setProperty( "TESTING", "true" );
        String fullMessage = us.message( IkonMakerUserStrings.CHECK_OVERWRITE_IN_EXPORT_MSG0 );
        int breakPos = fullMessage.indexOf( '\n' );
        exportWarning = fullMessage.substring( 0, breakPos );
    }

    public boolean mainTest() {
        //Do init() and cleanup just to ensure that we're starting with a clean slate.
        init();
        cleanup();
        IkonMaker.main( null );
        ui = new UIIkonMaker();
        Waiting.pause( 100 );
        UINewIkonDialog uinid = ui.activateNew();
        assert uinid.okButton() != null;
        uinid.cancel();
        cleanup();
        return true;
    }

    public boolean constructorTest() {
        init();

        //There should not be an IkonCanvas.
        Component canvas = UI.findNamedComponent( IkonMaker.DRAWING_CANVAS_NAME );
        assert canvas == null;

        //There should be an inactive export button.
        assert!UI.isEnabled( ui.exportButton() );

        //There should be an active 'new' button.
        assert UI.isEnabled( ui.newIkonButton() );

        //There should be an inactive 'use image' button.
        assert!UI.isEnabled( ui.useImageButton() );

        //There should be an active 'open' button.
        assert UI.isEnabled( ui.openButton() );

        //These buttons are all the same (sensible) size.
        Dimension size = UI.getSize( ui.exportButton() );
        assert size.width > 40;
        assert size.height > 20;
        assert UI.getSize( ui.openButton() ).equals( size );
        assert UI.getSize( ui.newIkonButton() ).equals( size );
        assert UI.getSize( ui.useImageButton() ).equals( size );
        assert UI.getSize( ui.saveAsButton() ).equals( size );

        //Create an ikon, and all the buttons are enabled.
        ui.createNewIkon( "test", 12, 12 );
        assert UI.isEnabled( ui.exportButton() );
        assert UI.isEnabled( ui.newIkonButton() );
        assert UI.isEnabled( ui.useImageButton() );
        assert UI.isEnabled( ui.openButton() );

        cleanup();
        return true;
    }

    public boolean buildIkonTest() throws IOException {
        init();
        //Build an assymetrical ikon.
        ui.createNewIkon( "ike", 16, 16 );
        ui.selectColour( Color.BLACK );
        ui.uiIkonCanvas( 16, 16 ).dragMouse( 3, 0, 3, 5 );
        ui.selectColour( Color.GREEN );
        ui.uiIkonCanvas( 16, 16 ).dragMouse( 5, 0, 5, 10 );
        //Export it.
        File dest = new File( exportDir, "buildIkonTest.png" );
        ui.export( dest );
        //Read in the exported image and check it.
        Color[][] expected = new Color[16][16];
        for (Color[] anExpected : expected) {
            for (int j = 0; j < anExpected.length; j++) {
                anExpected[j] = IkonMaker.DEFAULT_BACKGROUND_COLOUR;
            }
        }
        for (int j = 0; j < 6; j++) {
            expected[j][3] = Color.BLACK;
        }
        for (int j = 0; j < 11; j++) {
            expected[j][5] = Color.GREEN;
        }
        displayAndCheckSavedImage( dest, expected );
        cleanup();
        return true;
    }

    public boolean exportTest() throws IOException {
        init();
        ui.createNewIkon( "test", 16, 16 );
        File dest = new File( Files.tempDir(), "FirstExport.png" );
        assert!dest.exists();
        ui.export( dest );
        assert dest.exists();
        assert dest.isFile();

        //Check the saved image.
        Color[][] expected = new Color[16][16];
        for (Color[] anExpected : expected) {
            for (int j = 0; j < anExpected.length; j++) {
                anExpected[j] = IkonMaker.DEFAULT_BACKGROUND_COLOUR;
            }
        }
        displayAndCheckSavedImage( dest, expected );

        //Now try to export it again.
        //First we will cancel. We'll check that the
        //last modified time of the file has not changed.
        long lastModified = dest.lastModified();
        //Pause because the timing resolution is
        //pretty rough on some platforms.
        Waiting.pause( 300 );

        ui.export( dest );
        assert UI.findLabelShowingText( exportWarning ) != null;
        //Focus is on yes, tab to no.
        cyborg.tab();
        cyborg.activateFocussedButton();
        assert dest.lastModified() == lastModified;

        //This time, overwrite.
        ui.export( dest );
        assert UI.findLabelShowingText( exportWarning ) != null;
        //Focus is on yes.
        cyborg.activateFocussedButton();
        assert dest.lastModified() > lastModified;
        //Check that the saved image is still ok.
        displayAndCheckSavedImage( dest, expected );

        cleanup();
        return true;
    }

    public boolean saveAsTest() throws IOException {
        init();
        ui.createNewIkon( "test", 16, 16 );
//        ui.s
        File dest = new File( Files.tempDir(), "FirstExport.png" );
        assert!dest.exists();
        ui.export( dest );
        assert dest.exists();
        assert dest.isFile();

        //Check the saved image.
        Color[][] expected = new Color[16][16];
        for (Color[] anExpected : expected) {
            for (int j = 0; j < anExpected.length; j++) {
                anExpected[j] = IkonMaker.DEFAULT_BACKGROUND_COLOUR;
            }
        }
        displayAndCheckSavedImage( dest, expected );

        //Now try to export it again.
        //First we will cancel. We'll check that the
        //last modified time of the file has not changed.
        long lastModified = dest.lastModified();
        //Pause because the timing resolution is
        //pretty rough on some platforms.
        Waiting.pause( 300 );

        ui.export( dest );
        assert UI.findLabelShowingText( exportWarning ) != null;
        //Focus is on yes, tab to no.
        cyborg.tab();
        cyborg.activateFocussedButton();
        assert dest.lastModified() == lastModified;

        //This time, overwrite.
        ui.export( dest );
        assert UI.findLabelShowingText( exportWarning ) != null;
        //Focus is on yes.
        cyborg.activateFocussedButton();
        assert dest.lastModified() > lastModified;
        //Check that the saved image is still ok.
        displayAndCheckSavedImage( dest, expected );

        cleanup();
        return true;
    }

    public boolean cancelExportTest() throws IOException {
        init();
        ui.createNewIkon( "test", 16, 16 );
        ui.activateExport();
        cyborg.escape();
        cleanup();
        return true;
    }

    public boolean addExtensionInExportTest() throws IOException {
        init();
        ui.createNewIkon( "test", 16, 16 );
        ui.activateExport();
        File actualDestination = new File( Files.tempDir(), "FirstExport.png" );
        File destinationAsEntered = new File( Files.tempDir(), "FirstExport" );
        assert!destinationAsEntered.exists();
        assert!actualDestination.exists();
        ui.export( destinationAsEntered );
        assert!destinationAsEntered.exists();
        assert!destinationAsEntered.isFile();
        assert actualDestination.exists();
        assert actualDestination.isFile();
        cleanup();
        return true;
    }

    public boolean mroIkonTest() {
        init();
        //The title of the frame does not mention an ikon name.
        assert UI.getTitle( ui.frame() ).equals( us.message( IkonMakerUserStrings.FRAME_TITLE_MSG0 ) );
        //Open an ikon (copying it first, so that it exists).
        String ikonName = "Iota";
        copyIkonFromTestData( Ikonmaker.Iota_ikon );
        ui.open( ikonName );
        assert UI.getTitle( ui.frame() ).equals( ikonName + " - " + us.message( IkonMakerUserStrings.FRAME_TITLE_MSG0 ) );
        //Close the IkonMaker.
        ui.exit();

        //Now init again. The Iota ikon should be showing.
        new IkonMaker( storeDir );
        ui = new UIIkonMaker();
        assert UI.getTitle( ui.frame() ).equals( ikonName + " - " + us.message( IkonMakerUserStrings.FRAME_TITLE_MSG0 ) );
        assert ui.exportButton().isEnabled();
        cleanup();
        return true;
    }

    public boolean recentExportPathTest() {
        init();
        //Open an ikon (copying it first, so that it exists).
        String ikonName = "Iota";
        copyIkonFromTestData( Ikonmaker.Iota_ikon );
        ui.open( ikonName );
        //Export it.
        String name = "" + System.currentTimeMillis() + ".png";
        File exportTo = new File( exportDir, name );
        ui.export( exportTo );
        //Again activate export.
        ui.export( exportTo );
        assert UI.findLabelShowingText( exportWarning ) != null;
        cyborg.escape();//Cancel the error message.
        cyborg.escape();//Cancel the export dialog.
        cleanup();
        return true;
    }

    public boolean useImageFileChooserTest() {
        init();
        ui.createNewIkon( "ike1", 16, 16 );
        ui.invokeUseImage();
        JFileChooser chooser =
                UI.findFileChooserThatIsCurrentlyShowing();
        //Check that directories and files can be selected
        //(directories so that one can navigate
        //around the file system).
        assert chooser.getFileSelectionMode() ==
                JFileChooser.FILES_AND_DIRECTORIES;
        FileFilter ff = chooser.getFileFilter();
        //Check that .png, .gif, .jpg, and .jpeg
        //files are accepted, and that
        //case is ignored in checking file extensions.
        File png = new File( Ikonmaker.RGGR_png );
        assert ff.accept( png );
        File gif = new File( Ikonmaker.BRRB_gif );
        assert ff.accept( gif );
        File jpg = new File( Ikonmaker.waratah_jpg );
        assert ff.accept( jpg );
        File jpeg = new File( Ikonmaker.BirdInBush_JPEG );
        assert ff.accept( jpeg );

        //Actually choose the png image. Then re-invoke the
        //file chooser and check that it is looking at the
        //directory containing the png image.
        cyborg.type( png.getAbsolutePath() );
        cyborg.enter();
        ui.invokeUseImage();
        chooser = UI.findFileChooserThatIsCurrentlyShowing();
        File currentDir = chooser.getCurrentDirectory();
        assert currentDir.equals( new File( Ikonmaker.PATH ) ) :
                "Current dir: " + currentDir;
        cyborg.escape();//Close the chooser.
        cleanup();
        return true;
    }

    public boolean useImageTest() {
        init();
        ui.createNewIkon( "ike1", 16, 16 );
        useAndCheckRGGRImage();
        cleanup();
        return true;
    }

    /**
     * Checks that the location of the main frame is written to
     * and read from user preferences.
     */
    public boolean frameLocationTest() {
        init();
        //Get the current location.
        Point initialLocation = UI.getScreenLocation( ui.frame() );

        //Move the frame a bit.
        cyborg.dragFrame( ui.frame(), 100, 200 );

        Point newLocation = UI.getScreenLocation( ui.frame() );
        //Sanity check.
        assert newLocation.x <= initialLocation.x + 102;
        assert newLocation.y <= initialLocation.y + 202;
        assert newLocation.x >= initialLocation.x + 98;
        assert newLocation.y >= initialLocation.y + 198;

        //Close the IkonMaker.
        ui.exit();

        //Check that the new location is stored in preferences.
        int prefsX = preferences.getInt(
                IkonMaker.PREFERENCES_KEY_LOCATION_X,
                Integer.MIN_VALUE );
        int prefsY = preferences.getInt(
                IkonMaker.PREFERENCES_KEY_LOCATION_Y,
                Integer.MIN_VALUE );
        assert newLocation.x == prefsX;
        assert newLocation.y == prefsY;

        //Now build a new IkonMaker.
        new IkonMaker( storeDir );
        ui = new UIIkonMaker();

        //Check that it's in the right spot.
        newLocation = UI.getScreenLocation( ui.frame() );
        assert newLocation.x <= initialLocation.x + 102;
        assert newLocation.y <= initialLocation.y + 202;
        assert newLocation.x >= initialLocation.x + 98;
        assert newLocation.y >= initialLocation.y + 198;

        cleanup();
        return true;
    }

    /**
     * Checks that the size of the main frame is written to
     * and read from user preferences.
     */
    public boolean frameSizeTest() {
        init();
        //Get the current size.
        Dimension initialSize = UI.getSize( ui.frame() );

        //Re-size the frame a bit.
        cyborg.resizeFrame( ui.frame(), 20, 20 );

        Dimension newSize = UI.getSize( ui.frame() );
        //Sanity check.
        assert newSize.width <= initialSize.width + 22;
        assert newSize.width >= initialSize.width + 18;
        assert newSize.height <= initialSize.height + 22;
        assert newSize.height >= initialSize.height + 18;

        //Close the IkonMaker.
        ui.exit();

        //Check that the new size is stored in preferences.
        int prefsWidth = preferences.getInt(
                IkonMaker.PREFERENCES_KEY_WIDTH,
                Integer.MIN_VALUE );
        int prefsHeight = preferences.getInt(
                IkonMaker.PREFERENCES_KEY_HEIGHT,
                Integer.MIN_VALUE );
        assert newSize.width == prefsWidth;
        assert newSize.height == prefsHeight;

        //Now build a new IkonMaker.
        new IkonMaker( storeDir );
        ui = new UIIkonMaker();

        //Check that it's the right size.
        newSize = UI.getSize( ui.frame() );
        assert newSize.width <= initialSize.width + 22;
        assert newSize.width >= initialSize.width + 18;
        assert newSize.height <= initialSize.height + 22;
        assert newSize.height >= initialSize.height + 18;

        cleanup();
        return true;
    }

    private void useAndCheckRGGRImage() {
        File rggr = new File( Ikonmaker.RGGR_png );
        ui.useImage( rggr );
        File dest = new File( Files.tempDir(), "ike1.png" );
        ui.export( dest );
        //RGGR is a 32-by-32 image with red in the top left and bottom right quarters, green elsewhere.
        Color[][] expected = new Color[16][16];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                expected[i][j] = Color.RED;
            }
            for (int j = 8; j < 16; j++) {
                expected[i][j] = Color.GREEN;
            }
        }
        for (int i = 8; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                expected[i][j] = Color.GREEN;
            }
            for (int j = 8; j < 16; j++) {
                expected[i][j] = Color.RED;
            }
        }
        displayAndCheckSavedImage( dest, expected );
    }

    public boolean cancelUseImageTest() {
        init();
        ui.createNewIkon( "ike", 16, 16 );
        ui.invokeUseImage();
        cyborg.escape();
        useAndCheckRGGRImage();
        cleanup();
        return true;
    }

    public boolean newIkonTest() {
        init();
        //First a simple one. Build it and save and check the image.
        ui.createNewIkon( "ike1", 16, 16 );
        Color colour = new Color( 55, 66, 222 );
        ui.selectColour( colour );
        ui.uiIkonCanvas( 16, 16 ).clickPixelSquare( 5, 6 );
        ui.uiIkonCanvas( 16, 16 ).clickPixelSquare( 6, 7 );
        ui.uiIkonCanvas( 16, 16 ).clickPixelSquare( 7, 8 );
        ui.uiIkonCanvas( 16, 16 ).clickPixelSquare( 8, 9 );
        File dest = new File( Files.tempDir(), "ike1.png" );
        ui.export( dest );
        Color[][] expected = new Color[16][16];
        for (Color[] anExpected : expected) {
            for (int j = 0; j < anExpected.length; j++) {
                anExpected[j] = IkonMaker.DEFAULT_BACKGROUND_COLOUR;
            }
        }
        expected[6][5] = colour;
        expected[7][6] = colour;
        expected[8][7] = colour;
        expected[9][8] = colour;
        displayAndCheckSavedImage( dest, expected );

        //Now a small ikon.
        ui.createNewIkon( "ike2", 4, 4 );
        colour = new Color( 234, 66, 22 );
        ui.selectColour( colour );
        ui.uiIkonCanvas( 4, 4 ).clickPixelSquare( 0, 0 );
        ui.uiIkonCanvas( 4, 4 ).clickPixelSquare( 0, 1 );
        ui.uiIkonCanvas( 4, 4 ).clickPixelSquare( 1, 0 );
        ui.uiIkonCanvas( 4, 4 ).clickPixelSquare( 1, 1 );
        dest = new File( Files.tempDir(), "ike2.png" );
        ui.export( dest );
        expected = new Color[4][4];
        for (Color[] anExpected1 : expected) {
            for (int j = 0; j < anExpected1.length; j++) {
                anExpected1[j] = IkonMaker.DEFAULT_BACKGROUND_COLOUR;
            }
        }
        expected[0][0] = colour;
        expected[0][1] = colour;
        expected[1][0] = colour;
        expected[1][1] = colour;
        displayAndCheckSavedImage( dest, expected );
        cleanup();
        return true;
    }

    private void displayAndCheckSavedImage( final File imageFile, final Color[][] expectedPixels ) {
        final JFrame[] frames = new JFrame[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                frames[0] = new JFrame();
            }
        } );
        final JLabel[] result = new JLabel[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                result[0] = new JLabel();
                result[0].setIcon( new ImageIcon(
                        Toolkit.getDefaultToolkit().createImage(
                                imageFile.getAbsolutePath() ) ) );
                frames[0].add( result[0] );
                frames[0].pack();
                frames[0].setVisible( true );
                frames[0].toFront();
            }
        } );
        final JComponent imageComponent = result[0];
        for (int i = 0; i < expectedPixels.length; i++) {
            Color[] expectedPixel = expectedPixels[i];
            for (int j = 0; j < expectedPixel.length; j++) {
                Color color = expectedPixel[j];
                cyborg.checkPixelInComponent(
                        i, j, color, imageComponent );
            }
        }
        //Close the frame showing the image.
        UI.runInEventThread( new Runnable() {
            public void run() {
                frames[0].dispose();
            }
        } );
    }

    void init() {
        super.init();
        UI.runInEventThread( new Runnable() {
            public void run() {
                new IkonMaker( storeDir );
            }
        } );
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return UI.findNamedFrame( IkonMaker.FRAME_NAME ) != null;
            }
        }, 2000 );
        ui = new UIIkonMaker();
    }

    private void cleanup() {
        assert ui != null : "Cleanup without init";
        System.setProperty( "TESTING", "true" );
//        assert ui.frame().hasFocus() : "IkonMaker main frame not on top";
        ui.exit();
        boolean exited = Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return ui.frame() == null;
            }
        }, 2000 );
        assert exited : "IkonMaker main frame still showing, perhaps it did not have the focus.";
//        cyborg.closeTopWindow();
//        UI.disposeOfAllFrames();
//        Waiting.pause( 100 );
    }
}