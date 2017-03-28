package de.cneubauer.ml.nlp;

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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christoph on 28.03.2017.
 * Provides logic for reading and writing models to the file
 */
public class NLPFileHelper {
    private List<NLPModel> models;
    private List<Account> accounts;

    NLPFileHelper() {
        AccountDao accountDao = new AccountDaoImpl();
        this.accounts = accountDao.getAll();
    }

    public List<NLPModel> getModels() {
        if (models == null) {
            try {
                this.initiateModels();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return models;
    }

    /**
     * @return the file containing model information
     * @throws IOException if there is a problem reading the textfile
     */
    private File openFile() throws IOException {
        Path path = Paths.get(System.getProperty("user.dir") + "\\nlpModels.txt");
        if (Files.exists(path)) {
            return path.toFile();
        } else {
            return Files.createFile(path).toFile();
        }
    }

    private void initiateModels() throws IOException {
        models = new LinkedList<>();
        InputStream in = new FileInputStream(this.openFile());
        BufferedReader r = new BufferedReader(new InputStreamReader(in));

        String line = r.readLine();
        while (line != null) {
            NLPModel m = new NLPModel();
            List<String> values = this.getValues(line);
            m.setValues(values.get(0));

            for (String accountRelation : values.get(1).split(";")) {
                String account = accountRelation.split(":")[0];
                Double value = Double.valueOf(accountRelation.split(":")[1]);
                for (Account a : accounts) {
                    if (a.getAccountNo().equals(account)) {
                        m.addToCreditAccounts(a, value);
                        break;
                    }
                }
            }

            for (String accountRelation : values.get(2).split(";")) {
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
     * Writes the model information to the learning file
     * @param model the model to be saved
     */
    void writeToFile(NLPModel model) {
        try {
            File modelFile = this.openFile();
            FileWriter fw = new FileWriter(modelFile, true);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (String key : model.getValues().keySet()) {
                sb.append("{");
                sb.append(key);
                sb.append("}");
            }
            sb.append("][");
            String credit = this.getEntryString(model.getCredit());
            sb.append(credit);

            sb.append("][");

            String debit = this.getEntryString(model.getDebit());
            sb.append(debit);
            sb.append("]");

            fw.write(System.lineSeparator());
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getEntryString(Map<Account, Double> credOrDeb) {
        boolean first = true;
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Account, Double> m : credOrDeb.entrySet()) {
            if (!first) {
                result.append(";");
            }
            result.append(m.getKey().getAccountNo());
            result.append(":");
            result.append(m.getValue());
            first = false;
        }
        return result.toString();
    }
}
