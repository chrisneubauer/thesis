-- Links positions to scans

USE FERD_Transformator;

ALTER TABLE Account DROP FOREIGN KEY account_ibfk_1;
ALTER TABLE Account DROP COLUMN AccountType_Id;

RENAME TABLE Record TO Position;

ALTER TABLE Position DROP COLUMN EntryDate;
ALTER TABLE Position DROP COLUMN DocumentNo;
ALTER TABLE Position DROP COLUMN Document;
ALTER TABLE Position ADD COLUMN Scan INT(6) UNSIGNED;

ALTER TABLE Position ADD FOREIGN KEY (Scan) REFERENCES Scan(Id) ON DELETE CASCADE;

RENAME TABLE Account_Record TO Account_Position;
ALTER TABLE Account_Position CHANGE Record Position INT(6) UNSIGNED;