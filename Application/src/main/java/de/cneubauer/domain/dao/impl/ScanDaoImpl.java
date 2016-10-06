package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.ScanDao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Implementation of ScanDao
 */
public class ScanDaoImpl extends AbstractDao<Scan> implements ScanDao {
    public ScanDaoImpl() {
        super(Scan.class);
    }

    // hook-method before saving
    @Override
    public void onSave(Scan entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        entity.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
    }
}
