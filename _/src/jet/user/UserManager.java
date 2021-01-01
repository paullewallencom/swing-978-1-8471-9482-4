package jet.user;

import org.grandtestauto.*;

import java.util.*;

/**
 * See Chapter 15.
 */
@DoesNotNeedTest( reason="Only to sketch a design pattern.")
public class UserManager {
    private Set<User> users = new HashSet<User>();

    public void addUser( User user ) {
        users.add( user );
    }
    //etc...
}
