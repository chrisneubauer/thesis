package de.cneubauer.gui.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

/**
 * Created by Christoph Neubauer on 04.11.2016.
 * Controller to manage settings selected in the view
 */
public class SettingsController extends GUIController {
    // General settings
    public ChoiceBox applicationLanguageDropdown;
    public MenuItem applicationLanguageGerman;
    public MenuItem applicationLanguageEnglish;
    
    // Scan settings
    public TextField confidenceIntervalField;
    public ChoiceBox tesseractLanguageSettingDropDown;
    public MenuItem TesseractEnglishLanguage;
    public MenuItem TesseractGermanLanguage;
    public MenuItem TesseractEnglishAndGerman;
    
    // ZugFerd settings
    public ChoiceBox DefaultFerdProfileDropDown;
    public MenuItem BasicLevel;
    public MenuItem ComfortLevel;
    public MenuItem ExtendedLevel;
    
    // Database settings
    public TextField ServernameSettings;
    public TextField DatabaseNameSettings;
    public TextField UsernameSettings;
    public TextField PasswordSettings;
    public TextField PortSettings;
    public Button testDatabaseConnectionButton;
    
    // general buttons
    public Button okButtonSettings;
    public Button cancelButtonSettings;
    public Button applyButtonSettings;

    public void applyNewSettings(ActionEvent actionEvent) {

    }

    public void closeAndSaveSettings(ActionEvent actionEvent) {

    }

    public void closeAndIgnoreSettings(ActionEvent actionEvent) {

    }

    public void testDatabaseConnection(ActionEvent actionEvent) {

    }
}
