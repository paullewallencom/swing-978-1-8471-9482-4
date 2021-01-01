package jet.login;

import jet.util.UserStrings;

/**
 * Uses the resource management scheme developed
 * in Chapter 5.
 */
public class LoginUserStrings extends UserStrings {

    //Constants for message keys.
    public static final String LOGIN_EXCEPTION_MSG0 = "LOGIN_EXCEPTION_MSG0";
    public static final String ADMINISTRATION_MSG0 = "ADMINISTRATION_MSG0";
    public static final String CLINICAL_REPORTER_MSG0 = "CLINICAL_REPORTER_MSG0";
    public static final String DATA_ENTRY_AUDITOR_MSG0 = "DATA_ENTRY_AUDITOR_MSG0";
    public static final String WRONG_PASSWORD_MSG0 = "WRONG_PASSWORD_MSG0";
    public static final String UNKNOWN_USER_MSG1 = "UNKNOWN_USER_MSG1";
    public static final String INSUFFICIENT_PRIVILEGES_MSG2 = "INSUFFICIENT_PRIVILEGES_MSG2";

    //Constants for resource family keys.
    public static final String USER_NAME = "USER_NAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String OK = "OK";
    public static final String CANCEL = "CANCEL";
    public static final String ADMINISTRATOR = "ADMINISTRATOR";
    public static final String SCAN_WORKLIST = "SCAN_WORKLIST";
    public static final String VALIDATOR = "VALIDATOR";
    public static final String CLINICAL_KNOWLEDGE_BUILDER = "CLINICAL_KNOWLEDGE_BUILDER";
    public static final String AUDITOR = "AUDITOR";
    public static final String AUDITOR_KNOWLEDGE_BUILDER = "AUDITOR_KNOWLEDGE_BUILDER";

    private static LoginUserStrings instance = new LoginUserStrings();

    public static LoginUserStrings instance() {
        return instance;
    }

    private LoginUserStrings() {
    }
}