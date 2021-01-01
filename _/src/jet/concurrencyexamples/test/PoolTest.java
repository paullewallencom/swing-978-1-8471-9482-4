package jet.concurrencyexamples.test;

import jet.concurrencyexamples.*;

/**
 * Used as an example in Chapter 12.
 */
public class PoolTest {
    final int INSTANCES = 10;
    final int ITERATIONS = 100;
    private Pool<String> pool;

    public boolean concurrencyTest() throws Exception {
        pool = new Pool<String>();
        Reader[] readers = new Reader[INSTANCES];
        Writer[] writers = new Writer[INSTANCES];
        for (int i = 0; i < INSTANCES; i++) {
            readers[i] = new Reader( i );
            writers[i] = new Writer( i );
            readers[i].start();
            writers[i].start();
        }
        for (Reader reader : readers) {
            reader.join();
            assert reader.finished:
                    reader.getName() + " did not finish";
        }
        for (Writer writer : writers) {
            writer.join();
            assert writer.finished:
                    writer.getName() + " did not finish";
        }
        return true;
    }

    private class Reader extends Thread {
        boolean finished = false;

        public Reader( int i ) {
            super( "Reader" + i );
        }

        public void run() {
            for (int i = 0; i < ITERATIONS; i++) {
                pool.getAll();
            }
            finished = true;
        }
    }

    private class Writer extends Thread {
        boolean finished = false;

        public Writer( int i ) {
            super( "Writer " + i );
        }

        public void run() {
            for (int i = 0; i < ITERATIONS; i++) {
                pool.put( getName() + " Count:  " + i );
            }
            finished = true;
        }
    }

    //Dummy tests for other methods, as this is only a demo class.
    public boolean constructorTest() {
        return true;
    }

    public boolean putTest() {
        return true;
    }

    public boolean getAllTest() {
        return true;
    }
}
