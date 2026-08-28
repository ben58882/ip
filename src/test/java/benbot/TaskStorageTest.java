package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests saving and loading tasks through a temporary data file. */
class TaskStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void store_writesReloadableTasksAndCompletionState() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("data/stored-task");
        Task firstTask = new Task("read book");
        Task secondTask = new Task("buy pen");
        secondTask.markDone();

        new TaskDataStore(storedTaskPath).store(new Task[] {firstTask, secondTask}, 2);

        String storedData = Files.readString(storedTaskPath);
        assertEquals("todo read book" + System.lineSeparator()
                        + "todo buy pen" + System.lineSeparator()
                        + "mark 2" + System.lineSeparator(),
                storedData);
    }

    @Test
    void load_savedTasks_recreatesTasksAndCompletionState() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo read book\n"
                + "deadline return book /by 1/12/2029\n"
                + "mark 2\n");
        Task[] tasks = new Task[3];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(3), tasks, taskCount));

        assertEquals(2, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertEquals("deadline return book /by 1/12/2029", tasks[1].toStorageString());
        assertTrue(tasks[1].isDone());
        assertTrue(output.contains("2 tasks already in storage."));
    }

    @Test
    void load_missingFile_reportsFreshStartWithoutChangingTasks() throws Exception {
        Path missingPath = temporaryDirectory.resolve("missing-task-file");
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(missingPath)
                .load(new TaskLoader(1), tasks, taskCount));

        assertEquals(0, taskCount[0]);
        assertTrue(output.contains("No stored data found starting from scratch..."));
    }

    @Test
    void load_corruptFile_reportsErrorAndKeepsValidEarlierTasks() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo read book\ninvalid command\n");
        Task[] tasks = new Task[2];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> new StoredTaskLoader(storedTaskPath)
                .load(new TaskLoader(2), tasks, taskCount));

        assertEquals(1, taskCount[0]);
        assertFalse(tasks[0].isDone());
        assertTrue(output.contains("Stored-task file is corrupted at line 2."));
    }
}
