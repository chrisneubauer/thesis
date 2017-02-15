use ferd_transformator;

CREATE TABLE Keyword (
Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
Name VARCHAR(100) NULL
);

CREATE TABLE Creditor (
Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
Name VARCHAR(100) NULL,
LegalPerson_Id INT(6) UNSIGNED NOT NULL,
FOREIGN KEY (LegalPerson_Id) REFERENCES LegalPerson(Id)
);

CREATE Table DocumentCase(
ID INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
Case_ID INT(6) UNSIGNED,
Creditor INT(6) UNSIGNED NOT NULL,
Keyword INT(6) UNSIGNED NOT NULL,
Position VARCHAR(100) NULL,
IsCorrect BOOL NOT NULL,
CreatedDate Date,
FOREIGN KEY (Creditor) REFERENCES Creditor(Id),
FOREIGN KEY (Keyword) REFERENCES Keyword(Id)
);

INSERT INTO Keyword(Name) VALUES('Dokumentenart');
INSERT INTO Keyword(Name) VALUES('Rechnungsnummer');
INSERT INTO Keyword(Name) VALUES('Rechnungsdatum');
INSERT INTO Keyword(Name) VALUES('Kreditor');
INSERT INTO Keyword(Name) VALUES('Debitor');