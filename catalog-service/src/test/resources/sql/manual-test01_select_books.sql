select * from book order by last_modified_date desc;
select * 
from book 
where price > 10.50
order by last_modified_date desc;

select * from flyway_schema_history;

select * from information_schema.applicable_roles;
select * from information_schema.table_privileges;
select * from information_schema."tables";
select * from information_schema.schemata;
select * from information_schema.usage_privileges;
select * from information_schema.view_table_usage;
select * from information_schema.role_column_grants where grantor = 'user' order by table_name, column_name, grantee, privilege_type;
select * from information_schema.role_table_grants;
select * from pg_catalog.pg_stat_all_tables where schemaname = 'public';
select * from pg_catalog.pg_stat_database;
select * from pg_catalog.pg_stat_activity;