package jet.testtools.help;

import jet.testtools.*;
import org.w3c.dom.*;

import javax.help.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map;
import java.util.regex.*;

/**
 * This class is discussed extensivel in Chapter 11.
 * 
 * @author Lindsay Peters
 */
public class HelpGenerator {
    public static final String TARGET_FILE_NAME = "Help";
    public static final String MAP_FILE_NAME = "HelpMap.jhm";
    public static final String HELP_INDEX_FILE_NAME = "HelpIndex.xml";
    public static final String JAVA_EXTN = ".java";
    public static final String HTML_EXTN = ".html";
    public static final String MAP_TAG_NAME = "map";
    public static final String MAP_ID_TAG_NAME = "mapID";
    public static final String TARGET_TAG_NAME = "target";
    public static final String URL_TAG_NAME = "url";

    private static final String INDEX_ITEM_TAG = "indexitem";
    private static final String HELPSET_NAME = "jet/testtools/help/HelpSet.hs";
    private static HelpBroker broker;
    private static final String TARGET_TAG = "target";
    private static final String INDEX_TEXT_TAG = "text";

    private File mapFile;
    private File topicsFile;
    private File helpDirectory;
    private String packageName;
    private File indexFile;
    public Map<String, String> targetToIndexText;

    /**
     * Generates a class file, in the specified package, that contains String contants for HTML file names
     * found in the specified data directory.
     *
     * @param packageName       the package for the generated class file
     * @param helpDirectoryName the directory for the HTML data files
     */
    public HelpGenerator( String packageName, String helpDirectoryName ) throws IOException {
        this.packageName = packageName.toLowerCase();
        helpDirectory = new File( helpDirectoryName );

        assert helpDirectory.exists() : "help directory '" + helpDirectory.getPath() + "' does not exist";

        indexFile = new File( helpDirectory, HELP_INDEX_FILE_NAME );
        mapFile = new File( helpDirectory, MAP_FILE_NAME );

        //Delete the existing Help.java so it can be re-written.
        new File( helpDirectory, TARGET_FILE_NAME + JAVA_EXTN ).delete();
        topicsFile = new File( helpDirectory, TARGET_FILE_NAME + JAVA_EXTN );

        //Check consistency between the index and the HTML files.
        Set<Topic> topicsFromHtmlFiles = readHelpTopicsFromHtmlFiles();
        targetToIndexText = readIndexItemsAndTargetsFromIndexFile();
        compareTopicsWithTargets( topicsFromHtmlFiles, targetToIndexText.keySet() );

        //Check for problems within the html files themselves.
        checkLinks();
        //Titles: "To avoid confusion, ensure that the <TITLE> tag
        // corresponds to the title used in the table of contents."
        // (From JavaHelpTM 2.0 System User's Guide).
        checkTitles();

        //generate the map of target->HTML file, and the enumeration of Help topics
        writeMapFile( targetToIndexText.keySet() );
        writeTopicsFile( topicsFromHtmlFiles );
    }

    private void compareTopicsWithTargets( Set<Topic> topics, Set<String> targetsFromIndex ) {
        Set<String> topicNames = new HashSet<String>();

        //Does every topic (i.e. HTML file) have an index entry?
        for (Topic topic : topics) {
            topicNames.add( topic.name() );
            assert targetsFromIndex.contains( topic.name() ) : unreferencedHelpFileMessage( topic.file(), indexFile );
        }

        //Does every index entry have a topic, i.e. a HTML file?
        for (String target : targetsFromIndex) {
            assert topicNames.contains( target ) : indexTopicWithoutHtmlFileMessage( target, indexFile );
        }
    }

    public static String indexTopicWithoutHtmlFileMessage( String targetForIndexEntry, File indexFile ) {
        return "The entry: '" + targetForIndexEntry + "' in the Index file: " + indexFile.getPath() + " did not have a HTML file";
    }

    public static String unreferencedHelpFileMessage( File unreferencedHelpFile, File indexFile ) {
        return "The Help file: '" + unreferencedHelpFile.getPath() + "' was not referenced in the Index file: '" + indexFile.getPath() + "'";
    }

    private Set<Topic> readHelpTopicsFromHtmlFiles() {
        File[] files = listHTMLFiles();
        Set<Topic> helpTopics = new TreeSet<Topic>();
        for (File f : files) {
            helpTopics.add( new Topic( f ) );
        }
        return helpTopics;
    }

    private void checkLinks() throws IOException {
        File[] files = listHTMLFiles();
        boolean brokenLinksFound = false;
        for (File f : files) {
            Set<String> brokenLinks = LinkChecker.invalidLinks( f );
            if (brokenLinks.size() > 0) {
                brokenLinksFound = true;
                String msg = "The file " + f.getName() + " has ";
                if (brokenLinks.size() == 1) {
                    msg += "this broken link:";
                } else {
                    msg += "these broken links:";
                }
                System.out.println( msg );
                for (String link : brokenLinks) {
                    System.out.println( link );
                }
            }
        }
        if (brokenLinksFound) {
            assert false : "One or more broken links, as shown above.";
        }
    }

    private void checkTitles() throws IOException {
        boolean allOK = true;
        File[] files = listHTMLFiles();
        for (File f : files) {
            String topicName = topicNameForFile( f );
            String indexText = this.targetToIndexText.get( topicName );
            String title = extractTitle( f );
            if (!indexText.equals( title )) {
                String msg = "Title '" + title +"' does not match index text '" + indexText + "' for file " + f.getName();
                System.out.println( msg );
                allOK = false;
            }
        }
        assert allOK : "One or more index/title mismatches, as listed."; 
    }

    private static String extractTitle( File htmlFile ) {
        Pattern p = Pattern.compile( "<title>(.*)</title>", Pattern.CASE_INSENSITIVE );
        Matcher m = p.matcher( Files.contents( htmlFile ) );
        m.find();
        return m.group(1);
    }
    private File[] listHTMLFiles() {
        return helpDirectory.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.endsWith( HTML_EXTN );
            }
        } );
    }

    private void writeMapFile( Set<String> targets ) throws IOException {
        Document document = docBuilder().newDocument();
        Node root = document.createElement( MAP_TAG_NAME );
        document.appendChild( root );
        for (String target : targets) {
            Element element = document.createElement( MAP_ID_TAG_NAME );
            root.appendChild( element );
            element.setAttribute( TARGET_TAG_NAME, target );
            element.setAttribute( URL_TAG_NAME, target + HTML_EXTN );
        }
        Writer writer = new BufferedWriter( new FileWriter( mapFile ) );
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform( new DOMSource( document ), new StreamResult( writer ) );
        } catch (TransformerException e) {
            e.printStackTrace();
            assert false : "Problems with default XML configuration, as shown.";
        }
        writer.close();
    }

    private void writeTopicsFile( Collection<Topic> topics ) throws IOException {
        BufferedWriter writer = new BufferedWriter( new FileWriter( topicsFile ) );
        writePackageName( writer );
        writeStartOfClass( writer );
        for (Topic topic : topics) {
            writer.write( topic.toString() );
            writer.newLine();
        }
        writeEndOfClass( writer );
        writer.close();
    }

    private void writeStartOfClass( BufferedWriter writer ) throws IOException {
        writer.write( "public enum " );
        writer.write( TARGET_FILE_NAME );
        writer.write( " {" );
        writer.newLine();
        writer.newLine();
    }

    private void writeEndOfClass( BufferedWriter writer ) throws IOException {
        writer.newLine();
        writer.write( '}' );
    }

    private void writePackageName( BufferedWriter writer ) throws IOException {
        writer.write( "package " );
        writer.write( packageName );
        writer.write( ';' );
        writer.newLine();
        writer.newLine();
    }

    private class Topic implements Comparable {
        private File file;

        public Topic( File file ) {
            this.file = file;
        }

        public String name() {
            return topicNameForFile( file );
        }

        public File file() {
            return file;
        }

        public int compareTo( Object o ) {
            return name().compareTo( ((Topic) o).name() );
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append( "    " );
            sb.append( name() );
            sb.append( "," );
            return sb.toString();
        }
    }

    private static String topicNameForFile( File file ) {
        return file.getName().substring( 0, file.getName().indexOf( HTML_EXTN ) );
    }

    /**
     * @return The map of target to index text from the file HelpIndex.xml
     */
    public Map<String, String> readIndexItemsAndTargetsFromIndexFile() throws IOException {
        Map<String, String> targetToIndexText = new HashMap<String, String>();
        BufferedInputStream buffIn = new BufferedInputStream( new FileInputStream( indexFile ) );
        Document document = null;
        try {
            document = docBuilder().parse( buffIn );
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Problem parsing index file, as shown";
        }
        buffIn.close();
        loadIndexItemsAndTargetsFromNode( document, targetToIndexText );
        return targetToIndexText;
    }

    private DocumentBuilder docBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            assert false : "Problem with default DocumentBuilderFactory, as shown";
            return null;//If assertions are off.
        }
    }

    private static void loadIndexItemsAndTargetsFromNode( Node node, Map<String, String> targetToIndexText ) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item( i );
            if (child instanceof Element) {
                Element element = (Element) child;
                if (element.getNodeName().equals( INDEX_ITEM_TAG )) {
                    String indexText = element.getAttribute( INDEX_TEXT_TAG );
                    String target = element.getAttribute( TARGET_TAG );
                    if (targetToIndexText.containsValue( indexText )) {
                        assert false : "Duplicate index text: '" + indexText + "'";
                    }
                    if (targetToIndexText.keySet().contains( target )) {
                        assert false : "The target '" + target + "' is associated with 2 index entries: '" + indexText + "' and '" + targetToIndexText.get( target ) + "'";
                    }
                    if (indexText.equals( "" )) {
                        assert false : "Blank index entry associated with target '" + target;
                    }
                    if (target.equals( "" )) {
                        assert false : "The index entry '" + indexText + "' had a blank target ";
                    }
                    targetToIndexText.put( target, indexText );
                }
                loadIndexItemsAndTargetsFromNode( child, targetToIndexText );
            }
        }
    }

    public static HelpBroker broker() {
        if (broker == null) {
            initialiseBroker();
        }
        return broker;
    }

    private static void initialiseBroker() {
        HelpSet helpSet = null;
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            URL helpURL = ClassLoader.getSystemResource( HELPSET_NAME );
            helpSet = new HelpSet( classLoader, helpURL );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println( "*** Help Set not found ***" );
        }
        broker = helpSet.createHelpBroker();
    }

    /**
     * The action that will bring up the Help with the specified topic selected
     */
    public static ActionListener helpActionFor( Help topic ) {
        if (broker == null) {
            initialiseBroker();
        }
        broker.setCurrentID( topic.toString() );
        return new CSH.DisplayHelpFromSource( broker );
    }

    /**
     * @param args [0] root package for generated class files
     * @param args [1] root source directory
     */
    public static void main( String[] args ) {
        try {
            new HelpGenerator( args[0], args[1] );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}