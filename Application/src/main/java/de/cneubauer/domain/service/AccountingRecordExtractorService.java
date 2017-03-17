package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Creditor;
import de.cneubauer.domain.bo.DocumentCase;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.bo.Record;
import de.cneubauer.ml.LearningService;
import de.cneubauer.ml.Model;
import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.ocr.hocr.HocrElement;
import de.cneubauer.util.DocumentCaseSet;
import de.cneubauer.util.enumeration.CaseKey;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph on 17.03.2017.
 * DataExtractionService for accounting records
 */
public class AccountingRecordExtractorService extends DataExtractorService {
    public AccountingRecordExtractorService(HocrDocument doc, String[] parts) {
        super(doc, parts);
        super.extractInvoice = false;
    }

    private List<Record> extractAccountingRecordInformationFromHocr() {
        List<Record> records = new LinkedList<>();
        boolean endReached;
        LegalPerson possibleCreditor = this.getLegalPersonFromDatabase(this.getHocrDoument(), true);

        if (possibleCreditor != null) {
            Creditor creditor = null;
            for (Creditor c : this.creditorList) {
                if (c.getLegalPerson().getId() == possibleCreditor.getId()) {
                    creditor = c;
                    break;
                }
            }

            List<DocumentCase> cases = this.caseDao.getAllByCreditorName(possibleCreditor.getName());

            List<DocumentCase> positionCase = new LinkedList<>();
            int highestCase = 0;
            this.caseSet = new DocumentCaseSet();

            for (DocumentCase documentCase : cases) {
                if (documentCase.getKeyword().getId() == CaseKey.POSITION) {
                    positionCase.add(documentCase);
                }
                if (documentCase.getCaseId() > highestCase) {
                    highestCase = documentCase.getCaseId();
                }
            }

            int minStartX = 2000;
            int minStartY = 2000;
            int possibleMaxX = 0;
            int possibleMaxY = 0;

            // getting starting values of Y and X positions in the document as well as possible endings
            for (DocumentCase position : positionCase) {
                String pos = position.getPosition();
                // 0: startX, 1: startY, 2: endX, 3: endY
                int startX = Integer.valueOf(pos.split("\\+")[0]);
                minStartX = startX < minStartX ? startX : minStartX;
                int startY = Integer.valueOf(pos.split("\\+")[1]);
                minStartY = startY < minStartY ? startY : minStartY;
                int endX = Integer.valueOf(pos.split("\\+")[2]);
                possibleMaxX = endX > possibleMaxX ? endX : possibleMaxX;
                int endY = Integer.valueOf(pos.split("\\+")[3]);
                possibleMaxY = endY > possibleMaxY ? endY : possibleMaxY;
            }

            List<HocrElement> words = this.document.getPage(0).getRecursiveElementsByPosition(new int[]{minStartX, minStartY, possibleMaxX, possibleMaxY}, 20);

            // new highest case:
            highestCase = highestCase + 1;

            LearningService service = new LearningService();
            List<HocrElement> lines = new LinkedList<>();
            for (HocrElement word : words) {
                lines.add(word.getParent());
            }
            for (HocrElement line : lines) {
                for (HocrElement word : words) {
                    String position = word.getValue();
                    endReached = this.lineContainsTableEndInformation(position);
                    if (!endReached) {
                        Record r = new Record();
                        String recordLine = this.removeFinancialInformationFromRecordLine(position);
                        double value = this.getValueFromLine(line.getValue());

                        Model m = service.getMostLikelyModel(recordLine);
                        if (m != null) {
                            r.setEntryText(m.getPosition());
                            r.setRecordAccounts(m.getAsAccountRecord(value));
                            r.setProbability(m.getProbability());
                            records.add(r);
                            this.caseSet.addPositionCase(new DocumentCase(creditor, highestCase, keywordList.get(5), position));
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            return records;
        }
        return null;
    }

    /**
     * Uses scanned page and looks for several information regarding accounting records
     * @return  returns a list of all AccountingRecords that has been found on the page
     */
    private List<Record> extractAccountingRecordInformation() {
        List<Record> records = new LinkedList<>();
        int index = 0;
        boolean found = false;
        boolean endReached;
        this.body = this.body.replace("\n\n", "\n");
        String[] lines = this.body.split("\n");
        while (!found && lines.length > index) {
            String line = lines[index];
            if (this.lineContainsTableInformation(line)) {
                found = true;
                LearningService service = new LearningService();
                while (lines.length > index + 1) {
                    String nextLine = lines[index + 1];
                    if(!this.nextLineContainsValue(nextLine) && lines.length > index + 1) {
                        // go on until we find a line with value or end of file reached
                        index++;
                    }
                    else {
                        // now we have a line with position information
                        // or the end if there are keywords like "Betrag" or "Summe"
                        // check these before going on
                        endReached = this.lineContainsTableEndInformation(nextLine);
                        if (!endReached) {
                            Record r = new Record();
                            String recordLine = this.removeFinancialInformationFromRecordLine(nextLine);
                            double value = this.getValueFromLine(nextLine);

                            Model m = service.getMostLikelyModel(recordLine);
                            //RecordTrainingEntry entry = this.recordInLearningFile(recordLine);
                            if (m == null) {
                                r.setEntryText(nextLine);
                            } else {
                                r.setEntryText(m.getPosition());
                                r.setRecordAccounts(m.getAsAccountRecord(value));
                                r.setProbability(m.getProbability());
                            }
                            records.add(r);
                            index++;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                index++;
            }
        }

        return records;
    }

    @Override
    public void run() {
        String info = this.extractInvoice? "invoice information" : "account record information";
        Logger.getLogger(this.getClass()).log(Level.INFO, "Thread started. Searching for " + info);
        this.threadRecord = this.extractAccountingRecordInformationFromHocr();
        if (this.threadRecord == null || this.threadRecord.size() == 0) {
            Logger.getLogger(this.getClass()).log(Level.INFO, "No record information in hOCR, using default strategy");
            this.threadRecord = extractAccountingRecordInformation();
        }
    }
}
