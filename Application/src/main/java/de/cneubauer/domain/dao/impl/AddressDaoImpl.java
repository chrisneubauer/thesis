package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Address;
import de.cneubauer.domain.dao.AddressDao;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by Christoph Neubauer on 26.10.2016.
 * DAO implementation of business object Address
 */
public class AddressDaoImpl extends AbstractDao<Address> implements AddressDao {
    public AddressDaoImpl() {
        super(Address.class);
    }

    @Override
    public void onSave(Address entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        entity.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
    }
}
