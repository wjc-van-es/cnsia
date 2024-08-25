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
</style>

# Chaper 5: Persisting and managing data in the cloud
We would like to persist state of our catalog-service, and we choose to use a containerized postgresql db as
backing service with Docker. 

## Postgresql docker container setup
- We used postgres:15.3 as image from docker-hub: [https://hub.docker.com/_/postgres](https://hub.docker.com/_/postgres)
- Beware, that a lot of environment variables and also the volume mapping container dir is different from the
  registry.ocp4.example.com:8443/rhel9/postgresql-13 image we used to work with.
- create a volume only once
  ```bash
  $ docker volume create polar-postgres
  $ docker volume inspect polar-postgres 
  [
    {
    "CreatedAt": "2023-07-16T17:01:25+02:00",
    "Driver": "local",
    "Labels": null,
    "Mountpoint": "/var/lib/docker/volumes/polar-postgres/_data",
    "Name": "polar-postgres",
    "Options": null,
    "Scope": "local"
    }
  ]
  ```
  - Only if the volume gets corrupted in some way you must remove it with `docker volume rm polar-postgres` after which 
    it can be recreated with the same `docker volume create` command used previously.
- We have made a shell script to create a docker container consistently at 
  [../catalog-service/run-pgres.sh](../catalog-service/run-pgres.sh)
  - In addition to the book example it contains a volume mapping `-v polar-postgres:/var/lib/postgresql/data` (beware,
    that the container dir is `/var/lib/postgresql/data` in this image),
  - moreover, we added the `--rm` flag, so the container will automatically be removed when it is stopped, and we won´t
    get warnings that a container named `polar-postgres` already exists, when we attempt to rerun the script.
  - hence, a typical postgres container run cycle would be:
    ```bash
    $ ./run-pgres.sh 
    3d2e32287b832289dfd14f3d1592e096da8fb1224d07f595c7cda4d7ac9ce127
    $ docker ps -a
    CONTAINER ID   IMAGE           COMMAND                  CREATED          STATUS          PORTS                                       NAMES
    3d2e32287b83   postgres:15.3   "docker-entrypoint.s…"   14 seconds ago   Up 13 seconds   0.0.0.0:5432->5432/tcp, :::5432->5432/tcp   polar-postgres
    $ docker stop polar-postgres
    polar-postgres
    $ docker ps -a
    CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
    $
    ```
## postgres volume with `docker compose`
Somehow the polar-postgres volume that was purposefully created, got replaced by a `catalog-service_polar-postgres`
after a `docker compose up -d` inside the `cnsia/catalog-service/` where the `docker-compose.yml` is at.
The old volume is simply called **polar-postgres** and was made on the command line with
`docker volume create polar-postgres`
We can inspect both:
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ docker volume inspect polar-postgres
[
  {
  "CreatedAt": "2023-07-16T17:01:25+02:00",
  "Driver": "local",
  "Labels": null,
  "Mountpoint": "/var/lib/docker/volumes/polar-postgres/_data",
  "Name": "polar-postgres",
  "Options": null,
  "Scope": "local"
  }
]
willem@linux-laptop:~/git/cnsia/catalog-service$ docker volume inspect catalog-service_polar-postgres
[
  {
  "CreatedAt": "2023-07-15T22:15:32+02:00",
  "Driver": "local",
  "Labels": {
    "com.docker.compose.project": "catalog-service",
    "com.docker.compose.version": "2.19.1",
    "com.docker.compose.volume": "polar-postgres"
    },
  "Mountpoint": "/var/lib/docker/volumes/catalog-service_polar-postgres/_data",
  "Name": "catalog-service_polar-postgres",
  "Options": null,
  "Scope": "local"
  }
]
```
It appears that any docker compose deployment has a project name that defaults to the name of the parent directory
the docker-compose.yml file is located in, therefore in our case its `catalog-service` and this can also be surmised
from the value of the `com.docker.compose.project` label.
Then apparently, whenever we define a volume inside the `docker-compose.yml` its name becomes a combination of
`${com.docker.compose.project}_${volume-reference}` and this is also reflected in the Mountpoint.

Let's investigate whether this could be made more explicit in the `docker-compose.yml`, perhaps by using a `mount`
where we could use type bind instead of volume. In `bind` you would specify the path to the source location 
(i.e. the Mountpoint, instead of the volume's name).
This page [https://docs.docker.com/engine/storage/volumes/](https://docs.docker.com/engine/storage/volumes/) recommends
using volumes though, as they are managed by Docker and are a safer option when shared among containers.

At [https://docs.docker.com/engine/storage/volumes/#use-a-volume-with-docker-compose](https://docs.docker.com/engine/storage/volumes/#use-a-volume-with-docker-compose)
it is mentioned that Running docker compose up for the first time creates a volume. Docker reuses the same volume when 
you run the command subsequently.
You can create a volume directly outside of Compose using `docker volume create` and then reference it inside 
`docker-compose.yaml` as follows:
```yaml
services:
  frontend:
    image: node:lts
    volumes:
      - myapp:/home/node/app
volumes:
  myapp:
    external: true
    name: actual-name-used-at-volume-create
```
If we had used the `external: true` option then we probably had not dealt with the unexpected prefixing of the volume's
`Name` and `Mountpoint` with `${com.docker.compose.project}_`
See 
[https://docs.docker.com/reference/compose-file/volumes/#name](https://docs.docker.com/reference/compose-file/volumes/#name)

```bash
[
    {
        "CreatedAt": "2024-08-24T17:50:31+02:00",
        "Driver": "local",
        "Labels": {
            "com.docker.compose.project": "catalog-service",
            "com.docker.compose.version": "2.29.1",
            "com.docker.compose.volume": "polar-postgres"
        },
        "Mountpoint": "/var/lib/docker/volumes/catalog-service_polar-postgres/_data",
        "Name": "catalog-service_polar-postgres",
        "Options": null,
        "Scope": "local"
    }
]

```

## Excercise:
## extract data from current postgres db (volume) & set up a new compose config with an independent external volume

### Overview of Steps:
1. figure out how to create a datadump within the container with an interactive cli session
   1.1. [https://www.postgresql.org/docs/current/app-pgdump.html](https://www.postgresql.org/docs/current/app-pgdump.html)
2. find a way to extract the dump file from the container's filesystem
   probably, by writing it to an extra volume for file exchange
3. create a separate Flyway V4__*.sql script from the data insert bit of the db dump
   (with a little alteration to be recognizably different from the original). We will need to use a new image of
4. For the new Flyway scripts to work we need a new Docker image of catalog-service. I propose 
   `'ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT'` 
5. Let's see if we can update the dependencies for `'ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT'` as much as
   possible
6. Create the external volume and specify this one in `docker-compose.yml`
7. In `docker-compose.yml` update the images of both the `catalog-service` and the `polar-postgres` containers.
8. Test the result all records should have version 1 (and the same current creation and modification timestamp)

### Step 1: The datadump
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
- later we did another complete dump, because we needed to know how the sequence was connected to the `books.id` column.

### Copy the dump file from the running container in another terminal
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ docker cp polar-postgres:/tmp/polardb_catalog-data-inserts.sql \
 /home/willem/git/cnsia/catalog-service/polardb_catalog-dumps/polardb_catalog-data-inserts.sql
Successfully copied 4.1kB to /home/willem/git/cnsia/catalog-service/polardb_catalog-dumps/polardb_catalog-data-inserts.sql
willem@linux-laptop:~/git/cnsia/catalog-service$ 

```
## 5.4 Managing databases in production with Flyway


## List of consulted resources
- [https://hub.docker.com/_/postgres](https://hub.docker.com/_/postgres)
- [https://www.composerize.com/](https://www.composerize.com/)
- [https://www.postgresql.org/docs/current/ddl-constraints.html](https://www.postgresql.org/docs/current/ddl-constraints.html)
- [https://www.postgresql.org/docs/current/datatype-numeric.html](https://www.postgresql.org/docs/current/datatype-numeric.html)
- [https://stackoverflow.com/questions/48629799/postgres-image-is-not-creating-database](https://stackoverflow.com/questions/48629799/postgres-image-is-not-creating-database)
- [https://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html](https://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html)
- [https://www.baeldung.com/maven-run-single-test](https://www.baeldung.com/maven-run-single-test)
- [https://java.testcontainers.org/](https://java.testcontainers.org/)
  - [https://java.testcontainers.org/modules/databases/jdbc/](https://java.testcontainers.org/modules/databases/jdbc/)
- [https://stackoverflow.com/questions/70017400/how-to-have-a-custom-equals-hashcode-for-records](https://stackoverflow.com/questions/70017400/how-to-have-a-custom-equals-hashcode-for-records)

- [https://flathub.org/apps/io.dbeaver.DBeaverCommunity](https://flathub.org/apps/io.dbeaver.DBeaverCommunity)
- [https://mvnrepository.com/artifact/org.flywaydb/flyway-core](https://mvnrepository.com/artifact/org.flywaydb/flyway-core)
- [https://stackoverflow.com/questions/53172123/flyway-found-non-empty-schemas-public-without-schema-history-table-use-bas](https://stackoverflow.com/questions/53172123/flyway-found-non-empty-schemas-public-without-schema-history-table-use-bas)
- [https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.data-migration](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.data-migration)
- [https://www.postgresql.org/docs/current/ddl-alter.html](https://www.postgresql.org/docs/current/ddl-alter.html)
- 