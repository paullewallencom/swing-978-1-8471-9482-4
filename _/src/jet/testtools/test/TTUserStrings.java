package jet.testtools.test;

import jet.util.UserStrings;

public class TTUserStrings extends UserStrings {

    public static final String HOBBITS = "HOBBITS"; 
    public static final String BILBO = "BILBO";
    public static final String SAM = "SAM";
    public static final String FRODO = "FRODO";
    public static final String MERRY = "MERRY";
    public static final String PIPPIN = "PIPPIN";

    private static TTUserStrings instance;

    public synchronized static TTUserStrings instance() {
        if (TTUserStrings.instance == null) {
            TTUserStrings.instance = new TTUserStrings( );
        }
        return TTUserStrings.instance;
    }

    private TTUserStrings() {
    }
}
