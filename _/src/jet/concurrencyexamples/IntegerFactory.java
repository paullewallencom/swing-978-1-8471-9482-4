package jet.concurrencyexamples;

/**
 * Used as an example in Chapter 12.
 */
public class IntegerFactory {
    private static Integer current = -1;

    public static synchronized Integer next() {
        return ++current;
    }

    //Private constructor as we don't want this instantiated.
    private IntegerFactory() {
    }
}
