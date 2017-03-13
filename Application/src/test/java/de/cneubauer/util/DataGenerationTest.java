package de.cneubauer.util;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Random;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Christoph on 07.03.2017.
 * This class is responsible for generating several invoice documents that are used for testing purposes
 */
public class DataGenerationTest {
    private String temp1;
    private String temp2;
    private String temp3;

    private String[] debitors = {
            "Herr Heribert Becker", "Herr Marcel Heitmann", "Herr Hildebrant Heidler", "Herr Albwin Schutz", "Herr Leo Traeger", "Herr Willi Grothmann",
            "Herr Ferdinand Bitterlich", "Herr Lorenz Licht", "Herr Leo May", "Herr Gerhard Amann", "Herr Ulrich Rheingold", "Herr Willi Hersh",
            "Herr Paul Hamburger", "Herr Rupprecht Schillinger", "Herr Torben Schnabel", "Herr Eckhardt Eberl", "Herr Werner Kirsch", "Herr Leo Mahlau",
            "Herr Klemens Egner", "Herr Ole Gutheil", "Frau Juna Engelberger", "Frau Augusta Görder", "Frau Ilse Veil", "Frau Lydia Wertheim",
            "Frau Gerlind Wachtel", "Frau Teresa Ellmenreich", "Frau Merle Egner", "Frau Valerie Reeder", "Frau Ursel Fährmann", "Frau Madleen Danzig",
            "Frau Emely Fischinger", "Frau Verena Rohmer", "Frau Elsbeth Hirsch", "Frau Irina Meyer", "Frau Genoveva Hering", "Frau Madeleine Joachim",
            "Frau Leonore Thälmann", "Frau Ann-Katrin Friedeberg", "Frau Amelie Gmehling", "Frau Wilma Schmitt"
    };
    private String[] creditors = {"ABC GmbH", "DEF GmbH", "GHI GmbH", "JKL GmbH", "MNO AG", "PQR AG", "STU AG", "VWX AG", "YZA GmbH"};

    private String[] positions = {
        "Abgassonde", "Achse", "Alufelge", "Anlasser", "Auspuff", "Außenspiegel", "Autoradio",
            "Benzineinspritzanlage", "Bremsanlage", "Bremsbacken", "Bremse", "Bremssattel", "Bremsscheiben", "Bremstrommel",
            "Gasfedern", "Getriebe", "Heizung", "Katalysator", "Klimaanlage", "Kolben", "Kotflügel", "Kühler", "Kupplung",
            "Lambdasonde", "Lichtmaschine", "Motoren", "Ölpumpe", "Ölwanne", "Rücklicht", "Scheinwerfer", "Standheizung",
            "Starter", "Stossfänger", "Stoßdämpfer", "Stoßstange", "Vergaser", "Zylinderkopf"
    };

    @Before
    public void setUp() {
        String template1 = "D:\\Christoph\\Documents\\1_Studium\\1.2_Master\\MA\\Thesis\\Application\\src\\test\\resources\\data\\templates\\template1.xml";
        String template2 = "D:\\Christoph\\Documents\\1_Studium\\1.2_Master\\MA\\Thesis\\Application\\src\\test\\resources\\data\\templates\\template2.xml";
        String template3 = "D:\\Christoph\\Documents\\1_Studium\\1.2_Master\\MA\\Thesis\\Application\\src\\test\\resources\\data\\templates\\template3.xml";
        File temp1File = new File(template1);
        File temp2File = new File(template2);
        File temp3File = new File(template3);

        try {
            temp1 = FileUtils.readFileToString(temp1File, "UTF-8");
            temp2 = FileUtils.readFileToString(temp2File, "UTF-8");
            temp3 = FileUtils.readFileToString(temp3File, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerationOfInvoices() {
        String outputPath = "D:\\Christoph\\Documents\\1_Studium\\1.2_Master\\MA\\Thesis\\Application\\src\\test\\resources\\data\\output\\";
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 100; j++) {
                String newDocument;
                switch (i) {
                    case 1: newDocument = temp1; break;
                    case 2: newDocument = temp2; break;
                    case 3: newDocument = temp3; break;
                    default: newDocument = "";
                }

                if (newDocument.length() > 0) {
                    newDocument = newDocument.replace("CreditorN", this.getRandomCreditor());
                    newDocument = newDocument.replace("DebitorN", this.getRandomDebitor());
                    GregorianCalendar invoiceDate = this.getRandomDate();
                    newDocument = newDocument.replace("invoiceNumber", this.getRandomInvoiceNumber());
                    newDocument = newDocument.replace("invDate", this.calendarToGermanDate(invoiceDate));

                    String pos1 = this.getRandomPosition();
                    String pos2 = this.getRandomPosition(pos1);
                    String pos3 = this.getRandomPosition(pos1, pos2);
                    newDocument = newDocument.replace("Pos1pos", pos1);
                    newDocument = newDocument.replace("Pos2pos", pos2);
                    newDocument = newDocument.replace("Pos3pos", pos3);

                    Random r1 = new Random();
                    Random r2 = new Random();
                    Random r3 = new Random();
                    double pos1v = Math.floor((0.1 + (400 - 0.1) * r1.nextDouble()) * 100) / 100;
                    double pos2v = Math.floor((0.1 + (400 - 0.1) * r2.nextDouble()) * 100) / 100;
                    double pos3v = Math.floor((0.1 + (400 - 0.1) * r3.nextDouble()) * 100) / 100;

                    double sum1;
                    double sum2;
                    double sum3;

                    if (i < 3) {
                        newDocument = newDocument.replace("Pos1v", new DecimalFormat("#.##").format(pos1v));
                        newDocument = newDocument.replace("Pos2v", new DecimalFormat("#.##").format(pos2v));
                        newDocument = newDocument.replace("Pos3v", new DecimalFormat("#.##").format(pos3v));

                        int pos1a = r1.nextInt(20);
                        int pos2a = r2.nextInt(20);
                        int pos3a = r3.nextInt(20);

                        newDocument = newDocument.replace("Pos1a", String.valueOf(pos1a));
                        newDocument = newDocument.replace("Pos2a", String.valueOf(pos2a));
                        newDocument = newDocument.replace("Pos3a", String.valueOf(pos3a));

                        newDocument = newDocument.replace("Pos1s", String.valueOf(pos1v * pos1a));
                        newDocument = newDocument.replace("Pos2s", String.valueOf(pos2v * pos2a));
                        newDocument = newDocument.replace("Pos3s", String.valueOf(pos3v * pos3a));

                        sum1 = pos1v * pos1a;
                        sum2 = pos2v * pos2a;
                        sum3 = pos3v * pos3a;
                    } else {
                        newDocument = newDocument.replace("Pos1s", String.valueOf(pos1v));
                        newDocument = newDocument.replace("Pos2s", String.valueOf(pos2v));
                        newDocument = newDocument.replace("Pos3s", String.valueOf(pos3v));

                        sum1 = pos1v;
                        sum2 = pos2v;
                        sum3 = pos3v;

                        invoiceDate.add(Calendar.DAY_OF_MONTH, +5);
                        newDocument = newDocument.replace("delDate", this.calendarToGermanDate(invoiceDate));
                    }

                    double netValue = Math.floor((sum1 + sum2 + sum3) * 100) / 100;
                    newDocument = newDocument.replace("netValue", new DecimalFormat("#.##").format(netValue));
                    newDocument = newDocument.replace("taxValue", new DecimalFormat("#.##").format(netValue * 0.19));
                    newDocument = newDocument.replace("totValue", new DecimalFormat("#.##").format(netValue + netValue * 0.19));
                }

                try {
                    File outFile = new File(outputPath + "template" + i + "_generated" + j + ".xml");
                    FileUtils.writeStringToFile(outFile, newDocument, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String calendarToGermanDate(GregorianCalendar gc) {
        StringBuilder dateBuilder = new StringBuilder();
        if (gc.get(Calendar.DAY_OF_MONTH) < 10) {
            dateBuilder.append("0");
        }
        dateBuilder.append(gc.get(Calendar.DAY_OF_MONTH) + 1).append(".");

        if (gc.get(Calendar.MONTH) < 10) {
            dateBuilder.append("0");
        }
        dateBuilder.append(gc.get(Calendar.MONTH)).append(".");
        dateBuilder.append(gc.get(Calendar.YEAR));
        return dateBuilder.toString();
    }

    private GregorianCalendar getRandomDate() {
        GregorianCalendar gc = new GregorianCalendar();
        int year = randomNumberBetween(2012, 2016);
        gc.set(Calendar.YEAR, year);
        int dayOfYear = randomNumberBetween(1, gc.getActualMaximum(Calendar.DAY_OF_YEAR));
        gc.set(Calendar.DAY_OF_YEAR, dayOfYear);
        return gc;
    }

    private String getRandomInvoiceNumber() {
        return String.valueOf(this.randomNumberBetween(10000000, 99999999));
    }

    private int randomNumberBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    private String getRandomPosition() {
        Random random = new Random();
        int index = random.nextInt(positions.length);
        return positions[index];
    }

    private String getRandomPosition(String filter1) {
        Random random = new Random();
        int index = random.nextInt(positions.length);
        String position = positions[index];

        while (position.equals(filter1)) {
            index = random.nextInt(positions.length);
            position = positions[index];
        }
        return position;
    }

    private String getRandomPosition(String filter1, String filter2) {
        Random random = new Random();
        int index = random.nextInt(positions.length);
        String position = positions[index];

        while (position.equals(filter1) || position.equals(filter2)) {
            index = random.nextInt(positions.length);
            position = positions[index];
        }
        return position;
    }


    private String getRandomDebitor() {
        Random random = new Random();
        int index = random.nextInt(debitors.length);
        return debitors[index];
    }

    private String getRandomCreditor() {
        Random random = new Random();
        int index = random.nextInt(creditors.length);
        return creditors[index];
    }


}
