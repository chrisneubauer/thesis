package de.cneubauer.util;

import java.io.*;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Used for generating sql sequences
 */
public class SQLGenerator {
    public void createAccountSQL() {
        File text = new File("..\\Accounting\\KontenUTF8.txt");
        File output = new File("..\\Accounting\\InsertAccountData.sql");
        try {
            InputStream in = new FileInputStream(text);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line = r.readLine();
            BufferedWriter w = new BufferedWriter(new FileWriter(output));
            while (line != null) {
                System.out.println(line);
                //String[] parts = line.split(" ");
                //int completeLines = parts.length - 1;
                String accNo = line.substring(0, 4); //parts[0];
                String accName = line.substring(5, line.length() - 2);// "";
                /*for (int i = 1; i < completeLines; i++) {
                    accName += parts[i];
                }*/
                String accType = line.substring(line.length() - 1, line.length());//parts[completeLines];
                String sqlString = "INSERT INTO Account (AccountNo, Name, AccountType_Id) VALUES (" +
                        "\"" + accNo + "\", \"" + accName + "\", " + accType + ");";
                System.out.println("Writing: " + sqlString);
                w.write(sqlString);
                w.newLine();
                line = r.readLine();
            }
            w.flush();
            w.close();
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
