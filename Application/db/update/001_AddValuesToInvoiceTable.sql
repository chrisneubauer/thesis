USE Ferd_transformator;

ALTER TABLE Invoice CHANGE Date IssueDate TIMESTAMP;
ALTER TABLE Invoice CHANGE MoneyValue GrandTotal NUMERIC(15,2);
ALTER TABLE Invoice ADD Column InvoiceNumber VARCHAR(50);
ALTER TABLE Invoice ADD Column DeliveryDate TIMESTAMP;
ALTER TABLE Invoice ADD Column LineTotal NUMERIC(15,2);
ALTER TABLE Invoice ADD Column ChargeTotal NUMERIC(15,2);
ALTER TABLE Invoice ADD Column AllowanceTotal NUMERIC(15,2);
ALTER TABLE Invoice ADD Column TaxBasisTotal NUMERIC(15,2);
ALTER TABLE Invoice ADD Column TaxTotal NUMERIC(15,2);