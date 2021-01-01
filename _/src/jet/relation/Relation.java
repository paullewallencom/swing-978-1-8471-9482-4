package jet.relation;

import java.util.*;

/**
 * Very simple partial implementation of a mathematical relation,
 * used to show the 'bootstrapping' approach to writing
 * unit tests that is discussed in Chapter 2.
 */
public class Relation<E, F> {
    private HashSet<Tuple> tuples = new HashSet<Tuple>();

    /**
     * Inserts the specified pair.
     *
     * @param domainElement not null
     * @param rangeElement  not null
     */
    public void add( E domainElement, F rangeElement ) {
        assert domainElement != null;
        assert rangeElement != null;
        tuples.add( new Tuple( domainElement, rangeElement ) );
    }

    /**
     * Removes the specified pair.
     *
     * @param domainElement not null
     * @param rangeElement  not null
     */
    public void remove( E domainElement, F rangeElement ) {
        assert domainElement != null;
        assert rangeElement != null;
        tuples.remove( new Tuple( domainElement, rangeElement ) );
    }

    /**
     * The number of (domainElement, rangeElement) pairs in this relation.
     */
    public int size() {
        return tuples.size();
    }

    /**
     * The set of domain elements of this relation.
     */
    public Set<E> domain() {
        HashSet<E> domain = new HashSet<E>();
        for (Tuple t : tuples) {
            domain.add( t.domainElement );
        }
        return domain;
    }

    /**
     * The set of range elements of this relation.
     */
    public Set<F> range() {
        return rangeImpl( null );
    }

    /**
     * True if and only if the given object is in the range of this relation.
     */
    public boolean inRange( F obj ) {
        for (Tuple t : tuples) {
            if (t.rangeElement.equals( obj )) return true;
        }
        return false;
    }

    /**
     * The range of the relation corresponding to the specified domain element.
     */
    public Set<F> rangeFor( E domainElement ) {
        return rangeImpl( domainElement );
    }

    private Set<F> rangeImpl( E domainElement ) {
        HashSet<F> range = new HashSet<F>();
        for (Tuple t : tuples) {
            if (domainElement == null || domainElement.equals( t.domainElement )) {
                range.add( t.rangeElement );
            }
        }
        return range;
    }

    private class Tuple {
        E domainElement;
        F rangeElement;

        public Tuple( E domainElement, F rangeElement ) {
            this.domainElement = domainElement;
            this.rangeElement = rangeElement;
        }

        public boolean equals( Object o ) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple tuple = (Tuple) o;

            if (!domainElement.equals( tuple.domainElement )) return false;
            return rangeElement.equals( tuple.rangeElement );
        }

        public int hashCode() {
            int result;
            result = domainElement.hashCode();
            result = 31 * result + rangeElement.hashCode();
            return result;
        }
    }
}