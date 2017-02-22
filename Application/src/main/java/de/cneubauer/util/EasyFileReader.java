package de.cneubauer.util;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 22.02.2017.
 * Convenience class to easily read files.
 */
public class EasyFileReader {
    private File file;
    private List<String> lines;

    public EasyFileReader(String path) {
        this.file = new File(path);
        this.lines = null;
    }

    private void readLines() {
        this.lines = new LinkedList<>();
        try {
            InputStream in = new FileInputStream(file);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line = r.readLine();
            while (line != null) {
                this.lines.add(line);
                line = r.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getLines() {
        if (this.lines == null) {
            this.readLines();
        }
        return this.lines;
    }
}
