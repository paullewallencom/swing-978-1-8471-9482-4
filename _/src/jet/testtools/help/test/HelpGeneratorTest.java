package jet.testtools.help.test;

import jet.testtools.*;
import jet.testtools.help.*;
import jet.testtools.test.testtools.help.Help;
import org.w3c.dom.*;

import javax.help.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.xml.parsers.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Map;

public class HelpGeneratorTest {
    private String helpDirectoryName;
    private String helpPackageName = "jet.run.help";
    private File helpDirectory;

    public boolean mainTest() throws Exception {
        init();

        File topicsFile = new File( helpDirectory, HelpGenerator.TARGET_FILE_NAME + HelpGenerator.JAVA_EXTN );
        Files.copyTo( new File( Help.HelpIndex_xml ), helpDirectory );
        Files.copyTo( new File( Help.ExampleTopic_html ), helpDirectory );
        Files.copyTo( new File( Help.ExampleSubTopic_html ), helpDirectory );

        HelpGenerator.main( new String[]{helpPackageName, helpDirectoryName} );
        //Check whether the class file has been created.
        assert topicsFile.exists() : "Topics file: '" + topicsFile.getPath() + "' was not created";
        String contents = Files.contents( topicsFile );
        String expected = Files.contents( new File( Help.ExpectedTopics_txt ) );
        Assert.equal( contents, expected );

        //Check whether the map file has been created,
        //and the map of target -> url are equivalent.
        File mapFile = new File( helpDirectory, HelpGenerator.MAP_FILE_NAME );
        File expectedMapFile = new File( Help.ExpectedHelpMap_txt );
        assert mapFile.exists() : "Map file: '" + mapFile.getPath() + "' was not created";
        Files.contents( mapFile );
        Map<String, String> mapFound = targetToUrl( mapFile );
        Assert.equal( mapFound.size(), 2 );
        Map<String, String> mapExpected = targetToUrl( expectedMapFile );
        Assert.equal( mapFound, mapExpected );
        return true;
    }

    private Map<String, String> targetToUrl( File mapFile ) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        BufferedInputStream buffIn = new BufferedInputStream( new FileInputStream( mapFile ) );
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( buffIn );
        buffIn.close();
        NodeList list = doc.getElementsByTagName( HelpGenerator.MAP_TAG_NAME );
        Assert.equal( list.getLength(), 1 );
        Node root = list.item( 0 );
        list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item( i );
            if (node instanceof Element) {
                Element element = (Element) node;
                String target = element.getAttribute( HelpGenerator.TARGET_TAG_NAME );
                String url = element.getAttribute( HelpGenerator.URL_TAG_NAME );
                map.put( target, url );
            }
        }
        return map;
    }

    //Check that the HelpGenerator asserts if there is an HTML
    //file without a corresponding target in the index file.
    public boolean unreferencedHelpFileMessageTest() throws IOException {
        init();
        File indexFile = new File( helpDirectory, HelpGenerator.HELP_INDEX_FILE_NAME );
        Files.copyToFile( new File( Help.HelpIndex_xml ), indexFile );

        //provide a HTML file that is not referenced in the index file
        File unreferencedHelpFile = Files.copyTo( new File( Help.TopicWithoutTarget_html ), helpDirectory );
        Files.copyTo( new File( Help.ExampleTopic_html ), helpDirectory );
        Files.copyTo( new File( Help.ExampleSubTopic_html ), helpDirectory );
        try {
            HelpGenerator.main( new String[]{helpPackageName, helpDirectoryName} );
            assert false : "Did not assert";
        } catch (AssertionError e) {
            Assert.equal( e.getMessage(), HelpGenerator.unreferencedHelpFileMessage( unreferencedHelpFile, indexFile ) );
        }
        return true;
    }

    //Check that the HelpGenerator asserts if there is
    //a target in the index file with a corresponding HTML file.
    public boolean indexTopicWithoutHtmlFileMessageTest() throws IOException {
        init();
        File indexFile = new File( helpDirectory, HelpGenerator.HELP_INDEX_FILE_NAME );
        Files.copyToFile( new File( Help.HelpIndex_xml ), indexFile );

        //Provide the example topic HTML file, but not the the example sub-topic HTML file.
        Files.copyTo( new File( Help.ExampleTopic_html ), helpDirectory );
        try {
            HelpGenerator.main( new String[]{helpPackageName, helpDirectoryName} );
            assert false : "Did not assert";
        } catch (AssertionError e) {
            Assert.equal( e.getMessage(), HelpGenerator.indexTopicWithoutHtmlFileMessage( "ExampleSubTopic", indexFile ) );
        }
        return true;
    }

    public boolean readIndexItemsAndTargetsFromIndexFileTest() throws IOException {
        init();
        File indexFile = new File( helpDirectory, HelpGenerator.HELP_INDEX_FILE_NAME );
        Files.copyToFile( new File( Help.HelpIndex_xml ), indexFile );
        Files.copyTo( new File( Help.ExampleTopic_html ), helpDirectory );
        Files.copyTo( new File( Help.ExampleSubTopic_html ), helpDirectory );
        HelpGenerator helpGenerator = new HelpGenerator( helpPackageName, helpDirectoryName );
        Map<String, String> map = helpGenerator.readIndexItemsAndTargetsFromIndexFile();
        Map<String, String> expected = new HashMap<String, String>();
        expected.put( "ExampleTopic", "This is an example topic." );
        expected.put( "ExampleSubTopic", "This is an example sub-topic." );
        Assert.equal( expected, map );
        return true;
    }

    public boolean helpActionForTest() throws IOException {
        init();
        Files.copyTo( new File( Help.HelpIndex_xml ), helpDirectory );
        Files.copyTo( new File( Help.ExampleTopic_html ), helpDirectory );
        Files.copyTo( new File( Help.ExampleSubTopic_html ), helpDirectory );
        final HelpGenerator helpGenerator = new HelpGenerator( helpPackageName, helpDirectoryName );
        jet.testtools.help.Help topic = jet.testtools.help.Help.ExampleTopic;
        Map<String, String> map = helpGenerator.readIndexItemsAndTargetsFromIndexFile();
        String target = topic.toString();
        showHelpForTarget( topic );
        boolean found = Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                Frame frame = UI.findFrameWithTitle( "Example of Help" );
                return frame != null;
            }
        }, 5000 );
        assert found;
        checkHelpIsShowingForIndexAndTarget( map.get( target ), target );
        new Cyborg().closeTopWindow();
        UI.disposeOfAllFrames();

        return true;
    }

    public boolean linksAreCheckedTest() throws IOException {
        //Setup a help directory in which there is a file with no
        //bad links, a file with a single bad link, and one with
        //two bad links.
        init();
        File indexFile = new File( helpDirectory, HelpGenerator.HELP_INDEX_FILE_NAME );
        Files.copyToFile( new File( Help.HelpIndexForBrokenSite_xml ), indexFile );
        Files.copyTo( new File( Help.ExampleTopic_html ), helpDirectory );
        Files.copyTo( new File( Help.BrokenLinks_html ), helpDirectory );
        Files.copyTo( new File( Help.SingleBrokenLink_html ), helpDirectory );

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream( byteOut );
        PrintStream oldOut = System.out;
        System.setOut( newOut );
        boolean result;
        try {
            HelpGenerator.main( new String[]{helpPackageName, helpDirectoryName} );
            result = false;
        } catch (AssertionError e) {
            result = true;
        }
        newOut.flush();
        String message = byteOut.toString();

        System.out.println( "Got: '" + message + "'" );
        //Reset System.out.
        System.setOut( oldOut );

        assert result : "Did not get assertion.";

        String expected = "The file BrokenLinks.html has these broken links:" + StringUtils.NL +
                "NonExistent1.html" + StringUtils.NL +
                "Puce.png" + StringUtils.NL +
                "The file SingleBrokenLink.html has this broken link:" + StringUtils.NL +
                "NonExistent1.html" + StringUtils.NL;
        Assert.equal( expected, message );
        return true;
    }

    public boolean titlesAreCheckedTest() throws IOException {
        //Setup a help directory in which there is a file with no
        //bad links, a file with a single bad link, and one with
        //two bad links.
        init();
        File indexFile = new File( helpDirectory, HelpGenerator.HELP_INDEX_FILE_NAME );
        Files.copyToFile( new File( Help.HelpIndexForBadTitles_xml ), indexFile );
        Files.copyTo( new File( Help.A_html ), helpDirectory );
        Files.copyTo( new File( Help.B_html ), helpDirectory );
        Files.copyTo( new File( Help.C_html ), helpDirectory );

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream( byteOut );
        PrintStream oldOut = System.out;
        System.setOut( newOut );
        boolean result;
        try {
            HelpGenerator.main( new String[]{helpPackageName, helpDirectoryName} );
            result = false;
        } catch (AssertionError e) {
            result = true;
        }
        newOut.flush();
        String message = byteOut.toString();
//
        System.out.println( "Got: '" + message + "'" );
        //Reset System.out.
        System.setOut( oldOut );

        assert result : "Did not get assertion.";

        String expected = "Title '' does not match index text 'Topic A' for file A.html" + StringUtils.NL +
                "Title 'untitled' does not match index text 'Topic C' for file C.html" + StringUtils.NL;
        Assert.equal( expected, message );
        return true;
    }

    private void showHelpForTarget( final jet.testtools.help.Help target ) {
        UI.runInEventThread( new Runnable() {
            public void run() {
                HelpGenerator.helpActionFor( target ).actionPerformed( new ActionEvent( new JFrame(), 0, "" ) );
            }
        } );
    }

    private static void checkHelpIndexItemIsSelected( final String nameOfIndexItem, String target ) {
        Window window = ((DefaultHelpBroker) HelpGenerator.broker()).getWindowPresentation().getHelpWindow();
        Component component = UI.findComponentIn( window, new UI.ComponentSearchCriterion() {
            public boolean isSatisfied( Component component ) {
                if (component instanceof JTree) {
                    TreePath selectionPath = ((JTree) component).getSelectionPath();
                    if (selectionPath != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                        IndexItem item = (IndexItem) node.getUserObject();
                        return item.getName().equals( nameOfIndexItem );
                    }
                }
                return false;
            }
        } );
        assert component != null : "Help index item not showing: '" + nameOfIndexItem + "' for target '" + target + "'";
    }

    public void checkHelpIsShowingForTopic( jet.testtools.help.Help target ) {
        try {
            HelpGenerator helpGenerator = new HelpGenerator( helpPackageName, helpDirectoryName );
            Map<String, String> map = helpGenerator.readIndexItemsAndTargetsFromIndexFile();
            checkHelpIsShowingForIndexAndTarget( map.get( target.toString() ), target.toString() );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkHelpIsShowingForIndexAndTarget( final String indexText, String target ) {
        boolean found = Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return HelpGenerator.broker() != null && HelpGenerator.broker().isDisplayed();
            }
        }, 10000 );
        assert found : "Help did not show for '" + indexText + "' and target '" + target + "'";
        checkHelpIndexItemIsSelected( indexText, target );
    }

    public boolean constructorTest() {
        //tested elsewhere
        return true;
    }

    public boolean helpButtonTest() {
        final JFrame frame = UI.createFrame( "HelpGeneratorTest" );
        Runnable runnable = new Runnable() {
            public void run() {
                JButton helpButton = new JButton( new AbstractAction() {
                    public void actionPerformed( ActionEvent e ) {
                        HelpGenerator.helpActionFor( jet.testtools.help.Help.ExampleTopic ).actionPerformed( e );
                    }
                } );
                helpButton.setText( "Help" );
                helpButton.setMnemonic( 'H' );
                frame.getContentPane().add( helpButton );
            }
        };
        UI.runInEventThread( runnable );
        UI.showFrame( frame );
        new Cyborg().altChar( 'H' );
        checkHelpIsShowingForTopic( jet.testtools.help.Help.ExampleTopic );
        UI.disposeOfAllFrames();
        return true;
    }

    public boolean brokerTest() {
        //tested elsewhere
        return true;
    }

    private void init() throws IOException {
        File tempDir = Files.cleanedTempDir();
        helpDirectoryName = tempDir.getPath() + File.separatorChar + "jet" + File.separatorChar + "run" + File.separatorChar + "help";
        helpDirectory = new File( helpDirectoryName );
        helpDirectory.mkdirs();
        Files.copyTo( new File( jet.testtools.test.testtools.help.Help.HelpSet_hs ), helpDirectory );
    }
}