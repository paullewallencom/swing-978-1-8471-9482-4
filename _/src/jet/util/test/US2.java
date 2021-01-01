package jet.util.test;

import jet.util.*;

public class US2 extends UserStrings {

    public static final String CONST1_MSG0 = "CONST2_MSG0";

    private static US2 instance;

    public synchronized static US2 instance() {
        if (instance == null) {
            instance = new US2();
        }
        return instance;
    }

    private US2() {
    }
}
