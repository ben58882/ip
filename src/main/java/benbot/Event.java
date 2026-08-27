package benbot;

import java.time.LocalDateTime;
import java.util.Arrays;

/** A task that has a starting time and an ending time. */
public class Event extends Task {
    /** The event's start date and optional time. */
    private final LocalDateTime from;

    /** Whether the start date included a time. */
    private final boolean fromIncludesTime;

    /** The event's end date and optional time. */
    private final LocalDateTime to;

    /** Whether the end date included a time. */
    private final boolean toIncludesTime;

    /**
     * Creates an event from words in the form
     * {@code event DESCRIPTION /from START /to END}. Dates are stored as date-time values.
     *
     * @param words the words entered in the command.
     */
    public Event(String[] words) throws InvalidCommandException {
        super(getDescription(words), TaskType.EVENT);
        DateTimeParser.ParsedDateTime parsedFrom = DateTimeParser.parse(getFrom(words));
        DateTimeParser.ParsedDateTime parsedTo = DateTimeParser.parse(getTo(words));
        this.from = parsedFrom.value();
        this.fromIncludesTime = parsedFrom.includesTime();
        this.to = parsedTo.value();
        this.toIncludesTime = parsedTo.includesTime();
        if (to.isBefore(from)) {
            throw new InvalidCommandException("An event's end must not be before its start.");
        }
    }

    /** Returns the event description before the {@code /from} marker. */
    private static String getDescription(String[] words) {
        int fromIndex = findMarker(words, "/from");
        return String.join(" ", Arrays.copyOfRange(words, 1, fromIndex));
    }

    /** Returns the start-date text between the {@code /from} and {@code /to} markers. */
    private static String getFrom(String[] words) {
        int fromIndex = findMarker(words, "/from");
        int toIndex = findMarker(words, "/to");
        return String.join(" ", Arrays.copyOfRange(words, fromIndex + 1, toIndex));
    }

    /** Returns the end-date text after the {@code /to} marker. */
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
        return super.toString() + " (from: " + DateTimeParser.format(from, fromIncludesTime)
                + " to: " + DateTimeParser.format(to, toIncludesTime) + ")";
    }

    /** Returns the command used to recreate this event from stored data. */
    @Override
    public String toStorageString() {
        return "event " + getDescription() + " /from "
                + DateTimeParser.formatForStorage(from, fromIncludesTime) + " /to "
                + DateTimeParser.formatForStorage(to, toIncludesTime);
    }
}
