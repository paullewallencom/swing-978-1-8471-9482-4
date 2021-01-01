package jet.relation.test;

import jet.relation.*;

public class RelationTest {

    public boolean addTest() {
        //a. After an add() of a pair (domainElement, rangeElement),
        //not previously in the relation, does the size
        //of the relation increase by one?
        Relation<String, String> relation = r();
        assert relation.size() == 0;
        relation.add( "a", "b" );
        assert relation.size() == 1;

        //b. After an add(), does domainElement appear in the
        //domain, and rangeElement appear in the range?
        relation = r();
        assert !relation.domain().contains( "a" );
        assert !relation.inRange( "b" );
        relation.add( "a", "b" );
        assert relation.domain().contains( "a" );
        assert relation.inRange( "b" );

        //c. If we add the same pair in a second time,
        //does the size remain the same?
        relation = r();
        relation.add( "a", "b" );
        assert relation.size() == 1;
        relation.add( "a", "b" );
        assert relation.size() == 1;

        //d. If we add in a pair where the domainElement
        //is already in the domain, but the rangeElement
        //is not in the range, does the size of the
        //domain stay constant?
        //Does the size of the relation still increase by one?
        //Does the size of the range increase by one?
        relation = r();
        assert relation.domain().size() == 0;
        relation.add( "a", "b" );
        assert relation.domain().size() == 1;
        assert relation.size() == 1;
        assert relation.range().size() == 1;
        relation.add( "a", "c" );
        assert relation.domain().size() == 1;
        assert relation.size() == 2;
        assert relation.range().size() == 2;

        //e. Similarly, if we add in a pair where the rangeElement
        //is already in the range, but the domainElement
        //is not in the domain,
        //does the size of the range stay constant?
        //Does the size of the Relation still increase by one?
        //Does the size of the domain increase by one?
        relation = r();
        relation.add( "a", "b" );
        assert relation.domain().size() == 1;
        assert relation.size() == 1;
        assert relation.range().size() == 1;
        relation.add( "x", "b" );
        assert relation.domain().size() == 2;
        assert relation.size() == 2;
        assert relation.range().size() == 1;

        //f. If we add a pair (a, b) where each of a and b are
        //already in the relation domain and range respectively,
        //but the pair itself is new to the relation,
        //does the size of the relation increase by one,
        //and are the sizes of the domain and range unchanged?
    relation = r();
    relation.add( "a", "b" );
    relation.add( "c", "d" );
    assert relation.domain().size() == 2;
    assert relation.size() == 2;
    assert relation.range().size() == 2;
    relation.add( "a", "d" );
    assert relation.size() == 3;
    assert relation.domain().size() == 2;
    assert relation.range().size() == 2;
    relation.add( "c", "b" );
    assert relation.size() == 4;
    assert relation.domain().size() == 2;
    assert relation.range().size() == 2;

        return true;
    }

    private Relation<String, String> r() {
        return new Relation<String, String>();
    }

    //Dummy tests for other methods, as this is only a demo class.
    public boolean constructorTest() {
        return true;
    }

    public boolean removeTest() {
        return true;
    }

    public boolean sizeTest() {
        return true;
    }

    public boolean domainTest() {
        return true;
    }

    public boolean rangeTest() {
        return true;
    }

    public boolean inRangeTest() {
        return true;
    }

    public boolean rangeForTest() {
        return true;
    }
}
