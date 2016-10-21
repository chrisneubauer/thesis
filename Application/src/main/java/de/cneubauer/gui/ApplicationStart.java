package de.cneubauer.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.net.URL;

/**
 * Created by Christoph Neubauer on 24.09.2016.
 * The main class to start the application
 */
public class ApplicationStart extends Application {
    private Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(this.getClass().getResource("../../../FXML/mainMenu.fxml"));
        Scene scene = new Scene(root, 800,600);
        this.window = primaryStage;

        primaryStage.setTitle("Ferd-Transformator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
        launch(args);
    }
}
