Important things that have to be made:

- Multiple savings not possible due to error: Transaction already active -> Probably fixed, if not use function in IDAO to stop sessions

- Forgetful learning of positions per Creditor

- Extract Accounting Record as Textfile

- Efficient searching for Accounts

- Deletion of Accounting Records

- Issue Date gets Month + 1

- Validation of Accounting Records doesn't take place if user is in invoice view. (Makes it possible to save document without acc recs)

- UI Resizes the second time

- Issue with value of accounting records. If for example 70.24 on the left side and 40.0 + 30.24 on the ride: "Values does not sum up to zero", probably issue with integer

- if validation issue on a smaller accounting record, the arrow pointing to the right is red

#
17/03/15 20:58:49 INFO controller.ResultsController: Listeners added to textfields
17/03/15 20:58:49 INFO controller.AccountingRecordsController: initiating AccountingRecordsController data
17/03/15 20:58:49 ERROR internal.DriverManagerConnectionProviderImpl: Collection leak detected: there are 1 unclosed connections upon shutting down pool jdbc:mysql://127.0.0.1:3306/ferd_transformator?autoReconnect=true&useSSL=false
17/03/15 20:58:49 INFO controller.AccountingRecordsController: Listeners added to textfields
17/03/15 20:58:49 INFO controller.AccountingRecordsController: 0 records found!