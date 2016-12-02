USE ferd_transformator;

CREATE TABLE AccountingRecord (
Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
EntryDate TIMESTAMP,
DocumentNo VARCHAR (100),
Document BLOB Null,
EntryText VARCHAR (100),
Debit_Account INT(6) UNSIGNED NOT NULL,
Credit_Account INT(6) UNSIGNED NOT NULL,
BruttoValue NUMERIC(15,2),
VAT_Rate NUMERIC (3,2),
SalesTaxID VARCHAR(100),
FOREIGN KEY (Debit_Account) REFERENCES Account(Id),
FOREIGN KEY (Credit_Account) REFERENCES Account(Id)
);
