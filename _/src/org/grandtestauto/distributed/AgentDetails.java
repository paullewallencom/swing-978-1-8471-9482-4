package org.grandtestauto.distributed;

import java.io.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public class AgentDetails implements Serializable {

    private String name;
    private int maximumGrade;
    private Properties systemProperties;

    public AgentDetails( String name, int maximumGrade ) {
        this.name = name;
        this.maximumGrade =maximumGrade;
        systemProperties = System.getProperties();
    }

    public String name() {
        return name;
    }

    public int maximumGrade() {
        return maximumGrade;
    }

    public Properties systemProperties() {
        return systemProperties;
    }

    @Override
    public String toString() {
        return name;
    }
}
