package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents a line in the HOCR output format
 */
public class HocrLine {
    private String id;
    private String position;

    private List<HocrWord> words;

    public HocrLine(String line) {
        String[] parts = line.split("<span");
        this.words = new LinkedList<>();
        this.position = "";
        for (String part : parts) {
            if(part.contains("class='ocr_line'")) {
                String[] words = part.split(" ");
                for (int i = 0; i < words.length; i++) {
                    if (words[i].contains("id='")) {
                        int length = words[i].length();
                        this.id = words[i].substring(4, length - 1);
                    } else if (words[i].contains("title=")) {
                        for (int j = 1; j <= 4; j++) {
                            this.position += words[i+j] + "+";
                        }
                        this.position = this.position.replaceAll("[^+0-9]", "");
                        //this.position = this.position.replace(";", "");
                        this.position = this.position.substring(0, position.length() -1);
                    }
                }
            } else if (part.contains("class='ocrx_word'")) {
                HocrWord word = new HocrWord(part);
                this.words.add(word);
            }
        }
    }

    public List<HocrWord> getWords() {
        return words;
    }
}
