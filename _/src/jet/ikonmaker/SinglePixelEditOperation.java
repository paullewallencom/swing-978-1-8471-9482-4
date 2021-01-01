package jet.ikonmaker;

import java.awt.*;

/**
 * Represents a change to a single pixel in a bitmap.
 * 
 * @author Tim Lavers
 */
public class SinglePixelEditOperation extends EditOperation {
    private int row;
    private int column;
    private Color colour;

    public SinglePixelEditOperation( int row, int column, Color colour ) {
        this.row = row;
        this.column = column;
        this.colour = colour;
    }

    protected SinglePixelEditOperation( String[] serialisationComponents ) {
        //Ignore the first component, which is simply the letter 'S'.
        int[] components = new int[serialisationComponents.length - 1];
        for (int i = 1; i < serialisationComponents.length; i++) {
            components[i-1] = Integer.parseInt( serialisationComponents[i] );
        }
        row = components[0];
        column = components[1];
        colour = new Color( components[2], components[3], components[4], components[5] );
    }

    public int row() {
        return row;
    }

    public int column() {
        return column;
    }

    public Color colour() {
        return colour;
    }

    public void apply( Ikon ikon ) {
        ikon.setColourAt( colour, row, column );
    }

    public boolean equals( Object o ) {
        if (o == null) return false;
        if (!(o instanceof SinglePixelEditOperation)) return false;
        SinglePixelEditOperation eo = (SinglePixelEditOperation) o;
        return eo.row == row && eo.column == column && eo.colour.equals( colour );
    }

    public int hashCode() {
        return 37 * row + 37 * 37 * column + colour.hashCode();
    }

    public String stringSerialise() {
        StringBuilder sb = new StringBuilder();
        sb.append( 'S' );
        sb.append( ':' );
        sb.append( row );
        sb.append( ':' );
        sb.append( column );
        sb.append( ':' );
        sb.append( colour.getRed() );
        sb.append( ':' );
        sb.append( colour.getGreen() );
        sb.append( ':' );
        sb.append( colour.getBlue() );
        sb.append( ':' );
        sb.append( colour.getAlpha() );
        return sb.toString();
    }
}
