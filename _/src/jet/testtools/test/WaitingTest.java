package jet.testtools.test;

import jet.testtools.Waiting;

public class WaitingTest {

    public boolean pauseTest() {
        long before = System.currentTimeMillis();
        Waiting.pause( 200 );
        long after = System.currentTimeMillis();
        long diff = after - before;
        assert diff > 160;
        assert diff < 240;
        return true;
    }

    public boolean stopTest() {
        //This test is a bit lame. We just check that
        //a thread that calls Waiting.stop() goes
        //into a timed waiting state.
        Thread t = new Thread() {
            public void run() {
                Waiting.stop();
            }
        };
        t.setDaemon( true );
        t.start();
        Waiting.pause( 20 );
        assert t.getState().equals( Thread.State.TIMED_WAITING );
        return true;
    }

    public boolean waitFor_Waiting$ItHappened_long_Test() throws InterruptedException {
        //We create a thread that will use Waiting.waitFor() to
        //wait on a flag we can control.
        final boolean[] resultOfItHappened = new boolean[]{false};
        final boolean[] flagThatWaitOver = new boolean[]{false};
        IH ih = new IH( resultOfItHappened );
        Waiter waiter = new Waiter( ih, 1000, flagThatWaitOver );
        waiter.start();//Should pretty immediately go into a wait state.
        //Pause the test then set the flag so the waiter finishes waiting.
        Waiting.pause( 100 );
        resultOfItHappened[0] = true;
        //Wait for the run() method of the waiter to have
        //completed, so that we can be sure it has finished waiting.
        waiter.join( 1000 );
        //Check that the wait finished naturally and was
        //of about the right length, and that there were about
        //the right number of calls to ih.itHappened().
        assert waiter.flagThatWaitOver[0];
        //Note the large margins for error - the timing
        //resolution on Win XP is pretty coarse.
        assert isApproximately( waiter.timeWaiting, 100, 40 );
        assert isApproximately( ih.numberOfChecksDone, 10, 4 );

        //Another test with a longer wait, and relatively
        //finer error margins.
        resultOfItHappened[0] = false;
        flagThatWaitOver[0] = false;
        ih = new IH( resultOfItHappened );
        waiter = new Waiter( ih, 10000, flagThatWaitOver );
        waiter.start();
        Waiting.pause( 1000 );
        resultOfItHappened[0] = true;
        waiter.join( 10000 );
        assert waiter.flagThatWaitOver[0];
        assert isApproximately( waiter.timeWaiting, 1000, 100 );
        assert isApproximately( ih.numberOfChecksDone, 100, 10 );

        //Now a test that the wait eventually times out.
        resultOfItHappened[0] = false;
        flagThatWaitOver[0] = false;
        ih = new IH( resultOfItHappened );
        waiter = new Waiter( ih, 100, flagThatWaitOver );
        waiter.start();
        Waiting.pause( 200 );
        assert !resultOfItHappened[0];

        return true;
    }

    public boolean waitFor_Waiting$ItHappened_long_long_Test() throws InterruptedException {
        //See comments above. Here we are adding check period.
        final boolean[] resultOfItHappened = new boolean[]{false};
        final boolean[] flagThatWaitOver = new boolean[]{false};
        IH ih = new IH( resultOfItHappened );
        Waiter waiter = new Waiter( ih, 5000, 100, flagThatWaitOver );
        waiter.start();//Should pretty immediately go into a wait state.
        //Pause the test then set the flag so the waiter finishes waiting.
        Waiting.pause( 2000 );
        resultOfItHappened[0] = true;
        //Wait for the run() method of the waiter to have
        //completed, so that we can be sure it has finished waiting.
        waiter.join( 6000 );
        //Check that the wait finished naturally and was
        //of about the right length, and that there were about
        //the right number of calls to ih.itHappened().
        assert waiter.flagThatWaitOver[0];
        //Note the large margins for error - the timing
        //resolution on Win XP is pretty coarse.
        assert isApproximately( waiter.timeWaiting, 2000, 100 );
        assert isApproximately( ih.numberOfChecksDone, 20, 6 );

        //Another test with a longer wait.
        resultOfItHappened[0] = false;
        flagThatWaitOver[0] = false;
        ih = new IH( resultOfItHappened );
        waiter = new Waiter( ih, 20000,1000, flagThatWaitOver );
        waiter.start();
        Waiting.pause( 10000 );
        resultOfItHappened[0] = true;
        waiter.join( 12000 );
        assert waiter.flagThatWaitOver[0];
        assert isApproximately( waiter.timeWaiting, 10000, 1000 );
        assert isApproximately( ih.numberOfChecksDone, 10, 2 );

        //Now a test that the wait eventually times out.
        resultOfItHappened[0] = false;
        flagThatWaitOver[0] = false;
        ih = new IH( resultOfItHappened );
        waiter = new Waiter( ih, 2000, 50, flagThatWaitOver );
        waiter.start();
        Waiting.pause( 3000 );
        assert !resultOfItHappened[0];
        assert isApproximately( ih.numberOfChecksDone, 40, 10 );

        return true;
    }

    private static boolean isApproximately( long actualValue, long expectedValue, long tolerance ) {
        boolean result = actualValue < expectedValue + tolerance;
        result &= actualValue > expectedValue - tolerance;
        return result;
    }

    private static class IH implements Waiting.ItHappened {
        private final boolean[] resultValue;
        volatile int numberOfChecksDone;

        public IH( boolean[] resultValue ) {
            this.resultValue = resultValue;
        }

        public boolean itHappened() {
            numberOfChecksDone++;
            return resultValue[0];
        }
    }

    static class Waiter extends Thread {
        private final Waiting.ItHappened ih;
        private final boolean[] flagThatWaitOver;
        private final long timeout;
        private Long checkPeriod = null;
        long timeWaiting;

        public Waiter( Waiting.ItHappened ih, long timeout, boolean[] flagThatWaitOver ) {
            this.ih = ih;
            this.timeout = timeout;
            this.flagThatWaitOver = flagThatWaitOver;
        }

        public Waiter( Waiting.ItHappened ih, long timeout, long checkPeriod, boolean[] flagThatWaitOver ) {
            this.ih = ih;
            this.timeout = timeout;
            this.flagThatWaitOver = flagThatWaitOver;
            this.checkPeriod = checkPeriod;
        }
        public void run() {
            long then = System.currentTimeMillis();
            if (checkPeriod == null) {
                Waiting.waitFor( ih, timeout );
            } else {
                Waiting.waitFor( ih, timeout, checkPeriod );
            }
            flagThatWaitOver[0] = true;
            long now = System.currentTimeMillis();
            timeWaiting = now - then;
        }
    }
}
