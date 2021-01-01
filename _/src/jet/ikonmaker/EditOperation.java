package jet.ikonmaker;

/**
 * General form a change to a single pixel in a bitmap.
 *
 * @author Tim Lavers
 */
public abstract class EditOperation {

    public static EditOperation deserialise( String serialised ) {
        String[] componentStrings = serialised.split( ":" );
        switch (componentStrings[0].charAt( 0 )) {
            case 'S':
                return new SinglePixelEditOperation( componentStrings );
            case 'C':
                return new ColourChangeEditOperation( componentStrings );
            default:
                throw new InternalError( "Could not deserialise: '" + serialised + "'" );
        }
    }

    public abstract String stringSerialise();

    public abstract void apply( Ikon ikon );
}
