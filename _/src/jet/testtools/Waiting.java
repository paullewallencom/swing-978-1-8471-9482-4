package jet.testtools;

/**
 * This class provides a means of suspending our test thread
 * just long enough for some anticipated event to occur, as
 * is discussed in Chapter 12.
 *
 * @author Tim Lavers
 */
public final class Waiting {

    /**
     * Private constructor as we don't want to instantiate this.
     */
    private Waiting() {}

    public static interface ItHappened {
        public boolean itHappened();
    }

    /**
     * Suspend the calling thread for the given number of milliseconds.
     */
    public static void pause( final long milliseconds ) {
        try {
            Thread.sleep( milliseconds );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Suspend the calling thread essentially forever.
     */
    public static void stop() {
        pause( Long.MAX_VALUE );
    }

    /**
     * Suspend the calling thread until the event
     * defined by <code>ih</code> has ocurred;
     * return true if this has happened within the given timeout,
     * and check <code>ih</code> every 10 milliseconds.
     */
    public static boolean waitFor( ItHappened ih, long timeout ) {
        return new Waiter(ih).waitFor(timeout);
    }

    public static boolean waitFor(final ItHappened ih, final long timeout, long period) {
      return new Waiter(ih, period).waitFor(timeout);
    }
}

/**
 * Helper class that provides the machinery for
 * <code>Waiting.waitForCheckingWithPeriod()</code>.
 * The <code>waitFor()</code> method suspends the calling
 * thread, and starts a new thread that checks the
 * condition. When the condition is met, the calling thread
 * is awakened, and <code>true<code> returned.
 * If the calling thread awakens, without the condition
 * having been met, <code>false</code> is returned.
 * We use this two thread model so that the
 * checks (which might throw an exception) are not
 * done in the calling thread. Also, we can make use
 * of the timeout mechanism in <code>Thread.sleep()</code>.
 */
final class Waiter {
    private final Waiting.ItHappened ih;
    private final Object lock;
    private boolean finished;
    private long period = 10;

    public Waiter(final Waiting.ItHappened ih) {
      this.ih = ih;
      lock = new Object();
    }

    public Waiter(final Waiting.ItHappened ih, long period) {
      this.ih = ih;
      lock = new Object();
      this.period = period;
    }

    boolean waitFor(final long timeout) {
      //Start a thread to check that the event has happened.
      final boolean[] didItHappen = new boolean[]{false};
      final Thread checker = new Thread("Checker") {
        public void run() {
          while (!finished) {
            Waiting.pause(period);
            didItHappen[0] = ih.itHappened();
            if (didItHappen[0]) {
              finished = true;
            }
          }
          synchronized (lock) {
            lock.notify();
          }
        }
      };
      try {
        synchronized (lock) {
          checker.start();
          lock.wait(timeout);
        }
      } catch (InterruptedException e) {
        finished = true;
        return false;
      }
      finished = true;
      return didItHappen[0];
    }
}