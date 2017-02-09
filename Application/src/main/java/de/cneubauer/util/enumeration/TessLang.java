package de.cneubauer.util.enumeration;

import java.util.Objects;

/**
 * Created by Christoph Neubauer on 07.11.2016.
 * Possible languages for tesseract
 */
public enum TessLang {
    ENGLISH("eng"),
    GERMAN ("deu"),
    ENGLISHANDGERMAN("deu+eng");

    private String value;

    private TessLang(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TessLang ofValue(String value) {
        if (Objects.equals(value, "eng")) {
            return ENGLISH;
        } else if (Objects.equals(value, "deu")) {
            return GERMAN;
        } else if (Objects.equals(value, "deu+eng")) {
            return ENGLISHANDGERMAN;
        } else {
            return null;
        }
    }
}
