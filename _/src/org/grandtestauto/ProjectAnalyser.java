package org.grandtestauto;

import java.io.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class ProjectAnalyser {
    private List<String> unitTestPackages = new LinkedList<String>();
    private List<String> functionTestPackages = new LinkedList<String>();
    private List<String> loadTestPackages = new LinkedList<String>();

    public ProjectAnalyser( Settings settings ) throws IOException {
        PackageLister packageLister = new PackageLister();
        GrandTestAuto gta = new GrandTestAuto( settings, packageLister );
        gta.resultsLogger().log( "Analysing classes...", null );
        packageLister.setGTA( gta );
        gta.runAllTests();
        gta.resultsLogger().log( "Analysis complete...starting distributor..", null );
    }

    public Collection<String> unitTestPackages() {
        return unitTestPackages;
    }

    public Collection<String> functionTestPackages() {
        return functionTestPackages;
    }

    public List<String> loadTestPackages() {
        return loadTestPackages;
    }

    private class PackageLister extends DPWImpl {
        Boolean invokeRun( String packageName, UnitTesterIF ut ) {
            if (ut != null) {
                unitTestPackages.add( packageName );
            }
            return true;
        }

        public boolean runAutoLoadTestPackage( boolean areFunctionTests, PackagesInfo<AutoLoadTestFinder> testPackageFinder, String testPackageName ) {
            if (areFunctionTests) {
                functionTestPackages.add( testPackageName );
            } else {
                loadTestPackages.add( testPackageName );
            }
            return true;
        }

        @Override
        public boolean verbose() {
            return false;
        }
    }
}