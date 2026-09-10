package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests expense parsing, validation, and formatting. */
class ExpenseTest {
    @Test
    void createFromCommand_validAmount_createsExpense() throws InvalidCommandException {
        Expense expense = Expense.createFromCommand(
                new String[] {"expense", "bus", "fare", "/amount", "2.5"});

        assertEquals("[$] bus fare ($2.50)", expense.toString());
        assertEquals("expense bus fare /amount 2.50", expense.toStorageString());
    }

    @Test
    void createFromCommand_invalidAmounts_throwException() {
        String[] invalidAmounts = {"abc", "0", "-1", "1.234"};

        for (String amount : invalidAmounts) {
            assertThrows(InvalidCommandException.class, () ->
                    Expense.createFromCommand(
                            new String[] {"expense", "lunch", "/amount", amount}));
        }
    }

    @Test
    void createFromCommand_missingDescription_throwsException() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                Expense.createFromCommand(new String[] {"expense", "/amount", "4.50"}));

        assertEquals("Use: " + Expense.USAGE, exception.getMessage());
    }
}
