package org.grandtestauto;

/**
 * @author Tim Lavers
 */
public interface DoPackageWork {
    boolean doUnitTests( String packageName );
    boolean runAutoLoadTestPackage( boolean areFunctionTests, PackagesInfo<AutoLoadTestFinder> testPackageFinder, String testPackageName );
    boolean verbose();
}
