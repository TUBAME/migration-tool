#include "test_defs.h"
#include <stdio.h>
#include <assert.h>
#ifdef WIN32
#include <windows.h>
#endif

#include <sql.h>
#include <sqlext.h>

int main()
{

	SQLUSMALLINT num_cols;

	SQLCHAR buf1[MAX_LEN];
	SQLCHAR buf2[MAX_LEN];
	SQLCHAR dateStr[56];

	SQLUSMALLINT col;
	SQLSMALLINT col_len;
	SQLSMALLINT type;
	SQLUINTEGER sz;
	SQLSMALLINT scale;
	SQLSMALLINT can_null;

	GET_LOGIN_VARS();

	VERBOSE("calling SQLAllocHandle(EnvHandle) \n");

	rc = SQLAllocHandle(SQL_HANDLE_ENV, SQL_NULL_HANDLE, &EnvHandle);
	assert(rc == SQL_SUCCESS);
	assert(EnvHandle != (SQLHANDLE) NULL);

	rc = SQLSetEnvAttr(EnvHandle, SQL_ATTR_ODBC_VERSION,
			   (SQLPOINTER) SQL_OV_ODBC3, SQL_IS_UINTEGER);

	assert(rc == SQL_SUCCESS);

	VERBOSE("calling SQLAllocHandle(ConHandle) \n");

	rc = SQLAllocHandle(SQL_HANDLE_DBC, EnvHandle, &ConHandle);
	assert(ConHandle != (SQLHANDLE) NULL);
	assert(rc == SQL_SUCCESS);

	if (dsn[0])
		rc = SQLDriverConnect(ConHandle, NULL, dsn,
				      SQL_NTS, NULL, 0, NULL,
				      SQL_DRIVER_NOPROMPT);
	else
		rc = SQLConnect(ConHandle, twoTask, SQL_NTS,
				(SQLCHAR *) userName, SQL_NTS, (SQLCHAR *) pswd,
				SQL_NTS);
	assert(rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);

	VERBOSE("connected to  database %s\n", twoTask);
	VERBOSE("allocing handle\n");

	rc = SQLAllocStmt(ConHandle, &StmtHandle);
	assert(rc == SQL_SUCCESS);
	sprintf(SQLStmt, "drop table some_char_types");
	rc = SQLExecDirect(StmtHandle, SQLStmt, SQL_NTS);

	sprintf(SQLStmt, "create table some_char_types (a_char char(1),");
	strcat(SQLStmt, "  a_vchr varchar(1)) ");
	VERBOSE("executing %s\n", SQLStmt);

	rc = SQLExecDirect(StmtHandle, SQLStmt, SQL_NTS);
	assert(rc == SQL_SUCCESS);

	sprintf(SQLStmt, "select * from some_char_types where 1=0");

	VERBOSE("executing %s\n", SQLStmt);

	rc = SQLExecDirect(StmtHandle, SQLStmt, SQL_NTS);
	assert(rc == SQL_SUCCESS);

	rc = SQLNumResultCols(StmtHandle, &num_cols);
	assert(rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO);
	assert(num_cols == 2);

	for (col = 1; col <= num_cols; col++) {
		rc = SQLDescribeCol(StmtHandle, col, buf1, MAX_LEN, &col_len,
				    &type, &sz, &scale, &can_null);

		assert(rc == SQL_SUCCESS);

		if (col == 1)
			assert(type == SQL_CHAR);
		if (col == 2)
			assert(type == SQL_VARCHAR);

		VERBOSE
		    ("col=%d name:%s len=%d type=%d size=%d scale=%d nullable=%d\n",
		     col, buf1, col_len, type, sz, scale, can_null);

		rc = SQLColAttribute(StmtHandle, col, SQL_DESC_NAME,
				     buf2, sizeof(buf2), NULL, NULL);

		assert(rc == SQL_SUCCESS);
		assert(strcmp(buf1, buf2) == 0);

		rc = SQLColAttribute(StmtHandle, col, SQL_DESC_TYPE,
				     NULL, 0, NULL, (SQLPOINTER) & type);
		assert(rc == SQL_SUCCESS);
		VERBOSE("col:%s type(1):%d type(2):%d\n", buf1, type, type);
		if (col == 1)
			assert(type == SQL_CHAR);
		if (col == 2)
			assert(type == SQL_VARCHAR);
	}

	rc = SQLDisconnect(ConHandle);
	assert(rc == SQL_SUCCESS);
	VERBOSE("disconnected from  database\n");

	VERBOSE("calling SQLFreeHandle(ConHandle) \n");

	assert(ConHandle != (SQLHANDLE) NULL);
	rc = SQLFreeHandle(SQL_HANDLE_DBC, ConHandle);
	assert(rc == SQL_SUCCESS);

	VERBOSE("calling SQLFreeHandle(EnvHandle) \n");

	assert(EnvHandle != (SQLHANDLE) NULL);
	rc = SQLFreeHandle(SQL_HANDLE_ENV, EnvHandle);
	assert(rc == SQL_SUCCESS);

	return (rc);
}