package benbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Loads previously stored task commands from the application's data file. */
public class StoredTaskLoader {
    /** The location of the stored task commands, relative to the working directory. */
    private static final Path DEFAULT_STORED_TASK_PATH = Path.of("data/stored-task");

    /** The location from which this loader reads task commands. */
    private final Path storedTaskPath;

    /** Creates a loader for task commands stored in the application's data file. */
    public StoredTaskLoader() {
        this(DEFAULT_STORED_TASK_PATH);
    }

    /**
     * Creates a loader for task commands stored at the supplied path.
     *
     * @param storedTaskPath the file that contains the stored task commands.
     */
    StoredTaskLoader(Path storedTaskPath) {
        this.storedTaskPath = storedTaskPath;
    }

    /**
     * Loads each command in the stored-task file into the supplied task list.
     * Loading is silent: commands are processed but their normal responses are not displayed.
     *
     * @param taskLoader processes each stored command.
     * @param tasks the task array to populate.
     * @param taskCountPointer holds the current number of stored tasks.
     */
    public void load(TaskLoader taskLoader, Task[] tasks, int[] taskCountPointer) {
        if (!Files.exists(storedTaskPath)) {
            System.out.println("No stored data found starting from scratch...");
            System.out.println(BenBot.DIVIDER);
            return;
        }

        try {
            int loadedTaskCount = loadStoredTasks(taskLoader, tasks, taskCountPointer);
            printLoadedTaskCount(loadedTaskCount);
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /** Reads and validates the stored commands, returning the number of tasks added. */
    private int loadStoredTasks(TaskLoader taskLoader, Task[] tasks, int[] taskCountPointer)
            throws IOException {
        int taskCountBeforeLoading = taskCountPointer[0];
        String storedData = Files.readString(storedTaskPath);
        String[] commands = storedData.split("\\R");
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].isBlank()) {
                continue;
            }
            loadStoredCommand(commands[i], i + 1, taskLoader, tasks, taskCountPointer);
        }
        return taskCountPointer[0] - taskCountBeforeLoading;
    }

    /** Loads one command or reports its line as corrupted when it does not change valid state. */
    private void loadStoredCommand(String command, int lineNumber, TaskLoader taskLoader,
                                   Task[] tasks, int[] taskCountPointer) throws IOException {
        int taskCountBefore = taskCountPointer[0];
        int markedTaskIndex = getMarkedTaskIndex(command, taskCountBefore);
        boolean wasMarkedTaskDone = markedTaskIndex >= 0 && tasks[markedTaskIndex].isDone();

        taskLoader.addTask(command, tasks, taskCountPointer, false);

        boolean wasTaskAdded = taskCountPointer[0] == taskCountBefore + 1;
        boolean wasTaskMarked = markedTaskIndex >= 0
                && !wasMarkedTaskDone && tasks[markedTaskIndex].isDone();
        if (wasTaskAdded || wasTaskMarked) {
            return;
        }
        throw new IOException("Stored-task file is corrupted at line " + lineNumber
                + ". Each line must be a valid todo, deadline, event, or mark command.");
    }

    private void printLoadedTaskCount(int loadedTaskCount) {
        String taskWord = loadedTaskCount == 1 ? "task" : "tasks";
        System.out.println(loadedTaskCount + " " + taskWord + " already in storage.");
        System.out.println(BenBot.DIVIDER);
    }

    /** Returns the zero-based task index in a stored mark command, or -1 when it is invalid. */
    private int getMarkedTaskIndex(String command, int taskCount) {
        String[] words = command.trim().split("\\s+");
        if (words.length != 2 || !words[0].equals("mark")) {
            return -1;
        }

        try {
            int taskNumber = Integer.parseInt(words[1]);
            return taskNumber >= 1 && taskNumber <= taskCount ? taskNumber - 1 : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
