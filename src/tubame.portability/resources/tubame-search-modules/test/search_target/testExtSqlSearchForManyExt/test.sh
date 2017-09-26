#!/bin/sh

for LINE in `cat ./tmp/tablenames.txt`;
do

# select test from B;

TABLE_NAME=`echo ${LINE} | tr -d '\r' | tr -d '\n'`
echo "----------------------------------------------"
echo "INSERT INTO ${BK_TABLE_NAME} SELECT * FROM ${TABLE_NAME};"
BK_TABLE_NAME="${TABLE_NAME%???}_BK"
sqlplus -s ${CONNECTION_STRING} << EOF
  INSERT INTO ${BK_TABLE_NAME}
  SELECT * FROM ${TABLE_NAME};
  EXIT;
EOF
done