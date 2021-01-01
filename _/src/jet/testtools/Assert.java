package jet.testtools;

/**
 * A simple assertion facility used as an example
 * in Chapter 12. Has a useful <code>equal()</code>
 * method that prints out the classes of
 * objects that are not equal but are indistinguishable
 * by their <code>toString()</code> values.
 */
public final class Assert {

    /**
     * Private constructor to prevent instantiation.
     */
    private Assert() {
    }

    public static void equal( Object o1, Object o2 ) {
        if (!o1.equals( o2 )) {
            System.out.println( "1st Object: '" + o1 + "'" );
            System.out.println( "2nd Object: '" + o2 + "'" );
            //If they are not equal but their strings are,
            //print out their classes, if different.
            //This prevents the confusion that can
            //arise when two apparently identical
            //objects are said to differ.
            if (("" + o1).equals( "" + o2 )) {
                if (!o1.getClass().equals( o2.getClass() )) {
                    System.out.println( "Class of o1: " +
                            o1.getClass() );
                    System.out.println( "Class of o2: " +
                            o2.getClass() );
                }
            }
            throw new RuntimeException( "Objects different as above" );
        }
    }
}
