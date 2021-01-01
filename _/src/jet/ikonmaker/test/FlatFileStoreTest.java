package jet.ikonmaker.test;

import jet.ikonmaker.*;
import jet.testtools.Files;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * @author Tim Lavers
 */
public class FlatFileStoreTest {
    private File dataDir;
    private FlatFileStore store;

    public boolean constructorTest() {
        init();
        assert store.storedIkonNames().isEmpty();
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "ikon1" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "ikon2" ) ) );
        TreeSet<IkonName> expected = new TreeSet<IkonName>();
        expected.add( IkonTest.in( "ikon1" ) );
        expected.add( IkonTest.in( "ikon2" ) );
        store = new FlatFileStore( dataDir );
        assert store.storedIkonNames().equals( expected );
        return true;
    }

    public boolean storedIkonNamesTest() {
        init();
        //Check that they are in ab order.
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "A" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "Z" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "C" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "X" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "b" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "q" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "r" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "e" ) ) );
        IkonHistory ih = store.historyOf( IkonTest.in( "e" ) );
        Iterator itor = store.storedIkonNames().iterator();
        assert itor.next().equals( IkonTest.in( "A" ) );
        assert itor.next().equals( IkonTest.in( "b" ) );
        assert itor.next().equals( IkonTest.in( "C" ) );
        assert itor.next().equals( IkonTest.in( "e" ) );
        assert itor.next().equals( IkonTest.in( "q" ) );
        assert itor.next().equals( IkonTest.in( "r" ) );
        assert itor.next().equals( IkonTest.in( "X" ) );
        assert itor.next().equals( IkonTest.in( "Z" ) );
        assert !itor.hasNext();
        ih.shuttingDown();//The previous ones are already shut down.
        return true;
    }

    public boolean storedIkonsTest() {
        init();
        //Check that they are in ab order.
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "A" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "Z" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "C" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "X" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "b" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "q" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "r" ) ) );
        store.saveNewIkon( new Ikon( 12, 23, Color.BLACK, IkonTest.in( "e" ) ) );
        IkonHistory ih = store.historyOf( IkonTest.in( "e" ) );
        Iterator<Ikon> itor = store.storedIkons().iterator();
        assert itor.next().name().equals( IkonTest.in( "A" ) );
        assert itor.next().name().equals( IkonTest.in( "b" ) );
        assert itor.next().name().equals( IkonTest.in( "C" ) );
        assert itor.next().name().equals( IkonTest.in( "e" ) );
        assert itor.next().name().equals( IkonTest.in( "q" ) );
        assert itor.next().name().equals( IkonTest.in( "r" ) );
        assert itor.next().name().equals( IkonTest.in( "X" ) );
        assert itor.next().name().equals( IkonTest.in( "Z" ) );
        assert !itor.hasNext();
        ih.shuttingDown();//The previous ones are already shut down.
        return true;
    }

    public boolean saveNewIkonTest() {
        init();
        store.saveNewIkon( new Ikon( 12, 13, Color.CYAN, IkonTest.in( "A" ) ) );
        IkonHistory ih = store.historyOf( IkonTest.in( "A" ) );
        assert ih.dimension().equals( new Dimension( 12, 13 ) );
        List<SinglePixelEditOperation> expectedOps = new LinkedList<SinglePixelEditOperation>();
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                expectedOps.add( new SinglePixelEditOperation( i, j, Color.CYAN ) );
            }
        }
        ih.shuttingDown();
        return true;
    }

    public boolean historyOfTest() {
        init();
        store.saveNewIkon( new Ikon( 2, 2, Color.BLACK, IkonTest.in( "ikon" ) ) );
        List<SinglePixelEditOperation> expected = new LinkedList<SinglePixelEditOperation>();
        expected.add( new SinglePixelEditOperation( 0, 0, Color.BLACK ) );
        expected.add( new SinglePixelEditOperation( 0, 1, Color.BLACK ) );
        expected.add( new SinglePixelEditOperation( 1, 0, Color.BLACK ) );
        expected.add( new SinglePixelEditOperation( 1, 1, Color.BLACK ) );
        IkonHistory ih = store.historyOf( IkonTest.in( "ikon" ) );
        assert ih.allOperations().equals( expected );
        LinkedList<Color> colours = new LinkedList<Color>();
        colours.add( Color.BLACK );
        colours.add( Color.BLUE );
        colours.add( Color.GREEN );
        colours.add( Color.CYAN );
        colours.add( Color.MAGENTA );
        colours.add( Color.YELLOW );
        for (Color colour : colours) {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 30; j++) {
                    ih.ikonChanged( new SinglePixelEditOperation( i, j, colour ) );
                    expected.add( new SinglePixelEditOperation( i, j, colour ) );
                }
            }
        }
        assert ih.allOperations().equals( expected );
        
        //Preserved after rebuild.
        ih.shuttingDown();
        store = new FlatFileStore( dataDir );
        ih = store.historyOf( IkonTest.in( "ikon" ) );
        assert ih.allOperations().equals( expected );
        ih.shuttingDown();
        return true;
    }

    private void init() {
        dataDir = Files.cleanedTempDir();
        store = new FlatFileStore( dataDir );
    }
}