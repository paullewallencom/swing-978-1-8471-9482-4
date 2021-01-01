package jet.ikonmaker;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.LinkedList;

/**
 * Model for a bitmap.
 *
 * @author Tim Lavers
 */
public class Ikon implements Comparable<Ikon> {

    public interface Listener {
        void ikonChanged( EditOperation editOperation );
    }

    private int width;
    private int height;
    private IkonName name;
    private Color[][] data;
    private LinkedList<Listener> listeners;

    public Ikon( int width, int height, Color backgroundColour, IkonName name ) {
        this.name = name;
        this.width = width;
        this.height = height;
        data = new Color[height][width];//Number of rows = height, number of columns = width.
        for (int row = 0; row < data.length; row++) {//For each row.
            Color[] colors = data[row];
            for (int column = 0; column < colors.length; column++) {//For each column.
                data[row][column] = backgroundColour;
            }
        }
        listeners = new LinkedList<Listener>();
    }

    private Ikon( Color[][] data, IkonName name ) {
        this.name = name;
        this.width = data[0].length;
        this.height = data.length;
        this.data = data;
        listeners = new LinkedList<Listener>();
    }

    public Ikon copy( IkonName newName ) {
        Color[][] newData = new Color[height][width];
        for (int row = 0; row < newData.length; row++) {//For each row.
            Color[] colors = newData[row];
            for (int column = 0; column < colors.length; column++) {//For each column.
                newData[row][column] = data[row][column];
            }
        }
        return new Ikon( newData, newName );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public IkonName name() {
        return name;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Color colourAt( int row, int column ) {
        return data[row][column];
    }

    public BufferedImage asImage() {
        BufferedImage result = new BufferedImage( data[0].length, data.length, BufferedImage.TYPE_INT_ARGB );
        for (int row = 0; row < data.length; row++) {//For each row
            for (int column = 0; column < data[row].length; column++) {//For each element of the row.
                result.setRGB( column, row, data[row][column].getRGB() );
            }
        }
        return result;
    }

    public void setColourAt( Color colour, int row, int column ) {
        data[row][column] = colour;
        for (Listener listener : listeners) listener.ikonChanged( new SinglePixelEditOperation( row, column, colour ) );
    }

    public int compareTo( Ikon ikon ) {
        return name.compareTo( ikon.name() );
    }

    public void fillWithImage( BufferedImage bim ) {
        //Work out the scale factors.
        int imWidth = bim.getWidth();
        int imHeight = bim.getHeight();
        float widthScale = ((float) width) / ((float) imWidth);
        float heightScale = ((float) height) / ((float) imHeight);
        //Build the scaling op.
        AffineTransform transform = new AffineTransform( widthScale, 0, 0, heightScale, 0, 0 );
        AffineTransformOp resize = new AffineTransformOp( transform, AffineTransformOp.TYPE_BILINEAR );
        //Get a destination image and apply the op.
        BufferedImage transformed = resize.createCompatibleDestImage( bim, bim.getColorModel() );
        try {
            resize.filter( bim, transformed );
        } catch (ImagingOpException e) {
            transformed = transformTheHardWay( bim, widthScale, heightScale );
        }
        //Get the alpha channel, if there is one.
        boolean hasAlphaRaster = transformed.getAlphaRaster() != null;
        //Use the new image to set the colours.
        for (int row = 0; row < data.length; row++) {//For each row
            for (int column = 0; column < data[row].length; column++) {//For each element of the row.
                setColourAt( new Color( transformed.getRGB( column, row ), hasAlphaRaster ), row, column );
            }
        }
    }

    private BufferedImage transformTheHardWay( BufferedImage source, float widthScale, float heightScale ) {
        if (widthScale > 1 || heightScale > 1) {
            throw new IllegalArgumentException( "Unable to enlarge image." );
        }
        BufferedImage transformed = new BufferedImage( width,  height, BufferedImage.TYPE_INT_ARGB );
        int numSrcPixelsAcrossPerImagePixel = Math.round( 1 / widthScale );
        int numSrcPixelsDownPerImagePixel = Math.round( 1 / heightScale );
        for (int col=0; col<width; col++) {
            for (int row=0; row<height; row++) {
                int[] rgba = new int[4];
                for (int i=0; i< numSrcPixelsAcrossPerImagePixel; i++ ) {
                    for (int j=0; j<numSrcPixelsDownPerImagePixel; j++) {
                        int x = numSrcPixelsAcrossPerImagePixel * col + i;
                        int y = numSrcPixelsAcrossPerImagePixel * row + j;
                        int rgbaSrc = source.getRGB( x, y );
                        rgba[0] += (rgbaSrc >> 16) & 0xff;
                        rgba[1] += (rgbaSrc >> 8) & 0xff;
                        rgba[2] += rgbaSrc & 0xff;
                        rgba[3] += (rgbaSrc >> 24) & 0xff;
                    }
                }
                int totalSrcPixelsPerImagePixel = numSrcPixelsAcrossPerImagePixel * numSrcPixelsDownPerImagePixel;
                for (int i = 0; i < rgba.length; i++) {
                    rgba[i] = rgba[i] / totalSrcPixelsPerImagePixel;
                }

                transformed.setRGB( col, row, (rgba[3] << 24) | (rgba[0] << 16) | (rgba[1] << 8) | rgba[2]);
            }
        }
        return transformed;
    }

    public String toString() {
        StringBuilder result = new StringBuilder( "Ikon[" );
        result.append( name );
        result.append( ", " );
        result.append( width );
        result.append( ", " );
        result.append( height );
        result.append( "]" );
        return result.toString();
    }
}