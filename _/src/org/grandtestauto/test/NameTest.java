package org.grandtestauto.test;

import org.grandtestauto.*;

import java.io.*;
import java.util.*;

import jet.testtools.test.org.grandtestauto.*;

public class NameTest {

    private void init( String zipName ) {
        Helpers.cleanTempDirectory();
        Helpers.expandZipTo( new File( zipName ), Helpers.tempDirectory() );
    }

    public boolean subDirectoriesAsNamesTest() {
        init( Grandtestauto.test53_zip );
        TreeSet<Name> expected = new TreeSet<Name>( );
        expected.add( new Name( "a53" ) );
        expected.add( new Name( "a53.a0" ) );
        expected.add( new Name( "a53.a1" ) );
        expected.add( new Name( "a53.a2" ) );
        expected.add( new Name( "a53.a3" ) );
        expected.add( new Name( "a53.a4" ) );
        expected.add( new Name( "a53.a5" ) );
        expected.add( new Name( "a53.test" ) );
        expected.add( new Name( "a53.functiontest" ) );
        expected.add( new Name( "a53.loadtest" ) );
        expected.add( new Name( "a53.a0.test" ) );
        expected.add( new Name( "a53.a0.functiontest" ) );
        expected.add( new Name( "a53.a0.loadtest" ) );
        expected.add( new Name( "a53.a1.test" ) );
        expected.add( new Name( "a53.a1.functiontest" ) );
        expected.add( new Name( "a53.a1.loadtest" ) );
        expected.add( new Name( "a53.a2.test" ) );
        expected.add( new Name( "a53.a2.functiontest" ) );
        expected.add( new Name( "a53.a2.loadtest" ) );
        expected.add( new Name( "a53.a3.test" ) );
        expected.add( new Name( "a53.a3.functiontest" ) );
        expected.add( new Name( "a53.a3.loadtest" ) );
        expected.add( new Name( "a53.a4.test" ) );
        expected.add( new Name( "a53.a4.functiontest" ) );
        expected.add( new Name( "a53.a4.loadtest" ) );
        expected.add( new Name( "a53.a5.test" ) );
        expected.add( new Name( "a53.a5.functiontest" ) );
        expected.add( new Name( "a53.a5.loadtest" ) );
        Set<Name> names = Name.subDirectoriesAsNames( Helpers.tempDirectory() );
        assert names.equals( expected ): "Got: " + names;
        return true;
    }

    public boolean toStringTest() {
        assert new Name( "" ).toString().equals( ""): "Got: '" + new Name( "" ) + "'";
        assert new Name( "a" ).toString().equals( "a"): "Got: '" + new Name( "a" ) + "'";
        assert new Name( "a.b" ).toString().equals( "a.b"): "Got:' " + new Name( "a.b" ) + "'";
        return true;
    }

    public boolean equalsTest() {
        assert new Name( "" ).equals( new Name( "" ) );
        assert new Name( "a.b.c" ).equals( new Name( "a.b.c" ) );
        assert !new Name( "" ).equals( new Name( "ff" ) );
        assert !new Name( "gg" ).equals( null );
        assert !new Name( "gg" ).equals( "gg" );
        return true;
    }

    public boolean hashCodeTest() {
        assert new Name( "").hashCode() == new Name( "").hashCode();
        assert new Name( "a.b.c.d").hashCode() == new Name( "a.b.c.d").hashCode();
        return true;
    }

    public boolean compareToTest() {
        assert n( "" ).compareTo( n( "" ) ) == 0;
        assert n( "a" ).compareTo( n( "a" ) ) == 0;
        assert n( "a.b" ).compareTo( n( "a.b" ) ) == 0;
        assert n( "a.bcde.fg" ).compareTo( n( "a.bcde.fg" ) ) == 0;

        assert n( "" ).compareTo( n( "a" ) ) < 0;
        assert n( "a" ).compareTo( n( "a.b" ) ) < 0;
        assert n( "a.b" ).compareTo( n( "a.bcde.fg" ) )< 0;
        assert n( "a.bcde.fg" ).compareTo( n( "a.bcde.fg.hi" ) )< 0;

        assert n( "a" ).compareTo( n( "" ) ) > 0;
        assert n( "a.b" ).compareTo( n( "a" ) ) > 0;
        assert n( "a.bcde.fg" ).compareTo( n( "a.bcde" ) ) > 0;
        assert n( "a.bcde.fg.hi" ).compareTo( n( "a.bcde.fg" ) ) > 0;

        assert n( "a" ).compareTo( n( "b" ) ) < 0;
        assert n( "abc" ).compareTo( n( "abd" ) ) < 0;
        assert n( "a.bc.de" ).compareTo( n( "a.bc.df" ) ) < 0;
        assert n( "a.bcdef.fg" ).compareTo( n( "a.bcdeg.fg" ) ) < 0;

        assert n( "ab" ).compareTo( n( "aa" ) ) > 0;
        assert n( "a.b" ).compareTo( n( "a.a" ) ) > 0;

        return true;
    }

    public boolean constructorTest() {
        //Try various values, to check that none throws an exception.
        new Name( "" );
        new Name( "jfsf" );
        new Name( "." );
        new Name( "a.b" );
        return true;
    }

    public boolean matchesTest() {
        Name n1 = new Name( "animal" );
        Name n2 = new Name( "" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "a" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "an" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "ani" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "animal" );
        assert n2.matches( n1 );
        assert n1.matches( n2 );

        n2 = new Name( "animals" );
        assert !n2.matches( n1 );
        assert n1.matches( n2 );

        n2 = new Name( "animus" );
        assert !n2.matches( n1 );
        assert !n1.matches( n2 );

        n1 = new Name( "animal.mammal.ungulate" );
        n2 = new Name( "" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "a" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "a.m" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "a.ma" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "a.ma.u" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "a.m.u" );
        assert n2.matches( n1 );
        assert !n1.matches( n2 );

        n2 = new Name( "a.m.u.t" );
        assert !n2.matches( n1 );
        assert !n1.matches( n2 );

        return true;
    }

    private Name n( String s ) {
        return new Name( s );
    }
}
