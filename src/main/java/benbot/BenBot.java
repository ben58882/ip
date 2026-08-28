package benbot;

/** The entry point for the BenBot chatbot application. */
public class BenBot {
    /** The maximum number of tasks that BenBot can store in memory. */
    private static final int MAX_TASKS = 100;

    /** Separates sections of BenBot's command-line output. */
    public static final String DIVIDER =
            "____________________________________________________________";

    /** The tasks currently managed by the application. */
    private final Task[] tasks;

    /** A mutable holder for the number of occupied entries in {@link #tasks}. */
    private final int[] taskCount;

    /** Processes commands that update the task list. */
    private final TaskLoader taskLoader;

    /** Loads task commands saved during a previous run. */
    private final StoredTaskLoader storedTaskLoader;

    /** Handles BenBot's command-line input and output. */
    private final Ui ui;

    /** Creates a BenBot application with an empty task list. */
    public BenBot() {
        tasks = new Task[MAX_TASKS];
        taskCount = new int[] {0};
        taskLoader = new TaskLoader(MAX_TASKS);
        storedTaskLoader = new StoredTaskLoader();
        ui = new Ui();
    }

    /** Loads tasks stored by a previous run of the application. */
    public void load() {
        storedTaskLoader.load(taskLoader, tasks, taskCount);
    }

    /** Starts the command-line interaction loop. */
    public void run() {
        ui.run(taskLoader, tasks, taskCount);
    }

    /**
     * Starts BenBot and displays its initial greeting.
     *
     * @param args command-line arguments, which this application does not use.
     */
    public static void main(String[] args) {
        BenBot benBot = new BenBot();
        benBot.ui.welcome();
        benBot.load();
        benBot.run();
    }
}
