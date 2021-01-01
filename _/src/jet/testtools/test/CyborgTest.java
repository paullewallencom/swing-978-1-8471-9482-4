package jet.testtools.test;

import jet.testtools.*;
import jet.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

/**
 * @author Tim Lavers
 */
public class CyborgTest extends TestBase {
    private JPanel panel;
    private RainbowPanel rainbowPanel;
    private JButton button1;
    private JButton button2;
    private JTextArea textArea;
    private JList list;
    private JComboBox combo;
    private JTable table;
    private RecordingMML mml;
    private RecordingML ml;
    private RecordingKL kl;
    private RecordingActionListener al;
    private RecordingTableColumnModelListener tcml;

    public boolean robotTest() {
        assert borg.robot() != null;
        return true;
    }

    public boolean constructorTest() {
        //Pretty cursory check as the constructor is very simple.
        assert borg.robot().isAutoWaitForIdle();
        return true;
    }

    public boolean activateFocussedButtonTest() {
        initWithButton();
        assert al.events.size() == 0;
        borg.activateFocussedButton();
        assert al.events.size() == 1;
        ActionEvent ae = al.events.getFirst();
        assert ae.getSource() == button1;
        cleanup();
        return true;
    }

    public boolean speedTest() {
        initWithButton();
        assert al.events.size() == 0;
        int numClicks = 100;
        for (int i = 0; i < numClicks; i++) {
            borg.altChar( 'B' );
        }
        int numEvents = al.events.size();
        assert numEvents == numClicks : "Got " + numEvents;
        cleanup();
        return true;
    }

    public boolean putIntoClipboardTest() throws Exception {
        String str = "THERE were four of us - George, and William Samuel Harris, and myself, and Montmorency.";
        borg.putIntoClipboard( str );
        assert clipboardContents().equals( str );
        cleanup();
        return true;
    }

    private String clipboardContents() throws Exception {
        Clipboard clipboard =
                Toolkit.getDefaultToolkit().getSystemClipboard();
        return clipboard.getContents( null ).getTransferData(
                DataFlavor.stringFlavor ).toString();
    }

    public boolean pasteTest() {
        initWithTextArea();
        //First some pretty plain text.
        assert UI.getText( textArea ).equals( "" );
        String str = "THERE were four of us - George, and William Samuel Harris, and myself, and Montmorency.";
        borg.putIntoClipboard( str );
        borg.paste();
        assert UI.getText( textArea ).equals( str );

        cleanup();
        return true;
    }

    public boolean enterTextTest() {
        initWithTextArea();
        //First some pretty plain text.
        assert UI.getText( textArea ).equals( "" );
        String str = "THERE were four of us - George, and William Samuel Harris, and myself, and Montmorency.";
        borg.enterText( str );
        assert UI.getText( textArea ).equals( str );

        //Now all letters and digits, lowercase.
        String lc = "the quick brown fox jumped over the lazy dog 1234567890 times";
        borg.enterText( lc );
        String expected = str + lc;
        assert UI.getText( textArea ).equals( expected );

        //Add uppercase letters.
        String uc = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";
        borg.enterText( uc );
        expected = expected + uc;
        assert UI.getText( textArea ).equals( expected );

        //Add symbols.
        String symbols = "!@#$%^&*()-_=+";
        borg.enterText( symbols );
        expected = expected + symbols;
        assert UI.getText( textArea ).equals( expected );

        symbols = "[]\\{}|";
        borg.enterText( symbols );
        expected = expected + symbols;
        assert UI.getText( textArea ).equals( expected );

        symbols = ";:";
        borg.enterText( symbols );
        expected = expected + symbols;
        assert UI.getText( textArea ).equals( expected );

        symbols = ",./<>?";
        borg.enterText( symbols );
        expected = expected + symbols;
        assert UI.getText( textArea ).equals( expected );

        //Newline.
        borg.enterText( "\n" );
        expected = expected + "\n";
        assert UI.getText( textArea ).equals( expected );
        cleanup();
        return true;
    }

    public boolean typeTest() throws Exception {
        initWithTextArea();
        String initialClipboardContents = "Initial clipboard contents";
        borg.putIntoClipboard( initialClipboardContents );
        //First some pretty plain text.
        assert UI.getText( textArea ).equals( "" );
        String str = "THERE were four of us - George, and William Samuel Harris, and myself, and Montmorency.";
        borg.type( str );
        assert UI.getText( textArea ).equals( str );
        assert clipboardContents().equals( initialClipboardContents );

        //Now all letters and digits, lowercase.
        String lc = "the quick brown fox jumped over the lazy dog 1234567890 times";
        borg.type( lc );
        String expected = str + lc;
        assert UI.getText( textArea ).equals( expected );
        assert clipboardContents().equals( initialClipboardContents );

        //Add uppercase letters.
        String uc = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";
        borg.type( uc );
        expected = expected + uc;
        assert UI.getText( textArea ).equals( expected );
        assert clipboardContents().equals( initialClipboardContents );

        //Add symbols.
        String symbols = "!@#$%^&*()-_=+";
        borg.type( symbols );
        expected = expected + symbols;
        assert UI.getText( textArea ).equals( expected );

        symbols = "[]\\{}|";
        borg.type( symbols );
        expected = expected + symbols;
        assert UI.getText( textArea ).equals( expected );

        symbols = ";:";
        borg.type( symbols );
        expected = expected + symbols;
        assert UI.getText( textArea ).equals( expected );

        symbols = ",./<>?";
        borg.type( symbols );
        expected = expected + symbols;
        assert UI.getText( textArea ).equals( expected );

        //Newline.
        borg.type( "\n" );
        expected = expected + "\n";
        assert UI.getText( textArea ).equals( expected );
        cleanup();
        return true;
    }

    public boolean escapeTest() {
        initWithPanel();
        borg.escape();
        checkTypeKey( KeyEvent.VK_ESCAPE );
        cleanup();
        return true;
    }

    public boolean enterTest() {
        initWithPanel();
        borg.enter();
        checkTypeKey( KeyEvent.VK_ENTER );
        cleanup();
        return true;
    }

    public boolean deleteTest() {
        initWithPanel();
        borg.delete();
        checkTypeKey( KeyEvent.VK_DELETE );
        cleanup();
        return true;
    }

    public boolean contextMenuTest() {
        initWithPanel();
        borg.contextMenu();
        checkNonTypeKey( KeyEvent.VK_CONTEXT_MENU );
        cleanup();
        return true;
    }

    public boolean downTest() {
        initWithPanel();
        borg.down();
        checkNonTypeKey( KeyEvent.VK_DOWN );
        cleanup();
        return true;
    }

    public boolean rightTest() {
        initWithPanel();
        borg.right();
        checkNonTypeKey( KeyEvent.VK_RIGHT );
        cleanup();
        return true;
    }

    public boolean leftTest() {
        initWithPanel();
        borg.left();
        checkNonTypeKey( KeyEvent.VK_LEFT );
        cleanup();
        return true;
    }

    public boolean upTest() {
        initWithPanel();
        borg.up();
        checkNonTypeKey( KeyEvent.VK_UP );
        cleanup();
        return true;
    }

    public boolean homeTest() {
        initWithPanel();
        borg.home();
        checkNonTypeKey( KeyEvent.VK_HOME );
        cleanup();
        return true;
    }

    public boolean endTest() {
        initWithPanel();
        borg.end();
        checkNonTypeKey( KeyEvent.VK_END );
        cleanup();
        return true;
    }

    public boolean tabTest() {
        //The panel will not receive tab keystrokes, as these are
        //for moving between components. We do two checks. First
        //we test that tab() moves between two buttons.
        //Then we check that it types a \t char into a text area.
        initWithTwoButtons();
        assert al.events.size() == 0;
        borg.activateFocussedButton();
        assert al.events.size() == 1;
        assert al.events.getFirst().getSource() == button1;
        borg.tab();
        borg.activateFocussedButton();
        assert al.events.size() == 2;
        assert al.events.getLast().getSource() == button2;
        cleanup();

        initWithTextArea();
        borg.type( "Before the tab" );
        borg.tab();
        borg.type( "after the tab" );
        assert UI.getText( textArea ).equals( "Before the tab\tafter the tab" );
        cleanup();
        return true;
    }

    public boolean altCharTest() {
        initWithTwoButtons();
        assert al.events.size() == 0;
        borg.altChar( 'B' );
        assert al.events.size() == 1;
        assert al.events.getFirst().getSource() == button1;
        borg.altChar( 'U' );
        assert al.events.size() == 2;
        assert al.events.getLast().getSource() == button2;
        cleanup();
        return true;
    }

    public boolean spaceTest() {
        initWithTextArea();
        borg.space();
        assert UI.getText( textArea ).equals( " " );
        cleanup();
        return true;
    }

    public boolean closeTopWindowTest() {
        initWithPanel();
        assert UI.isShowing( frame );
        borg.closeTopWindow();
        assert !UI.isShowing( frame );
        cleanup();
        return true;
    }

    public boolean selectAllTextTest() {
        initWithTextArea();
        String text = "This is the text.";
        borg.type( text );
        assert UI.getText( textArea ).equals( text );
        borg.selectAllText();
        assert UI.getSelectedText( textArea ).equals( text );
        cleanup();
        return true;
    }

    private void checkNonTypeKey( int expected ) {
        assert kl.events.size() == 2 : kl.events;
        assert kl.events.get( 0 ).getKeyCode() == expected;
        assert kl.events.get( 0 ).getID() == KeyEvent.KEY_PRESSED;
        assert kl.events.get( 1 ).getKeyCode() == expected;
        assert kl.events.get( 1 ).getID() == KeyEvent.KEY_RELEASED;
    }

    private void checkTypeKey( int expected ) {
        assert kl.events.size() == 3 : kl.events;
        assert kl.events.get( 0 ).getKeyCode() == expected;
        assert kl.events.get( 0 ).getID() == KeyEvent.KEY_PRESSED;
        assert kl.events.get( 1 ).getKeyCode() == KeyEvent.VK_UNDEFINED;
        assert kl.events.get( 1 ).getID() == KeyEvent.KEY_TYPED;
        assert kl.events.get( 2 ).getKeyCode() == expected;
        assert kl.events.get( 2 ).getID() == KeyEvent.KEY_RELEASED;
    }

    public boolean mouseDragTest() {
        initWithPanel();
        //First of all, a horizontal line.
        Point offset = panel.getLocationOnScreen();
        Point start = new Point( 50, 50 );
        Point end = new Point( 60, 50 );
        borg.mouseDrag( start, end );
        assert mml.drags.size() == 11 : mml.drags.size();
        for (int i = 1; i <= 11; i++) {
            Point expected = new Point( start.x + i, start.y );
            expected.translate( -offset.x, -offset.y );
            Point got = mml.drags.get( i - 1 ).getPoint();
            assert got.equals( expected ) :
                    "Expected: " + expected + ", got: " + got;
        }

        //Next, a line of slope 1.
        mml.drags = new LinkedList<MouseEvent>();
        start = new Point( 50, 50 );
        end = new Point( 100, 100 );
        borg.mouseDrag( start, end );
        assert mml.drags.size() == 51 : mml.drags.size();
        for (int i = 1; i <= 51; i++) {
            Point expected = new Point( start.x + i, start.y + i );
            expected.translate( -offset.x, -offset.y );
            Point got = mml.drags.get( i - 1 ).getPoint();
            assert got.equals( expected ) :
                    "Expected: " + expected + ", got: " + got;
        }

        cleanup();
        return true;
    }

    public boolean mouseLeftClick_Component_Test() {
        initWithPanel();
        borg.mouseLeftClick( panel );
        assert ml.clicks.size() == 1;
        MouseEvent me = ml.clicks.getFirst();
        assert me.getClickCount() == 1;
        assert me.getSource() == panel;
        assert me.getButton() == MouseEvent.BUTTON1;
        cleanup();
        return true;
    }

    public boolean mouseLeftClick_Point_Test() {
        initWithPanel();
        //We'll click a point that happens to be in panel.
        //Take some point.
        Point p = new Point( 100, 100 );
        Point q = new Point( p );
        //Get the point in the panel with the same co-ordinates,
        //in the panel's co-ordinate system.
        Point panelLocation = UI.getScreenLocation( panel );
        q.translate( panelLocation.x, panelLocation.y );
        //Click the point.
        borg.mouseLeftClick( q );
        assert ml.clicks.size() == 1;
        MouseEvent me = ml.clicks.getFirst();
        assert me.getClickCount() == 1;
        assert me.getSource() == panel;
        assert me.getButton() == MouseEvent.BUTTON1;
        //The click should seem to have occured at a point equal to p,
        //since getPoint() is in the panel's co-ordinate space.
        assert me.getPoint().equals( p );
        cleanup();
        return true;
    }

    public boolean mouseLeftClickTest() {
        initWithPanel();
        //So that we don't click on some background application,
        //move the mouse to a position in the panel.
        Point p = new Point( 100, 100 );
        Point panelLocation = UI.getScreenLocation( panel );
        p.translate( panelLocation.x, panelLocation.y );
        borg.robot().mouseMove( p.x, p.y );
        //Click the point.
        borg.mouseLeftClick();
        assert ml.clicks.size() == 1;
        MouseEvent me = ml.clicks.getFirst();
        assert me.getClickCount() == 1;
        assert me.getSource() == panel;
        assert me.getButton() == MouseEvent.BUTTON1;
        cleanup();
        return true;
    }

    public boolean safelyGetPixelColourTest() {
        initWithRainbowPanel();
        Point location = UI.getScreenLocation( rainbowPanel );
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {
                Point inPanel = new Point( i, j );
                inPanel.translate( location.x, location.y );
                Color actual = borg.safelyGetPixelColour( inPanel.x, inPanel.y );
                assert actual.equals( RAINBOW[i] ) :
                        "Expected: " + RAINBOW[i] + ", got: " + actual;
            }
        }
        cleanup();
        return true;
    }

    public boolean checkPixelInComponentTest() {
        initWithRainbowPanel();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {
                Point inPanel = new Point( i, j );
                //A check that should pass.
                borg.checkPixelInComponent( inPanel.x, inPanel.y, RAINBOW[i], rainbowPanel );
                //A check that should fail.
                boolean gotError = false;
                try {
                    borg.checkPixelInComponent( inPanel.x, inPanel.y, Color.WHITE, rainbowPanel );
                } catch (AssertionError e) {
                    gotError = true;
                }
                assert gotError : "Should have got error!";
            }
        }
        cleanup();
        return true;
    }

    public boolean checkPixelTest() {
        initWithRainbowPanel();
        Point location = UI.getScreenLocation( rainbowPanel );
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {
                Point inPanel = new Point( i, j );
                inPanel.translate( location.x, location.y );
                //A check that should pass.
                borg.checkPixel( inPanel.x, inPanel.y, RAINBOW[i] );
                //A check that should fail.
                boolean gotError = false;
                try {
                    borg.checkPixel( inPanel.x, inPanel.y, Color.WHITE );
                } catch (AssertionError e) {
                    gotError = true;
                }
                assert gotError : "Should have got error!";
            }
        }
        cleanup();
        return true;
    }

    public boolean selectFirstOnlyInListTest() {
        initWithList();
        borg.selectFirstOnlyInList();
        assert Arrays.equals( UI.getSelectedIndices( list ), new int[]{0} );
        borg.down();
        borg.down();
        borg.down();
        assert Arrays.equals( UI.getSelectedIndices( list ), new int[]{3} );
        borg.selectFirstOnlyInList();
        assert Arrays.equals( UI.getSelectedIndices( list ), new int[]{0} );
        cleanup();
        return true;
    }

    public boolean selectLastOnlyInListTest() {
        initWithList();
        borg.selectLastOnlyInList();
        assert Arrays.equals( UI.getSelectedIndices( list ), new int[]{29} );
        borg.up();
        borg.up();
        borg.up();
        assert Arrays.equals( UI.getSelectedIndices( list ), new int[]{26} );
        borg.selectLastOnlyInList();
        assert Arrays.equals( UI.getSelectedIndices( list ), new int[]{29} );
        cleanup();
        return true;
    }

    public boolean selectListIndicesOnlyTest() {
        initWithList();
        int[] toSelect = new int[]{1, 3, 5, 7, 9, 11, 13, 15};
        borg.selectListIndicesOnly( toSelect, 30 );
        assert Arrays.equals( UI.getSelectedIndices( list ), toSelect );
        toSelect = new int[]{1, 4, 5, 9, 14, 23};
        borg.selectListIndicesOnly( toSelect, 30 );
        assert Arrays.equals( UI.getSelectedIndices( list ), toSelect );
        toSelect = new int[]{};
        borg.selectListIndicesOnly( toSelect, 30 );
        assert Arrays.equals( UI.getSelectedIndices( list ), toSelect );
        toSelect = new int[]{0};
        borg.selectListIndicesOnly( toSelect, 30 );
        assert Arrays.equals( UI.getSelectedIndices( list ), toSelect );
        toSelect = new int[]{0, 15};
        borg.selectListIndicesOnly( toSelect, 30 );
        assert Arrays.equals( UI.getSelectedIndices( list ), toSelect );
        cleanup();
        return true;
    }

    public boolean selectTreeRootTest() {
        initWithTree();
        borg.selectTreeItemByDepthFirstIndex( 8 );
        checkPath( "Root", "B", "B", "B" );
        borg.selectTreeRoot();
        checkPath( "Root" );
        borg.selectTreeRoot();
        checkPath( "Root" );
        cleanup();
        return true;
    }

    public boolean selectTreeItemByDepthFirstIndexTest() {
        initWithTree();
        borg.selectTreeItemByDepthFirstIndex( 8 );
        checkPath( "Root", "B", "B", "B" );
        borg.selectTreeItemByDepthFirstIndex( 7 );
        checkPath( "Root", "B", "B", "A" );
        borg.selectTreeItemByDepthFirstIndex( 0 );
        checkPath( "Root" );
        cleanup();
        return true;
    }

    private void checkPath( String... items ) {
        TreePath path = UI.getSelectedTreePath( tree );
        assert path.getPathCount() == items.length : "Expected: " + items.length + ", got: " + path.getPathCount();
        for (int i = 0; i < items.length; i++) {
            Object got = path.getPathComponent( i );
            assert items[i].equals( got.toString() ) :
                    "At index " + i + ", Expected: " + items[i] + ", got: " + got;
        }
    }

    public boolean clickTableCellTest() {
        initWithTable();
        borg.clickTableCell( table, 0, 0 );
        int[] selected = selectedCell();
        assert selected[0] == 0;
        assert selected[1] == 0;
        borg.clickTableCell( table, 2, 3 );
        selected = selectedCell();
        assert selected[0] == 2;
        assert selected[1] == 3;
        cleanup();
        return true;
    }

    public boolean clickTableHeaderTest() {
        initWithTable();
        for (int i = 0; i < 7; i++) {
            borg.clickTableHeader( table, i );
            assert selectedHeaderCell() == i;
        }
        cleanup();
        return true;
    }

    public boolean dragTableColumnTest() {
        initWithTable();
        borg.dragTableColumn( table, 1, 4 );
        List<Integer> expectedFromColumns
                = new LinkedList<Integer>();
        expectedFromColumns.add( 1 );
        expectedFromColumns.add( 2 );
        expectedFromColumns.add( 3 );
        List<Integer> expectedToColumns
                = new LinkedList<Integer>();
        expectedToColumns.add( 2 );
        expectedToColumns.add( 3 );
        expectedToColumns.add( 4 );
        assert tcml.fromColumns.equals( expectedFromColumns );
        assert tcml.toColumns.equals( expectedToColumns );
        cleanup();
        return true;
    }

    private int[] selectedCell() {
        final int[][] result = new int[1][2];
        UI.runInEventThread( new Runnable() {
            public void run() {
                result[0][0] = table.getSelectedRow();
                result[0][1] = table.getSelectedColumn();
            }
        } );
        return result[0];
    }

    private int selectedHeaderCell() {
        final int[] result = new int[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                result[0] = table.getTableHeader().getColumnModel().getSelectedColumns()[0];
            }
        } );
        return result[0];
    }

    public boolean activateMenuItemTest() {
        final UserStrings us = TTUserStrings.instance();
        final boolean[] targets = new boolean[]{false, false, false};
        final ActionListener[] listeners = new ActionListener[3];
        for (int i = 0; i < listeners.length; i++) {
            final int i1 = i;
            listeners[i] = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    targets[i1] = true;
                }
            };
        }
        setupMenu( us, new String[]{TTUserStrings.BILBO, TTUserStrings.FRODO, TTUserStrings.SAM}, new boolean[]{true, true, true}, listeners );
        borg.activateMenuItem( us, TTUserStrings.HOBBITS, TTUserStrings.SAM );
        final boolean[] activation = new boolean[]{false, false, false};
        UI.runInEventThread( new Runnable() {
            public void run() {
                for (int i = 0; i < activation.length; i++) {
                    activation[i] = targets[i];
                }
            }
        } );
        assert !activation[0];
        assert !activation[1];
        assert activation[2];
        cleanup();
        return true;
    }

    public boolean selectElementInComboByIndexTest() {
        initWithCombo( 15 );
        assert UI.getSelectedComboIndex( combo ) == 0;
        for (int i = 0; i < 15; i++) {
            borg.selectElementInComboByIndex( i );
            assert UI.getSelectedComboIndex( combo ) == i;
        }
        cleanup();
        return true;
    }

    public boolean selectFirstInComboTest() {
        initWithCombo( 30 );
        assert UI.getSelectedComboIndex( combo ) == 0;
        borg.selectLastInCombo();
        assert UI.getSelectedComboIndex( combo ) == 29;
        borg.selectFirstInCombo();
        assert UI.getSelectedComboIndex( combo ) == 0;
        cleanup();
        return true;
    }

    public boolean selectLastInComboTest() {
        //Tested above.
        return true;
    }

    public boolean multipleCombosTest() {
        final JComboBox[] combos = new JComboBox[3];
        final JLabel[] labels = new JLabel[3];
        final String[][] items = new String[][]{
                {"Bilbo", "Frodo", "Merry", "Pippin", "Sam"},
                {"Arwyn", "Elrond", "Galadriel"},
                {"Gandalf", "Radagast", "Saruman"},
        };

        Runnable setupTask = new Runnable() {
            public void run() {
                combos[0] = new JComboBox( items[0] );
                labels[0] = new JLabel( "Hobbits" );
                labels[0].setDisplayedMnemonic( 'h' );
                labels[0].setLabelFor( combos[0] );

                combos[1] = new JComboBox( items[1] );
                labels[1] = new JLabel( "Elves" );
                labels[1].setDisplayedMnemonic( 'e' );
                labels[1].setLabelFor( combos[1] );

                combos[2] = new JComboBox( items[2] );
                labels[2] = new JLabel( "Wizards" );
                labels[2].setDisplayedMnemonic( 'w' );
                labels[2].setLabelFor( combos[2] );
                panel = new JPanel( new GridLayout( 3, 2 ) );
                panel.add( labels[0] );
                panel.add( combos[0] );
                panel.add( labels[1] );
                panel.add( combos[1] );
                panel.add( labels[2] );
                panel.add( combos[2] );
                setupFrame( panel, null );
            }
        };
        UI.runInEventThread( setupTask );
        assert UI.getSelectedComboIndex( combos[0] ) == 0;
        assert UI.getSelectedComboIndex( combos[1] ) == 0;
        assert UI.getSelectedComboIndex( combos[2] ) == 0;

        borg.altChar( 'E' );//Focus to the elves.
        borg.selectElementInComboByIndex( 1 );
        assert UI.getSelectedComboIndex( combos[0] ) == 0;
        assert UI.getSelectedComboIndex( combos[1] ) == 1;
        assert UI.getSelectedComboIndex( combos[2] ) == 0;

        borg.altChar( 'H' );//Focus to the halflings.
        borg.selectElementInComboByIndex( 2 );
        assert UI.getSelectedComboIndex( combos[0] ) == 2;
        assert UI.getSelectedComboIndex( combos[1] ) == 1;
        assert UI.getSelectedComboIndex( combos[2] ) == 0;

        borg.altChar( 'W' );//Focus to the wizards.
        borg.selectElementInComboByIndex( 1 );
        assert UI.getSelectedComboIndex( combos[0] ) == 2;
        assert UI.getSelectedComboIndex( combos[1] ) == 1;
        assert UI.getSelectedComboIndex( combos[2] ) == 1;

        cleanup();
        return true;
    }

    public boolean dragFrameTest() {
        initWithPanel();
        Point locationBefore = UI.getScreenLocation( frame );
        borg.dragFrame( frame, 100, 50 );
        Point locationAfter = UI.getScreenLocation( frame );
        assert locationAfter.x <= locationBefore.x + 102;
        assert locationAfter.x >= locationBefore.x + 98;
        assert locationAfter.y <= locationBefore.y + 52;
        assert locationAfter.y >= locationBefore.y + 48;
        cleanup();
        return true;
    }

    public boolean resizeFrameTest() {
        initWithPanel();
        Dimension sizeBefore = UI.getSize( frame );
        borg.resizeFrame( frame, 100, 50 );
        Dimension sizeAfter = UI.getSize( frame );
        assert sizeAfter.width <= sizeBefore.width + 102;
        assert sizeAfter.width >= sizeBefore.width + 98;
        assert sizeAfter.height <= sizeBefore.height + 52;
        assert sizeAfter.height >= sizeBefore.height + 48;
        cleanup();
        return true;
    }

    private void initWithTable() {
        final Vector<Vector> items = createTableItems();
        final Vector<String> columnNames = createColumnNames();
        Runnable setupTask = new Runnable() {
            public void run() {
                table = new JTable( items, columnNames );
                table.setCellSelectionEnabled( true );
                table.setColumnSelectionAllowed( true );
                table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
                final JTableHeader header = table.getTableHeader();
                header.addMouseListener( new MouseAdapter() {
                    public void mouseClicked( MouseEvent e ) {
                        int col = header.columnAtPoint( e.getPoint() );
                        table.getColumnModel().getSelectionModel().setSelectionInterval( col, col );
                    }
                } );
                //Add a column listener so that we can test the column re-ordering methods.
                tcml = new RecordingTableColumnModelListener();
                table.getColumnModel().addColumnModelListener( tcml );
                JScrollPane scroller = new JScrollPane( table );
                setupFrame( scroller, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//See notes below.
    }

    private void initWithCombo( int numberOfListItems ) {
        final String[] items = createListItems( numberOfListItems );
        Runnable setupTask = new Runnable() {
            public void run() {
                combo = new JComboBox( items );
                setupFrame( combo, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//See notes below.
    }

    private void initWithList() {
        final String[] items = createListItems( 30 );
        Runnable setupTask = new Runnable() {
            public void run() {
                list = new JList( items );
                JScrollPane scroller = new JScrollPane( list );
                scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
                setupFrame( scroller, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//Without this, the combination of selectFirstInComboTest selectFirstOnlyInListTest will
        //fail about 2 times out of 5. The failure is caused by the list not having focus when the
        //call borg.selectFirstOnlyInList(); happens. MYSTERIOUS!!!!
    }

    private void initWithPanel() {
        Runnable setupTask = new Runnable() {
            public void run() {
                panel = new JPanel();
                panel.setSize( 200, 200 );
                panel.setPreferredSize( new Dimension( 200, 200 ) );
                panel.setBackground( Color.MAGENTA );
                panel.setEnabled( true );
                panel.setFocusable( true );
                ml = new RecordingML();
                panel.addMouseListener( ml );
                mml = new RecordingMML();
                panel.addMouseMotionListener( mml );
                kl = new RecordingKL();
                panel.addKeyListener( kl );
                setupFrame( panel, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//See notes below.
    }

    private void initWithRainbowPanel() {
        Runnable setupTask = new Runnable() {
            public void run() {
                rainbowPanel = new RainbowPanel();
                setupFrame( rainbowPanel, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//See notes below.
    }

    private void initWithButton() {
        Runnable setupTask = new Runnable() {
            public void run() {
                button1 = new JButton();
                button1.setText( "Button" );
                button1.setMnemonic( 'b' );
                button1.setEnabled( true );
                al = new RecordingActionListener();
                button1.addActionListener( al );
                setupFrame( button1, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//See notes below.
    }

    private void initWithTwoButtons() {
        Runnable setupTask = new Runnable() {
            public void run() {
                button1 = new JButton();
                button1.setText( "Button1" );
                button1.setMnemonic( 'b' );
                button1.setEnabled( true );
                al = new RecordingActionListener();
                button1.addActionListener( al );
                button2 = new JButton();
                button2.setText( "Button2" );
                button2.setMnemonic( 'u' );
                button2.setEnabled( true );
                button2.addActionListener( al );
                Box box = Box.createVerticalBox();
                box.add( button1 );
                box.add( button2 );
                setupFrame( box, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//See notes below.
    }

    private void initWithTextArea() {
        Runnable setupTask = new Runnable() {
            public void run() {
                textArea = new JTextArea( 10, 100 );
                textArea.setWrapStyleWord( true );
                textArea.setLineWrap( true );
                JScrollPane scroller = new JScrollPane( textArea );
                scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
                setupFrame( scroller, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//See notes below.
    }

    private class RecordingKL implements KeyListener {
        List<KeyEvent> events = new LinkedList<KeyEvent>();

        public void keyTyped( KeyEvent e ) {
            events.add( e );
        }

        public void keyPressed( KeyEvent e ) {
            events.add( e );
        }

        public void keyReleased( KeyEvent e ) {
            events.add( e );
        }
    }

    private class RecordingMML implements MouseMotionListener {
        List<MouseEvent> drags = new LinkedList<MouseEvent>();
        List<MouseEvent> moves = new LinkedList<MouseEvent>();

        public void mouseDragged( MouseEvent e ) {
            drags.add( e );
        }

        public void mouseMoved( MouseEvent e ) {
            moves.add( e );
        }
    }

    private class RecordingML implements MouseListener {
        LinkedList<MouseEvent> clicks = new LinkedList<MouseEvent>();
        List<MouseEvent> presses = new LinkedList<MouseEvent>();
        List<MouseEvent> releases = new LinkedList<MouseEvent>();
        List<MouseEvent> ingresses = new LinkedList<MouseEvent>();
        List<MouseEvent> egresses = new LinkedList<MouseEvent>();

        public void mouseClicked( MouseEvent e ) {
            clicks.add( e );
        }

        public void mousePressed( MouseEvent e ) {
            presses.add( e );
        }

        public void mouseReleased( MouseEvent e ) {
            releases.add( e );
        }

        public void mouseEntered( MouseEvent e ) {
            ingresses.add( e );
        }

        public void mouseExited( MouseEvent e ) {
            egresses.add( e );
        }
    }

    private class RecordingActionListener implements ActionListener {
        LinkedList<ActionEvent> events = new LinkedList<ActionEvent>();

        public void actionPerformed( ActionEvent e ) {
            events.add( e );
        }
    }

    private static Color[] RAINBOW = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA};

    private class RainbowPanel extends JPanel {
        public void paint( Graphics g ) {
            Graphics2D g2 = (Graphics2D) g;
            BufferedImage bim = new BufferedImage( 6, 10, BufferedImage.TYPE_4BYTE_ABGR );
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 10; j++) {
                    bim.setRGB( i, j, RAINBOW[i].getRGB() );
                }
            }
            g2.drawImage( bim, 0, 0, this );
        }
    }

    private class RecordingTableColumnModelListener
            implements TableColumnModelListener {
        List<Integer> fromColumns = new LinkedList<Integer>();
        List<Integer> toColumns = new LinkedList<Integer>();

        public void columnMoved( TableColumnModelEvent e ) {
            //There are heaps of events generated by a column drag,
            //we ignore those that are from a column to itself.
            if (e.getFromIndex() != e.getToIndex()) {
                fromColumns.add( e.getFromIndex() );
                toColumns.add( e.getToIndex() );
            }
        }

        public void columnAdded( TableColumnModelEvent e ) {
        }

        public void columnRemoved( TableColumnModelEvent e ) {
        }

        public void columnMarginChanged( ChangeEvent e ) {
        }

        public void columnSelectionChanged( ListSelectionEvent e ) {
        }
    }
}