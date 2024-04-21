package ee.inbank.inbankbackend.exceptions;

/**
 * Thrown when no valid loan is found.
 */
public class AgeConstraintException extends Throwable {
    private final String message;
    private final Throwable cause;

    public AgeConstraintException(String message) {
        this(message, null);
    }

    public AgeConstraintException(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
