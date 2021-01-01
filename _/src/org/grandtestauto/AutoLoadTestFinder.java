package org.grandtestauto;

import java.io.File;
import java.lang.reflect.Modifier;

/**
 * Finds the test classes in a test package.
 *
 * @author Tim Lavers
 */
public class AutoLoadTestFinder extends TestFinder {

    /**
     * Creates a <code>AutoLoadTestFinder</code> that searches the directory
     * <code>classesDir</code> for test classes.
     */
    public AutoLoadTestFinder( String packageName, File classesDir ) {
        super( packageName, classesDir );
    }

    public void processClass( String relativeName ) {
        Class<?> klass = classFor( relativeName );
        int m = klass.getModifiers();
        if (Modifier.isPublic( m ) && !Modifier.isAbstract( m )) {
            if (AutoLoadTest.class.isAssignableFrom( klass )) {
                boolean hasNoArgsConstructor = false;
                try {
                    klass.getDeclaredConstructor( ) ;
                    hasNoArgsConstructor = true;//No-args constructor found.
                } catch (NoSuchMethodException e) {
                    //Guess it's not there.
                }
                if (hasNoArgsConstructor) {
                    relevantClassNames.add( relativeName );
                }
            }
        }
    }
}
