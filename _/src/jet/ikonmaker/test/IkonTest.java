package jet.ikonmaker.test;

import jet.ikonmaker.*;
import jet.testtools.test.ikonmaker.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;

/**
 * @author Tim Lavers
 */
public class IkonTest {
    
    public static IkonName in( String str ) {
        return new IkonName( str );
    }

    public boolean constructorTest() {
        Ikon ike = new Ikon( 2, 3, Color.BLACK, in( "ike" ) );
        assert  ike.height() == 3;
        assert  ike.width() == 2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                assert ike.colourAt( i, j ).equals( Color.BLACK );
            }
        }
        return true;
    }

    public boolean heightTest() {
        Ikon ike = new Ikon( 2, 334, Color.BLACK, in( "ike" ) );
        assert  ike.height() == 334;
        return true;
    }

    public boolean compareToTest() {
        Ikon ike1 = new Ikon( 2, 334, Color.BLACK, in( "ike" ) );
        Ikon ike2 = new Ikon( 2, 334, Color.BLACK, in( "Ike" ) );
        Ikon ike3 = new Ikon( 2, 334, Color.BLACK, in( "zike" ) );
        Ikon ike4 = new Ikon( 2, 334, Color.BLACK, in( "aike" ) );
        assert ike1.compareTo( ike2 ) == 0;
        assert ike1.compareTo( ike3 ) < 0;
        assert ike1.compareTo( ike4 ) > 0;
        return true;
    }

    public boolean setColourAtTest() {
        Ikon ike = new Ikon( 2, 3, Color.BLACK, in( "ike" ) );
        assert  ike.height() == 3;
        assert  ike.width() == 2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                assert ike.colourAt( i, j ).equals( Color.BLACK );
                ike.setColourAt( Color.RED, i, j );
                assert ike.colourAt( i, j ).equals( Color.RED );
            }
        }
        return true;
    }

    public boolean addListenerTest() {
        class L implements Ikon.Listener {
            boolean changeRegistered = false;

            public void ikonChanged( EditOperation eo ) {
                changeRegistered = true;
            }
        }
        Ikon ike = new Ikon( 2, 3, Color.BLACK, in( "ike" ) );
        L l1 = new L();
        L l2 = new L();
        ike.addListener( l1 );
        ike.addListener( l2 );
        ike.setColourAt( Color.CYAN, 0, 0 );
        assert l1.changeRegistered;
        assert l2.changeRegistered;
        return true;
    }

    public boolean copyTest() {
        Ikon ike = new Ikon( 2, 3, Color.BLACK, in( "ike" ) );
        Ikon copy = ike.copy( in( "dwight" ) );
        assert copy.name().equals( in( "dwight" ) );
        assert copy.width() == 2;
        assert copy.height() == 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                assert copy.colourAt( i, j ).equals( Color.BLACK );
            }
        }
        return true;
    }

    public boolean fillWithImageTest() throws Exception {
        //First a simple test where a white image is filled with an equal sized black one.
        Ikon ike1 = new Ikon( 2, 3, Color.BLACK, in( "ike1" ) );//2 wide, 3 high
        Ikon ike2 = new Ikon( 2, 3, Color.WHITE, in( "ike2" ) );//Same dimensions but white.
        ike2.fillWithImage( ike1.asImage() );
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                assert ike2.colourAt( i, j ).equals( Color.BLACK );
            }
        }

        //Now one where the fill image is twice as long as the original.
        ike1 = new Ikon( 4, 3, Color.BLACK, in( "ike1" ) );
        ike2 = new Ikon( 2, 3, Color.WHITE, in( "ike2" ) );
        ike2.fillWithImage( ike1.asImage() );
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                assert ike2.colourAt( i, j ).equals( Color.BLACK );
            }
        }

        //Now one where the fill image is twice as long and twice as wide as the original.
        ike1 = new Ikon( 4, 6, Color.BLACK, in( "ike1" ) );
        ike2 = new Ikon( 2, 3, Color.WHITE, in( "ike2" ) );
        ike2.fillWithImage( ike1.asImage() );
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                assert ike2.colourAt( i, j ).equals( Color.BLACK );
            }
        }

        //As above, but a chequerboard pattern that should get averaged to grey.
        ike1 = new Ikon( 4, 6, Color.BLACK, in( "ike1" ) );
        ike1.setColourAt( Color.WHITE, 0, 0 );
        ike1.setColourAt( Color.WHITE, 2, 0 );
        ike1.setColourAt( Color.WHITE, 4, 0 );
        ike1.setColourAt( Color.WHITE, 1, 1 );
        ike1.setColourAt( Color.WHITE, 3, 1 );
        ike1.setColourAt( Color.WHITE, 5, 1 );
        ike1.setColourAt( Color.WHITE, 0, 2 );
        ike1.setColourAt( Color.WHITE, 2, 2 );
        ike1.setColourAt( Color.WHITE, 4, 2 );
        ike1.setColourAt( Color.WHITE, 1, 3 );
        ike1.setColourAt( Color.WHITE, 3, 3 );
        ike1.setColourAt( Color.WHITE, 5, 3 );
        ike2 = new Ikon( 2, 3, Color.RED, in( "ike2" ) );//Starts off red but should end up grey.
        ike2.fillWithImage( ike1.asImage() );
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                assert ike2.colourAt( i, j ).equals( Color.GRAY ) : ike2.colourAt( i, j );
            }
        }

        //A jpeg. For now just check that no exception is thrown.
        File waratah = new File( Ikonmaker.waratah_jpg );
        BufferedImage bim = ImageIO.read( waratah );
        int width = bim.getWidth();
        int height = bim.getHeight();
        ike2 = new Ikon( width / 5, height / 5, Color.RED, in( "sa" ) );
        ike2.fillWithImage( bim );

        return true;
    }

    public boolean toStringTest() {
        Ikon ike1 = new Ikon( 2, 3, Color.BLACK, in( "ike1" ) );
        assert ike1.toString().equals( "Ikon[ike1, 2, 3]" ) : ike1;
        return true;
    }

    public boolean asImageTest() {
        Ikon ike = new Ikon( 2, 3, Color.BLACK, in( "ike" ) );//2 wide, 3 high
        ike.setColourAt( Color.BLUE, 0, 0 );
        ike.setColourAt( Color.CYAN, 0, 1 );
        ike.setColourAt( Color.DARK_GRAY, 1, 0 );
        ike.setColourAt( Color.GRAY, 1, 1 );
        ike.setColourAt( Color.GREEN, 2, 0 );
        ike.setColourAt( Color.LIGHT_GRAY, 2, 1 );

        BufferedImage bim = ike.asImage();
        Raster bob = bim.getData();
        assert bob.getHeight() == 3;
        assert bob.getWidth() == 2;
        assert bim.getRGB( 0, 0 ) == Color.BLUE.getRGB();
        assert bim.getRGB( 1, 0 ) == Color.CYAN.getRGB();
        assert bim.getRGB( 0, 1 ) == Color.DARK_GRAY.getRGB();
        assert bim.getRGB( 1, 1 ) == Color.GRAY.getRGB();
        assert bim.getRGB( 0, 2 ) == Color.GREEN.getRGB();
        assert bim.getRGB( 1, 2 ) == Color.LIGHT_GRAY.getRGB();

        return true;
    }

    public boolean nameTest() {
        Ikon ike = new Ikon( 2, 334, Color.BLACK, in( "ike" ) );
        assert  ike.name().equals( in( "ike" ) );
        return true;
    }

    public boolean widthTest() {
        Ikon ike = new Ikon( 56, 334, Color.BLACK, in( "ike" ) );
        assert  ike.width() == 56;
        return true;
    }

    public boolean colourAtTest() {
        Ikon ike = new Ikon( 2, 3, Color.BLACK, in( "ike" ) );
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                assert ike.colourAt( i, j ).equals( Color.BLACK );
            }
        }
        return true;
    }
}
