package benbot;

/** Provides shared operations for words in parsed user commands. */
final class CommandWords {
    private CommandWords() {
    }

    /** Returns a marker's index, or the number of words when the marker is absent. */
    static int findMarker(String[] words, String marker) {
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(marker)) {
                return i;
            }
        }
        return words.length;
    }
}
