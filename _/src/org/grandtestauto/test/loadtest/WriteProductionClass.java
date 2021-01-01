package org.grandtestauto.test.loadtest;

import java.io.*;

public class WriteProductionClass extends WriteClass {
    private int methodFactor;

    public WriteProductionClass( String packageName, File srcDir, String relativeName, int methodFactor ) {
        super( packageName, srcDir, relativeName );
        this.methodFactor = methodFactor;
    }

    String implementsClause() {
        return "";
    }

    void writePrivateMethods( BufferedWriter bw ) throws IOException {
        for (int i=0; i<methodFactor; i++) {
            bw.newLine();
            bw.newLine();
            bw.write( "\tprivate void privateMethod" + i + "() {"  );
            bw.newLine();
            bw.write( "\t}"  );
        }
    }

    void writePackageMethods( BufferedWriter bw ) throws IOException {
        for (int i=0; i<methodFactor; i++) {
            bw.newLine();
            bw.newLine();
            bw.write( "\tString packageMethod" + i + "() {"  );
            bw.newLine();
            bw.write( "\t\treturn \"dfsfs\";"  );
            bw.newLine();
            bw.write( "\t}"  );
        }
    }

    void writeProtectedMethods( BufferedWriter bw ) throws IOException {
        for (int i=0; i<methodFactor; i++) {
            bw.newLine();
            bw.newLine();
            bw.write( "\tprotected int protectedMethod" + i + "() {"  );
            bw.newLine();
            bw.write( "\t\treturn 8;");
            bw.newLine();
            bw.write( "\t}"  );
        }
    }

    void writePublicMethods( BufferedWriter bw ) throws IOException {
        for (int i=0; i<methodFactor; i++) {
            bw.newLine();
            bw.newLine();
            bw.write( "\tpublic void publicMethod" + i + "() {"  );
            bw.newLine();
            bw.write( "\t}"  );
        }
    }
}
