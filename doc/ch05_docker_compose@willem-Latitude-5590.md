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
    [catalog-service/docker-compose.yml](../catalog-service/docker-compose.yml) we change 
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
    - it appears that with `ss -tnlp` we can see the exposure of port `5433` (see next section).
  - Moreover, we can do `HTTP GET requests` [http://localhost:9001/books](http://localhost:9001/books) or 
    [http://localhost:9001/books/9781633438958](http://localhost:9001/books/9781633438958)
  - Also on `@linux-laptop` we reconfigured DBeaver to connect with port 5433 instead of 5432.
  - The _catalog-service_ container still connects to the _polar-postgres_ container through the internal 5432 port.
    Therefore, no configuration changes are made in
    [../catalog-service/src/main/resources/application.yml#spring.datasource.url](../catalog-service/src/main/resources/application.yml)
  - and therefore, no new image needs to be build either only the compose file was modified.

#### How to find all ports exposed by Docker: `ss -tunlp` instead of `nmap localhost`
- `t` for TCP
- `u` for UDP
- `n` for numeric
- `l` for listening ports
- `p` for PID's

If we use `docker ps` we see:
```bash
CONTAINER ID   IMAGE                                               COMMAND                  CREATED         STATUS         PORTS                                                                                  NAMES
57c01ed1ec49   ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT   "/cnb/process/web"       7 seconds ago   Up 7 seconds   0.0.0.0:8001->8001/tcp, :::8001->8001/tcp, 0.0.0.0:9001->9001/tcp, :::9001->9001/tcp   catalog-service
ae305863106f   postgres:16.4                                       "docker-entrypoint.s…"   7 seconds ago   Up 7 seconds   0.0.0.0:5433->5432/tcp, [::]:5433->5432/tcp                                            polar-postgres
```
So when we apply `cc -tnlp` we should see lines with these values for the `Local Address:Port` column
- `0.0.0.0:8001->8001/tcp` and `:::8001`
- `0.0.0.0:9001` and `:::9001`
- `0.0.0.0:5433` and `[::]:5433`

This is the result with all other results filtered out (with grep `ss -tnlp | grep -E 'State|:8000|:9001|:5432|:5433'`),
so this appears correct.
```bash
State            Recv-Q           Send-Q                          Local Address:Port                      Peer Address:Port          Process                                                                                                          
LISTEN           0                4096                                  0.0.0.0:8001                           0.0.0.0:*                                                            
LISTEN           0                4096                                  0.0.0.0:5433                           0.0.0.0:*                                                                      
LISTEN           0                4096                                  0.0.0.0:9001                           0.0.0.0:*                                                                                                                                                         
LISTEN           0                4096                                     [::]:8001                              [::]:*                                                            
LISTEN           0                4096                                     [::]:5433                              [::]:*                                                            
LISTEN           0                4096                                     [::]:9001                              [::]:*                                                             
```

If we also omit the `-l` option we see connections that were established:
```bash
willem@linux-laptop:~/git/cnsia$ ss -ntp
State      Recv-Q      Send-Q                                    Local Address:Port                               Peer Address:Port       Process                                       
ESTAB      0           0                                             127.0.0.1:5433                                  127.0.0.1:34484                                                     
ESTAB      0           0                                            172.19.0.1:57568                                172.19.0.2:5432                                                 
ESTAB      0           0                                            172.19.0.1:57560                                172.19.0.2:5432                                                          
ESTAB      0           0                                             127.0.0.1:5433                                  127.0.0.1:34474                                                
ESTAB      0           0                                             127.0.0.1:5433                                  127.0.0.1:34458                                                       
ESTAB      0           0                                    [::ffff:127.0.0.1]:34458                        [::ffff:127.0.0.1]:5433        users:(("java",pid=7507,fd=20))                
ESTAB      0           0                                    [::ffff:127.0.0.1]:34484                        [::ffff:127.0.0.1]:5433        users:(("java",pid=7507,fd=144))         
ESTAB      0           0                                    [::ffff:127.0.0.1]:34474                        [::ffff:127.0.0.1]:5433        users:(("java",pid=7507,fd=130))              
```

After we run this again just after calling the REST API GET `http://localhost:9001/books/` on a Chrome browser:
```bash
willem@linux-laptop:~/git/cnsia$ ss -ntp | grep -E 'State|:9001|:5432|:5433'
State      Recv-Q Send-Q                          Local Address:Port                   Peer Address:Port Process                             
ESTAB      0      0                                   127.0.0.1:5433                      127.0.0.1:34484                                    
ESTAB      0      0                                  172.19.0.1:57568                    172.19.0.2:5432                                     
ESTAB      0      0                                  172.19.0.1:57560                    172.19.0.2:5432                                     
CLOSE-WAIT 0      0                                  172.19.0.1:40812                    172.19.0.3:9001                                     
ESTAB      0      0                                  172.19.0.1:57548                    172.19.0.2:5432                                     
ESTAB      0      0                                   127.0.0.1:5433                      127.0.0.1:34474                                    
ESTAB      0      0                                   127.0.0.1:5433                      127.0.0.1:34458                                    
FIN-WAIT-2 0      0                                       [::1]:9001                          [::1]:38472                                    
FIN-WAIT-2 0      0                                       [::1]:9001                          [::1]:38486                                    
ESTAB      0      0                          [::ffff:127.0.0.1]:34458            [::ffff:127.0.0.1]:5433  users:(("java",pid=7507,fd=20))    
ESTAB      0      0                          [::ffff:127.0.0.1]:34484            [::ffff:127.0.0.1]:5433  users:(("java",pid=7507,fd=144))   
CLOSE-WAIT 1      0                                       [::1]:38472                         [::1]:9001  users:(("chrome",pid=9395,fd=26))  
ESTAB      0      0                          [::ffff:127.0.0.1]:34474            [::ffff:127.0.0.1]:5433  users:(("java",pid=7507,fd=130))   
CLOSE-WAIT 1      0                                       [::1]:38486                         [::1]:9001  users:(("chrome",pid=9395,fd=33))  
  
willem@linux-laptop:~/git/cnsia$ 

```
- You see Chrome browser interacting with the REST service with `[::1]:9001` as Peer
- We see `172.19.0.2:5432` being connected to local  `172.19.0.1:57568/57560/57548`, which should represent internal
  calls from the _catalog-service_ container to the _polar-postgres_ container
- The java process with pid=7507 connections with peer `[::ffff:127.0.0.1]:5433` should be the _DBeaver_ client
  on localhost connecting with the _Postgres DB_ `polardb_catalog` on the _polar-postgres_ container.
  - executing `ps ax o user,pid,time,cmd:50 | grep -E ' PID | 7507 '` reveals 'dbeaver' inside in the 'CMD' column.

#### Resources
- [https://stackoverflow.com/questions/44509452/docker-ps-shows-different-ports-than-nmap](https://stackoverflow.com/questions/44509452/docker-ps-shows-different-ports-than-nmap)
- [https://linuxize.com/post/grep-multiple-patterns/](https://linuxize.com/post/grep-multiple-patterns/)
- [https://monovm.com/blog/linux-process-list/](https://monovm.com/blog/linux-process-list/)
- [https://unix.stackexchange.com/questions/403026/how-can-i-increase-the-ps-column-width-for-column-user](https://unix.stackexchange.com/questions/403026/how-can-i-increase-the-ps-column-width-for-column-user)

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

