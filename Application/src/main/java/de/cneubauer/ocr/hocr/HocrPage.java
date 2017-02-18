package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents a page in the HOCR output format
 */
public class HocrPage {
    private int pageNumber;
    private String id;
    private List<HocrArea> areas;

    public HocrPage(String line) {
        if (line.contains("ppageno")) {
            String[] words = line.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].equals("ppageno")) {
                    String no = words[i+1].split("'")[0];
                    this.pageNumber = Integer.parseInt(no);
                }
                if (words[i].contains("id='")) {
                    int length = words[i].length();
                    this.id = words[i].substring(4, length - 1);
                }
            }
        }
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public List<HocrArea> getAreas() {
        return this.areas;
    }

    public HocrArea getArea(String areaNumber) {
        for (HocrArea area : this.areas) {
            if (area.getId().equals(areaNumber)) {
                return area;
            }
        }
        return null;
    }

    public void addArea(HocrArea area) {
        if (this.areas == null) {
            this.areas = new LinkedList<>();
        }
        this.areas.add(area);
    }

    public HocrArea getAreaByPosition(int[] position) {
        for (HocrArea area : this.areas) {
            String[] stringPos = area.getPosition().split("\\+");
            // 0: startX, 1: startY, 2: endX, 3: endY
            int[] pos = new int[] {Integer.valueOf(stringPos[0]), Integer.valueOf(stringPos[1]), Integer.valueOf(stringPos[2]), Integer.valueOf(stringPos[3])};

            boolean xStartsEarlier = pos[0] <= position[0];
            boolean yStartsEarlier = pos[1] <= position[1];
            boolean xEndsLater = pos[2] >= position[2];
            boolean yEndsLater = pos[3] >= position[3];

            if (xStartsEarlier && yStartsEarlier && xEndsLater && yEndsLater) {
                return area;
            }
        }
        return null;
    }

    public String findPosition(String name) {
        int length = 1;
        if (name.contains(" ")) {
            length = name.split(" ").length;
        }
        if (length > 1) {
            for (HocrArea area : this.areas) {
                List<HocrWord> words = area.getAllWordsInAreaAsList();
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
                                    foundWords.add(words.get(i+j));
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
                            /*for (String pos : positions) {
                                String[] position = pos.split("\\+");
                                // 0: startX, 1: startY, 2: endX, 3: endY
                                int[] pos = new int[] {Integer.valueOf(position[0]), Integer.valueOf(position[1]), Integer.valueOf(position[2]), Integer.valueOf(position[3])};
                                /*String startXPosition = pos.split("\\+")[0];
                                String startYPosition = pos.split("\\+")[1];
                                String endXPosition = pos.split("\\+")[2];
                                String endYPosition = pos.split("\\+")[3];
                                minX = Integer.valueOf(startXPosition) < minX ? Integer.valueOf(startXPosition) : minX;
                                minY = Integer.valueOf(startYPosition) < minY ? Integer.valueOf(startYPosition) : minY;
                                maxX = Integer.valueOf(endXPosition) > maxX ? Integer.valueOf(endXPosition) : maxX;
                                maxY = Integer.valueOf(endYPosition) > maxY ? Integer.valueOf(endYPosition) : maxY;
                            }*/
                            return minX + "+" + minY + "+" + maxX + "+" + maxY;
                        }
                    }
                }
            }
        } else {
            for (HocrArea area : this.areas) {
                List<HocrWord> words = area.getAllWordsInAreaAsList();
                for (HocrWord word : words) {
                    if (word.getValue().equals(name)) {
                        return word.getPosition();
                    }
                }
            }
        }
        return null;
    }
}
