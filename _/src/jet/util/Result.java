package jet.util;

/**
 * Wrapper for the return, or error, of a method invocation on the server.
 */
public class Result<E> {

    private boolean succeeded;
    private E result;
    private Throwable error;

    public static <E> Result<E> createSuccess( E value ) {
        return new Result<E>( true, value, null );
    }

    public static Result<Object> createFailure( Throwable exception ) {
        return new Result<Object>( false, null, exception );
    }

    private Result( boolean succeeded, E result, Throwable error ) {
        this.succeeded = succeeded;
        this.result = result;
        this.error = error;
    }

    /**
     * Did the method call succeed?
     */
    public boolean pass() {
        return succeeded;
    }

    /**
     * The value of a succesful method call.
     */
    public E value() {
        return result;
    }

    /**
     * If an exception was thrown in the method call, this is it.
     */
    public Throwable exception() {
        return error;
    }
}
