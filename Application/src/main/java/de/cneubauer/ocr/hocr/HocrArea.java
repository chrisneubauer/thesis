package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents an area in the HOCR output format
 */
public class HocrArea extends HocrElement {
    HocrArea(String line) {
        this.position = "";
        String[] words = line.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains("id='")) {
                int length = words[i].length();
                this.id = words[i].substring(4, length - 1);
            }
            if (words[i].contains("title=")) {
                for (int j = 1; j <= 4; j++) {
                    this.position += words[i+j] + "+";
                }
                this.position = this.position.replaceAll("[^+0-9]", "");
                this.position = position.substring(0, position.length() -1);
            }
        }
    }

    public String getId() {
        return id;
    }

    public List<String> getAllWordsInArea() {
        List<String> result = new LinkedList<>();
        for (HocrElement p : this.getSubElements()) {
            for (HocrElement line : p.getSubElements()) {
                for (HocrElement word : line.getSubElements()) {
                    result.add(word.getValue());
                }
            }
        }
        return result;
    }

    List<HocrElement> getAllWordsInAreaAsList() {
        List<HocrElement> result = new LinkedList<>();
        for (HocrElement p : this.getSubElements()) {
            for (HocrElement line : p.getSubElements()) {
                for (HocrElement word : line.getSubElements()) {
                    result.add(word);
                }
            }
        }
        return result;
    }
}
