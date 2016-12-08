package de.cneubauer.domain.helper;

import de.cneubauer.util.config.ConfigHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christoph Neubauer on 24.11.2016.
 * This class is responsible for retrieving and writing values to the learning file
 */
public final class AccountFileHelper {
    private static File learningFile;
    private static Map<String, String> positionAccountMap;

    private AccountFileHelper() {
        Logger.getLogger(ConfigHelper.class).log(Level.INFO, "initiating learning file on path: " + System.getProperty("user.dir") + "\\accountingData.txt");
        setLearningFile(System.getProperty("user.dir"));
    }

    public static String getValue(String property) {
        if (positionAccountMap == null) {
            new AccountFileHelper();
        }
        return positionAccountMap.get(property);
    }

    public static Map<String, String> getConfig() {
        if (positionAccountMap == null) {
            new AccountFileHelper();
        }
        Map<String, String> properties = new HashMap<>();
        try {
            InputStream in = new FileInputStream(learningFile);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line = r.readLine();
            while (line != null) {
                String position = line.split("]")[0];
                position = position.substring(1);
                String account = line.split("]")[1];
                account = account.substring(1);
                properties.put(position, account);
                line = r.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        positionAccountMap = properties;
        return positionAccountMap;
    }


    /*
     * searches for key and adjusts value
     * if no key could be found, a new line is added to the config file
     * @param   key     the position to be stored
     * @param   value   the account that is used for the specified position
     */
    public static void addOrUpdate(String key, String value) {
        if (getValue(key) == null) {
            positionAccountMap.put(key, value);
            write(key, value);
        } else {
            positionAccountMap.replace(key, value);
        }
        rewrite(positionAccountMap);
    }

    /*
     * Extend training data by the specified position account value
     * @param   key     the position to be stored
     * @param   value   the account that is used for the specified position
     */
    // extends config file for one line
    public static void write(String key, String value) {
        try {
            if (learningFile == null) {
                new AccountFileHelper();
            }
            OutputStream out = new FileOutputStream(learningFile, true);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
            w.write("[" + key + "][" + value + "]");
            w.newLine();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(ConfigHelper.class).log(Level.ERROR, "Unable to add account settings! Please delete config.ini to reset to default settings");
        }
    }

    //TODO: Make cryptic
    private static void setLearningFile(String path) {
        try {
            learningFile = Files.createFile(Paths.get(path + "\\accountingData.txt")).toFile();
        } catch (IOException e) {
            learningFile = new File(path + "\\accountingData.txt");
        }
    }

    private static void rewrite(Map<String, String> newConfiguration) {
        try {
            OutputStream out = new FileOutputStream(learningFile);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
            for (Map.Entry<String, String> entry : newConfiguration.entrySet()) {
                w.write("[" + entry.getKey() + "][" + entry.getValue() + "]");
                w.newLine();
            }
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(ConfigHelper.class).log(Level.ERROR, "Unable to rewrite account settings! Please delete config.ini to reset to default settings");
        }
    }
}
