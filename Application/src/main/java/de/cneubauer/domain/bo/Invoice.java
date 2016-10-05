package de.cneubauer.domain.bo;

import java.sql.Timestamp;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Busines Object for table Invoice
 */
public class Invoice {
    private int id;
    private LegalPerson debitor;
    private LegalPerson creditor;
    private Timestamp date;
    private double moneyVale;
    private boolean hasSkonto;
    private double skonto;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
}
