package jet.ikonmaker.test;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import jet.ikonmaker.EditOperation;
import jet.ikonmaker.FlatFileIkonHistory;
import jet.testtools.Waiting;
import jet.testtools.Files;

/**
 * @author Tim Lavers
 */
public class FlatFileIkonHistoryTest {
    private File dataDir;
    private FlatFileIkonHistory ffih;

    public boolean constructorTest() {
        init();
        assert ffih.allOperations().isEmpty();

        List<EditOperation> expected = new LinkedList<EditOperation>();
        for (int row=0; row<100; row++) {
            for (int column = 0; column < 50; column++) {
                expected.add( EditOperationTest.speo( row, column, new Color( row, column, row + column)));
                ffih.ikonChanged( EditOperationTest.speo( row, column, new Color( row, column, row + column)));
            }
        }
        assert expected.equals( ffih.allOperations() );

        //Rebuild from db.
        ffih.shuttingDown();//Clean up the old one.
        ffih = new FlatFileIkonHistory( new File( dataDir, "ffih" ), null );
        assert expected.equals( ffih.allOperations() );

        return true;
    }

    public boolean ikonChangedTest() {
        //Tested above.
        return true;
    }

    public boolean allOperationsTest() {
        init();
        List<EditOperation> expected = new LinkedList<EditOperation>();
        ffih.ikonChanged( EditOperationTest.speo(10, 12, Color.MAGENTA) );
        expected.add( EditOperationTest.speo(10, 12, Color.MAGENTA) );
        assert ffih.allOperations().equals( expected );
        //Check that no error happens when the ikon is closed for writing
        ffih.shuttingDown();
        assert ffih.allOperations().equals( expected );
        return true;
    }

    public boolean shuttingDownTest() {
        init();
        ffih.shuttingDown();
        ffih.shuttingDown();//No harm in doing it twice.
        assert new File( dataDir, "ffih" ).delete() : "Could not delete file after shutdown!";
        ffih.shuttingDown();
        return true;
    }

    public boolean recordDimensionTest() {
        init( 89, 234 );
        assert ffih.dimension().equals( new Dimension( 89, 234 ) ) : ffih.dimension();
        ffih.shuttingDown();
        ffih = new FlatFileIkonHistory( new File( dataDir, "ffih" ), null );
        assert ffih.dimension().equals( new Dimension( 89, 234 ) );
        ffih.shuttingDown();
        return true;
    }

    public boolean dimensionTest() {
        init();
        for (int row=0; row<100; row++) {
            for (int column = 0; column < 50; column++) {
                ffih.ikonChanged( EditOperationTest.speo( row, column, new Color( row, column, row + column)));
            }
        }
        assert ffih.dimension().equals( new Dimension( 16, 16 ) );
        ffih.shuttingDown();
        ffih = new FlatFileIkonHistory( new File( dataDir, "ffih" ), null );
        assert ffih.dimension().equals( new Dimension( 16, 16 ) );
        ffih.shuttingDown();
        return true;
    }

    private void init() {
        init( 16, 16 );
    }

    private void init( int width, int height ) {
        if (ffih != null) {
            ffih.shuttingDown();
            ffih = null;
        }
        Waiting.pause(100);
        dataDir = Files.cleanedTempDir();
        ffih = new FlatFileIkonHistory( new File( dataDir, "ffih" ), new Dimension( width, height ) );
    }
}