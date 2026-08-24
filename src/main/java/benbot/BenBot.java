package benbot;

import java.io.IOException;
import java.util.Scanner;

/** The entry point for the BenBot chatbot application. */
public class BenBot {
    /** The maximum number of tasks that BenBot can keep during one run. */
    private static final int MAX_TASKS = 100;

    /** A line used to separate BenBot's messages in the terminal. */
    public static final String DIVIDER = "____________________________________________________________";

    /**
     * Starts BenBot, stores entered tasks in memory, and exits when the user enters
     * {@code bye}.
     */
    public static void main(String[] args) {
        String banner = " ____              ____        _   \n"
                + "| __ )  ___ _ __  | __ )  ___ | |_ \n"
                + "|  _ \\ / _ \\ '_ \\ |  _ \\ / _ \\| __|\n"
                + "| |_) |  __/ | | || |_) | (_) | |_ \n"
                + "|____/ \\___|_| |_||____/ \\___/ \\__|\n";

        System.out.println(banner);
        System.out.println("Hello! I'm BenBot.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);

        Task[] tasks = new Task[MAX_TASKS];
        int[] taskCount = {0};
        TaskLoader taskLoader = new TaskLoader(MAX_TASKS);
        LoadStoredData storedDataLoader = new LoadStoredData();
        storedDataLoader.load(taskLoader, tasks, taskCount);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            System.out.println(DIVIDER);
            if (taskLoader.addTask(line, tasks, taskCount, true)) {
                try {
                    new StoreData().store(tasks, taskCount[0]);
                } catch (IOException e) {
                    System.out.println("ERROR: Unable to store tasks: " + e.getMessage());
                }
                break;
            }
        }
    }
}
