package jet.testtools.test;

import jet.testtools.UI;
import jet.testtools.Cyborg;
import jet.testtools.Waiting;
import jet.util.UserStrings;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author Tim Lavers
 */
public class TestBase {
    JFrame frame;
    protected JMenu menu;
    protected Cyborg borg = new Cyborg();
    protected JTree tree;
    protected DefaultMutableTreeNode t = new DefaultMutableTreeNode( "Root" );
    protected DefaultMutableTreeNode tA = new DefaultMutableTreeNode( "A" );
    protected DefaultMutableTreeNode tB = new DefaultMutableTreeNode( "B" );
    protected DefaultMutableTreeNode tC = new DefaultMutableTreeNode( "C" );
    protected DefaultMutableTreeNode tD = new DefaultMutableTreeNode( "D" );
    protected DefaultMutableTreeNode tAA = new DefaultMutableTreeNode( "A" );
    protected DefaultMutableTreeNode tAB = new DefaultMutableTreeNode( "B" );
    protected DefaultMutableTreeNode tBA = new DefaultMutableTreeNode( "A" );
    protected DefaultMutableTreeNode tBB = new DefaultMutableTreeNode( "B" );
    protected DefaultMutableTreeNode tBC = new DefaultMutableTreeNode( "C" );
    protected DefaultMutableTreeNode tCA = new DefaultMutableTreeNode( "A" );
    protected DefaultMutableTreeNode tDA = new DefaultMutableTreeNode( "A" );
    protected DefaultMutableTreeNode tDB = new DefaultMutableTreeNode( "B" );
    protected DefaultMutableTreeNode tBBA = new DefaultMutableTreeNode( "A" );
    protected DefaultMutableTreeNode tBBB = new DefaultMutableTreeNode( "B" );
    protected DefaultMutableTreeNode tBBC = new DefaultMutableTreeNode( "C" );
    protected JSlider slider;
    protected JSpinner spinner;


    void setupFrame( JComponent toGoInCentre, Dimension sizeOrNull ) {
        //Safety check that the calling thread is the event thread.
        assert SwingUtilities.isEventDispatchThread() :
                "Should be called from the event thread!";
        frame = new JFrame();
        frame.setLayout( new BorderLayout() );
        frame.add( toGoInCentre, BorderLayout.CENTER );
        if (sizeOrNull == null) {
            frame.pack();
        } else {
            frame.setSize( sizeOrNull );
        }
        frame.setVisible( true );
        frame.toFront();
        frame.transferFocus();
    }

    void cleanup() {
        Waiting.pause( 100 );
        UI.disposeOfAllFrames();
        Waiting.pause( 100 );
    }

    Vector<Vector> createTableItems() {
        Vector<Vector> result = new Vector<Vector>();
        result.add( new MusicalWork( "Beethoven", "Opus 2 Number 2", "Sonata", "A", "Piano", "", "17" ).tableRow() );
        result.add( new MusicalWork( "Beethoven", "Opus 106", "Sonata", "X", "Piano", "Hammerklavier", "49" ).tableRow() );
        result.add( new MusicalWork( "Prokofiev", "Opus 83", "Sonata", "", "Piano", "", "18" ).tableRow() );
        result.add( new MusicalWork( "Shostakovich", "Opus 10", "Symphony", "", "Orchestra", "", "31" ).tableRow() );
        return result;
    }

    Vector<String> createColumnNames() {
        final Vector<String> columnNames = new Vector<String>();
        columnNames.add( "Composer" );
        columnNames.add( "Catalogue" );
        columnNames.add( "Form" );
        columnNames.add( "Key" );
        columnNames.add( "Performing media" );
        columnNames.add( "Name" );
        columnNames.add( "Duration (minutes)" );
        return columnNames;
    }

    protected void setupMenu( final UserStrings us, final String[] keys, final boolean[] enabled, final ActionListener[] listeners ) {
        Runnable setupTask = new Runnable() {
            public void run() {
                menu = new JMenu( us.label( TTUserStrings.HOBBITS ) );
                menu.setName( TTUserStrings.HOBBITS );
                menu.setMnemonic( us.mnemonic( TTUserStrings.HOBBITS ) );
                addMenuItems( menu, us, keys, enabled, listeners );
                frame = new JFrame( "checkMenuTest" );
                JMenuBar jmb = new JMenuBar();
                jmb.add( menu );
                frame.setJMenuBar( jmb );
                frame.setSize( new Dimension( 600, 400 ) );
                frame.setVisible( true );
                frame.toFront();
            }
        };
        UI.runInEventThread( setupTask );
    }

    void addMenuItems( JComponent menu, UserStrings us, String[] keys, boolean[] enabled, ActionListener[] listeners ) {
        for (int i = 0; i < keys.length; i++) {
            JMenuItem mi = new JMenuItem( us.label( keys[i] ) );
            mi.setName( keys[i] );
            if (us.mnemonic( keys[i] ) != null) {
                mi.setMnemonic( us.mnemonic( keys[i] ) );
            }
            mi.setEnabled( enabled[i] );
            if (listeners != null) {
                mi.addActionListener( listeners[i] );
            }
            menu.add( mi );
        }
    }

    private class MusicalWork {
        String composer;
        String catalogueNumber;
        String form;
        String key;
        String performingMedia;
        String name;
        String duration;

        public MusicalWork( String composer, String catalogueNumber, String form, String key, String performingMedia, String name, String duration ) {
            this.composer = composer;
            this.form = form;
            this.key = key;
            this.catalogueNumber = catalogueNumber;
            this.performingMedia = performingMedia;
            this.name = name;
            this.duration = duration;
        }

        Vector<String> tableRow() {
            Vector<String> result = new Vector<String>();
            result.add( composer );
            result.add( catalogueNumber );
            result.add( form );
            result.add( key );
            result.add( performingMedia );
            result.add( name );
            result.add( duration );
            return result;
        }

    }

    String[] createListItems( int n ) {
        final String[] items = new String[n];
        for (int i = 0; i < items.length; i++) {
            items[i] = "item " + i;
        }
        return items;
    }

    void initWithTree() {
        Runnable setupTask = new Runnable() {
            public void run() {
                t.add( tA );
                t.add( tB );
                t.add( tC );
                t.add( tD );
                tA.add( tAA );
                tA.add( tAB );
                tB.add( tBA );
                tB.add( tBB );
                tB.add( tBC );
                tC.add( tCA );
                tD.add( tDA );
                tD.add( tDB );
                tBB.add( tBBA);
                tBB.add( tBBB);
                tBB.add( tBBC);

                tree = new JTree( t );
                JScrollPane scroller = new JScrollPane( tree );
                scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
                setupFrame( scroller, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//See notes below.        
    }

    void initWithSlider() {
        Runnable setupTask = new Runnable() {
            public void run() {
                slider = new JSlider( JSlider.VERTICAL, 0, 400, 314 );
                setupFrame( slider, null );
            }
        };
        UI.runInEventThread( setupTask );
    }

    void initWithSpinner() {
        Runnable setupTask = new Runnable() {
            public void run() {
                spinner = new JSpinner( new SpinnerNumberModel( 50, 0, 100, 10 ) );
                setupFrame( spinner, null );
            }
        };
        UI.runInEventThread( setupTask );
    }
}