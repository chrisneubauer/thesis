package de.cneubauer.ocr;

import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.ocr.hocr.HocrElement;
import de.cneubauer.ocr.hocr.HocrPage;
import de.cneubauer.ocr.hocr.HocrWord;
import de.cneubauer.util.EasyFileReader;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Postprocessor to improve output of images by checking dictionary values
 */
public class PostProcessor {
    private double confidence = 1 - ConfigHelper.getConfidenceRate();
    private String textToProcess;
    private HocrDocument documentToProcess;

    public PostProcessor(String text) {
        this.textToProcess = text;
        this.documentToProcess = null;
    }

    public PostProcessor(HocrDocument doc) {
        this.documentToProcess = doc;
        this.textToProcess = null;
    }

    public String improveValues() {
        String[] separated = this.textToProcess.split(" ");
        List<String> dict = this.readDictionaryValues();

        // compare every word in the sequence with values in the dictionary
        for (String word : separated) {
            for (String dictWord : dict) {
                // replace the word if the dictionary word is probably the right word
                if (StringUtils.getLevenshteinDistance(word, dictWord) < this.confidence) {
                    word = dictWord;
                }
            }
        }

        // reunite the words and return them
        StringBuilder sb = new StringBuilder();
        for (String word : separated) {
            sb.append(word);
            sb.append(" ");
        }

        return sb.toString();
    }

    public HocrDocument postProcess() {
        List<String> correctWords = this.readDictionaryValues();
        for (HocrPage page : this.documentToProcess.getPages()) {
            for (HocrElement area : page.getSubElements()) {
                for (HocrElement paragraph : area.getSubElements()) {
                    for (HocrElement line : paragraph.getSubElements()) {
                        for (int i = 0; i < line.getSubElements().size(); i++) {
                            HocrWord w = (HocrWord) line.getSubElements().get(i);
                            for (String dictWord : correctWords) {
                                // replace the word if the dictionary word is probably the right word
                                double confidenceRate = ConfigHelper.getConfidenceRate();
                                double distance = StringUtils.getLevenshteinDistance(w.getValue().toLowerCase().trim(), dictWord.toLowerCase().trim());
                                double comparison = distance / w.getValue().length();
                                if (comparison < confidenceRate) {
                                    w.setValue(dictWord);
                                    line.getSubElements().set(i, w);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return this.documentToProcess;
    }

    private List<String> readDictionaryValues() {
        String DICTIONARYPATH = ".\\src\\main\\resources\\dict\\invoice.de.txt";
        EasyFileReader reader = new EasyFileReader(DICTIONARYPATH);
        return reader.getLines();
    }
}
