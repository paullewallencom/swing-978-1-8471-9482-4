package jet.testtools.test;

import jet.testtools.*;

import java.io.*;

public class SizeTest implements Serializable {
    public boolean sizeInBytesTest() {
        checkWithDummySerializable( 0);
        checkWithDummySerializable( 1);
        checkWithDummySerializable( 10);
        checkWithDummySerializable( 100);
        checkWithDummySerializable( 1000);
        checkWithDummySerializable( 10000);
        checkWithByteArray( 0 );
        checkWithByteArray( 1 );
        checkWithByteArray( 10 );
        checkWithByteArray( 100 );
        checkWithByteArray( 1000 );
        checkWithByteArray( 10000 );
        return true;
    }

    private void checkWithDummySerializable( int i ) {
        int size = Size.sizeInBytes( new DummySerializable( i ) );
        int baseSize = Size.sizeInBytes( new DummySerializable( 0 ) );
        assert (size - baseSize) == i;
    }

    private void checkWithByteArray( int i ) {
        int size = Size.sizeInBytes( new byte[i] );
        int baseSize = Size.sizeInBytes( new byte[0] );
        assert (size - baseSize) == i;
    }
    public class DummySerializable implements Serializable {
        byte[] data;

        public DummySerializable( int size ) {
            data = new byte[size];
        }
    }
}
