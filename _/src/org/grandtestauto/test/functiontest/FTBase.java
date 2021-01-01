package org.grandtestauto.test.functiontest;

import org.grandtestauto.*;
import org.grandtestauto.test.*;

import java.util.*;

public abstract class FTBase implements AutoLoadTest {
    static List<String> testsRun = new LinkedList<String>();

    public static void recordTest( Class qlass ) {
        testsRun.add( qlass.getName() );
    }

    protected FTBase() {
        init();
    }

    protected void init() {
        Helpers.cleanTempDirectory();
        testsRun = new LinkedList<String>();
    }
}
