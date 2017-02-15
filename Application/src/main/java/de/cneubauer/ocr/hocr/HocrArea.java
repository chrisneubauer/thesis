package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents an area in the HOCR output format
 */
public class HocrArea {
    private String id;
    private List<HocrParagraph> paragraphs;
    private String position;

    public HocrArea(String line) {
        this.paragraphs = new LinkedList<>();
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

    public void addParagraph(HocrParagraph paragraph) {
        this.paragraphs.add(paragraph);
    }

    public HocrParagraph getPararaph(String currentParagraph) {
        for (HocrParagraph paragraph : paragraphs) {
            if (paragraph.getId().equals(currentParagraph)) {
                return  paragraph;
            }
        }
        return null;
    }

    public List<HocrParagraph> getParagraphs() {
        return this.paragraphs;
    }

    public List<String> getAllWordsInArea() {
        List<String> result = new LinkedList<>();
        for (HocrParagraph p : this.getParagraphs()) {
            for (HocrLine line : p.getLines()) {
                for (HocrWord word : line.getWords()) {
                    result.add(word.getValue());
                }
            }
        }
        return result;
    }
}
