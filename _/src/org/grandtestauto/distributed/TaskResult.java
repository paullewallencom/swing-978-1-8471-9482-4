package org.grandtestauto.distributed;

import java.io.*;

/**
 * @author Tim Lavers
 */
public interface TaskResult extends Serializable, Comparable<TaskResult> {
    String testAgentName();
    TaskSummary taskSummary();
    String serr();
    String sout();
    String resultsFileContents();
}
