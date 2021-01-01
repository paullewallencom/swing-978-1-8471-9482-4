package org.grandtestauto.test.loadtest;

import java.io.*;

public class WriteAutoLoadTestClass extends WriteClass {

    public WriteAutoLoadTestClass( String packageName, File srcDir, String relativeName) {
        super( packageName, srcDir, relativeName );
    }

    String implementsClause() {
        return "implements org.grandtestauto.AutoLoadTest";
    }

    void writePublicMethods( BufferedWriter bw ) throws IOException {
        bw.write( "public boolean runTest() throws Exception {");
        bw.newLine();
        bw.write( "\t\treturn true;");
        bw.newLine();
        bw.write( "\t}"  );
    }
}
