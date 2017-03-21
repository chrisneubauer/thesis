package de.cneubauer.domain.bo;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 * DocumentCase BO
 */
public class DocumentCase {
    private int id;
    private int caseId;
    private Creditor creditor;
    private Keyword keyword;
    private String position;
    private boolean isCorrect;
    private Date createdDate;

    /**
     * A DocumentCase that stores information to a creditor such as invoice number and their position in the document
     * @param creditor the creditor that should be related to this case
     * @param id the caseId that indicates how old this case is
     * @param keyword a Keyword that specifies what kind of information this DocumentCase holds
     * @param position the position where the specified keyword can be found
     */
    public DocumentCase(Creditor creditor, int id, Keyword keyword, String position) {
        this.setCreditor(creditor);
        this.setCaseId(id);
        this.setKeyword(keyword);
        this.setPosition(position);
        this.setCreatedDate(java.sql.Date.valueOf(LocalDate.now()));
    }

    /**
     * Default constructor of the DocumentCase object
     */
    public DocumentCase() {}

    /**
     * Returns the id of the DocumentCase object stored in the database table
     * @return  the id of the object in the database
     */
    public int getId() {
        return id;
    }

    /**
     * Do not use this method. It is used by Hibernate internally
     * @param id  the id for the object in the database table
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The caseId that indicates how old this case is
     * @return caseId that indicates how old this case is
     */
    public int getCaseId() {
        return caseId;
    }

    /**
     * The caseId that indicates how old this case is (usually the highest existing case id + 1)
     * @param caseId caseId that indicates how old this case is
     */
    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    /**
     * Returns the creditor of this DocumentCase
     * @return the creditor that should be related to this case
     */
    public Creditor getCreditor() {
        return creditor;
    }

    /**
     * The creditor where this DocumentCase has been applied to
     * @param creditor the creditor that should be related to this case
     */
    public void setCreditor(Creditor creditor) {
        this.creditor = creditor;
    }

    /**
     * Returns the Keyword that indicates what this DocumentCase describes
     * @return a Keyword that specifies what kind of information this DocumentCase holds
     */
    public Keyword getKeyword() {
        return keyword;
    }

    /**
     * The Keyword this DocumentCase describes
     * @param keyword a Keyword that specifies what kind of information this DocumentCase holds
     */
    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the position of the case
     * @return the position where the specified keyword can be found
     */
    public String getPosition() {
        return position;
    }

    /**
     * The position where the Keyword has been found
     * @param position the position where the specified keyword can be found
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Returns information if this DocumentCase has been marked as correct
     * @return true if this DocumentCase is correct, false if otherwise
     */
    public boolean getIsCorrect() {
        return isCorrect;
    }

    /**
     * Sets information if this DocumentCase is correct
     * @param correct flag that shows if the case has been marked as correct
     */
    public void setIsCorrect(boolean correct) {
        isCorrect = correct;
    }

    /**
     * Returns the creation date of the DocumentCase
     * @return the Date the DocumentCase has been created
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * The creation date of the DocumentCase
     * @param createdDate the Date the DocumentCase has been created
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
