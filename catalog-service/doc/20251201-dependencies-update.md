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

### Building a new image from the docker file:
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