package jet.testtools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author Lindsay Peters
 */
public class TestDataClassGenerator {
    public static final String TEST_DATA_ROOT_SYSTEM_PROPERTY_KEY = "TestDataRoot";
    private static final String EXTENSION = ".java";
    private static final String VSS_FILE_NAME = "vssver.scc";
    private static final String PATH_VARIABLE = "PATH";

    private static String FILE_SEPARATOR;

    private static String rootSrcDirectoryName;
    private File dataDirectory;
    private File testDataRootDirectory;

    private String packageName;
    private ArrayList<FileGenerator> fileGenerators = new ArrayList<FileGenerator>();

    static {
        FILE_SEPARATOR = File.separator;
        if (FILE_SEPARATOR.equals( "\\")) {
            FILE_SEPARATOR = "\\\\";
        }
    }

    /**
     * Recursively generates a class file, in the specified package, that contains String contants for data file names
     * found in the specified data directory.
     *
     * @param packageName   the package for the generated class file
     * @param dataDirectory the directory for the test data files
     * @throws IOException if any of the files cannot be written.
     */
    private TestDataClassGenerator(String packageName, File testDataRootDirectory, File dataDirectory) throws IOException {
        this.packageName = packageName.toLowerCase();
        this.dataDirectory = dataDirectory;
        this.testDataRootDirectory = testDataRootDirectory;
        File[] files = dataDirectory.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                String subPackageName = packageName + "." + f.getName();
                new TestDataClassGenerator(subPackageName, testDataRootDirectory, f);
            } else {
                //Don't bother generating an entry for VSS files
                if (!f.getName().equals(VSS_FILE_NAME)) {
                    fileGenerators.add(new FileGenerator(f));
                 }
            }
        }
        toClassFile();
    }

    private static String fileNameFromPackageName(String packageName) {
        String name = packageName.replace(".", FILE_SEPARATOR);
        return rootSrcDirectoryName + FILE_SEPARATOR + name + FILE_SEPARATOR;
    }

    private void toClassFile() throws IOException {
        String classDirName = fileNameFromPackageName(packageName);
        File directoryForClassFile = new File(classDirName);
        directoryForClassFile.mkdirs();
        File classFile = new File(directoryForClassFile, toClassName() + EXTENSION);
        BufferedWriter writer = new BufferedWriter(new FileWriter(classFile));

        writePackageName(writer);
        writeStartOfClass(writer);
        writePathVariable(writer);
        for (FileGenerator fg : fileGenerators) {
            writer.write(fg.instanceForDataFile());
            writer.newLine();
        }
        writeEndOfClass(writer);
        writer.close();
    }

    private void writePathVariable(BufferedWriter writer) throws IOException {
        writer.write("public static String ");
        writer.write(PATH_VARIABLE);
        writer.write(" = System.getProperty( \"" + TEST_DATA_ROOT_SYSTEM_PROPERTY_KEY + "\" )  + \"");
        String pathToDataDirectory = dataDirectory.getCanonicalPath();
        String pathToDataRootDirectory = testDataRootDirectory.getCanonicalPath();
        String relativePath = pathToDataDirectory.substring( pathToDataRootDirectory.length() );
        relativePath = relativePath.replace("\\", "\\\\");
        writer.write( relativePath );
        writer.write( "\\\\\";" );

        //On a Windows system, the path will include \ chars which
        //need to be escaped.
//        pathToDataDirectory = pathToDataDirectory.replace("\\", "\\\\");
//        writer.write(pathToDataDirectory);
//        writer.write(FILE_SEPARATOR);
//        writer.write("\";");
        writer.newLine();
        writer.newLine();
    }

    private void writeStartOfClass(BufferedWriter writer) throws IOException {
        writer.write("public class ");
        writer.write(toClassName());
        writer.write(" {");
        writer.newLine();
        writer.newLine();
    }

    private void writeEndOfClass(BufferedWriter writer) throws IOException {
        writer.newLine();
        writer.write('}');
    }


    private void writePackageName(BufferedWriter writer) throws IOException {
        writer.write("package ");
        writer.write(toLegalPackageName(packageName));
        writer.write(';');
        writer.newLine();
        writer.newLine();
    }

    private String toLegalPackageName(String packageName) {
        return packageName.replaceAll(" ", "_");
    }

    private String toClassName() {
        //remove illegal characters
        String str = toLegalJavaFieldName(dataDirectory.getName());

        //capitalise the first letter
        return str.toUpperCase().substring(0, 1) + str.substring(1, str.length());
    }

    private static String toLegalJavaFieldName(String str) {
        str = str.replace(".", "_");
        str = str.replace("-", "_");
        str = str.replace(" ", "_");

        //if the filename starts with a numeric, prefix it with an alpha character so that we have a legal field name
        try {
            Integer.parseInt(str.substring(0, 1));
            //NOT OK
            str = "a_" + str;
        } catch (NumberFormatException e) {
            //OK
        }
        return str;
    }

    private class FileGenerator {
        private File file;

        public FileGenerator(File file) {
            this.file = file;
        }

        public String instanceForDataFile() {
            StringBuilder sb = new StringBuilder();
            sb.append("public static final String ");
            sb.append(toLegalJavaFieldName(file.getName()));
            sb.append(" = ");
            sb.append(PATH_VARIABLE);
            sb.append(" + ");
            sb.append("\"");
            sb.append(file.getName());
            sb.append("\"");
            sb.append(';');
            return sb.toString();
        }

        public String toString() {
            return instanceForDataFile();
        }
    }

    /**
     * @param args - Three arguments are required: the root source directory
     *             the root package for generated class files and the root test data directory.
     */
    public static void main(String[] args) {
        rootSrcDirectoryName = args[0];
        String rootTestDataPackageName = args[1];
        try {
            File dataDirectory = new File(args[2]);
            new TestDataClassGenerator(rootTestDataPackageName, dataDirectory, dataDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}