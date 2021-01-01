package jet.ikonmaker;

import java.awt.*;

/**
 * Represents a colour change to a pixel in a bitmap.
 *
 * @author Tim Lavers
 */
public class ColourChangeEditOperation extends EditOperation {

    private int deltaRed;
    private int deltaGreen;
    private int deltaBlue;

    public ColourChangeEditOperation( int deltaRed, int deltaGreen, int deltaBlue ) {
        this.deltaRed = deltaRed;
        this.deltaGreen = deltaGreen;
        this.deltaBlue = deltaBlue;
    }

    protected ColourChangeEditOperation( String[] serialisationComponents ) {
        //Ignore the first component, which is simply the letter 'C'.
        int[] components = new int[serialisationComponents.length - 1];
        for (int i = 1; i < serialisationComponents.length; i++) {
            components[i-1] = Integer.parseInt( serialisationComponents[i] );
        }
        deltaRed = components[0];
        deltaGreen = components[1];
        deltaBlue = components[2];
    }

    public String stringSerialise() {
        StringBuilder sb = new StringBuilder( "C" );
        sb.append( ":" );
        sb.append( deltaRed );
        sb.append( ":" );
        sb.append( deltaGreen );
        sb.append( ":" );
        sb.append( deltaBlue );
        return sb.toString();
    }

    public void apply( Ikon ikon ) {
        int width = ikon.width();
        int height = ikon.height();
        for (int i = 0; i< width; i++) {
            for (int j=0; j< height; j++) {
                int originalRGB = ikon.colourAt( j, i ).getRGB();
                ikon.setColourAt( new Color( transform( originalRGB ), true ), j, i);
            }
        }
    }

    public int transform( int originalRGB ) {
        int alpha = (originalRGB >> 24) & 0xff;
        int red = (originalRGB >> 16) & 0xff;
        int green = (originalRGB >> 8) & 0xff;
        int blue = (originalRGB) & 0xff;
        red = Math.max( Math.min( 255, red + deltaRed ), 0 );
        green = Math.max( Math.min( 255, green + deltaGreen ), 0 );
        blue = Math.max( Math.min( 255, blue + deltaBlue ), 0 );
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public int deltaRed() {
        return deltaRed;
    }

    public int deltaGreen() {
        return deltaGreen;
    }

    public int deltaBlue() {
        return deltaBlue;
    }
}