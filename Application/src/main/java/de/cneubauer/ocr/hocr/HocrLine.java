package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents a line in the HOCR output format
 */
public class HocrLine extends HocrElement {
    private String id;
    //private String position;

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

    @Override
    public String getValue() {
        return this.getWordsAsString();
    }

    public List<HocrWord> getWords() {
        return words;
    }

    public String getPosition() {
        return position;
    }

    public HocrWord getWordByPosition(int[] position) {
        for (HocrWord word : this.words) {
            String[] stringPos = word.getPosition().split("\\+");
            // 0: startX, 1: startY, 2: endX, 3: endY
            int[] pos = new int[] {Integer.valueOf(stringPos[0]), Integer.valueOf(stringPos[1]), Integer.valueOf(stringPos[2]), Integer.valueOf(stringPos[3])};

            boolean xStartsEarlier = pos[0] <= position[0];
            boolean yStartsEarlier = pos[1] <= position[1];
            boolean xEndsLater = pos[2] >= position[2];
            boolean yEndsLater = pos[3] >= position[3];

            if (xStartsEarlier && yStartsEarlier && xEndsLater && yEndsLater) {
                return word;
            }
        }
        return null;
    }

    public String getWordsAsString() {
        StringBuilder sb = new StringBuilder();
        for (HocrWord word : this.words) {
            sb.append(word.getValue());
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public HocrWord getWordsByPosition(int[] linePos) {
        List<HocrWord> possibleWords = new LinkedList<>();
        int xStart = linePos[0];
        int yStart = linePos[1];
        int xEnd = linePos[2];
        int yEnd = linePos[3];
        for (HocrWord word : this.words) {
            String[] stringPos = word.getPosition().split("\\+");
            // 0: startX, 1: startY, 2: endX, 3: endY
            int[] pos = new int[] {Integer.valueOf(stringPos[0]), Integer.valueOf(stringPos[1]), Integer.valueOf(stringPos[2]), Integer.valueOf(stringPos[3])};

            boolean xStartsEarlier = xStart <= pos[0];
            boolean yStartsEarlier = yStart <= pos[1];
            boolean xEndsLater = xEnd >= pos[2];
            boolean yEndsLater = yEnd >= pos[3];

            if (xStartsEarlier && yStartsEarlier && xEndsLater && yEndsLater) {
                possibleWords.add(word);
            }
        }

        if (possibleWords.size() > 0) {
            HocrWord combinedWord = new HocrWord();
            StringBuilder sb = new StringBuilder();
            for (HocrWord word : possibleWords) {
                sb.append(word.getValue() + " ");
            }
            combinedWord.setValue(sb.toString());
            String pos = linePos[0] + "+" + linePos[1] + "+" + linePos[2] + "+" + linePos[3];
            combinedWord.setPosition(pos);
            return combinedWord;
        }
        return null;
    }
}
