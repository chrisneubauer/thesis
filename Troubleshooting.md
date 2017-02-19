# Troubleshooting

If you have any problems during the installation or usage of the application, have a look in this list of problems. Every known issue regarding the application will be listed here.

### Packet for query is too large
##### Problem: 
Possible stacktrace message: 
caused by: java.sql.BatchUpdateException: Packet for query is too large (1238730 > 1048576). You can change this value on the server by setting the max_allowed_packet' variable.
at com.mysql.jdbc.PreparedStatement.executeBatchSerially(PreparedStatement.java:1213)

##### Solution: 
Check your MySQL Database settings. You can do that by using your MySQL Client and write: 
``` 
SELECT @@max_allowed_packet;
```
 
The number will be most likely very small (e.g. 4 mio), which roughly translates to 4mb. If this is the case, you can do one of the following things:
Set the MySQL packet size to a larger value (256MB) and restart MySQL Server. 256MB should be large enough to cover most cases.

```
shell> mysqld --max_allowed_packet=256M
```

Alternatively, you can do this on your MySQL server's settings by editing MySQL's my.cnf file (often named my.ini on Windows operating systems). Locate the [mysqld]section in the file, and add/modify the following parameters:

```
1 [mysqld]
2 ...
3 max_allowed_packet = 256M
4 ...
```

Remember to restart MySQL services in order for the changes above to take effect. For more information, please refer to MySQL manual [here](http://dev.mysql.com/doc/refman/5.0/en/packet-too-large.html).

Note:
If you are unable to stop your database, you can alternatively set the value of the max allowed packet parameter dynamically. To do so:
Log in as a root user.
You'll need to set the value as an integer, rather than '256M'. 256M is equivalent to 256*1024*1024, or 268435456.

```
mysql> SET GLOBAL max_allowed_packet=268435456;
```

You will still need to update your /etc/my.cnf file as described in the method above to make the change persistent.