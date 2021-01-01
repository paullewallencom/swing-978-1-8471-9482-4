package jet.util.test;

import jet.util.*;

public class DummyUserStrings extends UserStrings {

    public static final String PLAIN_MESSAGE_MSG0 = "PLAIN_MESSAGE_MSG0";
    public static final String ONE_PLACE_MESSAGE_MSG1 = "ONE_PLACE_MESSAGE_MSG1";
    public static final String TWO_PLACE_MESSAGE_MSG2 = "TWO_PLACE_MESSAGE_MSG2";
    public static final String THREE_PLACE_MESSAGE_MSG3 = "THREE_PLACE_MESSAGE_MSG3";
    public static final String FOUR_PLACE_MESSAGE_MSG4 = "FOUR_PLACE_MESSAGE_MSG4";

    public static final String FULL_RESOURCE_FAMILY = "FULL_RESOURCE_FAMILY";
    public static final String NO_ICON_RESOURCE_FAMILY = "NO_ICON_RESOURCE_FAMILY";
    public static final String NO_TOOLTIP_RESOURCE_FAMILY = "NO_TOOLTIP_RESOURCE_FAMILY";
    public static final String NO_ACCELERATOR_RESOURCE_FAMILY = "NO_ACCELERATOR_RESOURCE_FAMILY";
    public static final String NO_MNEMONIC_RESOURCE_FAMILY = "NO_MNEMONIC_RESOURCE_FAMILY";
    public static final String EMPTY_RESOURCE_FAMILY = "EMPTY_RESOURCE_FAMILY";

    public static final String LABEL_ONLY_RESOURCE_FAMILY = "LABEL_ONLY_RESOURCE_FAMILY";
    public static final String ICON_ONLY_RESOURCE_FAMILY = "ICON_ONLY_RESOURCE_FAMILY";
    public static final String TOOLTIP_ONLY_RESOURCE_FAMILY = "TOOLTIP_ONLY_RESOURCE_FAMILY";
    public static final String ACCELERATOR_ONLY_RESOURCE_FAMILY = "ACCELERATOR_ONLY_RESOURCE_FAMILY";
    public static final String MNEMONIC_ONLY_RESOURCE_FAMILY = "MNEMONIC_ONLY_RESOURCE_FAMILY";

    private static DummyUserStrings instance;

    public synchronized static DummyUserStrings instance() {
        if (DummyUserStrings.instance == null) {
            DummyUserStrings.instance = new DummyUserStrings();
        }
        return DummyUserStrings.instance;
    }

    private DummyUserStrings() {
    }
}
