package jet.ikonmaker.test;

import jet.ikonmaker.*;

import java.awt.*;

/**
 * @author Tim Lavers
 */
@SuppressWarnings({"ObjectEqualsNull"})
public class SinglePixelEditOperationTest extends EditOperationTest {
    private SinglePixelEditOperation eo1 = EditOperationTest.speo( 1, 2, Color.RED );

    public boolean rowTest() {
        assert eo1.row() == 1;
        return true;
    }

    public boolean columnTest() {
        assert eo1.column() == 2;
        return true;
    }

    public boolean colourTest() {
        assert eo1.colour().equals( Color.RED );
        return true;
    }

    public boolean stringSerialiseTest() {
        SinglePixelEditOperation eo = speo( 435, 56, Color.MAGENTA );
        SinglePixelEditOperation deserialised = (SinglePixelEditOperation) EditOperation.deserialise( eo.stringSerialise() );
        assert deserialised.row() == eo.row();
        assert deserialised.column() == eo.column();
        assert deserialised.colour().equals( eo.colour() );

        eo = speo( 0, 56342, Color.BLACK );
        deserialised = (SinglePixelEditOperation) SinglePixelEditOperation.deserialise( eo.stringSerialise() );
        assert deserialised.row() == eo.row();
        assert deserialised.column() == eo.column();
        assert deserialised.colour().equals( eo.colour() );

        eo = speo( 9999, 56342, new Color( 1, 2, 3 ) );
        deserialised = (SinglePixelEditOperation) EditOperation.deserialise( eo.stringSerialise() );
        assert deserialised.row() == eo.row();
        assert deserialised.column() == eo.column();
        assert deserialised.colour().equals( eo.colour() );
        return true;
    }

    public boolean constructor_int_int_Color_Test() {
        assert eo1.row() == 1;
        assert eo1.column() == 2;
        assert eo1.colour().equals( Color.RED );
        return true;
    }

    public boolean constructor_StringArray_Test() {
        class SinglePixelEditOperationExt extends SinglePixelEditOperation {
            public SinglePixelEditOperationExt( String[] serialisationComponents ) {
                super( serialisationComponents );
            }
        }
        SinglePixelEditOperationExt speo = new SinglePixelEditOperationExt( new String[]{  "S", "1", "2", "128", "64", "255", "192" } );
        assert speo.row() == 1;
        assert speo.column() == 2;
        assert speo.colour().getRed(  ) == 128;
        assert speo.colour().getGreen(  ) == 64;
        assert speo.colour().getBlue(  ) == 255;
        assert speo.colour().getAlpha(  ) == 192;
        return true;
    }

    public boolean applyTest() {
        Ikon ike = new Ikon( 2, 2, Color.BLACK, new IkonName( "ike" ) );
        speo( 0, 0, Color.RED ).apply( ike );
        assert ike.colourAt( 0, 0 ).equals( Color.RED );
        assert ike.colourAt( 0, 1 ).equals( Color.BLACK );
        assert ike.colourAt( 1, 0 ).equals( Color.BLACK );
        assert ike.colourAt( 1, 1 ).equals( Color.BLACK );

        speo( 0, 1, Color.MAGENTA ).apply( ike );
        assert ike.colourAt( 0, 0 ).equals( Color.RED );
        assert ike.colourAt( 0, 1 ).equals( Color.MAGENTA );
        assert ike.colourAt( 1, 0 ).equals( Color.BLACK );
        assert ike.colourAt( 1, 1 ).equals( Color.BLACK );

        speo( 1, 0, Color.GRAY ).apply( ike );
        assert ike.colourAt( 0, 0 ).equals( Color.RED );
        assert ike.colourAt( 0, 1 ).equals( Color.MAGENTA );
        assert ike.colourAt( 1, 0 ).equals( Color.GRAY );
        assert ike.colourAt( 1, 1 ).equals( Color.BLACK );
        return true;
    }

    public boolean deserialiseTest() {
        SinglePixelEditOperation eo = speo( 435, 56, Color.MAGENTA );
        SinglePixelEditOperation deserialised = (SinglePixelEditOperation) EditOperation.deserialise( eo.stringSerialise() );
        assert deserialised.row() == eo.row();
        assert deserialised.column() == eo.column();
        assert deserialised.colour().equals( eo.colour() );

        eo = speo( 0, 56342, Color.BLACK );
        deserialised = (SinglePixelEditOperation) EditOperation.deserialise( eo.stringSerialise() );
        assert deserialised.row() == eo.row();
        assert deserialised.column() == eo.column();
        assert deserialised.colour().equals( eo.colour() );

        eo = speo( 9999, 56342, new Color( 1, 2, 3 ) );
        deserialised = (SinglePixelEditOperation) EditOperation.deserialise( eo.stringSerialise() );
        assert deserialised.row() == eo.row();
        assert deserialised.column() == eo.column();
        assert deserialised.colour().equals( eo.colour() );

        ColourChangeEditOperation cceo = cceo( 0, 1, 2 );
        ColourChangeEditOperation deserialisedCceo = (ColourChangeEditOperation) EditOperation.deserialise( cceo.stringSerialise() );
        assert deserialisedCceo.deltaRed() == 0;
        assert deserialisedCceo.deltaGreen() == 1;
        assert deserialisedCceo.deltaBlue() == 2;

        cceo = cceo( 255, 128, 249 );
        deserialisedCceo = (ColourChangeEditOperation) EditOperation.deserialise( cceo.stringSerialise() );
        assert deserialisedCceo.deltaRed() == 255;
        assert deserialisedCceo.deltaGreen() == 128;
        assert deserialisedCceo.deltaBlue() == 249;
        return true;
    }

    public boolean equalsTest() {
        SinglePixelEditOperation eo0 = EditOperationTest.speo( 0, 0, Color.BLACK );
        SinglePixelEditOperation eo1 = EditOperationTest.speo( 1, 0, Color.BLACK );
        SinglePixelEditOperation eo2 = EditOperationTest.speo( 0, 2, Color.BLACK );
        SinglePixelEditOperation eo3 = EditOperationTest.speo( 0, 0, Color.GRAY );
        SinglePixelEditOperation eo4 = EditOperationTest.speo( 0, 0, Color.BLACK );
        assert !eo0.equals( null );
        assert !eo0.equals( "klfsf" );
        assert !eo0.equals( eo1 );
        assert !eo0.equals( eo2 );
        assert !eo0.equals( eo3 );
        assert eo0.equals( eo4 );
        return true;
    }

    public boolean hashCodeTest() {
        for (int row = 0; row < 100; row++) {
            for (int column = 0; column < 50; column++) {
                SinglePixelEditOperation eo0 = EditOperationTest.speo( row, column, new Color( row, column, row + column ) );
                SinglePixelEditOperation eo1 = EditOperationTest.speo( row, column, new Color( row, column, row + column ) );
                assert eo0.hashCode() == eo1.hashCode();
            }
        }
        return true;
    }
}
