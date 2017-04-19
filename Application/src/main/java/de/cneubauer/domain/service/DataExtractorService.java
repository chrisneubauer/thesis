package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.*;
import de.cneubauer.domain.dao.CreditorDao;
import de.cneubauer.domain.dao.DocumentCaseDao;
import de.cneubauer.domain.dao.KeywordDao;
import de.cneubauer.domain.dao.LegalPersonDao;
import de.cneubauer.domain.dao.impl.CreditorDaoImpl;
import de.cneubauer.domain.dao.impl.DocumentCaseDaoImpl;
import de.cneubauer.domain.dao.impl.KeywordDaoImpl;
import de.cneubauer.domain.dao.impl.LegalPersonDaoImpl;
import de.cneubauer.domain.helper.TableContentFileHelper;
import de.cneubauer.domain.helper.TableEndFileHelper;
import de.cneubauer.ocr.hocr.*;
import de.cneubauer.gui.model.DocumentCaseSet;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christoph Neubauer on 20.10.2016.
 * This service contains methods for extracting multiple information from a processed form
 * Output is a possible invoice filled with as much information as possible
 */
public abstract class DataExtractorService implements Runnable {
    String leftHeader;
    String rightHeader;
    String body;
    String footer;
    double confidence = ConfigHelper.getConfidenceRate();
    HocrDocument document;
    DocumentCaseDao caseDao;
    private LegalPersonDao legalPersonDao;
    List<Creditor> creditorList;
    List<Keyword> keywordList;
    List<LegalPerson> list;
    DocumentCaseSet caseSet;
    boolean extractInvoice;
    Invoice threadInvoice;
    List<Position> threadRecord;
    private List<String> tableContentWords;

    /**
     * Constructor of the DataExtractorService class
     * @param parts  the resulting strings from the ocr in the following order:
     * <p><ul>
     *    <li>[0]: left header string</li>
     *    <li>[1]: right header string</li>
     *    <li>[2]: body string</li>
     *    <li>[3]: footer string</li>
     *    <li>[4]: hocr document string</li>
     * </ul></p>
     */
    DataExtractorService(HocrDocument hocrDocument, String[] parts) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Using confidence level: " + confidence*100 + "%");
        this.document = hocrDocument;
        this.caseDao = new DocumentCaseDaoImpl();
        this.legalPersonDao = new LegalPersonDaoImpl();
        this.list = this.legalPersonDao.getAll();

        CreditorDao creditorDao = new CreditorDaoImpl();
        this.creditorList = creditorDao.getAll();

        KeywordDao keywordDao = new KeywordDaoImpl();
        this.keywordList = keywordDao.getAll();

        this.leftHeader = parts[0];
        this.rightHeader = parts[1];
        this.body = parts[2];
        this.footer = parts[3];

        TableContentFileHelper contentFileHelper = new TableContentFileHelper();
        this.tableContentWords = contentFileHelper.getValues();
    }

    /**
     * Searches for an HocrElement that has been stored in a documentcase before
     * @param cases a list of documentCases where a possible match could be
     * @return the element that has been found or null if nothing has been found
     */
    HocrElement findInCase(List<DocumentCase> cases) {
        // TODO: make parameter to repell or attract position
        for (DocumentCase docCase : cases) {
            if (docCase.getIsCorrect()) {
                String[] position = docCase.getPosition().split("\\+");
                // 0: startX, 1: startY, 2: endX, 3: endY
                int[] pos = new int[] {Integer.valueOf(position[0]), Integer.valueOf(position[1]), Integer.valueOf(position[2]), Integer.valueOf(position[3])};

                HocrElement possibleArea = this.document.getPage(0).getByPosition(pos, 50);
                if (possibleArea != null) {
                    HocrParagraph possibleParagraph = (HocrParagraph) possibleArea.getByPosition(pos, 30);
                    if (possibleParagraph != null) {
                        HocrLine possibleLine = (HocrLine) possibleParagraph.getByPosition(pos, 30);
                        if (possibleLine != null) {
                            HocrWord possibleWord = (HocrWord) possibleLine.getByPosition(pos, 10);
                            if (possibleWord != null) {
                                return possibleWord;
                            } else {
                                // refine to multiple words, pixel threshold only a few pixels since we are searching for word
                                possibleWord = possibleLine.getWordsByPosition(pos, 10);
                                return possibleWord;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Parses a string and tries to extract numeric information
     * @param pos the string that contains the value information
     * @return a double value or 0.0 if nothing has been found
     */
    double getValueFromLine(String pos) {
        int euroPos = 0;
        if (pos.toLowerCase().contains("eur")) {
            euroPos = pos.toLowerCase().lastIndexOf("eur");

        } else if (pos.toLowerCase().contains("€")) {
            euroPos = pos.toLowerCase().lastIndexOf("€");
        }
        try {
            pos = pos.substring(0, euroPos);
            String[] parts = pos.split(" ");
            if (parts[parts.length - 1].contains(",")) {
                parts[parts.length - 1] = parts[parts.length - 1].replace(",", ".");
            }
            return Double.valueOf(parts[parts.length -1]);
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Unable to parse double value, using default");
            return 0;
        }
    }

    /**
     * Takes a line and searches for keywords that are usually in the header of an invoice table
     * @param line the line to be searched
     * @return true if the line contains table information
     */
    boolean lineContainsTableInformation(String line) {
        line = line.toLowerCase().trim();
        boolean result = false;
        // first iteration: search for some strings:
        //List<String> valuesToCheck = TableContentFileHelper.getValues();
        for (String value : tableContentWords) {
            result = (result || line.contains(value));
        }
        if (result) { return true; }
        else {
            // second iteration: we have to refine the search because we didn't find any of those words
            if (this.refineSearch(line.split(" "), tableContentWords)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Takes a line and searches for keywords that are usually in the end of an invoice table
     * @param line the line to be searched
     * @return true if the line contains table end information
     */
    boolean lineContainsTableEndInformation(String line) {
        line = line.toLowerCase().trim();
        boolean result = false;
        // first iteration: search for some strings:
        TableEndFileHelper tableEndFileHelper = new TableEndFileHelper();
        List<String> valuesToCheck = tableEndFileHelper.getValues();
        for (String value : valuesToCheck) {
            result = (result || line.contains(value));
        }
        if (result) { return true; }
        else {
            // second iteration: we have to refine the search because we didn't find any of those words
            if (this.refineSearch(line.split(" "), valuesToCheck)) {
                return true;
            }
        }
        return false;
    }

    private boolean refineSearch(String[] line, List<String> valuesToCheck) {
        boolean result = false;
        for (String string : line) {
            for (String value : valuesToCheck) {
                result = result || this.refineSearch(string, value);
            }
            if (result) { return true; }
        }
        return false;
    }

    /**
     * Removes unnecessary columns with financial information
     * @param line  the line that should be checked
     * @return  the original line cleared by financial information
     */
    String removeFinancialInformationFromRecordLine(String line) {
        line = line.replace("EUR", "");
        line = line.replace("€","");
        // regex that replaces all occurances of numbers up to 100 million + "," and two digits afterwards
        Pattern p = Pattern.compile("\\d{1,9}(,\\d{2})");
        Matcher m = p.matcher(line);

        line = m.replaceAll("");
        return line;
    }

    LegalPerson getLegalPersonFromDatabase(List<String> lines, boolean searchForCreditor) {
        LegalPersonDao dao = new LegalPersonDaoImpl();

        if (searchForCreditor) {

            for (String line : lines) {
                // As almost all creditors are companies, we can try to improve this line if we find some corporate information
                // ocr errors, such as a whole line containing not only the company, but also their address can be made more accurate
                // therefore, we search for corporate information. If we find any, we will only take the first part of the string
                int idx = this.findCorporateFormIndex(line);
                if (idx > 0) {
                    line = line.substring(0, idx);
                }
                String compareLine = line.trim().toLowerCase();
                for (Creditor c : creditorList) {
                    String creditor = c.getName().trim().toLowerCase();
                    if (compareLine.contains(creditor) || refineSearch(compareLine, creditor)) {
                        return c.getLegalPerson();
                    }
                }
            }
            // retry again to maybe find a creditor that has been saved but not been found due to ocr
            double min = 1;
            int minPos = -1;
            for (String line : lines) {
                for (int i = 0; i < creditorList.size(); i++) {
                    Creditor c = creditorList.get(i);
                    String creditor = c.getName().trim().toLowerCase();

                    double distance = StringUtils.getLevenshteinDistance(line.toLowerCase().trim(), creditor);
                    double comparison = distance / line.length();

                    minPos = comparison < min ? i : minPos;
                    min = comparison < min ? comparison : min;
                }
            }
            if (minPos >= 0 && min < ConfigHelper.getConfidenceRate()) {
                creditorList.get(minPos);
            }
        }
        else {
            List<LegalPerson> list = dao.getAllDebitors();
            for (String line : lines) {
                for (LegalPerson p : list) {
                    if (p.getName() != null) {
                        String person = p.getName().trim().toLowerCase();
                        String toCheck = line.trim().toLowerCase();
                        if (toCheck.contains(person) || this.refineSearch(toCheck, person)) {
                            return p;
                        }
                    }
                }
            }
        }
        return null;
    }

    LegalPerson getLegalPersonFromDatabase(HocrDocument document, boolean searchForCreditor) {
        List<String> lines = document.getDocumentAsList();
        return this.getLegalPersonFromDatabase(lines, searchForCreditor);
    }

    boolean refineSearch(String checkString, String compareWith) {
        // refine search if we have some ocr probs
        /* calculation explanation:
         * Comparing "18:1 Telecom GmbH" with "Telekom"
         * Levenshtein-Distance would be: 11
         * String length: 17
         * 11 of 17 are incorrect (65%) which means 35% correct
         * When confidence 80% we need less errors, maximum: 0/17 -> 0% < 20%
        */
        if (checkString == null || compareWith == null) {
            return false;
        }
        double confidenceRate = ConfigHelper.getConfidenceRate();
        double distance = StringUtils.getLevenshteinDistance(checkString.toLowerCase().trim(), compareWith.toLowerCase().trim());
        double comparison = distance / checkString.length();
        return comparison < confidenceRate;
    }

    /**
     * Convenience method
     * @param searchConditions the conditions that should be searched for
     * @param searchPosition the string where the conditions should be in
     * @return the next word after the found search condition, or an empty string if nothing has been found
     */
    String findValueInString(String[] searchConditions, String searchPosition) {
        return this.findValueInString(searchConditions, searchPosition, false);
    }

    String findValueInString(String[] searchConditions, List<String> searchPosition, boolean everythingAfter) {
        StringBuilder sb = new StringBuilder();
        for (String s : searchPosition) {
            sb.append(s).append("\n");
        }
        return this.findValueInString(searchConditions, sb.toString(), everythingAfter);
    }


    /**
     * This method goes through the searchPosition and tries to find any of the given search conditions
     * @param searchConditions  the conditions that should be searched for
     * @param searchPosition  the string where the conditions should be in
     * @param everythingAfter a flag that indicates if the whole line should be returned after the search condition
     * @return  the next word after the found search condition, or an empty string if nothing has been found
     */
    String findValueInString(String[] searchConditions, String searchPosition, boolean everythingAfter) {
        String[] lines = searchPosition.split("\n");
        double min = 1.0;
        int minPos = -1;
        int minI = -1;
        for (int i1 = 0; i1 < lines.length; i1++) {
            String line = lines[i1];
            String[] parts = line.split(" ");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (this.partContainsSearchConditions(part, searchConditions)) {
                    if (i < parts.length - 1) {
                        return parts[i + 1];
                    } else {
                        return "";
                    }
                } else {
                    // try again with levenshtein distance
                    double avgRatio = this.getAverageDistanceOfSearchConditions(part, searchConditions);
                    // save the line if ratio > 80%
                    if (avgRatio < confidence) {
                        minPos = avgRatio < min? i1 : minPos;
                        minI = avgRatio < min? i : minI;
                        min = avgRatio < min ? avgRatio : min;
                    }
                }
            }
        }
        // use the best levenshtein result if there is one
        if (minPos >= 0 && min < 1.0) {
            String[] parts = lines[minPos].split(" ");
            if (minI < parts.length - 1) {
                if (everythingAfter) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < parts.length - minI; i++) {
                        sb.append(parts[minI + i]);
                    }
                    return sb.toString();
                } else {
                    return parts[minI + 1];
                }
            }
        }
        // if we are here we have not found an invoice number
        return "";
    }

    /**
     * Calculates the average distance of all given searchConditions
     * This enables an overall check for multiple words
     * @param part  the string where the conditions should be in
     * @param searchConditions  the conditions that should be searched for
     * @return  the average distance as a double value
     */
    double getAverageDistanceOfSearchConditions(String part, String[] searchConditions) {
        double min;
        double oldMin = 100;
        double result = 100;
        // we want to find signal words in the string
        // therefore the whole line has to be separated into each word
        String[] lines = part.split(" ");
        for (String con : searchConditions) {
            for (String line : lines) {
                min = StringUtils.getLevenshteinDistance(line, con);
                if (min < oldMin) {
                    result = min / line.length();
                    oldMin = min;
                }
                //result += ((double) amountOfChanges) / (Math.max(part.length(), con.length()));
            }
        }
        return result / 2; // additional divisor to adjust number influence level // / (double) searchConditions.length;
    }

    /**
     *
     * @param part  the part to be checked
     * @param searchConditions  the search conditions to be searched for
     * @return  true if at least one of the search conditions are in the given part, false if otherwise
     */
    private boolean partContainsSearchConditions(String part, String[] searchConditions) {
        for (String con : searchConditions) {
            if (part.contains(con)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param nextLine  the line that should be searched in
     * @return  true if the next line contains a numeric value
     */
    boolean nextLineContainsValue(String nextLine) {
        if (nextLine.contains(",")) {
            String[] parts = nextLine.split(",");
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                part = part.substring(part.length() - 1, part.length());
                String nextPart = parts[i+1];
                if (StringUtils.isNumeric(part) && StringUtils.isNumeric(nextPart.substring(0,1))) {
                    return true;
                }
            }
        }
        return false;
    }

    public DocumentCaseSet getCaseSet() {
        return caseSet;
    }

    private int findCorporateFormIndex(String line) {
        line = line.toLowerCase();
        int result = 0;
        if (line.indexOf("gmbh") > 0) {
            result = line.indexOf("gmbh") + 4;
        }
        else if (line.indexOf("ag") > 0) {
            result = line.indexOf("ag") + 2;
        }
        else if (line.indexOf("kg") > 0) {
            result = line.indexOf("kg") + 2;
        }
        else if (line.indexOf("kgaa") > 0) {
            result = line.indexOf("kgaa") + 4;
        }
        else if (line.indexOf("ohg") > 0) {
            result = line.indexOf("ohg") + 3;
        }
        else if (line.indexOf("gbr") > 0) {
            result = line.indexOf("gbr") + 3;
        }
        return result;
    }

    HocrDocument getHocrDoument() {
        return this.document;
    }

    public Invoice getThreadInvoice() {
        if (this.threadInvoice == null) {
            return null;
        } else {
            this.caseDao = null;
            this.legalPersonDao = null;
            return this.threadInvoice;
        }
    }

    public List<Position> getThreadRecord() {return this.threadRecord;}
}
