package jet.testtools;

/**
 * Convenient access to some oft-used constants.
 */
public class StringUtils {

    private StringUtils() {}

    /**
     * The line-feed char .
     */
    public static String LF = "" + ((char) 10);

    /**
     * The carriage-return char.
     */
    public static String CR = "" + ((char) 13);

    /**
     * The carriage-return char followed by the line-feed char.
     */
    public static String CRLF = CR + LF;

    /**
     * The system-specific newline.
     */
    public static String NL = System.getProperty( "line.separator" );
}

