USE ferd_transformator;

ALTER TABLE Address MODIFY COLUMN CreatedDate date;
ALTER TABLE Address MODIFY COLUMN ModifiedDate date;

ALTER TABLE Corporateform MODIFY COLUMN CreatedDate date;
ALTER TABLE Corporateform MODIFY COLUMN ModifiedDate date;

ALTER TABLE Country MODIFY COLUMN CreatedDate date;
ALTER TABLE Country MODIFY COLUMN ModifiedDate date;

ALTER TABLE Country_Corporateform MODIFY COLUMN CreatedDate date;
ALTER TABLE Country_Corporateform MODIFY COLUMN ModifiedDate date;

ALTER TABLE Invoice MODIFY COLUMN DeliveryDate date;
ALTER TABLE Invoice MODIFY COLUMN IssueDate date;
ALTER TABLE Invoice MODIFY COLUMN CreatedDate date;
ALTER TABLE Invoice MODIFY COLUMN ModifiedDate date;

ALTER TABLE LegalPerson MODIFY COLUMN CreatedDate date;
ALTER TABLE LegalPerson MODIFY COLUMN ModifiedDate date;

ALTER TABLE Record MODIFY COLUMN EntryDate date;

ALTER TABLE Scan MODIFY COLUMN CreatedDate date;
ALTER TABLE Scan MODIFY COLUMN ModifiedDate date;
