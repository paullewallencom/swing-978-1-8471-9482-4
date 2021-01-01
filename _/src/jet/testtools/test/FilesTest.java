package jet.testtools.test;

import jet.testtools.*;
import jet.testtools.test.testtools.*;
import jet.testtools.test.ikonmaker.*;

import java.io.*;

public class FilesTest {
    public boolean tempDirTest() {
        File temp = Files.tempDir();
        assert temp.exists();
        assert temp.isDirectory();
        File userDir = new File( System.getProperty( "user.dir" ) );
        assert temp.getParentFile().equals( userDir );
        assert temp.getName().equals( "temp" );
        return true;
    }

    public boolean cleanedTempDirTest() throws IOException {
        File temp = Files.cleanedTempDir();
        assert temp.exists();
        assert temp.isDirectory();
        File userDir = new File( System.getProperty( "user.dir" ) );
        assert temp.getParentFile().equals( userDir );
        assert temp.getName().equals( "temp" );
        File[] files = temp.listFiles();
        assert files.length == 0;

        //Put some files in.
        File f1 = new File( temp, "file1" );
        f1.createNewFile();
        assert f1.exists();
        File subDir = new File( temp, "subdir" );
        subDir.mkdirs();
        File f2 = new File( subDir, "file2" );
        f2.createNewFile();
        assert f2.exists();
        File subsubDir = new File( subDir, "subsubDir" );
        subsubDir.mkdirs();
        File f3 = new File( subsubDir, "file3" );
        f3.createNewFile();
        assert f3.exists();
        files = temp.listFiles();
        assert files.length == 2;//f1 and subDir

        //Get it again, check all files gone.
        temp = Files.cleanedTempDir();
        assert temp.exists();
        assert temp.isDirectory();
        assert temp.getParentFile().equals( userDir );
        assert temp.getName().equals( "temp" );
        files = temp.listFiles();
        assert files.length == 0;
        assert !f1.exists();
        assert !f2.exists();
        assert !f3.exists();
        assert !subDir.exists();
        assert !subsubDir.exists();

        return true;
    }

    public boolean cleanDirectoryTest() throws IOException {
        File temp = Files.cleanedTempDir();
        //Put some files in.
        File f1 = new File( temp, "file1" );
        f1.createNewFile();
        assert f1.exists();
        File subDir = new File( temp, "subdir" );
        subDir.mkdirs();
        File f2 = new File( subDir, "file2" );
        f2.createNewFile();
        assert f2.exists();
        File subsubDir = new File( subDir, "subsubDir" );
        subsubDir.mkdirs();
        File f3 = new File( subsubDir, "file3" );
        f3.createNewFile();
        assert f3.exists();
        File[] files = temp.listFiles();
        assert files.length == 2;//f1 and subDir

        //Clean it, check all files gone.
        Files.cleanDirectory( temp );
        assert temp.exists();
        assert temp.isDirectory();
        assert temp.getName().equals( "temp" );
        files = temp.listFiles();
        assert files.length == 0;
        assert !f1.exists();
        assert !f2.exists();
        assert !f3.exists();
        assert !subDir.exists();
        assert !subsubDir.exists();

        return true;
    }

    public boolean writeIntoFileTest() {
        testWith( "" );
        testWith( "Madam, I'm Adam" );
        testWith( Files.contents( new File( Testtools.a_24hrs10_txt ) ) );
        return true;
    }

    public boolean contentsTest() {
        String bennett = Files.contents( new File( Testtools.a_24hrs10_txt ) );
        assert bennett.length() == 87142;
        assert bennett.startsWith( "Project Gutenberg Etext How to Live on 24 Hours a Day, by Bennett");
        assert bennett.endsWith( "End of Project Gutenberg Etext How to Live on 24 Hours a Day, by Bennett" + StringUtils.CRLF + StringUtils.CRLF );
        return true;
    }

    private void testWith( String str ) {
        File tempDir = Files.cleanedTempDir();
        File f = new File( tempDir, "junk1.txt" );
        Files.writeIntoFile( str, f );
        assert Files.contents( f ).equals( str );
    }

    public boolean equalZipsTest() throws Exception {
        //Different numbers of files.
        assert !Files.equalZips( new File( Testtools.zip1_zip ), new File( Testtools.zip2_zip ) );
        //File contents differ.
        assert !Files.equalZips( new File( Testtools.zip1_zip ), new File( Testtools.zip3_zip ) );
        //Same.
        assert Files.equalZips( new File( Testtools.zip1_zip ), new File( Testtools.zip4_zip ) );
        return true;
    }

    public boolean copyToTest() throws IOException {
        File temp = Files.cleanedTempDir();
        File waratah = new File( Ikonmaker.waratah_jpg );
        assert waratah.length() == 278515 : "Actual length: " + waratah.length();
        File copy = Files.copyTo( waratah, temp );
        long copyLength = copy.length();
        assert copyLength == 278515 : "Actual length: " + copyLength;
        return true;
    }

    public boolean copyToFileTest() throws IOException {
        File tempDir = Files.cleanedTempDir();
        File temp = new File( tempDir, "Junk");
        assert temp.length() == 0;
        File waratah = new File( Ikonmaker.waratah_jpg );
        assert waratah.length() == 278515 : "Actual length: " + waratah.length();
        File copy = Files.copyToFile( waratah, temp );
        long copyLength = copy.length();
        assert copyLength == 278515 : "Actual length: " + copyLength;
        assert temp.length() == 278515;

        File temp2 = new File( tempDir, "MoreJunk");
        assert temp2.length() == 0;
        Files.writeIntoFile( "This is more junk", temp2 );
        assert temp2.length() > 0;
        assert temp2.length() < 278515;
        copy = Files.copyToFile( waratah, temp2 );
        copyLength = copy.length();
        assert copyLength == 278515 : "Actual length: " + copyLength;
        assert temp2.length() == 278515;

        return true;
    }

    public boolean unzipTest() throws IOException {
        File temp = Files.cleanedTempDir();
        Files.unzip( Testtools.input_zip, temp );
        File[] tempFiles = temp.listFiles();
        assert tempFiles.length == 3;
        for (File tempFile : tempFiles) {
            assert tempFile.getName().startsWith( "inputfile" );
            assert Files.contents( tempFile ).startsWith( "data for data file" );
        }
        return true;
    }

    public boolean zipTest() throws Exception {
        File tempDir = Files.cleanedTempDir();
        //1 text file to zip.
        File[] filesToZip = new File[1];
        filesToZip[0] = new File( Testtools.a_24hrs10_txt );
        File zip = new File( tempDir, "zip1.zip");
        Files.zip( filesToZip, zip );
        File checkDir = new File( tempDir,  "CheckDir" );
        checkDir.mkdirs();
        checkZip( checkDir, zip, filesToZip );
        
        //1 binary to zip.
        Files.cleanDirectory( tempDir );
        filesToZip = new File[1];
        filesToZip[0] = new File( Ikonmaker.waratah_jpg );
        zip = new File( tempDir, "zip1.zip");
        Files.zip( filesToZip, zip );
        checkDir = new File( tempDir,  "CheckDir" );
        checkDir.mkdirs();
        checkZip( checkDir, zip, filesToZip );
        return true;
    }

    private void checkZip( File tempDir, File zip, File[] expectedContents ) throws Exception {
        assert tempDir.list().length == 0 : "Temp dir not empty.";
        Files.unzip( zip.getAbsolutePath(),  tempDir );
        File[] unzipped = tempDir.listFiles();
        assert unzipped.length == expectedContents.length;
        for (int i = 0; i < unzipped.length; i++) {
            checkFilesAreTheSame( unzipped[i], expectedContents[i]);
        }
    }

    private void checkFilesAreTheSame( File f, File g ) throws Exception {
        assert f.length() == g.length() : "Lengths differ: " + f.length() + ", " + g.length() ;
        assert f.getName().equals( g.getName() );
        FileInputStream fout = new FileInputStream( f );
        FileInputStream gout = new FileInputStream( g );
        int nextf;
        int nextg;
        while ((nextf = fout.read()) != -1) {
            nextg = gout.read();
            assert nextf == nextg;
        }
        fout.close();
        gout.close();
    }
}
