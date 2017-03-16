package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.DocumentCase;
import de.cneubauer.domain.dao.DocumentCaseDao;
import de.cneubauer.util.DocumentCaseSet;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 * DAO for business object DocumentCase
 */
public class DocumentCaseDaoImpl extends AbstractDao<DocumentCase> implements DocumentCaseDao {
    public DocumentCaseDaoImpl() { super(DocumentCase.class); }

    @Override
    protected void onSave(DocumentCase entity) {

    }

    @Override
    public List<DocumentCase> getAllByCreditorName(String name) {
        String hql = "FROM DocumentCase c WHERE c.creditor.name = ?1";

        Session currentSession = this.getSessionFactory().openSession();
        Transaction getTrans = currentSession.beginTransaction();

        Query q = currentSession.createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching for Cases of Creditor " + name);
        q.setParameter(1, name);

        List cases = q.getResultList();
        List<DocumentCase> docCases = new ArrayList<>(cases.size());

        for (Object docCase : cases) {
            docCases.add((DocumentCase) docCase);
        }
        getTrans.commit();
        //currentSession.close();
        return docCases;
    }

    @Override
    public void saveCases(DocumentCaseSet set) {
        if (set.getInvoiceDateCase() != null) {
            this.save(set.getInvoiceDateCase());
        }
        if (set.getInvoiceNoCase() != null) {
            this.save(set.getInvoiceNoCase());
        }
        if (set.getBuyerCase() != null) {
            this.save(set.getBuyerCase());
        }
        if (set.getSellerCase() != null) {
            this.save(set.getSellerCase());
        }
        if (set.getDocumentTypeCase() != null) {
            this.save(set.getDocumentTypeCase());
        }
        if (set.getPositionCases() != null && set.getPositionCases().size() > 0) {
            this.saveAll(set.getPositionCases());
        }
    }

    @Override
    public int getHighestCaseId() {
        List<DocumentCase> documentCases = this.getAll();
        int max = 0;
        for (DocumentCase docCase : documentCases) {
            if (docCase.getCaseId() > max) {
                max = docCase.getCaseId();
            }
        }
        return max;
    }

    @Override
    public void saveAll(List<DocumentCase> positionCases) {
        for (DocumentCase docCase : positionCases) {
            this.save(docCase);
        }
    }
}
