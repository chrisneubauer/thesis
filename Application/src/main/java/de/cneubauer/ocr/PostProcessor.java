package de.cneubauer.ocr;

import de.cneubauer.util.config.ConfigHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Postprocessor to improve output of images by checking dictionary values
 */
public class PostProcessor {
    private final String DICTIONARYPATH = ".\\src\\main\\resources\\dict\\invoice.de.txt";
    private double confidence = 1 - (Double.valueOf(ConfigHelper.getValue("confidenceRate")));
    private String textToProcess;

    public PostProcessor(String text) {
        this.textToProcess = text;
    }

    public String improveValues() {
        String[] separated = this.textToProcess.split(" ");
        String[] dict = this.readDictionaryValues();

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

    private String[] readDictionaryValues() {
        File dictionary = new File(DICTIONARYPATH);
        LinkedList<String> lines = new LinkedList<>();
        try {
            InputStream in = new FileInputStream(dictionary);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line = r.readLine();
            while (line != null) {
                lines.add(r.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String[]) lines.toArray();
    }
}
