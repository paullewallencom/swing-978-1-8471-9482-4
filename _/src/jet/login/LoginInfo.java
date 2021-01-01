package jet.login;

/**
 * Encapsulates the data a user enters when logging in: user name, password,
 * and choice of application, as discussed in Chapter 7.
 */
public class LoginInfo {
    private String userName;
    private String password;
    private ApplicationChoice chosenApplication;

    public LoginInfo( String userName, String password, ApplicationChoice chosenApplication ) {
        this.userName = userName;
        this.password = password;
        this.chosenApplication = chosenApplication;
    }

    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final LoginInfo loginInfo = (LoginInfo) o;

        if (chosenApplication != loginInfo.chosenApplication) return false;
        if (!password.equals( loginInfo.password )) return false;
        if (!userName.equals( loginInfo.userName )) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = userName.hashCode();
        result = 29 * result + password.hashCode();
        result = 29 * result + chosenApplication.hashCode();
        return result;
    }

    public String userName() {
        return userName;
    }

    public String password() {
        return password;
    }

    public ApplicationChoice chosenApplication() {
        return chosenApplication;
    }
}
