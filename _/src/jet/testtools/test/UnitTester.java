package jet.testtools.test;

import org.grandtestauto.*;
import org.grandtestauto.distributed.*;

/**
 * For the automatic running of unit tests using GrandTestAuto.
 */
@SimpleGrade( grade = 5 )
public final class UnitTester extends CoverageUnitTester {

    public static void main( final String[] args ) {
        new jet.testtools.test.UnitTester( args[0] ).runTests();
    }

    public UnitTester( final GrandTestAuto gta ) {
        super( gta );
    }

    public UnitTester( final String classesRoot ) {
        super( classesRoot );
    }
}
