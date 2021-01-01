package jet.ikonmaker;

import java.awt.Dimension;
import java.util.List;

/**
 * The complete editing history for a bitmap.
 * 
 * @author Tim Lavers
 */
public interface IkonHistory extends Ikon.Listener{
    Dimension dimension();
    List<EditOperation> allOperations();
    void shuttingDown();
}
