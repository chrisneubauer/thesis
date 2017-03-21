package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Creditor;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.dao.CreditorDao;
import de.cneubauer.domain.dao.LegalPersonDao;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Implementation of LegalPersonDAO
 */
public class LegalPersonDaoImpl extends AbstractDao<LegalPerson> implements LegalPersonDao {

    public LegalPersonDaoImpl() {
        super(LegalPerson.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the legal person that should be saved
     */
    @Override
    public void onSave(LegalPerson entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        }
        entity.setModifiedDate(Date.valueOf(LocalDate.now()));
    }

    @Override
    public List<LegalPerson> getAllDebitors() {
        Logger.getLogger(this.getClass()).log(Level.INFO, "getting all debitors");

        CreditorDao creditorDao = new CreditorDaoImpl();
        List<Creditor> creditors = creditorDao.getAll();

        List<LegalPerson> legalPersonList = this.getAll();
        List<LegalPerson> result = new ArrayList<>(legalPersonList);
        for (LegalPerson p : legalPersonList) {
            for (Creditor c : creditors) {
                if (p.getId() == c.getLegalPerson().getId()) {
                    result.remove(p);
                }
            }
        }
        return result;
    }
}
