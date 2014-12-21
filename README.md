keydatacomparer
===============

I have to fast compare the key data (such configuration parameters) between two databases.
As input I have a XML file with list of tables to compare, same in both databases.

Program connects to databases, reads the list of tables to compare and their fields,
compares data in records with the same data in primary key fields in compared tables,
and finally produces report: records missing in databases and records with differences.
