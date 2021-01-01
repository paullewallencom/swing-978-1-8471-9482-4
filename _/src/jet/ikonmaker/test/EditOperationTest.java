package jet.ikonmaker.test;

import jet.ikonmaker.*;

import java.awt.*;

/**
 * @author Tim Lavers
 */
public class EditOperationTest {
    static SinglePixelEditOperation speo( int row, int column, Color colour ) {
        return new SinglePixelEditOperation( row, column, colour );
    }

    static ColourChangeEditOperation cceo( int deltaRed, int deltaGreen, int deltablue ) {
        return new ColourChangeEditOperation( deltaRed, deltaGreen, deltablue );
    }

    public boolean deserialiseTest() {
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

}
