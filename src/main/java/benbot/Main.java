package benbot;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Starts and owns BenBot's JavaFX graphical interface. */
public class Main extends Application {
    /** The chatbot instance shared with the graphical controller. */
    private final BenBot benBot = new BenBot();

    /**
     * Loads the FXML view, connects it to BenBot, and displays the primary window.
     *
     * @param stage the primary JavaFX window.
     * @throws IOException if an FXML or stylesheet resource cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        benBot.load();

        URL view = requireResource("/view/MainWindow.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(view);
        Parent root = fxmlLoader.load();
        MainWindow controller = fxmlLoader.getController();
        controller.setBenBot(benBot);

        Scene scene = new Scene(root, 520, 680);
        scene.getStylesheets().add(requireResource("/styles/main.css").toExternalForm());

        stage.setTitle("BenBot");
        stage.setMinWidth(440);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }

    /** Saves the current task list whenever JavaFX closes the window. */
    @Override
    public void stop() {
        String storageError = benBot.save();
        if (!storageError.isEmpty()) {
            System.err.println(storageError);
        }
    }

    /** Returns a required classpath resource or fails with a helpful message. */
    private URL requireResource(String path) {
        URL resource = Main.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Missing application resource: " + path);
        }
        return resource;
    }
}
