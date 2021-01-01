package jet.ikonmaker;

/**
 * This class implements the design pattern for internationalisation
 * that is presented in Chapter 5.
 *
 * @author Tim Lavers
 */
public class IkonMakerUserStrings extends jet.util.UserStrings {

    //Constants for message keys.
    public static final String CHECK_OVERWRITE_IN_EXPORT_MSG0 = "CHECK_OVERWRITE_IN_EXPORT_MSG0";
    public static final String EXPORT_MSG0 = "EXPORT_MSG0";
    public static final String FRAME_TITLE_MSG0 = "FRAME_TITLE_MSG0";
    public static final String IMAGE_FILES_DESCRIPTION_MSG0 = "IMAGE_FILES_DESCRIPTION_MSG0";
    public static final String INSTRUCTIONS_WITH_IKON_MSG0 = "INSTRUCTIONS_WITH_IKON_MSG0";
    public static final String INSTRUCTIONS_WITH_NO_IKON_MSG0 = "INSTRUCTIONS_WITH_NO_IKON_MSG0";
    public static final String OPAQUE_MSG0 = "OPAQUE_MSG0";
    public static final String OPEN_IKON_MSG0 = "OPEN_IKON_MSG0";
    public static final String PNG_DESCRIPTION_MSG0 = "PNG_DESCRIPTION_MSG0";
    public static final String TRANSPARENT_MSG0 = "TRANSPARENT_MSG0";

    //Constants for resource family keys.
    public static final String BLUE = "BLUE";
    public static final String CANCEL = "CANCEL";
    public static final String EXPORT = "EXPORT";
    public static final String FILL_WITH_IMAGE = "FILL_WITH_IMAGE";
    public static final String GREEN = "GREEN";
    public static final String HEIGHT = "HEIGHT";
    public static final String NAME = "NAME";
    public static final String NEW = "NEW";
    public static final String OK = "OK";
    public static final String OPEN = "OPEN";
    public static final String RED = "RED";
    public static final String SAVE_AS = "SAVE_AS";
    public static final String TRANSPARENCY = "TRANSPARENCY";
    public static final String WIDTH = "WIDTH";

    private static IkonMakerUserStrings instance;

    public synchronized static IkonMakerUserStrings instance() {
        if (IkonMakerUserStrings.instance == null) {
            IkonMakerUserStrings.instance = new IkonMakerUserStrings();
        }
        return IkonMakerUserStrings.instance;
    }

    private IkonMakerUserStrings() {
    }
}
