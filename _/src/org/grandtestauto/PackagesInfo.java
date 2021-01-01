/****************************************************************************
*
* Name: PackagesInfo.java
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

import java.io.*;
import java.util.*;

/**
 * Encapsulates information about the packages that make up a project.
 *
 * @author Tim Lavers
 */
public abstract class PackagesInfo<T extends ClassFinder> {

    public interface Filter {
        boolean accept( String packageName );
    }

    /** Selects directories. */
	private static FileFilter dirFilter = new FileFilter() {
		public boolean accept( File f ) {
			return f.isDirectory();
		}
	};

	/** The root of the classes directory. */
	private File classesRoot;

	/** Maps package names to info about them. */
	private SortedMap<String, T> packageNamesToInfo = new TreeMap<String, T>();

    private Filter filter;

    /**
	 * Returns true if the given package name does not end with ".test",
     * and does not name a function or load test package.
	 */
	public static boolean namesPackageThatMightNeedUnitTests( String packageName ) {
		if (packageName.endsWith( ".test" )) return false;
        if (namesFunctionTestPackage( packageName )) return false;
        if (namesLoadTestPackage( packageName )) return false;
        if (packageName.contains( ".test.")) return false;
        if (packageName.contains( ".functiontest.")) return false;
        if (packageName.contains( ".loadtest.")) return false;
        return true;
    }

    /**
     * Returns true if the given package name ends with ".functiontest".
     */
    public static boolean namesFunctionTestPackage( String packageName ) {
        return packageName.endsWith( ".functiontest");
    }

    /**
     * Returns true if the given package name ends with ".loadtest".
     */
    public static boolean namesLoadTestPackage( String packageName ) {
        return packageName.endsWith( ".loadtest");
    }

    /**
	 * Creates a <code>PackagesInfo</code> that searches the
	 * directory tree rooted at <code>classesRoot</code> for
	 * information.
	 */
	public PackagesInfo( Filter filter, File classesRoot ) {
        this.filter = filter;
        this.classesRoot = classesRoot;
        //Collect classes info from all subdirs.
		File[] subDirs = classesRoot.listFiles( dirFilter );
        for (int i=0; i<subDirs.length; i++) {
            addSubPackages( subDirs[i] );
        }
	}

    /**
     * The root of the classes directory.
     */
    public File classesRoot() {
        return classesRoot;
    }

	/**
	 * The names of the non-test packages.
	 */
	public Set<String> testablePackageNames() {
		return packageNamesToInfo.keySet();
	}

	/**
	 * An information object for the named package.
	 */
	public T packageInfo( String packageName ) {
		return packageNamesToInfo.get( packageName );
	}

    public abstract T createClassFinder( String packageName, File baseDir );

    /**
	 * For the given directory, get the list of class files.
	 * If this is non-empty, add the information about these
	 * classes. Recursively apply the method to any subdirectories.
	 */
	private void addSubPackages( File baseDir ) {
		String packName = baseDir.getPath();
		packName = packName.substring( classesRoot.getPath().length() + 1 );
		packName = packName.replace( File.separatorChar, '.' );
		if (filter.accept( packName )) {
			T pi = createClassFinder( packName, baseDir );
            pi.seek();
            if (pi.foundSomeClasses()) {
                packageNamesToInfo.put( packName, pi );
			}
        }
        File[] subDirs = baseDir.listFiles( dirFilter );
        for (int i=0; i<subDirs.length; i++) {
            addSubPackages( subDirs[i] );
        }
    }
}