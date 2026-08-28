package benbot;

import java.io.IOException;
import java.util.Scanner;

/** Handles BenBot's command-line interaction with the user. */
class Ui {

    /** Reads commands entered through the standard input stream. */
    private final Scanner scanner;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays BenBot's welcome banner and initial prompt. */
    public void welcome() {
        String banner = " ____              ____        _   \n"
                + "| __ )  ___ _ __  | __ )  ___ | |_ \n"
                + "|  _ \\ / _ \\ '_ \\ |  _ \\ / _ \\| __|\n"
                + "| |_) |  __/ | | || |_) | (_) | |_ \n"
                + "|____/ \\___|_| |_||____/ \\___/ \\__|\n";

        System.out.println(banner);
        System.out.println("Hello! I'm BenBot.");
        System.out.println("What can I do for you?");
        System.out.println(BenBot.DIVIDER);
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
                try {
                    new TaskDataStore().store(tasks, taskCount[0]);
                } catch (IOException e) {
                    System.out.println("ERROR: Unable to store tasks: " + e.getMessage());
                }
                break;
            }
        }
    }
}
