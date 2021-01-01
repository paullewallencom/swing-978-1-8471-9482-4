package org.grandtestauto.test.loadtest;

import java.io.*;

public class WriteUnitTesterClass extends WriteClass {

    public WriteUnitTesterClass( String packageName, File srcDir ) {
        super( packageName, srcDir, "UnitTester");
    }

    String extendsClause() {
        return "extends CoverageUnitTester";
    }

    String imports() {
        return "import org.grandtestauto.*;\n" +
                "import org.grandtestauto.test.*;";
    }

    String constructors() {
        return "    public UnitTester( GrandTestAuto gta ) {\n" +
                "        super( gta );\n" +
                "    }";
    }

}
