package benbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Processes BenBot commands and updates the in-memory task list. */
public class TaskLoader {
    /** The maximum number of tasks the task array can contain. */
    private final int maxTasks;

    /** The response lines produced while processing the most recent command. */
    private final List<String> responseLines = new ArrayList<>();

    /**
     * Creates a command processor with the specified maximum task capacity.
     *
     * @param maxTasks the maximum number of tasks that may be stored.
     */
    public TaskLoader(int maxTasks) {
        this.maxTasks = maxTasks;
    }

    /**
     * Processes one command and optionally prints BenBot's response.
     *
     * @param line the command entered by the user.
     * @param tasks the task array to update.
     * @param taskCountPointer the current task count, stored in a one-element array.
     * @param shouldPrint whether to display command responses and error messages.
     * @return whether the command requests that BenBot exits.
     */
    public boolean addTask(String line, Task[] tasks, int[] taskCountPointer, boolean shouldPrint) {
        responseLines.clear();
        int taskCount = taskCountPointer[0];
        boolean shouldExit = false;

        try {
            if (line.isEmpty()) {
                throw new InvalidCommandException("Please enter a command.");
            }
            String[] words = line.split("\\s+");
            String command = words[0];

            if (command.equals("bye")) {
                requireNoArguments(words, "bye");
                printMessage(shouldPrint, "Bye. Hope to see you again soon!");
                shouldExit = true;
            } else if (command.equals("list")) {
                requireNoArguments(words, "list");
                printMessage(shouldPrint, "Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    printMessage(shouldPrint, (i + 1) + "." + tasks[i]);
                }
            } else if (command.equals("todo")) {
                requireDescription(words, "todo DESCRIPTION");
                ensureTaskListHasSpace(taskCount);
                tasks[taskCount] = new ToDo(words);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount, shouldPrint);
            } else if (command.equals("deadline")) {
                validateDeadline(words);
                ensureTaskListHasSpace(taskCount);
                tasks[taskCount] = new Deadline(words);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount, shouldPrint);
            } else if (command.equals("event")) {
                validateEvent(words);
                ensureTaskListHasSpace(taskCount);
                tasks[taskCount] = new Event(words);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount, shouldPrint);
            } else if (command.equals("delete")) {
                int taskIndex = getTaskIndex(words, taskCount, "delete");
                Task removedTask = tasks[taskIndex];
                taskCount = removeTask(tasks, taskIndex, taskCount);
                printTaskRemoved(removedTask, taskCount, shouldPrint);
            } else if (command.equals("mark")) {
                Task task = tasks[getTaskIndex(words, taskCount, "mark")];
                task.markDone();
                printMessage(shouldPrint, "Nice! I've marked this task as done:", "  " + task);
            } else if (command.equals("unmark")) {
                Task task = tasks[getTaskIndex(words, taskCount, "unmark")];
                task.markUndone();
                printMessage(shouldPrint, "OK, I've marked this task as not done yet:", "  " + task);
            } else if (command.equals("find")) {
                requireDescription(words, "find KEYWORD");
                String keyword = line.substring(command.length()).trim();
                printMatchingTasks(keyword, tasks, taskCount, shouldPrint);
            } else {
                throw new InvalidCommandException("I don't know what that means.");
            }
        } catch (BenBotException e) {
            printMessage(shouldPrint, e.getMessage());
        }
        taskCountPointer[0] = taskCount;
        return shouldExit;
    }

    /**
     * Returns the response produced by the most recently processed command.
     * This allows graphical interfaces to display replies without redirecting standard output.
     *
     * @return the complete response, with each response line separated by the platform line separator.
     */
    public String getLastResponse() {
        return String.join(System.lineSeparator(), responseLines);
    }

    /** Prints the confirmation shown after adding a task. */
    private void printTaskAdded(Task task, int taskCount, boolean shouldPrint) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        printMessage(shouldPrint,
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /** Prints the confirmation shown after deleting a task. */
    private void printTaskRemoved(Task task, int taskCount, boolean shouldPrint) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        printMessage(shouldPrint,
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /** Prints every task whose description contains the given keyword, ignoring case. */
    private void printMatchingTasks(String keyword, Task[] tasks, int taskCount, boolean shouldPrint) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        printMessage(shouldPrint, "Here are the matching tasks in your list:");

        for (int i = 0; i < taskCount; i++) {
            String description = tasks[i].getDescription().toLowerCase(Locale.ROOT);
            if (description.contains(normalizedKeyword)) {
                printMessage(shouldPrint, (i + 1) + "." + tasks[i]);
            }
        }
    }

    /** Prints responses only when output is enabled. */
    private void printMessage(boolean shouldPrint, String... messages) {
        responseLines.addAll(List.of(messages));
        if (!shouldPrint) {
            return;
        }
        for (String message : messages) {
            System.out.println(message);
        }
    }

    /**
     * Removes a task by shifting all later tasks one position to the left.
     *
     * @return the new number of stored tasks
     */
    private int removeTask(Task[] tasks, int taskIndex, int taskCount) {
        for (int i = taskIndex; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[taskCount - 1] = null;
        return taskCount - 1;
    }

    /** Ensures that a command without extra words has the expected format. */
    private void requireNoArguments(String[] words, String command) throws InvalidCommandException {
        if (words.length != 1) {
            throw new InvalidCommandException("Use: " + command);
        }
    }

    /** Ensures that a task command contains a description. */
    private void requireDescription(String[] words, String usage) throws InvalidCommandException {
        if (words.length < 2) {
            throw new InvalidCommandException("Use: " + usage);
        }
    }

    /** Ensures the fixed-size task array has a free position. */
    private void ensureTaskListHasSpace(int taskCount) throws TaskListFullException {
        if (taskCount >= maxTasks) {
            throw new TaskListFullException();
        }
    }

    /** Validates the required description and {@code /by} parts of a deadline. */
    private void validateDeadline(String[] words) throws InvalidCommandException {
        int byIndex = findMarker(words, "/by");
        if (byIndex <= 1 || byIndex >= words.length - 1) {
            throw new InvalidCommandException("Use: deadline DESCRIPTION /by DATE");
        }
    }

    /** Validates the required description, {@code /from}, and {@code /to} parts of an event. */
    private void validateEvent(String[] words) throws InvalidCommandException {
        int fromIndex = findMarker(words, "/from");
        int toIndex = findMarker(words, "/to");
        if (fromIndex == words.length || toIndex == words.length
                || fromIndex <= 1 || toIndex <= fromIndex + 1 || toIndex == words.length - 1) {
            throw new InvalidCommandException("Use: event DESCRIPTION /from START /to END");
        }
    }

    /** Finds a formatting marker, returning the array length when it is absent. */
    private int findMarker(String[] words, String marker) {
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(marker)) {
                return i;
            }
        }
        return words.length;
    }

    /** Converts and validates the one-based task number in a mark command. */
    private int getTaskIndex(String[] words, int taskCount, String command)
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
