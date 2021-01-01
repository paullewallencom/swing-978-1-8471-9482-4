package org.grandtestauto.test;

import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.*;

import java.io.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class ProjectAnalyserTest {

    public boolean constructorTest() {
        return true;
    }

    public boolean functionTestPackagesTest() throws Exception {
        ProjectAnalyser pa = Helpers.setupProjectAnalyser( new File( Grandtestauto.test35_zip ) );
        Collection<String> got = pa.functionTestPackages();
        assert got.size() == 3;
        assert got.contains( "a35.b.functiontest");
        assert got.contains( "a35.c.functiontest");
        assert got.contains( "a35.d.test.functiontest");
        return true;
    }

    public boolean loadTestPackagesTest() throws Exception {
        ProjectAnalyser pa = Helpers.setupProjectAnalyser( new File( Grandtestauto.test37_zip ) );
        Collection<String> got = pa.loadTestPackages();
        assert got.size() == 3;
        assert got.contains( "a37.b.loadtest");
        assert got.contains( "a37.c.loadtest");
        assert got.contains( "a37.d.test.loadtest");
        return true;
    }

    public boolean unitTestPackagesTest() throws Exception {
        ProjectAnalyser pa = Helpers.setupProjectAnalyser( new File( Grandtestauto.test1_zip ) );
        Collection<String> got = pa.unitTestPackages();
        assert got.size() == 5: "Got: " + got;
        assert got.contains( "a1.b.e.g") : "Got: " + got;
        assert got.contains( "a1.b");
        assert got.contains( "a1.c.h");
        assert got.contains( "a1.d");
        assert got.contains( "a1");
        return true;
    }
}