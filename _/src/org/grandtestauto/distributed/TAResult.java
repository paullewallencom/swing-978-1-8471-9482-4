package org.grandtestauto.distributed;

/**
 * @author Tim Lavers
 */
class TAResult implements TaskResult {
    private String agentName;
    private String serr;
    private String sout;
    private TaskSummary taskSummary;
    private String resultsFileContents;

    TAResult( TaskSummary taskSummary, String agentName, String[] soutSerr ) {
        this.taskSummary = taskSummary;
        this.agentName = agentName;
        this.serr = soutSerr[1];
        this.sout = soutSerr[0];
    }

    public int compareTo( TaskResult o ) {
        int result = taskSummary.packageName().compareTo( o.taskSummary().packageName() );
        if (result == 0) {
            result = serr.compareTo( o.serr() );
        }
        if (result == 0) {
            result = sout.compareTo( o.sout() );
        }
        if (result == 0) {
            result = resultsFileContents.compareTo( o.resultsFileContents() );
        }
        if (result == 0) {
            result = agentName.compareTo( o.testAgentName() );
        }
        return result;
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TAResult taResult = (TAResult) o;

        if (!agentName.equals( taResult.agentName )) return false;
        if (!resultsFileContents.equals( taResult.resultsFileContents )) return false;
        if (!serr.equals( taResult.serr )) return false;
        if (!sout.equals( taResult.sout )) return false;
        if (!taskSummary.equals( taResult.taskSummary )) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = agentName.hashCode();
        result = 31 * result + serr.hashCode();
        result = 31 * result + sout.hashCode();
        result = 31 * result + taskSummary.hashCode();
        result = 31 * result + resultsFileContents.hashCode();
        return result;
    }

    public String testAgentName() {
        return agentName;
    }

    public TaskSummary taskSummary() {
        return taskSummary;
    }

    public String serr() {
        return serr;
    }

    public String sout() {
        return sout;
    }

    public String resultsFileContents() {
        return resultsFileContents;
    }

    public String toString() {
        String nl = System.getProperty( "line.separator" );
        StringBuilder sb = new StringBuilder( );
        sb.append( "Results from " ).append( agentName ).append(  " for ").append( taskSummary.packageName() ).append( ":" );
        sb.append( nl );
        sb.append( nl );
        sb.append( "sout: '").append( sout ).append( "'");
        sb.append( nl );
        sb.append( nl );
        sb.append( "serr: '").append( serr ).append( "'");
        sb.append( nl );
        sb.append( nl );
        sb.append( "Results file contents: '").append( resultsFileContents ).append( "'");
        sb.append( "End of results from ").append( agentName).append( "." );
        sb.append( nl );
        return sb.toString();
    }

    void setResultsFileContents( String resultsFileContents ) {
        this.resultsFileContents = resultsFileContents;
    }
}
