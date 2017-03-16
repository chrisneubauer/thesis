package de.cneubauer.util.enumeration;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 * State of Scan Result
 * OK: Invoice successfully scanned, enough information found for saving in db
 * ISSUE: Invoice successfully scanned, issues with information which make it impossible to save
 * ERROR: Issues during scan, has to be reviewed manually
 */
public enum ScanStatus {
    OK,
    ERROR,
    PROFORMAINVOICE,
    CREDITNOTE,
    INVOICE,
    ISSUE
}
