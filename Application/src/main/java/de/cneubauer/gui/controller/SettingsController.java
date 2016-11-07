package de.cneubauer.gui.controller;

import de.cneubauer.database.MySQLConnector;
import de.cneubauer.util.config.ConfigHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by Christoph Neubauer on 04.11.2016.
 * Controller to manage settings selected in the view
 */
public class SettingsController extends GUIController {
    // General settings
    @FXML public ChoiceBox<String> applicationLanguageDropdown;
    @FXML public MenuItem applicationLanguageGerman;
    @FXML public MenuItem applicationLanguageEnglish;
    
    // Scan settings
    @FXML public TextField confidenceIntervalField;
    @FXML public ChoiceBox<String> tesseractLanguageSettingDropDown;
    @FXML public MenuItem tesseractEnglishLanguage;
    @FXML public MenuItem tesseractGermanLanguage;
    @FXML public MenuItem tesseractEnglishAndGerman;
    
    // ZugFerd settings
    @FXML public ChoiceBox<String> defaultFerdProfileDropDown;
    @FXML public MenuItem basicLevel;
    @FXML public MenuItem comfortLevel;
    @FXML public MenuItem extendedLevel;
    
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
        this.databaseNameSettings.setText(ConfigHelper.getValue("databaseName"));
        this.servernameSettings.setText(ConfigHelper.getValue("databaseServerName"));
        this.usernameSettings.setText(ConfigHelper.getValue("databaseUsername"));
        this.passwordSettings.setText(ConfigHelper.getValue("databasePassword"));
        this.portSettings.setText(ConfigHelper.getValue("databasePort"));
        this.confidenceIntervalField.setText(ConfigHelper.getValue("confidenceRate"));

        this.applicationLanguageDropdown.setItems(FXCollections.observableArrayList("English", "German"));
        this.tesseractLanguageSettingDropDown.setItems(FXCollections.observableArrayList("English", "German", "English and German"));
        this.defaultFerdProfileDropDown.setItems(FXCollections.observableArrayList("Basic", "Comfort", "Extended"));

        String appLang = ConfigHelper.getValue("appLang");
        if (Objects.equals(appLang, "ger")) {
            this.applicationLanguageDropdown.setValue("German");
        } else {
            this.applicationLanguageDropdown.setValue("English");
        }

        String tesLang = ConfigHelper.getValue("tesseractLanguage");
        if (Objects.equals(tesLang, "eng")) {
            this.tesseractLanguageSettingDropDown.setValue("English");
        } else if (Objects.equals(tesLang, "deu")) {
            this.tesseractLanguageSettingDropDown.setValue("German");
        } else {
            this.tesseractLanguageSettingDropDown.setValue("English and German");
        }

        String ferdLevel = ConfigHelper.getValue("defaultFerdProfile");
        if (Objects.equals(ferdLevel, "Comfort")) {
            this.tesseractLanguageSettingDropDown.setValue("Comfort");
        } else if (Objects.equals(ferdLevel, "Extended")) {
            this.tesseractLanguageSettingDropDown.setValue("Extended");
        } else {
            this.tesseractLanguageSettingDropDown.setValue("Basic");
        }
    }

    public void applyNewSettings() {
        this.updateSettings();
    }

    public void closeAndSaveSettings() {
        this.updateSettings();
        Stage settingsWindow = (Stage) this.cancelButtonSettings.getScene().getWindow();
        settingsWindow.close();
    }

    public void closeAndIgnoreSettings() {
        Stage settingsWindow = (Stage) this.cancelButtonSettings.getScene().getWindow();
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
        // first adjust language settings
        String selectedLanguage = this.getSelectedApplicationLanguage();
        String currentLanguage = ConfigHelper.getValue("appLang");
        if (!Objects.equals(selectedLanguage, currentLanguage)) {
            this.changeLanguage(selectedLanguage);
        }

        ConfigHelper.addOrUpdate("defaultFerdProfile", this.getDefaultFerdProfileDropDown());
        ConfigHelper.addOrUpdate("tesseractLanguage", this.getTesseractLanguageSettingDropDown());
    }

    // changes languages in the application
    // TODO: make internationalization
    private void changeLanguage(String newLanguage) {

    }

    private String getSelectedApplicationLanguage() {
        if (this.applicationLanguageDropdown.getSelectionModel().getSelectedItem().equals(this.applicationLanguageEnglish.getText())) {
            return "eng";
        }
        else if (this.applicationLanguageDropdown.getSelectionModel().getSelectedItem().equals(this.applicationLanguageGerman.getText())) {
            return "deu";
        }
        else {
            return "eng";
        }
    }

    public String getTesseractLanguageSettingDropDown() {
        if (Objects.equals(this.tesseractLanguageSettingDropDown.getSelectionModel().getSelectedItem(), this.tesseractEnglishLanguage.toString())) {
            return "eng";
        } else if (Objects.equals(this.tesseractLanguageSettingDropDown.getSelectionModel().getSelectedItem(), this.tesseractEnglishLanguage.toString())) {
            return "deu";
        } else {
            return "eng+deu";
        }
    }

    private String getDefaultFerdProfileDropDown() {
        if (this.defaultFerdProfileDropDown.getSelectionModel().getSelectedItem().equals(this.basicLevel.getText())) {
            return "basic";
        }
        else if (this.defaultFerdProfileDropDown.getSelectionModel().getSelectedItem().equals(this.comfortLevel.getText())) {
            return "comfort";
        }
        else if (this.defaultFerdProfileDropDown.getSelectionModel().getSelectedItem().equals(this.extendedLevel.getText())) {
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
}
