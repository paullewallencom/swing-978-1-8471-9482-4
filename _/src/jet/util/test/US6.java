package jet.util.test;

import jet.util.*;

public class US6 extends UserStrings {

    public static final String RESOURCE_FAMILY = "RESOURCE_FAMILY";

    private static US6 instance;

    public synchronized static US6 instance() {
        if (instance == null) {
            instance = new US6();
        }
        return instance;
    }

    private US6() {
    }
}
