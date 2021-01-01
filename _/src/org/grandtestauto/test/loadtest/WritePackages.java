package org.grandtestauto.test.loadtest;

import java.io.*;

/**
 * Writes src files for a package in a given directory, and recursively writes subpackages.
 */
public class WritePackages {

    public static void main( String[] args ) throws IOException {
        String num = "64";
        File src = new File( "C:\\personal\\TestingBook\\code\\testpackages\\test" + num + "\\src\\a" + num);
        src.mkdirs();
        System.out.println( "src: " + src.getPath() );
        WritePackages wp = new WritePackages( "a" + num, src, 3 );
        wp.doIt();
    }

    private String packageName;
    private File srcDir;
    private int depthToGo;
    private int factor = 11;

    public WritePackages( String packageName, File srcDir, int depthToGo ) {
        this.packageName = packageName;
        this.srcDir = srcDir;
        this.depthToGo = depthToGo;
    }

    public void doIt() throws IOException {
        if (depthToGo == 0) return;

        srcDir.mkdir();
        //Write out the src files for this package.
        for (int i=0; i<factor; i++) {
            WriteProductionClass wc = new WriteProductionClass( packageName, srcDir, "A" + i, 10 );
            wc.doIt();
        }

        //Write the unit tests.
        File unitTestDir = new File( srcDir, "test");
        unitTestDir.mkdir();
        String testPackageName = packageName + ".test";
        for (int i=0; i<factor; i++) {
            WriteUnitTestClass wc = new WriteUnitTestClass( testPackageName, unitTestDir, "A" + i + "Test", 10 );
            wc.doIt();
        }
        new WriteUnitTesterClass( testPackageName, unitTestDir ).doIt();

        //Produce the function and load test packages.
        File functionTestDir = new File( srcDir, "functiontest");
        functionTestDir.mkdir();
        testPackageName = packageName + ".functiontest";
        for (int i=0; i<factor; i++) {
            WriteAutoLoadTestClass wc = new WriteAutoLoadTestClass( testPackageName, functionTestDir, "A" + i );
            wc.doIt();
        }

        File loadTestDir = new File( srcDir, "loadtest");
        loadTestDir.mkdir();
        testPackageName = packageName + ".loadtest";
        for (int i=0; i<factor; i++) {
            WriteAutoLoadTestClass wc = new WriteAutoLoadTestClass( testPackageName, loadTestDir, "A" + i );
            wc.doIt();
        }
        //Produce the sub-packages.
        for (int i=0; i<factor; i++) {
            String suffix = "a" + i;
            File newSrcDir = new File( srcDir, suffix );
            String newPackageName = packageName + "." + suffix;
            WritePackages wp = new WritePackages( newPackageName,  newSrcDir, depthToGo - 1 );
            wp.doIt();
        }
    }
}
