package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christoph Neubauer on 02.02.2017.
 * This class is responsible for reading in the file that contains the already stored models
 */
@Deprecated
public class ModelReader {
    private List<Model> models;
    private List<Account> accounts;

    public ModelReader() {
        AccountDao accountDao = new AccountDaoImpl();
        this.accounts = accountDao.getAll();
    }

    /**
     * @return the file containing model information
     * @throws IOException if there is a problem reading the textfile
     */
    private File openFile() throws IOException {
        Path path = Paths.get(System.getProperty("user.dir") + "\\models.txt");
        if (Files.exists(path)) {
            return path.toFile();
        } else {
            return Files.createFile(path).toFile();
        }
    }

    /**
     * Reads in model information from the learning file
     * @throws IOException if there is a problem reading the textfile
     */
    private void readModel() throws IOException {
        models = new LinkedList<>();
        InputStream in = new FileInputStream(this.openFile());
        BufferedReader r = new BufferedReader(new InputStreamReader(in));

        String line = r.readLine();
        while (line != null) {
            Model m = new Model();
            List<String> values = this.getValues(line);
            m.setPosition(values.get(0));

            for (String accountRelation : values.get(1).split(";")) {
                String account = accountRelation.split(":")[0];
                Double value = Double.valueOf(accountRelation.split(":")[1]);
                // TODO: Make more efficient
                for (Account a : accounts) {
                    if (a.getAccountNo().equals(account)) {
                        m.addToCreditAccounts(a, value);
                        break;
                    }
                }
            }

            for (String accountRelation : values.get(2).split(";")) {
                // TODO: Make more efficient
                String account = accountRelation.split(":")[0];
                Double value = Double.valueOf(accountRelation.split(":")[1]);
                for (Account a : accounts) {
                    if (a.getAccountNo().equals(account)) {
                        m.addToDebitAccounts(a, value);
                        break;
                    }
                }
            }
            this.models.add(m);
            line = r.readLine();
        }
    }

    /**
     * splits a line consisting of position, credit acc numbers and debit acc numbers into three strings
     * <p><ul>
     *     <li>[0]: the position</li>
     *     <li>[1]: the credit account numbers</li>
     *     <li>[2]: the debit account numbers</li>
     * </ul></p>
     * @param line  the line consisting with the information
     * @return  the list of values
     */
    private List<String> getValues(String line) {
        List<String> list = new ArrayList<>(3);
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(line);
        int idx = 0;
        while (m.find()) {
            list.add(idx, m.group(1));
            idx++;
        }
        return list;
    }

    /**
     * Reads the learning file and returns all models as a list
     * @return the list of models existing in the learning file
     * @throws IOException if there is a problem reading the textfile
     */
    public List<Model> getModels() throws IOException {
        if (this.models == null) {
            this.readModel();
        }
        return this.models;
    }

    /**
     * @param position  the position to be searched for
     * @param category  the accounts that define a model to be in this category
     * @return the model found that is existing in the learning file or null if not existent
     * @throws IOException if there is a problem reading the textfile
     */
    Model getModelByStringAndAccounts(String position, List<Account> category) throws IOException {
        if (this.models == null) {
            this.getModels();
        }
        for (Model m : models) {
            if (m.getPosition().equals(position)) {
                List<Account> deleteableList = new LinkedList<>(category);
                for (Account debitAcc : m.getDebit().keySet()) {
                    if (deleteableList.contains(debitAcc)) {
                        deleteableList.remove(debitAcc);
                    }
                }
                for (Account creditAcc : m.getCredit().keySet()) {
                    if (deleteableList.contains(creditAcc)) {
                        deleteableList.remove(creditAcc);
                    }
                }
                if (deleteableList.size() == 0) {
                    return m;
                }
            }
        }
        return null;
    }
}
