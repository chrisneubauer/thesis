package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root, 800,600);
        //Scene scene = new Scene(new VBox(), 800, 600);
        primaryStage.setTitle("Ferd-Transformator");
       // primaryStage.setScene(scene);

        //MenuBar menuBar = new MenuBar();

       // BorderPane layout = new BorderPane();
       // this.addMenuBar(layout);

       // primaryStage.setScene(layout.getScene());
       // menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

        //((VBox) scene.getRoot()).getChildren().addAll(menuBar);
        //scene.setRoot(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void addMenuBar(BorderPane layout) {
        final MenuBar toolBar = new MenuBar(
                new Menu("File"),
                new Menu("Edit"),
                new Menu("Options")
        );

        layout.setTop(toolBar);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
