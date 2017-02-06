package de.cneubauer.gui.controller;

import de.cneubauer.database.MySQLConnector;
import de.cneubauer.util.config.Cfg;
import de.cneubauer.util.config.ConfigHelper;
import de.cneubauer.util.enumeration.AppLang;
import de.cneubauer.util.enumeration.FerdLevel;
import de.cneubauer.util.enumeration.TessLang;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 04.11.2016.
 * Controller to manage settings selected in the view
 */
public class SettingsController extends GUIController {
    @FXML FlowPane root;
    private Stage primaryStage;

    // General settings
    @FXML public ChoiceBox<AppLang> applicationLanguageDropdown;
    
    // Scan settings
    @FXML public TextField confidenceIntervalField;
    @FXML public ChoiceBox<TessLang> tesseractLanguageSettingDropDown;
    
    // ZugFerd settings
    @FXML public ChoiceBox<FerdLevel> defaultFerdProfileDropDown;
    
    // Database settings
    @FXML public TextField servernameSettings;
    @FXML public TextField databaseNameSettings;
    @FXML public TextField usernameSettings;
    @FXML public TextField passwordSettings;
    @FXML public TextField portSettings;
    @FXML public Button testDatabaseConnectionButton;
    
    // general buttons
    @FXML public Button okButtonSettings;
    @FXML public Button cancelButtonSettings;
    @FXML public Button applyButtonSettings;

    // load settings from configuration file
    @FXML
    private void initialize() {
        this.databaseNameSettings.setText(ConfigHelper.getDBName());
        this.servernameSettings.setText(ConfigHelper.getDBServerName());
        this.usernameSettings.setText(ConfigHelper.getDBUserName());
        this.passwordSettings.setText(ConfigHelper.getDBPassword());
        this.portSettings.setText(String.valueOf(ConfigHelper.getDBPort()));
        this.confidenceIntervalField.setText(String.valueOf(ConfigHelper.getConfidenceRate()));

        this.applicationLanguageDropdown.setItems(FXCollections.observableArrayList(AppLang.ENGLISH, AppLang.GERMAN));
        this.tesseractLanguageSettingDropDown.setItems(FXCollections.observableArrayList(TessLang.ENGLISH, TessLang.GERMAN, TessLang.ENGLISHANDGERMAN));
        this.defaultFerdProfileDropDown.setItems(FXCollections.observableArrayList(FerdLevel.BASIC, FerdLevel.COMFORT, FerdLevel.EXTENDED));

        // populating dropdowns with values from config
        this.applicationLanguageDropdown.setValue(ConfigHelper.getApplicationLanguage());
        this.tesseractLanguageSettingDropDown.setValue(ConfigHelper.getTesseractLanguages());
        this.defaultFerdProfileDropDown.setValue(ConfigHelper.getPreferredFerdLevel());
    }

    public void applyNewSettings() {
        this.updateSettings();
    }

    public void closeAndSaveSettings() {
        this.updateSettings();
        Stage settingsWindow = (Stage) root.getScene().getWindow();
        //Stage settingsWindow = (Stage) this.cancelButtonSettings.getScene().getWindow();
        settingsWindow.close();
    }

    public void closeAndIgnoreSettings() {
        //Stage settingsWindow = (Stage) this.cancelButtonSettings.getScene().getWindow();
        Stage settingsWindow = (Stage) root.getScene().getWindow();
        settingsWindow.close();
    }

    public void testDatabaseConnection() {
        boolean valid = this.validateDatabaseFields();
        if (valid) {
            boolean dbConIsWorking = this.connectToDatabase();
            if (dbConIsWorking) {
                // output popup it's working
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Connected");
                alert.setContentText("A database connection has been established successfully!");
                alert.showAndWait();
            } else {
                // output popup it's not working
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Connection problem");
                alert.setContentText("Could not connect to database! Please check your values.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid fields");
            alert.setContentText("Some values are empty or incorrect. Port should only contain numbers! Please check your values.");
            alert.showAndWait();
        }
    }

    // tries to connect to db with the given values
    // returns true if connected, false if otherwise
    private boolean connectToDatabase() {
        String srv = this.getServernameSettings();
        String db = this.getDatabaseNameSettings();
        String usr = this.getUsernameSettings();
        String pw = this.getPasswordSettings();
        int port = this.getPortSettings();
        MySQLConnector connector = new MySQLConnector(srv, db, usr, pw, port);
        Connection con = connector.connect();
        if (con != null) {
            try {
                con.close();
            return true;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    // validates that all fields contain values
    private boolean validateDatabaseFields() {
        boolean valid = true;
        if (this.getServernameSettings() == null) {
            this.invalidateField(this.servernameSettings);
            valid = false;
        }
        if (this.getDatabaseNameSettings() == null) {
            this.invalidateField(this.databaseNameSettings);
            valid = false;
        }
        if (this.getUsernameSettings() == null) {
            this.invalidateField(this.usernameSettings);
            valid = false;
        }
        if (this.getPasswordSettings() == null) {
            this.invalidateField(this.passwordSettings);
            valid = false;
        }
        if (valid) {
            String port = this.portSettings.getText();
            if (port != null) {
                try {
                    int prt = Integer.parseInt(port);
                    if (prt > 0) {
                        return true;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass()).log(Level.INFO, "Unable to parse port value. Only numbers are allowed!");
                    this.invalidateField(this.portSettings);
                    return false;
                }
            }
        }
        return false;
    }

    // invalidates a field
    private void invalidateField(TextField textField) {
        textField.getStyleClass().add("error");
    }

    // updates all setting values in config.ini
    private void updateSettings() {
        ConfigHelper.addOrUpdate(Cfg.FERDPROFILE.getValue(), this.getDefaultFerdProfileDropDown());
        ConfigHelper.addOrUpdate(Cfg.TESSERACTLANGUAGE.getValue(), this.getTesseractLanguageSettingDropDown());

        AppLang selectedLanguage = this.getSelectedApplicationLanguage();
        AppLang currentLanguage = ConfigHelper.getApplicationLanguage();

        Logger.getLogger(this.getClass()).log(Level.INFO, "Comparing languages: " + selectedLanguage + " and " + currentLanguage);
        if (!Objects.equals(selectedLanguage, currentLanguage)) {
            this.changeLanguage(selectedLanguage);
        }

    }

    // changes languages in the application
    private void changeLanguage(AppLang newLanguage) {
            Locale locale;
            if (newLanguage.equals(AppLang.GERMAN)) {
                locale = Locale.GERMANY;
                ConfigHelper.addOrUpdate(Cfg.APPLICATIONLANGUAGE.getValue(), AppLang.GERMAN.name());
            } else {
                locale = Locale.ENGLISH;
                ConfigHelper.addOrUpdate(Cfg.APPLICATIONLANGUAGE.getValue(), AppLang.ENGLISH.name());
            }

            Logger.getLogger(this.getClass()).log(Level.INFO, "trying to apply new language:" + locale.toLanguageTag());
            Scene oldScene = this.primaryStage.getScene();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);
            URL fxmlURL = this.getClass().getClassLoader().getResource("FXML/main.fxml");
            try {
                if (fxmlURL != null) {
                    oldScene.setRoot(FXMLLoader.load(fxmlURL, bundle));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private AppLang getSelectedApplicationLanguage() {
       return this.applicationLanguageDropdown.getSelectionModel().getSelectedItem();
    }

    private String getTesseractLanguageSettingDropDown() {
        if (this.tesseractLanguageSettingDropDown.getSelectionModel().getSelectedItem() != null) {
            if (this.tesseractLanguageSettingDropDown.getSelectionModel().getSelectedItem().equals(TessLang.ENGLISH)) {
                return "eng";
            } else if (this.tesseractLanguageSettingDropDown.getSelectionModel().getSelectedItem().equals(TessLang.GERMAN)) {
                return "deu";
            } else {
                return "eng+deu";
            }
        } else {
            return "eng+deu";
        }
    }

    private String getDefaultFerdProfileDropDown() {
        if (this.defaultFerdProfileDropDown.getSelectionModel().getSelectedItem().equals(FerdLevel.BASIC)) {
            return "basic";
        }
        else if (this.defaultFerdProfileDropDown.getSelectionModel().getSelectedItem().equals(FerdLevel.COMFORT)) {
            return "comfort";
        }
        else if (this.defaultFerdProfileDropDown.getSelectionModel().getSelectedItem().equals(FerdLevel.BASIC)) {
            return "extended";
        }
        else {
            return "basic";
        }
    }

    private String getServernameSettings() {
        return this.servernameSettings.getText();
    }

    private String getDatabaseNameSettings() {
        return this.databaseNameSettings.getText();
    }

    private String getUsernameSettings() {
        return this.usernameSettings.getText();
    }

    private String getPasswordSettings() {
        return this.passwordSettings.getText();
    }

    private int getPortSettings() {
        return Integer.parseInt(this.portSettings.getText());
    }

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
