package jet.wizard.test;

import jet.testtools.*;
import jet.wizard.*;

import java.util.*;

public class WizardTest {
    private Wizard wizard;

    public boolean showStepTest() {
        //Create the wizard in a thread-safe manner,
        //and show the first step.
        final int numberOfSteps = 5;
        UI.runInEventThread( new Runnable() {
            public void run() {
                wizard = new Wizard( numberOfSteps );
                wizard.showStep();
            }
        } );

        //Type in some information at each step,
        //record it for later comparison,
        //and go to the next step.
        Cyborg cyborg = new Cyborg();
        List<String> expected = new LinkedList<String>();
        for (int i = 1; i <= numberOfSteps; i++) {
            String str = "Information for step " + i;
            expected.add( str );
            cyborg.type( str );
            cyborg.altChar( 'N' );
        }

        //Retrieve the entered text from the wizard
        //in a thread-safe manner.
        final List<List<String>> result = new LinkedList<List<String>>();
        UI.runInEventThread( new Runnable() {
            public void run() {
                result.add( wizard.userEnteredText() );
            }
        } );
        //Compare the retrieved text with that expected
        Assert.equal( result.get(0), expected );
        return true;
    }

    public boolean constructorTest() {
        //Just return true as this is only an example class.
        return true;
    }

    public boolean frameTest() {
        //...
        return true;
    }

    public boolean nextPressedTest() {
        //...
        return true;
    }

    public boolean userEnteredTextTest() {
        //Just return true as this is only an example class.
        return true;
    }
}
