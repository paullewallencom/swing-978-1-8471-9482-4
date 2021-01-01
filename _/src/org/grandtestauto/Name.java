package org.grandtestauto;

import java.util.*;
import java.io.*;

public class Name implements Comparable<Name> {
    private String[] parts;

    public static SortedSet<Name> subDirectoriesAsNames( File root ) {
        SortedSet<Name> result = new TreeSet<Name>();
        addSubdirectoriesToDirectoryList( result, null, root );
        return result;
    }

    private static void addSubdirectoriesToDirectoryList( Set<Name> list, final Name base, File baseDir ) {
        File[] subDirs = baseDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory();
            }
        } );
        if (subDirs == null) return;
        for (File subDir : subDirs) {
            Name newName = base == null ? new Name( subDir.getName() ) : new Name( base, subDir.getName() );
            addSubdirectoriesToDirectoryList( list, newName, subDir );
            list.add( newName );
        }
    }

    public Name( String name ) {
        parts = name.split( "\\." );
    }

    Name( Name base, String newPart ) {
        parts = new String[base.parts.length + 1 ];
        System.arraycopy( base.parts, 0, parts, 0, base.parts.length );
        parts[parts.length -1 ] = newPart;
    }

    public boolean matches( Name other ) {
        //Cannot match if the other has fewer parts.
        if (other.parts.length < parts.length) return false;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            String otherPart = other.parts[i];
            if (!otherPart.startsWith( part )) return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder( "");
        boolean first = true;
        for (String part: parts) {
            if (first) {
                first = false;
            } else {
                sb.append( "." );
            }
            sb.append( part );
        }
        return sb.toString();
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Name name = (Name) o;

        if (!Arrays.equals( parts, name.parts )) return false;

        return true;
    }

    public int hashCode() {
        return Arrays.hashCode( parts );
    }

    public int compareTo( Name o ) {
        int n = Math.min( parts.length, o.parts.length);
        int i = 0;
        while (i < n) {
            int diff = parts[i].compareTo( o.parts[i]);
            i++;
            if (diff != 0) return diff;
        }
        //To get to this point, the names are equal along their shared length,
        //so return the difference in lengths.
        return parts.length - o.parts.length;
    }
}
