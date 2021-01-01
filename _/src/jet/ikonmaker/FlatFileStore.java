package jet.ikonmaker;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Directory-based persistence mechanism for the bitmaps,
 * with each stored in its own file.
 * 
 * @author Tim Lavers
 */
public class FlatFileStore implements Store {
    private File directory;
    private IkonHistory currentHistory;

    public FlatFileStore( File directory ) {
        this.directory = directory;
        directory.mkdirs();
    }

    public void saveNewIkon( Ikon ikon ) {
        if (storedIkonNames().contains( ikon.name() )) {
            throw new IllegalArgumentException( "An ikon with this name already exists: " + ikon.name() );
        }
        File file = new File( directory, ikon.name() + ".ikon" );
        IkonHistory history = new FlatFileIkonHistory( file, new Dimension( ikon.width(), ikon.height() ) );
        for (int row = 0; row < ikon.height(); row++) {
            for (int column = 0; column < ikon.width(); column++) {
                history.ikonChanged( new SinglePixelEditOperation( row, column, ikon.colourAt( row, column ) ) );
            }
        }
        history.shuttingDown();
    }

    public IkonHistory historyOf( IkonName ikonName ) {
        if (currentHistory != null) {
            currentHistory.shuttingDown();
        }
        File file = new File( directory, ikonName.toString() + ".ikon" );
        currentHistory = new FlatFileIkonHistory( file, null );
        return currentHistory;
    }

    public SortedSet<Ikon> storedIkons() {
        TreeSet<Ikon> result = new TreeSet<Ikon>();
        for (IkonName in : storedIkonNames()) {
            IkonHistory history = historyOf( in );
            Dimension dimension = history.dimension();
            Ikon ike = new Ikon( dimension.width,  dimension.height, Color.WHITE, in );
            for (EditOperation op : history.allOperations()) {
                op.apply( ike );
            }
            history.shuttingDown();
            result.add( ike );
        }
        return result;
    }

    public SortedSet<IkonName> storedIkonNames() {
        File[] files = directory.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.endsWith( ".ikon" );
            }
        } );
        TreeSet<IkonName> result = new TreeSet<IkonName>();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            result.add( new IkonName( fileName.split( "\\." )[0] ) );
        }
        return result;
    }
}
