package jet.ikonmaker.test;

import jet.ikonmaker.IkonName;

import java.util.*;

/**
 * @author Tim Lavers
 */
public class IkonNameTest {

    private IkonName frodo = new IkonName( "Frodo" );
    private IkonName merry = new IkonName( "merry" );
    private IkonName pippin = new IkonName( "Pippin" );
    private IkonName sam = new IkonName( "sam" );
    private IkonName arwen = new IkonName( "ARWEN" );
    private IkonName gandalf = new IkonName( "Gandalf" );

    public boolean constructorTest() {
        boolean gotNPE = false;
        try {
            new IkonName( null );
        } catch (NullPointerException npe) {
            gotNPE = true;
        }
        assert gotNPE : "Should have thrown NullPointerException";

        checkIAEThrown( "" );
        checkIAEThrown( "dfsd\\fsfs" );
        checkIAEThrown( "fsdf/fsdf" );
        checkIAEThrown( "sdfs*fdsfs" );
        checkIAEThrown( "fsdfds.sfsdf" );
        checkIAEThrown( "fsdfds(sfsdf" );
        checkIAEThrown( "fsdfds<sfsdf" );
        checkIAEThrown( "fsdfds,sfsdf" );
        checkIAEThrown( "fsdfds?sfsdf" );

        return true;
    }

    private void checkIAEThrown( String str ) {
        boolean gotIAE = false;
        try {
            new IkonName( str );
        } catch (IllegalArgumentException npe) {
            gotIAE = true;
        }
        assert gotIAE : "Should have thrown IllegalArgumentException";
    }

    public boolean toStringTest() {
        assert frodo.toString().equals( "Frodo" );
        assert arwen.toString().equals( "ARWEN" );
        assert gandalf.toString().equals( "Gandalf" );
        return true;
    }

    public boolean hashCodeTest() {
        assert merry.hashCode() == new IkonName( "merry" ).hashCode();
        assert pippin.hashCode() == new IkonName( "pippin" ).hashCode();
        assert arwen.hashCode() == new IkonName( "arwen" ).hashCode();
        return true;
    }

    public boolean equalsTest() {
        assert merry.equals( new IkonName( "MERRY" ) );
        assert arwen.equals( new IkonName( "arwen" ) );
        assert gandalf.equals( new IkonName( "gandalf" ) );
        return true;
    }

    public boolean compareToTest() {
        //Check by putting them into a sorted set and checking that they're in the right.
        TreeSet<IkonName> ts = new TreeSet<IkonName>();
        ts.add( frodo );
        ts.add( sam );
        ts.add( pippin );
        ts.add( merry );
        ts.add( arwen );
        ts.add( gandalf );

        assert ts.size() == 6;//None lost.
        Iterator itor = ts.iterator();
        assert itor.next().equals( arwen );
        assert itor.next().equals( frodo );
        assert itor.next().equals( gandalf );
        assert itor.next().equals( merry );
        assert itor.next().equals( pippin );
        assert itor.next().equals( sam );
        return true;
    }
}