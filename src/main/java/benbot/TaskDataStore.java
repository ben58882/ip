package benbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** Stores BenBot's tasks so that they can be reloaded in a later run. */
public class TaskDataStore {
    /** The location of the file containing task commands. */
    private static final Path DEFAULT_STORED_TASK_PATH = Path.of("data/stored-task");

    /** The location to which this data store writes task commands. */
    private final Path storedTaskPath;

    /** Creates a data store that writes reloadable task commands. */
    public TaskDataStore() {
        this(DEFAULT_STORED_TASK_PATH);
    }

    /**
     * Creates a data store that writes task commands to the supplied path.
     *
     * @param storedTaskPath the file that receives the stored task commands.
     */
    TaskDataStore(Path storedTaskPath) {
        this.storedTaskPath = storedTaskPath;
    }

    /**
     * Writes every stored task as one reloadable command line.
     * The file is created when absent and replaced when it already exists.
     *
     * @param tasks the task array to store.
     * @param taskCount the number of tasks currently stored in the array.
     * @throws IOException if the data file or its parent directory cannot be written.
     */
    public void store(Task[] tasks, int taskCount) throws IOException {
        assert tasks != null : "A task array must be provided for storage";
        assert taskCount >= 0 && taskCount <= tasks.length
                : "Only tasks within the array can be stored";

        Path parentDirectory = storedTaskPath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        assert IntStream.range(0, taskCount).allMatch(index -> tasks[index] != null)
                : "Every task selected for storage must be initialized";
        Stream<String> taskCommands = IntStream.range(0, taskCount)
                .mapToObj(index -> tasks[index].toStorageString());
        Stream<String> markCommands = IntStream.range(0, taskCount)
                .filter(index -> tasks[index].isDone())
                .mapToObj(index -> "mark " + (index + 1));
        String storedData = Stream.concat(taskCommands, markCommands)
                .map(command -> command + System.lineSeparator())
                .collect(Collectors.joining());

        Files.writeString(storedTaskPath, storedData,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
