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

import java.io.*;
import java.util.logging.*;

/**
 * Searches a package for classes according to some criterion.
 *
 * @author Tim Lavers
 */
public abstract class ClassFinder {

    private File classesDir;
    private String packageName;

    /**
     * Accepts class files.
     */
    private static FileFilter classFileAccepter = new FileFilter() {
        public boolean accept( File f ) {
            return f.isFile() && f.getName().indexOf( ".class" ) > 0;
        }
    };

    /**
     * Creates a <code>ClassFinder</code> that searches the directory <code>classesDir</code>.
     */
    public ClassFinder( String packageName, File classesDir ) {
        this.classesDir = classesDir;
        this.packageName = packageName;
    }

    public void seek() {
        //Look for the classes.
        File[] classFiles = classesDir.listFiles( classFileAccepter );
        for (int i = 0; i < classFiles.length; i++) {
            //Add the class name, having stripped off the ".class".
            String className = classFiles[i].getName();
            className = className.substring( 0, className.length() - 6 );
            processClass( className );
        }
    }

    public abstract boolean foundSomeClasses();

    /**
     * Deal with the given class.
     */
    public abstract void processClass( String relativeName );

    Class classFor( String relativeName ) {
        String fullName = packageName + "." + relativeName;
        Class result = null;
        try {
            result = Class.forName( fullName );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            String message = Messages.message( Messages.OPK_COULD_NOT_FIND_CLASS, fullName );
            Logger.getAnonymousLogger().log( Level.WARNING, message );
        }
        return result;
    }
}