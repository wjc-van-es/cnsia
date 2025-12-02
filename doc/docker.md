<style>
body {
  font-family: "Spectral", "Gentium Basic", Cardo , "Linux Libertine o", "Palatino Linotype", Cambria, serif;
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

</style>

# Docker

## Creating an image with Spring Boot
Traditionally we create a docker image from a docker file. This yields the most control on things like the base image.
However, here we are presented with a Spring Boot maven plugin to directly build the image and storing it in our local 
repository, without a Dockerfile as input.

```bash
# Make sure a JDK 17 is used in the current terminal 
$ sdk env

Using java version 17.0.6-tem in this shell.

$ mvn spring-boot:build-image

# see what image was build:  catalog-service:0.0.1-SNAPSHOT
$ docker image ls
REPOSITORY                        TAG              IMAGE ID       CREATED         SIZE
...
paketobuildpacks/builder          base             ae737b9d787f   43 years ago    1.16GB
catalog-service                   0.0.1-SNAPSHOT   95fa030bf990   43 years ago    274MB

# To discard older versions of images
$ docker image prune
``` 

## Running a container instance based on the catalog-service image

```bash
$ docker run --rm --name catalog-service -p 8080:8080 catalog-service:0.0.1-SNAPSHOT
```
- `docker run` Runs a container from an image
- `--rm` Remove the container after its execution completes
- `--name catalog-service` Name of the container
- `-p 8080:8080` Exposes service outside the container through port 8080
- `catalog-service:0.0.1-SNAPSHOT` Name and version of the image to run as container instance.

## Creating and running a docker-compose.yml file from the above command
In the simple example above, where we run one container from one image with simple configuration (basically just port 
forwarding) than a docker command works just fine.
However, if the configuration becomes more complex, with multiple containers from multiple images, where some volumes
and networking needs to be configured as well the `docker run` command will become rather complex.
In such cases, you can store the configuration in a docker-compose.yml file, which can be stored in our version control 
system and will yield repeatable and consistent results.

At [https://www.composerize.com/](https://www.composerize.com/) we can convert a docker run command in a 
`docker-compose.yml` file as a starting point to use docker compose.

We can find the result in [../catalog-service/docker-compose.yml](../catalog-service/docker-compose.yml).

## Adding a chapter suffix to our application name will change the name of our image
As we add n-oo directories for our own development building experiments, and we could get a clash between
different chapter versions of the same application modules like catalog-service we will choose to extend them
with the chapter number n as -n suffix in 
- the application root dir name (within `Chaptern/n-oo/`), 
- the project/artifactId and
- project/name values of the pom.xml file.

Therefore, for Chapter03 the name of the root dir, maven artifacId and project name will be `catalog-service-03`.
The consequence is that the name of the Docker image will be
`catalog-service-03:0.0.1-SNAPSHOT` and of the fat jar `target/catalog-service-03-0.0.1-SNAPSHOT.jar`.

Beware, that the `-03` suffix in the image name should also be reflected in the 
[../Chapter03/03-oo/catalog-service-03/docker-compose.yml](../Chapter03/03-oo/catalog-service-03/docker-compose.yml)

## Running with docker compose
```bash
# make sure you are in the directory with the docker-compose.yml
# -d makes sure you are in detached mode and the command returns without logging evrything from the running containers
# use -f to specify a docker compose file when the docker-compose.yml has another name or is in another location
$ docker compose up -d

# To stop the execution of the container(s) run
$ docker compose down
```

## Other useful docker commands
```bash
# To see all running containers
$ docker ps
# To see both running and stopped containers
$ docker ps -a

# a list of volumes
$ docker volume list 
$ docker volume ls

# remove dangling volumes 
$ docker volume prune

# remove specified volumes
$ docker volume rm $(docker volume ls -q --filter dangling=true)

# list dangling volumes except "polar-postgres" to make sure it isn't removed accidentally anyway
# see https://unix.stackexchange.com/questions/299462/how-to-filter-out-lines-of-a-command-output-that-occur-in-a-text-file
$ docker volume ls -q --filter dangling=true | grep -v -x -F "polar-postgres"

# -v means select the inverse, -x means exact match
# For selecting any volumes with postgres in the name use:
$ docker volume ls -q | grep -F "postgres"

# For multiple literal fragments we can use multiple fragments like
$ docker volume ls -q | grep "postgres\|jenkins\|qm1\|polar\|kustomize\|car-rental"

# The oposite will then be 
$ docker volume ls -q | grep -v "postgres\|jenkins\|qm1\|polar\|kustomize\|car-rental"

# even more safely
docker volume ls -q --filter dangling=true | grep -v "postgres\|jenkins\|qm1\|polar\|kustomize\|car-rental"

# safe removal then becomes
$ docker volume rm $(docker volume ls -q --filter dangling=true | grep -v "postgres\|jenkins\|qm1\|polar\|kustomize\|car-rental")

# remove specified dangling volumes except "polar-postgres"
$ docker volume rm $(docker volume ls -q --filter dangling=true | grep -v -x -F "polar-postgres")

# open an interactive bash session in a running container specified by name (i.e. here it's catalog-service)
$ docker exec -it catalog-service bash
```

### Safe removal of volumes, exclude patterns in names of volumes you want to keep:
- All volumes you want to keep: `docker volume ls -q | grep "postgres\|jenkins\|qm1\|polar\|kustomize\|car-rental"`
- The reversal with `grep` argument `-v` all you want to throw away (and filter for dangling):
  `docker volume ls -q --filter dangling=true | grep -v "postgres\|jenkins\|qm1\|polar\|kustomize\|car-rental")`
- The actual removal command:
- `docker volume rm $(docker volume ls -q --filter dangling=true | grep -v "postgres\|jenkins\|qm1\|polar\|kustomize\|car-rental")`

```bash
$ docker volume inspect catalog-service_polar-postgres 
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