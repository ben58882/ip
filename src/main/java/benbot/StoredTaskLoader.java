package benbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Loads previously stored task commands from the application's data file. */
public class StoredTaskLoader {
    /** The location of the stored task commands, relative to the working directory. */
    private static final Path STORED_TASK_PATH = Path.of("data/stored-task");

    /** Creates a loader for task commands stored in the application's data file. */
    public StoredTaskLoader() {
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
        if (!Files.exists(STORED_TASK_PATH)) {
            System.out.println("No stored data found starting from scratch...");
            System.out.println(BenBot.DIVIDER);
            return;
        }

        try {
            int taskCountBeforeLoading = taskCountPointer[0];
            String storedData = Files.readString(STORED_TASK_PATH);
            String[] commands = storedData.split("\\R");
            for (int i = 0; i < commands.length; i++) {
                String command = commands[i];
                if (!command.isBlank()) {
                    int taskCountBefore = taskCountPointer[0];
                    boolean isMarkCommand = command.startsWith("mark ");
                    int markedTaskIndex = getMarkedTaskIndex(command, taskCountBefore);
                    boolean wasMarkedTaskDone = isMarkCommand && markedTaskIndex >= 0
                            && tasks[markedTaskIndex].isDone();
                    taskLoader.addTask(command, tasks, taskCountPointer, false);
                    boolean addedTask = taskCountPointer[0] == taskCountBefore + 1;
                    boolean markedTask = isMarkCommand && markedTaskIndex >= 0
                            && !wasMarkedTaskDone && tasks[markedTaskIndex].isDone();
                    if (!addedTask && !markedTask) {
                        throw new IOException("Stored-task file is corrupted at line " + (i + 1)
                                + ". Each line must be a valid todo, deadline, event, or mark command.");
                    }
                }
            }
            int loadedTaskCount = taskCountPointer[0] - taskCountBeforeLoading;
            String taskWord = loadedTaskCount == 1 ? "task" : "tasks";
            System.out.println(loadedTaskCount + " " + taskWord + " already in storage.");
            System.out.println(BenBot.DIVIDER);
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
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
