USE ferd_transformator;

DROP Table AccountingRecord;

Create Table Record(
Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
EntryDate TIMESTAMP,
DocumentNo VARCHAR (100),
Document BLOB Null,
EntryText VARCHAR (100)
);

CREATE Table Account_Record(
ID INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
BruttoValue NUMERIC(15,2),
IsDebit BOOL NOT NULL,
Account INT(6) UNSIGNED NOT NULL,
Record INT(6) UNSIGNED NOT NULL,
FOREIGN KEY (Record) REFERENCES Record(Id),
FOREIGN KEY (Account) REFERENCES Account(Id)
);

