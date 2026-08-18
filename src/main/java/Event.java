import java.util.Arrays;

/** A task that has a starting time and an ending time. */
public class Event extends Task {
    /** The start time text supplied by the user. */
    private final String from;

    /** The end time text supplied by the user. */
    private final String to;

    /**
     * Creates an event from words in the form
     * {@code event DESCRIPTION /from START /to END}. Times are kept as text.
     *
     * @param words the words entered in the command
    */
    public Event(String[] words) {
        super(getDescription(words), TaskType.EVENT);
        this.from = getFrom(words);
        this.to = getTo(words);
    }

    /** Returns the event description before the {@code /from} marker. */
    private static String getDescription(String[] words) {
        int fromIndex = findMarker(words, "/from");
        return String.join(" ", Arrays.copyOfRange(words, 1, fromIndex));
    }

    /** Returns the start-time text between the {@code /from} and {@code /to} markers. */
    private static String getFrom(String[] words) {
        int fromIndex = findMarker(words, "/from");
        int toIndex = findMarker(words, "/to");
        return String.join(" ", Arrays.copyOfRange(words, fromIndex + 1, toIndex));
    }

    /** Returns the end-time text after the {@code /to} marker. */
    private static String getTo(String[] words) {
        int toIndex = findMarker(words, "/to");
        return String.join(" ", Arrays.copyOfRange(words, toIndex + 1, words.length));
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

    /** Returns the event in the chatbot's display format. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
