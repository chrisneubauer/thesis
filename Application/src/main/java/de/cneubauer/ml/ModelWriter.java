package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public void writeToFile(Model model) {
        try {
            File modelFile = this.openFile();
            FileWriter fw = new FileWriter(modelFile, true);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(model.getPosition());
            sb.append("][");
            boolean first = true;
            for (Account a : model.getCredit()) {
                if (!first) {
                    sb.append(";");
                }
                sb.append(a.getAccountNo());
                first = false;
            }
            sb.append("][");
            first = true;
            for (Account a : model.getDebit()) {
                if (!first) {
                    sb.append(";");
                }
                sb.append(a.getAccountNo());
                first = false;
            }
            sb.append("]");

            fw.write(sb.toString());
            fw.write(System.lineSeparator());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
