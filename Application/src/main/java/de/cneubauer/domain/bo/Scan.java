package de.cneubauer.domain.bo;

import java.sql.Timestamp;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Business Object for table scan
 */
public class Scan {
    private int id;
    private Byte[] file;
    private Invoice invoiceInformation;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
}
