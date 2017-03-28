package de.cneubauer.domain.helper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Created by Christoph Neubauer on 20.02.2017.
 * Manages table content file
 */
public class TableContentFileHelper extends AbstractFileHelper {
    private static final String PATH = System.getProperty("user.dir") + "\\tablecontents.txt";

    void init() {
        Logger.getLogger(de.cneubauer.util.config.ConfigHelper.class).log(Level.INFO, "reading file on path: " + System.getProperty("user.dir") + "\\tablecontents.txt");
        setFile(PATH);
    }
}