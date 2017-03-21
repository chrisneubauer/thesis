package de.cneubauer.ocr.hocr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Represents the document in the HOCR output format
 */
public class HocrDocument extends HocrElement {
    private List<HocrPage> pages;

    public HocrDocument(String document) {
        document = this.removeHtmlTags(document);
        String[] lines = document.split("\\r?\\n");
        this.pages = new LinkedList<>();
        int currentPage = -1;
        String currentArea = "";
        String currentParagraph = "";
        for (String line : lines) {
            if (line.contains("<div class='ocr_page'")) {
                HocrPage page = new HocrPage(line);
                page.setParent(this);
                currentPage++;
                this.pages.add(page);
            }
            if (line.contains("<div class='ocr_carea'")) {
                HocrArea area = new HocrArea(line);
                area.setParent(this.getPage(currentPage));

                this.getPage(currentPage).addSubElement(area);
                currentArea = area.getId();
            }
            if (line.contains("<p class='ocr_par")) {
                HocrParagraph paragraph = new HocrParagraph(line);
                paragraph.setParent(this.getPage(currentPage).getSubElement(currentArea));
                this.getPage(currentPage).getSubElement(currentArea).addSubElement(paragraph);
                currentParagraph = paragraph.getId();
            }
            if (line.contains("<span class='ocr_line'")) {
                HocrLine hocrLine = new HocrLine(line);
                hocrLine.setParent(this.getPage(currentPage).getSubElement(currentArea).getSubElement(currentParagraph));
                this.getPage(currentPage).getSubElement(currentArea).getSubElement(currentParagraph).addSubElement(hocrLine);
            }
        }
    }

    private String removeHtmlTags(String document) {
        document = document.replace("<em>", "");
        document = document.replace("</em>", "");
        document = document.replace("<strong>", "");
        document = document.replace("</strong>", "");
        return document;
    }

    public HocrPage getPage(int pageNumber) {
        for (HocrPage page : pages) {
            if (page.getPageNumber() == pageNumber) {
                return page;
            }
        }
        return null;
    }

    public List<HocrPage> getPages() {
        return pages;
    }

    public List<String> getDocumentAsList() {
        List<String> lines = new LinkedList<>();
        for (HocrElement area : this.getPage(0).getSubElements()) {
            for (HocrElement paragraph : area.getSubElements()) {
                for (HocrElement line : paragraph.getSubElements()) {
                    HocrLine currentLine = (HocrLine) line;
                    lines.add(currentLine.getWordsAsString());
                }
            }
        }
        return lines;
    }
}
