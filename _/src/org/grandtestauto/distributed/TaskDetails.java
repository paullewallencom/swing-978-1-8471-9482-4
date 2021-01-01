package org.grandtestauto.distributed;

import org.grandtestauto.*;

import java.io.*;

/**
 * @author Tim Lavers
 */
public class TaskDetails implements Serializable, Comparable<TaskDetails> {

    private String packageName;
    private String classpath;
    private Grade grade;

    public TaskDetails( String packageName, String classpath ) {
        this.packageName = packageName;
        this.classpath = classpath.replaceAll( "\\\\", "/" );
        //Get the suppliedGrade. If there is a class "GTAGrade", use it.
        //Otherwise, if it's a unit test package and the UnitTester
        //class implements Grade, use it,otherwise use a default suppliedGrade.
        String testPackageName;
        if (PackagesInfo.namesFunctionTestPackage( packageName ) ||
                PackagesInfo.namesLoadTestPackage( packageName )) {
            testPackageName = packageName;
        } else {
            testPackageName = packageName + ".test";
        }
        //Get the grade. If there is a suitable GTAGrade defined, then it has priority.
        try {
            Class<?> gradeClass = Class.forName( testPackageName + ".GTAGrade" );
            grade = (Grade) gradeClass.newInstance();
        } catch (ClassNotFoundException e) {
            //This is OK, it has simply not been defined.
        } catch (Exception e) {
            //This is probably bad. The user has meant there to be a GTAGrade,
            //but has made an error defining it.
            e.printStackTrace();
        }
        if (grade == null) {
            //If there was no GTAGrade, look for a grade annotation in the UnitTester (if there is one).
            try {
                Class<?> utClass = Class.forName( testPackageName + ".UnitTester" );
                SimpleGrade sg = utClass.getAnnotation( SimpleGrade.class );
                if (sg != null) {
                    grade = new DefaultGrade( sg.grade() );
                }
            } catch (Exception e) {
                //This was only ever a possibility.
            }
        }
        //If there is still no grade, use the default.
        if (grade == null) {
            grade = new DefaultGrade( 0 );
        }
    }

    public Grade grade() {
        return grade;
    }

    public String packageName() {
        return packageName;
    }

    public String classpath() {
        return classpath;
    }

    public boolean canRunTests( AgentDetails agentDetails ) {
        return grade.grade() <= agentDetails.maximumGrade() && grade.compatible( agentDetails );
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskDetails that = (TaskDetails) o;

        if (!packageName.equals( that.packageName )) return false;

        return true;
    }

    public int hashCode() {
        return packageName.hashCode();
    }

    public int compareTo( TaskDetails o ) {
        int result = o.grade.grade() - grade.grade();
        if (result == 0) {
            return packageName.compareTo( o.packageName() );
        }
        return result;
    }

    @Override
    public String toString() {
        return "TaskDetails: Classpath = '" + classpath + "' package name: '" + packageName + "'. Grade: " + grade.grade() +".";
    }
}