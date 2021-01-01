package jet.util;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

/**
 * For testing a <code>UserStrings</code> sub-class and its associated
 * properties file. The constants in the class are either keys in the
 * properties file for a single message (call these 'message constants),
 * or bases for families of related resources (label, mnemonic, tooltip, icon,
 * accelerator, call these 'resource family constants').
 * <p/>
 * There is a naming pattern for the constants: message constants have
 * names ending "_MSGn" where 'n' the number of placeholders for
 * building formatted messages.
 * <p/>
 * The constants are all to have the same value as their name.
 * <p/>
 * This class will be used to check:
 * <ul>
 * <li> all constants correspond to resources
 * <li> all resources are represented by a constant
 * <li> values for message constants actually have the number of
 * placeholders implied by their name
 * <li> the resource family for a constant (label, mnemonic, etc) is
 * consistent in that: the mnemonic for a label is a character in the
 * label, the accelerator (if not blank) really is a key stroke, and the
 * icon location (again, if not blank) does actually point to an image.
 * </ul>
 * This class is discussed in Chapter 5.
 *
 * @author Tim Lavers
 */
public class ResourcesTester {

    //The resource bundle for the UserStrings subclass.
    private ResourceBundle rb;

    //Instance of the class under test.
    private UserStrings us;

    //Map of public static final strings in the class under test
    //to their values. We need to look at field names and values,
    //this map makes it easy to do so.
    private Map<Field, String> constants
            = new HashMap<Field, String>();

    //Recognises constants that are for messages, and extracts
    //from the constants the implied number of placeholders.
    private Pattern messagesPattern =
            Pattern.compile( ".*_MSG([0-9]*)" );

    public static void testAll( UserStrings us ) {
        ResourcesTester tester = new ResourcesTester( us );
        tester.checkClassConstants();
        tester.checkResourceKeys();
    }

    private ResourcesTester( UserStrings us ) {
        this.us = us;
        rb = ResourceBundle.getBundle( us.getClass().getName() );

        //Load the key constants.
        Field[] fields = us.getClass().getDeclaredFields();
        for (Field field : fields) {
            int m = field.getModifiers();
            if (Modifier.isPublic( m ) && Modifier.isStatic( m ) &&
                    Modifier.isFinal( m )) {
                try {
                    constants.put(
                            field, (String) field.get( null ) );
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    assert false : "Could not get field value.";
                }
            }
        }
    }

    /**
     * Check that each key in the resource bundle is represented by a
     * message constant or resource family constant in the interface.
     */
private void checkResourceKeys() {
    for (String keyStr : rb.keySet()) {
        Matcher matcher = messagesPattern.matcher( keyStr );
        if (matcher.matches()) {
            //It should be a constant name.
            assert constants.values().contains( keyStr )
                    : "Not a constant: " + keyStr;
        } else {
            //It should be a member of the resource family for
            //for a constant.
            boolean endingOk = (
                    keyStr.endsWith( UserStrings.ACCELERATOR_EXT ) ||
                    keyStr.endsWith( UserStrings.ICON_EXT ) ||
                    keyStr.endsWith( UserStrings.LABEL_EXT ) ||
                    keyStr.endsWith( UserStrings.MNEMONIC_EXT ) ||
                    keyStr.endsWith( UserStrings.TOOLTIP_EXT ));
            assert endingOk : "Unknown resource type: " + keyStr;

            //The base of the key should be a constant.
            //All _EXT constants have length 3.
            int limit = keyStr.length() - 3;
            String keyBase = keyStr.substring( 0, limit );
            assert constants.values().contains( keyBase ) :
                    "No constant for: '" + keyBase + "'";
        }
    }
}

    /**
     * Check that each constant is either a message constant or a
     * resource family constant, then check that the values in the
     * resource bundle are valid.
     */
    private void checkClassConstants() {
        for (Field constant : constants.keySet()) {
            String value = constants.get( constant );
            Matcher matcher = messagesPattern.matcher( value );
            if (matcher.matches()) {
                checkMessageConstant( matcher, constant );
            } else {
                checkResourceFamilyConstant( constant );
            }
        }
    }

    private void checkResourceFamilyConstant( Field constant ) {
        String value = constants.get( constant );

        //If the accelerator string is defined, then it must
        //represent a valid accelerator.
        try {
            String ac = rb.getString(
                    value + UserStrings.ACCELERATOR_EXT );
            if (ac != null && !ac.equals( "" )) {
                assert us.accelerator( value ) != null :
                        ac + " is not a KeyStroke";
            }
        } catch (MissingResourceException e) {
            //The accelerator is not defined.
        }

        //Check that if the mnemonic and label exist,
        //then the mnemonic is a character in the label.
        try {
            String mn = rb.getString( value + UserStrings.MNEMONIC_EXT );
            String label = us.label( value );
            if (mn != null && mn.length() > 0 && label != null) {
                if (label.toLowerCase().indexOf( mn.toLowerCase() ) < 0) {
                    String errorMessage = "Mn not in label. Key: '" +
                            "', label text: '" + label + "', mnemonic: '"
                            + mn + "'";
                    assert false : errorMessage;
                }
            }
        } catch (MissingResourceException e) {
            //Label or mnemonic not defined, so nothing to check.
        }

        //Check that if an icon is defined, it actually represents
        //an image resource.
        try {
            String iconVal = rb.getString( value + UserStrings.ICON_EXT );
            if (iconVal != null && iconVal.length() > 0) {
                assert us.icon( value ) != null : "No icon: " + iconVal;
            }
        } catch (MissingResourceException e) {
            //Icon not defined, so nothing to check.
        }
    }

    private void checkMessageConstant( Matcher m, Field constant ) {
        //First check that the name is in fact the value.
        String constantValue = constants.get( constant );
        String fieldName = constant.getName();
        assert constantValue.equals( fieldName ) :
                "The field " + constant + "did not have " +
                        "its name the same as its value.";
        String numberAtEnd = m.group( 1 );
        int numSlots = Integer.parseInt( numberAtEnd );
        String value = rb.getString( constantValue );
        if (numSlots == 0) {
            //Check that there are in fact no placeholders.
            assert!value.contains( "ZZZZ" );//Sanity check
            String formatted = MessageFormat.format( value, "ZZZZ" );
            //If ZZZZ is in the formatted string, there was one.
            assert!formatted.contains( "ZZZZ" ) :
                    "Should be no placeholders for: " + constant;
        } else {
            //Check that it has sufficient parameter slots.
            //Do this by formatting it with some strange values.
            Object[] params = new String[numSlots];
            for (int i = 0; i < params.length; i++) {
                params[i] = "ZZZZ" + i + "YYYY";
            }
            String formatted = MessageFormat.format( value, params );
            //All of these strange values should
            //appear in the formatted string.
            for (Object param : params) {
                assert formatted.contains( param.toString() ) :
                        "Missing parameter slot for: " + constant;
            }
            //There should be no placeholders left over. Format the
            //formatted string, with a new strange value, to check this.
            formatted = MessageFormat.format( formatted, "WWWWQQQQ" );
            assert!formatted.contains( "WWWWQQQQ" ) :
                    "Extra parameter slot for: " + constant;
        }
    }
}