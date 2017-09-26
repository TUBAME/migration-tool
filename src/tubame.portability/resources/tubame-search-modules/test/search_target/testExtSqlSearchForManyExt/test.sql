/*
select dt, EXTRACT(YEAR FROM dt),
 EXTRACT(MONTH FROM dt), EXTRACT(DAY FROM dt) from date_sample;
*/
select dt, EXTRACT(YEAR FROM dt),
 EXTRACT(MONTH FROM dt), EXTRACT(DAY FROM dt) from date_sample;

-- select emp_id, dept_id, salary,var_samp( salary ) over( partition by dept_id ) as var_samp from emp;