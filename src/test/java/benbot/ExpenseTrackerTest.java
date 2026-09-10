package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/** Tests expense collection operations and total calculation. */
class ExpenseTrackerTest {
    @Test
    void addAndRemove_expenses_updatesTotal() throws InvalidCommandException {
        ExpenseTracker tracker = new ExpenseTracker();
        tracker.add(Expense.createFromCommand(
                new String[] {"expense", "lunch", "/amount", "12.50"}));
        tracker.add(Expense.createFromCommand(
                new String[] {"expense", "bus", "/amount", "2.40"}));

        assertEquals(new BigDecimal("14.90"), tracker.getTotalAmount());

        tracker.remove(0);

        assertEquals(new BigDecimal("2.40"), tracker.getTotalAmount());
        assertEquals("1.[$] bus ($2.40)", tracker.toNumberedDisplayLines()[0]);
    }
}
