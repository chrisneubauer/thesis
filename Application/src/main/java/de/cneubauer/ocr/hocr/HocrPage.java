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
}
