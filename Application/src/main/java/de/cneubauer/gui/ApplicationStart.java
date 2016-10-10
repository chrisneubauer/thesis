package de.cneubauer.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Created by Christoph Neubauer on 24.09.2016.
 * The main class to start the application
 */
public class ApplicationStart extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(this.getClass().getResource("../../../FXML/mainMenu.fxml"));
        Scene scene = new Scene(root, 800,600);
        primaryStage.setTitle("Ferd-Transformator");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
