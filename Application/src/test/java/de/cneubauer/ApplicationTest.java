package de.cneubauer;

import de.cneubauer.domain.bo.*;
import de.cneubauer.domain.service.DataExtractorService;
import de.cneubauer.domain.service.ZugFerdExtendService;
import de.cneubauer.ml.LearningService;
import de.cneubauer.ml.Model;
import de.cneubauer.ml.ModelWriter;
import de.cneubauer.ocr.ImagePartitioner;
import de.cneubauer.ocr.ImagePreprocessor;
import de.cneubauer.ocr.tesseract.TesseractWrapper;
import de.cneubauer.transformation.ZugFerdTransformator;
import de.cneubauer.ml.ModelReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 04.02.2017.
 * This class holds logic for testing the whole application (except GUI)
 */
public class ApplicationTest extends AbstractTest {
    private ImagePreprocessor preprocessor;
    private ImagePartitioner partitioner;
    private TesseractWrapper wrapper;
    private DataExtractorService dataExtractorService;
    private ZugFerdTransformator transformator;
    private ZugFerdExtendService zugFerdExtendService;
    private ModelReader reader;
    private ModelWriter writer;
    private LearningService learningService;
    private Logger logger;

    @Before
    public void SetUp() {
        this.preprocessor = new ImagePreprocessor("..\\Data\\Datenwerk4.pdf");
        this.wrapper = new TesseractWrapper();
        this.transformator = new ZugFerdTransformator();
        this.reader = new ModelReader();
        this.writer = new ModelWriter();
        this.learningService = new LearningService();
        this.logger = Logger.getLogger(this.getClass());
    }

    @Test
    public void execute() {
        this.logger.log(Level.INFO, "initiating full application test");
        this.logger.log(Level.INFO, "preprocessing image");
        BufferedImage preprocessedImage = this.preprocessor.preprocess();
        this.logger.log(Level.INFO, "partitioning");
        this.partitioner = new ImagePartitioner(preprocessedImage);
        BufferedImage[] parts = this.partitioner.process();

        String[] stringParts = new String[parts.length];

        this.logger.log(Level.INFO, "doing OCR");
        for (int i = 0; i < parts.length; i++) {
            stringParts[i] = this.wrapper.initOcr(parts[i]);
        }

        this.dataExtractorService = new DataExtractorService(stringParts);
        this.logger.log(Level.INFO, "extracting invoice information");
        Invoice extractedInvoiceInformation = this.dataExtractorService.extractInvoiceInformation();

        this.logger.log(Level.INFO, "extracting accounting record information");
        List<Record> extractedAccountingRecordInformation = this.dataExtractorService.extractAccountingRecordInformation();

        this.logger.log(Level.INFO, "creating conformal zugFerd file");
        if (extractedInvoiceInformation.getDebitor() == null) {
            extractedInvoiceInformation.setDebitor(new LegalPerson("FakeDebitor"));
        }
        if (extractedInvoiceInformation.getCreditor() == null) {
            extractedInvoiceInformation.setCreditor(new LegalPerson("FakeCreditor"));
        }
        io.konik.zugferd.Invoice resultingInvoice = this.transformator.createFullConformalBasicInvoice(extractedInvoiceInformation);

        try {
            this.logger.log(Level.INFO, "reading models");
            List<Model> existingModels = this.reader.getModels();

            this.logger.log(Level.INFO, "adding models that are missing");
            for (Record r : extractedAccountingRecordInformation) {
                if (r.getEntryText() != null) {
                    if (this.learningService.exists(r.getEntryText())) {
                        // dunno
                    } else {
                        Model m = new Model();
                        m.setPosition(r.getEntryText());
                        Set<Account> credits = new LinkedHashSet<>();
                        Set<Account> debits = new LinkedHashSet<>();
                        for (AccountRecord ar : r.getRecordAccounts()) {
                            if (ar.getIsDebit()) {
                                debits.add(ar.getAccount());
                            } else {
                                credits.add(ar.getAccount());
                            }
                        }
                        m.setCredit(credits);
                        m.setDebit(debits);
                        this.writer.writeToFile(m);
                    }

                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
