package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests BenBot's user-interface welcome and exit behavior. */
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
    void welcome_displaysEveryAvailableCommand() throws Exception {
        Ui ui = new Ui(new Scanner(""), new TaskDataStore(temporaryDirectory.resolve("tasks")));
        String[] commandLines = {
            "todo DESCRIPTION",
            "deadline DESCRIPTION /by DATE [TIME]",
            "event DESCRIPTION /from START /to END",
            "list",
            "find KEYWORD",
            "mark TASK_NUMBER",
            "unmark TASK_NUMBER",
            "delete TASK_NUMBER",
            "contact NAME /phone PHONE /email EMAIL",
            "contacts",
            "delete-contact CONTACT_NUMBER",
            "note TEXT",
            "notes",
            "delete-note NOTE_NUMBER",
            "expense DESCRIPTION /amount AMOUNT",
            "expenses",
            "delete-expense EXPENSE_NUMBER"
        };

        String output = OutputCapture.capture(ui::welcome);
        List<String> outputLines = output.lines().map(String::strip).toList();

        for (String commandLine : commandLines) {
            assertTrue(outputLines.contains(commandLine));
        }
        assertTrue(outputLines.contains("Type bye to save all your data and exit."));
        assertTrue(output.contains("15/9/2026"));
        assertTrue(output.contains("1800"));
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

    @Test
    void run_inputEndsWithoutBye_doesNotStoreTasks() throws Exception {
        Path storedTaskPath = temporaryDirectory.resolve("stored-task");
        Ui ui = new Ui(new Scanner("todo read book\n"), new TaskDataStore(storedTaskPath));
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        OutputCapture.capture(() -> ui.run(new TaskLoader(1), tasks, taskCount));

        assertEquals(1, taskCount[0]);
        assertFalse(Files.exists(storedTaskPath));
    }

    @Test
    void storeData_storageFailure_returnsHelpfulError() throws Exception {
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Files.writeString(parentFile, "not a directory");
        Ui ui = new Ui(new Scanner(""), new TaskDataStore(parentFile.resolve("stored-task")));

        String response = ui.storeData(new TaskLoader(1), new Task[1], 0);

        assertTrue(response.startsWith("ERROR: Unable to store data:"));
    }

    @Test
    void run_byeWithStorageFailure_printsError() throws Exception {
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Files.writeString(parentFile, "not a directory");
        Ui ui = new Ui(new Scanner("bye\n"),
                new TaskDataStore(parentFile.resolve("stored-task")));

        String output = OutputCapture.capture(() ->
                ui.run(new TaskLoader(1), new Task[1], new int[] {0}));

        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        assertTrue(output.contains("ERROR: Unable to store data:"));
    }
}
