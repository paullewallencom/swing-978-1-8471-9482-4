package jet.user;

import org.grandtestauto.*;

/**
 * See Chapter 15.
 */
@DoesNotNeedTest( reason="Only to sketch a design pattern.")
public class User {
    private String name;
    private String password;

    public void setName( String name ) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String password() {
        return password;
    }
}
