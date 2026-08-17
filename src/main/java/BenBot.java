import java.util.Scanner;

/** The entry point for the BenBot chatbot application. */
public class BenBot {
    /** The maximum number of tasks that BenBot can keep during one run. */
    private static final int MAX_TASKS = 100;

    /** A line used to separate BenBot's messages in the terminal. */
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Starts BenBot, stores entered tasks in memory, and exits when the user enters
     * {@code bye}.
     */
    public static void main(String[] args) {
        String banner = " ____              ____        _   \n"
                + "| __ )  ___ _ __  | __ )  ___ | |_ \n"
                + "|  _ \\ / _ \\ '_ \\ |  _ \\ / _ \\| __|\n"
                + "| |_) |  __/ | | || |_) | (_) | |_ \n"
                + "|____/ \\___|_| |_||____/ \\___/ \\__|\n";

        System.out.println(banner);
        System.out.println("Hello! I'm BenBot.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);

        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            System.out.println(DIVIDER);
            boolean shouldExit = false;

            try {
                if (line.isEmpty()) {
                    throw new InvalidCommandException("Please enter a command.");
                }
                String[] words = line.split("\\s+");
                String command = words[0];

                if (command.equals("bye")) {
                    requireNoArguments(words, "bye");
                    System.out.println("Bye. Hope to see you again soon!");
                    shouldExit = true;
                } else if (command.equals("list")) {
                    requireNoArguments(words, "list");
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                } else if (command.equals("todo")) {
                    requireDescription(words, "todo DESCRIPTION");
                    ensureTaskListHasSpace(taskCount);
                    tasks[taskCount] = new ToDo(words);
                    taskCount++;
                    printTaskAdded(tasks[taskCount - 1], taskCount);
                } else if (command.equals("deadline")) {
                    validateDeadline(words);
                    ensureTaskListHasSpace(taskCount);
                    tasks[taskCount] = new Deadline(words);
                    taskCount++;
                    printTaskAdded(tasks[taskCount - 1], taskCount);
                } else if (command.equals("event")) {
                    validateEvent(words);
                    ensureTaskListHasSpace(taskCount);
                    tasks[taskCount] = new Event(words);
                    taskCount++;
                    printTaskAdded(tasks[taskCount - 1], taskCount);
                } else if (command.equals("delete")) {
                    int taskIndex = getTaskIndex(words, taskCount, "delete");
                    Task removedTask = tasks[taskIndex];
                    taskCount = removeTask(tasks, taskIndex, taskCount);
                    printTaskRemoved(removedTask, taskCount);
                } else if (command.equals("mark")) {
                    Task task = tasks[getTaskIndex(words, taskCount, "mark")];
                    task.markDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task);
                } else if (command.equals("unmark")) {
                    Task task = tasks[getTaskIndex(words, taskCount, "unmark")];
                    task.markUndone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task);
                } else {
                    throw new InvalidCommandException("I don't know what that means.");
                }
            } catch (BenBotException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(DIVIDER);
            if (shouldExit) {
                break;
            }
        }
    }

    /** Prints the confirmation shown after adding a task. */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /** Prints the confirmation shown after deleting a task. */
    private static void printTaskRemoved(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /**
     * Removes a task by shifting all later tasks one position to the left.
     *
     * @return the new number of stored tasks
     */
    private static int removeTask(Task[] tasks, int taskIndex, int taskCount) {
        for (int i = taskIndex; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[taskCount - 1] = null;
        return taskCount - 1;
    }

    /** Ensures that a command without extra words has the expected format. */
    private static void requireNoArguments(String[] words, String command) throws InvalidCommandException {
        if (words.length != 1) {
            throw new InvalidCommandException("Use: " + command);
        }
    }

    /** Ensures that a task command contains a description. */
    private static void requireDescription(String[] words, String usage) throws InvalidCommandException {
        if (words.length < 2) {
            throw new InvalidCommandException("Use: " + usage);
        }
    }

    /** Ensures the fixed-size task array has a free position. */
    private static void ensureTaskListHasSpace(int taskCount) throws TaskListFullException {
        if (taskCount >= MAX_TASKS) {
            throw new TaskListFullException();
        }
    }

    /** Validates the required description and {@code /by} parts of a deadline. */
    private static void validateDeadline(String[] words) throws InvalidCommandException {
        int byIndex = findMarker(words, "/by");
        if (byIndex <= 1 || byIndex >= words.length - 1) {
            throw new InvalidCommandException("Use: deadline DESCRIPTION /by DATE");
        }
    }

    /** Validates the required description, {@code /from}, and {@code /to} parts of an event. */
    private static void validateEvent(String[] words) throws InvalidCommandException {
        int fromIndex = findMarker(words, "/from");
        int toIndex = findMarker(words, "/to");
        if (fromIndex == words.length || toIndex == words.length
                || fromIndex <= 1 || toIndex <= fromIndex + 1 || toIndex == words.length - 1) {
            throw new InvalidCommandException("Use: event DESCRIPTION /from START /to END");
        }
    }

    /** Finds a formatting marker, returning the array length when it is absent. */
    private static int findMarker(String[] words, String marker) {
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(marker)) {
                return i;
            }
        }
        return words.length;
    }

    /** Converts and validates the one-based task number in a mark command. */
    private static int getTaskIndex(String[] words, int taskCount, String command)
            throws InvalidTaskNumberException {
        if (words.length != 2) {
            throw new InvalidTaskNumberException("Use: " + command + " TASK_NUMBER");
        }

        try {
            int taskNumber = Integer.parseInt(words[1]);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new InvalidTaskNumberException("That task number does not exist.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException("The task number must be a whole number.");
        }
    }
}
