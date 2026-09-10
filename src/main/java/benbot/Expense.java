package benbot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

/** Stores an expense description and a precise monetary amount. */
final class Expense implements StorableEntity {
    /** The syntax accepted when adding an expense. */
    static final String USAGE = "expense DESCRIPTION /amount AMOUNT";

    /** The explanation of what the user paid for. */
    private final String description;

    /** The positive expense amount, represented with two decimal places. */
    private final BigDecimal amount;

    /** Creates an expense with a validated description and amount. */
    Expense(String description, BigDecimal amount) {
        assert description != null && !description.isBlank() : "An expense description must be provided";
        assert amount != null && amount.signum() > 0 : "An expense amount must be positive";
        assert amount.scale() == 2 : "An expense amount must have two decimal places";
        this.description = description;
        this.amount = amount;
    }

    /** Creates an expense from a valid expense command. */
    static Expense createFromCommand(String[] words) throws InvalidCommandException {
        int amountIndex = CommandWords.findMarker(words, "/amount");
        if (amountIndex <= 1 || amountIndex != words.length - 2) {
            throw new InvalidCommandException("Use: " + USAGE);
        }

        String description = String.join(" ", Arrays.copyOfRange(words, 1, amountIndex));
        try {
            BigDecimal amount = new BigDecimal(words[amountIndex + 1])
                    .setScale(2, RoundingMode.UNNECESSARY);
            if (amount.signum() <= 0) {
                throw new InvalidCommandException(
                        "The expense amount must be greater than zero.");
            }
            return new Expense(description, amount);
        } catch (NumberFormatException | ArithmeticException e) {
            throw new InvalidCommandException(
                    "The expense amount must be a number with at most two decimal places.");
        }
    }

    /** Returns this expense's monetary amount. */
    BigDecimal getAmount() {
        return amount;
    }

    /** Returns the expense in BenBot's display format. */
    @Override
    public String toString() {
        return "[$] " + description + " ($" + amount.toPlainString() + ")";
    }

    /** Returns the command used to recreate this expense from stored data. */
    @Override
    public String toStorageString() {
        return "expense " + description + " /amount " + amount.toPlainString();
    }
}
