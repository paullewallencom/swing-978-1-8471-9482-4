package jet.user.test;

import org.grandtestauto.*;

/**
 * For the automatic running of unit tests using GrandTestAuto.
 */
public final class UnitTester extends CoverageUnitTester {

    public static void main( final String[] args ) {
        new jet.util.test.UnitTester( args[0] ).runTests();
    }

    public UnitTester( final GrandTestAuto gta ) {
        super( gta );
    }

    public UnitTester( final String classesRoot ) {
        super( classesRoot );
    }
}