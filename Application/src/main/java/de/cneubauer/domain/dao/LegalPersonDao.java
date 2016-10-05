package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.LegalPerson;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Data-Access-Object for LegalPerson
 */
public class LegalPersonDao {
    private Session session;
    private SessionFactory sessionFactory;
    private Configuration config;

    public LegalPersonDao() {
        this.config = new Configuration();
        File configFile = new File("src/main/hibernate.cfg.xml");
        this.config.configure(configFile);
        this.sessionFactory = this.config.buildSessionFactory();
        this.session = this.sessionFactory.openSession();
    }

    public LegalPerson getById(int id) {
        LegalPerson result = null;

        this.session.beginTransaction();
        //this.session.createQuery("from LegalPerson p where p.id = " + id);
        //this.session.by
        result = session.get(LegalPerson.class, id);
        return result;
    }

    public void save(LegalPerson p) {
        this.session.beginTransaction();
        p.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.session.save(p);
        this.session.getTransaction().commit();
    }
}
