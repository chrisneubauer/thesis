package de.cneubauer.domain.service;

import de.cneubauer.AbstractTest;
import de.cneubauer.domain.bo.*;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.ocr.tesseract.TesseractWrapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resources;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 07.12.2016.
 * Test for DatabaseService class
 */
public class DatabaseServiceTest extends AbstractTest {
    private AccountDao accountDao;
    private DatabaseService service;

    @Before
    public void setUp() throws Exception {
      super.setUp();
        this.service = new DatabaseService();
        databaseChanged = true;
        this.accountDao = new AccountDaoImpl();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void saveProcessResult() throws Exception {
        ExtractionModel model = new ExtractionModel();
        List<Position> recordList = new ArrayList<>();
        recordList.add(this.createRecord());
        File f = new File(getClass().getResource("/data/output/template1_generated0.pdf").toURI());

        Invoice i = this.createInvoice();
        model.setRecords(recordList);
        model.setUpdatedRecords(recordList);
        model.setInvoiceInformation(i);
        model.setUpdatedInvoiceInformation(i);
        model.setHocrDocument(this.createHocrDocument(f));

        ProcessResult mock = new ProcessResult();
        mock.setExtractionModel(model);
        mock.setFile(f);

        this.service.saveProcessResult(mock);
    }

    private HocrDocument createHocrDocument(File f) {
        TesseractWrapper wrapper = new TesseractWrapper();
        String hocr = wrapper.initOcr(f, true);
        return new HocrDocument(hocr);
    }

    private Invoice createInvoice() {
        Invoice i = new Invoice();
        i.setInvoiceNumber("mockupNo123");
        i.setIssueDate(Date.valueOf(LocalDate.now()));
        i.setDeliveryDate(Date.valueOf(LocalDate.now()));

        LegalPerson creditor = new LegalPerson();
        creditor.setName("Kreditor");
        LegalPerson debitor = new LegalPerson();
        debitor.setName("Debitor");

        i.setCreditor(creditor);
        i.setDebitor(debitor);
        i.setLineTotal(100);
        i.setChargeTotal(0);
        i.setAllowanceTotal(0);
        i.setTaxBasisTotal(100);
        i.setTaxTotal(19);
        i.setGrandTotal(119);
        i.setHasSkonto(false);

        return i;
    }

    private Position createRecord() {
        Account darlehen = this.accountDao.getByAccountNo("0550");
        Account bank = this.accountDao.getByAccountNo("0630");

        Position record = new Position();
        record.setEntryText("fakePosition");

        AccountPosition ap = new AccountPosition();
        ap.setAccount(darlehen);
        ap.setBruttoValue(119);

        AccountPosition ap2 = new AccountPosition();
        ap2.setAccount(bank);
        ap2.setIsDebit(true);
        ap2.setBruttoValue(119);

        Set<AccountPosition> set = new HashSet<>();
        set.add(ap);
        set.add(ap2);
        record.setPositionAccounts(set);
        return record;
    }
}