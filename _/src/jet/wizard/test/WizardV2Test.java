package jet.wizard.test;

import jet.testtools.*;
import jet.wizard.*;

import java.util.*;

public class WizardV2Test {
    private WizardV2 wizard;

    public boolean showStepTest() {
        final int numberOfSteps = 5;
        UI.runInEventThread( new Runnable() {
            public void run() {
                wizard = new WizardV2( numberOfSteps );
                wizard.showStep();
            }
        } );
        List<String> expected = new LinkedList<String>();
        for (int i = 1; i <= numberOfSteps; i++) {
            String str = "Information for step " + i;
            expected.add( str );
            Cyborg cyborg = new Cyborg();
            cyborg.type( str );
            cyborg.altChar( 'N' );
        }
        final List<String>[] result = new List[1];
        UI.runInEventThread( new Runnable() {
            public void run() {
                result[0] = wizard.userEnteredText();
            }
        } );
        Assert.equal( result[0], expected );
        return true;
    }

    public boolean constructorTest() {
        //Just return true as this is only an example class.
        return true;
    }

    public boolean userEnteredTextTest() {
        //...
        return true;
    }
}
