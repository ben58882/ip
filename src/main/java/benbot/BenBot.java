package benbot;

/** The entry point for the BenBot chatbot application. */
public class BenBot {
    /** Separates sections of BenBot's command-line output. */
    public static final String DIVIDER =
            "____________________________________________________________";

    /** The maximum number of tasks that BenBot can store in memory. */
    private static final int MAX_TASKS = 100;

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

    /** Whether the most recently processed command asked the application to exit. */
    private boolean exitRequested;

    /** Creates a BenBot application with an empty task list. */
    public BenBot() {
        this(new Task[MAX_TASKS], new int[] {0}, new TaskLoader(MAX_TASKS),
                new StoredTaskLoader(), new Ui());
    }

    /**
     * Creates a BenBot application from its collaborating components.
     *
     * @param tasks the task array managed by the application.
     * @param taskCount holds the number of tasks in {@code tasks}.
     * @param taskLoader processes task commands.
     * @param storedTaskLoader loads previously stored task commands.
     * @param ui handles user interaction.
     */
    BenBot(Task[] tasks, int[] taskCount, TaskLoader taskLoader,
           StoredTaskLoader storedTaskLoader, Ui ui) {
        this.tasks = tasks;
        this.taskCount = taskCount;
        this.taskLoader = taskLoader;
        this.storedTaskLoader = storedTaskLoader;
        this.ui = ui;
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
     * Processes one command and returns BenBot's reply for a graphical interface to display.
     *
     * @param input the command entered by the user.
     * @return BenBot's response to the command.
     */
    public String getResponse(String input) {
        exitRequested = taskLoader.addTask(input.trim(), tasks, taskCount, false);
        String response = taskLoader.getLastResponse();
        if (exitRequested) {
            String storageError = save();
            if (!storageError.isEmpty()) {
                response += System.lineSeparator() + storageError;
            }
        }
        return response;
    }

    /** Returns whether the most recently processed command asked BenBot to exit. */
    public boolean isExitRequested() {
        return exitRequested;
    }

    /**
     * Saves the current tasks when a graphical window closes.
     *
     * @return an empty string on success, or a user-facing error message on failure.
     */
    public String save() {
        return ui.storeTasks(tasks, taskCount[0]);
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
