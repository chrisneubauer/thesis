package de.cneubauer.domain.helper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 20.02.2017.
 * Abstract file helper class that facilitates the process of file reading
 */
public abstract class AbstractFileHelper {
    protected File file;
    protected List<String> list;

    protected void setFile(String fullFilePath) {
        try {
            file = Files.createFile(Paths.get(fullFilePath)).toFile();
        } catch (IOException e) {
            file = new File(fullFilePath);
        }
    }

    List<String> readFile() {
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

    abstract void init();

    public List<String> getValues() {
        List<String> result = new LinkedList<>();
        init();

        try {
            list = readFile();
            for (String line : list) {
                result.add(line.trim().toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
            list = new LinkedList<>();
        }
        return result;
    }
}
