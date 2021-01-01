package jet.ikonmaker.test;

import jet.ikonmaker.*;

import java.awt.*;

/**
 * @author Tim Lavers
 */
public class ColourChangeEditOperationTest extends EditOperationTest {

    public boolean constructor_int_int_int_Test() {
        ColourChangeEditOperation cceo = new ColourChangeEditOperation( 1, 2, 3 );
        assert cceo.deltaRed() == 1;
        assert cceo.deltaGreen() == 2;
        assert cceo.deltaBlue() == 3;
        return true;
    }

    public boolean constructor_StringArray_Test() {
        class ColourChangeEditOperationExt extends ColourChangeEditOperation {
            protected ColourChangeEditOperationExt( String[] serialisationComponents ) {
                super( serialisationComponents );
            }
        }

        ColourChangeEditOperationExt cceo = new ColourChangeEditOperationExt( new String[]{"C", "1", "2", "3"} );
        assert cceo.deltaRed() == 1;
        assert cceo.deltaGreen() == 2;
        assert cceo.deltaBlue() == 3;
        return true;
    }

    public boolean stringSerialiseTest() {
        for (int r = 0; r < 255; r += 5) {
            for (int g = 0; g < 255; g += 5) {
                for (int b = 0; b < 255; b += 5) {
                    ColourChangeEditOperation cceo = cceo( r, g, b );
                    ColourChangeEditOperation deserialisedCceo = (ColourChangeEditOperation) EditOperation.deserialise( cceo.stringSerialise() );
                    assert deserialisedCceo.deltaRed() == r;
                    assert deserialisedCceo.deltaGreen() == g;
                    assert deserialisedCceo.deltaBlue() == b;
                }
            }
        }
        return true;
    }

    public boolean deltaRedTest() {
        for (int r = 0; r < 255; r++) {
            ColourChangeEditOperation cceo = cceo( r, 1, 1 );
            assert cceo.deltaRed() == r;
        }
        return true;

    }

    public boolean deltaGreenTest() {
        for (int g = 0; g < 255; g++) {
            ColourChangeEditOperation cceo = cceo( 1, g, 1 );
            assert cceo.deltaGreen() == g;
        }
        return true;

    }

    public boolean deltaBlueTest() {
        for (int b = 0; b < 255; b++) {
            ColourChangeEditOperation cceo = cceo( 1, 1, b );
            assert cceo.deltaBlue() == b;
        }
        return true;
    }

    public boolean transformTest() {
        ColourChangeEditOperation cceo = cceo( 0, 0, 0 );
        assert cceo.transform( Color.BLACK.getRGB() ) == Color.BLACK.getRGB();
        assert cceo.transform( Color.BLUE.getRGB() ) == Color.BLUE.getRGB();
        assert cceo.transform( Color.CYAN.getRGB() ) == Color.CYAN.getRGB();
        assert cceo.transform( Color.DARK_GRAY.getRGB() ) == Color.DARK_GRAY.getRGB();
        assert cceo.transform( Color.GRAY.getRGB() ) == Color.GRAY.getRGB();
        assert cceo.transform( Color.GREEN.getRGB() ) == Color.GREEN.getRGB();
        assert cceo.transform( Color.LIGHT_GRAY.getRGB() ) == Color.LIGHT_GRAY.getRGB();
        assert cceo.transform( Color.MAGENTA.getRGB() ) == Color.MAGENTA.getRGB();
        assert cceo.transform( Color.ORANGE.getRGB() ) == Color.ORANGE.getRGB();
        assert cceo.transform( Color.PINK.getRGB() ) == Color.PINK.getRGB();
        assert cceo.transform( Color.RED.getRGB() ) == Color.RED.getRGB();
        assert cceo.transform( Color.WHITE.getRGB() ) == Color.WHITE.getRGB();
        assert cceo.transform( Color.YELLOW.getRGB() ) == Color.YELLOW.getRGB();

        cceo = cceo( 255, 0, 0 );
        assert cceo.transform( Color.BLACK.getRGB() ) == Color.RED.getRGB();
        assert cceo.transform( Color.BLUE.getRGB() ) == new Color( 255, 0, 255 ).getRGB();
        assert cceo.transform( Color.CYAN.getRGB() ) == Color.WHITE.getRGB();//cyan is 255 of green and blue.
        assert cceo.transform( Color.DARK_GRAY.getRGB() ) == new Color( 255, 64, 64 ).getRGB();
        assert cceo.transform( Color.GRAY.getRGB() ) == new Color( 255, 128, 128 ).getRGB();
        assert cceo.transform( Color.GREEN.getRGB() ) == new Color( 255, 255, 0 ).getRGB();
        assert cceo.transform( Color.LIGHT_GRAY.getRGB() ) == new Color( 255, 192, 192 ).getRGB();
        assert cceo.transform( Color.MAGENTA.getRGB() ) == Color.MAGENTA.getRGB();//Already has red = 255.
        assert cceo.transform( Color.ORANGE.getRGB() ) == Color.ORANGE.getRGB();//Already has red = 255.
        assert cceo.transform( Color.PINK.getRGB() ) == Color.PINK.getRGB();//Already has red = 255.
        assert cceo.transform( Color.RED.getRGB() ) == Color.RED.getRGB();//Already has red = 255.
        assert cceo.transform( Color.WHITE.getRGB() ) == Color.WHITE.getRGB();//Already has red = 255.
        assert cceo.transform( Color.YELLOW.getRGB() ) == Color.YELLOW.getRGB();//Already has red = 255.

        cceo = cceo( 0, 255, 0 );
        assert cceo.transform( Color.BLACK.getRGB() ) == Color.GREEN.getRGB();
        assert cceo.transform( Color.BLUE.getRGB() ) == Color.CYAN.getRGB();
        assert cceo.transform( Color.CYAN.getRGB() ) == Color.CYAN.getRGB();
        assert cceo.transform( Color.DARK_GRAY.getRGB() ) == new Color( 64, 255, 64 ).getRGB();
        assert cceo.transform( Color.GRAY.getRGB() ) == new Color( 128, 255, 128 ).getRGB();
        assert cceo.transform( Color.GREEN.getRGB() ) == Color.GREEN.getRGB();
        assert cceo.transform( Color.LIGHT_GRAY.getRGB() ) == new Color( 192, 255, 192 ).getRGB();
        assert cceo.transform( Color.MAGENTA.getRGB() ) == Color.white.getRGB();
        assert cceo.transform( Color.ORANGE.getRGB() ) == Color.YELLOW.getRGB();
        assert cceo.transform( Color.PINK.getRGB() ) == new Color( 255, 255, 175 ).getRGB();
        assert cceo.transform( Color.RED.getRGB() ) == Color.YELLOW.getRGB();
        assert cceo.transform( Color.WHITE.getRGB() ) == Color.WHITE.getRGB();
        assert cceo.transform( Color.YELLOW.getRGB() ) == Color.YELLOW.getRGB();

        cceo = cceo( 0, 0, 255 );
        assert cceo.transform( Color.BLACK.getRGB() ) == Color.BLUE.getRGB();
        assert cceo.transform( Color.BLUE.getRGB() ) == Color.BLUE.getRGB();
        assert cceo.transform( Color.CYAN.getRGB() ) == Color.CYAN.getRGB();
        assert cceo.transform( Color.DARK_GRAY.getRGB() ) == new Color( 64, 64, 255 ).getRGB();
        assert cceo.transform( Color.GRAY.getRGB() ) == new Color( 128, 128, 255 ).getRGB();
        assert cceo.transform( Color.GREEN.getRGB() ) == Color.CYAN.getRGB();
        assert cceo.transform( Color.LIGHT_GRAY.getRGB() ) == new Color( 192, 192, 255 ).getRGB();
        assert cceo.transform( Color.MAGENTA.getRGB() ) == Color.MAGENTA.getRGB();
        assert cceo.transform( Color.ORANGE.getRGB() ) == new Color( 255, 200, 255 ).getRGB();
        assert cceo.transform( Color.PINK.getRGB() ) == new Color( 255, 175, 255 ).getRGB();
        assert cceo.transform( Color.RED.getRGB() ) == Color.MAGENTA.getRGB();
        assert cceo.transform( Color.WHITE.getRGB() ) == Color.WHITE.getRGB();
        assert cceo.transform( Color.YELLOW.getRGB() ) == Color.WHITE.getRGB();

        assert cceo( 50, 0, 0 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 150, 100, 100 ).getRGB();
        assert cceo( 50, 50, 0 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 150, 150, 100 ).getRGB();
        assert cceo( 50, 50, 50 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 150, 150, 150 ).getRGB();

        assert cceo( 50, 0, 0 ).transform( new Color( 150, 150, 150 ).getRGB() ) == new Color( 200, 150, 150 ).getRGB();
        assert cceo( 50, 50, 0 ).transform( new Color( 150, 150, 150 ).getRGB() ) == new Color( 200, 200, 150 ).getRGB();
        assert cceo( 50, 50, 50 ).transform( new Color( 150, 150, 150 ).getRGB() ) == new Color( 200, 200, 200 ).getRGB();

        assert cceo( 50, 0, 0 ).transform( new Color( 200, 200, 200 ).getRGB() ) == new Color( 250, 200, 200 ).getRGB();
        assert cceo( 50, 50, 0 ).transform( new Color( 200, 200, 200 ).getRGB() ) == new Color( 250, 250, 200 ).getRGB();
        assert cceo( 50, 50, 50 ).transform( new Color( 200, 200, 200 ).getRGB() ) == new Color( 250, 250, 250 ).getRGB();

        assert cceo( 50, 0, 0 ).transform( new Color( 250, 250, 250 ).getRGB() ) == new Color( 255, 250, 250 ).getRGB();
        assert cceo( 50, 50, 0 ).transform( new Color( 250, 250, 250 ).getRGB() ) == new Color( 255, 255, 250 ).getRGB();
        assert cceo( 50, 50, 50 ).transform( new Color( 250, 250, 250 ).getRGB() ) == new Color( 255, 255, 255 ).getRGB();

        assert cceo( -50, 0, 0 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 50, 100, 100 ).getRGB();
        assert cceo( -50, -50, 0 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 50, 50, 100 ).getRGB();
        assert cceo( -50, -50, -50 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 50, 50, 50 ).getRGB();

        assert cceo( -50, 0, 0 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 50, 100, 100 ).getRGB();
        assert cceo( -50, -50, 0 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 50, 50, 100 ).getRGB();
        assert cceo( -50, -50, -50 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 50, 50, 50 ).getRGB();

        assert cceo( -50, 0, 0 ).transform( new Color( 50, 50, 100 ).getRGB() ) == new Color( 0, 50, 100 ).getRGB();
        assert cceo( -50, -50, 0 ).transform( new Color( 50, 50, 100 ).getRGB() ) == new Color( 0, 0, 100 ).getRGB();
        assert cceo( -50, -50, -50 ).transform( new Color( 100, 100, 100 ).getRGB() ) == new Color( 50, 50, 50 ).getRGB();

        assert cceo( -350, -350, -350 ).transform( new Color( 0, 0, 0 ).getRGB() ) == new Color( 0, 0, 0 ).getRGB();
        return true;
    }

    public boolean applyTest() {
        Ikon ike = new Ikon( 2, 2, Color.BLACK, new IkonName( "ike" ) );
        cceo( 255, 0, 0 ).apply( ike );//This should make it red.
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                assert ike.colourAt( i, j ).equals( Color.RED );
            }
        }
        return true;
    }
}