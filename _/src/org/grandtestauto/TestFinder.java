/****************************************************************************
 *
 * Name: PackageInfo.java
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

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.TreeSet;
import java.util.SortedSet;

/**
 * Finds the test classes in a test package.
 *
 * @author Tim Lavers
 */
public class TestFinder extends ClassFinder {

    /** The names of the classes found in the package. */
    SortedSet<String> relevantClassNames= new TreeSet<String>();

    /**
     * Creates a <code>ClassFinder</code> that searches the directory <code>classesDir</code> for test classes.
     */
    public TestFinder( String packageName, File classesDir ) {
        super( packageName, classesDir );
    }

    public void processClass( String relativeName ) {
        Class klass = classFor( relativeName );
        int m = klass.getModifiers();
        if (Modifier.isPublic( m ) && !Modifier.isAbstract( m ) && klass.getName().endsWith( "Test" )) {
            relevantClassNames.add( relativeName );
        }
    }

    public boolean foundSomeClasses() {
        return relevantClassNames.size() > 0;
    }

    /**
     * The (relative) names of the classes in the package, in alphabetical order.
     */
    public SortedSet<String> classesInPackage() {
        return relevantClassNames;
    }
}