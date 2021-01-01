package jet.testtools;

import java.io.*;
import java.nio.channels.*;
import java.util.zip.*;
import java.util.*;

/**
 * Contains a few handy methods not in the Java API or
 * in Apache commons-io, as discussed in Chapters 3
 * and 14.
 *
 * @author Tim Lavers
 */
public class Files {

    //Private constructor as this class has only static methods.
    private Files() {}

    /**
     * A handy location for tests to dump files.
     */
    public static File tempDir() {
        String userDir = System.getProperty( "user.dir" );
        File result = new File( userDir, "temp" );
        result.mkdirs();
        return result;
    }

    /**
     * A handy location for tests to dump files, c
     * @return
     */
    public static File cleanedTempDir() {
        cleanDirectory( tempDir() );
        return tempDir();
    }

    public static void cleanDirectory( File dir ) {
        File[] contents = dir.listFiles();
        for (File content : contents) {
            if (!content.isFile()) {
                cleanDirectory( content );
            }
            boolean deleted = content.delete();
            if (!deleted) {
                //Give whatever process is using the file a chance to complete.
                Waiting.pause( 600 );
                deleted = content.delete();
            }
            if (!deleted) {
                System.out.println( "Can't delete: " + content + " exists: " + content.exists() );
            }
            assert deleted : "Could not delete: " + content;
        }
    }

    public static File copyTo( File sourceFile, File destinationDirectory ) throws IOException {
        return copyToFile( sourceFile,
                new File( destinationDirectory, sourceFile.getName() ) );
    }

    public static File copyToFile( File sourceFile, File destinationFile ) throws IOException {
        FileChannel sourceChannel = new FileInputStream( sourceFile ).getChannel();
        FileChannel destinationChannel = new FileOutputStream( destinationFile ).getChannel();
        sourceChannel.transferTo( 0, sourceFile.length(), destinationChannel );
        sourceChannel.close();
        destinationChannel.close();
        return destinationFile;
    }

    public static String contents( File file ) {
        StringBuffer resultBuff = new StringBuffer();
        try {
            BufferedReader buff = new BufferedReader( new FileReader( file ) );
            int nextChar;
            while ((nextChar = buff.read()) != -1) {
                resultBuff.append( (char) nextChar );
            }
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Could not read file: '"
                    + file.getPath() + "', as shown.";
        }
        return resultBuff.toString();
    }

    public static void writeIntoFile( String str, File file ) {
        try {
            BufferedWriter bw = new BufferedWriter( new FileWriter( file ) );
            bw.write( str );
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Could not write to file: '"
                    + file.getPath() + "', as shown.";
        }
    }

    public static void zip( File[] filesToZip, File zippedFile ) {
        try {
            ZipOutputStream zipOut = new ZipOutputStream( new FileOutputStream( zippedFile ));
            for (File file : filesToZip) {
                ZipEntry entry = new ZipEntry( file.getName() );
                zipOut.putNextEntry( entry );
                BufferedInputStream sourceStream = new BufferedInputStream( new FileInputStream( file ) );
                byte[] buff = new byte[1024];
                int n;
                while ( (n = sourceStream.read( buff )) != -1) {
                    zipOut.write( buff, 0, n );                    
                }
                zipOut.closeEntry();
                sourceStream.close();
            }
            zipOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Zip failure, as shown";
        }
    }

    public static void unzip( String zipName, File destination )  {
        org.grandtestauto.test.Helpers.expandZipTo( new File( zipName ), destination );
    }

    public static boolean equalZips( File zip1,
                                     File zip2 ) throws Exception {
        return nameToContents( zip1 ).equals( nameToContents( zip2 ) );
    }

    private static Map<String, String> nameToContents( File zipFile ) throws Exception {
        ZipInputStream zis = new ZipInputStream( new FileInputStream( zipFile ) );
        Map<String, String> map = new HashMap<String, String>();
        ZipEntry entry;
        while (((entry = zis.getNextEntry()) != null)) {
            if (entry.isDirectory()) continue;
            StringBuilder sb = new StringBuilder();
            BufferedInputStream buffIn = new BufferedInputStream( zis );
            int n;
            while ((n = buffIn.read()) != -1) {
                sb.append( (char) n );
            }
            map.put( entry.getName(), sb.toString() );
        }
        zis.close();
        return map;
    }
}
