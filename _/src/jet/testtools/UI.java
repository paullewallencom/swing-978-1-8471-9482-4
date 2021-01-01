package jet.testtools;

import jet.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;

/**
 * @author Tim Lavers
 */
public final class UI {

    /**
     * Private constructor as we don't want to instantiate this.
     */
    private UI() {}    

    public static interface ComponentSearchCriterion {
        public boolean isSatisfied( Component component );
    }

    public static JLabel findLabelShowingText( final String text ) {
        return (JLabel) findComponentInSomeFrame(
                new ComponentSearchCriterion() {
                    public boolean isSatisfied( Component comp ) {
                        if (!comp.isShowing()) return false;
                        if (comp instanceof JLabel) {
                            return text.equals( ((JLabel) comp).getText() );
                        }
                        return false;
                    }
                } );
    }

    /**
     * The component having the given name, or null if none is found.
     */
    public static Component findNamedComponent( final String name ) {
        return findComponentInSomeFrame(
                new ComponentSearchCriterion() {
                    public boolean isSatisfied( Component component ) {
                        return name.equals( component.getName() );
                    }
                } );
    }

    public static JButton findButtonWithText( final String text ) {
        return (JButton) findComponentInSomeFrame( new ComponentSearchCriterion() {
            public boolean isSatisfied( Component component ) {
                if (component != null && component instanceof JButton) {
                    return ((JButton) component).getText().equals( text );
                }
                return false;
            }
        } );
    }

    public static JFileChooser findFileChooserThatIsCurrentlyShowing() {
        return (JFileChooser) findComponentInSomeFrame(
                new ComponentSearchCriterion() {
                    public boolean isSatisfied( Component component ) {
                        return (component instanceof JFileChooser)
                                && component.isShowing();
                    }
                } );
    }

    public static JProgressBar findProgressBarThatIsCurrentlyShowing() {
        return (JProgressBar) findComponentInSomeFrame(
                new ComponentSearchCriterion() {
                    public boolean isSatisfied( Component component ) {
                        return (component instanceof JProgressBar)
                                && component.isVisible()
                                && component.isShowing();
                    }
                } );
    }

    /**
     * Searches all components of all frames and their subwindows
     * for a component that matches the given criterion,
     * and returns the first match, or null if there
     * is no match.
     */
    private static Component findComponentInSomeFrame(
            final ComponentSearchCriterion criterion ) {
        final Component[] resultHolder = new Component[1];
        runInEventThread( new Runnable() {
            public void run() {
                Frame[] allFrames = Frame.getFrames();
                for (Frame frame : allFrames) {
                    if (!frame.isShowing()) {
                        continue;
                    }
                    Component result = findComponentInWindow(
                            frame, criterion );
                    if (result != null) {
                        resultHolder[0] = result;
                        return;
                    } else {
                        Window[] subWindows = frame.getOwnedWindows();
                        for (Window subWindow : subWindows) {
                            result = findComponentInWindow(
                                    subWindow, criterion );
                            if (result != null) {
                                resultHolder[0] = result;
                                return;
                            }
                        }
                    }
                }
            }
        } );
        return resultHolder[0];
    }

    /**
     * The first found frame that has the given title and
     * is showing.
     */
    public static Frame findFrameWithTitle( String title ) {
        Frame[] allFrames = Frame.getFrames();
        for (Frame allFrame : allFrames) {
            if (!allFrame.isShowing()) {
                continue;
            }
            if (allFrame.getTitle().equals( title )) return allFrame;
        }
        return null;
    }

    /**
     * The first found frame that has the given name and
     * is showing.
     */
    public static Frame findNamedFrame( String name ) {
        Frame[] allFrames = Frame.getFrames();
        for (Frame allFrame : allFrames) {
            if (!allFrame.isShowing()) {
                continue;
            }
            if (allFrame.getName().equals( name )) return allFrame;
        }
        return null;
    }

    /**
     * The first found dialog that has the given name and
     * is showing (though the owning frame need not be showing).
     */
    public static Dialog findNamedDialog( String name ) {
        Frame[] allFrames = Frame.getFrames();
        for (Frame allFrame : allFrames) {
            Window[] subWindows = allFrame.getOwnedWindows();
            for (Window subWindow : subWindows) {
                if (subWindow instanceof Dialog) {
                    Dialog d = (Dialog) subWindow;
                    if (name.equals( d.getName() ) && d.isShowing()) {
                        return (Dialog) subWindow;
                    }
                }
            }
        }
        return null;
    }

    public static void disposeOfAllFrames() {
        Runnable runnable = new Runnable() {
            public void run() {
                Frame[] allFrames = Frame.getFrames();
                for (Frame allFrame : allFrames) {
                    allFrame.dispose();
                }
            }
        };
        runInEventThread( runnable );
    }

    public static Component findComponentIn( final Window w, final ComponentSearchCriterion criterion ) {
        final Component[] resultHolder = new Component[1];
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = findComponentAmongst( w.getComponents(), criterion );
            }
        } );
        return resultHolder[0];
    }

    //Only called from findComponentInSomeFrame,
    //so does not need to be thread-safe.
    private static Component findComponentInWindow(
            Window w, ComponentSearchCriterion criterion ) {
        return findComponentAmongst( w.getComponents(), criterion );
    }

    //Only called from findComponentInWindow, or recursively,
    //so does not need to be thread-safe.
    private static Component findComponentAmongst(
            final Component[] components,
            final ComponentSearchCriterion isMatch ) {
        for (final Component component : components) {
            if (isMatch.isSatisfied( component )) {
                return component;
            }
            if (component instanceof Container) {
                final Component recurse =
                        findComponentAmongst( ((Container) component).
                                getComponents(), isMatch );
                if (recurse != null) {
                    return recurse;
                }
            }
        }
        return null;
    }

    /**
     * Handy wrapper for <code>SwingUtilities.invokeAndWait()</code>
     * that does not try to use the event thread if that is the
     * calling thread.
     */
    public static void runInEventThread( final Runnable r ) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait( r );
                new Cyborg().robot().waitForIdle();
            } catch (Exception e) {
                e.printStackTrace();
                assert false : e.getMessage();
            }
        }
    }

    /**
     * Use the event thread to show a frame. When this method has
     * returned the frame will be showing and to the front.
     */
    public static void showFrame( final JFrame frame ) {
        runInEventThread( new Runnable() {
            public void run() {
                frame.setVisible( true );
                frame.toFront();
            }
        } );
    }

    /**
     * Safely creates a JFrame, but doesn't show it.
     */
    public static JFrame createFrame( final String title ) {
        final JFrame[] resultHolder = new JFrame[1];
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = new JFrame( title );
            }
        } );
        return resultHolder[0];
    }

    /**
     * Creates and then shows a frame.
     *
     * @see #createFrame
     * @see #showFrame
     */
    public static JFrame createAndShowFrame( final String title ) {
        JFrame frame = createFrame( title );
        showFrame( frame );
        return frame;
    }

    /**
     * Safely read the selected state of the given button.
     */
    public static boolean isSelected( final AbstractButton button ) {
        final boolean[] resultHolder = new boolean[]{false};
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = button.isSelected();
            }
        } );
        return resultHolder[0];
    }

    /**
     * Safely read the enabled state of the given component.
     */
    public static boolean isEnabled( final Component component ) {
        final boolean[] resultHolder = new boolean[]{false};
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = component.isEnabled();
            }
        } );
        return resultHolder[0];
    }

    /**
     * Safely read the focused state of the given component.
     */
    public static boolean hasFocus( final Component component ) {
        final boolean[] resultHolder = new boolean[]{false};
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = component.hasFocus();
            }
        } );
        return resultHolder[0];
    }

    /**
     * Safely read the showing state of the given window.
     */
    public static boolean isShowing( final Window window ) {
        final boolean[] resultHolder = new boolean[]{false};
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = window.isShowing();
            }
        } );
        return resultHolder[0];
    }

    //Lists.

    /**
     * Safely read the selected indices in the given list.
     */
    public static int[] getSelectedIndices( final JList list ) {
        final int[][] resultHolder = new int[1][];
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = list.getSelectedIndices();
            }
        } );
        return resultHolder[0];
    }

    //Tables.

    /**
     * A point just inside the indexed cell in the given table.
     */
    public static Point getCellPositionOnScreen( JTable table, int row, int col ) {
        return pointFromCell( table, row, col, false );
    }

    /**
     * A point just insice the indexed header cell in the given table.
     */
    public static Point getColumnHeaderPosition( JTable table, int col ) {
        return pointFromCell( table, -1, col, true );
    }

    private static Point pointFromCell( final JTable table, final int row, final int col, final boolean headerCell ) {
        final Point[] locationInTable = new Point[1];
        runInEventThread( new Runnable() {
            public void run() {
                Rectangle rectangle = rect( table, row, col, headerCell );
                locationInTable[0] = rectangle.getLocation();
            }
        } );
        final Point tableLocationOnScreen = headerCell ? getScreenLocation( table.getTableHeader() ) : getScreenLocation( table );
        return new Point( locationInTable[0].x + tableLocationOnScreen.x,
                locationInTable[0].y + tableLocationOnScreen.y );
    }

    private static Rectangle rect( JTable table, int row, int col, boolean headerCell ) {
        return headerCell ? table.getTableHeader().getHeaderRect( col ) : table.getCellRect( row, col, true );
    }

    //Menus.

    /**
     * Finds the named menu and checks that the i'th item
     * has text <code>us.label( itemKeys[i] )</code> and
     * enabled status equal to <code>enabledFlags[i]</code>.
     * Also checks that every item has a mnemonic, that
     * no mnemonic is used twice, and that there
     * are no more or less items than expected.
     * Any failure will result in an assertion error
     * with a suitable message.
     */
    public static void checkMenu( final String name,
                                  final UserStrings us,
                                  final String[] itemKeys,
                                  final boolean[] enabledFlags ) {
        final String[] problemsFound = new String[1];
        problemsFound[0] = null;

        Runnable problemFinder = new Runnable() {
            public void run() {
                //Get the menu itself.
                JMenu menu = (JMenu) findNamedComponent( name );
                //Check the components..
                checkJMenuItems( menu.getMenuComponents(),
                        enabledFlags, problemsFound, us, itemKeys );
            }
        };
        runInEventThread( problemFinder );
        if (problemsFound[0] != null) {
            assert false : problemsFound[0];
        }
    }

    /**
     * Finds the named popup menu (which fails if the menu
     * is not showing) and checks that the i'th item
     * has text <code>us.label( itemKeys[i] )</code> and
     * enabled status equal to <code>enabledFlags[i]</code>.
     * Also checks that every item has a mnemonic, that
     * no mnemonic is used twice, and that there
     * are no more or less items than expected.
     * Any failure will result in an assertion error
     * with a suitable message.
     */
    public static void checkPopup( final String name,
                                   final UserStrings us,
                                   final String[] itemKeys,
                                   final boolean[] enabledFlags ) {
        final String[] problemsFound = new String[1];
        problemsFound[0] = null;

        Runnable problemFinder = new Runnable() {
            public void run() {
                //Get the popup.
                JPopupMenu menu = (JPopupMenu) findNamedComponent( name );
                if (menu == null || !menu.isVisible()) {
                    problemsFound[0] = "Popup not found";
                    return;
                }
                //Get the components in the menu.
                Component[] components = menu.getComponents();
                checkJMenuItems( components, enabledFlags, problemsFound, us, itemKeys );
            }
        };
        runInEventThread( problemFinder );
        if (problemsFound[0] != null) {
            assert false : problemsFound[0];
        }
    }

    private static void checkJMenuItems( Component[] components,
                                         boolean[] enabledFlags,
                                         String[] problemsFound,
                                         UserStrings us,
                                         String[] itemKeys ) {
        //Iterate over the components. For those that are JMenuItems,
        //check the text, the enabled state, and the mnenonic.
        int index = 0;
        Set<Integer> mnemonicsUsed = new HashSet<Integer>();
        for (Component component : components) {
            if (component instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) component;
                //Check that there are not more
                //components than expected.
                if (index >= enabledFlags.length) {
                    problemsFound[0] = "" +
                            "More components than expected.";
                    break;
                }
                //Check the text.
                String expectedText = us.label( itemKeys[index] );
                if (!expectedText.equals( item.getText() )) {
                    problemsFound[0] = "At index " + index +
                            " expected " + expectedText +
                            " but got " + item.getText();
                    break;
                }
                //Check the enabled state.
                if (item.isEnabled() != enabledFlags[index]) {
                    problemsFound[0] = "At index " + index +
                            " enabled state was: "
                            + item.isEnabled();
                    break;
                }
                //Check that there is a mnemonic.
                int mnemonic = item.getMnemonic();
                if (mnemonic == 0) {
                    problemsFound[0] =
                            "No mnemonic at index " + index;
                    break;
                }
                //Check that the mnemonic
                //has not already been used.
                if (mnemonicsUsed.contains( mnemonic )) {
                    problemsFound[0] =
                            "Mnemonic " + mnemonic +
                                    " re-used at index " + index;
                    break;
                }
                mnemonicsUsed.add( mnemonic );
                //Increment index.
                index++;
            }
        }
        //Check that there are not too few components.
        //Skip this if problems already found, in which
        //case index will be less than the number
        //of components.
        if (problemsFound[0] == null
                && index < enabledFlags.length) {
            problemsFound[0] =
                    "Wrong number of components: " + index;
        }
    }

    //Combo boxes.

    /**
     * Safely read the selected index in the given combo.
     */
    public static int getSelectedComboIndex( final JComboBox combo ) {
        final int[] resultHolder = new int[1];
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = combo.getSelectedIndex();
            }
        } );
        return resultHolder[0];
    }

    /**
     * Safely read the selected value in the given combo.
     */
    public static Object getSelectedComboValue( final JComboBox combo ) {
        final Object[] resultHolder = new Object[1];
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = combo.getSelectedItem();
            }
        } );
        return resultHolder[0];
    }

    /**
     * Safely read the number of elements in the given combo.
     */
    public static int getNumberOfElementsInCombo( final JComboBox combo ) {
        final int[] resultHolder = new int[1];
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = combo.getModel().getSize();
            }
        } );
        return resultHolder[0];
    }

    //JTrees.
    /**
     * Safely read the selected path of the given tree.
     */
    public static TreePath getSelectedTreePath( final JTree tree ) {
        final TreePath[] resultHolder = new TreePath[1];
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = tree.getSelectionPath();
            }
        } );
        return resultHolder[0];
    }

    //JSlider.
    public static int getSliderValue( final JSlider slider ) {
        final int[] resultHolder = new int[1];
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = slider.getValue();
            }
        } );
        return resultHolder[0];
    }

    //JSpinner.
    public static Object getSpinnerValue( final JSpinner spinner ) {
        final Object[] resultHolder = new Object[1];
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = spinner.getValue();
            }
        } );
        return resultHolder[0];
    }

    //Text components.

    /**
     * Safely read the text of the given text component.
     */
    public static String getText( JTextComponent textComponent ) {
        return getTextImpl( textComponent, true );
    }

    /**
     * Safely read the selected text of the given text component.
     */
    public static String getSelectedText( JTextComponent textComponent ) {
        return getTextImpl( textComponent, false );
    }

    private static String getTextImpl(
            final JTextComponent textComponent, final boolean allText ) {
        final String[] resultHolder = new String[]{null};
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = allText ? textComponent.getText() :
                        textComponent.getSelectedText();
            }
        } );
        return resultHolder[0];
    }

    /**
     * Safely read the screen location of the given component.
     */
    public static Point getScreenLocation( final Component component ) {
        final Point[] resultHolder = new Point[]{null};
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = component.getLocationOnScreen();
            }
        } );
        return resultHolder[0];
    }

    /**
     * Safely read the size of the given component.
     */
    public static Dimension getSize( final Component component ) {
        final Dimension[] resultHolder = new Dimension[]{null};
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = component.getSize();
            }
        } );
        return resultHolder[0];
    }

    /**
     * Safely read the title of the given frame.
     */
    public static String getTitle( final Frame frame ) {
        final String[] resultHolder = new String[]{null};
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = frame.getTitle();
            }
        } );
        return resultHolder[0];
    }

    /**
     * Safely read the title of the given dialog.
     */
    public static String getTitle( final Dialog dialog ) {
        final String[] resultHolder = new String[]{null};
        runInEventThread( new Runnable() {
            public void run() {
                resultHolder[0] = dialog.getTitle();
            }
        } );
        return resultHolder[0];
    }
}
