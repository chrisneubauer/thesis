package de.cneubauer.util.config;

import de.cneubauer.util.enumeration.AppLang;
import de.cneubauer.util.enumeration.FerdLevel;
import de.cneubauer.util.enumeration.TessLang;
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

    static {
        Logger.getLogger(ConfigHelper.class).log(Level.INFO, "initiating configuration file on path: " + System.getProperty("user.dir") + "\\config.ini");
        setConfigFile(System.getProperty("user.dir"));
        if (config == null) {
            getConfig();
        }
    }
    private ConfigHelper() {
        Logger.getLogger(ConfigHelper.class).log(Level.INFO, "initiating configuration file on path: " + System.getProperty("user.dir") + "\\config.ini");
        setConfigFile(System.getProperty("user.dir"));
        if (config == null) {
            getConfig();
        }
    }

    private static void setConfigFile(String path) {
        try {
            configFile = Files.createFile(Paths.get(path + "\\config.ini")).toFile();
        } catch (IOException e) {
            configFile = new File(path + "\\config.ini");
        }
    }

    public static float getConfidenceRate() {
        if (config == null) {
            new ConfigHelper();
        }
        String rate = config.get(Cfg.CONFIDENCERATE.getValue());
        return Float.valueOf(rate);
    }

    public static String getDBUserName() {
        if (config == null) {
            new ConfigHelper();
        }
        return config.get(Cfg.DBUSER.getValue());
    }

    public static String getDBPassword() {
        if (config == null) {
            new ConfigHelper();
        }
        return config.get(Cfg.DBPASSWORD.getValue());
    }

    public static String getDBName() {
        if (config == null) {
            new ConfigHelper();
        }
        return config.get(Cfg.DBNAME.getValue());
    }

    public static String getDBServerName() {
        if (config == null) {
            new ConfigHelper();
        }
        return config.get(Cfg.DBSERVER.getValue());
    }

    public static int getDBPort() {
        if (config == null) {
            new ConfigHelper();
        }
        return Integer.valueOf(config.get(Cfg.DBPORT.getValue()));
    }

    public static AppLang getApplicationLanguage() {
        if (config == null) {
            new ConfigHelper();
        }
        return AppLang.valueOf(config.get(Cfg.APPLICATIONLANGUAGE.getValue()));
    }

    public static TessLang getTesseractLanguages() {
        if (config == null) {
            new ConfigHelper();
        }
        return TessLang.valueOf(config.get(Cfg.TESSERACTLANGUAGE.getValue()));
    }

    public static FerdLevel getPreferredFerdLevel() {
        if (config == null) {
            new ConfigHelper();
        }
        return FerdLevel.valueOf(config.get(Cfg.FERDPROFILE.getValue()));
    }

    public static boolean isDebugMode() {
        if (config == null) {
            new ConfigHelper();
        }
        try {
            return Boolean.valueOf(config.get(Cfg.DEBUG.getValue()));
        } catch (Exception e) {
            return false;
        }
    }

    private static String getValue(String property) {
        if (config == null) {
            new ConfigHelper();
        }
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

    public static void rewrite(Map<String, String> newConfiguration) {
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

    // searches for key and adjusts value
    // if no key could be found, a new line is added to the config file
    public static void addOrUpdate(String key, String value) {
        if (getValue(key) == null) {
            config.put(key, value);
            write(key, value);
        } else {
            config.replace(key, value);
        }
        rewrite(config);
    }

    // extends config file for one line
    private static void write(String key, String value) {
        try {
            if (configFile == null) {
                new ConfigHelper();
            }
            OutputStream out = new FileOutputStream(configFile, true);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
            w.write(key + "=" + value);
            w.newLine();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(ConfigHelper.class).log(Level.ERROR, "Unable to add configuration settings! Please delete config.ini to reset to default settings");
        }
    }

}
