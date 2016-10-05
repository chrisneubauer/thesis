package de.cneubauer.gui.Controller;

import de.cneubauer.ocr.TesseractWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by Christoph Neubauer on 04.10.2016.
 * Provides controls for performing OCR in the UI
 */
public class OCRController extends GUIController {
    @FXML
    private TextField fileInput;

    @FXML
    protected void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            this.fileInput.setText(file.getPath());
        }
    }

    @FXML
    protected void scanFile() {
        boolean valid = this.validateFileInput();
        if (valid) {
            File fileToScan = new File(this.fileInput.getText());
            TesseractWrapper wrapper = new TesseractWrapper();
            String result = wrapper.initOcr(fileToScan.getPath());
            System.out.println(result);
        }
    }

    private boolean validateFileInput() {
        return this.fileInput.getText() != null;
    }
}
