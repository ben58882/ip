/** Indicates that the fixed-size in-memory task list cannot accept another task. */
public class TaskListFullException extends BenBotException {
    /** Creates an exception explaining that the task list has reached its limit. */
    public TaskListFullException() {
        super("Sorry, the task list is full.");
    }
}
