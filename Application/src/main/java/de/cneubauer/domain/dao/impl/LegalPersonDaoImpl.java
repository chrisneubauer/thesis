package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.dao.LegalPersonDao;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaQuery;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Implementation of LegalPersonDAO
 */
public class LegalPersonDaoImpl extends AbstractDao implements LegalPersonDao {

    public LegalPersonDaoImpl() {
        super();
    }

    @Override
    public LegalPerson getById(int id) {
        LegalPerson result;

        this.getSession().beginTransaction();
        result = this.getSession().get(LegalPerson.class, id);
        return result;
    }

    @Override
    public List<LegalPerson> getAll() {
        CriteriaQuery<LegalPerson> query = this.getSession().getCriteriaBuilder().createQuery(LegalPerson.class);
        query.select(query.from(LegalPerson.class));

        Query<LegalPerson> q2 = this.getSession().createQuery(query);
        return q2.getResultList();
        //query.select(query.from(clazz));
        //Query<T> q = getCurrentSession().createQuery(query);  // CriteriaQueryTypeQueryAdapter instance
        //q.setCacheable(cache); // execute AbstractProducedQuery#setCacheable
//    return q.getResultList(); // CriteriaQueryTypeQueryAdapter#getResultList(), cache not works
        //return q.list();
    }

    @Override
    public void save(LegalPerson p) {
        this.getSession().beginTransaction();
        p.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.getSession().save(p);
        this.getSession().getTransaction().commit();
    }
}
