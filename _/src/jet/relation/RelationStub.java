package jet.relation;

import java.util.*;

/**
 * Just defines the main methods required in the
 * simple implementation of a mathematical relation,
 * used to show the 'bootstrapping' approach to writing
 * unit tests, that is discussed in Chapter 2.
 */
public class RelationStub<E, F> {

    /**
     * Inserts the specified pair.
     *
     * @param domainElement not null
     * @param rangeElement  not null
     */
    public void add( E domainElement, F rangeElement ) {
    }

    /**
     * Removes the specified pair.
     *
     * @param domainElement not null
     * @param rangeElement  not null
     */
    public void remove( E domainElement, F rangeElement ) {
    }

    /**
     * The number of (domainElement, rangeElement)
     * pairs in this relation.
     */
    public int size() {
        return 0;
    }

    /**
     * The set of domain elements of this relation.
     */
    public Set<E> domain() {
        return null;
    }

    /**
     * True if and only if the given object
     * is in the range of this relation.
     */
    public boolean inRange( F obj ) {
        return false;
    }

    /**
     * The range of the relation corresponding
     * to the specified domain element.
     */
    public Set<F> rangeFor( E domainElement ) {
        return null;
    }
}
