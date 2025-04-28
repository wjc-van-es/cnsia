<style>
body {
  font-family: "Gentium Basic", Cardo , "Linux Libertine o", "Palatino Linotype", Cambria, serif;
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

# Maven

## Installed with SDKMan!
```bash
willem@linux-laptop:~/git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service$ sdk list maven
willem@linux-laptop:~/git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service$ sdk list maven | cat -
================================================================================
Available Maven Versions
================================================================================
     4.0.0-alpha-5       3.6.0               3.0.4                              
     4.0.0-alpha-4       3.5.4                                                  
     3.9.1               3.5.3                                                  
     3.9.0               3.5.2                                                  
     3.8.8               3.5.0                                                  
     3.8.7               3.3.9                                                  
     3.8.6               3.3.3                                                  
     3.8.5               3.3.1                                                  
     3.8.4               3.2.5                                                  
     3.8.3               3.2.3                                                  
     3.8.2               3.2.2                                                  
     3.8.1               3.2.1                                                  
     3.6.3               3.1.1                                                  
     3.6.2               3.1.0                                                  
     3.6.1               3.0.5                                                  

================================================================================
+ - local version
* - installed
> - currently in use
================================================================================
willem@linux-laptop:~/git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service$ sdk install maven 3.9.1
willem@linux-laptop:~/git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service$ sdk list maven | cat -
================================================================================
Available Maven Versions
================================================================================
     4.0.0-alpha-5       3.6.0               3.0.4                              
     4.0.0-alpha-4       3.5.4                                                  
   * 3.9.1               3.5.3                                                  
     3.9.0               3.5.2                                                  
     3.8.8               3.5.0                                                  
     3.8.7               3.3.9                                                  
     3.8.6               3.3.3                                                  
     3.8.5               3.3.1                                                  
     3.8.4               3.2.5                                                  
     3.8.3               3.2.3                                                  
     3.8.2               3.2.2                                                  
     3.8.1               3.2.1                                                  
     3.6.3               3.1.1                                                  
     3.6.2               3.1.0                                                  
     3.6.1               3.0.5                                                  

================================================================================
+ - local version
* - installed
> - currently in use
================================================================================
willem@linux-laptop:~/git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service$ mvn --version
Command 'mvn' not found, but can be installed with:
sudo apt install maven
willem@linux-laptop:~$ sdk use maven 3.9.1
Setting maven version 3.9.1 as default.

Using maven version 3.9.1 in this shell.
$ sdk list maven | cat -
================================================================================
Available Maven Versions
================================================================================
     4.0.0-alpha-5       3.6.0               3.0.4                              
     4.0.0-alpha-4       3.5.4                                                  
 > * 3.9.1               3.5.3                                                  
     3.9.0               3.5.2                                                  
     3.8.8               3.5.0                                                  
     3.8.7               3.3.9                                                  
     3.8.6               3.3.3                                                  
     3.8.5               3.3.1                                                  
     3.8.4               3.2.5                                                  
     3.8.3               3.2.3                                                  
     3.8.2               3.2.2                                                  
     3.8.1               3.2.1                                                  
     3.6.3               3.1.1                                                  
     3.6.2               3.1.0                                                  
     3.6.1               3.0.5                                                  

================================================================================
+ - local version
* - installed
> - currently in use
================================================================================
$ mvn --version
Command 'mvn' not found, but can be installed with:
sudo apt install maven
willem@linux-laptop:~$ sdk selfupdate
No update available at this time.
willem@linux-laptop:~$ sdk flush
21 archive(s) flushed, freeing 689M     /home/willem/.sdkman/tmp.
15 archive(s) flushed, freeing 64K      /home/willem/.sdkman/var/metadata.
willem@linux-laptop:~$ sdk current maven

Using maven version 3.9.1
willem@linux-laptop:~$ mvn -version
Apache Maven 3.9.1 (2e178502fcdbffc201671fb2537d0cb4b4cc58f8)
Maven home: /home/willem/.sdkman/candidates/maven/current
Java version: 11.0.18, vendor: Eclipse Adoptium, runtime: /home/willem/.sdkman/candidates/java/11.0.18-tem
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.19.0-38-generic", arch: "amd64", family: "unix"
willem@linux-laptop:~$ cd git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service
~/git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service$ sdk env

Using java version 17.0.6-tem in this shell.
willem@linux-laptop:~/git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service$ mvn -version
Apache Maven 3.9.1 (2e178502fcdbffc201671fb2537d0cb4b4cc58f8)
Maven home: /home/willem/.sdkman/candidates/maven/current
Java version: 17.0.6, vendor: Eclipse Adoptium, runtime: /home/willem/.sdkman/candidates/java/17.0.6-tem
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.19.0-38-generic", arch: "amd64", family: "unix"
willem@linux-laptop:~/git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service$ 

```

The `mvn` command took some time being recognized. We needed `sdk use maven 3.9.1` to set a default version and 
`sdk flush` was also necessary to get the `mvn` command to be recognized in a bash terminal.
`mvn -version` reveals that the default jdk is used, but we can simply change that with either a
`sdk use java 17.0.6-tem` or `sdk env` from a directory where a `.sdkmanrc` is present with the desired java version
declared.

## Important commands for this Spring Boot project
For maven we can choose to use a terminal with the local `./mvnw` shell script distributed with the catalog-service.zip 
distribution created with [Spring Initialzr](https://start.spring.io/) or the `mvn` command made available from the
SDKMan! installation, (but don´t forget to run `sdk env` or `sdk use java 17.0.6-tem` first, because this Spring Boot
project expects to use a JDK 17). Finally, we can use a maven command from IntelliJ's Maven tool window. We changed the
maven version from the bundled one to the SDKMan! installation at `~/.sdkman/candidates/maven/3.9.1` with 
*File | Settings | Build, Execution, Deployment | Build Tools | Maven | Runner* set to 'Use Project's JDK', that should
be `~/.sdkman/candidates/java/17.0.6-tem`.


Gradle                      | Maven
--------------------------- | ---------------------------------
`./gradlew clean`           | `./mvnw clean`
`./gradlew build`           | `./mvnw install`
`./gradlew test`            | `./mvnw test`
`./gradlew bootJar`         | `./mvnw spring-boot:repackage`
`./gradlew bootRun`         | `./mvnw spring-boot:run`
`./gradlew bootBuildImage`  | `./mvnw spring-boot:build-image`