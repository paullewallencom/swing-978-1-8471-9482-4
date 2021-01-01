package jet.ikonmaker;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;

/**
 * A paired slider and spinner for selecting transparency values between 0 and 255.
 * The value can be set from either component, also it can be set externally.
 *
 * @author Tim Lavers
 */
public class TransparencySelecter {

    public static final String SLIDER_NAME = "TransparencySelecter.Slider";
    public static final String SPINNER_NAME = "TransparencySelecter.Spinner";
    private JComponent panel;
    private JSlider slider;
    private SpinnerNumberModel spinnerModel;
    private IkonMakerUserStrings us = IkonMakerUserStrings.instance();

    public TransparencySelecter() {
        slider = new JSlider( JSlider.VERTICAL, 0, 255, 255 );
        final Dictionary<Integer, JLabel> d = new Hashtable<Integer, JLabel>();
        d.put( new Integer( 255 ), new JLabel( us.message( IkonMakerUserStrings.OPAQUE_MSG0 ) ) );
        d.put( new Integer( 0 ), new JLabel( us.message( IkonMakerUserStrings.TRANSPARENT_MSG0 ) ) );
        slider.setLabelTable( d );
        slider.setName( SLIDER_NAME );
        slider.setPaintTicks( true );
        slider.setPaintTrack( true );
        slider.setPaintLabels( true );
        slider.setMajorTickSpacing( 64 );
        slider.setMinorTickSpacing( 16 );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( final ChangeEvent e ) {
                setValue( slider.getValue() );
            }
        } );

        //The spinner and its label, in a box.
        spinnerModel = new SpinnerNumberModel( 255, 0, 255, 1 );
        final JSpinner field = new JSpinner( spinnerModel );
        field.addChangeListener( new ChangeListener() {
            public void stateChanged( final ChangeEvent e ) {
                setValue( spinnerModel.getNumber().intValue() );
            }
        } );
        field.setName( SPINNER_NAME );

        panel = new JPanel( new GridBagLayout() );
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets( 5, 0, 0, 0 );
        JLabel transparencyLabel = new JLabel( us.label( IkonMakerUserStrings.TRANSPARENCY ) );
        transparencyLabel.setDisplayedMnemonic( us.mnemonic( IkonMakerUserStrings.TRANSPARENCY ) );
        transparencyLabel.setLabelFor( field );
        panel.add( transparencyLabel, gbc );
        panel.add( field, gbc );
        panel.add( slider, gbc );
        panel.add( Box.createVerticalStrut( 60 ), gbc );
    }

    public JComponent ui() {
        return panel;
    }

    public int value() {
        return slider.getValue();
    }

    public void setValue( final int transparency ) {
        if (slider.getValue() != transparency) {
            slider.setValue( transparency );
        }
        if (spinnerModel.getNumber().intValue() != transparency) {
            spinnerModel.setValue( new Integer( transparency ) );
        }
    }
}