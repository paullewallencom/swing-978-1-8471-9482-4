package jet.testtools;

import java.io.*;

/**
 * For testing whether instances of
 * <code>java.io.Serializable</code> really are
 * serializable. This code is taken from a JavaWorld
 * article about deep copying:
 * "Java Tip 76: An alternative to the deep copy technique"
 * By Dave Miller, JavaWorld.com, 08/06/99.
 * See http://www.javaworld.com/javaworld/javatips/jw-javatip76.html
 */
public class SerializationTester {
    public static boolean check( Serializable object ) {
        boolean result = false;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream( bos );
            oos.writeObject( object );
            oos.flush();
            ByteArrayInputStream bin =
                    new ByteArrayInputStream( bos.toByteArray() );
            ois = new ObjectInputStream( bin );
            Object deserialized = ois.readObject();
            result = deserialized != null;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
                //Don't worry.
            }
            try {
                ois.close();
            } catch (Exception e) {
                //Don't worry.
            }
        }
        return result;
    }

    //Prevent instantiation.
    private SerializationTester() {
    }
}