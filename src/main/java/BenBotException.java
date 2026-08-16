/** The base class for input errors that BenBot can report to the user. */
public class BenBotException extends Exception {
    /** Creates an exception with a user-friendly explanation. */
    public BenBotException(String message) {
        super(message);
    }
}
