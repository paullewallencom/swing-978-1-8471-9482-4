package jet.ikonmaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Visual representation of an Ikon. Can be used as an active
 * drawing screen or just as a representation of the ikon.
 *
 * @author Tim Lavers
 */
public class IkonCanvas implements Ikon.Listener {
    public interface ColourSupplier {
        public Color currentColour();
        public void setCurrentColour( Color c );
    }

    private ColourSupplier colourSupplier;
    private JComponent ui;
    private int pixelSize;
    private Ikon ikon;
    private IkonHistory history;
    private Dimension size;
    private int c = -1;//Initially no point has been selected

    /**
     * The size in which the pixels must be represented in order for the displayed
     * canvas to be about 160 pixels as its maximum dimension.
     */
    public static int pixelSizeForOptimalDrawingSize( int widthOfIkon, int heightOfIkon ) {
        int max = Math.max( widthOfIkon, heightOfIkon );
        if (max < 10) return 20;
        if (max < 20) return 12;
        if (max < 30) return 8;
        if (max < 40) return 6;
        if (max < 50) return 5;
        if (max < 60) return 4;
        if (max < 70) return 3;
        if (max < 80) return 2;
        return 1;
    }

    public IkonCanvas( ColourSupplier colourSupplier, int pixelSize, Ikon ikon, final boolean isActive, String name, IkonHistory history ) {
        this.colourSupplier = colourSupplier;
        this.pixelSize = pixelSize;
        this.ikon = ikon;
        this.history = history;
        ui = new JComponent() {
            public final void paint( final Graphics g ) {
                //Create the pixel tiling in an off-screen buffer.
                BufferedImage bim = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );
                Graphics2D g2 = bim.createGraphics();
                setDoubleBuffered( true );
                //Paint each pixel as a square.
                for (int row = 0; row < IkonCanvas.this.ikon.height(); row++) {
                    for (int col = 0; col < IkonCanvas.this.ikon.width(); col++) {
                        g2.setColor( IkonCanvas.this.ikon.colourAt( row, col ) );
                        g2.fillRect( col * IkonCanvas.this.pixelSize, row * IkonCanvas.this.pixelSize, IkonCanvas.this.pixelSize, IkonCanvas.this.pixelSize );
                        //The currently selected pixel is in a highlighted colour, at least for an active canvas.
                        if ((col * IkonCanvas.this.ikon.width()) + row == c && isActive) {
                            Color pixelColour = g2.getColor();
                            Color highlight = new Color( (pixelColour.getRed() + 128) % 255, (pixelColour.getGreen() + 128) % 255, (pixelColour.getBlue() + 128) % 255 );
                            g2.setColor( highlight );
                            Rectangle2D rect = new Rectangle( col * IkonCanvas.this.pixelSize + 3, row * IkonCanvas.this.pixelSize + 3, IkonCanvas.this.pixelSize - 6, IkonCanvas.this.pixelSize - 6 );
                            g2.fill( rect );
                        }
                    }
                }
                g.drawImage( bim, 0, 0, null );
                //For an active canvas, draw in guide lines.
//                if (isActive) {
//                    g.setColor( Color.BLACK );
//                    g.drawArc( 0, 0, size.width - 1, size.height - 1, 0, 360 );
//                }
            }
        };
        ui.setName( name );

        //Set the component size.
        size = new Dimension( pixelSize * ikon.width(), pixelSize * ikon.height() );
        ui.setSize( size );
        ui.setMaximumSize( size );
        ui.setMinimumSize( size );
        ui.setPreferredSize( size );
        ui.setSize( size );
        ui.setFocusable( true );
        ui.setEnabled( true );

        ikon.addListener( this );

        //Exit the constructor before adding the mouse and keyboard bindings
        //if this is not an active component.
        if (!isActive) return;
        
        //Mouse listening.
        ui.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if (e.isShiftDown()) {
                    shiftPointClicked( e.getPoint() );
                } else {
                    pointClicked( e.getPoint() );
                }
            }
        } );
        ui.addMouseMotionListener( new MouseMotionAdapter() {
            public void mouseDragged( MouseEvent e ) {
                if (ui.contains( e.getPoint() )) {
                    IkonCanvas.this.mouseDragged( e );
                }
            }
        } );
    }

    public void ikonChanged( EditOperation editOperation ) {
        ui.repaint();
    }

    public JComponent component() {
        return ui;
    }

    public Ikon ikon() {
        return ikon;
    }

    public int pixelSize() {
        return pixelSize;
    }

    private void pointClicked( Point point ) {
        edited( pointToPixel( point ) );
    }

    private void shiftPointClicked( Point point ) {
        int[] xy = pointToPixel( point );
        colourSupplier.setCurrentColour( ikon.colourAt( xy[1], xy[0] ) );
    }

    private void mouseDragged( MouseEvent e ) {
        edited( pointToPixel( e.getPoint() ) );
    }

    private void edited( int[] ij ) {
        int cDash = ij[0] * ikon.width() + ij[1];
        if (c != cDash) {
            SinglePixelEditOperation speo = new SinglePixelEditOperation( ij[1], ij[0], colourSupplier.currentColour() );
            speo.apply( ikon );
            if (history != null) {
                history.ikonChanged( speo );
            }
        }
    }

    private int[] pointToPixel( Point point ) {
        return new int[]{point.x / pixelSize, point.y / pixelSize};
    }
}