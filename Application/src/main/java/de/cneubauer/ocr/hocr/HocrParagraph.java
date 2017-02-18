package de.cneubauer.ocr.hocr;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents a paragraph in the HOCR output format
 */
public class HocrParagraph extends HocrElement {
    HocrParagraph(String line) {
        this.position = "";
        String[] words = line.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.contains("id='")) {
                int length = word.length();
                this.id = word.substring(4, length - 1);
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
}
