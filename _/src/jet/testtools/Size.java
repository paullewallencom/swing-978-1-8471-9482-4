package jet.testtools;

import java.io.*;

/**
 * For measuring the memory usage of serialisable objects.
 */
public class Size {
    //Private constructor as this class has only static methods.
    private Size() {}
    
    public static int sizeInBytes( Serializable s) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( s );
            oos.flush();
            return baos.toByteArray().length;
        } catch (IOException e) {
            e.printStackTrace( );
            assert false : "Could not serialise object, as shown.";
        }
        return -1;
    }
}
