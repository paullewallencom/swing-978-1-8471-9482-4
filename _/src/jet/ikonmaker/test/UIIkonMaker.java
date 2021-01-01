package jet.ikonmaker.test;

import jet.testtools.*;
import jet.ikonmaker.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author Tim Lavers
 */
public class UIIkonMaker {
    private Cyborg cyborg = new Cyborg();
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();

    public Frame frame() {
        return UI.findNamedFrame( IkonMaker.FRAME_NAME );
    }

    public void exit() {
        cyborg.closeTopWindow();
    }

    public JButton openButton() {
        return UI.findButtonWithText( us.label( IkonMakerUserStrings.OPEN ) );
    }

    public JButton exportButton() {
        return UI.findButtonWithText( us.label( IkonMakerUserStrings.EXPORT) );
    }

    public JButton newIkonButton() {
        return UI.findButtonWithText( us.label( IkonMakerUserStrings.NEW ) );
    }

    public JButton saveAsButton() {
        return UI.findButtonWithText( us.label( IkonMakerUserStrings.SAVE_AS ) );
    }

    public JButton useImageButton() {
        return UI.findButtonWithText( us.label( IkonMakerUserStrings.FILL_WITH_IMAGE ) );
    }

    public void useImage( File imageFile ) {
        invokeUseImage();
        cyborg.type( imageFile.getAbsolutePath() );
        cyborg.enter();
    }

    public void invokeUseImage() {
        cyborg.altChar( us.mnemonic( IkonMakerUserStrings.FILL_WITH_IMAGE ).intValue() );
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return UI.findFileChooserThatIsCurrentlyShowing() != null;
            }
        }, 2000, 100 );
    }

    public void export( File toExportTo ) {
        activateExport();
        cyborg.type( toExportTo.getAbsolutePath() );
        cyborg.enter();
    }

    public void activateExport() {
        cyborg.altChar( us.mnemonic( IkonMakerUserStrings.EXPORT ).intValue() );
    }

    public UINewIkonDialog activateNew() {
        cyborg.altChar( us.mnemonic( IkonMakerUserStrings.NEW ).intValue() );
        return new UINewIkonDialog();
    }

    public UIOpenDialog activateOpen() {
        cyborg.altChar( us.mnemonic( IkonMakerUserStrings.OPEN ).intValue() );
        return new UIOpenDialog();
    }

    public UISaveAsDialog activateSaveAs() {
        cyborg.altChar( us.mnemonic( IkonMakerUserStrings.SAVE_AS ).intValue() );
        return new UISaveAsDialog();
    }

    public void open( String name ) {
        activateOpen().open( name );
    }

    public void createNewIkon( String name, int width, int height ) {
        UINewIkonDialog nid = activateNew();
        nid.setHeight( height );
        nid.setWidth( width );
        nid.setName( name );
        nid.ok();
    }

    public UIIkonCanvas uiIkonCanvas( int ikonWidth, int ikonHeight ) {
        return new UIIkonCanvas( IkonMaker.DRAWING_CANVAS_NAME, IkonCanvas.pixelSizeForOptimalDrawingSize( ikonWidth, ikonHeight ) );
    }
    
    public void selectColour( Color colour ) {
        //Choose the RGB swatch.
        cyborg.altChar( mn("ColorChooser.rgbMnemonic") );
        enterColourComponent( mn("ColorChooser.rgbRedMnemonic"),
                colour.getRed() ) ;
        enterColourComponent( mn("ColorChooser.rgbGreenMnemonic"),
                colour.getGreen() ) ;
        enterColourComponent( mn("ColorChooser.rgbBlueMnemonic"),
                colour.getBlue() ) ;
        //Return to the colour tiles swatch,
        //which does not use any mnemonics
        //used elsewhere.
        cyborg.altChar( mn("ColorChooser.swatchesMnemonic") );
    }

    private int mn( String uiManagerConstant ) {
        Object uiValue = UIManager.get(uiManagerConstant);
        return Integer.parseInt( uiValue.toString() );
    }
    
    private void enterColourComponent( int mnemonic, int value ) {
        cyborg.altChar( mnemonic );
        cyborg.tab();//Go to text field
        cyborg.selectAllText();
        cyborg.type( "" + value );
        cyborg.enter();
    }
}
