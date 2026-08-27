package benbot;

import java.io.IOException;
import java.util.Scanner;

class Ui{

    public static final String DIVIDER = "____________________________________________________________";

    private Scanner scanner;

    public Ui(){
        this.scanner = new Scanner(System.in);
    }

    public void welcome(){
        String banner = " ____              ____        _   \n"
                + "| __ )  ___ _ __  | __ )  ___ | |_ \n"
                + "|  _ \\ / _ \\ '_ \\ |  _ \\ / _ \\| __|\n"
                + "| |_) |  __/ | | || |_) | (_) | |_ \n"
                + "|____/ \\___|_| |_||____/ \\___/ \\__|\n";

        System.out.println(banner);
        System.out.println("Hello! I'm BenBot.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
    }

    public void run(TaskLoader taskLoader, Task[] tasks, int[] taskCount){
        while (this.scanner.hasNextLine()) {
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