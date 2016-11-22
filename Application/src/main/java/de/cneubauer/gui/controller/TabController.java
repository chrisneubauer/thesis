package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Invoice;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * Created by Christoph Neubauer on 22.11.2016.
 */
public class TabController extends SplitPaneController {

    @FXML public VBox invoiceTab;
    @FXML public VBox accountingRecordsTab;
    @FXML private ResultsController invoiceTabController;

    public void initResults(Invoice extractedInformation, String text) {
        invoiceTabController.initData(extractedInformation, text);
    }
}
