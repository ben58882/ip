package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the application coordinator's loading and interaction delegation. */
class BenBotTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_delegatesToStoredTaskLoader() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Files.writeString(storedTaskPath, "todo read book\n");
        Task[] tasks = new Task[2];
        int[] taskCount = {0};
        BenBot benBot = new BenBot(tasks, taskCount, new TaskLoader(2),
                new StoredTaskLoader(storedTaskPath),
                new Ui(new Scanner(""), new TaskDataStore(temporaryDirectory.resolve("output"))));

        String output = OutputCapture.capture(benBot::load);

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertTrue(output.contains("1 task already in storage."));
    }

    @Test
    void run_delegatesToUiCommandLoop() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Task[] tasks = new Task[2];
        int[] taskCount = {0};
        BenBot benBot = new BenBot(tasks, taskCount, new TaskLoader(2),
                new StoredTaskLoader(temporaryDirectory.resolve("input")),
                new Ui(new Scanner("todo read book\nbye\n"), new TaskDataStore(storedTaskPath)));

        BenBot finalBenBot = benBot;
        OutputCapture.capture(finalBenBot::run);

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book" + System.lineSeparator(), Files.readString(storedTaskPath));
    }
}
