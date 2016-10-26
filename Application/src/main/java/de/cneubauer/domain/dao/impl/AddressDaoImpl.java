package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Address;
import de.cneubauer.domain.dao.AddressDao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Christoph Neubauer on 26.10.2016.
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
