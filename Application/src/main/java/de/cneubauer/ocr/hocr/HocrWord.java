package de.cneubauer.ocr.hocr;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents a word in the HOCR output format
 */
public class HocrWord extends HocrElement {
    private String id;
   // private String value;
    //private String position;

    public HocrWord() {}

    public HocrWord(String line) {
        String[] words = line.split(" ");
        this.position = "";
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.contains("</span>")) {
                try {
                    String temp = word.split("</span>")[0];
                    if (temp.contains("<strong>")) {
                        temp = temp.split("<strong>")[1];
                        this.value = temp.split("</strong>")[0];
                    } else {
                        this.value = temp.split(">")[1];
                    }
                } catch (Exception e) {
                    Logger.getLogger(this.getClass()).log(Level.ERROR, "Error parsing line: " + word + "\n Using empty value");
                    value = "";
                }
            }
            if (word.contains("id='")) {
                int length = word.length();
                this.id = word.substring(4, length - 1);
            }
            if (words[i].contains("title=")) {
                for (int j = 1; j <= 4; j++) {
                    this.position += words[i+j] + "+";
                }
                this.position = this.position.replaceAll("[^+0-9]", "");
                //this.position = this.position.replace(";", "");
                this.position = position.substring(0, position.length() -1);
            }
        }
        this.replaceHTMLCodes();
    }

    /**
     * Replaces all html code occurrences in the value
     */
    private void replaceHTMLCodes() {
        this.value = this.value.replace("&amp;", "&");
        this.value = this.value.replace("&euro;", "€");
        this.value = this.value.replace("&gt;" , ">");
        this.value = this.value.replace("&lt;" , "<");
        this.value = this.value.replace("&sect;", "§");
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }
}
