package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the command processor's task-list operations and responses. */
class TaskLoaderTest {
    @Test
    void addTask_todoDeadlineAndEvent_addsEachTask() {
        TaskLoader loader = new TaskLoader(3);
        Task[] tasks = new Task[3];
        int[] taskCount = {0};

        loader.addTask("todo read book", tasks, taskCount, false);
        loader.addTask("deadline return book /by 1/12/2029", tasks, taskCount, false);
        loader.addTask("event study /from 2/12/2019 1800 /to 2/12/2019 2000",
                tasks, taskCount, false);

        assertEquals(3, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertEquals("deadline return book /by 1/12/2029", tasks[1].toStorageString());
        assertEquals("event study /from 2/12/2019 1800 /to 2/12/2019 2000",
                tasks[2].toStorageString());
    }

    @Test
    void addTask_markUnmarkAndDelete_updateRequestedTask() {
        TaskLoader loader = new TaskLoader(3);
        Task[] tasks = new Task[3];
        int[] taskCount = {0};
        loader.addTask("todo first task", tasks, taskCount, false);
        loader.addTask("todo second task", tasks, taskCount, false);

        loader.addTask("mark 2", tasks, taskCount, false);
        assertTrue(tasks[1].isDone());

        loader.addTask("unmark 2", tasks, taskCount, false);
        assertFalse(tasks[1].isDone());

        loader.addTask("delete 1", tasks, taskCount, false);
        assertEquals(1, taskCount[0]);
        assertEquals("todo second task", tasks[0].toStorageString());
        assertNull(tasks[1]);
    }

    @Test
    void addTask_find_ignoresCaseAndDoesNotChangeTaskList() throws Exception {
        TaskLoader loader = new TaskLoader(3);
        Task[] tasks = {new Task("Read Book"), new Task("buy pen"), new Task("Book flight")};
        int[] taskCount = {3};

        String output = OutputCapture.capture(() ->
                loader.addTask("find BOOK", tasks, taskCount, true));

        assertEquals(3, taskCount[0]);
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] Read Book"));
        assertFalse(output.contains("2.[T][ ] buy pen"));
        assertTrue(output.contains("3.[T][ ] Book flight"));
        assertTrue(output.indexOf("1.[T][ ] Read Book")
                < output.indexOf("3.[T][ ] Book flight"));
    }

    @Test
    void addTask_listAndInvalidCommand_printExpectedResponses() throws Exception {
        TaskLoader loader = new TaskLoader(2);
        Task[] tasks = {new Task("read book"), null};
        int[] taskCount = {1};

        String listOutput = OutputCapture.capture(() ->
                loader.addTask("list", tasks, taskCount, true));
        String errorOutput = OutputCapture.capture(() ->
                loader.addTask("unknown", tasks, taskCount, true));

        assertTrue(listOutput.contains("Here are the tasks in your list:"));
        assertTrue(listOutput.contains("1.[T][ ] read book"));
        assertTrue(errorOutput.contains("I don't know what that means."));
        assertEquals(1, taskCount[0]);
    }

    @Test
    void addTask_invalidTaskNumberAndFullList_leaveTasksUnchanged() throws Exception {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = {new Task("read book")};
        int[] taskCount = {1};

        String invalidNumberOutput = OutputCapture.capture(() ->
                loader.addTask("mark 2", tasks, taskCount, true));
        String fullListOutput = OutputCapture.capture(() ->
                loader.addTask("todo another task", tasks, taskCount, true));

        assertTrue(invalidNumberOutput.contains("That task number does not exist."));
        assertTrue(fullListOutput.contains("Sorry, the task list is full."));
        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
    }

    @Test
    void addTask_byeWithoutArguments_requestsExit() {
        TaskLoader loader = new TaskLoader(1);

        assertTrue(loader.addTask("bye", new Task[1], new int[] {0}, false));
        assertFalse(loader.addTask("bye later", new Task[1], new int[] {0}, false));
    }

    @Test
    void addTask_invalidEventStructure_rejectsCommand() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};
        String[] invalidCommands = {
            "event /from 1/1/2029 /to 2/1/2029",
            "event meeting 1/1/2029 /to 2/1/2029",
            "event meeting /from 1/1/2029 2/1/2029",
            "event meeting /to 2/1/2029 /from 1/1/2029",
            "event meeting /from /to 2/1/2029",
            "event meeting /from 1/1/2029 /to"
        };

        for (String command : invalidCommands) {
            loader.addTask(command, tasks, taskCount, false);
            assertEquals("Use: event DESCRIPTION /from START /to END", loader.getLastResponse());
        }
        assertEquals(0, taskCount[0]);
    }

    @Test
    void addTask_taskCountExceedsCapacity_throwsAssertionError() {
        TaskLoader loader = new TaskLoader(1);

        assertThrows(AssertionError.class, () ->
                loader.addTask("list", new Task[1], new int[] {2}, false));
    }

    @Test
    void addTask_contactCommands_manageContactsSeparatelyFromTasks() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask("contact Alice Tan /phone 91234567 /email alice@example.com",
                tasks, taskCount, false);
        loader.addTask("contact Bob /phone 87654321 /email bob@example.com",
                tasks, taskCount, false);
        loader.addTask("contacts", tasks, taskCount, false);

        assertEquals(0, taskCount[0]);
        assertTrue(loader.getLastResponse().contains(
                "1.[C] Alice Tan (phone: 91234567; email: alice@example.com)"));
        assertTrue(loader.getLastResponse().contains(
                "2.[C] Bob (phone: 87654321; email: bob@example.com)"));

        loader.addTask("delete-contact 1", tasks, taskCount, false);
        loader.addTask("contacts", tasks, taskCount, false);

        assertFalse(loader.getLastResponse().contains("Alice Tan"));
        assertTrue(loader.getLastResponse().contains("1.[C] Bob"));
    }

    @Test
    void addTask_invalidContactCommands_leaveContactsUnchanged() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};
        String[] invalidCommands = {
            "contact Alice /email alice@example.com /phone 91234567",
            "contact Alice /phone /email alice@example.com",
            "contact Alice /phone 91234567 /email",
            "delete-contact one",
            "delete-contact 1"
        };

        for (String command : invalidCommands) {
            loader.addTask(command, tasks, taskCount, false);
        }
        loader.addTask("contacts", tasks, taskCount, false);

        assertEquals("Here are your contacts:", loader.getLastResponse());
    }
}
