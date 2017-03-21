package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Creditor;
import de.cneubauer.domain.dao.CreditorDao;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 * Implementation of the CreditorDao interface
 */
public class CreditorDaoImpl extends AbstractDao<Creditor> implements CreditorDao {
    public CreditorDaoImpl() {
        super(Creditor.class);
    }

    @Override
    protected void onSave(Creditor entity) {

    }
}
