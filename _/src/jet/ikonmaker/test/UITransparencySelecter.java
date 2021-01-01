package jet.ikonmaker.test;

import jet.ikonmaker.*;
import jet.testtools.*;

import javax.swing.*;

/**
 * @author Tim Lavers
 */
public class UITransparencySelecter {

    Cyborg robot = new Cyborg();
    private IkonMakerUserStrings us =
            IkonMakerUserStrings.instance();

    public void setValue( int value ) {
        robot.altChar( us.mnemonic(
                IkonMakerUserStrings.TRANSPARENCY ) );
        robot.selectAllText();
        robot.type( "" + value );
        robot.enter();
    }

    /**
     * Checks that the spinner and slider show
     * the same value and returns it as an int.
     */
    public int value() {
        int spinnerValue = Integer.parseInt(
                UI.getSpinnerValue( spinner() ).toString());
        int sliderValue = UI.getSliderValue( slider() );
        assert sliderValue == spinnerValue;
        return spinnerValue;
    }

    public JSlider slider() {
        return (JSlider) UI.findNamedComponent(
                TransparencySelecter.SLIDER_NAME );
    }

    public JSpinner spinner() {
        return (JSpinner) UI.findNamedComponent(
                TransparencySelecter.SPINNER_NAME );
    }
}