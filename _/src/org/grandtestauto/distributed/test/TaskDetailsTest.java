package org.grandtestauto.distributed.test;

import jet.testtools.*;
import jet.testtools.test.org.grandtestauto.*;
import org.grandtestauto.distributed.*;
import org.grandtestauto.test.*;

import java.io.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class TaskDetailsTest {
    private String classpath = System.getProperty( "java.class.path" );

    public boolean constructorTest() {
        TaskDetails td = new TaskDetails( "a;b;c;", "a.b.c" );
        assert td.classpath().equals( "a.b.c" );
        assert td.packageName().equals( "a;b;c;" );
        return true;
    }

    public boolean classpathTest() {
        String cp = ".;pks.jar;gnu-regexp.jar;mail.jar;activation.jar;xerces.jar;jh.jar;jide.jar;colt.jar;jxl.jar;itext.jar";
        assert new TaskDetails( "a", cp ).classpath().equals( cp );
        return true;
    }

    public boolean packageNameTest() {
        assert new TaskDetails( "org.gta", "p" ).packageName().equals( "org.gta" );
        return true;
    }

    public boolean serialisationTest() {
        assert SerializationTester.check( new TaskDetails( "a.b.c", "a;b;c;" ) );
        return true;
    }

    public boolean gradeTest() {
        init( Grandtestauto.test91_zip );
        TaskDetails td = new TaskDetails( "a91.g1", classpath );
        assert td.grade().grade() == 1;

        td = new TaskDetails( "a91.g2", classpath );
        assert td.grade().grade() == 2 : "Got: " + td.grade().grade();

        td = new TaskDetails( "a91.g3", classpath );
        assert td.grade().grade() == 3;

        td = new TaskDetails( "a91.g4", classpath );
        assert td.grade().grade() == 4;

        //Check that a GTAGrade class that implements Grade
        //beats an annotated UnitTester class.
        init( Grandtestauto.test92_zip );
        td = new TaskDetails( "a92.g6", classpath );
        assert td.grade().grade() == 5;

        return true;
    }

    public boolean compareToTest() {
        //test92 has 9 test packages, with 3 of grade 0, 4 of grade 5 and 3 of grade 10.
        //The grades are defined with a mixture of defaults, GTAGrades, and annotations.
        init( Grandtestauto.test92_zip );
        TaskDetails td1 = new TaskDetails( "a92.g1", classpath );
        TaskDetails td2 = new TaskDetails( "a92.g2", classpath );
        TaskDetails td3 = new TaskDetails( "a92.g3", classpath );
        TaskDetails td4 = new TaskDetails( "a92.g4", classpath );//Grade 10
        TaskDetails td5 = new TaskDetails( "a92.g5", classpath );//Grade 10
        TaskDetails td6 = new TaskDetails( "a92.g6", classpath );//Grade 5
        TaskDetails td7 = new TaskDetails( "a92.g7", classpath );
        TaskDetails td8 = new TaskDetails( "a92.g8", classpath );
        TaskDetails td9 = new TaskDetails( "a92.g9", classpath );
        //Implicitly test the ordering by putting these into a sorted set.
        TreeSet<TaskDetails> set = new TreeSet<TaskDetails>();
        set.add( td1 );
        set.add( td2 );
        set.add( td3 );
        set.add( td4 );
        set.add( td5 );
        set.add( td6 );
        set.add( td7 );
        set.add( td8 );
        set.add( td9 );
        Iterator<TaskDetails> itor = set.iterator();
        assert itor.next() == td4;
        assert itor.next() == td5;
        assert itor.next() == td6;
        assert itor.next() == td7;
        assert itor.next() == td8;
        assert itor.next() == td9;
        assert itor.next() == td1;
        assert itor.next() == td2;
        assert itor.next() == td3;

        //Some explicit tests.
        assert td1.compareTo( td1 ) == 0;
        assert td1.compareTo( td2 ) < 0;
        assert td2.compareTo( td1 ) > 0;
        return true;
    }

    public boolean equalsTest() {
        TaskDetails td1 = new TaskDetails( "a.b.c", "classpath" );
        TaskDetails td2 = new TaskDetails( "a.B.c", "classpath" );
        TaskDetails td3 = new TaskDetails( "a.b.c", "Klasspath" );
        TaskDetails td4 = new TaskDetails( "classpath", "a.b.c" );
        assert !td1.equals( td2 );
        assert td1.equals( td3 );
        assert !td1.equals( td4 );
        assert td1.equals( td1 );
        //@todo check that grade has no effect on equality.
        return true;
    }

    public boolean hashCodeTest() {
        TaskDetails td1 = new TaskDetails( "a.b.c", "classpath" );
        assert td1.hashCode() == "a.b.c".hashCode();
        return true;
    }

    public boolean toStringTest() {
        TaskDetails td = new TaskDetails( "a.b.c", "p1;p2;" );
        assert td.toString().equals( "TaskDetails: Classpath = 'p1;p2;' package name: 'a.b.c'. Grade: 0." ) : td.toString();
        return true;
    }

    public boolean canRunTestsTest() {
        init( Grandtestauto.test92_zip );
        TaskDetails td1 = new TaskDetails( "a92.g1", classpath );
        TaskDetails td2 = new TaskDetails( "a92.g2", classpath );
        TaskDetails td3 = new TaskDetails( "a92.g3", classpath );
        TaskDetails td4 = new TaskDetails( "a92.g4", classpath );
        TaskDetails td5 = new TaskDetails( "a92.g5", classpath );
        TaskDetails td6 = new TaskDetails( "a92.g6", classpath );
        TaskDetails td7 = new TaskDetails( "a92.g7", classpath );
        TaskDetails td8 = new TaskDetails( "a92.g8", classpath );
        TaskDetails td9 = new TaskDetails( "a92.g9", classpath );
        AgentDetails ad0 = new AgentDetails( "A", 0 );
        assert td1.canRunTests( ad0 );
        assert td2.canRunTests( ad0 );
        assert td3.canRunTests( ad0 );
        assert !td4.canRunTests( ad0 );
        assert !td9.canRunTests( ad0 );
        AgentDetails ad5 = new AgentDetails( "A", 5 );
        assert td1.canRunTests( ad5 );
        assert !td5.canRunTests( ad5 );//Grade 10
        assert td6.canRunTests( ad5 );
        assert td7.canRunTests( ad5 );
        assert td8.canRunTests( ad5 );
        return true;
    }

    private void init( String configuredZipName ) {
        Helpers.cleanTempDirectory();
        Helpers.expandZipTo( new File( configuredZipName ), Helpers.tempDirectory() );
    }
}
