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

# `pg_dump` sessions

## Getting a complete dump

### Creating the dump
```bash
willem@linux-laptop:~/git/cnsia$ docker exec -it polar-postgres bash
root@f37fa4b4075a:/# psql -U user -d polardb_catalog -c "select isbn, version, created_date, last_modified_date from book order by last_modified_date desc"
     isbn      | version |        created_date        |     last_modified_date     
---------------+---------+----------------------------+----------------------------
 9781633438958 |       2 | 2024-08-24 16:15:13.119058 | 2024-08-24 16:38:37.975843
 1234567891    |       2 | 2024-08-24 16:28:52.935271 | 2024-08-24 16:31:28.118619
 9781617298425 |       1 | 2024-08-24 16:29:42.681757 | 2024-08-24 16:29:42.681757
 9781617298424 |       1 | 2024-08-24 16:24:43.214021 | 2024-08-24 16:24:43.214021
(4 rows)

root@f37fa4b4075a:/# cd tmp/
root@f37fa4b4075a:/tmp# ls -la
total 8
drwxrwxrwt 1 root root 4096 Sep  7  2023 .
drwxr-xr-x 1 root root 4096 Aug 25 18:27 ..
root@f37fa4b4075a:/tmp# pg_dump polardb_catalog -U user --inserts > polardb_catalog-all.sql
root@f37fa4b4075a:/tmp# ls -la
total 16
drwxrwxrwt 1 root root 4096 Aug 25 20:42 .
drwxr-xr-x 1 root root 4096 Aug 25 18:27 ..
-rw-r--r-- 1 root root 4817 Aug 25 20:42 polardb_catalog-all.sql
root@f37fa4b4075a:/tmp# 
exit
willem@linux-laptop:~/git/cnsia$ 
```

### Copying the dumpfile from the running container to your project on the Docker (local)host
```bash
willem@linux-laptop:~/git/cnsia$ docker cp polar-postgres:/tmp/polardb_catalog-all.sql \
 /home/willem/git/cnsia/catalog-service/polardb_catalog-dumps/polardb_catalog-all.sql
Successfully copied 6.66kB to /home/willem/git/cnsia/catalog-service/polardb_catalog-dumps/polardb_catalog-all.sql
willem@linux-laptop:~/git/cnsia$ 
```
