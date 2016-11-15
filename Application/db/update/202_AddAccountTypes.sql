USE ferd_transformator;

CREATE TABLE AccountType (
Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
Type INT,
Name Varchar(100)
);

INSERT INTO AccountType (Type, Name) VALUES (1, "Aktivkonto");
INSERT INTO AccountType (Type, Name) VALUES (2, "Passivkonto");
INSERT INTO AccountType (Type, Name) VALUES (3, "Aufwandskonto");
INSERT INTO AccountType (Type, Name) VALUES (4, "Ertragskonto");
INSERT INTO AccountType (Type, Name) VALUES (5, "Statistikkonto");

CREATE TABLE Account (
Id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
AccountNo VARCHAR(5),
Name VARCHAR(100),
AccountType_Id INT(6) UNSIGNED NOT NULL,
FOREIGN KEY (AccountType_Id) REFERENCES AccountType(Id)
);
