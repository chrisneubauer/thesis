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
public class ModelReader {
    private List<Model> models;
    private List<Account> accounts;

    public ModelReader() {
        AccountDao accountDao = new AccountDaoImpl();
        this.accounts = accountDao.getAll();
    }

    private File openFile() throws IOException {
        Path path = Paths.get(System.getProperty("user.dir") + "\\models.txt");
        if (Files.exists(path)) {
            return path.toFile();
        } else {
            return Files.createFile(path).toFile();
        }
    }

    private void readModel() throws IOException {
        models = new LinkedList<>();
        InputStream in = new FileInputStream(this.openFile());
        BufferedReader r = new BufferedReader(new InputStreamReader(in));

        String line = r.readLine();
        while (line != null) {
            Model m = new Model();
            List<String> values = this.getValues(line);
            m.setPosition(values.get(0));

            for (String accNo : values.get(1).split(";")) {
                // TODO: Make more efficient
                for (Account a : accounts) {
                    if (a.getAccountNo().equals(accNo)) {
                        m.addCreditAccount(a);
                        break;
                    }
                }
            }

            for (String accNo : values.get(2).split(";")) {
                // TODO: Make more efficient
                for (Account a : accounts) {
                    if (a.getAccountNo().equals(accNo)) {
                        m.addDebitAccount(a);
                        break;
                    }
                }
            }
            this.models.add(m);
            line = r.readLine();
        }
    }

    private List<String> getValues(String line) {
        // splits a line consisting of position, credit acc numbers and debit acc numbers into three strings
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

    public List<Model> getModels() throws IOException {
        if (this.models == null) {
            this.readModel();
        }
        return this.models;
    }

    public Model getModelByStringAndAccounts(String position, List<Account> category) throws IOException {
        if (this.models == null) {
            this.getModels();
        }
        for (Model m : models) {
            if (m.getPosition().equals(position)) {
                List<Account> deleteableList = new LinkedList<>(category);
                for (Account debitAcc : m.getDebit()) {
                    if (deleteableList.contains(debitAcc)) {
                        deleteableList.remove(debitAcc);
                    }
                }
                for (Account creditAcc : m.getCredit()) {
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
