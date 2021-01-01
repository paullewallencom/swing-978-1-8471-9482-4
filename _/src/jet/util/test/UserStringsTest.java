package jet.util.test;

import jet.util.UserStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class UserStringsTest {

    private DummyUserStrings us = DummyUserStrings.instance();

    public boolean constructorTest() {
        //Check that if the constructor is called for a sub-class
        //without an accompanying properties file, a sensible
        //exception is thrown.
        class SillyUserStrings extends UserStrings {
            public SillyUserStrings() {
            }
        }
        boolean gotError = false;
        System.err.println(">>>>> EXPECTED STACK TRACE START <<<<<");
        try {
            new SillyUserStrings();
        } catch (AssertionError ae) {
            gotError = true;
        }
        System.err.println(">>>>> EXPECTED STACK TRACE END <<<<<");
        return gotError;
    }

    public boolean labelTest() {
        assert us.label( DummyUserStrings.FULL_RESOURCE_FAMILY ).equals( "Label");
        assert us.label( DummyUserStrings.EMPTY_RESOURCE_FAMILY ).equals( "");
        return true;
    }

    public boolean toolTipTest() {
        assert us.toolTip( DummyUserStrings.FULL_RESOURCE_FAMILY ).equals( "Tool tip");
        assert us.toolTip( DummyUserStrings.NO_TOOLTIP_RESOURCE_FAMILY ).equals( "");
        return true;
    }

    public boolean mnemonicTest() {
        assert us.mnemonic( DummyUserStrings.FULL_RESOURCE_FAMILY ).equals( KeyEvent.VK_L );
        assert us.mnemonic( DummyUserStrings.NO_MNEMONIC_RESOURCE_FAMILY ) == null;
        return true;
    }

    public boolean messageTest() {
        assert us.message( DummyUserStrings.PLAIN_MESSAGE_MSG0 ).equals( "x" );
        assert us.message( DummyUserStrings.ONE_PLACE_MESSAGE_MSG1, "0" ).equals( "0 x" );
        return true;
    }

    public boolean acceleratorTest() {
        assert us.accelerator( DummyUserStrings.FULL_RESOURCE_FAMILY ).equals( KeyStroke.getKeyStroke( "ctrl L"));
        assert us.accelerator( DummyUserStrings.NO_ACCELERATOR_RESOURCE_FAMILY ) == null;
        return true;
    }

    public boolean iconTest() {
        Icon ike = us.icon( DummyUserStrings.FULL_RESOURCE_FAMILY );
        assert ike.getIconHeight() == 16;
        assert ike.getIconWidth() == 16;
        assert us.icon( DummyUserStrings.NO_ICON_RESOURCE_FAMILY ) == null;
        return true;
    }

    public boolean createJButtonTest() {
        Action a = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //Do nothing.
            }
        };
        JButton button = us.createJButton( a, DummyUserStrings.FULL_RESOURCE_FAMILY );
        assert button.getAction() == a;
        assert button.getText().equals( "Label");
        assert button.getToolTipText().equals( "Tool tip");
        assert button.getMnemonic() == (int) 'L';
        Icon ike = button.getIcon();
        assert ike.getIconHeight() == 16;
        assert ike.getIconWidth() == 16;

        button = us.createJButton( a, DummyUserStrings.EMPTY_RESOURCE_FAMILY );
        assert button.getAction() == a;
        assert button.getText().equals( "");
        assert button.getToolTipText() == null;
        assert button.getIcon() == null;
        return true;
    }

    /*
     * Checks that not all the keys are needed in a resource family.
     */
    public boolean partialFamiliesTest() {
        assert us.label( DummyUserStrings.LABEL_ONLY_RESOURCE_FAMILY ).equals( "Label");

        Icon ike = us.icon( DummyUserStrings.ICON_ONLY_RESOURCE_FAMILY );
        assert ike.getIconHeight() == 16;
        assert ike.getIconWidth() == 16;

        assert us.accelerator( DummyUserStrings.ACCELERATOR_ONLY_RESOURCE_FAMILY ).equals( KeyStroke.getKeyStroke( "ctrl A") );

        assert us.toolTip( DummyUserStrings.TOOLTIP_ONLY_RESOURCE_FAMILY ).equals( "Tool tip" );

        assert us.mnemonic( DummyUserStrings.MNEMONIC_ONLY_RESOURCE_FAMILY ).equals( KeyEvent.VK_M);
        return true;
    }

}