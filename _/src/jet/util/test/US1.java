package jet.util.test;

import jet.util.*;

public class US1 extends UserStrings {

    public static final String CONST1_MSG0 = "CONST1_MSG0";
    public static final String CONST2_MSG0 = "CONST2_MSG0";

    private static US1 instance;

    public synchronized static US1 instance() {
        if (instance == null) {
            instance = new US1();
        }
        return instance;
    }

    private US1() {
    }
}
