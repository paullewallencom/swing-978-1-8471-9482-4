package org.grandtestauto.test;

import java.lang.reflect.*;
import java.util.*;

import org.grandtestauto.*;

/**
 * Unit test for <code>Messages</code> and its associated properties file.
 * Will test that every constant in Messages matches a key in the properties
 * file and that every key in the properties file is accounted for.
 *
 * @author Tim Lavers
 */
public class MessagesTest {
    
    private static final String UNLIKELY_STRING = "xxx_unlikely_string_xxx";
    private static final String OTHER_UNLIKELY_STRING = "xxx_another_unlikely_string_xxx";
    private ResourceBundle bundle;
    private Set<Field> keyConsts;
    
    public MessagesTest() throws Exception {
        Class messagesClass = Class.forName( "org.grandtestauto.Messages" );
        bundle = ResourceBundle.getBundle( messagesClass.getName() );
        //Create and load the key constants set.
        keyConsts = new HashSet<Field>();
        Field[] fields = messagesClass.getDeclaredFields();
        for (int i=0; i<fields.length; i++) {
            Field field = fields[i];
            int m = field.getModifiers();
            if (Modifier.isPublic( m ) && Modifier.isStatic( m ) && Modifier.isFinal( m )) {
                keyConsts.add( field );
            }
        }
    }
    
    public boolean message_String_Test() throws Exception {
        boolean result = true;
        //Check that for each simple messagekey in the Messages class,
        //there is a constant in the properties file.
        for (Iterator itor = keyConsts.iterator(); itor.hasNext(); ) {
            Field f = (Field) itor.next();
            if (((String) f.get( null )).startsWith( "SK_" )) {
                //This method call will fail if the key is not in the props file.
                String fromBundle = bundle.getString( (String) f.get( null ) );
                String fromMessages = Messages.message( (String) f.get( null ) );
                result &= fromBundle.equals( fromMessages );
                assert result : "Failed for key constant " + f;
            }
        }
        
        //Now check that for each key in the bundle there is a constant in Messages.
        for (Enumeration e = bundle.getKeys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            if (key.startsWith( "SK_" )) {
                result &= checkContainsFieldFor( key );
                assert result : "No key constant for: " + key;
            }
        }
        return result;
    }
    
    public boolean message_String_String_Test() throws Exception {
        boolean result = true;
        //Check that for each One Place Key in the Messages class,
        //there is a constant in the properties file.
        for (Iterator itor = keyConsts.iterator(); itor.hasNext(); ) {
            Field f = (Field) itor.next();
            if (((String) f.get( null )).startsWith( "TPK_" )) {
                //This method call will fail if the key is not in the props file.
                bundle.getString( (String) f.get( null ) );
                //Now check that it is properly formatted.
                String fromMessages = Messages.message( (String) f.get( null ), UNLIKELY_STRING, OTHER_UNLIKELY_STRING );
                result &= fromMessages.indexOf( UNLIKELY_STRING ) >= 0;
                result &= fromMessages.indexOf( OTHER_UNLIKELY_STRING ) >= 0;
                assert result : "Failed for key constant " + f;
            }
        }
        
        //Now check that for each key in the bundle there is a constant in Messages.
        for (Enumeration e = bundle.getKeys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            if (key.contains( "_CHOICE_")) continue;

            if (key.startsWith( "TPK_" )) {
                result &= checkContainsFieldFor( key );
                assert result : "No key constant for: " + key;
            }
        }
        return result;
    }
    
    public boolean message_String_String_String_Test() throws Exception {
        boolean result = true;
        //Check that for each Two Place Key in the Messages class,
        //there is a constant in the properties file.
        for (Iterator itor = keyConsts.iterator(); itor.hasNext(); ) {
            Field f = (Field) itor.next();
            if (((String) f.get( null )).startsWith( "OPK_" ) && !((String) f.get( null )).startsWith( "OPK_CK")) {
                //This method call will fail if the key is not in the props file.
                bundle.getString( (String) f.get( null ) );
                //Now check that it is properly formatted.
                String fromMessages = Messages.message( (String) f.get( null ), UNLIKELY_STRING );
                result &= fromMessages.indexOf( UNLIKELY_STRING ) >= 0;
                assert result : "Failed for key constant " + f;
            }
        }
        
        //Now check that for each key in the bundle there is a constant in Messages.
        for (Enumeration e = bundle.getKeys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            if (key.startsWith( "OPK_" ) && !key.startsWith( "OPK_CK")) {
                result &= checkContainsFieldFor( key );
                assert result : "No key constant for: " + key;
            }
        }
        return result;
    }
    
    public boolean message_String_int_Test() throws Exception {
        boolean result = true;
        //Check that for each Choice Key in the Messages class,
        //there is a constant in the properties file.
        for (Iterator itor = keyConsts.iterator(); itor.hasNext(); ) {
            Field f = (Field) itor.next();
            if (((String) f.get( null )).startsWith( "CK_" )) {
                //This method call will fail if the key is not in the props file.
                bundle.getString( (String) f.get( null ) );
                //Now check that it is properly formatted.
                String fromMessages1 = Messages.message( (String) f.get( null ), 1 );
                //The 2 lines below check that there is a bundle key for
                //the choice corresponding to 1.
                String filler1 = bundle.getString( f.get( null ) + "_CHOICE_0" );
                result &= fromMessages1.indexOf( filler1 ) >= 0;
                //Now check that there is a bundle key for the choice for values > 1.
                String fromMessages2 = Messages.message( (String) f.get( null ), 2 );
                String filler2 = bundle.getString( f.get( null ) + "_CHOICE_1" );
                result &= fromMessages2.indexOf( filler2 ) >= 0;
                assert result : "Failed for key constant " + f;
            }
        }
        
        //Now check that for each key in the bundle there is a constant in Messages.
        for (Enumeration e = bundle.getKeys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            if (key.startsWith( "CK_" ) && (key.indexOf( "_CHOICE_" ) < 0)) {
                result &= checkContainsFieldFor( key );
                assert result : "No key constant for: " + key;
            }
        }
        return result;
    }
    
    public boolean message_String_String_int_Test() throws Exception {
        boolean result = true;
        //Check that for each One Place Key Choice Key in the Messages class,
        //there is a constant in the properties file.
        for (Iterator itor = keyConsts.iterator(); itor.hasNext(); ) {
            Field f = (Field) itor.next();
            if (((String) f.get( null )).startsWith( "OPK_CK_" )) {
                //This method call will fail if the key is not in the props file.
                bundle.getString( (String) f.get( null ) );
                //Now check that it is properly formatted.
                String fromMessages1 = Messages.message( (String) f.get( null ), UNLIKELY_STRING, 1 );
                //The 3 lines below check that there is a bundle key for
                //the choice corresponding to 1, and that the correct formatting happens.
                String filler1 = bundle.getString( f.get( null ) + "_CHOICE_0" );
                result &= fromMessages1.indexOf( filler1 ) >= 0;
                result &= fromMessages1.indexOf( UNLIKELY_STRING ) >= 0;
                //Now check that there is a bundle key for the choice for values > 1.
                String fromMessages2 = Messages.message( (String) f.get( null ), UNLIKELY_STRING, 2 );
                String filler2 = bundle.getString( f.get( null ) + "_CHOICE_1" );
                result &= fromMessages2.indexOf( filler2 ) >= 0;
                result &= fromMessages2.indexOf( UNLIKELY_STRING ) >= 0;
                assert result : "Failed for key constant " + f;
            }
        }
     
        //Now check that for each key in the bundle there is a constant in Messages.
        for (Enumeration e = bundle.getKeys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            if (key.contains( "_CHOICE_")) continue;
            if (key.startsWith( "OPK_CK_" )) {
                assert checkContainsFieldFor( key ) : "No key constant for: " + key;
            }
        }
        return result;
    }
    
    public boolean nlTest() {
        return Messages.nl().equals( System.getProperty( "line.separator" ) );
    }
    
    public boolean passOrFailTest() {
        boolean result = true;
        result &= Messages.passOrFail( true ).equals( bundle.getString( Messages.SK_PASSED ) );
        result &= Messages.passOrFail( false ).equals( bundle.getString( Messages.SK_FAILED ) );
        return result;
    }
    
    /**
     * Check that there is a constant field for the given key.
     */
    private boolean checkContainsFieldFor( String key ) throws Exception {
        boolean result = false;
        for (Iterator itor=keyConsts.iterator(); itor.hasNext() && !result; ) {
            Field f = (Field) itor.next();
            result = (f.get( null )).equals( key );
        }
        return result;
    }
}