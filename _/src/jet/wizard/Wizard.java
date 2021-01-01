package jet.wizard;

import javax.swing.*;
import java.util.*;

/**
 * Example from Chapter 6.
 */
public class Wizard {
    private int numberOfSteps;
    private JFrame frame = new JFrame();
    private int currentStepNumber = 1;
    private List<String> userEnteredText = new LinkedList<String>();

    public Wizard( int numberOfSteps ) {
        this.numberOfSteps = numberOfSteps;
        frame.setVisible( true );
    }

    public void showStep() {
        new Step( this, currentStepNumber ).show();
    }

    public void nextPressed( String userInputForStep ) {
        userEnteredText.add( userInputForStep );
        if (++currentStepNumber <= numberOfSteps) {
            showStep();
        } else {
            frame.dispose();
        }
    }

    public JFrame frame() {
        return frame;
    }

    public List<String> userEnteredText() {
        return userEnteredText;
    }
}
