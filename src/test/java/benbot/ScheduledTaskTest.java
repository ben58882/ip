package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests scheduled deadline and event task behaviour. */
class ScheduledTaskTest {
    @Test
    void deadline_dateAndTime_formatsForDisplayAndStorage() throws InvalidCommandException {
        Deadline deadline = new Deadline(new String[] {
            "deadline", "return", "book", "/by", "2/12/2019", "1800"
        });

        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00 pm)", deadline.toString());
        assertEquals("deadline return book /by 2/12/2019 1800", deadline.toStorageString());
    }

    @Test
    void deadline_dateOnly_preservesDateOnlyStorageFormat() throws InvalidCommandException {
        Deadline deadline = new Deadline(new String[] {
            "deadline", "return", "book", "/by", "1/12/2029"
        });

        assertEquals("[D][ ] return book (by: Dec 01 2029)", deadline.toString());
        assertEquals("deadline return book /by 1/12/2029", deadline.toStorageString());
    }

    @Test
    void deadline_invalidDate_throwsException() {
        assertThrows(InvalidCommandException.class, () -> new Deadline(new String[] {
            "deadline", "return", "book", "/by", "not-a-date"
        }));
    }

    @Test
    void event_validDates_formatsForDisplayAndStorage() throws InvalidCommandException {
        Event event = new Event(new String[] {
            "event", "study", "/from", "2/12/2019", "1800", "/to", "2/12/2019", "2000"
        });

        assertEquals("[E][ ] study (from: Dec 02 2019 6:00 pm to: Dec 02 2019 8:00 pm)",
                event.toString());
        assertEquals("event study /from 2/12/2019 1800 /to 2/12/2019 2000",
                event.toStorageString());
    }

    @Test
    void event_endBeforeStart_throwsException() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class,
                () -> new Event(new String[] {
                    "event", "study", "/from", "2/12/2019", "2000", "/to", "2/12/2019", "1800"
                }));

        assertEquals("An event's end must not be before its start.", exception.getMessage());
    }
}
