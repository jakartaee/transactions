#-------------------------------------------------
# SQL DML statements used in the Transactions TCK tests
#-------------------------------------------------

#*****************************************************************************
# The SQL Statements which are used in DBSupport in jta/ee/txpropagationtest
# These are the ONLY tables used by the 195 transaction tests
#*****************************************************************************
JTA_Tab1_Delete=delete from JTA_Tab1
JTA_Tab2_Delete=delete from JTA_Tab2

JTA_Tab1_Insert=insert into JTA_Tab1 values(?, ?, ?)
JTA_Tab2_Insert=insert into JTA_Tab2 values(?, ?, ?)

JTA_Delete1=delete from JTA_Tab1 where KEY_ID = ?
JTA_Delete2=delete from JTA_Tab2 where KEY_ID = ?

JTA_Tab1_Update1=update JTA_Tab1 set COF_NAME = ? where KEY_ID = ?
JTA_Tab2_Update1=update JTA_Tab2 set CHOC_NAME = ? where KEY_ID = ?

JTA_Tab1_Update2=update JTA_Tab1 set PRICE = ? where KEY_ID = ?
JTA_Tab2_Update2=update JTA_Tab2 set PRICE = ? where KEY_ID = ?

JTA_Tab1_Select=select * from JTA_Tab1
JTA_Tab2_Select=select * from JTA_Tab2

JTA_Tab1_Select1=select KEY_ID,  COF_NAME, PRICE FROM JTA_Tab1 where KEY_ID = ?
JTA_Tab2_Select1=select KEY_ID,  CHOC_NAME, PRICE FROM JTA_Tab2 where KEY_ID = ?

-- Made with Bob
