import java.util.Arrays;

/** A task that must be completed by a specified date or time. */
public class Deadline extends Task {
    /** The deadline text supplied by the user. */
    private final String by;

    /**
     * Creates a deadline from words in the form {@code deadline DESCRIPTION /by DATE}.
     * The date is kept as text rather than converted to a date object.
     *
     * @param words the words entered in the command
     */
    public Deadline(String[] words) {
        super(getDescription(words));
        this.by = getBy(words);
    }

    /** Returns the task description before the {@code /by} marker. */
    private static String getDescription(String[] words) {
        int byIndex = findMarker(words, "/by");
        return String.join(" ", Arrays.copyOfRange(words, 1, byIndex));
    }

    /** Returns the deadline text after the {@code /by} marker. */
    private static String getBy(String[] words) {
        int byIndex = findMarker(words, "/by");
        return String.join(" ", Arrays.copyOfRange(words, byIndex + 1, words.length));
    }

    /** Finds the location of a formatting marker in a command. */
    private static int findMarker(String[] words, String marker) {
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(marker)) {
                return i;
            }
        }
        return words.length;
    }

    /** Returns the deadline in the chatbot's display format. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
