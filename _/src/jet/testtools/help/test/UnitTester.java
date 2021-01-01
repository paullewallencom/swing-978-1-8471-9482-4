package jet.testtools.help.test;

import org.grandtestauto.*;
import org.grandtestauto.distributed.*;

@SimpleGrade( grade = 2 )
public final class UnitTester extends CoverageUnitTester {

    public UnitTester( final GrandTestAuto gta ) {
        super( gta );
    }

    public UnitTester( final String classesRoot ) {
        super( classesRoot );
    }
}