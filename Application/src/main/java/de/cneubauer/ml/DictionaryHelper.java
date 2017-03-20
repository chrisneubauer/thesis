package de.cneubauer.ml;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Christoph on 20.03.2017.
 * Helper to normalize values to a standard word
 */
public class DictionaryHelper {
    private final String DICTIONARYPATH = "src\\main\\resources\\dict\\dictionary.dump";

    public String replaceValuesFromDictionary(String feature) {
        try {
            LineIterator it = FileUtils.lineIterator(new File(DICTIONARYPATH), "UTF-8");
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    String parts[] = line.split("\t");
                    if (feature.toLowerCase().equals(parts[0].toLowerCase())) {
                        Logger.getLogger(this.getClass()).log(Level.INFO, "Found replaceable word " + parts[1] + " for feature: " + feature);
                        return parts[1];
                    }
                }
            } finally {
                LineIterator.closeQuietly(it);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to retrieve dictionary information. Using given feature string");
            return feature;
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to retrieve dictionary information. Using given feature string");
        }
        return feature;
    }
}
