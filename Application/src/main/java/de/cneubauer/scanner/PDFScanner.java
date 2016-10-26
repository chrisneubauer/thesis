package de.cneubauer.scanner;

import com.snowtide.PDF;
import com.snowtide.pdf.Document;
import com.snowtide.pdf.OutputTarget;

/**
 * Created by Christoph on 17.08.2016.
 * Reads pdf by using snowtide framework
 * Results are unaccurate
 */
class PDFScanner {

    /*public String readPdf(String path) throws java.io.IOException {
        Document pdf = PDF.open(path);
        StringBuilder text = new StringBuilder(1024);
        pdf.pipe(new OutputTarget(text));
        pdf.close();
        return text.toString();
    }*/

    public String readPdf(String path) throws java.io.IOException {
        Document pdf = PDF.open(path);
        StringBuffer pdfText = new StringBuffer(1024);
        pdf.pipe(new OutputTarget(pdfText));
        pdf.close();
        return pdfText.toString();
    }
}
