package jet.login.test;

import jet.login.LoginUserStrings;
import jet.util.ResourcesTester;

public class LoginUserStringsTest {
    public boolean instanceTest() throws Exception {
        ResourcesTester.testAll(LoginUserStrings.instance() );
        return true;
    }
}
