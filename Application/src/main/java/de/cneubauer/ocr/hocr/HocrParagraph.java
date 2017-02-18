package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents a paragraph in the HOCR output format
 */
public class HocrParagraph extends HocrElement {
    private String id;
    private List<HocrLine> lines;
    private String position;

    public HocrParagraph(String line) {
        this.lines = new LinkedList<>();
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

    public String getId() {
        return id;
    }

    public void addLine(HocrLine line) {
        this.lines.add(line);
    }

    public List<HocrLine> getLines() {
        return lines;
    }

    public String getPosition() {
        return position;
    }

    public HocrLine getLineByPosition(int[] position) {
        for (HocrLine line : this.lines) {
            String[] stringPos = line.getPosition().split("\\+");
            // 0: startX, 1: startY, 2: endX, 3: endY
            int[] pos = new int[] {Integer.valueOf(stringPos[0]), Integer.valueOf(stringPos[1]), Integer.valueOf(stringPos[2]), Integer.valueOf(stringPos[3])};

            boolean xStartsEarlier = pos[0] <= position[0];
            boolean yStartsEarlier = pos[1] <= position[1];
            boolean xEndsLater = pos[2] >= position[2];
            boolean yEndsLater = pos[3] >= position[3];

            if (xStartsEarlier && yStartsEarlier && xEndsLater && yEndsLater) {
                return line;
            }
        }
        return null;
    }
}
