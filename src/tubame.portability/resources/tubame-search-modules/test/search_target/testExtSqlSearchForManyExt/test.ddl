create table emp (empno number(5) primary key,
                   name varchar2(20),
                   sal number(10,2),
                   job varchar2(20),
                   mgr  number(5),
                   Hiredate  date,
                   comm number(10,2));

create table tax (empno number(5), tax number(10,2));

insert into tax select empno,(sal-5000)*0.40
                     from emp where sal > 5000;