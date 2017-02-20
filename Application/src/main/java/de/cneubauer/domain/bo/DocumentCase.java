package de.cneubauer.domain.bo;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 */
public class DocumentCase {
    private int id;
    private int caseId;
    private Creditor creditor;
    private Keyword keyword;
    private String position;
    private boolean isCorrect;
    private Date createdDate;

    public DocumentCase(Creditor creditor, int id, Keyword keyword, String position) {
        this.setCreditor(creditor);
        this.setCaseId(id);
        this.setKeyword(keyword);
        this.setPosition(position);
        this.setCreatedDate(java.sql.Date.valueOf(LocalDate.now()));
    }

    public DocumentCase() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public Creditor getCreditor() {
        return creditor;
    }

    public void setCreditor(Creditor creditor) {
        this.creditor = creditor;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
