package jet.ikonmaker.test;

import jet.ikonmaker.Ikon;
import jet.ikonmaker.IkonMakerUserStrings;
import jet.ikonmaker.VisualOpenDialog;
import jet.testtools.UI;
import jet.testtools.Waiting;
import jet.testtools.test.ikonmaker.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Tim Lavers
 */
public class VisualOpenDialogTest {

    private JFrame frame;
    private VisualOpenDialog od;
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();
    private UIOpenDialog ui;

    public boolean constructorTest() throws IOException {
        init();

        //Check the title.
        assert UI.getTitle( od.dialog() ).equals(
                us.message( IkonMakerUserStrings.OPEN_IKON_MSG0 ) );

        //Ok not enabled.
        assert !UI.isEnabled( ui.okButton() );

        //Cancel enabled.
        assert UI.isEnabled( ui.cancelButton() );

        //List has focus.
        assert UI.hasFocus( ui.namesList() );

        //Check the order of the elements.
        assert ui.selected() == null;

        ui.robot.down();
        assert ui.selected().equals( IkonTest.in( "first" ) );

        ui.robot.down();
        assert ui.selected().equals( IkonTest.in( "second" ) );

        ui.robot.down();
        assert ui.selected().equals( IkonTest.in( "third" ) );

        ui.robot.down();
        assert ui.selected().equals( IkonTest.in( "ufourth" ) );

        ui.robot.down();
        assert ui.selected().equals( IkonTest.in( "vfifth" ) );

        ui.robot.down();
        assert ui.selected().equals( IkonTest.in( "wsixth" ) );

        //Check that the list allows only single selection.
        assert ui.namesList().getSelectionMode()
                == ListSelectionModel.SINGLE_SELECTION;
        cleanup();
        return true;
    }

    public boolean listRendererTest() {
        init();
        //Get the list object and from it the renderer.
        final JList list = ui.namesList();
        //Have to get the renderer in the event thread.
        final ListCellRenderer[] lcrHolder = new ListCellRenderer[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                lcrHolder[0] = list.getCellRenderer();
            }
        } );
        final ListCellRenderer lcr = lcrHolder[0];

        //An ikon to be rendered.
        String name = "IkonName";
        final Ikon ike = buildIkon( name, false );

        //If not selected, the border should be
        //black and one pixel wide.
        //Have to get the component in the event thread.
        final Component[] componentHolder = new Component[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                componentHolder[0] =
                         lcr.getListCellRendererComponent(
                                 list, ike, 0, false, false );
            }
        } );
        Box box = (Box) componentHolder[0];
        LineBorder border = (LineBorder) box.getBorder();
        assert border.getLineColor().equals( Color.BLACK );
        assert border.getThickness() == 1;

        cleanup();
        return true;
    }

    public boolean dialogTest() {
        //Check the size when small icons are used.
        init();
        Dimension size = UI.getSize( od.dialog() );
        assert size.height >= ( VisualOpenDialog.MINIMUM_SIZE.height );
        assert size.width >= ( VisualOpenDialog.MINIMUM_SIZE.width );
        assert size.height <= ( VisualOpenDialog.MAXIMUM_SIZE.height );
        assert size.width <= ( VisualOpenDialog.MAXIMUM_SIZE.width );
        cleanup();

        //Check the size with big icons.
        init( true );
        size = UI.getSize( od.dialog() );
        assert size.height >= ( VisualOpenDialog.MINIMUM_SIZE.height );
        assert size.width >= ( VisualOpenDialog.MINIMUM_SIZE.width );
        assert size.height <= ( VisualOpenDialog.MAXIMUM_SIZE.height );
        assert size.width <= ( VisualOpenDialog.MAXIMUM_SIZE.width );
        cleanup();
        return true;
    }

    public boolean showTest() {
        //show() is used in init() so is tested in all other tests.
        return true;
    }

    public boolean wasCancelledTest() {
        //Ok a selection.
        init();
        assert !wasCancelled();
        ui.robot.down();
        ui.ok();
        assert !UI.isShowing( od.dialog() );
        assert !wasCancelled();
        cleanup();

        //Cancel before a selection has been made.
        init();
        ui.cancel();
        assert !UI.isShowing( od.dialog() );
        assert wasCancelled();
        cleanup();

        //Cancel after a selection has been made.
        init();
        ui.robot.down();
        assert ui.selected().equals( IkonTest.in( "first" ) );
        ui.cancel();
        assert !UI.isShowing( od.dialog() );
        assert wasCancelled();
        cleanup();
        return true;
    }

    public boolean selectedNameTest() {
        init();
        assert od.selectedName() == null;
        ui.robot.down();
        assert ui.selected().equals( IkonTest.in( "first" ) );
        ui.ok();
        assert ui.selected().equals( IkonTest.in( "first" ) );
        cleanup();
        return true;
    }

    public boolean usabilityTest() {
        init();

        //Check that 'escape' cancels.
        ui.robot.escape();
        assert !UI.isShowing( od.dialog() );
        assert wasCancelled();
        cleanup();

        //Check that 'enter' is like 'ok'.
        init();
        ui.robot.down();
        assert ui.selected().equals( IkonTest.in( "first" ) );
        ui.robot.enter();
        assert ui.selected().equals( IkonTest.in( "first" ) );
        assert !UI.isShowing( od.dialog() );
        assert !wasCancelled();

        cleanup();
        return true;
    }

    private boolean wasCancelled() {
        final boolean[] result = new boolean[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                result[0] = od.wasCancelled();
            }
        } );
        return result[0];
    }

    private void init() {
        init( false );
    }

    private void init( boolean bigIcons ) {
        Runnable creater = new Runnable() {
            public void run() {
                frame = new JFrame( "OpenDialogTest" );
                frame.setLayout( new BoxLayout( frame.getContentPane(), BoxLayout.X_AXIS ) );
                frame.pack();
            }
        };
        UI.runInEventThread( creater );
        UI.showFrame( frame );
        SortedSet<Ikon> names = new TreeSet<Ikon>( new Comparator<Ikon>() {
            public int compare( Ikon ikon, Ikon ikon1 ) {
                return ikon.name().compareTo( ikon1.name() );
            }
        } );
        names.add( buildIkon( "first", bigIcons ) );
        names.add( buildIkon( "second", bigIcons ) );
        names.add( buildIkon( "third", bigIcons ) );
        names.add( buildIkon( "ufourth", bigIcons ) );
        names.add( buildIkon( "Vfifth", bigIcons ) );
        names.add( buildIkon( "wsixth", bigIcons ) );
        names.add( buildIkon( "zzThisIsAVeryLongNameForAnIkonButThenSomeOfThemDoHaveVeryLongNames", bigIcons ) );
        for (int i = 0; i < 20; i++) {
            names.add( buildIkon( "zzzzzz" + i, bigIcons ) );
        }

        od = new VisualOpenDialog( frame, names );
        //Start a thread to show the dialog (it is modal).
        new Thread( "DialogShower" ) {
            public void run() {
                od.show();
            }
        }.start();
        //Wait for the dialog to be showing.
        Waiting.waitFor( new Waiting.ItHappened() {
            public boolean itHappened() {
                return UI.isShowing( od.dialog() );
            }
        }, 1000 );

        Waiting.pause( 300 );//Just to wait for paint events.
        ui = new UIOpenDialog();
    }

    private Ikon buildIkon( String name, boolean bigIcon ) {
        File sonia = new File(  bigIcon ? Ikonmaker.waratah_jpg: Ikonmaker.Red_png );
        BufferedImage bim = null;
        try {
            bim = ImageIO.read( sonia );
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Could not load image";
        }
        int width = bim.getWidth();
        int height = bim.getHeight();
        int scale = bigIcon ? 5 : 1;
        Ikon ike = new Ikon( width / scale, height / scale, Color.RED, IkonTest.in( name ) );
        ike.fillWithImage( bim );
        return ike;
    }

    private void cleanup() {
        UI.runInEventThread( new Runnable() {
            public void run() {
                frame.dispose();
            }
        } );
    }
}