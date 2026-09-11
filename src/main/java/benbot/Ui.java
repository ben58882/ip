package benbot;

import java.io.IOException;
import java.util.Scanner;

/** Handles BenBot's command-line interaction with the user. */
class Ui {
    /** The greeting and command summary shown when BenBot starts. */
    private static final String WELCOME_MESSAGE = String.join(System.lineSeparator(),
            "Hello! I'm BenBot.",
            "Here are the commands I understand:",
            "  todo DESCRIPTION",
            "  deadline DESCRIPTION /by DATE [TIME]",
            "  event DESCRIPTION /from START /to END",
            "  list",
            "  find KEYWORD",
            "  mark TASK_NUMBER",
            "  unmark TASK_NUMBER",
            "  delete TASK_NUMBER",
            "  bye",
            "Dates look like 15/9/2026; optional times use 24-hour HHmm, such as 1800.",
            "What would you like to do?");

    /** Reads commands entered through the standard input stream. */
    private final Scanner scanner;

    /** Stores tasks when the user exits the application. */
    private final TaskDataStore taskDataStore;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        this(new Scanner(System.in), new TaskDataStore());
    }

    /**
     * Creates a user interface with the supplied input source and data store.
     *
     * @param scanner reads commands entered by the user.
     * @param taskDataStore stores tasks when the user exits.
     */
    Ui(Scanner scanner, TaskDataStore taskDataStore) {
        this.scanner = scanner;
        this.taskDataStore = taskDataStore;
    }

    /** Displays BenBot's welcome banner and initial prompt. */
    public void welcome() {
        String banner = " ____              ____        _   \n"
                + "| __ )  ___ _ __  | __ )  ___ | |_ \n"
                + "|  _ \\ / _ \\ '_ \\ |  _ \\ / _ \\| __|\n"
                + "| |_) |  __/ | | || |_) | (_) | |_ \n"
                + "|____/ \\___|_| |_||____/ \\___/ \\__|\n";

        System.out.println(banner);
        System.out.println(WELCOME_MESSAGE);
        System.out.println(BenBot.DIVIDER);
    }

    /** Returns the greeting and command summary shared by BenBot's interfaces. */
    static String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Reads and processes commands until input ends or the user enters {@code bye}.
     *
     * @param taskLoader processes the entered commands.
     * @param tasks stores the current tasks.
     * @param taskCount holds the current number of tasks.
     */
    public void run(TaskLoader taskLoader, Task[] tasks, int[] taskCount) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            System.out.println(BenBot.DIVIDER);
            if (taskLoader.addTask(line, tasks, taskCount, true)) {
                String storageError = storeTasks(tasks, taskCount[0]);
                if (!storageError.isEmpty()) {
                    System.out.println(storageError);
                }
                break;
            }
        }
    }

    /**
     * Stores the current tasks and returns an error message if storage fails.
     * Returning the message lets both text and graphical interfaces display it appropriately.
     */
    String storeTasks(Task[] tasks, int taskCount) {
        try {
            taskDataStore.store(tasks, taskCount);
            return "";
        } catch (IOException e) {
            return "ERROR: Unable to store tasks: " + e.getMessage();
        }
    }
}
