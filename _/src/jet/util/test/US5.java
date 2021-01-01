package jet.util.test;

import jet.util.*;

public class US5 extends UserStrings {

    public static final String CONST1_MSG3 = "CONST1_MSG0";

    private static US5 instance;

    public synchronized static US5 instance() {
        if (instance == null) {
            instance = new US5();
        }
        return instance;
    }

    private US5() {
    }
}
