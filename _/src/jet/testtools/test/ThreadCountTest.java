package jet.testtools.test;

import jet.testtools.TestHelper;
import jet.testtools.Waiting;

/**
 * @deprecated Is this used????????????????//
 */
public class ThreadCountTest {
  final int ACCESSORS = 10;
  final int ITERATIONS = 100;

  public boolean countTest() throws Exception {
    Reader[] readers = new Reader[ACCESSORS];
    for (int i = 0; i < ACCESSORS; i++) {
      readers[i] = new Reader(i);
      readers[i].start();
    }
    System.out.println(TestHelper.namesOfActiveThreads());
    for (Reader reader : readers) {
      reader.seFinished(true);
    }
    for (Reader reader : readers) {
      reader.join();
    }
    System.out.println(TestHelper.namesOfActiveThreads());
    return true;
  }

  private class Reader extends Thread {
    boolean finished = false;

    public Reader(int i) {
      super("Reader" + i);
    }

    public void run() {
      while (!finished) {
        Waiting.pause(100);
      }
    }

    public void seFinished(boolean isFinished) {
      finished = isFinished;
    }
  }


}
