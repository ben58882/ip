package benbot;

/** A task stored by BenBot. */
public class Task {
    private final String description;
    private final TaskType type;
    private TaskStatus status;

    /** Creates a basic to-do task. */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /** Creates a task with the specified type. */
    protected Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.status = TaskStatus.NOT_DONE;
    }

    /** Returns the icon for the current completion status. */
    protected String getStatusIcon() {
        return status.getIcon();
    }

    /** Marks this task as completed. */
    public void markDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as not completed. */
    public void markUndone() {
        status = TaskStatus.NOT_DONE;
    }

    /** Returns whether this task has been marked as completed. */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /** Returns this task as the command used to recreate it from stored data. */
    public String toStorageString() {
        return "todo " + description;
    }

    /** Returns this task's description for subclasses that build storage commands. */
    protected String getDescription() {
        return description;
    }

    /** Returns the task in the format shown by BenBot. */
    @Override
    public String toString() {
        return type.getSymbol() + getStatusIcon() + " " + description;
    }
}
