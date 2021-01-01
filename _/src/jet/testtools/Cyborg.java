package jet.testtools;

import jet.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;

/**
 * Extremely versatile and user-friendly wrapper for
 * <code>java.awt.Robot</code>. This is used throughout
 * the book for automated unit tests of user interfaces.
 * This class is discussed in detail in Chapter 4.
 *
 * Note that parts of this class are based
 * on code written at Pacific Knowledge Systems
 * by Martin Manttan and Van Hai Ho.
 * 
 * @author Tim Lavers
 */
public final class Cyborg {

    private Robot robot;

    public Cyborg() {
        try {
            robot = new Robot();
            robot.setAutoWaitForIdle( true );
            robot.setAutoDelay( 5 );
        } catch (AWTException e) {
            e.printStackTrace();
            assert false : "Could not create robot";
        }
    }

    public void type( final String str ) {
        final char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            type( chars[i] );
        }
    }

    /**
     * Enters the text into the currently focused component
     * either by typing it directly or by putting it into
     * the system clipboard and pasting it.
     */
    public void enterText( final String str ) {
        if (str.length() < 3) {
            type( str );
        } else {
            putIntoClipboard( str );
            paste();
        }
    }

    /**
     * Puts the given string into the system clipboard.
     */
    public void putIntoClipboard( final String str ) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard =
                toolkit.getSystemClipboard();
        StringSelection selection = new StringSelection( str );
        clipboard.setContents( selection, selection );
    }

    /**
     * Pastes whatever text is in the system clipboard
     * using CTRL_V.
     */
    public void paste() {
        modifiedKey( KeyEvent.VK_V, KeyEvent.VK_CONTROL );
    }

    public void contextMenu() {
        typeKey( KeyEvent.VK_CONTEXT_MENU );
    }

    public void delete() {
        typeKey( KeyEvent.VK_DELETE );
    }

    public void escape() {
        typeKey( KeyEvent.VK_ESCAPE );
    }

    public void enter() {
        typeKey( KeyEvent.VK_ENTER );
    }

    public void up() {
        typeKey( KeyEvent.VK_UP );
    }

    public void down() {
        typeKey( KeyEvent.VK_DOWN );
    }

    public void right() {
        typeKey( KeyEvent.VK_RIGHT );
    }

    public void left() {
        typeKey( KeyEvent.VK_LEFT );
    }

    public void home() {
        typeKey( KeyEvent.VK_HOME );
    }

    public void end() {
        typeKey( KeyEvent.VK_END );
    }

    public void tab() {
        typeKey( KeyEvent.VK_TAB );
    }

    public void altChar( int mnemonic ) {
        modifiedKey( mnemonic, KeyEvent.VK_ALT );
    }

    public void space() {
        typeKey( KeyEvent.VK_SPACE );
    }

    public void activateFocussedButton() {
        space();
    }

    /**
     * Uses key strokes to close the focussed window.
     * In Windows, the key strokes are alt+F4.
     */
    public void closeTopWindow() {
        modifiedKey( KeyEvent.VK_F4, KeyEvent.VK_ALT );
    }

    /**
     * Uses key strokes to select all text in a text area.
     * In Windows, the key strokes are ctrl+a
     */
    public void selectAllText() {
        modifiedKey( KeyEvent.VK_A, KeyEvent.VK_CONTROL );
    }

    /**
     * Moves the mouse to the given screen coordinates,
     * and then presses and releases the left mouse button.
     */
    public void mouseLeftClick( Point where ) {
        robot.mouseMove( where.x, where.y );
        mouseLeftClick();
    }

    /**
     * Left-clicks the mouse just inside the given component.
     * Will not work for tiny components (width or
     * height < 3).
     */
    public void mouseLeftClick( Component component ) {
        Point componentLocation = UI.getScreenLocation( component );
        Point justInside = new Point( componentLocation.x + 2,
                componentLocation.y + 2 );
        mouseLeftClick( justInside );
    }

    /**
     * Presses and releases the left mouse button.
     */
    public void mouseLeftClick() {
        robot.mousePress( InputEvent.BUTTON1_MASK );
        robot.mouseRelease( InputEvent.BUTTON1_MASK );
    }

    /**
     * Moves the mouse to <code>start</code>, presses
     * the left mouse button, moves the mouse to
     * <code>end</code> and releases the button.
     */
    public void mouseDrag( Point start, Point end ) {
        java.util.List<Point> points = lineJoining( start, end );
        robot.mouseMove( start.x, start.y );
        robot.mousePress( InputEvent.BUTTON1_MASK );
        for (Point o : points) {
            robot.mouseMove( o.x, o.y );
        }
        robot.mouseRelease( InputEvent.BUTTON1_MASK );
    }

    /**
     * The points on the line joining a and b,
     * assuming b is physically to the right and below a.
     * The algorithm is simple: a must be on the line.
     * For each point on the line, either the point immediately
     * to the right or the point to the right and down
     * or the point immediately below, is on the line too,
     * unless we have gotten to b.
     */
    private java.util.List<Point> lineJoining( Point a, Point b ) {
        LinkedList<Point> result = new LinkedList<Point>();
        result.add( a );
        double[] slopeIntercept = lineFormula( a, b );
        while (result.getLast().x <= b.x && result.getLast().y <= b.y) {
            Point last = result.getLast();
            //Substitute in the next x value.
            double yAtNextX = slopeIntercept[0] * (last.x + 1) + slopeIntercept[1];
            if (yAtNextX >= last.y && yAtNextX < (last.y + 1)) {
                result.add( new Point( last.x + 1, last.y ) );
            } else if (yAtNextX == last.y + 1) {
                result.add( new Point( last.x + 1, last.y + 1 ) );
            } else {
                result.add( new Point( last.x, last.y + 1 ) );
            }
        }
        return result;
    }

    private double[] lineFormula( Point a, Point b ) {
        double rise = b.y - a.y;
        double run = b.x - a.x;
        double intercept = (b.y * a.x - a.y * b.x) / (a.x - b.x);
        return new double[]{rise / run, intercept};
    }

    /**
     * The java.awt.Robot that it used to perform all actions.
     */
    public Robot robot() {
        return robot;
    }

    /**
     * Check that the pixel at the given co-ordinates
     * of the given component is the expected colour.
     * The co-ordinates are in the co-ordinate space
     * of the component; to check the top left pixel
     * use relativeX = 0, relativeY=0.
     */
    public void checkPixelInComponent( final int relativeX,
                                       final int relativeY,
                                       Color expected,
                                       final Component component ) {
        int actualX = relativeX + UI.getScreenLocation( component ).x;
        int actualY = relativeY + UI.getScreenLocation( component ).y;
        Color actual = safelyGetPixelColour( actualX, actualY );
        String errMsg = "relativeX: " + relativeX + ", relativeY: "
                + relativeY + ", actualX: " + actualX + ", actualY: "
                + actualY + ", expected: " + expected
                + ", got: " + actual;
        assert actual.equals( expected ) : errMsg;
    }

    /**
     * Retrieves the colour of the pixel at the given
     * co-ordinates; does this from within the event thread.
     */
    public Color safelyGetPixelColour( final int x, final int y ) {
        final Color[] result = new Color[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                result[0] = robot.getPixelColor( x, y );
            }
        } );
        return result[0];
    }

    /**
     * Asserts that the colour at the given screen
     * co-ordinates equals that given.
     */
    public void checkPixel( int x, int y, Color expected ) {
        Color actual = safelyGetPixelColour( x, y );
        String errMsg = "x: " + x + ", y: "
                + y + ", expected: " + expected
                + ", got: " + actual;
        assert actual.equals( expected ) : errMsg;
    }

    //List manipulation methods.
    /**
     * Assuming that a list is in focus, selects the first element
     * and de-selects any others that are currently selected.
     */
    public void selectFirstOnlyInList() {
        home();
    }

    /**
     * Assuming that a list is in focus, selects the last element
     * and de-selects any others that are currently selected.
     */
    public void selectLastOnlyInList() {
        end();
    }

    /**
     * Assuming that a list is in focus, selects the given indices,
     * and de-selects any others that are currently selected.
     *
     * @param toSelect the indices to be selected, in increasing order.
     */
    public void selectListIndicesOnly(
            final int[] toSelect, int listSize ) {
        //Select just the first element.
        selectFirstOnlyInList();
        //An array of flags representing the indices to be selected.
        boolean[] toBeSelected = new boolean[listSize];
        for (int i = 0; i < toSelect.length; i++) {
            toBeSelected[toSelect[i]] = true;
        }
        //Now do the selections....
        //Press the control button.
        robot.keyPress( KeyEvent.VK_CONTROL );
        //Do we need to de-select the first item?
        if (!toBeSelected[0]) space();
        //Go through the remaining items.
        for (int i = 1; i < listSize; i++) {
            //Move to the item.
            down();
            //Select if necessary.
            if (toBeSelected[i]) space();
        }
        //Release the control button.
        robot.keyRelease( KeyEvent.VK_CONTROL );
    }

    //JComboBox
    /**
     * Assuming that a combo box is in focus, selects the first element.
     */
    public void selectFirstInCombo() {
        home();
    }

    /**
     * Assuming that a combo box is in focus, selects the last element.
     */
    public void selectLastInCombo() {
        end();
    }

    /**
     * Assuming that a combo box is in focus,
     * selects the indexed element.
     */
    public void selectElementInComboByIndex( int index ) {
        selectFirstInCombo();//So we know where we are.
        space();//Brings up the list view.
        for (int i = 0; i < index; i++) down();
        enter();//Lock it in!
    }

    //JTree.
    /**
     * Selects the root node of the currently
     * focussed tree.
     */
    public void selectTreeRoot() {
        home();
    }

    /**
     * Expands at least as many tree nodes are required,
     * and then arrows down to the indexed item.
     */
    public void selectTreeItemByDepthFirstIndex( int index ) {
        selectTreeRoot();
        int maxNumRightArrowsNeeded = 2 * index;
        for (int i = 0; i < maxNumRightArrowsNeeded; i++) {
            right();
            right();
            down();
        }
        selectTreeRoot();
        for (int i = 0; i < index; i++) {
            down();
        }
    }

    //JTable
    /**
     * Click just inside the indexed cell of the given table.
     */
    public void clickTableCell( JTable table, int row, int column ) {
        mouseLeftClickInsideComponentAt(
                UI.getCellPositionOnScreen( table, row, column ) );
    }

    /**
     * Click just inside the indexed header cell of the given table.
     */
    public void clickTableHeader( JTable table, int column ) {
        mouseLeftClickInsideComponentAt(
                UI.getColumnHeaderPosition( table, column ) );
    }

    public void dragTableColumn(
            JTable table, int column, int destination ) {
        Point from = pointJustInsideComponentAt(
                UI.getColumnHeaderPosition( table, column ) );
        Point to = pointJustInsideComponentAt(
                UI.getColumnHeaderPosition( table, destination ) );
        mouseDrag( from, to );
    }

    private void mouseLeftClickInsideComponentAt( Point position ) {
        mouseLeftClick( pointJustInsideComponentAt( position ) );
    }

    private Point pointJustInsideComponentAt( Point position ) {
        return new Point( position.x + 5, position.y + 5 );
    }

    //Frame manipulation.
    /**
     * Move the given frame by dragging with the mouse.
     *
     * @param frame  the frame to be moved
     * @param deltaX the horizontal displacement
     * @param deltaY the vertical displacement
     */
    public void dragFrame( Frame frame, int deltaX, int deltaY ) {
        Point initialLocation = UI.getScreenLocation( frame );
        Point destination = new Point( initialLocation );
        destination.translate( deltaX, deltaY );
        //From trial and error, we know that clicking
        //40 pixels to the left, and 5 down, from the top
        //left corner of a frame allows us to drag it.
        //Adjust the locations accordingly.
        initialLocation.translate( 40, 5 );
        destination.translate( 40, 5 );
        mouseDrag( initialLocation, destination );
    }

    /**
     * Re-size the given frame by dragging the lower
     * right corner of it with the mouse.
     * The re-size might be one or two pixels off.
     *
     * @param frame       the frame to be resized.
     * @param deltaWidth  the increase in width
     * @param deltaHeight the increase in height.
     */
    public void resizeFrame( Frame frame,
                             int deltaWidth, int deltaHeight ) {
        Point location = UI.getScreenLocation( frame );
        Dimension initialSize = UI.getSize( frame );
        //Click just inside the bottom right corner.
        Point placeToClick = new Point( location.x + initialSize.width - 2,
                location.y + initialSize.height - 2 );
        Point placeToDragTo = new Point( placeToClick.x + deltaWidth,
                placeToClick.y + deltaHeight );
        mouseDrag( placeToClick, placeToDragTo );
    }

    //Menus

    /**
     * Activates the menu using the mnemonic
     * <code>us.mnemonic( menuKey )</code> and then
     * activates the menu item with mnemonic
     * <code>us.mnemonic( itemKey )</code>.
     */
    public void activateMenuItem(
            UserStrings us,
            String menuKey, String itemKey ) {
        altChar( us.mnemonic( menuKey ) );
        type( (char) us.mnemonic( itemKey ).intValue() );
    }

    /**
     * Press then release the given key. Assert fail if the key could
     * not be typed (which presumably means that the current platform
     * does not support that key).
     *
     * @param key a KeyEvent constant, eg KeyEvent.VK_0.
     */
    private void typeKey( final int key ) {
        try {
            pause();
            robot.keyPress( key );
            pause();
            robot.keyRelease( key );
            pause();
        } catch (Exception e) {
            assert false : "Invalid key code. As int: '"
                    + key + "', as char: '" + (char) key + "'";
        }
    }

    private void modifiedKey( final int key, final int modifier ) {
        pause();
        robot.keyPress( modifier );
        pause();
        typeKey( key );
        robot.keyRelease( modifier );
        pause();
    }

    private void pause() {
        robot.waitForIdle();
        Waiting.pause( 10 );
        robot.waitForIdle();
    }

    /**
     * Types in the given character, using the keys available on a standard
     * Windows-compatible US keyboard.
     */
    private void type( char c ) {
        switch (c) {
            //Digits.
            case '0':
                typeKey( KeyEvent.VK_0 );
                break;
            case '1':
                typeKey( KeyEvent.VK_1 );
                break;
            case '2':
                typeKey( KeyEvent.VK_2 );
                break;
            case '3':
                typeKey( KeyEvent.VK_3 );
                break;
            case '4':
                typeKey( KeyEvent.VK_4 );
                break;
            case '5':
                typeKey( KeyEvent.VK_5 );
                break;
            case '6':
                typeKey( KeyEvent.VK_6 );
                break;
            case '7':
                typeKey( KeyEvent.VK_7 );
                break;
            case '8':
                typeKey( KeyEvent.VK_8 );
                break;
            case '9':
                typeKey( KeyEvent.VK_9 );
                break;

                //Lower case letters.
            case 'a':
                typeKey( KeyEvent.VK_A );
                break;
            case 'b':
                typeKey( KeyEvent.VK_B );
                break;
            case 'c':
                typeKey( KeyEvent.VK_C );
                break;
            case 'd':
                typeKey( KeyEvent.VK_D );
                break;
            case 'e':
                typeKey( KeyEvent.VK_E );
                break;
            case 'f':
                typeKey( KeyEvent.VK_F );
                break;
            case 'g':
                typeKey( KeyEvent.VK_G );
                break;
            case 'h':
                typeKey( KeyEvent.VK_H );
                break;
            case 'i':
                typeKey( KeyEvent.VK_I );
                break;
            case 'j':
                typeKey( KeyEvent.VK_J );
                break;
            case 'k':
                typeKey( KeyEvent.VK_K );
                break;
            case 'l':
                typeKey( KeyEvent.VK_L );
                break;
            case 'm':
                typeKey( KeyEvent.VK_M );
                break;
            case 'n':
                typeKey( KeyEvent.VK_N );
                break;
            case 'o':
                typeKey( KeyEvent.VK_O );
                break;
            case 'p':
                typeKey( KeyEvent.VK_P );
                break;
            case 'q':
                typeKey( KeyEvent.VK_Q );
                break;
            case 'r':
                typeKey( KeyEvent.VK_R );
                break;
            case 's':
                typeKey( KeyEvent.VK_S );
                break;
            case 't':
                typeKey( KeyEvent.VK_T );
                break;
            case 'u':
                typeKey( KeyEvent.VK_U );
                break;
            case 'v':
                typeKey( KeyEvent.VK_V );
                break;
            case 'w':
                typeKey( KeyEvent.VK_W );
                break;
            case 'x':
                typeKey( KeyEvent.VK_X );
                break;
            case 'y':
                typeKey( KeyEvent.VK_Y );
                break;
            case 'z':
                typeKey( KeyEvent.VK_Z );
                break;

                //Upper case letters: use the shift key.
            case 'A':
                modifiedKey( KeyEvent.VK_A, KeyEvent.VK_SHIFT );
                break;
            case 'B':
                modifiedKey( KeyEvent.VK_B, KeyEvent.VK_SHIFT );
                break;
            case 'C':
                modifiedKey( KeyEvent.VK_C, KeyEvent.VK_SHIFT );
                break;
            case 'D':
                modifiedKey( KeyEvent.VK_D, KeyEvent.VK_SHIFT );
                break;
            case 'E':
                modifiedKey( KeyEvent.VK_E, KeyEvent.VK_SHIFT );
                break;
            case 'F':
                modifiedKey( KeyEvent.VK_F, KeyEvent.VK_SHIFT );
                break;
            case 'G':
                modifiedKey( KeyEvent.VK_G, KeyEvent.VK_SHIFT );
                break;
            case 'H':
                modifiedKey( KeyEvent.VK_H, KeyEvent.VK_SHIFT );
                break;
            case 'I':
                modifiedKey( KeyEvent.VK_I, KeyEvent.VK_SHIFT );
                break;
            case 'J':
                modifiedKey( KeyEvent.VK_J, KeyEvent.VK_SHIFT );
                break;
            case 'K':
                modifiedKey( KeyEvent.VK_K, KeyEvent.VK_SHIFT );
                break;
            case 'L':
                modifiedKey( KeyEvent.VK_L, KeyEvent.VK_SHIFT );
                break;
            case 'M':
                modifiedKey( KeyEvent.VK_M, KeyEvent.VK_SHIFT );
                break;
            case 'N':
                modifiedKey( KeyEvent.VK_N, KeyEvent.VK_SHIFT );
                break;
            case 'O':
                modifiedKey( KeyEvent.VK_O, KeyEvent.VK_SHIFT );
                break;
            case 'P':
                modifiedKey( KeyEvent.VK_P, KeyEvent.VK_SHIFT );
                break;
            case 'Q':
                modifiedKey( KeyEvent.VK_Q, KeyEvent.VK_SHIFT );
                break;
            case 'R':
                modifiedKey( KeyEvent.VK_R, KeyEvent.VK_SHIFT );
                break;
            case 'S':
                modifiedKey( KeyEvent.VK_S, KeyEvent.VK_SHIFT );
                break;
            case 'T':
                modifiedKey( KeyEvent.VK_T, KeyEvent.VK_SHIFT );
                break;
            case 'U':
                modifiedKey( KeyEvent.VK_U, KeyEvent.VK_SHIFT );
                break;
            case 'V':
                modifiedKey( KeyEvent.VK_V, KeyEvent.VK_SHIFT );
                break;
            case 'W':
                modifiedKey( KeyEvent.VK_W, KeyEvent.VK_SHIFT );
                break;
            case 'X':
                modifiedKey( KeyEvent.VK_X, KeyEvent.VK_SHIFT );
                break;
            case 'Y':
                modifiedKey( KeyEvent.VK_Y, KeyEvent.VK_SHIFT );
                break;
            case 'Z':
                modifiedKey( KeyEvent.VK_Z, KeyEvent.VK_SHIFT );
                break;
                //Symbols that are shifted numbers.
            case '!':
                modifiedKey( KeyEvent.VK_1, KeyEvent.VK_SHIFT );
                break;
            case '@':
                modifiedKey( KeyEvent.VK_2, KeyEvent.VK_SHIFT );
                break;
            case '#':
                modifiedKey( KeyEvent.VK_3, KeyEvent.VK_SHIFT );
                break;
            case '$':
                modifiedKey( KeyEvent.VK_4, KeyEvent.VK_SHIFT );
                break;
            case '%':
                modifiedKey( KeyEvent.VK_5, KeyEvent.VK_SHIFT );
                break;
            case '^':
                modifiedKey( KeyEvent.VK_6, KeyEvent.VK_SHIFT );
                break;
            case '&':
                modifiedKey( KeyEvent.VK_7, KeyEvent.VK_SHIFT );
                break;
            case '*':
                modifiedKey( KeyEvent.VK_8, KeyEvent.VK_SHIFT );
                break;
            case '(':
                modifiedKey( KeyEvent.VK_9, KeyEvent.VK_SHIFT );
                break;
            case ')':
                modifiedKey( KeyEvent.VK_0, KeyEvent.VK_SHIFT );
                break;
                //Other symbols.
            case ' ':
                typeKey( KeyEvent.VK_SPACE );
                break;
            case '-':
                typeKey( KeyEvent.VK_MINUS );
                break;
            case '_':
                modifiedKey( KeyEvent.VK_MINUS, KeyEvent.VK_SHIFT );
                break;
            case '=':
                typeKey( KeyEvent.VK_EQUALS );
                break;
            case '+':
                modifiedKey( KeyEvent.VK_EQUALS, KeyEvent.VK_SHIFT );
                break;
            case '[':
                typeKey( KeyEvent.VK_OPEN_BRACKET );
                break;
            case '{':
                modifiedKey( KeyEvent.VK_OPEN_BRACKET, KeyEvent.VK_SHIFT );
                break;
            case ']':
                typeKey( KeyEvent.VK_CLOSE_BRACKET );
                break;
            case '}':
                modifiedKey( KeyEvent.VK_CLOSE_BRACKET, KeyEvent.VK_SHIFT );
                break;
            case '\\':
                typeKey( KeyEvent.VK_BACK_SLASH );
                break;
            case '|':
                modifiedKey( KeyEvent.VK_BACK_SLASH, KeyEvent.VK_SHIFT );
                break;
            case ';':
                typeKey( KeyEvent.VK_SEMICOLON );
                break;
            case ':':
                modifiedKey( KeyEvent.VK_SEMICOLON, KeyEvent.VK_SHIFT );
                break;
            case ',':
                typeKey( KeyEvent.VK_COMMA );
                break;
            case '<':
                modifiedKey( KeyEvent.VK_COMMA, KeyEvent.VK_SHIFT );
                break;
            case '.':
                typeKey( KeyEvent.VK_PERIOD );
                break;
            case '>':
                modifiedKey( KeyEvent.VK_PERIOD, KeyEvent.VK_SHIFT );
                break;
            case '/':
                typeKey( KeyEvent.VK_SLASH );
                break;
            case '?':
                modifiedKey( KeyEvent.VK_SLASH, KeyEvent.VK_SHIFT );
                break;
            case '\n':
                enter();
                break;

            default:
                assert false : "Did not know how to type: '" + c + "'";
        }
    }
}