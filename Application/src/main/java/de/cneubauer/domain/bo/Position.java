package de.cneubauer.domain.bo;

import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.util.RecordTrainingEntry;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Business Object for Position table
 */
public class Position {
    private Scan scan;
    private int id;
    private String entryText;
    private Set<AccountPosition> positionAccounts;
    private float probability;

    /**
     * Default constructor of the Position object. Sets positionAccounts to a size of 0
     */
    public Position() {
        this.positionAccounts = new HashSet<>(0);
    }

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
     * Returns a Scan object that is related to the position
     * @return The Scan object that is related to the position
     */
    public Scan getScan() {
        return scan;
    }

    /**
     * Links a Scan object to this position
     * @param scan the Scan object that should be linked with this position
     */
    public void setScan(Scan scan) {
        this.scan = scan;
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

    /**
     * @param probability the probability of this position
     */
    public void setProbability(float probability) {
        this.probability = probability;
    }

    /**
     * @return the probability of this position
     */
    public float getProbability() {
        return probability;
    }
}
