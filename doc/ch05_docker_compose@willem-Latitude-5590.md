<style>
body {
  font-family: "Gentium Basic", Cardo , "Linux Libertine o", "Palatino Linotype", Cambria, serif;
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

# Postgres container set up `@willem-Latitude-5590`

## Modifying [catalog-service/docker-compose.yml](../catalog-service/docker-compose.yml)
We already have a regular PostgreSQL Database server instance running on the `willem-Latitude-5590` laptop.

### Option 1: use different external port exposed to the Docker host
- To see which ports are already in use, hence not available we use `nmap localhost`
- We needed to install this first with `sudo apt install nmap`
- First run:
  ```bash
  willem@willem-Latitude-5590:~$ nmap localhost
  Starting Nmap 7.80 ( https://nmap.org ) at 2024-08-31 21:56 CEST
  Nmap scan report for localhost (127.0.0.1)
  Host is up (0.00015s latency).
  Not shown: 998 closed ports
  PORT     STATE SERVICE
  631/tcp  open  ipp
  5432/tcp open  postgresql

  ```
  - So port 5432 on localhost is already engaged, therefore in 
    [catalog-service/docker-compose.yml](../catalog-service/docker-compose.yml:30) we change 
    `services.postgresql.ports[0].'5432:5432'` to `services.postgresql.ports[0].'5433:5432'` 
- `~/git/cnsia/catalog-service$ docker compose up -d` started the containers without errors
  - Second `nmap loclahost` run:
    ```bash
    willem@willem-Latitude-5590:~$ nmap localhost
    Starting Nmap 7.80 ( https://nmap.org ) at 2024-08-31 22:17 CEST
    Nmap scan report for localhost (127.0.0.1)
    Host is up (0.00015s latency).
    Not shown: 996 closed ports
    PORT     STATE SERVICE
    631/tcp  open  ipp
    5432/tcp open  postgresql
    8001/tcp open  vcom-tunnel
    9001/tcp open  tor-orport
  
    Nmap done: 1 IP address (1 host up) scanned in 0.07 seconds
    willem@willem-Latitude-5590:~$

    ```
  - we see the extra ports for the `catalog-service` (remote debug on `8001` and http server on `9001`), but no mention of
    `5433`
  - However, we can successfully connect to `jdbc:postgresql://localhost:5433/polardb_catalog` with the DBeaver client
    on the docker host
    - TODO, we have to dive into (Docker) networking to find out how this works.
  - Moreover, we can do `HTTP GET requests` [http://localhost:9001/books](http://localhost:9001/books) or 
    [http://localhost:9001/books/9781633438958](http://localhost:9001/books/9781633438958)

### Option 2: shutdown the PostgreSQL server instance on localhost with `sudo systemctl stop postgresql.service`

- Postgres was enabled as systemctl service, which makes it startup automatically at laptop startup
  If this wasn't already the case, this can be achieved with: `sudo systemctl status postgresql.service`
- `sudo systemctl status postgresql.service` to see if the service is running or not
- `sudo systemctl stop postgresql.service` to stop the service when running
- `sudo systemctl start postgresql.service` to start the serice when stopped

#### resources
- `tldr systemctl` command
- `systemctl list-units --type=service` command
- `systemctl list-unit-files` command
- [https://www.baeldung.com/linux/postgresql-start](https://www.baeldung.com/linux/postgresql-start)
- [https://www.digitalocean.com/community/tutorials/how-to-use-systemctl-to-manage-systemd-services-and-units](https://www.digitalocean.com/community/tutorials/how-to-use-systemctl-to-manage-systemd-services-and-units)

#### example run
```bash

willem@willem-Latitude-5590:~$ sudo systemctl status postgresql.service 
[sudo] password for willem: 
● postgresql.service - PostgreSQL RDBMS
     Loaded: loaded (/lib/systemd/system/postgresql.service; enabled; vendor preset: enabled)
     Active: active (exited) since Sat 2024-08-31 21:33:01 CEST; 2h 2min ago
    Process: 1062 ExecStart=/bin/true (code=exited, status=0/SUCCESS)
   Main PID: 1062 (code=exited, status=0/SUCCESS)
        CPU: 1ms

aug 31 21:33:01 willem-Latitude-5590 systemd[1]: Starting PostgreSQL RDBMS...
aug 31 21:33:01 willem-Latitude-5590 systemd[1]: Finished PostgreSQL RDBMS.

willem@willem-Latitude-5590:~$ sudo systemctl stop postgresql.service 

willem@willem-Latitude-5590:~$ sudo systemctl status postgresql.service 
○ postgresql.service - PostgreSQL RDBMS
     Loaded: loaded (/lib/systemd/system/postgresql.service; enabled; vendor preset: enabled)
     Active: inactive (dead) since Sat 2024-08-31 23:40:48 CEST; 31s ago
    Process: 1062 ExecStart=/bin/true (code=exited, status=0/SUCCESS)
   Main PID: 1062 (code=exited, status=0/SUCCESS)
        CPU: 1ms

aug 31 21:33:01 willem-Latitude-5590 systemd[1]: Starting PostgreSQL RDBMS...
aug 31 21:33:01 willem-Latitude-5590 systemd[1]: Finished PostgreSQL RDBMS.
aug 31 23:40:48 willem-Latitude-5590 systemd[1]: postgresql.service: Deactivated successfully.
aug 31 23:40:48 willem-Latitude-5590 systemd[1]: Stopped PostgreSQL RDBMS.

willem@willem-Latitude-5590:~$ nmap localhost
Starting Nmap 7.80 ( https://nmap.org ) at 2024-08-31 23:41 CEST
Nmap scan report for localhost (127.0.0.1)
Host is up (0.00015s latency).
Not shown: 997 closed ports
PORT     STATE SERVICE
631/tcp  open  ipp
8001/tcp open  vcom-tunnel
9001/tcp open  tor-orport

Nmap done: 1 IP address (1 host up) scanned in 0.08 seconds

willem@willem-Latitude-5590:~$ sudo systemctl start postgresql.service 

willem@willem-Latitude-5590:~$ sudo systemctl status postgresql.service 
● postgresql.service - PostgreSQL RDBMS
     Loaded: loaded (/lib/systemd/system/postgresql.service; enabled; vendor preset: enabled)
     Active: active (exited) since Sat 2024-08-31 23:42:18 CEST; 5s ago
    Process: 13030 ExecStart=/bin/true (code=exited, status=0/SUCCESS)
   Main PID: 13030 (code=exited, status=0/SUCCESS)
        CPU: 3ms

aug 31 23:42:18 willem-Latitude-5590 systemd[1]: Starting PostgreSQL RDBMS...
aug 31 23:42:18 willem-Latitude-5590 systemd[1]: Finished PostgreSQL RDBMS.

willem@willem-Latitude-5590:~$ nmap localhost
Starting Nmap 7.80 ( https://nmap.org ) at 2024-08-31 23:42 CEST
Nmap scan report for localhost (127.0.0.1)
Host is up (0.00021s latency).
Not shown: 996 closed ports
PORT     STATE SERVICE
631/tcp  open  ipp
5432/tcp open  postgresql
8001/tcp open  vcom-tunnel
9001/tcp open  tor-orport

Nmap done: 1 IP address (1 host up) scanned in 0.09 seconds
willem@willem-Latitude-5590:~$ 

```

