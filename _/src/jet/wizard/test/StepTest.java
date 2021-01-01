package jet.wizard.test;

import jet.testtools.*;
import jet.wizard.*;

import java.awt.*;
import java.util.*;
import java.util.List;


public class StepTest {
    private Wizard wizard;
    private Step step;

    public boolean showTest() {
        //Create a wizard and from it a step.
        //Show the step.
        UI.runInEventThread( new Runnable() {
            public void run() {
                wizard = new Wizard( 5 );
                step = new Step( wizard, 1 );
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
        final List<List<String>> result = new LinkedList<List<String>>();
        UI.runInEventThread( new Runnable() {
            public void run() {
                result.add( wizard.userEnteredText() );
            }
        } );
        String receivedByWizard = result.get( 0 ).get( 0 );
        Assert.equal( receivedByWizard, str );
        UI.disposeOfAllFrames();
        return true;
    }

    public boolean constructorTest() {
        //Just return true as this is only an example class.
        return true;
    }
}
