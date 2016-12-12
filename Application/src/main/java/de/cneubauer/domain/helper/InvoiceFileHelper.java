package de.cneubauer.domain.helper;

import de.cneubauer.util.config.ConfigHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Christoph Neubauer on 24.11.2016.
 * This class is responsible for retrieving and writing values to the invoice learning file
 */
public final class InvoiceFileHelper {
    private static File learningFile;
    private static Map<String, String> invoiceMap;

    private InvoiceFileHelper() {
        Logger.getLogger(ConfigHelper.class).log(Level.INFO, "initiating learning file on path: " + System.getProperty("user.dir") + "\\invoiceData.txt");
        setLearningFile(System.getProperty("user.dir"));
    }

    public static String getValue(String property) {
        if (invoiceMap == null) {
            new InvoiceFileHelper();
        }
        return invoiceMap.get(property);
    }

    public static Map<String, String> getConfig() {
        if (invoiceMap == null) {
            new InvoiceFileHelper();
        }
        Map<String, String> properties = new HashMap<>();
        try {
            InputStream in = new FileInputStream(learningFile);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line = r.readLine();
            while (line != null) {
                String creditor = line.split("]")[0];
                creditor = creditor.substring(1);
                String debitor = line.split("]")[1];
                debitor = debitor.substring(1);
                properties.put(creditor, debitor);
                line = r.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        invoiceMap = properties;
        return invoiceMap;
    }


    /*
     * searches for key and adjusts value
     * if no key could be found, a new line is added to the config file
     * @param   key     the name of the creditor
     * @param   value   the name of the debitor
     */
    public static void addOrUpdate(String key, String value) {
        if (getValue(key) == null) {
            invoiceMap.put(key, value);
            write(key, value);
        } else {
            invoiceMap.replace(key, value);
        }
        rewrite(invoiceMap);
    }

    /*
     * Extend training data by the specified position account value
     * @param   key     the name of the creditor
     * @param   value   the name of the debitor
     */
    // extends config file for one line
    public static void write(String key, String value) {
        try {
            if (invoiceMap == null) {
                new InvoiceFileHelper();
            }
            OutputStream out = new FileOutputStream(learningFile, true);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
            w.write("[" + key + "][" + value + "]");
            w.newLine();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(ConfigHelper.class).log(Level.ERROR, "Unable to add invoice settings! Please delete config.ini to reset to default settings");
        }
    }

    //TODO: Make cryptic
    private static void setLearningFile(String path) {
        try {
            learningFile = Files.createFile(Paths.get(path + "\\invoiceData.txt")).toFile();
        } catch (IOException e) {
            learningFile = new File(path + "\\invoiceData.txt");
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
            Logger.getLogger(ConfigHelper.class).log(Level.ERROR, "Unable to rewrite invoice settings! Please delete config.ini to reset to default settings");
        }
    }

    // returns all keys and values as a List
    public static List<String> createListOfValues() {
        if (invoiceMap == null) {
            new InvoiceFileHelper();
        }
        List<String> result = new ArrayList<>(invoiceMap.size() * 2);
        result.addAll(invoiceMap.keySet());
        result.addAll(invoiceMap.values());
        return result;
    }
}
