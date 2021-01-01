package jet.wizard;

import javax.swing.*;
import java.util.*;

/**
 * Example from Chapter 6.
 */
public class WizardV2 {
    private int numberOfSteps;
    private int currentStepNumber = 1;
    private List<String> userEnteredText = new LinkedList<String>();

    private JFrame frame = new JFrame();

    public WizardV2( int numberOfSteps ) {
        frame.setVisible( true );
        this.numberOfSteps = numberOfSteps;
    }

    public void showStep() {
        new StepV2( new StepHandler(), currentStepNumber ).show();
    }

    public List<String> userEnteredText() {
        return userEnteredText;
    }

    private class StepHandler implements StepV2.Handler {
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
    }
}
