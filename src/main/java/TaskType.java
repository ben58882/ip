/** The kinds of tasks that BenBot can store. */
public enum TaskType {
    /** A task without a date or time. */
    TODO("[T]"),

    /** A task that must be completed by a date or time. */
    DEADLINE("[D]"),

    /** A task with a start and end time. */
    EVENT("[E]");

    private final String symbol;

    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /** Returns the type symbol used in the task list. */
    public String getSymbol() {
        return symbol;
    }
}
