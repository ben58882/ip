import java.util.Scanner;

/** The entry point for the BenBot chatbot application. */
public class BenBot {
    /** The maximum number of tasks that BenBot can keep during one run. */
    private static final int MAX_TASKS = 100;

    /** A line used to separate BenBot's messages in the terminal. */
    private static final String DIVIDER = "____________________________________________________________";

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
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(DIVIDER);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            }

            else if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            }
            else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                Task task = tasks[taskNumber - 1];
                task.markDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task);
            }
            else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                Task task = tasks[taskNumber - 1];
                task.markUndone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + task);
            }
            else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println(" added: " + command);
            }

            System.out.println(DIVIDER);
        }
    }
}
