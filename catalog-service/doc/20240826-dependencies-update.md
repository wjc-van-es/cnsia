<style>
body {
  font-family: "Gentium Basic", Cardo, "Linux Libertine o", "Palatino Linotype", Cambria, serif;
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

# Maven (pom) dependencies update on monday 26-08-2024

## Approach
I used the initializr [https://start.spring.io/](https://start.spring.io/) and this was the resulting pom:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.3.3</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>demo</name>
	<description>Demo project for Spring Boot</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>21</java.version>
		<spring-cloud.version>2023.0.3</spring-cloud.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jdbc</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>com.okta.spring</groupId>
			<artifactId>okta-spring-boot-starter</artifactId>
			<version>3.0.7</version>
		</dependency>
		<dependency>
			<groupId>org.flywaydb</groupId>
			<artifactId>flyway-core</artifactId>
		</dependency>
		<dependency>
			<groupId>org.flywaydb</groupId>
			<artifactId>flyway-database-postgresql</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter</artifactId>
		</dependency>

		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-testcontainers</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>junit-jupiter</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>postgresql</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

## List of updates

### [pom.xml](../pom.xml)
I adjusted the pom file as much as possible to correspond with the example above

- Java 17 -> Java 21
- org.springframework.boot:spring-boot-starter-parent:3.3.3 (from 3.1.6)
- org.springframework.cloud:spring-cloud-dependencies:2023.0.3 (from 2022.0.4)
- separate org.testcontainers:testcontainers-bom:1.19.3 removed from dependencyManagement
- version specification removed from org.postgresql:postgresql dependency
- org.flywaydb:flyway-database-postgresql dependency added
- org.springframework.cloud:spring-cloud-starter dependency added
- org.testcontainers:junit-jupiter test-scoped dependency added

### [catalog-service/src/test/resources/application-integration.yml](../src/test/resources/application-integration.yml)
- changed postgres test container version from 15.3 to 16.4

## Checks
- `mvn clean package -e` succeeds
- `mvn spring-boot:build-image -e` succeeds
  - `docker image ls` reveals the latest `0.0.5-SNAPSHOT`:
    `ghcr.io/wjc-van-es/catalog-service    0.0.5-SNAPSHOT   c8d7681f5bc3   44 years ago    374MB`
- run all tests in `catalog-service/src/test/java` in IDE context succeeds
- Commit Push fails GitHub Workflow, because 
  [.github/workflows/commit-stage.yml](../../.github/workflows/commit-stage.yml) still references Java 17 instead of 21.
- After commit Push of the updated GitHub workflows file, the build succeeds:
  - [https://github.com/wjc-van-es/cnsia/actions/runs/10568645840](https://github.com/wjc-van-es/cnsia/actions/runs/10568645840)
  - On [https://github.com/wjc-van-es/cnsia/pkgs/container/catalog-service/versions](https://github.com/wjc-van-es/cnsia/pkgs/container/catalog-service/versions)
    the catalog-service is availabe with tag latest (not with 0.0.5-SNAPSHOT for some reason; look into this).