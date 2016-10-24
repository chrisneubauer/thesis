package de.cneubauer.util.config;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christoph Neubauer on 24.10.2016.
 * Manages configuration file
 */
public final class ConfigHelper {
    private static File configFile;
    private static Map<String, String> config;

    private ConfigHelper() {
        Logger.getLogger(ConfigHelper.class).log(Level.INFO, "initiating configuration file on path: " + System.getProperty("user.dir") + "\\config.ini");
        setConfigFile(System.getProperty("user.dir"));
    }

    private static void setConfigFile(String path) {
        try {
            configFile = Files.createFile(Paths.get(path + "\\config.ini")).toFile();
        } catch (IOException e) {
            configFile = new File(path + "\\config.ini");
        }
    }

    public static String getValue(String property) {
        return config.get(property);
    }

    public static Map<String, String> getConfig() {
        if (configFile == null) {
            new ConfigHelper();
        }
        Map<String, String> properties = new HashMap<>();
        try {
            InputStream in = new FileInputStream(configFile);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line = r.readLine();
            while (line != null) {
                properties.put(line.split("=")[0], line.split("=")[1]);
                line = r.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        config = properties;
        return config;
    }

    public static void write(Map<String, String> newConfiguration) {
        try {
            OutputStream out = new FileOutputStream(configFile);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
            for (Map.Entry<String, String> entry : newConfiguration.entrySet()) {
                w.write(entry.getKey() + "=" + entry.getValue());
                w.newLine();
            }
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(ConfigHelper.class).log(Level.ERROR, "Unable to rewrite configuration settings! Please delete config.ini to reset to default settings");
        }
    }
}
