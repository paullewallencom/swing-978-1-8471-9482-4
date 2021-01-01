package jet.testtools.test;

import jet.testtools.*;
import jet.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;

public class UITest extends TestBase {
    private Dimension tableCellSize = new Dimension( 80, 80 );

    public boolean findLabelShowingTextTest() {
        final String[] texts = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final JLabel[] showingLabels = new JLabel[texts.length];
        final JLabel[] invisibleLabels = new JLabel[texts.length];

        Runnable setupTask = new Runnable() {
            public void run() {
                Box labelBox = Box.createVerticalBox();
                for (int i = 0; i < showingLabels.length; i++) {
                    showingLabels[i] = new JLabel( texts[i] );
                    invisibleLabels[i] = new JLabel( texts[i] );
                    labelBox.add( showingLabels[i] );
                }
                setupFrame( labelBox, null );
            }
        };
        UI.runInEventThread( setupTask );

        for (int i = 0; i < showingLabels.length; i++) {
            assert UI.findLabelShowingText( texts[i] ) == showingLabels[i];
        }

        //When the frame has been disposed, the labels
        //should no longer be found.
        UI.disposeOfAllFrames();
        for (int i = 0; i < showingLabels.length; i++) {
            assert UI.findLabelShowingText( texts[i] ) == null;
        }
        return true;
    }

    public boolean findNamedComponentTest() {
        final String[] names = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final JComponent[] showing = new JComponent[names.length];
        final JComponent[] invisible = new JComponent[names.length];

        Runnable setupTask = new Runnable() {
            public void run() {
                Box labelBox = Box.createVerticalBox();
                for (int i = 0; i < showing.length; i++) {
                    showing[i] = new JPanel();
                    showing[i].setName( names[i] );
                    invisible[i] = new JPanel();
                    invisible[i].setName( names[i] );
                    labelBox.add( showing[i] );
                }
                setupFrame( labelBox, null );
            }
        };
        UI.runInEventThread( setupTask );

        for (int i = 0; i < showing.length; i++) {
            assert UI.findNamedComponent( names[i] ) == showing[i];
        }

        //When the frame has been disposed, the components
        //should no longer be found.
        UI.disposeOfAllFrames();
        for (int i = 0; i < showing.length; i++) {
            assert UI.findNamedComponent( names[i] ) == null;
        }
        return true;
    }

    public boolean findComponentInTest() {
        final String[] texts = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final JButton[] buttons1 = new JButton[texts.length];
        final JButton[] buttons2 = new JButton[texts.length];
        final Box[] boxes = new Box[2];
        final JFrame[] frames = new JFrame[2];
        Runnable setupTask = new Runnable() {
            public void run() {
                boxes[0] = Box.createVerticalBox();
                boxes[1] = Box.createVerticalBox();
                for (int i = 0; i < buttons1.length; i++) {
                    buttons1[i] = new JButton( texts[i] );
                    boxes[0].add( buttons1[i] );
                    buttons2[i] = new JButton( texts[i] );
                    boxes[1].add( buttons2[i] );
                }
                frames[0] = new JFrame( "frame0" );
                frames[0].add( boxes[0] );
                frames[0].pack();
                frames[1] = new JFrame( "frame1" );
                frames[1].add( boxes[1] );
                frames[1].pack();
                frames[1].setLocation( 400, 300 );
                frames[0].setVisible( true );
                frames[1].setVisible( true );
            }
        };
        UI.runInEventThread( setupTask );
        for (int i = 0; i < texts.length; i++) {
            final int i1 = i;
            Component comp = UI.findComponentIn( frames[0], new UI.ComponentSearchCriterion() {
                public boolean isSatisfied( Component component ) {
                    if (component instanceof JButton) {
                        return ((JButton) component).getText().equals( texts[i1] );
                    } else {
                        return false;
                    }
                }
            } );
            assert comp == buttons1[i];
        }

        cleanup();
        return true;
    }

    public boolean findButtonWithTextTest() {
        final String[] texts = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final JButton[] showing = new JButton[texts.length];
        final JButton[] invisible = new JButton[texts.length];

        Runnable setupTask = new Runnable() {
            public void run() {
                Box labelBox = Box.createVerticalBox();
                for (int i = 0; i < showing.length; i++) {
                    showing[i] = new JButton( texts[i] );
                    invisible[i] = new JButton( texts[i] );
                    labelBox.add( showing[i] );
                }
                setupFrame( labelBox, null );
            }
        };
        UI.runInEventThread( setupTask );

        for (int i = 0; i < showing.length; i++) {
            assert UI.findButtonWithText( texts[i] ) == showing[i];
        }

        //When the frame has been disposed, the labels
        //should no longer be found.
        UI.disposeOfAllFrames();
        for (int i = 0; i < showing.length; i++) {
            assert UI.findButtonWithText( texts[i] ) == null;
        }
        return true;
    }

    public boolean findFileChooserThatIsCurrentlyShowingTest() {
        final JFileChooser[] choosers = new JFileChooser[2];

        Runnable setupTask = new Runnable() {
            public void run() {
                choosers[0] = new JFileChooser();
                choosers[1] = new JFileChooser();
                setupFrame( new JPanel(), null );
            }
        };
        UI.runInEventThread( setupTask );
        Thread shower = new Thread() {
            public void run() {
                choosers[0].showDialog( frame, "chooser0" );
            }
        };
        shower.start();
        Waiting.pause( 400 );//As we've just done something not thread safe.
        assert UI.findFileChooserThatIsCurrentlyShowing() == choosers[0];
        cleanup();
        assert UI.findFileChooserThatIsCurrentlyShowing() == null;
        return true;
    }

    public boolean findNamedFrameTest() {
        final String[] names = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final Frame[] showing = new Frame[names.length];
        final Frame[] invisible = new Frame[names.length];
        Runnable setupTask = new Runnable() {
            public void run() {
                for (int i = 0; i < invisible.length; i++) {
                    showing[i] = new Frame( names[i] );
                    showing[i].setName( names[i] );
                    invisible[i] = new Frame( names[i] );
                    invisible[i].setName( names[i] );
                    showing[i].setVisible( true );
                }
            }
        };
        UI.runInEventThread( setupTask );
        for (int i = 0; i < names.length; i++) {
            assert UI.findNamedFrame( names[i] ) == showing[i];
        }
        cleanup();
        return true;
    }

    public boolean findNamedDialogTest() {
        final String[] names = new String[]{"Pooh",};// "Piglet", "Owl", "Kanga"};
        final Dialog[] dialogs = new Dialog[names.length];
        Runnable setupTask = new Runnable() {
            public void run() {
                for (int i = 0; i < names.length; i++) {
                    dialogs[i] = new Dialog( new Frame(), names[i] );
                    dialogs[i].setName( names[i] );
                    dialogs[i].setVisible( true );
                }
            }
        };
        UI.runInEventThread( setupTask );
        for (int i = 0; i < names.length; i++) {
            assert UI.findNamedDialog( names[i] ) == dialogs[i];
        }
        cleanup();
        return true;
    }

    public boolean disposeOfAllFramesTest() {
        //Create and show some named frames.
        final String[] names = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final Frame[] showing = new Frame[names.length];
        final Frame[] invisible = new Frame[names.length];
        Runnable setupTask = new Runnable() {
            public void run() {
                for (int i = 0; i < invisible.length; i++) {
                    showing[i] = new Frame( names[i] );
                    showing[i].setName( names[i] );
                    invisible[i] = new Frame( names[i] );
                    invisible[i].setName( names[i] );
                    showing[i].setVisible( true );
                }
            }
        };
        UI.runInEventThread( setupTask );
        //Check that some of them are showing.
        for (int i = 0; i < names.length; i++) {
            assert UI.findNamedFrame( names[i] ) == showing[i];
        }
        //Call the method.
        UI.disposeOfAllFrames();
        //The frames should no longer be found.
        for (int i = 0; i < names.length; i++) {
            assert UI.findNamedFrame( names[i] ) == null;
        }
        return true;
    }

    public boolean runInEventThreadTest() {
        final Thread[] threadInWhichMethodCalled = new Thread[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                threadInWhichMethodCalled[0] = Thread.currentThread();
            }
        } );
        assert threadInWhichMethodCalled[0].getName().contains( "EventQueue" );
        return true;
    }

    public boolean findFrameWithTitleTest() {
        JFrame frame1 = UI.createAndShowFrame( "Max" );
        JFrame frame2 = UI.createAndShowFrame( "Agent99" );
        JFrame frame3 = UI.createAndShowFrame( "Chief" );
        assert UI.findFrameWithTitle( "Max" ) == frame1;
        assert UI.findFrameWithTitle( "Agent99" ) == frame2;
        assert UI.findFrameWithTitle( "Chief" ) == frame3;
        cleanup();
        return true;
    }

    public boolean showFrameTest() {
        UI.runInEventThread( new Runnable() {
            public void run() {
                frame = new JFrame( "showFrameTest" );
            }
        } );
        assert !UI.isShowing( frame );
        UI.showFrame( frame );
        assert UI.isShowing( frame );
        cleanup();
        assert !UI.isShowing( frame );
        cleanup();
        return true;
    }

    public boolean createAndShowFrameTest() {
        frame = UI.createAndShowFrame( "createAndShowFrameTest" );
        assert UI.isShowing( frame );
        cleanup();
        assert !UI.isShowing( frame );
        return true;
    }

    public boolean createFrameTest() {
        frame = UI.createFrame( "createFrameTest" );
        assert !UI.isShowing( frame );
        cleanup();
        return true;
    }

    public boolean isSelectedTest() {
        final JRadioButton[] buttons = initWithRadioButtons();
        //At first, no button is selected.
        assert !UI.isSelected( buttons[0] );
        assert !UI.isSelected( buttons[1] );
        assert !UI.isSelected( buttons[2] );
        assert !UI.isSelected( buttons[3] );

        //Select first button.
        borg.space();
        assert UI.isSelected( buttons[0] );
        assert !UI.isSelected( buttons[1] );
        assert !UI.isSelected( buttons[2] );
        assert !UI.isSelected( buttons[3] );

        //Select the second button.
        borg.tab();
        borg.space();
        assert !UI.isSelected( buttons[0] );
        assert UI.isSelected( buttons[1] );
        assert !UI.isSelected( buttons[2] );
        assert !UI.isSelected( buttons[3] );

        //Select the third button.
        borg.tab();
        borg.space();
        assert !UI.isSelected( buttons[0] );
        assert !UI.isSelected( buttons[1] );
        assert UI.isSelected( buttons[2] );
        assert !UI.isSelected( buttons[3] );
        cleanup();
        return true;
    }

    public boolean hasFocusTest() {
        final JRadioButton[] buttons = initWithRadioButtons();
        borg.space();
        assert UI.hasFocus( buttons[0] );
        assert !UI.hasFocus( buttons[1] );
        assert !UI.hasFocus( buttons[2] );
        assert !UI.hasFocus( buttons[3] );

        //Move to second button.
        borg.tab();
        borg.space();
        assert !UI.hasFocus( buttons[0] );
        assert UI.hasFocus( buttons[1] );
        assert !UI.hasFocus( buttons[2] );
        assert !UI.hasFocus( buttons[3] );

        //Move to the third button.
        borg.tab();
        borg.space();
        assert !UI.hasFocus( buttons[0] );
        assert !UI.hasFocus( buttons[1] );
        assert UI.hasFocus( buttons[2] );
        assert !UI.hasFocus( buttons[3] );
        cleanup();
        return true;
    }

    private JRadioButton[] initWithRadioButtons() {
        final String[] labels = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final JRadioButton[] buttons = new JRadioButton[labels.length];
        final ButtonGroup bg = new ButtonGroup();

        Runnable setupTask = new Runnable() {
            public void run() {
                Box labelBox = Box.createVerticalBox();
                for (int i = 0; i < buttons.length; i++) {
                    buttons[i] = new JRadioButton( labels[i] );
                    bg.add( buttons[i] );
                    labelBox.add( buttons[i] );
                }
                setupFrame( labelBox, null );
            }
        };
        UI.runInEventThread( setupTask );
        Waiting.pause( 200 );//See notes below.
        return buttons;
    }

    public boolean isEnabledTest() {
        final String[] labels = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final JButton[] enabled = new JButton[labels.length];
        final JButton[] disabled = new JButton[labels.length];

        Runnable setupTask = new Runnable() {
            public void run() {
                Box labelBox = Box.createVerticalBox();
                for (int i = 0; i < enabled.length; i++) {
                    enabled[i] = new JButton( labels[i] );
                    enabled[i].setEnabled( true );
                    labelBox.add( enabled[i] );
                    disabled[i] = new JButton( labels[i] );
                    disabled[i].setEnabled( false );
                    labelBox.add( disabled[i] );
                }
                setupFrame( labelBox, null );
            }
        };
        UI.runInEventThread( setupTask );

        for (int i = 0; i < labels.length; i++) {
            assert UI.isEnabled( enabled[i] );
            assert !UI.isEnabled( disabled[i] );
        }
        cleanup();
        return true;
    }

    public boolean isShowingTest() {
        //Create and show some named frames.
        final String[] names = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final Frame[] showing = new Frame[names.length];
        final Frame[] invisible = new Frame[names.length];
        Runnable setupTask = new Runnable() {
            public void run() {
                for (int i = 0; i < invisible.length; i++) {
                    showing[i] = new Frame( names[i] );
                    showing[i].setName( names[i] );
                    invisible[i] = new Frame( names[i] );
                    invisible[i].setName( names[i] );
                    showing[i].setVisible( true );
                }
            }
        };
        UI.runInEventThread( setupTask );
        //Check that some of them are showing.
        for (int i = 0; i < names.length; i++) {
            assert UI.isShowing( showing[i] );
            assert !UI.isShowing( invisible[i] );
        }
        UI.disposeOfAllFrames();
        for (int i = 0; i < names.length; i++) {
            assert !UI.isShowing( showing[i] );
            assert !UI.isShowing( invisible[i] );
        }
        return true;
    }

    public boolean getCellPositionOnScreenTest() {
        //Create and show a table with 7 columns and 4 rows.
        //Define the cell renderer so that each cell is
        //80 by 80.
        final JTable[] tables = setUpTable();

        //Usig the table location, and known cell dimensions,
        //calculate the location of each cell, and check
        //against that given by the method.
        Point tableLocation = UI.getScreenLocation( tables[0] );
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 7; col++) {
                int expectedCellX = col * tableCellSize.width
                        + tableLocation.x;
                int expectedCellY = row * tableCellSize.height
                        + tableLocation.y;
                Point expectedLocation =
                        new Point( expectedCellX, expectedCellY );
                Point actualCellPosition =
                        UI.getCellPositionOnScreen( tables[0], row, col );
                assert actualCellPosition.equals( expectedLocation ) :
                        "Expected: " + expectedLocation +
                                ", got: " + actualCellPosition;
            }
        }
        cleanup();
        return true;
    }

    public boolean getColumnHeaderPositionTest() {
        //Create and show a table with 7 columns and 4 rows.
        //Define the cell renderer so that each cell is
        //80 by 80.
        final JTable[] tables = setUpTable();
        Point headerLocation = UI.getScreenLocation( tables[0].getTableHeader() );
        for (int col = 0; col < 7; col++) {
            int expectedCellX = col * tableCellSize.width + headerLocation.x;
            Point expectedLocation = new Point( expectedCellX, headerLocation.y );
            Point actualCellPosition = UI.getColumnHeaderPosition( tables[0], col );
            assert actualCellPosition.equals( expectedLocation ) :
                    "Expected: " + expectedLocation + ", got: " + actualCellPosition;
        }
        cleanup();
        return true;
    }

    private JTable[] setUpTable() {
        final TableCellRenderer tcr = new TableCellRenderer() {
            public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, final int row, final int column ) {
                return new JPanel() {

                    public Dimension getSize( Dimension rv ) {
                        return tableCellSize;
                    }

                    public void paint( Graphics g ) {
                        Color bg = Color.BLACK;
                        if ((row + column) % 2 == 0) {
                            bg = Color.RED;
                        }
                        g.setColor( bg );
                        g.fillRect( 0, 0, tableCellSize.width, tableCellSize.height );
                    }
                };
            }
        };
        final JTable[] tables = new JTable[1];
        Runnable setupTask = new Runnable() {
            public void run() {
                tables[0] = new JTable( createTableItems(), createColumnNames() );
                for (int i = 0; i < 7; i++) {
                    TableColumn column = tables[0].getColumnModel().getColumn( i );
                    column.setCellRenderer( tcr );
                    column.setWidth( tableCellSize.width );
                    column.setPreferredWidth( tableCellSize.width );
                    column.setMaxWidth( tableCellSize.width );
                    column.setMinWidth( tableCellSize.width );
                }
                tables[0].setRowHeight( tableCellSize.height );
                tables[0].setRowHeight( tableCellSize.height );
                setupFrame( new JScrollPane( tables[0] ), new Dimension( 600, 400 ) );
            }
        };
        UI.runInEventThread( setupTask );
        return tables;
    }

    public boolean checkMenuTest() {
        final UserStrings us = TTUserStrings.instance();
        //Wrong text.
        setupMenu( us, new String[]{TTUserStrings.BILBO}, new boolean[]{true}, null );
        boolean[] enabledFlags = new boolean[]{true};
        String[] itemKeys = new String[]{TTUserStrings.FRODO};
        String errorMessage = null;
        try {
            UI.checkMenu( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "At index 0 expected Frodo but got Bilbo" );
        cleanup();

        //Wrongly enabled
        setupMenu( us, new String[]{TTUserStrings.BILBO}, new boolean[]{true}, null );
        enabledFlags = new boolean[]{false};
        itemKeys = new String[]{TTUserStrings.BILBO};
        try {
            UI.checkMenu( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "At index 0 enabled state was: true" );
        cleanup();

        //Wrongly disabled
        setupMenu( us, new String[]{TTUserStrings.BILBO}, new boolean[]{false}, null );
        enabledFlags = new boolean[]{true};
        itemKeys = new String[]{TTUserStrings.BILBO};
        try {
            UI.checkMenu( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "At index 0 enabled state was: false" );
        cleanup();

        //No mnemonic
        setupMenu( us, new String[]{TTUserStrings.PIPPIN}, new boolean[]{true}, null );
        enabledFlags = new boolean[]{true};
        itemKeys = new String[]{TTUserStrings.PIPPIN};
        try {
            UI.checkMenu( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "No mnemonic at index 0" );
        cleanup();

        //Mnemonic used twice.
        setupMenu( us, new String[]{TTUserStrings.FRODO, TTUserStrings.MERRY}, new boolean[]{true, true}, null );
        enabledFlags = new boolean[]{true, true};
        itemKeys = new String[]{TTUserStrings.FRODO, TTUserStrings.MERRY};
        try {
            UI.checkMenu( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "Mnemonic 82 re-used at index 1" );
        cleanup();

        //Almost ok, but more items in the menu than there should be.
        setupMenu( us, new String[]{TTUserStrings.BILBO, TTUserStrings.FRODO, TTUserStrings.SAM}, new boolean[]{true, true, true}, null );
        enabledFlags = new boolean[]{true, true};
        itemKeys = new String[]{TTUserStrings.BILBO, TTUserStrings.FRODO};
        try {
            UI.checkMenu( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "More components than expected" );
        cleanup();

        //A good one.
        setupMenu( us, new String[]{TTUserStrings.BILBO, TTUserStrings.FRODO, TTUserStrings.SAM}, new boolean[]{true, true, true}, null );
        enabledFlags = new boolean[]{true, true, true};
        itemKeys = new String[]{TTUserStrings.BILBO, TTUserStrings.FRODO, TTUserStrings.SAM};
        boolean errorFound = false;
        try {
            UI.checkMenu( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            e.printStackTrace();
            errorFound = true;
        }
        assert !errorFound;
        cleanup();
        return true;
    }

    public boolean checkPopupTest() {
        final UserStrings us = TTUserStrings.instance();
        //Popup not showing.
        setupPopupMenu( us, new String[]{TTUserStrings.BILBO}, new boolean[]{true} );
        borg.escape();
        boolean[] enabledFlags = new boolean[]{true};
        String[] itemKeys = new String[]{TTUserStrings.FRODO};
        String errorMessage = null;
        try {
            UI.checkPopup( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "Popup not found" );
        cleanup();

        //Wrong text.
        setupPopupMenu( us, new String[]{TTUserStrings.BILBO}, new boolean[]{true} );
        enabledFlags = new boolean[]{true};
        itemKeys = new String[]{TTUserStrings.FRODO};
        errorMessage = null;
        try {
            UI.checkPopup( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "At index 0 expected Frodo but got Bilbo" );
        cleanup();

        //Wrongly enabled
        setupPopupMenu( us, new String[]{TTUserStrings.BILBO}, new boolean[]{true} );
        enabledFlags = new boolean[]{false};
        itemKeys = new String[]{TTUserStrings.BILBO};
        try {
            UI.checkPopup( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "At index 0 enabled state was: true" );
        cleanup();

        //Wrongly disabled
        setupPopupMenu( us, new String[]{TTUserStrings.BILBO}, new boolean[]{false} );
        enabledFlags = new boolean[]{true};
        itemKeys = new String[]{TTUserStrings.BILBO};
        try {
            UI.checkPopup( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "At index 0 enabled state was: false" );
        cleanup();

        //No mnemonic
        setupPopupMenu( us, new String[]{TTUserStrings.PIPPIN}, new boolean[]{true} );
        enabledFlags = new boolean[]{true};
        itemKeys = new String[]{TTUserStrings.PIPPIN};
        try {
            UI.checkPopup( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "No mnemonic at index 0" );
        cleanup();

        //Mnemonic used twice.
        setupPopupMenu( us, new String[]{TTUserStrings.FRODO, TTUserStrings.MERRY}, new boolean[]{true, true} );
        enabledFlags = new boolean[]{true, true};
        itemKeys = new String[]{TTUserStrings.FRODO, TTUserStrings.MERRY};
        try {
            UI.checkPopup( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "Mnemonic 82 re-used at index 1" );
        cleanup();

        //Almost ok, but more items in the menu than there should be.
        setupPopupMenu( us, new String[]{TTUserStrings.BILBO, TTUserStrings.FRODO, TTUserStrings.SAM}, new boolean[]{true, true, true} );
        enabledFlags = new boolean[]{true, true};
        itemKeys = new String[]{TTUserStrings.BILBO, TTUserStrings.FRODO};
        try {
            UI.checkPopup( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            errorMessage = e.getMessage();
        }
        assert errorMessage.contains( "More components than expected" );
        cleanup();

        //A good one.
        setupPopupMenu( us, new String[]{TTUserStrings.BILBO, TTUserStrings.FRODO, TTUserStrings.SAM}, new boolean[]{true, true, true} );
        enabledFlags = new boolean[]{true, true, true};
        itemKeys = new String[]{TTUserStrings.BILBO, TTUserStrings.FRODO, TTUserStrings.SAM};
        boolean errorFound = false;
        try {
            UI.checkPopup( TTUserStrings.HOBBITS, us, itemKeys, enabledFlags );
        } catch (AssertionError e) {
            e.printStackTrace();
            errorFound = true;
        }
        assert !errorFound;
        cleanup();
        return true;
    }

    private void setupPopupMenu( final UserStrings us, final String[] keys, final boolean[] enabled ) {
        final JPanel[] panel = new JPanel[1];
        Runnable setupTask = new Runnable() {
            public void run() {
                JFrame frame = new JFrame( "checkPopupTest" );
                panel[0] = new JPanel();
                frame.add( panel[0] );
                frame.setSize( new Dimension( 600, 400 ) );
                frame.setVisible( true );
                frame.toFront();
            }
        };
        UI.runInEventThread( setupTask );
        final JPopupMenu[] popup = new JPopupMenu[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                popup[0] = new JPopupMenu( us.label( TTUserStrings.HOBBITS ) );
                popup[0].setName( TTUserStrings.HOBBITS );
                addMenuItems( popup[0], us, keys, enabled, null );
                popup[0].show( panel[0], 0, 0 );
            }
        } );
    }

    public boolean getTextTest() {
        final String texts = "Achilles' wrath, to Greece the direful spring\n" +
                "Of woes unnumber'd, heavenly goddess, sing!";
        final JTextArea[] textArea = new JTextArea[1];

        Runnable setupTask = new Runnable() {
            public void run() {
                textArea[0] = new JTextArea( texts );
                setupFrame( new JScrollPane( textArea[0] ), null );
            }
        };
        UI.runInEventThread( setupTask );
        assert UI.getText( textArea[0] ).equals( texts );
        cleanup();
        return true;
    }

    public boolean getSelectedTextTest() {
        final String texts = "Achilles' wrath, to Greece the direful spring\n" +
                "Of woes unnumber'd, heavenly goddess, sing!";
        final JTextArea[] textArea = new JTextArea[1];

        Runnable setupTask = new Runnable() {
            public void run() {
                textArea[0] = new JTextArea( texts );
                setupFrame( new JScrollPane( textArea[0] ), null );
            }
        };
        UI.runInEventThread( setupTask );
        assert UI.getSelectedText( textArea[0] ) == null;
        UI.runInEventThread( new Runnable() {
            public void run() {
                textArea[0].select( 10, 26 );
            }
        } );
        //wrath, to Greece
        assert UI.getSelectedText( textArea[0] ).equals( "wrath, to Greece" );
        cleanup();
        return true;
    }

    public boolean getSelectedIndicesTest() {
        final JList[] list = new JList[1];
        Runnable setupTask = new Runnable() {
            public void run() {
                list[0] = new JList( createListItems( 10 ) );
                setupFrame( new JScrollPane( list[0] ), null );
            }
        };
        UI.runInEventThread( setupTask );
        assert Arrays.equals( UI.getSelectedIndices( list[0] ), new int[0] );
        final int[] selection = new int[]{0, 2, 4, 5};
        UI.runInEventThread( new Runnable() {
            public void run() {
                list[0].setSelectedIndices( selection );
            }
        } );
        assert Arrays.equals( UI.getSelectedIndices( list[0] ), selection );
        cleanup();
        return true;
    }

    public boolean getSelectedComboIndexTest() {
        final JComboBox[] combo = setUpWithCombo();
        assert UI.getSelectedComboIndex( combo[0] ) == 0;
        UI.runInEventThread( new Runnable() {
            public void run() {
                combo[0].setSelectedIndex( 7 );
            }
        } );
        assert UI.getSelectedComboIndex( combo[0] ) == 7;
        cleanup();
        return true;
    }

    public boolean getSelectedComboValueTest() {
        final JComboBox[] combo = setUpWithCombo();
        assert UI.getSelectedComboValue( combo[0] ).equals( "item 0" );
        UI.runInEventThread( new Runnable() {
            public void run() {
                combo[0].setSelectedIndex( 7 );
            }
        } );
        assert UI.getSelectedComboValue( combo[0] ).equals( "item 7" );
        cleanup();
        return true;
    }

    public boolean getNumberOfElementsInComboTest() {
        final JComboBox[] combo = setUpWithCombo();
        assert UI.getNumberOfElementsInCombo( combo[0] ) == 10;
        cleanup();
        return true;
    }

    private JComboBox[] setUpWithCombo() {
        final JComboBox[] combo = new JComboBox[1];
        Runnable setupTask = new Runnable() {
            public void run() {
                combo[0] = new JComboBox( createListItems( 10 ) );
                setupFrame( combo[0], null );
            }
        };
        UI.runInEventThread( setupTask );
        return combo;
    }

    //Progress bar
    public boolean findProgressBarThatIsCurrentlyShowingTest() {
        final JProgressBar[] jpb = new JProgressBar[1];
        Runnable setupTask = new Runnable() {
            public void run() {
                jpb[0] = new JProgressBar( 1, 32 );
                setupFrame( jpb[0], null );
            }
        };
        UI.runInEventThread( setupTask );

        assert UI.findProgressBarThatIsCurrentlyShowing().equals( jpb[0] );
        cleanup();
        return true;
    }

    //Tree.
    public boolean getSelectedTreePathTest() {
        initWithTree();
        TreePath path = UI.getSelectedTreePath( tree );
        assert path == null;
        final TreePath tp1 = new TreePath( new Object[]{t, tB, tBB} );
        UI.runInEventThread( new Runnable() {
            public void run() {
                tree.setSelectionPath( tp1 );
            }
        } );
        path = UI.getSelectedTreePath( tree );
        assert path.getPathCount() == 3;
        assert path.getLastPathComponent().equals( tBB );
        cleanup();
        return true;
    }

    //Slider.
    public boolean getSliderValueTest() {
        initWithSlider();
        assert UI.getSliderValue( slider ) == 314;
        cleanup();
        return true;
    }

    //Spinner.
    public boolean getSpinnerValueTest() {
        initWithSpinner();
        assert UI.getSpinnerValue( spinner ).toString().equals( "50" );
        cleanup();
        return true;
    }

    public boolean getScreenLocationTest() {
        //Create and show some named frames.
        final String[] names = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final Point[] locs = new Point[names.length];
        final Frame[] frames = new Frame[names.length];
        Runnable setupTask = new Runnable() {
            public void run() {
                for (int i = 0; i < names.length; i++) {
                    frames[i] = new Frame( names[i] );
                    frames[i].setName( names[i] );
                    locs[i] = new Point( i * 100, i * 100 );
                    frames[i].setVisible( true );
                    frames[i].setLocation( locs[i] );
                }
            }
        };
        UI.runInEventThread( setupTask );
        for (int i = 0; i < frames.length; i++) {
            assert UI.getScreenLocation( frames[i] ).equals( locs[i] );
        }
        cleanup();
        return true;
    }

    public boolean getSizeTest() {
        //Create and show some named frames.
        final String[] names = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final Point[] locs = new Point[names.length];
        final Dimension[] sizes = new Dimension[names.length];
        final Frame[] frames = new Frame[names.length];
        Runnable setupTask = new Runnable() {
            public void run() {
                for (int i = 0; i < names.length; i++) {
                    frames[i] = new Frame();
                    frames[i].setName( names[i] );
                    locs[i] = new Point( i * 100, i * 100 );
                    sizes[i] = new Dimension( (2 + i) * 100, (2 + i) * 100 );
                    frames[i].setVisible( true );
                    frames[i].setLocation( locs[i] );
                    frames[i].setSize( sizes[i] );
                }
            }
        };
        UI.runInEventThread( setupTask );
        for (int i = 0; i < frames.length; i++) {
            assert UI.getSize( frames[i] ).equals( sizes[i] );
        }
        cleanup();
        return true;
    }

    public boolean getTitle_Frame_Test() {
        final String[] names = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final Frame[] frames = new Frame[names.length];
        Runnable setupTask = new Runnable() {
            public void run() {
                for (int i = 0; i < names.length; i++) {
                    frames[i] = new Frame( names[i] );
                    frames[i].setVisible( true );
                }
            }
        };
        UI.runInEventThread( setupTask );
        for (int i = 0; i < frames.length; i++) {
            assert UI.getTitle( frames[i] ).equals( names[i] );
        }
        cleanup();
        return true;
    }

    public boolean getTitle_Dialog_Test() {
        final String[] names = new String[]{"Pooh", "Piglet", "Owl", "Kanga"};
        final Dialog[] dialogs = new Dialog[names.length];
        Runnable setupTask = new Runnable() {
            public void run() {
                for (int i = 0; i < names.length; i++) {
                    dialogs[i] = new Dialog( new Frame(), names[i] );
                    dialogs[i].setVisible( true );
                }
            }
        };
        UI.runInEventThread( setupTask );
        for (int i = 0; i < dialogs.length; i++) {
            assert UI.getTitle( dialogs[i] ).equals( names[i] );
        }
        cleanup();
        return true;
    }
}