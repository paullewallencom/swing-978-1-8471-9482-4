package org.grandtestauto.test.functiontest;

import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.util.*;
import java.io.*;

import jet.testtools.test.org.grandtestauto.*;

/**
 * See the GrandTestAuto test specification.
 */
public class PackageNameAbbreviation extends FTBase {

    public boolean runTest() {
        testsRun = new LinkedList<String>();
        Set<String> expectedTestsRun = new HashSet<String>();
        expectedTestsRun.add( "a85.mammals.placental.test.UnitTester" );
        expectedTestsRun.add( "a85.reptiles.lizards.test.UnitTester" );
        expectedTestsRun.add( "a85.reptiles.snakes.test.UnitTester" );
        GrandTestAuto gta = Helpers.setupForZip( new File( Grandtestauto.test85_zip ), true, true, true, "a.m.p", "", "", false, false, "", true, null, null,null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 3;
        assert testsRun.containsAll( expectedTestsRun );

        testsRun = new LinkedList<String>();
        expectedTestsRun = new HashSet<String>();
        expectedTestsRun.add( "a85.mammals.placental.test.UnitTester" );
        expectedTestsRun.add( "a85.reptiles.lizards.test.UnitTester" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test85_zip ), true, true, true, "a.m.p", "a.r.l", "", false, false, "", true, null, null,null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 2;
        assert testsRun.containsAll( expectedTestsRun );

        testsRun = new LinkedList<String>();
        expectedTestsRun = new HashSet<String>();
        expectedTestsRun.add( "a85.birds.rattites.test.UnitTester" );
        expectedTestsRun.add( "a85.birds.songbirds.test.UnitTester" );
        expectedTestsRun.add( "a85.mammals.marsupials.test.UnitTester" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test85_zip ), true, true, true, null, "a.m.m", "", false, false, "", true, null, null,null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 3;
        assert testsRun.containsAll( expectedTestsRun );

        testsRun = new LinkedList<String>();
        expectedTestsRun = new HashSet<String>();
        expectedTestsRun.add( "a85.mammals.monotremes.test.UnitTester" );
        gta = Helpers.setupForZip( new File( Grandtestauto.test85_zip ), true, true, true, "a.b.r", "a.r.s", "a.m.mo", false, false, "", true, null, null,null, null, null, null );
        gta.runAllTests();
        assert testsRun.size() == 1;
        assert testsRun.containsAll( expectedTestsRun );

        return true;
    }
}
