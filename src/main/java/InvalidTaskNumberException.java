/** Indicates that a task number is missing, malformed, or outside the task list. */
public class InvalidTaskNumberException extends BenBotException {
    /** Creates an exception with a user-friendly task-number error message. */
    public InvalidTaskNumberException(String message) {
        super(message);
    }
}
