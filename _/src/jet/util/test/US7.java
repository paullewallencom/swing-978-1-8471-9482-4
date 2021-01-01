package jet.util.test;

import jet.util.*;

public class US7 extends UserStrings {

    public static final String RESOURCE_FAMILY = "RESOURCE_FAMILY";

    private static US7 instance;

    public synchronized static US7 instance() {
        if (instance == null) {
            instance = new US7();
        }
        return instance;
    }

    private US7() {
    }
}
