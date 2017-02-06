package de.cneubauer.gui;

import de.cneubauer.gui.controller.GUIController;
import de.cneubauer.util.config.Cfg;
import de.cneubauer.util.config.ConfigHelper;
import de.cneubauer.util.enumeration.AppLang;
import de.cneubauer.util.enumeration.TessLang;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 24.09.2016.
 * The main class to start the new application
 */
public class Start extends Application {
    private static HostServices hostServices;

    private static void setHostServicesInternal(HostServices s){
        hostServices = s;
    }

    public static HostServices getHostServicesInternal() {
        return hostServices;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL fxmlURL = this.getClass().getClassLoader().getResource("FXML/startMenu.fxml");

        Locale locale;
        AppLang currentLanguage = ConfigHelper.getApplicationLanguage();
        if (currentLanguage.equals(AppLang.GERMAN)) {
            locale = Locale.GERMANY;
        } else {
            locale = Locale.ENGLISH;
        }

        ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);

        FXMLLoader loader = new FXMLLoader(fxmlURL, bundle);
        Parent root = loader.load();

        Scene scene = new Scene(root, 600,200);
        Logger.getLogger(this.getClass()).log(Level.INFO, "loading css files");
        scene.getStylesheets().add(String.valueOf(getClass().getResource("../../../css/validationError.css")));

        setHostServicesInternal(this.getHostServices());

        primaryStage.setTitle("Ferd-Transformator");
        primaryStage.setScene(scene);

        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure("src/main/log4j.properties");
        initSettings();
        Logger.getRootLogger().setLevel(Level.DEBUG);
        launch(args);
    }

    private static void initSettings() {
        Map<String, String> config = ConfigHelper.getConfig();
        config.putIfAbsent(Cfg.CONFIDENCERATE.getValue(), "0.2");
        config.putIfAbsent(Cfg.DBNAME.getValue(), "ferd_transformator");
        config.putIfAbsent(Cfg.DBUSER.getValue(), "root");
        config.putIfAbsent(Cfg.DBPASSWORD.getValue(), "toor");
        config.putIfAbsent(Cfg.DBSERVER.getValue(), "localhost");
        config.putIfAbsent(Cfg.DBPORT.getValue(), "3306");
        config.putIfAbsent(Cfg.TESSERACTLANGUAGE.getValue(), TessLang.ENGLISHANDGERMAN.getValue());
        config.putIfAbsent(Cfg.APPLICATIONLANGUAGE.getValue(), AppLang.ENGLISH.name());
        ConfigHelper.rewrite(config);
    }
}
