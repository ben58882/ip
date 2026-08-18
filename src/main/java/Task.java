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

    /** Returns the task in the format shown by BenBot. */
    @Override
    public String toString() {
        return type.getSymbol() + getStatusIcon() + " " + description;
    }
}
