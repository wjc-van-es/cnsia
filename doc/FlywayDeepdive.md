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

# Flyway deep dive

## Purpose

### Occasion
In [ch05_excercise_migrate_to_external_volume.md](ch05_excercise_migrate_to_external_volume.md) we describe how we 
migrated the small example database from one docker based Postgres 15.4 server instance to an upgraded Docker based
Postgres 16.4 server instance, going from one Docker volume where the state was persisted to a new one. This was needed,
because the files inside the old volumes were created with Postgres 15.x and were not compatible with versions 16.x.

We already had Flyway migration scripts in place for the DDL part of the schema, but we also wanted to migrate the
data that was inserted during a couple of test runs. So for this we needed to add at least one extra migration script
containing the DML part of the schema. Our approach was to use the Postgres `pg_dump` CLI tool executed in an 
interactive bash session on the running Docker container where the Postgres service proces was running. See
[ch05_excercise_migrate_to_external_volume.md](ch05_excercise_migrate_to_external_volume.md#starting-an-interactive-bash-session-into-the-running-postgresql-db-container-to-produce-the-datadump).

Then we proceeded with writing the Flyway migration scripts by hand and executing them in a Docker compose set up, 
consisting of 
- a PostgreSQL container from the latest postgres:16.4 image and a new external volume and
- a REST API application container implemented with SpringBoot in which Flyway was configured to run (only when the)
  database was behind on the available migration scripts. As is recorded in the `public.flyway_schema_history` table.

### Reasons to get more Flyway experience
We would like to use Flyway as a backup system to be able to migrate also valuable production like database schemas
- from one version to the next when the data storage files are incompatible between the two versions
- from one computer to the next
- from one vendor to another (if we manage to keep the migration scripts vendor-agnostic)

Our experience is limited to Flyway integrated as a maven plugin or in the SpringBoot application. In these settings
the Flyway migrations are tightly coupled to the application that uses the database during its development and build
cycles.

I would like to create more independent Flyway projects to manage production-like schema migrations, where the 
conservation of valuable data is important.

In normal development it is absolutely no problem to recreate the whole schema from scratch every time you start up
your environment to run some tests. As a matter of fact, this is the best way to guarantee consistent, reproducible
test results, because your database will always be in a predictable initial state. We, however, want to manage 
production database migrations with Flyway as well. And production data is valuable and should be recreated in such a 
way that new data for which no migration script is added yet is inadvertently overwritten.

## Flyway quick overview
- [https://documentation.red-gate.com/fd/getting-started-with-flyway-184127223.html](https://documentation.red-gate.com/fd/getting-started-with-flyway-184127223.html)
- [https://documentation.red-gate.com/fd/why-database-migrations-184127574.html](https://documentation.red-gate.com/fd/why-database-migrations-184127574.html)
- [https://documentation.red-gate.com/fd/getting-started-212140421.html](https://documentation.red-gate.com/fd/getting-started-212140421.html)

### Flyway desktop
- [https://www.youtube.com/watch?v=XQ2leB8dtko](https://www.youtube.com/watch?v=XQ2leB8dtko)
- [https://documentation.red-gate.com/fd/getting-started-212140421.html](https://documentation.red-gate.com/fd/getting-started-212140421.html)

### Flyway API
- [https://documentation.red-gate.com/fd/flyway-cli-and-api-183306238.html](https://documentation.red-gate.com/fd/flyway-cli-and-api-183306238.html)
- [https://documentation.red-gate.com/fd/quickstart-api-184127575.html](https://documentation.red-gate.com/fd/quickstart-api-184127575.html)
- [https://www.red-gate.com/hub/university/courses/flyway/getting-started-with-flyway](https://www.red-gate.com/hub/university/courses/flyway/getting-started-with-flyway)
- [https://documentation.red-gate.com/fd/welcome-to-flyway-184127914.html](https://documentation.red-gate.com/fd/welcome-to-flyway-184127914.html)

## Strategy
We still have to figure out an approach for maintaining our current and future databases. Maybe this will help:
[https://documentation.red-gate.com/fd/choosing-the-right-approach-with-flyway-246972498.html](https://documentation.red-gate.com/fd/choosing-the-right-approach-with-flyway-246972498.html)

Furthermore, we like what we saw in the Flyway desktop demo. Especially, how we could first establish a baseline for an 
existing production database and create a script to recreate that baseline. This would bypass the need for creating
baseline scripts by hand based on database dumps like we did earlier.

Furthermore, We feel we would have the most control over any database maintenance and migration by using the Flyway 
Java API. So we need to find out, how much of the Flyway desktop functionality we actually need and how we could 
create this with our on management program using the Java API.


