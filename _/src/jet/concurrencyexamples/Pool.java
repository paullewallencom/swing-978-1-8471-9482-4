package jet.concurrencyexamples;

import java.util.*;

/**
 * Used as an example in Chapter 12.
 */
public class Pool<E> {
    private Set<E> set = new HashSet<E>();

    public synchronized void put( E e ) {
        set.add( e );
    }

    public synchronized Set<E> getAll() {
        Set<E> result = new HashSet<E>();
        result.addAll( set );
        return result;
    }
}
