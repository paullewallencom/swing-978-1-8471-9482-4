package org.grandtestauto.test.loadtest;

import java.io.*;

/**
 * Writes the src code for a class.
 */
public abstract class WriteClass {
    private String packageName;
    private File srcDir;
    private String relativeName;

    public WriteClass( String packageName, File srcDir, String relativeName ) {
        this.packageName = packageName;
        this.srcDir = srcDir;
        this.relativeName = relativeName;
    }

    public void doIt() throws IOException {
        File srcFile = new  File( srcDir, relativeName + ".java");
        BufferedWriter bw = new BufferedWriter(  new FileWriter( srcFile ) );
        //Package declaration.
        bw.write( "package " + packageName + ";" );
        bw.newLine();
        bw.newLine();

        //imports.
        bw.write( imports() );
        bw.newLine();
        bw.newLine();

        //Name of class.
        bw.write( "public class " + relativeName + " "  + extendsClause() + " " + implementsClause() + " {");
        bw.newLine();
        bw.newLine();

        //Constructor.
        bw.write( constructors() );
        bw.newLine();
        bw.newLine();

        writePublicMethods( bw );
        writeProtectedMethods( bw );
        writePackageMethods( bw );
        writePrivateMethods( bw );

        //End of class.
        bw.newLine();
        bw.write( "}"  );

        bw.close();
    }

    String implementsClause() {
        return "";
    }

    String extendsClause() {
        return "";
    }

    String imports() {
        return "";
    }

    String constructors() {
        return "";
    }

    void writePrivateMethods( BufferedWriter bw ) throws IOException{}

    void writePackageMethods( BufferedWriter bw ) throws IOException {};

    void writeProtectedMethods( BufferedWriter bw ) throws IOException {};
    void writePublicMethods( BufferedWriter bw ) throws IOException {};
}
