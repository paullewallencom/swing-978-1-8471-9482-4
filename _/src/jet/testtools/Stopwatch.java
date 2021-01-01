package jet.testtools;

import java.text.*;
import java.util.*;

/**
 * For measuring times. Repeatedly calling <code>start()</code> and <code>stop()</code>
 * creates a sequence of time measurements that can be obtained with <code>times()</code>.
 *
 * @author Tim Lavers
 */
public class Stopwatch {
    private List<Long> times = new LinkedList<Long>();
    private Long lastStartTime = null;

    public void start() {
        lastStartTime = System.currentTimeMillis();
    }

    public void stop() {
        //Want to get the time before doing the assert.
        long now = System.currentTimeMillis();
        assert lastStartTime != null : "Has not been started or re-started since last stop!";
        times.add( now - lastStartTime );
        lastStartTime = null;
    }

    public List<Long> times() {
        return times;
    }

    public String toString() {
        NumberFormat formatter = new DecimalFormat();
        StringBuilder sb = new StringBuilder( "[" );
        boolean first = true;
        for (Long time : times) {
            if (!first) {
                sb.append( "; " );
            } else {
                first = false;
            }
            sb.append( formatter.format( time ) );
        }
        sb.append( "]" );
        return sb.toString();
    }

    public Long max() {
        if (times.size() == 0) return null;
        Long result = times.get( 0 );
        for (Long time : times) {
            if (time > result) result = time;
        }
        return result;
    }
}
