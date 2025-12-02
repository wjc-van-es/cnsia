<style>
body {
  font-family: "Spectral", "Gentium Basic", Cardo , "Linux Libertine o", "Palatino Linotype", Cambria, serif;
  font-size: 100% !important;
  padding-right: 12%;
}
code {
  padding: 0.25em;
	
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

# Maven (pom) dependencies update on monday 01-12-2025
- see [../pom.xml](../pom.xml)

## Approach
- Most recent Spring Boot 3 version as migration to 4 means a lot of broken interfaces
  - `org.springframework.boot:spring-boot-starter-parent:3.5.8`
  - This is compatible with `org.springframework.cloud:spring-cloud-dependencies:2025.0.0`
- [https://www.perplexity.ai/search/which-org-springframework-clou-LxCTHQBMSrKNg8LKnyXBrA#0](https://www.perplexity.ai/search/which-org-springframework-clou-LxCTHQBMSrKNg8LKnyXBrA#0)
- [https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.5.8](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.5.8)
- [https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2025.0.0](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-dependencies/2025.0.0)
- [https://www.reddit.com/r/java/comments/1d43rbh/how_can_java_21_virtual_thread_replace_reactive/](https://www.reddit.com/r/java/comments/1d43rbh/how_can_java_21_virtual_thread_replace_reactive/)

## Reconfiguring stuff
- This code base hasn't been actively maintained for a while
- So after updating dependencies and running code we have to check a number of things
- A lot of dependencies seem to be fixed under the umbrella of the `org.springframework.boot:spring-boot-starter-parent`
- When I run the tests some failed indicating the `org.springframework.cloud:spring-cloud-dependencies` version was 
  incompatible. So I searched for the version compatible with 
  - `org.springframework.boot:spring-boot-starter-parent:3.5.8`: 
  - `org.springframework.cloud:spring-cloud-dependencies:2025.0.0`
- Next when I run the tests I see a Postgres db is made in a container using docker image: postgres:16.4
- Things like Hikari Connection Pool and Flyway have their versions derived from the
  `org.springframework.boot:spring-boot-starter-parent`, I suppose.

### Building a new image from the docker file, first attempt:
- `~/git/cnsia/catalog-service$ docker build -t ghcr.io/wjc-van-es/catalog-service:0.0.6-SNAPSHOT .`
- `docker image ls` reveals `ghcr.io/wjc-van-es/catalog-service:0.0.6-SNAPSHOT`
- In [../docker-compose.yml](../docker-compose.yml) we change the `services.catalog-service.image` to 
  `ghcr.io/wjc-van-es/catalog-service:0.0.6-SNAPSHOT`
- We can see in the same docker-compose.yml that the `polar-postgres` container refers to an external volume named
  `polar-postgres-16` as we can derive from the values of
  - `services.postgresql.container_name`,
  - `services.postgresql.volumes[0]` and
  - `volumes.polar-postgres.name`
- On `@mint-22` this volume is not present yet as we can see by `docker volume ls`, therefore we create it once with
  - create a new volume `docker volume create polar-postgres-16`
  - with `docker volume inspect polar-postgres-16` we can check it has been created successfully and where it's located
  - Now any changes in the database will be persisted between sessions.
- Running the docker compose, however, starts the postgres container, but not the `catalog-service` container.
- the log complains some Spring library cannot be found
- Running [`com.polarbookshop.catalogservice.CatalogServiceApplication`](../src/main/java/com/polarbookshop/catalogservice/CatalogServiceApplication.java)
  Gave a problem with the incompatible Spring Cloud with Spring Boot incompatibility complaint we had before with mvn package
  `mvn package`, which was probably due to IntelliJ holding older library versions in the cache. So after invalidating
  caches it worked
- we also had to change the portnumber in `spring.datasource.url` to 5433 to conform with the configuration of
  `services.postgresql.ports` in [../docker-compose.yml](../docker-compose.yml) to be able to connect from the catalog-service running on
  the localhost (in a Java process on IntelliJ) to the database inside the container with the 5433 port exposed through
  port forwarding.

### Building a new image with `org.springframework.boot:spring-boot-maven-plugin`
- see also [../../doc/docker.md](../../doc/docker.md)
- Instead of direct execution of the `docker build` command we use the 
  `org.springframework.boot:spring-boot-maven-plugin` declared in [../pom.xml](../pom.xml) instead

#### Preparations
- update Java with
  - `sdk list java | grep tem` to see whether a new 21 is available
  - `sdk install java 21.0.9-tem` and allow it to be the default
- `~/git/cnsia/catalog-service$ mvn clean package -e`

#### execute `mvn spring-boot:build-image`
- `~/git/cnsia/catalog-service$ mvn spring-boot:build-image`
  - The tag of the image is derived from the `module.image.name` property declared in [../pom.xml](../pom.xml) 

#### Modifying `docker-compose.yml`
- Let's update the postgres image as well as the `service-catalog` image.
- `docker pull amd64/postgres:18.1-trixie`
- create a new external volume named `polar-postgres-18` with `docker volume create polar-postgres-18`
  - check with `docker volume inspect polar-postgres-18`
- In [`../docker-compose.yml`](../docker-compose.yml)
  - Modify the value of `services.catalog-service.image`
  - Modify the value of `services.postgresql.image` to `amd64/postgres:18.1-trixie`
  - Change the value of `volumes.polar-postgres.name` to `polar-postgres-18`
- When everything works remove the old volume:
  - `docker volume rm polar-postgres-16`
- Check with `docker volume ls`

### Volume mapping changed in postgres:18
- When starting `docker compose up -d` Both containers crashed
- After reading their logs and [https://github.com/docker-library/postgres/pull/1259](https://github.com/docker-library/postgres/pull/1259)
  We changed `services.postgresql.volumes` in [`../docker-compose.yml`](../docker-compose.yml)
    - from `- polar-postgres:/var/lib/postgresql/data` to `- polar-postgres:/var/lib/postgresql/18/docker`

<details>

#### details
```
(base) willem@mint-22:~/git/cnsia$ docker exec -it polar-postgres sh
# ls -la
total 64
drwxr-xr-x   1 root root 4096 Dec  2 16:49 .
drwxr-xr-x   1 root root 4096 Dec  2 16:49 ..
lrwxrwxrwx   1 root root    7 Nov  7 17:40 bin -> usr/bin
drwxr-xr-x   2 root root 4096 Nov  7 17:40 boot
drwxr-xr-x   5 root root  340 Dec  2 16:49 dev
drwxr-xr-x   2 root root 4096 Nov 18 04:58 docker-entrypoint-initdb.d
-rwxr-xr-x   1 root root    0 Dec  2 16:49 .dockerenv
drwxr-xr-x   1 root root 4096 Dec  2 16:49 etc
drwxr-xr-x   2 root root 4096 Nov  7 17:40 home
lrwxrwxrwx   1 root root    7 Nov  7 17:40 lib -> usr/lib
lrwxrwxrwx   1 root root    9 Nov  7 17:40 lib64 -> usr/lib64
drwxr-xr-x   2 root root 4096 Nov 17 00:00 media
drwxr-xr-x   2 root root 4096 Nov 17 00:00 mnt
drwxr-xr-x   2 root root 4096 Nov 17 00:00 opt
dr-xr-xr-x 526 root root    0 Dec  2 16:49 proc
drwx------   1 root root 4096 Nov 18 04:58 root
drwxr-xr-x   1 root root 4096 Nov 18 04:58 run
lrwxrwxrwx   1 root root    8 Nov  7 17:40 sbin -> usr/sbin
drwxr-xr-x   2 root root 4096 Nov 17 00:00 srv
dr-xr-xr-x  13 root root    0 Dec  2 16:49 sys
drwxrwxrwt   2 root root 4096 Nov 17 00:00 tmp
drwxr-xr-x   1 root root 4096 Nov 17 00:00 usr
drwxr-xr-x   1 root root 4096 Nov 17 00:00 var
# printenv
HOSTNAME=c355864f001c
HOME=/root
PG_VERSION=18.1-1.pgdg13+2
TERM=xterm
POSTGRES_PASSWORD=password
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/postgresql/18/bin
POSTGRES_USER=user
LANG=en_US.utf8
PG_MAJOR=18
GOSU_VERSION=1.19
PWD=/
POSTGRES_DB=polardb_catalog
PGDATA=/var/lib/postgresql/18/docker
# cd $PGDATA
# pwd
/var/lib/postgresql/18/docker
# ls -la
total 136
drwx------ 19 postgres root      4096 Dec  2 16:49 .
drwxr-xr-x  3 root     root      4096 Dec  2 16:49 ..
drwx------  6 postgres postgres  4096 Dec  2 16:45 base
drwx------  2 postgres postgres  4096 Dec  2 16:50 global
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_commit_ts
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_dynshmem
-rw-------  1 postgres postgres  5753 Dec  2 16:45 pg_hba.conf
-rw-------  1 postgres postgres  2681 Dec  2 16:45 pg_ident.conf
drwx------  4 postgres postgres  4096 Dec  2 16:54 pg_logical
drwx------  4 postgres postgres  4096 Dec  2 16:45 pg_multixact
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_notify
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_replslot
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_serial
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_snapshots
drwx------  2 postgres postgres  4096 Dec  2 16:49 pg_stat
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_stat_tmp
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_subtrans
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_tblspc
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_twophase
-rw-------  1 postgres postgres     3 Dec  2 16:45 PG_VERSION
drwx------  4 postgres postgres  4096 Dec  2 16:49 pg_wal
drwx------  2 postgres postgres  4096 Dec  2 16:45 pg_xact
-rw-------  1 postgres postgres    88 Dec  2 16:45 postgresql.auto.conf
-rw-------  1 postgres postgres 32319 Dec  2 16:45 postgresql.conf
-rw-------  1 postgres postgres    36 Dec  2 16:49 postmaster.opts
-rw-------  1 postgres postgres    99 Dec  2 16:49 postmaster.pid
# exit
(base) willem@mint-22:~/git/cnsia$ 
```

</details>
