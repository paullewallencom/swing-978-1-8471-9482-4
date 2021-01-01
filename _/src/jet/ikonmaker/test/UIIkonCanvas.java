package jet.ikonmaker.test;

import jet.testtools.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author Tim Lavers
 */
public class UIIkonCanvas {
    private Cyborg robot = new Cyborg();
    private JComponent component;
    private int pixelSize;

    public UIIkonCanvas( String canvasName, int pixelSize ) {
        component = (JComponent) UI.findNamedComponent( canvasName );
        assert component != null : "Could not find canvas.";
        this.pixelSize = pixelSize;
    }

    public void clickPixelSquare( int row, int col ) {
        robot.mouseLeftClick( pixelToPoint( row, col ) );
    }

    public void dragMouse( int startRow, int startCol, int endRow, int endCol ) {
        robot.mouseDrag( pixelToPoint( startRow, startCol ), pixelToPoint( endRow, endCol ) );
    }

    private Point pixelToPoint( int row, int col ) {
        final Point locationOnScreen = UI.getScreenLocation( component );
        return new Point( locationOnScreen.x + pixelSize * col, locationOnScreen.y + pixelSize * row );
    }
}

