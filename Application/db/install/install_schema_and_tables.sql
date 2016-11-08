CREATE SCHEMA ferd_transformator;

USE ferd_transformator;

CREATE TABLE LegalPerson (
Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
IsCompany BOOL NOT NULL,
CompanyName VARCHAR(100) NULL,
-- TODO: CorporateForm as own Table
CorporateForm VARCHAR(30) NULL,
Name VARCHAR(100) NULL,
SurName VARCHAR(100) NULL,
Street VARCHAR(100) NULL,
ZipCode INT(5) NULL,
City VARCHAR(100) NULL,
CreatedDate TIMESTAMP,
ModifiedDate TIMESTAMP
);

CREATE TABLE Invoice (
Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
Debitor INT(6) UNSIGNED NULL,
Creditor INT(6) UNSIGNED NULL,
Date TIMESTAMP,
MoneyValue NUMERIC(15,2) NULL,
HasSkonto BOOL NULL,
Skonto NUMERIC(2,1) NULL,
CreatedDate TIMESTAMP,
ModifiedDate TIMESTAMP,
FOREIGN KEY (Debitor) REFERENCES LegalPerson(Id) ON DELETE CASCADE,
FOREIGN KEY (Creditor) REFERENCES LegalPerson(Id) ON DELETE CASCADE
);

CREATE TABLE Scan (
Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
-- eventually needs to be longblob
File MEDIUMBLOB,
InvoiceInformation INT(6) UNSIGNED NULL,
CreatedDate TIMESTAMP,
ModifiedDate TIMESTAMP,
FOREIGN KEY (InvoiceInformation) REFERENCES Invoice(Id) ON DELETE CASCADE
);