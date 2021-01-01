package org.grandtestauto.test;

import org.grandtestauto.*;
import org.grandtestauto.distributed.*;

/**
 * Runs the unit tests for the gta package.
 *
 * @author Tim Lavers
 */
@SimpleGrade( grade = 4 )
public class UnitTester extends CoverageUnitTester {

    public static void main( final String[] args ) {
        boolean result = new UnitTester( args[0] ).runTests();
        System.out.println( "Unit test result: " + (result ? "passed!"  : "failed!"));
    }

    public UnitTester( GrandTestAuto gta ) {
        super( gta );
    }

    public UnitTester( final String classesRoot ) {
        super( classesRoot );
    }
}
