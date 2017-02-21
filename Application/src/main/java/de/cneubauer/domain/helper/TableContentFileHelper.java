package de.cneubauer.domain.helper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 20.02.2017.
 * Manages table content file
 */
public class TableContentFileHelper extends AbstractFileHelper {
    private static final String PATH = System.getProperty("user.dir") + "\\tablecontents.txt";

    private static void init() {
        Logger.getLogger(de.cneubauer.util.config.ConfigHelper.class).log(Level.INFO, "reading file on path: " + System.getProperty("user.dir") + "\\tablecontents.txt");
        setFile(PATH);
    }

    public static List<String> getValues() {
        List<String> result = new LinkedList<>();
        init();

        try {
            list = readFile();
            for (String line : list) {
                result.add(line.trim().toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
            list = new LinkedList<>();
        }
        return result;
    }
}