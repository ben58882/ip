/** Indicates that a command is unknown or does not follow its required format. */
public class InvalidCommandException extends BenBotException {
    /** Creates an exception with instructions for correcting the command. */
    public InvalidCommandException(String message) {
        super(message);
    }
}
