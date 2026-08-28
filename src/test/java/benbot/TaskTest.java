package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests basic task state and the task categories shown to users. */
class TaskTest {
    @Test
    void task_newTask_isNotDoneAndUsesTodoFormat() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("todo read book", task.toStorageString());
    }

    @Test
    void markDone_thenMarkUndone_updatesCompletionState() {
        Task task = new Task("read book");

        task.markDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());

        task.markUndone();
        assertFalse(task.isDone());
    }

    @Test
    void taskTypes_returnExpectedListSymbols() {
        assertEquals("[T]", TaskType.TODO.getSymbol());
        assertEquals("[D]", TaskType.DEADLINE.getSymbol());
        assertEquals("[E]", TaskType.EVENT.getSymbol());
    }

    @Test
    void taskStatuses_returnExpectedCompletionIcons() {
        assertEquals("[ ]", TaskStatus.NOT_DONE.getIcon());
        assertEquals("[X]", TaskStatus.DONE.getIcon());
    }

    @Test
    void toDo_constructor_joinsDescriptionWords() {
        ToDo task = new ToDo(new String[] {"todo", "read", "book"});

        assertEquals("[T][ ] read book", task.toString());
        assertEquals("todo read book", task.toStorageString());
    }
}
