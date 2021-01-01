package jet.ikonmaker.test;

import javax.swing.*;
import jet.ikonmaker.*;
import jet.testtools.*;

import java.awt.*;

/**
 * @author Tim Lavers
 */
public class UINewIkonDialog {
    
    Cyborg robot = new Cyborg();
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();

    public void setName( String name ) {
        robot.altChar( us.mnemonic( IkonMakerUserStrings.NAME ) );
        robot.type( name );
    }

    public void setHeight( int height ) {
        typeIntoHeightField( "" + height );
    }

    public void typeIntoHeightField( String str ) {
        robot.altChar( us.mnemonic( IkonMakerUserStrings.HEIGHT ) );
        robot.type( str );
    }

    public void setWidth( int width ) {
        typeIntoWidthField( "" + width );
    }

    public void typeIntoWidthField( String str ) {
        robot.altChar( us.mnemonic( IkonMakerUserStrings.WIDTH ) );
        robot.type( str );
    }

    public void ok() {
        robot.altChar( us.mnemonic( IkonMakerUserStrings.OK ) );
    }

    public void cancel() {
        robot.altChar( us.mnemonic( IkonMakerUserStrings.CANCEL ) );
    }

    public Component okButton() {
        return UI.findNamedComponent( IkonMakerUserStrings.OK );
    }

    public Component cancelButton() {
        return UI.findNamedComponent( IkonMakerUserStrings.CANCEL );
    }

    public JTextField widthField() {
        return (JTextField) UI.findNamedComponent( IkonMakerUserStrings.WIDTH );
    }

    public JTextField heightField() {
        return (JTextField) UI.findNamedComponent( IkonMakerUserStrings.HEIGHT );
    }
}
