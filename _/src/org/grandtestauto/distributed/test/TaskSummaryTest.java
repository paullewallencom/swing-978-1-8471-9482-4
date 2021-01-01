package org.grandtestauto.distributed.test;

import org.grandtestauto.distributed.*;

/**
 * @author Tim Lavers
 */
public class TaskSummaryTest {

    public boolean constructorTest() {
        String cp = System.getProperty( "java.class.path" );
        cp = cp.replaceAll( "\\\\", "/" );
        TaskSummary ts = new TaskSummary( "a.b.c", cp );
        assert ts.classpath().equals( cp );
        assert ts.packageName().equals( "a.b.c" );
        assert ts.toString().equals( "TaskSummary: Classpath = '" + cp + "' package name: 'a.b.c'." );
        return true;
    }

    public boolean classpathTest() {
        //Tested in constructorTest.
        return true;
    }

    public boolean packageNameTest() {
        //Tested in constructorTest.
        return true;
    }

    public boolean toStringTest() {
        //Tested in constructorTest.
        return true;
    }
}
