package jet.util;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.awt.event.KeyEvent;

/**
 * Base class for particular classes that provide support for use of
 * a ResourceBundle.
 * This class is discussed in Chapter 5.
 *
 * @author Tim Lavers
 */
public class UserStrings {

    public static final String LABEL_EXT = "_LA";
    public static final String MNEMONIC_EXT = "_MN";
    public static final String ACCELERATOR_EXT = "_AC";
    public static final String TOOLTIP_EXT = "_TT";
    public static final String ICON_EXT = "_IC";
    private ResourceBundle rb;

    protected UserStrings() {
        try {
            rb = ResourceBundle.getBundle( getClass().getName() );
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Could not load resource bundle.";
        }
    }

    /**
     * Formats the message for the given key with the specific
     * details provided.
     */
    public String message( String key, Object ... details ) {
        String value = rb.getString( key );
        assert key.endsWith( "" + details.length ) :
                "Mismatch between placeholders and variables. " +
                "Expected " + details.length +
                        " places. Value is: " + value;
        return MessageFormat.format( value, details );
    }

    /**
     * The mnemonic for the given key.
     */
    public Integer mnemonic( final String key ) {
        String message = rb.getString( key + "_MN" ).toUpperCase();
        if (message.length() == 0) return null;
        String fieldName = "VK_" + message.charAt( 0 );
        try {
            return KeyEvent.class.getField(
                    fieldName ).getInt( null );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * The tooltip associated with the given key.
     */
    public String toolTip( String key ) {
        String fullKey = key + TOOLTIP_EXT;
        if (!rb.containsKey( fullKey )) return null;
        return rb.getString( fullKey );
    }

    /**
     * The text for a button, label or menu, for the given key.
     */
    public String label( String key ) {
        return rb.getString( key + LABEL_EXT );
    }

    /**
     * The accelerator for the given key.
     */
    public KeyStroke accelerator( String key ) {
        String fullKey = key + ACCELERATOR_EXT;
        if (!rb.containsKey( fullKey )) return null;
        String message =  rb.getString( fullKey );
        return KeyStroke.getKeyStroke( message );
    }

    /**
     * The icon for the given key, or null.
     */
    public Icon icon( String key ) {
        Icon icon = null;
        String fullKey = key + ICON_EXT;
        if (!rb.containsKey( fullKey )) return null;
        String iconName = rb.getString( fullKey );
        if (iconName.length() > 0) {
            String packagePrefix = getClass().getPackage().getName();
            packagePrefix = packagePrefix.replace( '.', '/');
            iconName = packagePrefix + "/" + iconName;
            icon = new ImageIcon(
                    ClassLoader.getSystemResource( iconName ) );
        }
        return icon;
    }

    /**
     * Create a button with the given action, with label, mnemonic,
     * tooltip and so on from the resources for the given key.
     */
    public JButton createJButton( Action a, String key ) {
        JButton result = new JButton(a);
        result.setText( label( key ) );
        Integer mnemonic = mnemonic(key);
        if (mnemonic != null) {
            result.setMnemonic( mnemonic );
        }
        String toolTip = toolTip( key );
        if (toolTip != null && toolTip.length() > 0) {
            result.setToolTipText( toolTip );
        }
        Icon icon = icon( key );
        if (icon != null) {
            result.setIcon( icon );
        }
        KeyStroke accelerator = accelerator( key );
        if (accelerator != null) {
            a.putValue( Action.ACCELERATOR_KEY, accelerator );
        }
        result.setName( key );
        return result;
    }
}