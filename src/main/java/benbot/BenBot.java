package benbot;

import java.io.IOException;
import java.util.Scanner;

/** The entry point for the BenBot chatbot application. */
public class BenBot {
    private Task[] tasks;
    private int[] taskCount;
    private TaskLoader taskLoader;
    private LoadStoredData storedDataLoader;
    private Ui ui;
    private static final int MAX_TASKS = 100;
    public static final String DIVIDER = "____________________________________________________________";

    public BenBot() {
        this.tasks = new Task[MAX_TASKS];
        this.taskCount = new int[]{0};
        this.taskLoader = new TaskLoader(MAX_TASKS);
        this.storedDataLoader = new LoadStoredData();
        this.ui = new Ui();
    }

    public void load(){
        this.storedDataLoader.load(taskLoader, tasks, taskCount);
    }

    public void run() {
        this.ui.run(this.taskLoader, this.tasks, this.taskCount);
    }

    public static void main(String[] args) {
        BenBot benbot = new BenBot();
        benbot.ui.welcome();
        benbot.load();
        benbot.run();
    }
}