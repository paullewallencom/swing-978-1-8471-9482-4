package jet.util.test;

import jet.util.Result;

public class ResultTest {

    public boolean createSuccessTest() {
        String value = "hooray!";
        Result<String> r1 = Result.createSuccess( value );
        assert r1.value().equals( value );
        assert r1.exception() == null;
        assert r1.pass();
        return true;
    }

    public boolean createFailureTest() {
        Exception e = new IllegalArgumentException( "This is the error message." );
        assert Result.createFailure( e ).exception() == e;
        assert !Result.createFailure( e ).pass();
        assert !Result.createFailure( null ).pass();
        return true;
    }

    public boolean valueTest() {
        assert Result.createSuccess( null ).value() == null;
        String value = "this is the result";
        assert Result.createSuccess( value ).value().equals( value );
        assert Result.createFailure( new NullPointerException() ).value() == null;
        return true;
    }

    public boolean passTest() {
        assert !Result.createFailure( new IllegalArgumentException( "This is the error message." ) ).pass();
        assert !Result.createFailure( null ).pass();
        return true;
    }

    public boolean exceptionTest() {
        Error e = new IllegalAccessError( "No way!" );
        assert Result.createFailure( e ).exception() == e;
        return true;
    }
}
