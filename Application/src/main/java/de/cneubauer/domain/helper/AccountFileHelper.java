package de.cneubauer.domain.helper;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.util.RecordTrainingEntry;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    public static void addAccountingRecord(String line) {
        try {
            if (learningFile == null) {
                new AccountFileHelper();
            }
            OutputStream out = new FileOutputStream(learningFile, true);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
            w.newLine();
            w.write(line);
            //TODO: Here should be some logic about the chosen accounts
            w.newLine();
            w.write("ENDRECORD");
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(ConfigHelper.class).log(Level.ERROR, "Unable to add account settings! Please delete config.ini to reset to default settings");
        }
    }

    public static RecordTrainingEntry findAccountingRecord(String line) {
        for (RecordTrainingEntry entry : getAllRecords()) {
            if (entry.getPosition().equals(line)) {
                return entry;
            }
        }
        return null;
    }

    private static List<RecordTrainingEntry> getAllRecords() {
        List<RecordTrainingEntry> result = new ArrayList<>();
        try {
            if (learningFile == null) {
            new AccountFileHelper();
            }
            BufferedReader reader = new BufferedReader(new FileReader(learningFile));
            String currentLine = reader.readLine();
            while (currentLine != null) {
                RecordTrainingEntry r = new RecordTrainingEntry();
                boolean newRecord = true;
                while (!Objects.equals(currentLine, "ENDRECORD")) {
                    if (newRecord) {
                        r.setPosition(currentLine);
                        newRecord = false;
                    } else {
                        if (currentLine.contains(" an ")) {
                            String debitAcc = currentLine.split(" an ")[0];
                            String creditAcc = currentLine.split(" an ")[1];
                            extractEntryInformation(debitAcc, true, r);
                            extractEntryInformation(creditAcc, false, r);
                        } else {
                            extractEntryInformation(currentLine, true, r);
                        }
                    }
                    currentLine = reader.readLine();
                }
                result.add(r);
                currentLine = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

    private static void extractEntryInformation(String currentLine, boolean isDebit, RecordTrainingEntry r) {
        String[] parts = currentLine.split(" ");
        int length = parts.length;
        String value = parts[length-1].replace("€", "").replace("$", "").replace(".", "").replace(",",".");
        String accountName = "";
        for (int i = 0; i < parts.length - 1; i++) {
            accountName += parts[i] + " ";
        }
        // remove last additional whitespace
        accountName = accountName.substring(0, accountName.length() - 1);

        //AccountDao dao = new AccountDaoImpl();
        //Account a = dao.getByName(accountName);
        if (isDebit) {
            r.setDebitAccount(accountName, Double.valueOf(value));
        } else {
            r.setCreditAccount(accountName, Double.valueOf(value));
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
