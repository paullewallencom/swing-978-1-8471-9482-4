package jet.testtools.test;

import jet.testtools.*;
import jet.testtools.test.testtools.*;

import javax.tools.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

public class TestDataClassGeneratorTest {

    public boolean mainTest() throws Exception {
        //Clear out the temp directory.
        File tempDir = Files.cleanedTempDir();
        //In the temp dir create a directory testdata.
        File testDataRoot = new File( tempDir, "testdata" );
        testDataRoot.mkdir();
        //In the testdata dir create directory a.
        File ta = new File( testDataRoot, "a" );
        ta.mkdir();
        //In directory a create directories b and c.
        File tab = new File( ta, "b" );
        tab.mkdir();
        File tac = new File( ta, "c" );
        tac.mkdir();

        //Copy some files into a, a.b and a.c.
        Files.copyTo( new File( Testtools.a_24hrs10_txt ), ta );
        Files.copyTo( new File( Testtools.a_24hrs10_txt ), tab );
        Files.copyTo( new File( Testtools.a_24hrs10_txt ), tac );
        Files.copyTo( new File( Testtools.input_zip ), tac );

        //In the temp dir create a directory src with packages
        //a.b and a.c, with one or two classes in each.
        final File srcDir = new File( tempDir, "src" );
        srcDir.mkdir();

        File sa = new File( srcDir, "a" );
        sa.mkdir();
        File sab = new File( srcDir, "b" );
        sab.mkdir();
        File sac = new File( srcDir, "c" );
        sac.mkdir();

        //Call the main method, specifying a package prefix of "tdcg_test".
        TestDataClassGenerator.main( new String[]{srcDir.getPath(), "tdcg_test", testDataRoot.getPath()} );

        //Inspect the generated files to see that they contain the expected constants.
        final File generatedSrcRoot = new File( srcDir, "tdcg_test" );
        assert generatedSrcRoot.exists();
        File testDataMain = new File( generatedSrcRoot, "Testdata.java" );
        assert Files.contents( testDataMain ).contains( "public static String PATH =" );

        File ga = new File( generatedSrcRoot, "a" );
        assert ga.exists();
        File gaA = new File( ga, "A.java" );
        assert Files.contents( gaA ).contains( "public static final String a_24hrs10_txt = PATH + \"24hrs10.txt\";" );

        File gab = new File( ga, "b" );
        assert gab.exists();
        File gabB = new File( gab, "B.java" );
        assert Files.contents( gabB ).contains( "public static final String a_24hrs10_txt = PATH + \"24hrs10.txt\";" );

        File gac = new File( ga, "c" );
        assert gac.exists();
        File gacC = new File( gac, "C.java" );
        assert Files.contents( gacC ).contains( "public static final String a_24hrs10_txt = PATH + \"24hrs10.txt\";" );
        assert Files.contents( gacC ).contains( "public static final String input_zip = PATH + \"input.zip\";" );

        //Check that there are no problems compiling these files.
        File[] files1 = new File[]{testDataMain, gaA, gabB, gacC};
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager( null, null, null );
        DiagnosticCollector<? super JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        Iterable<? extends JavaFileObject> compilationUnits1 =
                fileManager.getJavaFileObjectsFromFiles( Arrays.asList( files1 ) );
        compiler.getTask( null, fileManager, diagnostics, null, null, compilationUnits1 ).call();

        assert diagnostics.getDiagnostics().isEmpty(): "Problems found: " +diagnostics.getDiagnostics();

        //Build class loader to load these classes.
        ClassLoader classLoader = new ClassLoader() {
            protected Class<?> findClass( String name ) throws ClassNotFoundException {
                String relativeName = name.replaceAll( "\\.", Matcher.quoteReplacement( File.separator ) ) + ".class";
                File classFile = new File( srcDir, relativeName );
                byte[] classData = new byte[(int) classFile.length()];
                try {
                    FileInputStream reader = new FileInputStream( classFile );
                    reader.read( classData );
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new ClassNotFoundException( "Could not find: " + name );
                }
                return defineClass(name, classData, 0, classData.length);
            }
        };
        //This is a little complicated....the path that is recovered
        //from the generated classes depends on a system property "TestDataRoot"
        //that is one of the JVM parameters for the JVM running these tests.
        //We need to switch the value of this system property to be the
        //root directory for the system of test data that we've just created.
        //So first we remember the real value...
        String realTestDataRoot = System.getProperty( TestDataClassGenerator.TEST_DATA_ROOT_SYSTEM_PROPERTY_KEY );
        //...and then we set the temporary value.
        System.setProperty( TestDataClassGenerator.TEST_DATA_ROOT_SYSTEM_PROPERTY_KEY, testDataRoot.getAbsolutePath() );
        //Check the fields for one of the classes.
        Class classA = Class.forName( "tdcg_test.a.A", true, classLoader );
        Field dataFileField = classA.getField( "a_24hrs10_txt" );
        String dataFilePath = (String) dataFileField.get( null );
        File recovered =new File( dataFilePath );
        Assert.equal(Files.contents( recovered ), Files.contents(new File( Testtools.a_24hrs10_txt ) ));

        //We need to re-set the TestDataRoot system property.
        System.setProperty( TestDataClassGenerator.TEST_DATA_ROOT_SYSTEM_PROPERTY_KEY, realTestDataRoot );

        //We want to be able to remove the class and src files (otherwise later
        //tests will fail as they cannot clean the temp directory). To be able
        //to remove the class file we need null all references to the class
        //and the class loader whence it came.
        recovered = null;
        dataFilePath = null;
        dataFileField = null;
        classA = null;
        classLoader = null;
        System.gc();
        System.gc();
        System.gc();
        Files.cleanDirectory( Files.tempDir() );

        return true;
    }
}