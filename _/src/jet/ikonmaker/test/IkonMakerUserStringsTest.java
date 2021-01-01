package jet.ikonmaker.test;

import jet.util.ResourcesTester;
import jet.ikonmaker.IkonMakerUserStrings;

public class IkonMakerUserStringsTest {
    public boolean instanceTest() throws Exception {
        ResourcesTester.testAll( IkonMakerUserStrings.instance() );
        return true;
    }
}
