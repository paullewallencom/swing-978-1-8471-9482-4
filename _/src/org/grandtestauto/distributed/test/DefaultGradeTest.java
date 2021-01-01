package org.grandtestauto.distributed.test;

import jet.testtools.*;
import org.grandtestauto.distributed.*;

/**
 * @author Tim Lavers
 */
public class DefaultGradeTest {

    public boolean constructor_Grade_Test() {
        //@todo remove this constructor
        DefaultGrade dg = new DefaultGrade( null );
        assert dg.grade() == 0;

        dg = new DefaultGrade( new GradeImpl( 299 ) );
        assert dg.grade() == 299;

        return true;
    }

    public boolean constructor_int_Test() {
        DefaultGrade dg = new DefaultGrade( 0 );
        assert dg.grade() == 0;

        dg = new DefaultGrade( 299 );
        assert dg.grade() == 299;

        return true;
    }

    public boolean serialisationTest() {
        assert SerializationTester.check( new DefaultGrade( 0 ) );
        return true;
    }

    public boolean compatibleTest() {
        DefaultGrade dg = new DefaultGrade( new GradeImpl( 0 ) );
        assert dg.compatible( new AgentDetails( "Agent99", 99 ) );
        return true;
    }

    public boolean gradeTest() {
        DefaultGrade dg = new DefaultGrade( new GradeImpl( 0 ) );
        assert dg.grade() == 0;

        dg = new DefaultGrade( new GradeImpl( 10 ) );
        assert dg.grade() == 10;

        dg = new DefaultGrade( new GradeImpl( 100 ) );
        assert dg.grade() == 100;

        dg = new DefaultGrade( new GradeImpl( 1000 ) );
        assert dg.grade() == 1000;

        return true;
    }

    private class GradeImpl implements Grade {
        private int i;

        private GradeImpl( int i ) {
            this.i = i;
        }

        public int grade() {
            return i;
        }

        public boolean compatible( AgentDetails agentDetails ) {
            return true;
        }
    }
}
