package de.cneubauer.util.config;

/**
 * Created by Christoph Neubauer on 07.11.2016.
 * Enum storing numerous configuration keys
 */
public enum Cfg {

    DBNAME("databaseName"),
    DBSERVER("databaseServerName"),
    DBUSER("databaseUsername"),
    DBPORT("databasePort"),
    DBPASSWORD("databasePassword"),
    APPLICATIONLANGUAGE("applicationLanguage"),
    TESSERACTLANGUAGE("tesseractLanguage"),
    CONFIDENCERATE("confidenceRate"),
    FERDPROFILE("defaultFerdProfile"),
    DEBUG("debugMode"),
    OCRENGINE("ocrEngine");

    private String value;

    Cfg(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
