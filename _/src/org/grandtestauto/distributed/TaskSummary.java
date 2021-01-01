package org.grandtestauto.distributed;

import java.io.*;

/**
 * @author Tim Lavers
 */
public class TaskSummary implements Serializable {

    private String packageName;
    private String classpath;

    public TaskSummary( String packageName, String classpath ) {
        this.packageName = packageName;
        this.classpath = classpath.replaceAll( "\\\\", "/" );
    }

    public String packageName() {
        return packageName;
    }

    public String classpath() {
        return classpath;
    }

    @Override
    public String toString() {
        return "TaskSummary: Classpath = '" + classpath + "' package name: '" + packageName + "'.";
    }
}