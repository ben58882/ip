import java.util.Scanner;

/** The entry point for the BenBot chatbot application. */
public class BenBot {
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Starts BenBot, echoes each command entered by the user, and exits when the
     * user enters {@code bye}.
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

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(DIVIDER);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }

            System.out.println(" " + command);
            System.out.println(DIVIDER);
        }
    }
}
