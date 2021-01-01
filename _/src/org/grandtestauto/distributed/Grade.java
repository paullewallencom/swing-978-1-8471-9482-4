package org.grandtestauto.distributed;

/**
 * Used to indicate the computer power needed to run a package
 * of tests, and also to prevent packages of tests from being
 * run on unsuitable machines.
 *
 * @author Tim Lavers
 */
public interface Grade extends java.io.Serializable {

    /**
     * Higher values indicate tests requiring
     * more powerful computers.
     * 
     * @return 0 for tests any machine can perform,
     * higher values as required.
     */
    int grade();

    /**
     * Independent of the grade, can the tests graded by this
     * Grade be run by the TestAgent described by the given
     * AgentDetails?
     */
    boolean compatible( AgentDetails agentDetails );
}
