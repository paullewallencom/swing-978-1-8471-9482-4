/****************************************************************************
 *
 * Name: Accountant.java
 *
 * Synopsis: See javadoc class comments.
 *
 * Description: See javadoc class comments.
 *
 * Copyright 2002 Timothy Gordon Lavers (Australia)
 *
 *                          The Wide Open License (WOL)
 *
 * Permission to use, copy, modify, distribute and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice and this license appear in all source copies.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT EXPRESS OR IMPLIED WARRANTY OF
 * ANY KIND. See http://www.dspguru.com/wol.htm for more information.
 *
 *****************************************************************************/
package org.grandtestauto;

import java.lang.reflect.*;
import java.util.*;

/**
 * Checks that all of the testable classes in a package are
 * tested and that each testable method of each class is tested.
 *
 * @author Tim Lavers
 */
class Accountant {

    /**
     * Methods for which a test method was expected but not found.
     */
    private Set<Method> untestedMethods = new HashSet<Method>();

    /**
     * Methods for which a test has been found.
     */
    private Set<Method> testedMethods = new HashSet<Method>();

    /**
     * Constructors for which no test has been found.
     */
    private Set<Constructor> untestedConstructors = new HashSet<Constructor>();

    /**
     * Record that a test method for <code>m</code> was sought but not found.
     */
    void noTestFound( Method m ) {
        //The method may already have been tested (it was
        //declared in a superclass and tested in the superclass's
        //unit test or the unit test for another descendant).
        //Only if it is not already found do we worry.
        if (!testedMethods.contains( m )) {
            untestedMethods.add( m );
        }
    }

    /**
     * Record that a test method for <code>m</code> was sought and found.
     */
    void testFound( Method m ) {
        //If it's recorded as untested, rectify this.
        if (untestedMethods.contains( m )) {
            untestedMethods.remove( m );
        }
        testedMethods.add( m );
    }

    void recordAsNeedingTest( Constructor c ) {
        untestedConstructors.add( c );
    }

    void recordAsTested( Constructor c ) {
        untestedConstructors.remove( c ); 
    }

    /**
     * The methods that have not been tested.
     */
    Set<Method> untestedMethods() {
        return untestedMethods;
    }

    Set<Constructor> untestedConstructors() {
        return untestedConstructors;
    }
}