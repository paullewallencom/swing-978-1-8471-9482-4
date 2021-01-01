package jet.ikonmaker;

import java.awt.Dimension;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Stores the command history for a particular bitmap
 * in a file.
 *
 * @author Tim Lavers
 */
public class FlatFileIkonHistory implements IkonHistory {
    
    private File file;
    private BufferedWriter writer;
    
    public FlatFileIkonHistory( File file, Dimension d ) {
        this.file = file;
        try {
            writer = new BufferedWriter( new FileWriter( file, true ) );
            if (d != null) {
                writer.write("" + d.width + ":" + d.height );
                writer.newLine();
                writer.flush();
            }
            assert dimension() != null;//Check the persisted state.
        } catch (IOException ex) {
            throw new RuntimeException( ex );
        }
    }
    
    public void ikonChanged( EditOperation editOperation ) {
        try {
            writer.write( editOperation.stringSerialise() );
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            throw new RuntimeException( ex );
        }
    }
    
    public List<EditOperation> allOperations() {
        LinkedList<EditOperation> result = new LinkedList<EditOperation>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            //The first line is the dimension, so skip it.
            reader.readLine();
            String line = reader.readLine();
            while (line != null) {
                result.add( EditOperation.deserialise( line ) );
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception ex) {
            throw new RuntimeException( ex );
        }
        return result;
    }

    public void shuttingDown() {
        try {
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException( ex );
        }
    }
    
    public Dimension dimension() {
        //The first line is the dimension, so skip it.
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();
            if (line != null) {
                String[] dimStrs = line.split( ":" );
                return new Dimension( Integer.parseInt( dimStrs[0] ), Integer.parseInt( dimStrs[1] ) );
            }
        } catch (Exception ex) {
            throw new RuntimeException( ex );
        }
        return null;//Should never happen.
    }
}
