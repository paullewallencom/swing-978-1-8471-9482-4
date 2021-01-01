package jet.util.test;

import jet.util.*;

public class US8 extends UserStrings {

    public static final String RESOURCE_FAMILY = "RESOURCE_FAMILY";

    private static US8 instance;

    public synchronized static US8 instance() {
        if (instance == null) {
            instance = new US8();
        }
        return instance;
    }

    private US8() {
    }
}
