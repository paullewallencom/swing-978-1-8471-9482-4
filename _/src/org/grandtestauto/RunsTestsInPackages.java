package org.grandtestauto;

/**
 * @author Tim Lavers
 */
class RunsTestsInPackages extends DPWImpl {

    Boolean invokeRun( String packageName, UnitTesterIF ut ) {
        Boolean runResult;
        try {
            runResult = ut.runTests();
        } catch (Throwable e) {
            String msg = Messages.message( Messages.OPK_ERROR_RUNNING_UNIT_TESTER_TEST, packageName );
            gta.resultsLogger().log( msg, null );
            runResult = false;
        }
        return runResult;
    }

    public boolean runAutoLoadTestPackage( boolean areFunctionTests, PackagesInfo<AutoLoadTestFinder> testPackageFinder, String testPackageName ) {
        String key;
        key = areFunctionTests ? Messages.OPK_RUNNING_FUNCTION_TEST_PACKAGE : Messages.OPK_RUNNING_LOAD_TEST_PACKAGE;
        gta.resultsLogger().log( Messages.message( key, testPackageName ), null );
        boolean packageResult = true;
        //Log that the tests are to be run.
        AutoLoadTestFinder cf = testPackageFinder.packageInfo( testPackageName );
        for (String testName : cf.classesInPackage()) {
            //Shortcut the tests for this package if a test has failed.
            if (!gta.continueWithTests( packageResult )) break;

            //Ignore any tests that are filtered out by package name.
            if (gta.classIsToBeSkippedBecauseOfSettings( testName )) continue;
            //Get the class. It is a AutoLoadTest.
            String fullClassName = testPackageName + "." + testName;
            try {
                //Run the test and add the result to the overall package result.
                packageResult &= GrandTestAuto.runAutoLoadTest( fullClassName, testName, gta.resultsLogger() );
            } catch (InstantiationException e) {
                String msg = Messages.message( Messages.OPK_AUTO_LOAD_TEST_DOES_NOT_HAVE_REQURIED_CONSTRUCTOR, testName );
                gta.resultsLogger().log( msg, null );
                packageResult = false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                //
                System.out.println( "XXXXX unexpected error, could not find: " + fullClassName );
            }
        }
        return packageResult;
    }
}
