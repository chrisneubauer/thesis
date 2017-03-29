package de.cneubauer;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.bo.Position;
import de.cneubauer.domain.service.AccountingRecordExtractorService;
import de.cneubauer.domain.service.DataExtractorService;
import de.cneubauer.domain.service.InvoiceExtractorService;
import de.cneubauer.ml.nlp.NLPFacade;
import de.cneubauer.ml.nlp.NLPModel;
import de.cneubauer.ocr.ImagePartitioner;
import de.cneubauer.ocr.ImagePreprocessor;
import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.ocr.tesseract.TesseractWrapper;
import de.cneubauer.transformation.ZugFerdTransformator;
import io.konik.validation.InvoiceValidator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.validation.ConstraintViolation;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 04.02.2017.
 * This class holds logic for testing the whole application (except GUI)
 */
public class ApplicationTest extends AbstractTest {
    private ImagePreprocessor preprocessor;
    private TesseractWrapper wrapper;
    private ZugFerdTransformator transformator;
    private NLPFacade facade;
    private Logger logger;

    @Before
    public void SetUp() throws IOException {
        InputStream file = this.getClass().getResourceAsStream("/data/output/template1_generated0.pdf");
        PDDocument pdf = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(pdf);

        BufferedImage pdfImage  = renderer.renderImageWithDPI(0, 300);
        this.preprocessor = new ImagePreprocessor(pdfImage);
        this.wrapper = new TesseractWrapper();
        this.transformator = new ZugFerdTransformator();
        this.facade = new NLPFacade();
        this.logger = Logger.getLogger(this.getClass());
    }

    @Test
    public void execute() {
        this.logger.log(Level.INFO, "initiating full application test");
        this.logger.log(Level.INFO, "preprocessing image");
        BufferedImage preprocessedImage = this.preprocessor.preprocess();

        this.logger.log(Level.INFO, "partitioning");
        ImagePartitioner partitioner = new ImagePartitioner(preprocessedImage);
        BufferedImage[] parts = partitioner.process();

        String[] stringParts = new String[parts.length];

        this.logger.log(Level.INFO, "doing OCR");
        for (int i = 0; i < parts.length -1; i++) {
            stringParts[i] = this.wrapper.initOcr(parts[i], false);
        }
        stringParts[3] = this.wrapper.initOcr(preprocessedImage, true);
        HocrDocument doc = new HocrDocument(stringParts[3]);

        DataExtractorService invoiceExtractor = new InvoiceExtractorService(doc, stringParts);
        DataExtractorService accountingRecordExtractor = new AccountingRecordExtractorService(doc, stringParts);

        Thread invoiceThread = new Thread(invoiceExtractor);
        Thread accountingRecordThread = new Thread(accountingRecordExtractor);

        invoiceThread.start();
        accountingRecordThread.start();

        boolean invoiceFinished = false;
        boolean accountingRecordFinished = false;

        Invoice extractedInvoiceInformation = null;
        List<Position> extractedAccountingRecordInformation = null;

        while (!invoiceFinished || !accountingRecordFinished) {
            if (!invoiceFinished && invoiceThread.getState().equals(Thread.State.TERMINATED)) {
                this.logger.log(Level.INFO, "getting extracted invoice information");
                extractedInvoiceInformation = invoiceExtractor.getThreadInvoice();
                invoiceFinished = true;
            }
            if (!accountingRecordFinished && accountingRecordThread.getState().equals(Thread.State.TERMINATED)) {
                this.logger.log(Level.INFO, "getting extracted accounting record information");
                extractedAccountingRecordInformation = accountingRecordExtractor.getThreadRecord();
                accountingRecordFinished = true;
            }
        }

        this.logger.log(Level.INFO, "creating conformal zugFerd file");
        if (extractedInvoiceInformation != null && extractedInvoiceInformation.getDebitor() == null) {
            extractedInvoiceInformation.setDebitor(new LegalPerson("FakeDebitor"));
        }
        if (extractedInvoiceInformation != null && extractedInvoiceInformation.getCreditor() == null) {
            extractedInvoiceInformation.setCreditor(new LegalPerson("FakeCreditor"));
        }

        io.konik.zugferd.Invoice resultingInvoice = this.transformator.createFullConformalBasicInvoice(extractedInvoiceInformation);

        this.logger.log(Level.INFO, "adding models that are missing");
        for (Position r : extractedAccountingRecordInformation) {
            if (r.getEntryText() != null) {
                NLPModel model = this.facade.getMostLikelyModel(r.getEntryText());
                if (model != null) {
                    Logger.getLogger(this.getClass()).log(Level.INFO, "Model found with probability of " + model.getProbability());
                } else {
                    this.facade.writeModel(r);
                }
            }
        }
        InvoiceValidator validator = new InvoiceValidator();
        Set<ConstraintViolation<io.konik.zugferd.Invoice>> violations = validator.validate(resultingInvoice);
        Assert.isTrue(violations.size() == 0);
    }
}
