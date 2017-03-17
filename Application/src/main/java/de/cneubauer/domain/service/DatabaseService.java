package de.cneubauer.domain.service;

import com.google.common.io.Files;
import de.cneubauer.domain.bo.*;
import de.cneubauer.domain.dao.*;
import de.cneubauer.domain.dao.impl.*;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.transformation.ZugFerdTransformator;
import de.cneubauer.util.DocumentCaseSet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Christoph Neubauer on 02.12.2016.
 * Service for saving revised documents to the database
 */
public class DatabaseService {
    private List<Keyword> keywordList;
    private List<Creditor> creditorList;
    private CreditorDao creditorDao;
    private DocumentCaseDao caseDao;

    /**
     * Saves the process result in the database completely
     * @param result  the process result that should be saved
     */
    public void saveProcessResult(ProcessResult result) {
        Scan scan = new Scan();

        Invoice i = result.getExtractionModel().getUpdatedInvoiceInformation();
        List<Position> records = result.getExtractionModel().getUpdatedRecords();

        InvoiceDao invoiceDao = new InvoiceDaoImpl();
        invoiceDao.save(i);

        PositionDao positionDao = new PositionDaoImpl();
        for (Position r : records) {
            r.setScan(scan);
        }

        try {
            ZugFerdTransformator transformator = new ZugFerdTransformator();
            byte[] file = Files.toByteArray(result.getFile());
            byte[] enhancedFile = transformator.appendInvoiceToPDF(file, i);
            scan.setFile(enhancedFile);
            scan.setCreatedDate(Date.valueOf(LocalDate.now()));
            scan.setInvoiceInformation(i);
            ScanDao scanDao = new ScanDaoImpl();
            scanDao.save(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Position r : records) {
            positionDao.save(r);
        }

        DocumentCaseSet additionalSet;
        DocumentCaseSet oldSet = result.getExtractionModel().getCaseSet();

        KeywordDao keywordDao = new KeywordDaoImpl();
        keywordList = keywordDao.getAll();

        this.creditorDao = new CreditorDaoImpl();
        this.creditorList = creditorDao.getAll();
        this.caseDao = new DocumentCaseDaoImpl();
        // oldSet == null if there was no information to the creditor or the creditor has not been found
        if (oldSet == null) {
            additionalSet = this.checkCaseSet(result.getExtractionModel(), false);
        } else {
            additionalSet = this.checkCaseSet(result.getExtractionModel(), true);
            caseDao.saveCases(oldSet);
        }
        caseDao.saveCases(additionalSet);
    }

    private DocumentCaseSet checkCaseSet(ExtractionModel extractionModel, boolean compareWithOldSet) {
        Invoice newI = extractionModel.getUpdatedInvoiceInformation();
        Invoice oldI = extractionModel.getInvoiceInformation();
        HocrDocument doc = extractionModel.getHocrDocument();
        DocumentCaseSet additionalSet = new DocumentCaseSet();
        DocumentCaseSet oldSet = extractionModel.getCaseSet();
        Creditor c = this.findCreditor(newI.getCreditor());
        int caseId = this.caseDao.getHighestCaseId() + 1;

        if (compareWithOldSet && oldSet.getBuyerCase() != null && oldI.getDebitor() != null && newI.getDebitor() != null) {
            oldSet.getBuyerCase().setIsCorrect(newI.getDebitor().getId() == oldI.getDebitor().getId());
        }
        String pos = doc.getPage(0).findPosition(newI.getDebitor().getName());
        if (pos != null) {
            additionalSet.setBuyerCase(new DocumentCase(c, caseId, this.keywordList.get(4), pos));
        }

        if (compareWithOldSet && oldSet.getInvoiceNoCase() != null && newI.getInvoiceNumber() != null && oldI.getInvoiceNumber() != null) {
            oldSet.getInvoiceNoCase().setIsCorrect(newI.getInvoiceNumber().equals(oldI.getInvoiceNumber()));
        }
        pos = doc.getPage(0).findPosition(newI.getInvoiceNumber());
        if (pos != null) {
            additionalSet.setInvoiceNoCase(new DocumentCase(c, caseId, this.keywordList.get(1), pos));
        }

        if (compareWithOldSet && oldSet.getInvoiceDateCase() != null && newI.getIssueDate() != null && oldI.getIssueDate() != null) {
            oldSet.getInvoiceDateCase().setIsCorrect(newI.getIssueDate().equals(oldI.getIssueDate()));
        }
        pos = doc.getPage(0).findPosition(this.convertDateToString(newI.getIssueDate()));
        if (pos != null) {
            additionalSet.setInvoiceDateCase(new DocumentCase(c, caseId, this.keywordList.get(2), pos));
        }

        if (compareWithOldSet && oldSet.getSellerCase() != null && newI.getCreditor() != null && oldI.getCreditor() != null) {
            oldSet.getSellerCase().setIsCorrect(newI.getCreditor().getName().equals(oldI.getCreditor().getName()));
        }
        pos = doc.getPage(0).findPosition(newI.getCreditor().getName());
        if (pos != null) {
            additionalSet.setSellerCase(new DocumentCase(c, caseId, this.keywordList.get(3), pos));
        }

        if (compareWithOldSet && oldSet.getDocumentTypeCase() != null) {
            oldSet.getDocumentTypeCase().setIsCorrect(true);
        }
        pos = doc.getPage(0).findPosition("Rechnung");
        if (pos != null) {
            additionalSet.setDocumentTypeCase(new DocumentCase(c, caseId, this.keywordList.get(0), pos));
        }

        for (Position r : extractionModel.getUpdatedRecords()) {
            // sort both alphabetically
            //extractionModel.getUpdatedRecords().sort(Comparator.comparing(Position::getEntryText));
            //oldSet.getPositionCases().sort(Comparator.comparing(DocumentCase::getPosition));
            if (compareWithOldSet && oldSet.getPositionCases() != null) {
                for (DocumentCase docCase : oldSet.getPositionCases()) {
                    if (!docCase.getIsCorrect() && docCase.getPosition().toLowerCase().equals(r.getEntryText().toLowerCase())) {
                        docCase.setIsCorrect(true);
                    }
                }
            }
            pos = doc.getPage(0).findPosition(r.getEntryText());
            if (pos != null) {
                Logger.getLogger(this.getClass()).log(Level.INFO, "Saving case for position " + r.getEntryText() + " with position " + pos);
                additionalSet.addPositionCase(new DocumentCase(c, caseId, this.keywordList.get(5), pos));
            }
        }

        return additionalSet;
    }

    private Creditor findCreditor(LegalPerson p) {
        for (Creditor c : creditorList) {
            if (c.getLegalPerson().getId() == p.getId()) {
                return c;
            } else if (p.getName() != null && c.getName().trim().toLowerCase().equals(p.getName().trim().toLowerCase())) {
                return c;
            }
        }
        // at this point, the legal person is not yet registered as a creditor
        Creditor c = new Creditor();
        c.setLegalPerson(p);
        c.setName(p.getName());
        creditorDao.save(c);
        this.creditorList = creditorDao.getAll();
        return c;
    }

    private String convertDateToString(Date date) {
        LocalDate ld = date.toLocalDate();
        StringBuilder sb = new StringBuilder();
        if (ld.getDayOfMonth() > 9) {
            sb.append(ld.getDayOfMonth()).append(".");
        } else {
            sb.append("0").append(ld.getDayOfMonth()).append(".");
        }
        if (ld.getMonthValue() > 9) {
            sb.append(ld.getMonthValue()).append(".");
        } else {
            sb.append("0").append(ld.getMonthValue()).append(".");
        }
        sb.append(ld.getYear());
        return sb.toString();
    }
}
