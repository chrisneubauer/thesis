package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.dao.RecordDao;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO implementation of business object Record
 */
public class RecordDaoImpl extends AbstractDao<Record> implements RecordDao {
    public RecordDaoImpl() {
        super(Record.class);
    }

    @Override
    protected void onSave(Record entity) {
    }
}
