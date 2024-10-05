select nspname from pg_catalog.pg_namespace;

select * from information_schema.table_privileges tp order by tp.grantee, tp.table_schema limit 10;

select * from information_schema.usage_privileges up order by up.grantee, up.object_schema limit 10;

select * from information_schema.applicable_roles ar;

select * from pg_catalog.pg_user;

select * from pg_catalog.pg_roles;







