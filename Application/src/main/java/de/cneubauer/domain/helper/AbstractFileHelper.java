package de.cneubauer.domain.helper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Christoph Neubauer on 20.02.2017.
 */
public class AbstractFileHelper {
    protected static File file;
    protected static List<String> list;

    protected static void setFile(String fullFilePath) {
        try {
            file = Files.createFile(Paths.get(fullFilePath)).toFile();
        } catch (IOException e) {
            file = new File(fullFilePath);
        }
    }

    static List<String> readFile() {
        List<String> contents = new LinkedList<>();
        try {
            InputStream in = new FileInputStream(file);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line = r.readLine();
            while (line != null) {
                contents.add(line);
                line = r.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents;
    }
}
