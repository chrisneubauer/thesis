package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Address;
import de.cneubauer.domain.dao.AddressDao;

import java.sql.Date;
import java.time.LocalDate;

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
            entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        }
        entity.setModifiedDate(Date.valueOf(LocalDate.now()));
    }
}
