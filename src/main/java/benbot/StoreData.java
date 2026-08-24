package benbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Stores BenBot's tasks so that they can be reloaded in a later run. */
public class StoreData {
    /** The location of the file containing task commands. */
    private static final Path STORED_TASK_PATH = Path.of("data/stored-task");

    /**
     * Writes every stored task as one reloadable command line.
     * The file is created when absent and replaced when it already exists.
     *
     * @param tasks the task array to store.
     * @param taskCount the number of tasks currently stored in the array.
     * @throws IOException If the data file or its parent directory cannot be written.
     */
    public void store(Task[] tasks, int taskCount) throws IOException {
        Path parentDirectory = STORED_TASK_PATH.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        StringBuilder storedData = new StringBuilder();
        for (int i = 0; i < taskCount; i++) {
            storedData.append(tasks[i].toStorageString()).append(System.lineSeparator());
        }
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].isDone()) {
                storedData.append("mark ").append(i + 1).append(System.lineSeparator());
            }
        }

        Files.writeString(STORED_TASK_PATH, storedData.toString(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
