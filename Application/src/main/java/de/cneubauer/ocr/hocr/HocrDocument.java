package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents the document in the HOCR output format
 */
public class HocrDocument {
    private List<HocrPage> pages;

    public HocrDocument(String document) {
        String[] lines = document.split("\\n");
        this.pages = new LinkedList<>();
        int currentPage = -1;
        String currentArea = "";
        String currentParagraph = "";
        for (String line : lines) {
            if (line.contains("<div class='ocr_page'")) {
                HocrPage page = new HocrPage(line);
                currentPage++;
                this.pages.add(page);
            }
            if (line.contains("<div class='ocr_carea'")) {
                HocrArea area = new HocrArea(line);

                this.getPage(currentPage).addArea(area);
                currentArea = area.getId();
            }
            if (line.contains("<p class='ocr_par")) {
                HocrParagraph paragraph = new HocrParagraph(line);
                this.getPage(currentPage).getArea(currentArea).addParagraph(paragraph);
                currentParagraph = paragraph.getId();
            }
            if (line.contains("<span class='ocr_line'")) {
                HocrLine hocrLine = new HocrLine(line);
                this.getPage(currentPage).getArea(currentArea).getPararaph(currentParagraph).addLine(hocrLine);
            }
        }
    }

    public HocrPage getPage(int pageNumber) {
        for (HocrPage page : pages) {
            if (page.getPageNumber() == pageNumber) {
                return page;
            }
        }
        return null;
    }
}
