package jet.login.test;

import jet.login.*;

public class LoginInfoTest {
    static LoginInfo li( String userName, String password, ApplicationChoice ap ) {
        return new LoginInfo( userName, password, ap );
    }

    public boolean constructorTest() {
        LoginInfo li = li( "Harry", "Quidditch", ApplicationChoice.ADMINISTRATOR );
        assert li.userName().equals( "Harry" );
        assert li.password().equals( "Quidditch" );
        assert li.chosenApplication().equals( ApplicationChoice.ADMINISTRATOR );
        return true;
    }

    public boolean userNameTest() {
        assert li( "ThisIsTheUserName", "ThisIsThePassword", ApplicationChoice.CLINICAL_KNOWLEDGE_BUILDER ).userName().equals( "ThisIsTheUserName" );
        return true;
    }

    public boolean passwordTest() {
        assert li( "ThisIsTheUserName", "ThisIsThePassword", ApplicationChoice.CLINICAL_KNOWLEDGE_BUILDER ).password().equals( "ThisIsThePassword" );
        return true;
    }

    public boolean chosenApplicationTest() {
        assert li( "ThisIsTheUserName", "ThisIsThePassword", ApplicationChoice.CLINICAL_KNOWLEDGE_BUILDER ).chosenApplication().equals( ApplicationChoice.CLINICAL_KNOWLEDGE_BUILDER );
        return true;
    }

    public boolean equalsTest() {
        assert !li( "a", "b", ApplicationChoice.ADMINISTRATOR ).equals( null );//null ok
        assert !li( "a", "b", ApplicationChoice.ADMINISTRATOR ).equals( "String" );//different class
        assert !li( "a", "b", ApplicationChoice.ADMINISTRATOR ).equals( li( "A", "b", ApplicationChoice.ADMINISTRATOR ) );//Different user name
        assert !li( "a", "b", ApplicationChoice.ADMINISTRATOR ).equals( li( "a", "B", ApplicationChoice.ADMINISTRATOR ) );//Different password
        assert !li( "a", "b", ApplicationChoice.ADMINISTRATOR ).equals( li( "a", "b", ApplicationChoice.CLINICAL_KNOWLEDGE_BUILDER ) );//Different app
        assert li( "a", "b", ApplicationChoice.ADMINISTRATOR ).equals( li( "a", "b", ApplicationChoice.ADMINISTRATOR ) );
        return true;
    }

    public boolean hashCodeTest() {
        assert li( "a", "b", ApplicationChoice.ADMINISTRATOR ).hashCode() == li( "a", "b", ApplicationChoice.ADMINISTRATOR ).hashCode();
        return true;
    }
}
