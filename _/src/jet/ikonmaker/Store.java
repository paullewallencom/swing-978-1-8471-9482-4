package jet.ikonmaker;

import java.util.SortedSet;

/**
 * Abstraction of a persistence mechanism for all of the
 * ikons managed by an application.
 * 
 * @author Tim Lavers
 */
public interface Store {
    SortedSet<Ikon> storedIkons();
    SortedSet<IkonName> storedIkonNames();
    void saveNewIkon( Ikon ikon );
    IkonHistory historyOf( IkonName ikonName );
}
