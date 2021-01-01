package org.grandtestauto.test.loadtest;

import java.io.*;

public class WriteUnitTestClass extends WriteClass {
    private int methodFactor;

    public WriteUnitTestClass( String packageName, File srcDir, String relativeName, int methodFactor ) {
        super( packageName, srcDir, relativeName );
        this.methodFactor = methodFactor;
    }

    void writePublicMethods( BufferedWriter bw ) throws IOException {
        bw.newLine();
        bw.newLine();
        bw.write( "\tpublic boolean constructorTest() {"  );
        bw.newLine();
        bw.write( "\t\treturn true;"  );
        bw.newLine();
        bw.write( "\t}"  );
        for (int i=0; i<methodFactor; i++) {
            bw.newLine();
            bw.newLine();
            bw.write( "\tpublic boolean publicMethod" + i + "Test() {"  );
            bw.newLine();
            bw.write( "\t\treturn true;"  );
            bw.newLine();
            bw.write( "\t}"  );
        }

        for (int i=0; i<methodFactor; i++) {
            bw.newLine();
            bw.newLine();
            bw.write( "\tpublic boolean protectedMethod" + i + "Test() {"  );
            bw.newLine();
            bw.write( "\t\treturn true;"  );
            bw.newLine();
            bw.write( "\t}"  );
        }

    }
}
