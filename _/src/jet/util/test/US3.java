package jet.util.test;

import jet.util.*;

public class US3 extends UserStrings {

    public static final String CONST1_MSG0 = "CONST1_MSG0";

    private static US3 instance;

    public synchronized static US3 instance() {
        if (instance == null) {
            instance = new US3();
        }
        return instance;
    }

    private US3() {
    }
}
