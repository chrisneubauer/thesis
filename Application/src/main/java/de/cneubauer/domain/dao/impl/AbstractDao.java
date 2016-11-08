package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.dao.IDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaQuery;
import java.io.File;
import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Abstract DAO for general configuration purpose
 */
public abstract class AbstractDao<T> implements IDao<T> {
    private Session session;
    private SessionFactory sessionFactory;
    private Configuration config;
    private Class<T> clazz;

    AbstractDao(Class<T> paramClass) {
        this.clazz = paramClass;
        this.config = new Configuration();
        File configFile = new File("src/main/resources/hibernate.cfg.xml");
        this.getConfig().configure(configFile);
        this.sessionFactory = this.getConfig().buildSessionFactory();
        this.session = this.getSessionFactory().openSession();
    }

    public Session getSession() {
        return session;
    }

    private SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private Configuration getConfig() {
        return config;
    }

    public T getById(int id) {
            T result;

            result = this.getSession().get(clazz, id);
            return result;
    }

    public List<T> getAll() {
        CriteriaQuery<T> query = this.getSession().getCriteriaBuilder().createQuery(clazz);
        query.select(query.from(clazz));

        Query<T> q2 = this.getSession().createQuery(query);
        return q2.getResultList();
    }

    public void save(T entity) {
        this.getSession().beginTransaction();

        this.onSave(entity);

        this.getSession().save(entity);
        this.getSession().getTransaction().commit();
    }

    protected abstract void onSave(T entity);
}