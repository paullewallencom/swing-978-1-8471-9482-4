package jet.wizard.test;

import jet.testtools.*;
import jet.wizard.*;

import javax.swing.*;
import java.awt.*;

public class StepV2Test {
    private JFrame frame;
    private String userInput;
    private HandlerForTest handler;

    private class HandlerForTest implements StepV2.Handler {
        public void nextPressed( String userInputForStep ) {
            userInput = userInputForStep;
        }

        public JFrame frame() {
            return frame;
        }
    }

    public boolean showTest() {
        //Create the frame and the handler.
        //Show the step.
        frame = UI.createAndShowFrame( "Test" );
        handler = new HandlerForTest();
        final StepV2 step = new StepV2( handler, 1 );
        UI.runInEventThread( new Runnable() {
            public void run() {
                step.show();
            }
        } );

        //Check the title and label of this step.
        final Dialog dialog = UI.findNamedDialog( "Step 1" );
        Assert.equal( UI.getTitle( dialog ), "Step 1" );
        assert UI.findLabelShowingText( "Enter info for Step 1:" ) != null;

        //Enter some text and press the Next button.
        String str = "Information for step 1";
        Cyborg cyborg = new Cyborg();
        cyborg.type( str );
        cyborg.altChar( 'N' );

        //Check that the entered text was received by the wizard.
        final String[] result = new String[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                result[0] = userInput;
            }
        } );
        Assert.equal( str, result[0] );

        //Cleanup.
        UI.disposeOfAllFrames();
        return true;
    }

    public boolean constructorTest() {
        //Just return true as this is only an example class.
        return true;
    }
}
