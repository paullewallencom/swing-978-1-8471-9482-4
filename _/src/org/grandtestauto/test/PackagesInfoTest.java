package org.grandtestauto.test;

import java.io.*;
import java.util.*;

import org.grandtestauto.*;
import jet.testtools.test.org.grandtestauto.*;

/**
 * Unit test for <code>PackagesInfo</code>.
 *
 * @author Tim Lavers
 */
public class PackagesInfoTest {

    //One to test.
    private PackagesInfo<PackageInfo> pi;

    private void init() throws Exception {
        //Expand the zip archive into the temp directory.
        Helpers.cleanTempDirectory();
        File zip = new File( Grandtestauto.test1_zip );
        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        pi = new PackagesInfo<PackageInfo>( new PackagesInfo.Filter() {
            public boolean accept( String packageName ) {
                return PackagesInfo.namesPackageThatMightNeedUnitTests( packageName );
            }
        }, Helpers.tempDirectory() ){
            public PackageInfo createClassFinder( String packageName, File baseDir ) {
                return new PackageInfo( packageName, baseDir );
            }
        };
    }

    public boolean constructorTest() {
        //The constructor is exercised by init(), so just return true.
        return true;
    }

    public boolean classesRootTest() throws Exception {
        init();
        assert pi.classesRoot().equals( Helpers.tempDirectory() );
        return true;
    }

    public boolean namesPackageThatMightNeedUnitTestsTest() {
        assert PackagesInfo.namesPackageThatMightNeedUnitTests( "a1" );
        assert !PackagesInfo.namesPackageThatMightNeedUnitTests( "a1.test" );
        assert !PackagesInfo.namesPackageThatMightNeedUnitTests( "a1.test.extra" );
        assert !PackagesInfo.namesPackageThatMightNeedUnitTests( "a1.functiontest" );
        assert !PackagesInfo.namesPackageThatMightNeedUnitTests( "a1.functiontest.junk" );
        assert !PackagesInfo.namesPackageThatMightNeedUnitTests( "a1.loadtest" );
        assert !PackagesInfo.namesPackageThatMightNeedUnitTests( "a1.loadtest.blah" );
        return true;
    }

    public boolean namesFunctionTestPackageTest() {
        assert !PackagesInfo.namesFunctionTestPackage( "a1" );
        assert !PackagesInfo.namesFunctionTestPackage( "a1.test" );
        assert PackagesInfo.namesFunctionTestPackage( "a1.functiontest" );
        assert !PackagesInfo.namesFunctionTestPackage( "a1.loadtest" );
        return true;
    }

    public boolean namesLoadTestPackageTest() {
        assert !PackagesInfo.namesLoadTestPackage( "a1" );
        assert !PackagesInfo.namesLoadTestPackage( "a1.test" );
        assert !PackagesInfo.namesLoadTestPackage( "a1.functiontest" );
        assert PackagesInfo.namesLoadTestPackage( "a1.loadtest" );
        return true;
    }

    public boolean testablePackageNamesTest() throws Exception {
        init();
        Set s = pi.testablePackageNames();
        assert s.size() == 6;
        assert s.contains( "a1" );
        assert s.contains( "a1.b" );
        assert s.contains( "a1.b.e" );
        assert s.contains( "a1.b.e.g" );
        assert s.contains( "a1.c.h" );
        assert s.contains( "a1.d" );
        return true;
    }

    public boolean packageInfoTest() throws Exception {
        init();
        PackageInfo info = pi.packageInfo( "a1" );
        Iterator<String> itor = info.classNameToTestability().keySet().iterator();
        assert itor.next().equals( "X" );
        assert info.classNameToTestability().get( "X" ).equals( Testability.TEST_REQUIRED );
        assert itor.next().equals( "Y" );
        assert info.classNameToTestability().get( "Y" ).equals( Testability.TEST_REQUIRED );
        assert !itor.hasNext();

        info = pi.packageInfo( "a1.b.e.g" );
        itor = info.classNameToTestability().keySet().iterator();
        assert itor.next().equals( "UnitTester" );
        assert info.classNameToTestability().get( "UnitTester" ).equals( Testability.TEST_REQUIRED );
        assert itor.next().equals( "X" );
        assert info.classNameToTestability().get( "X" ).equals( Testability.TEST_REQUIRED );
        assert !itor.hasNext();
        return true;
    }
}