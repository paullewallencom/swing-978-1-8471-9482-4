package jet.testtools.test;

import jet.testtools.*;

import java.io.*;

/**
 * @author Tim Lavers
 */
public class SerializationTesterTest {
    public boolean checkTest() {
        //Check that something that really
        //is Serializable passes.
        assert SerializationTester.check(
                new IsSerializable() );

        //Check that something that is not
        //Serializable fails. This will print an exception.
        System.out.println(
                "****** EXPECTED STACK TRACE BEGINS ******" );
        assert !SerializationTester.check(
                new NotActuallySerializable() );
        System.out.println(
                "****** EXPECTED STACK TRACE ENDS ******" );
        return true;
    }
}
class IsSerializable implements Serializable {
      private Thing thing = new Thing();

    public int value() {
        return thing.value;
    }

    private class Thing implements Serializable {
        int value;
    }
}
class NotActuallySerializable implements Serializable {

    private Thing thing = new Thing();

    public int value() {
        return thing.value;
    }

    private class Thing {
        int value;
    }
}
