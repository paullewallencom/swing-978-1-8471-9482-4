package jet.util.test;

import jet.util.*;

public class ResourcesTesterTest {

    public boolean testAllTest() throws Exception {
        //Interface constant not a key.
        check( US1.instance() );

        //Interface constant name is not its value.
        check( US2.instance() );

        //Key not represented by interface constant.
        check( US3.instance() );

        //Wrong number of placeholders in message.
        check( US4.instance() );
        check( US5.instance() );

        //Mnemonic not in label.
        check( US6.instance() );

        //Icon not a real image.
        check( US7.instance() );

        //Accelerator not a valid key.
        check( US8.instance() );

        //All ok.
        ResourcesTester.testAll( DummyUserStrings.instance() );
        return true;
    }

    private void check(UserStrings us) {
        boolean gotError= false;
        try {
            ResourcesTester.testAll( us );
        } catch (Throwable e) {
            gotError=true;
        }
        assert gotError : "Should have got error.";
    }
}
