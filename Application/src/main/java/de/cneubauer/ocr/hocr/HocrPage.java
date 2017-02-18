package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents a page in the HOCR output format
 */
public class HocrPage extends HocrElement {
    private int pageNumber;

    HocrPage(String line) {
        String[] words = line.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains("id='")) {
                int length = words[i].length();
                this.id = words[i].substring(4, length - 1);
            }
            if (words[i].equals("bbox") && words.length > i+4) {
                this.position = words[i+1] + "+" + words[i+2] + "+" + words[i+3] + "+" + words[i+4];
                this.position = this.position.replace(";","");
            }
            if (words[i].equals("ppageno")) {
                String no = words[i + 1].split("'")[0];
                this.pageNumber = Integer.parseInt(no);
            }
        }
    }

    int getPageNumber() {
        return pageNumber;
    }

    public String findPosition(String name) {
        int length = 1;
        if (name.contains(" ")) {
            length = name.split(" ").length;
        }
        if (length > 1) {
            for (HocrElement area : this.getSubElements()) {
                HocrArea currentArea = (HocrArea) area;
                List<HocrElement> words = currentArea.getAllWordsInAreaAsList();
                //String[] positions = new String[length];
                List<HocrWord> foundWords = new LinkedList<>();
                for (int i = 0; i < words.size(); i++) {
                    if (words.size() > i + length - 1) {
                        boolean found = false;
                        if (words.get(i).getValue().toLowerCase().equals(name.split(" ")[0].toLowerCase())) {
                            found = true;
                        }
                        if (found) {
                            int minY = 4000, maxY = -1, minX = 4000, maxX = -1;
                            for (int j = 0; j < length; j++) {
                                if (words.get(i + j).getValue().toLowerCase().equals(name.split(" ")[j].toLowerCase())) {
                                    //positions[j] = words.get(i + j).getPosition();
                                    foundWords.add((HocrWord) words.get(i+j));
                                }
                            }
                            for (HocrWord word : foundWords) {
                                String[] position = word.getPosition().split("\\+");
                                int[] pos = new int[] {Integer.valueOf(position[0]), Integer.valueOf(position[1]), Integer.valueOf(position[2]), Integer.valueOf(position[3])};
                                minX = pos[0] < minX ? pos[0] : minX;
                                minY = pos[1] < minY ? pos[1] : minY;
                                maxX = pos[2] > maxX ? pos[2] : maxX;
                                maxY = pos[3] > maxY ? pos[3] : maxY;
                            }
                            return minX + "+" + minY + "+" + maxX + "+" + maxY;
                        }
                    }
                }
            }
        } else {
            for (HocrElement area : this.getSubElements()) {
                HocrArea currentArea = (HocrArea) area;
                List<HocrElement> words = currentArea.getAllWordsInAreaAsList();
                for (HocrElement word : words) {
                    if (word.getValue().equals(name)) {
                        return word.getPosition();
                    }
                }
            }
        }
        return null;
    }
}
