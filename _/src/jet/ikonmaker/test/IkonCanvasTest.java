package jet.ikonmaker.test;

import jet.ikonmaker.*;
import jet.testtools.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author Tim Lavers
 */
public class IkonCanvasTest {

    private Cyborg robot = new Cyborg();
    private UIIkonCanvas ui;
    private Ikon ike;
    private IkonCanvas canvas;
    private JFrame frame;
    private Color colourToSupply = Color.RED;

    public boolean constructorTest() {
        init();
        //Just a basic check that the component is there and is the right size.
        assert canvas.component().getSize().height == 30;
        assert canvas.component().getSize().width == 40;
        return true;
    }

    public boolean pixelSizeForOptimalDrawingSizeTest() {
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 1, 1 ) == 20;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 1, 5 ) == 20;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 1, 10 ) == 12;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 1, 15 ) == 12;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 1, 19 ) == 12;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 19, 19 ) == 12;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 19, 20 ) == 8;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 20, 20 ) == 8;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 25, 20 ) == 8;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 25, 29 ) == 8;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 30, 20 ) == 6;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 35, 20 ) == 6;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 39, 20 ) == 6;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 39, 39 ) == 6;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 39, 40 ) == 5;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 39, 49 ) == 5;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 49, 49 ) == 5;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 39, 50 ) == 4;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 59, 59 ) == 4;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 69, 59 ) == 3;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 69, 69 ) == 3;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 39, 75 ) == 2;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 39, 79 ) == 2;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 79, 79 ) == 2;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 80, 79 ) == 1;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 80, 81 ) == 1;
        assert IkonCanvas.pixelSizeForOptimalDrawingSize( 39000, 478970 ) == 1;
        return true;
    }

    public boolean getMinimumSizeTest() {
        init();
        assert canvas.component().getMinimumSize().height == 30;
        assert canvas.component().getMinimumSize().width == 40;
        return true;
    }

    public boolean getMaximumSizeTest() {
        init();
        assert canvas.component().getMaximumSize().height == 30;
        assert canvas.component().getMaximumSize().width == 40;
        return true;
    }

    public boolean getSizeTest() {
        init();
        assert canvas.component().getSize().height == 30;
        assert canvas.component().getSize().width == 40;
        return true;
    }

    public boolean getPreferredSizeTest() {
        init();
        assert canvas.component().getPreferredSize().height == 30;
        assert canvas.component().getPreferredSize().width == 40;
        return true;
    }

    public boolean componentTest() {
        //Tested in the above.
        return true;
    }

    public boolean paintTest() {
        guiInit();
        checkIkonCanvas( canvas );
        cleanup();
        return true;
    }

    public boolean ikonChangedTest() {
        guiInit();
        ike.setColourAt( Color.YELLOW, 2, 3 );
        UI.runInEventThread( new Runnable() {
            public void run() {
                canvas.ikonChanged( null );
            }
        } );
        Color[][] expected = new Color[6][8];
        for (Color[] anExpected : expected) {
            for (int j = 0; j < anExpected.length; j++) {
                anExpected[j] = Color.MAGENTA;
            }
        }
        expected[2][3] = Color.YELLOW;
        checkIkonCanvas( canvas );//Confirm the repaint.
        cleanup();
        return true;
    }

    public boolean mouseClickedTest() {
        guiInit();
        ui.clickPixelSquare( 2, 3 );
        //Check that the colour has been changed in the underlying ikon.
        Color[][] expected = new Color[6][8];
        for (Color[] anExpected : expected) {
            for (int j = 0; j < anExpected.length; j++) {
                anExpected[j] = Color.MAGENTA;
            }
        }
        expected[2][3] = colourToSupply;
        checkIkonColours( expected );
        checkIkonCanvas( canvas );

        //Check that each square can be turned green.
        colourToSupply = Color.GREEN;
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                expected[i][j] = Color.GREEN;
                ui.clickPixelSquare( i, j );
                checkIkonColours( expected );
                checkIkonCanvas( canvas );
            }
        }
        cleanup();
        return true;
    }

    public boolean mouseDraggedTest() {
        guiInit( 8, 8, Color.BLUE );
        //Drag from top left to bottom right.
        ui.dragMouse( 0, 0, 7, 7 );
        Color[][] expected = new Color[8][8];
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                if (i != j) {
                    expected[i][j] = Color.BLUE;
                } else {
                    expected[i][j] = colourToSupply;
                }
            }
        }
        checkIkonColours( expected );
        checkIkonCanvas( canvas );

        //Drag from top left to top right.
        ui.dragMouse( 0, 0, 0, 7 );
        for (int j = 0; j < 8; j++) {
            expected[0][j] = colourToSupply;
        }
        checkIkonColours( expected );
        checkIkonCanvas( canvas );

        cleanup();
        return true;
    }

    public boolean pixelSizeTest() {
        init();
        assert canvas.pixelSize() == 5;
        cleanup();
        return true;
    }

    public boolean ikonTest() {
        init();
        assert canvas.ikon() == ike;
        return true;
    }

    void checkIkonColours( Color[][] expected ) {
        for (int row = 0; row < expected.length; row++) {
            for (int column = 0; column < expected[row].length; column++) {
                assert expected[row][column].equals( ike.colourAt( row, column ) ) : "Expected: " + expected[row][column] + ", but got: " + ike.colourAt( row, column );
            }
        }
    }

    void checkIkonCanvas( final IkonCanvas ic ) {
        //For each pixel in the ikon, check that the square
        //representing it in the canvas is correctly coloured.
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                try {
                    return isOk(ic );
                } catch (Exception e) {
                    e.printStackTrace( );
                    return false;
                }
            }
        }, 2000 );
        assert isOk( ic );
    }
     private boolean isOk(IkonCanvas ic) {
         Point screenLocation = UI.getScreenLocation( ic.component() );
        for (int ikonRow = 0; ikonRow < ic.ikon().height(); ikonRow++) {
            for (int ikonCol = 0;
                 ikonCol < ic.ikon().width(); ikonCol++) {
                Color pixelColour = ic.ikon().colourAt( ikonRow, ikonCol );
                int topLeftX = screenLocation.x + ikonCol * ic.pixelSize();
                int topLeftY = screenLocation.y + ikonRow * ic.pixelSize();
                //Check that each pixel in the part of the ikon canvas
                //component  that is representing this pixel
                //is coloured correctly.
                for (int repRow = 0; repRow < ic.pixelSize(); repRow++) {
                    for (int repCol = 0;
                         repCol < ic.pixelSize(); repCol++) {
                            robot.checkPixel( topLeftX + repCol,
                                    topLeftY + repRow, pixelColour );
                    }
                }
            }
        }
         return true;
    }

    private void init() {
        init( 8, 6, Color.MAGENTA );
    }

    private void init( int width, int height, Color bg ) {
        ike = new Ikon( width, height, bg, IkonTest.in( "TestCanvas" ) );
        final IkonCanvas.ColourSupplier colourSupplier = new IkonCanvas.ColourSupplier() {
            public Color currentColour() {
                return colourToSupply;
            }

            public void setCurrentColour( Color c ) {

            }
        };
        UI.runInEventThread( new Runnable() {
            public void run() {
                canvas = new IkonCanvas( colourSupplier, 5, ike, true, "TestCanvas", null );
            }
        } );
    }

    private void guiInit() {
        guiInit( 8, 6, Color.MAGENTA );
    }

    private void guiInit( int width, int height, Color bg ) {
        init( width, height, bg );
        Runnable creater = new Runnable() {
            public void run() {
                frame = new JFrame( "IkonCanvasTest" );
                frame.setLayout( new BoxLayout( frame.getContentPane(), BoxLayout.X_AXIS ) );
                frame.add( canvas.component() );
                frame.pack();
            }
        };
        UI.runInEventThread( creater );
        UI.showFrame( frame );
        ui = new UIIkonCanvas( "TestCanvas", 5 );
    }

    private void cleanup() {
        UI.runInEventThread( new Runnable() {
            public void run() {
                frame.dispose();
            }
        } );
    }
}