package jet.testtools.test;

import jet.testtools.*;

import java.util.*;
import java.util.regex.*;

/**
 * @author Tim Lavers
 */
public class StopwatchTest {
    private Stopwatch sw;

    public boolean constructorTest() {
        assert new Stopwatch().max() == null;
        assert new Stopwatch().times().isEmpty();
        return true;
    }

    public boolean startTest() {
        init();
        sw.start();
        Waiting.pause( 100 );
        sw.stop();
        checkTimes( 100l );
        sw.start();
        Waiting.pause( 100l );
        sw.start();//Re-start.
        Waiting.pause( 100 );
        sw.stop();
        checkTimes( 100l, 100l );
        return true;
    }

    public boolean stopTest() {
        init();
        //Check that stop fails for a Stopwatch that has
        //not been started.
        boolean gotErrorMessage = false;
        try {
            sw.stop();
        } catch (AssertionError ae) {
            gotErrorMessage = true;
        }
        assert gotErrorMessage;

        //Basic operation of stop.
        sw.start();
        assert sw.times().isEmpty();
        sw.stop();
        assert sw.times().size() == 1;

        //Two stops in a row causes an error.
        gotErrorMessage = false;
        try {
            sw.stop();
        } catch (AssertionError ae) {
            gotErrorMessage = true;
        }
        assert gotErrorMessage;
        return true;
    }

    public boolean timesTest() {
        init();
        assert sw.times().isEmpty();
        sw.start();
        Waiting.pause( 100 );
        sw.stop();
        sw.start();
        Waiting.pause( 200 );
        sw.stop();
        sw.start();
        Waiting.pause( 300 );
        sw.stop();
        sw.start();
        Waiting.pause( 400 );
        sw.stop();
        sw.start();
        Waiting.pause( 500 );
        sw.stop();
        checkTimes( 100l, 200l, 300l, 400l, 500l );
        return true;
    }

    public boolean maxTest() {
        init();
        assert sw.max() == null;
        sw.start();
        assert sw.max() == null;
        Waiting.pause( 100 );
        sw.stop();
        checkTime( 100l, sw.max() );

        sw.start();
        Waiting.pause( 20 );
        sw.stop();
        checkTime( 100l, sw.max() );

        sw.start();
        Waiting.pause( 300 );
        sw.stop();
        checkTime( 300l, sw.max() );

        sw.start();
        Waiting.pause( 200 );
        sw.stop();
        checkTime( 300l, sw.max() );

        sw.start();
        Waiting.pause( 500 );
        sw.stop();
        checkTime( 500l, sw.max() );
        return true;
    }

    public boolean toStringTest() {
        init();
        Assert.equal( sw.toString(), "[]" );
        sw.start();
        sw.stop();
        Pattern p = Pattern.compile( "\\[[0-9]+\\]");
        assert p.matcher( sw.toString() ).matches();

        sw.start();
        sw.stop();
        p = Pattern.compile( "\\[[0-9]+; [0-9]+\\]");
        assert p.matcher( sw.toString() ).matches();

        sw.start();
        sw.stop();
        p = Pattern.compile( "\\[[0-9]+; [0-9]+; [0-9]+\\]");
        assert p.matcher( sw.toString() ).matches();
        return true;
    }

    private void init() {
        sw = new Stopwatch();
    }

    private void checkTimes( Long... expected ) {
        List<Long> times = sw.times();
        assert expected.length == times.size();
        for (int i = 0; i < expected.length; i++) {
            checkTime( expected[i], sw.times().get( i ) );
        }
    }

    private void checkTime( Long expected, Long actual ) {
        assert expected <= actual * 1.1;
        assert expected >= actual * .9;

    }
}
