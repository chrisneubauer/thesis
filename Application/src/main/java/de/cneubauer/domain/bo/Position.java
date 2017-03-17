package de.cneubauer.domain.bo;

import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.util.RecordTrainingEntry;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Business Object for Record table
 */
public class Position {
    public Position() {
        this.positionAccounts = new HashSet<>(0);
    }

    private Scan scan;

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    private int id;
    private String entryText;
    private Set<AccountPosition> positionAccounts;
    private float probability;

    /**
     * @param entry  the account-record relation entry to be used for training instances
     */
    public void addRecordTrainingEntry(RecordTrainingEntry entry) {
        AccountDaoImpl accountDao = new AccountDaoImpl();
        List<Account> accountList = accountDao.getAll();
        for (Map.Entry<String, Double> mapEntry : entry.getDebitAccounts().entrySet()) {
            try {
                AccountPosition record = new AccountPosition();
                record.setIsDebit(true);
                for (Account a : accountList) {
                    if (mapEntry.getKey().equals(a.getName())) {
                        record.setAccount(a);
                        break;
                    }
                }
                record.setBruttoValue(mapEntry.getValue());
                this.getPositionAccounts().add(record);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse account from string, skipping..");
            }
        }

        for (Map.Entry<String, Double> mapEntry : entry.getCreditAccounts().entrySet()) {
            try {
                AccountPosition record = new AccountPosition();
                record.setIsDebit(false);
                for (Account a : accountList) {
                    if (mapEntry.getKey().equals(a.getName())) {
                        record.setAccount(a);
                        break;
                    }
                }
                record.setAccount(accountDao.getByName(mapEntry.getKey()));
                record.setBruttoValue(mapEntry.getValue());
                this.getPositionAccounts().add(record);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse account from string, skipping..");
            }
        }
    }

    /**
     * @return  returns a set of account-record relations
     */
    public Set<AccountPosition> getPositionAccounts() {
        return positionAccounts;
    }

    /**
     * @param positionAccounts  the set of account-record relations to be stored
     */
    public void setPositionAccounts(Set<AccountPosition> positionAccounts) {
        this.positionAccounts = positionAccounts;
    }

    /**
     * @return  the id of this object stored in the database table
     */
    public int getId() {
        return id;
    }

    /**
     * Do not use this method. It is used by Hibernate internally
     * @param id  the id for this object to be stored in the database table
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return  the text of this position
     */
    public String getEntryText() {
        return entryText;
    }

    /**
     * @param entryText  the position string
     */
    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public float getProbability() {
        return probability;
    }
}
