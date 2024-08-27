<style>
body {
  font-family: "Gentium Basic", Cardo, "Linux Libertine o", "Palatino Linotype", Cambria, serif;
  font-size: 130% !important;
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
- The latest stable version: `docker pull postgres:16.4-alpine3.20`

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
- modifying [../catalog-service/docker-compose.yml](../catalog-service/docker-compose.yml)
  - using the new postgres image `postgres:16.4`
  - using the new application image `ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT`
  - using the new volume