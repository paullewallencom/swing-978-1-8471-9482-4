package org.grandtestauto.distributed;

import java.io.*;

/**
 * A serialisable wrapper for the grades supplied in the test
 * packages.
 *
 * @author Tim Lavers
 */
public class DefaultGrade implements Grade {

    private int value;

    public DefaultGrade( int value ) {
        this.value = value;
    }

    public DefaultGrade( Grade grade ) {
        if (grade != null) {
            this.value = grade.grade();
        } else {
            value = 0;
        }
    }

    public int grade() {
        return value;
    }

    public boolean compatible( AgentDetails agentDetails ) {
        return true;
    }
}
