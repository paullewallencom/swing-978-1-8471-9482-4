package org.grandtestauto.test;

import java.io.File;

public class CFTestBase {
    protected File classesDir;

    protected void init( String archiveName, String packageName ) throws Exception {
        //Expand the zip archive into the temp directory.
        Helpers.cleanTempDirectory();
        File zip = new File( archiveName );
        Helpers.expandZipTo( zip, Helpers.tempDirectory() );
        classesDir = new File( Helpers.tempDirectory(), packageName.replace( '.', File.separatorChar ) );
    }
}
