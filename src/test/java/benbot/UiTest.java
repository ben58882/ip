package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests BenBot's user-interface welcome and exit behaviour. */
class UiTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void welcome_displaysGreetingAndDivider() throws Exception {
        Ui ui = new Ui(new Scanner(""), new TaskDataStore(temporaryDirectory.resolve("tasks")));

        String output = OutputCapture.capture(ui::welcome);

        assertTrue(output.contains("Hello! I'm BenBot."));
        assertTrue(output.contains(BenBot.DIVIDER));
    }

    @Test
    void run_byeCommand_storesTasksBeforeExiting() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Ui ui = new Ui(new Scanner("todo read book\nbye\n"), new TaskDataStore(storedTaskPath));
        Task[] tasks = new Task[2];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() -> ui.run(new TaskLoader(2), tasks, taskCount));

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book" + System.lineSeparator(), Files.readString(storedTaskPath));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }
}
