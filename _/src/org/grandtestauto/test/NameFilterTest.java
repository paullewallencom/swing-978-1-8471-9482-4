package org.grandtestauto.test;

import org.grandtestauto.*;

@SuppressWarnings({"ObjectEqualsNull"})
public class NameFilterTest {
    public boolean constructorTest() {
        NameFilter nf = new NameFilter( NameFilter.Type.PACKAGE, null, null, null );
        assert nf.accept( "a" );
        assert nf.accept( "m" );
        assert nf.accept( "z" );
        assert nf.accept( "" );
        assert nf.accept( null );

        nf = new NameFilter( NameFilter.Type.PACKAGE, "g", null, null );
        assert !nf.accept( "a" );
        assert nf.accept( "g" );
        assert nf.accept( "m" );
        assert nf.accept( "z" );
        assert !nf.accept( "" );

        nf = new NameFilter( NameFilter.Type.PACKAGE, null, "p", null );
        assert nf.accept( "a" );
        assert nf.accept( "g" );
        assert nf.accept( "m" );
        assert nf.accept( "p" );
        assert !nf.accept( "q" );
        assert !nf.accept( "z" );
        assert nf.accept( "" );

        nf = new NameFilter( NameFilter.Type.PACKAGE, "g", "p", null );
        assert !nf.accept( "a" );
        assert nf.accept( "g" );
        assert nf.accept( "m" );
        assert nf.accept( "p" );
        assert !nf.accept( "q" );
        assert !nf.accept( "z" );
        assert !nf.accept( "" );

        nf = new NameFilter( NameFilter.Type.PACKAGE, "g", "p", "k" );
        assert !nf.accept( "a" );
        assert !nf.accept( "g" );
        assert nf.accept( "k" );
        assert !nf.accept( "m" );
        assert !nf.accept( "p" );
        assert !nf.accept( "q" );
        assert !nf.accept( "z" );
        assert !nf.accept( "" );

        nf = new NameFilter( NameFilter.Type.PACKAGE, null, "p", "k" );
        assert !nf.accept( "a" );
        assert !nf.accept( "g" );
        assert nf.accept( "k" );
        assert !nf.accept( "m" );
        assert !nf.accept( "p" );
        assert !nf.accept( "q" );
        assert !nf.accept( "z" );
        assert !nf.accept( "" );

        nf = new NameFilter( NameFilter.Type.PACKAGE, "g", null, "k" );
        assert !nf.accept( "a" );
        assert !nf.accept( "g" );
        assert nf.accept( "k" );
        assert !nf.accept( "m" );
        assert !nf.accept( "p" );
        assert !nf.accept( "q" );
        assert !nf.accept( "z" );
        assert !nf.accept( "" );

        nf = new NameFilter( NameFilter.Type.PACKAGE, null, null, "k" );
        assert !nf.accept( "a" );
        assert !nf.accept( "g" );
        assert nf.accept( "k" );
        assert !nf.accept( "m" );
        assert !nf.accept( "p" );
        assert !nf.accept( "q" );
        assert !nf.accept( "z" );
        assert !nf.accept( "" );
        return true;
    }

    public boolean acceptTest() {
        //Tested also in constructor test.
        NameFilter nf = new NameFilter( NameFilter.Type.PACKAGE, "a85.mammals.placental", null, null );
        assert nf.accept( "a85.mammals.placental" );
        assert nf.accept( "a85.reptiles.lizards." );
        assert nf.accept( "a85.reptiles.snakes." );

        return true;
    }

    public boolean allowsASinglePackageOnlyTest() {
        NameFilter nf = new NameFilter( NameFilter.Type.PACKAGE, null, null, null );
        assert !nf.allowsASinglePackageOnly();

        nf = new NameFilter( NameFilter.Type.PACKAGE, null, null, "a.b.c" );
        assert nf.allowsASinglePackageOnly();
        return true;
    }

    public boolean loggingMessageTest() {
        NameFilter nf = new NameFilter( NameFilter.Type.PACKAGE, null, null, null );
        assert nf.loggingMessage().equals( "" );
        nf = new NameFilter( NameFilter.Type.PACKAGE, "g", null, null );
        assert nf.loggingMessage().equals( "Only packages from g (inclusive) will be tested." );

        nf = new NameFilter( NameFilter.Type.PACKAGE, null, "p", null );
        assert nf.loggingMessage().equals( "Only packages to p (inclusive) will be tested." );

        nf = new NameFilter( NameFilter.Type.PACKAGE, "g", "p", null );
        assert nf.loggingMessage().equals( "Only packages between g and p (inclusive) will be tested." );

        nf = new NameFilter( NameFilter.Type.PACKAGE, "g", "p", "k" );
        assert nf.loggingMessage().equals( "Only the package k will be tested." );

        nf = new NameFilter( NameFilter.Type.PACKAGE, null, "p", "k" );
        assert nf.loggingMessage().equals( "Only the package k will be tested." );

        nf = new NameFilter( NameFilter.Type.PACKAGE, "g", null, "k" );
        assert nf.loggingMessage().equals( "Only the package k will be tested." );

        nf = new NameFilter( NameFilter.Type.PACKAGE, null, null, "k" );
        assert nf.loggingMessage().equals( "Only the package k will be tested." );


        nf = new NameFilter( NameFilter.Type.CLASS, null, null, null );
        assert nf.loggingMessage().equals( "" );
        nf = new NameFilter( NameFilter.Type.CLASS, "g", null, null );
        assert nf.loggingMessage().equals( "The test classes from g (inclusive) will be run." );

        nf = new NameFilter( NameFilter.Type.CLASS, null, "p", null );
        assert nf.loggingMessage().equals( "The test classes to p (inclusive) will be run." );

        nf = new NameFilter( NameFilter.Type.CLASS, "g", "p", null );
        assert nf.loggingMessage().equals( "The test classes between g and p (inclusive) will be run." );

        nf = new NameFilter( NameFilter.Type.CLASS, "g", "p", "k" );
        assert nf.loggingMessage().equals( "Only the test class k will be run." );

        nf = new NameFilter( NameFilter.Type.CLASS, null, "p", "k" );
        assert nf.loggingMessage().equals( "Only the test class k will be run." );

        nf = new NameFilter( NameFilter.Type.CLASS, "g", null, "k" );
        assert nf.loggingMessage().equals( "Only the test class k will be run." );

        nf = new NameFilter( NameFilter.Type.CLASS, null, null, "k" );
        assert nf.loggingMessage().equals( "Only the test class k will be run." );


        nf = new NameFilter( NameFilter.Type.METHOD, null, null, null );
        assert nf.loggingMessage().equals( "" );
        nf = new NameFilter( NameFilter.Type.METHOD, "g", null, null );
        assert nf.loggingMessage().equals( "The test methods from g (inclusive) will be run." );

        nf = new NameFilter( NameFilter.Type.METHOD, null, "p", null );
        assert nf.loggingMessage().equals( "The test methods to p (inclusive) will be run." );

        nf = new NameFilter( NameFilter.Type.METHOD, "g", "p", null );
        assert nf.loggingMessage().equals( "The test methods between g and p (inclusive) will be run." );

        nf = new NameFilter( NameFilter.Type.METHOD, "g", "p", "k" );
        assert nf.loggingMessage().equals( "Only the test method k will be run." );

        nf = new NameFilter( NameFilter.Type.METHOD, null, "p", "k" );
        assert nf.loggingMessage().equals( "Only the test method k will be run." );

        nf = new NameFilter( NameFilter.Type.METHOD, "g", null, "k" );
        assert nf.loggingMessage().equals( "Only the test method k will be run." );

        nf = new NameFilter( NameFilter.Type.METHOD, null, null, "k" );
        assert nf.loggingMessage().equals( "Only the test method k will be run." );
        return true;
    }

    public boolean equalsTest() {
        NameFilter nf1 = new NameFilter( NameFilter.Type.PACKAGE, null, null, null );
        NameFilter nf2 = new NameFilter( NameFilter.Type.PACKAGE, "i", null, null );
        NameFilter nf3 = new NameFilter( NameFilter.Type.PACKAGE, "i", "f", null );
        NameFilter nf4 = new NameFilter( NameFilter.Type.PACKAGE, "i", "f", "s" );
        //Check crazy things.
        assert !nf1.equals( null );
        assert !nf1.equals( "ffjjdvfs" );
        assert nf1.equals( nf1 );

        //Different first.
        assert !nf1.equals( new NameFilter( NameFilter.Type.PACKAGE, "j", null, null ) );
        assert !nf1.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", null, null ) );
        assert !nf2.equals( new NameFilter( NameFilter.Type.PACKAGE, null, null, null ) );
        //Different last.
        assert !nf1.equals( new NameFilter( NameFilter.Type.PACKAGE, null, "f", null ) );
        assert !nf2.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", "g", null ) );
        assert !nf2.equals( new NameFilter( NameFilter.Type.PACKAGE, null, null, null ) );
        assert !nf3.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", "g", null ) );
        assert !nf3.equals( new NameFilter( NameFilter.Type.PACKAGE, null, null, null ) );

        //Different single.
        assert !nf1.equals( new NameFilter( NameFilter.Type.PACKAGE, "j", null, "t" ) );
        assert !nf2.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", null, "t" ) );
        assert !nf3.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", null, "t" ) );
        assert !nf3.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", "f", "t" ) );
        assert !nf4.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", null, "t" ) );
        assert !nf4.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", "f", "t" ) );

        //Same single, different first.
        assert nf4.equals( new NameFilter( NameFilter.Type.PACKAGE, "j", "f", "s" ) );
        assert nf4.equals( new NameFilter( NameFilter.Type.PACKAGE, null, "f", "s" ) );

        //Same single, different last.
        assert nf4.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", null, "s" ) );
        assert nf4.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", "g", "s" ) );

        //Same single, different first and last.
        assert nf4.equals( new NameFilter( NameFilter.Type.PACKAGE, null, null, "s" ) );
        assert nf4.equals( new NameFilter( NameFilter.Type.PACKAGE, "j", "g", "s" ) );

        //Same limits but different type.
        assert !nf4.equals( new NameFilter( NameFilter.Type.CLASS, null, null, "s" ) );
        assert !nf4.equals( new NameFilter( NameFilter.Type.METHOD, null, null, "s" ) );
        assert !nf3.equals( new NameFilter( NameFilter.Type.CLASS, "i", "f", null ) );
        assert !nf3.equals( new NameFilter( NameFilter.Type.METHOD, "i", "f", null ) );

        //Same.
        assert nf1.equals( new NameFilter( NameFilter.Type.PACKAGE, null, null, null ) );
        assert nf2.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", null, null ) );
        assert nf3.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", "f", null ) );
        assert nf4.equals( new NameFilter( NameFilter.Type.PACKAGE, "i", "f", "s" ) );

        return true;
    }

    public boolean hashCodeTest() {
        NameFilter nf1 = new NameFilter( NameFilter.Type.PACKAGE, null, null, null );
        NameFilter nf2 = new NameFilter( NameFilter.Type.PACKAGE, "i", null, null );
        NameFilter nf3 = new NameFilter( NameFilter.Type.PACKAGE, "i", "f", null );
        NameFilter nf4 = new NameFilter( NameFilter.Type.PACKAGE, "i", "f", "s" );
        assert nf1.hashCode() == new NameFilter( NameFilter.Type.PACKAGE, null, null, null ).hashCode();
        assert nf2.hashCode() == new NameFilter( NameFilter.Type.PACKAGE, "i", null, null ).hashCode();
        assert nf3.hashCode() == new NameFilter( NameFilter.Type.PACKAGE, "i", "f", null ).hashCode();
        assert nf4.hashCode() == new NameFilter( NameFilter.Type.PACKAGE, "i", "f", "s" ).hashCode();
        return true;
    }

    public boolean toStringTest() {
        NameFilter nf1 = new NameFilter( NameFilter.Type.PACKAGE, null, null, null );
        NameFilter nf2 = new NameFilter( NameFilter.Type.PACKAGE, "i", null, null );
        NameFilter nf3 = new NameFilter( NameFilter.Type.PACKAGE, "i", "f", null );
        NameFilter nf4 = new NameFilter( NameFilter.Type.PACKAGE, "i", "f", "s" );
        assert nf1.toString().equals( "[PACKAGE, null, null, null]" ) : nf1.toString();
        assert nf2.toString().equals( "[PACKAGE, i, null, null]" ) : nf2.toString();
        assert nf3.toString().equals( "[PACKAGE, i, f, null]" ) : nf3.toString();
        assert nf4.toString().equals( "[PACKAGE, null, null, s]" ) : nf4.toString();
        return true;
    }
}