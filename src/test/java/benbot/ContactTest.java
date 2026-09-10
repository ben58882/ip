package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests contact parsing and formatting. */
class ContactTest {
    @Test
    void createFromCommand_validDetails_createsContact() throws InvalidCommandException {
        Contact contact = Contact.createFromCommand(new String[] {
            "contact", "Alice", "Tan", "/phone", "91234567", "/email", "alice@example.com"
        });

        assertEquals("[C] Alice Tan (phone: 91234567; email: alice@example.com)",
                contact.toString());
        assertEquals("contact Alice Tan /phone 91234567 /email alice@example.com",
                contact.toStorageString());
    }

    @Test
    void createFromCommand_missingEmail_throwsException() {
        String[] words = {"contact", "Alice", "/phone", "91234567"};

        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                Contact.createFromCommand(words));

        assertEquals("Use: " + Contact.USAGE, exception.getMessage());
    }
}
