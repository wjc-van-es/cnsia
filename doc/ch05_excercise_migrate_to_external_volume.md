<style>
body {
  font-family: "Spectral", "Gentium Basic", Cardo, "Linux Libertine o", "Palatino Linotype", Cambria, serif;
  font-size: 100% !important;
  padding-right: 12%;
}
code {
	padding: 0 .25em;
	
	white-space: pre;
	font-family: "Tlwg mono", Consolas, "Liberation Mono", Menlo, Courier, monospace;
	
	background-color: #ECFFFA;
	//border: 1px solid #ccc;
	//border-radius: 3px;
}

kbd {
	display: inline-block;
	padding: 3px 5px;
	font-family: "Tlwg mono", Consolas, "Liberation Mono", Menlo, Courier, monospace;
	line-height: 10px;
	color: #555;
	vertical-align: middle;
	background-color: #ECFFFA;
	border: solid 1px #ccc;
	border-bottom-color: #bbb;
	border-radius: 3px;
	box-shadow: inset 0 -1px 0 #bbb;
}

h1,h2,h3,h4,h5 {
  color: #269B7D; 
  font-family: "fira sans", "Latin Modern Sans", Calibri, "Trebuchet MS", sans-serif;
}

img {
  width: auto; 
  height: 80%;
  max-height: 100%; 
}
</style>

## Excercise:
# extract data from current postgres db (volume) & set up a new compose config with an independent external volume

## Overview of Steps:
1. figure out how to create a datadump within the container with an interactive cli session
   1.1. [https://www.postgresql.org/docs/current/app-pgdump.html](https://www.postgresql.org/docs/current/app-pgdump.html)
2. find a way to extract the dump file from the container's filesystem
   probably, by writing it to an extra volume for file exchange
3. create a separate Flyway `V4__*.sql` script from the data insert bit of the db dump
   (with a little alteration to be recognizably different from the original). We will need to use a new image of
4. For the new Flyway scripts to work we need a new Docker image of catalog-service. I propose
   `'ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT'`
   1. Let's see if we can update the dependencies for `'ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT'` as much as
      possible
5. Create the external volume and specify this one in `docker-compose.yml`
6. In `docker-compose.yml` update the images of both the `catalog-service` and the `polar-postgres` containers.
7. Test the result all records should have version 1 (and the same current creation and modification timestamp)

## Step 1: The datadump
webpages with pgdump info and info of the used postgres image:
- general `pg_dump` info:
  [https://www.postgresql.org/docs/current/backup-dump.html](https://www.postgresql.org/docs/current/backup-dump.html)
- all `pg_dump` options:
  [https://www.postgresql.org/docs/current/app-pgdump.html](https://www.postgresql.org/docs/current/app-pgdump.html)
- the postgres docker image:
  [https://hub.docker.com/_/postgres](https://hub.docker.com/_/postgres)
- The latest stable version: `docker pull postgres:16.4`

### starting an interactive bash session into the running postgresql db container to produce the datadump
```bash
willem@linux-laptop:~/git/cnsia$ docker exec -it polar-postgres psql -U user -d polardb_catalog -c \
"select isbn, version, created_date, last_modified_date from book order by last_modified_date desc"
     isbn      | version |        created_date        |     last_modified_date     
---------------+---------+----------------------------+----------------------------
 9781633438958 |       2 | 2024-08-24 16:15:13.119058 | 2024-08-24 16:38:37.975843
 1234567891    |       2 | 2024-08-24 16:28:52.935271 | 2024-08-24 16:31:28.118619
 9781617298425 |       1 | 2024-08-24 16:29:42.681757 | 2024-08-24 16:29:42.681757
 9781617298424 |       1 | 2024-08-24 16:24:43.214021 | 2024-08-24 16:24:43.214021
(4 rows)

willem@linux-laptop:~/git/cnsia$ docker exec -it polar-postgres bash
root@3009e9e5ea12:/# psql -U user -d polardb_catalog -c "select isbn, version, created_date, last_modified_date from book order by last_modified_date desc"
     isbn      | version |        created_date        |     last_modified_date     
---------------+---------+----------------------------+----------------------------
 9781633438958 |       2 | 2024-08-24 16:15:13.119058 | 2024-08-24 16:38:37.975843
 1234567891    |       2 | 2024-08-24 16:28:52.935271 | 2024-08-24 16:31:28.118619
 9781617298425 |       1 | 2024-08-24 16:29:42.681757 | 2024-08-24 16:29:42.681757
 9781617298424 |       1 | 2024-08-24 16:24:43.214021 | 2024-08-24 16:24:43.214021
(4 rows)

root@3009e9e5ea12:/# pwd
/
root@3009e9e5ea12:/# ls -la
total 64
drwxr-xr-x   1 root root 4096 Aug 24 15:50 .
drwxr-xr-x   1 root root 4096 Aug 24 15:50 ..
lrwxrwxrwx   1 root root    7 Sep  4  2023 bin -> usr/bin
drwxr-xr-x   2 root root 4096 Jul 14  2023 boot
drwxr-xr-x   5 root root  340 Aug 24 15:50 dev
drwxr-xr-x   2 root root 4096 Sep  7  2023 docker-entrypoint-initdb.d
-rwxr-xr-x   1 root root    0 Aug 24 15:50 .dockerenv
drwxr-xr-x   1 root root 4096 Aug 24 15:50 etc
drwxr-xr-x   2 root root 4096 Jul 14  2023 home
lrwxrwxrwx   1 root root    7 Sep  4  2023 lib -> usr/lib
lrwxrwxrwx   1 root root    9 Sep  4  2023 lib32 -> usr/lib32
lrwxrwxrwx   1 root root    9 Sep  4  2023 lib64 -> usr/lib64
lrwxrwxrwx   1 root root   10 Sep  4  2023 libx32 -> usr/libx32
drwxr-xr-x   2 root root 4096 Sep  4  2023 media
drwxr-xr-x   2 root root 4096 Sep  4  2023 mnt
drwxr-xr-x   2 root root 4096 Sep  4  2023 opt
dr-xr-xr-x 558 root root    0 Aug 24 15:50 proc
drwx------   1 root root 4096 Sep  7  2023 root
drwxr-xr-x   1 root root 4096 Sep  7  2023 run
lrwxrwxrwx   1 root root    8 Sep  4  2023 sbin -> usr/sbin
drwxr-xr-x   2 root root 4096 Sep  4  2023 srv
dr-xr-xr-x  13 root root    0 Aug 24 15:50 sys
drwxrwxrwt   1 root root 4096 Sep  7  2023 tmp
drwxr-xr-x   1 root root 4096 Sep  4  2023 usr
drwxr-xr-x   1 root root 4096 Sep  4  2023 var
root@3009e9e5ea12:/# cat docker-entrypoint-initdb.d/
cat: docker-entrypoint-initdb.d/: Is a directory
root@3009e9e5ea12:/# cd docker-entrypoint-initdb.d/
root@3009e9e5ea12:/docker-entrypoint-initdb.d# ls -la
total 8
drwxr-xr-x 2 root root 4096 Sep  7  2023 .
drwxr-xr-x 1 root root 4096 Aug 24 15:50 ..
root@3009e9e5ea12:/docker-entrypoint-initdb.d# 
root@3009e9e5ea12:/docker-entrypoint-initdb.d# cd ../tmp
root@3009e9e5ea12:/tmp# pg_dump polardb_catalog -a -U user > polardb_catalog-data.sql
root@3009e9e5ea12:/tmp# ls -la
total 12
drwxrwxrwt 1 root root 4096 Aug 24 21:49 .
drwxr-xr-x 1 root root 4096 Aug 24 15:50 ..
-rw-r--r-- 1 root root 2120 Aug 24 21:50 polardb_catalog-data.sql
root@3009e9e5ea12:/tmp# pg_dump polardb_catalog -a -U user --inserts > polardb_catalog-data-inserts.sql
```
- `-a` is short for `--data-only` combined with `--inserts` delivers the data as insert scripts
- later we did another complete dump by ditching the `-a` argument, because we needed to know how the sequence was
  connected to the `books.id` column.
  - see [../catalog-service/doc/CLI-session-pg_dump.md](../catalog-service/doc/CLI-session-pg_dump.md)

## Step 2 - Copy the dump file from the running container in another terminal
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ docker cp polar-postgres:/tmp/polardb_catalog-data-inserts.sql \
 /home/willem/git/cnsia/catalog-service/polardb_catalog-dumps/polardb_catalog-data-inserts.sql
Successfully copied 4.1kB to /home/willem/git/cnsia/catalog-service/polardb_catalog-dumps/polardb_catalog-data-inserts.sql
willem@linux-laptop:~/git/cnsia/catalog-service$ 

```

## Step 3 - Create extra Flyway scripts
We decided to create two extra Flyway scripts:
- [catalog-service/src/main/resources/db/migration/V4__Create_book_id_sequence.sql](../catalog-service/src/main/resources/db/migration/V4__Create_book_id_sequence.sql)
  This creates `public.book_id_seq`, which is needed for generating values for the `public.book.id` primary key column.
  - The SQL statements were borrowed from 
    [../catalog-service/polardb_catalog-dumps/polardb_catalog-all.sql](../catalog-service/polardb_catalog-dumps/polardb_catalog-all.sql)
- [../catalog-service/src/main/resources/db/migration/V5__Insert_book_records.sql](../catalog-service/src/main/resources/db/migration/V5__Insert_book_records.sql)
  contains the actual record insertions. 
  - The main differences with
    [../catalog-service/polardb_catalog-dumps/polardb_catalog-data-inserts.sql](../catalog-service/polardb_catalog-dumps/polardb_catalog-data-inserts.sql)
    are:
    - `nextval('public.book_id_seq')` for `book.id` values instead of fixed values with the adjustment  by
      `SELECT pg_catalog.setval('public.book_id_seq', 4, true);`
    - `now()` for current values for the timestamps at the time this script has to be run (should be only once for 
      any volume containing the persistent state of a postgres db)

## Step 4 - Performing an dependency update followed with a new image version
- see [../catalog-service/doc/20240826-dependencies-update.md](../catalog-service/doc/20240826-dependencies-update.md)
  for details.
- `ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT` was locally build and is available
  - `docker image ls`
  - `docker image inspect --format="{{.Config.Labels}}" ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT` to limit
    the output to the subset under `.Config.Labels` or 
  - `docker image inspect ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT | jq '.[0].Config.Labels'` 
    (has better layout)
  - `docker image inspect ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT | jq '.[0].Config.Labels | del(."io.buildpacks.build.metadata")'`
    is not effective in removing the `."io.buildpacks.build.metadata"` field and has the same output as the previous one
  - `docker image inspect ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT | jq '.[0].Config.Labels | ."org.springframework.boot.version"'`
    returns just its value `"3.3.3"`

## Creating a new independent external volume `polar-postgres` and modify `catalog-service/docker-compose.yml`
- creating the volume
  ```bash
  willem@linux-laptop:~/git/cnsia$ docker volume create polar-postgres-16
  polar-postgres-16
  willem@linux-laptop:~/git/cnsia$ docker volume inspect polar-postgres-16
  [
    {
      "CreatedAt": "2024-08-28T17:03:47+02:00",
      "Driver": "local",
      "Labels": null,
      "Mountpoint": "/var/lib/docker/volumes/polar-postgres-16/_data",
      "Name": "polar-postgres-16",
      "Options": null,
      "Scope": "local"
    }
  ]
  willem@linux-laptop:~/git/cnsia$ docker volume ls -q --filter dangling=true | grep "polar-postgres"
  catalog-service_polar-postgres
  polar-postgres-16
  willem@linux-laptop:~/git/cnsia$ docker volume ls -q --filter dangling=true | grep -v "polar-postgres"
  pack-cache-4963c2ca7f05.build
  pack-cache-4963c2ca7f05.launch
  willem@linux-laptop:~/git/cnsia$
  ```
- modifying [../catalog-service/docker-compose.yml](../catalog-service/docker-compose.yml)
  - using the new postgres image `postgres:16.4`
  - using the new application image `ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT`
  - using the new volume

### First Run
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ docker compose up -d
[+] Running 3/3
 ✔ Network polar-bookstore_default  Created                                                                                                                                    0.1s 
 ✔ Container polar-postgres         Started                                                                                                                                    0.4s 
 ✔ Container catalog-service        Started                                                                                                                                    0.6s 
willem@linux-laptop:~/git/cnsia/catalog-service$ docker ps
CONTAINER ID   IMAGE                                               COMMAND                  CREATED          STATUS          PORTS                                                                                  NAMES
ce4c0b18b2a1   ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT   "/cnb/process/web"       16 seconds ago   Up 15 seconds   0.0.0.0:8001->8001/tcp, :::8001->8001/tcp, 0.0.0.0:9001->9001/tcp, :::9001->9001/tcp   catalog-service
2927c9d77da2   postgres:16.4                                       "docker-entrypoint.s…"   16 seconds ago   Up 15 seconds   0.0.0.0:5432->5432/tcp, :::5432->5432/tcp                                              polar-postgres
willem@linux-laptop:~/git/cnsia/catalog-service$ 

```

```bash

willem@linux-laptop:~/git/cnsia$ docker logs --since=2h polar-postgres > c-polar-postgres-v-polar-postgres-16-1rst-run.txt
initdb: warning: enabling "trust" authentication for local connections
initdb: hint: You can change this by editing pg_hba.conf or using the option -A, or --auth-local and --auth-host, the next time you run initdb.
2024-08-28 15:20:49.318 UTC [1] LOG:  starting PostgreSQL 16.4 (Debian 16.4-1.pgdg120+1) on x86_64-pc-linux-gnu, compiled by gcc (Debian 12.2.0-14) 12.2.0, 64-bit
2024-08-28 15:20:49.318 UTC [1] LOG:  listening on IPv4 address "0.0.0.0", port 5432
2024-08-28 15:20:49.318 UTC [1] LOG:  listening on IPv6 address "::", port 5432
2024-08-28 15:20:49.331 UTC [1] LOG:  listening on Unix socket "/var/run/postgresql/.s.PGSQL.5432"
2024-08-28 15:20:49.345 UTC [64] LOG:  database system was shut down at 2024-08-28 15:20:49 UTC
2024-08-28 15:20:49.354 UTC [1] LOG:  database system is ready to accept connections
2024-08-28 15:25:49.363 UTC [62] LOG:  checkpoint starting: time
2024-08-28 15:26:00.708 UTC [62] LOG:  checkpoint complete: wrote 115 buffers (0.7%); 0 WAL file(s) added, 0 removed, 0 recycled; write=11.262 s, sync=0.045 s, total=11.346 s; sync files=57, longest=0.012 s, average=0.001 s; distance=485 kB, estimate=485 kB; lsn=0/198B678, redo lsn=0/198B640
willem@linux-laptop:~/git/cnsia$ docker logs --since=2h catalog-service > c-catalog-service-v-polar-postgres-16-1rst-run.txt
Picked up JAVA_TOOL_OPTIONS: -Djava.security.properties=/layers/paketo-buildpacks_bellsoft-liberica/java-security-properties/java-security.properties -XX:+ExitOnOutOfMemoryError -agentlib:jdwp=transport=dt_socket,server=y,address=*:8001,suspend=n -XX:MaxDirectMemorySize=10M -Xmx24535061K -XX:MaxMetaspaceSize=106166K -XX:ReservedCodeCacheSize=240M -Xss1M -XX:+UnlockDiagnosticVMOptions -XX:NativeMemoryTracking=summary -XX:+PrintNMTStatistics -Dorg.springframework.cloud.bindings.boot.enable=true
willem@linux-laptop:~/git/cnsia$ 

```

/home/willem/git/cnsia/c-catalog-service-v-polar-postgres-16-1rst-run.txt shows database migration with Flyway:
```txt
2024-08-28T15:20:51.699Z  INFO 1 --- [catalog-service] [           main] org.flywaydb.core.FlywayExecutor         : Database: jdbc:postgresql://polar-postgres:5432/polardb_catalog (PostgreSQL 16.4)
2024-08-28T15:20:51.734Z  INFO 1 --- [catalog-service] [           main] o.f.c.i.s.JdbcTableSchemaHistory         : Schema history table "public"."flyway_schema_history" does not exist yet
2024-08-28T15:20:51.740Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbValidate     : Successfully validated 5 migrations (execution time 00:00.018s)
2024-08-28T15:20:51.764Z  INFO 1 --- [catalog-service] [           main] o.f.c.i.s.JdbcTableSchemaHistory         : Creating Schema History table "public"."flyway_schema_history" ...
2024-08-28T15:20:51.863Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Current version of schema "public": << Empty Schema >>
2024-08-28T15:20:51.875Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "1 - Initial schema"
2024-08-28T15:20:51.933Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "2 - Add publisher column to book table"
2024-08-28T15:20:51.964Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "3 - Alter author column size to book table"
2024-08-28T15:20:51.995Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "4 - Create book id sequence"
2024-08-28T15:20:52.000Z  WARN 1 --- [catalog-service] [           main] o.f.c.i.s.DefaultSqlScriptExecutor       : DB: relation "book_id_seq" already exists, skipping (SQL State: 42P07 - Error Code: 0)
2024-08-28T15:20:52.017Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "5 - Insert book records"
2024-08-28T15:20:52.046Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Successfully applied 5 migrations to schema "public", now at version v5 (execution time 00:00.053s)
```

- Send to PUT requests
  - one inserting new book with isbn 9789492478665 "Brieven aan Koos"
  - one changing the price of the book with isbn 9781633438958 "Artic & Antartic Quarkus in Action" 19.99 -> 24.95

```sql
select * from book order by last_modified_date desc;

id|author                          |isbn         |price|title                                          |created_date           |last_modified_date     |version|publisher                    |
--+--------------------------------+-------------+-----+-----------------------------------------------+-----------------------+-----------------------+-------+-----------------------------+
 4|Martin Štefanko and Jan Martiška|9781633438958|24.95|Artic & Antartic Quarkus in Action             |2024-08-28 15:20:52.016|2024-08-28 15:58:14.125|      2|Manning Publications Co.     |
 5|Tim Fransen                     |9789492478665| 15.0|Brieven aan Koos                               |2024-08-28 15:55:29.876|2024-08-28 15:55:29.876|      1|Das Mag Uitgevers            |
 1|THOMAS VITALE                   |9781617298424| 5.75|Cloud Native Spring in Action at the North Pole|2024-08-28 15:20:52.016|2024-08-28 15:20:52.016|      1|Artic Endeavor Books         |
 2|THOMAS VITALE                   |9781617298425| 19.9|Cloud Native Spring in Action at the Antartic  |2024-08-28 15:20:52.016|2024-08-28 15:20:52.016|      1|Wacky Publishers for weirdo's|
 3|Lyra Silverstar                 |1234567891   |19.75|Northern Lights                                |2024-08-28 15:20:52.016|2024-08-28 15:20:52.016|      1|Polarsophia                  |
```

### Second run
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ docker compose down
[+] Running 3/3
 ✔ Container catalog-service        Removed                                                                                                                                    0.6s 
 ✔ Container polar-postgres         Removed                                                                                                                                    0.2s 
 ✔ Network polar-bookstore_default  Removed                                                                                                                                    0.2s 
willem@linux-laptop:~/git/cnsia/catalog-service$ docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
willem@linux-laptop:~/git/cnsia/catalog-service$ docker ps -a
CONTAINER ID   IMAGE                                 COMMAND                  CREATED        STATUS                      PORTS     NAMES
92f56526bf7b   gcr.io/k8s-minikube/kicbase:v0.0.44   "/usr/local/bin/entr…"   2 months ago   Exited (130) 2 months ago             polar
willem@linux-laptop:~/git/cnsia/catalog-service$ docker compose up -d
[+] Running 3/3
 ✔ Network polar-bookstore_default  Created                                                                                                                                    0.1s 
 ✔ Container polar-postgres         Started                                                                                                                                    0.4s 
 ✔ Container catalog-service        Started                                                                                                                                    0.5s 
willem@linux-laptop:~/git/cnsia/catalog-service$ docker ps
CONTAINER ID   IMAGE                                               COMMAND                  CREATED          STATUS          PORTS                                                                                  NAMES
974d6bd49191   ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT   "/cnb/process/web"       13 seconds ago   Up 12 seconds   0.0.0.0:8001->8001/tcp, :::8001->8001/tcp, 0.0.0.0:9001->9001/tcp, :::9001->9001/tcp   catalog-service
2846b035b209   postgres:16.4                                       "docker-entrypoint.s…"   13 seconds ago   Up 12 seconds   0.0.0.0:5432->5432/tcp, :::5432->5432/tcp                                              polar-postgres
willem@linux-laptop:~/git/cnsia/catalog-service$ 

```

```bash
willem@linux-laptop:~/git/cnsia$ docker logs --since=2h polar-postgres > c-polar-postgres-v-polar-postgres-16-2nd-run.txt
2024-08-28 16:14:24.807 UTC [1] LOG:  starting PostgreSQL 16.4 (Debian 16.4-1.pgdg120+1) on x86_64-pc-linux-gnu, compiled by gcc (Debian 12.2.0-14) 12.2.0, 64-bit
2024-08-28 16:14:24.807 UTC [1] LOG:  listening on IPv4 address "0.0.0.0", port 5432
2024-08-28 16:14:24.807 UTC [1] LOG:  listening on IPv6 address "::", port 5432
2024-08-28 16:14:24.819 UTC [1] LOG:  listening on Unix socket "/var/run/postgresql/.s.PGSQL.5432"
2024-08-28 16:14:24.833 UTC [29] LOG:  database system was shut down at 2024-08-28 16:13:14 UTC
2024-08-28 16:14:24.841 UTC [1] LOG:  database system is ready to accept connections
willem@linux-laptop:~/git/cnsia$ docker logs --since=2h catalog-service > c-catalog-service-v-polar-postgres-16-2nd-run.txt
Picked up JAVA_TOOL_OPTIONS: -Djava.security.properties=/layers/paketo-buildpacks_bellsoft-liberica/java-security-properties/java-security.properties -XX:+ExitOnOutOfMemoryError -agentlib:jdwp=transport=dt_socket,server=y,address=*:8001,suspend=n -XX:MaxDirectMemorySize=10M -Xmx21973789K -XX:MaxMetaspaceSize=106166K -XX:ReservedCodeCacheSize=240M -Xss1M -XX:+UnlockDiagnosticVMOptions -XX:NativeMemoryTracking=summary -XX:+PrintNMTStatistics -Dorg.springframework.cloud.bindings.boot.enable=true
willem@linux-laptop:~/git/cnsia$ 

```
`c-polar-postgres-v-polar-postgres-16-2nd-run.txt` contains the line:
`PostgreSQL Database directory appears to contain a database; Skipping initialization`

/home/willem/git/cnsia/c-catalog-service-v-polar-postgres-16-2nd-run.txt shows that Flyway checks the db and includes
that the schema "public" is up to date:
```txt
2024-08-28T16:14:27.941Z  INFO 1 --- [catalog-service] [           main] org.flywaydb.core.FlywayExecutor         : Database: jdbc:postgresql://polar-postgres:5432/polardb_catalog (PostgreSQL 16.4)
2024-08-28T16:14:27.995Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbValidate     : Successfully validated 5 migrations (execution time 00:00.032s)
2024-08-28T16:14:28.080Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Current version of schema "public": 5
2024-08-28T16:14:28.083Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Schema "public" is up to date. No migration necessary.
```
This indicates that the data in `public.books` persisted in the external volume polar-postgres-16 is not overwritten, as
is as it should be.

#### The inserted record and the updated record are still visible between two docker compose sessions
```sql
select * from book order by last_modified_date desc;
id|author                          |isbn         |price|title                                          |created_date           |last_modified_date     |version|publisher                    |
--+--------------------------------+-------------+-----+-----------------------------------------------+-----------------------+-----------------------+-------+-----------------------------+
 4|Martin Štefanko and Jan Martiška|9781633438958|24.95|Artic & Antartic Quarkus in Action             |2024-08-28 15:20:52.016|2024-08-28 15:58:14.125|      2|Manning Publications Co.     |
 5|Tim Fransen                     |9789492478665| 15.0|Brieven aan Koos                               |2024-08-28 15:55:29.876|2024-08-28 15:55:29.876|      1|Das Mag Uitgevers            |
 1|THOMAS VITALE                   |9781617298424| 5.75|Cloud Native Spring in Action at the North Pole|2024-08-28 15:20:52.016|2024-08-28 15:20:52.016|      1|Artic Endeavor Books         |
 2|THOMAS VITALE                   |9781617298425| 19.9|Cloud Native Spring in Action at the Antartic  |2024-08-28 15:20:52.016|2024-08-28 15:20:52.016|      1|Wacky Publishers for weirdo's|
 3|Lyra Silverstar                 |1234567891   |19.75|Northern Lights                                |2024-08-28 15:20:52.016|2024-08-28 15:20:52.016|      1|Polarsophia                  |
```

#### DB initialization with Flyway is skipped as all known scripts are already run.
```sql
select * from flyway_schema_history;
installed_rank|version|description                           |type|script                                        |checksum   |installed_by|installed_on           |execution_time|success|
--------------+-------+--------------------------------------+----+----------------------------------------------+-----------+------------+-----------------------+--------------+-------+
             1|1      |Initial schema                        |SQL |V1__Initial_schema.sql                        | 1020582797|user        |2024-08-28 15:20:51.847|            23|true   |
             2|2      |Add publisher column to book table    |SQL |V2__Add_publisher_column_to_book_table.sql    | 2043061749|user        |2024-08-28 15:20:51.906|             9|true   |
             3|3      |Alter author column size to book table|SQL |V3__Alter_author_column_size_to_book_table.sql|-1638012560|user        |2024-08-28 15:20:51.955|             2|true   |
             4|4      |Create book id sequence               |SQL |V4__Create_book_id_sequence.sql               | 1652022416|user        |2024-08-28 15:20:51.975|             8|true   |
             5|5      |Insert book records                   |SQL |V5__Insert_book_records.sql                   | 1574550168|user        |2024-08-28 15:20:52.010|            11|true   |
```

## Remaining questions
1. when running 
  [`catalog-service/src/main/resources/db/migration/V4__Create_book_id_sequence.sql`](../catalog-service/src/main/resources/db/migration/V4__Create_book_id_sequence.sql)
  we got:
  ```text
  2024-08-28T15:20:51.995Z  INFO 1 --- [catalog-service] [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "4 - Create book id sequence"
  2024-08-28T15:20:52.000Z  WARN 1 --- [catalog-service] [           main] o.f.c.i.s.DefaultSqlScriptExecutor       : DB: relation "book_id_seq" already exists, skipping (SQL State: 42P07 - Error Code: 0)
  ```
  What triggered the implicit creation of book_id_seq?
  1. Is it the way the `public.book` table is defined in
    [`catalog-service/src/main/resources/db/migration/V1__Initial_schema.sql`](../catalog-service/src/main/resources/db/migration/V1__Initial_schema.sql)
    or
  2. Is it something Flyway does?
2.See how to run Flyway stand alone (outside Spring Boot, or maven / gradle plugin) to set up other databases not
  directly connected to a Java project.

## Answers
For the first quetion the answer is definitely 1.1
[https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL](https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL)
```sql
CREATE TABLE tablename (
 colname SERIAL
);
```
is equivalent to:
```sql
CREATE SEQUENCE tablename_colname_seq AS integer;
CREATE TABLE tablename (
  colname integer NOT NULL DEFAULT nextval('tablename_colname_seq')
);
ALTER SEQUENCE tablename_colname_seq OWNED BY tablename.colname;
```
Here BIGSERIAL is a variety of SERIAL as we can surmise from the table at the bottom of
[https://www.postgresql.org/docs/current/datatype.html](https://www.postgresql.org/docs/current/datatype.html).
See the excerpt table fragment making clear that 
- there are three precisions of serial
- the `V1__Initial_schema.sql` tends to use the aliases, whilst the
  [`catalog-service/polardb_catalog-dumps/polardb_catalog-all.sql`](../catalog-service/polardb_catalog-dumps/polardb_catalog-all.sql)
  prefers the official names.

| Name                       | Aliases          | Description                                      |
|----------------------------|------------------|--------------------------------------------------|
| bigserial                  | serial8          | autoincrementing eight-byte integer              |
| smallserial                | serial2          | autoincrementing two-byte integer                |
| serial                     | serial4          | autoincrementing four-byte integer               |
| double precision           | float8           | double precision floating-point number (8 bytes) |
| character varying [ (n) ]  | varchar [ (n) ]  | variable-length character string                 |

- the float8 type of the public.book.price column in 
  [`V1__Initial_schema.sql`](../catalog-service/src/main/resources/db/migration/V1__Initial_schema.sql) is actually the
  exact equivalent of the double precision used in `polardb_catalog-all.sql` and
- the same is true for the `varchar(n)` used in the former and the `character varying(n)` used in the latter.

### Flyway
- [https://documentation.red-gate.com/flyway](https://documentation.red-gate.com/flyway)
  - [https://documentation.red-gate.com/flyway/getting-started-with-flyway](https://documentation.red-gate.com/flyway/getting-started-with-flyway)
- [https://www.baeldung.com/database-migrations-with-flyway](https://www.baeldung.com/database-migrations-with-flyway)
  - Using it as Maven plugin
  - Using it with Spring Boot
- [https://github.com/flyway/flyway](https://github.com/flyway/flyway)
		
   
   