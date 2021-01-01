package jet.util.test;

import jet.util.*;

public class US4 extends UserStrings {

    public static final String CONST1_MSG0 = "CONST1_MSG0";

    private static US4 instance;

    public synchronized static US4 instance() {
        if (instance == null) {
            instance = new US4();
        }
        return instance;
    }

    private US4() {
    }
}
