package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by Christoph Neubauer on 02.02.2017.
 * This class enables writing models to the learning file
 */
public class ModelWriter {
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
     * Writes the model information to the learning file
     * @param model the model to be saved
     */
    void writeToFile(Model model) {
        try {
            File modelFile = this.openFile();
            FileWriter fw = new FileWriter(modelFile, true);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(model.getPosition());
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
