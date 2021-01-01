package org.grandtestauto;

import java.lang.reflect.*;

/**
 * @author Tim Lavers
 */
abstract class DPWImpl implements DoPackageWork {
    protected GrandTestAuto gta;

    public boolean doUnitTests( String packageName ) {
        String utClassName = packageName + ".test.UnitTester";
        boolean packageResult = false;
        //Try to create and invoke the UnitTester.
        try {
            Class<?> utClass = Class.forName( utClassName );
            int mods = utClass.getModifiers();
            if (!Modifier.isPublic( mods )) {
                packageResult = false;
                gta.testingError( Messages.message( Messages.OPK_UNIT_TESTER_NOT_PUBLIC, packageName ), null );
                return false;
            }
            if (Modifier.isAbstract( mods )) {
                packageResult = false;
                gta.testingError( Messages.message( Messages.OPK_UNIT_TESTER_ABSTRACT, packageName ), null );
                return false;
            }
            UnitTesterIF ut;
            if (UnitTesterIF.class.isAssignableFrom( utClass )) {
                Constructor<?> ct;
                try {
                    ct = utClass.getConstructor( gta.getClass() );
                    ut = (UnitTesterIF) ct.newInstance( gta );
                } catch (NoSuchMethodException e) {
                    //Don't worry, try a no-args constructor.
                    try {
                        ct = utClass.getConstructor();
                        ut = (UnitTesterIF) ct.newInstance();
                    } catch (NoSuchMethodException nsme) {
                        //The UnitTester could not be created.
                        packageResult = false;
                        gta.testingError( Messages.message( Messages.OPK_UNIT_TESTER_DOES_NOT_HAVE_REQURIED_CONSTRUCTOR, packageName ), null );
                        return false;
                    }
                }
                packageResult = invokeRun( packageName, ut );
            }
        } catch (ClassNotFoundException cnfe) {
            //The package has no unit tester.
            gta.nonUnitTestedPackageNames().add( packageName );
        } catch (InstantiationException ie) {
            //The UnitTester could not be created.
            packageResult = false;
            gta.testingError( Messages.message( Messages.OPK_COULD_NOT_CREATE_UNIT_TESTER, packageName ), null );
        } catch (IllegalAccessException iae) {
            //The UnitTester could not be created.
            packageResult = false;
            gta.testingError( Messages.message( Messages.OPK_COULD_NOT_CREATE_UNIT_TESTER, packageName ), null );
        } catch (ClassCastException cce) {
            //The UnitTester is not of the correct type.
            packageResult = false;
            gta.testingError( Messages.message( Messages.OPK_UNIT_TESTER_NOT_UNITTESTERIF, packageName ), null );
        } catch (InvocationTargetException ite) {
            //This means that the test failed.
            gta.resultsLogger().log( Messages.message( Messages.SK_TEST_FAILED_DUE_TO_EXCEPTION ), ite );
            packageResult = false;
        }
        if (verbose()) {
            gta.reportUTResult( packageName, packageResult );
        }
        return packageResult;
    }

    public boolean verbose() {
        return true;
    }

    abstract Boolean invokeRun( String packageName, UnitTesterIF ut );

    void setGTA( GrandTestAuto gta ) {
        this.gta = gta;
    }
}
