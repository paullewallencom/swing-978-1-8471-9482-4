package jet.concurrencyexamples.test;

import jet.concurrencyexamples.*;

import java.util.*;

/**
 * Used as an example in Chapter 12.
 */
public class IntegerFactoryTest {
    final int NUMBER_OF_READERS = 3;
    final int NUMBER_TO_READ = 100;

    public boolean nextTest() throws Exception {
        Reader[] readers = new Reader[NUMBER_OF_READERS];
        for (int i = 0; i < readers.length; i++) {
            readers[i] = new Reader();
            readers[i].start();
        }
        for (Reader reader : readers) {
            reader.join();
        }

        //Collect together all the values that were read.
        SortedSet<Integer> allValuesRead = new TreeSet<Integer>();
        //Add the values for each reader.
        for (Reader reader : readers) {
            for (int i = 0; i < NUMBER_TO_READ; i++) {
                //Check that the value was not read by another reader.
                assert!allValuesRead.contains( reader.values[i] );
                //Record the value as read.
                allValuesRead.add( reader.values[i] );
                //Check that the values are increasing.
                if (i > 0) {
                    assert reader.values[i] > reader.values[i - 1];
                }
            }
        }
        assert allValuesRead.first() == 0;
        int expectedCount = NUMBER_OF_READERS * NUMBER_TO_READ;
        assert allValuesRead.size() == expectedCount;
        assert allValuesRead.last() == expectedCount - 1;

        return true;
    }

    private class Reader extends Thread {
        int[] values = new int[NUMBER_TO_READ];

        public void run() {
            for (int i = 0; i < NUMBER_TO_READ; i++) {
                values[i] = IntegerFactory.next();
            }
        }
    }
}
