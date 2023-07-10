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

# SDKMAN!
## The Software Development Kit Manager
SDKMAN! is a tool for managing parallel versions of multiple Software Development Kits on most Unix based systems.
It provides a convenient Command Line Interface (CLI) and API for installing, switching, removing and listing Candidates.

It's main benefit is the ability to install multiple versions of multiple vendors of a JDK or SDK and being able to 
switch among versions, to choose one as default and to deviate from that default within a single terminal session or
configure a different default at a certain project root directory.

## Documentation
All documentation can be found at [https://sdkman.io/](https://sdkman.io/) with some shortcuts for
- Available JDK distributions [https://sdkman.io/jdks](https://sdkman.io/jdks)
- Available SDKs, including Groovy, Scala, Gradle, Maven and Spark [https://sdkman.io/sdks](https://sdkman.io/sdks)
- Instructions on using its CLI [https://sdkman.io/usage](https://sdkman.io/usage)

## Useful commands

## Marking an installed version as default

```bash
$ sdk default java 11.0.18-tem
# Now whenever you open a new terminal you can see
$ sdk current java

Using java version 11.0.18-tem

# Get the java.home property to know the installation directory
$ java -XshowSettings:properties --version
...
java.home = /home/willem/.sdkman/candidates/java/11.0.18-tem
...

# See all installed java versions
~/.sdkman/candidates/java$ ls -la
total 32
drwxrwxr-x 8 willem willem 4096 apr  9 15:11 .
drwxrwxr-x 4 willem willem 4096 dec 23 15:24 ..
drwxr-xr-x 9 willem willem 4096 jan 18 11:19 11.0.18-tem
drwxr-xr-x 9 willem willem 4096 jan 18 11:13 17.0.6-tem
drwxr-xr-x 9 willem willem 4096 jul 20  2022 18.0.2-tem
drwxr-xr-x 9 willem willem 4096 jan 18 12:56 19.0.2-tem
drwxr-xr-x 9 willem willem 4096 mrt 21 16:52 20-tem
drwxr-xr-x 8 willem willem 4096 jan 18 17:30 8.0.362-tem
lrwxrwxrwx 1 willem willem   11 jan 23 23:09 current -> 11.0.18-tem
~/.sdkman/candidates/java$ echo $JAVA_HOME
/home/willem/.sdkman/candidates/java/current

```


### Creating a .sdkmanrc file in a project root directory
Whenever you want a project to deviate from your default sdk version, you can specify an alternative inside a .sdkmanrc
file inside the project's root directory.
#### Only once per project root directory
```bash
# Make sure you are in the project's root dir, from which you want to open terminals in the future.
$ sdk current java

Using java version 11.0.18-tem
$ sdk use java 17.0.6-tem

Using java version 17.0.6-tem in this shell.

$ sdk env init
.sdkmanrc created.

# Now a .sdkmanrc file is created with the current terminal default version set:
$ cat .sdkmanrc 

# Enable auto-env through the sdkman_auto_env config
# Add key=value pairs of SDKs to use below
java=17.0.6-tem

```

#### For every new terminal opened at the project's root dir
```bash
$ sdk env

Using java version 17.0.6-tem in this shell.

# You can check
$ sdk current java

Using java version 17.0.6-tem

# Or revealing all system properties including the java.home 
$ java -XshowSettings:properties --version

```

## Other useful commands

### Maintaining most recent versions
```bash
# List all available java versions from all vendors
$ sdk list java

# If you want to filter by Dist name e.g. 'tem' for Eclipse Adoptium Temurin
# i.e. when that is the vendor of choice
willem@linux-laptop:~/git/cloud-native-spring-in-action/Chapter02/02-oo/catalog-service$ sdk list java | grep tem
 Temurin       |     | 20           | tem     |            | 20-tem              
               |     | 19.0.2       | tem     |            | 19.0.2-tem          
               |     | 19.0.1       | tem     | installed  | 19.0.1-tem          
               |     | 18.0.2       | tem     | local only | 18.0.2-tem          
               | >>> | 17.0.6       | tem     | installed  | 17.0.6-tem          
               |     | 17.0.5       | tem     | installed  | 17.0.5-tem          
               |     | 11.0.18      | tem     | installed  | 11.0.18-tem         
               |     | 11.0.17      | tem     | installed  | 11.0.17-tem         
               |     | 8.0.362      | tem     |            | 8.0.362-tem         
               |     | 8.0.352      | tem     | installed  | 8.0.352-tem         
               |     | 8.0.345      | tem     |            | 8.0.345-tem         
Omit Identifier to install default version 17.0.6-tem:
    $ sdk install java 17.0.6-tem
## Now we have a clue what is already installed, local only (meaning no longer available for installation, and whether
# there are more recent versions for a LTS version, you have currently installed
# Let's install the latest versions for jdk 8, 19 and 20 
$ sdk install java 8.0.362-tem
$ sdk install java 19.0.2-tem
$ sdk install java 20-tem
$ sdk list java | grep tem
 Temurin       |     | 20           | tem     | installed  | 20-tem              
               |     | 19.0.2       | tem     | installed  | 19.0.2-tem          
               |     | 19.0.1       | tem     | installed  | 19.0.1-tem          
               |     | 18.0.2       | tem     | local only | 18.0.2-tem          
               | >>> | 17.0.6       | tem     | installed  | 17.0.6-tem          
               |     | 17.0.5       | tem     | installed  | 17.0.5-tem          
               |     | 11.0.18      | tem     | installed  | 11.0.18-tem         
               |     | 11.0.17      | tem     | installed  | 11.0.17-tem         
               |     | 8.0.362      | tem     | installed  | 8.0.362-tem         
               |     | 8.0.352      | tem     | installed  | 8.0.352-tem         
               |     | 8.0.345      | tem     |            | 8.0.345-tem         
Omit Identifier to install default version 17.0.6-tem:
    $ sdk install java 17.0.6-tem
## Now in IntelliJ point each major java version to the java.home of its latest installation
## Then we can remove the older versions safely 
$ sdk uninstall java 8.0.352-tem
removed java 8.0.352-tem
$ sdk uninstall java 11.0.17-tem
removed java 11.0.17-tem
$ sdk uninstall java 17.0.5-tem
removed java 17.0.5-tem
$ sdk uninstall java 19.0.1-tem
removed java 19.0.1-tem
$ sdk list java | grep tem
 Temurin       |     | 20           | tem     | installed  | 20-tem              
               |     | 19.0.2       | tem     | installed  | 19.0.2-tem          
               |     | 19.0.1       | tem     |            | 19.0.1-tem          
               |     | 18.0.2       | tem     | local only | 18.0.2-tem          
               | >>> | 17.0.6       | tem     | installed  | 17.0.6-tem          
               |     | 17.0.5       | tem     |            | 17.0.5-tem          
               |     | 11.0.18      | tem     | installed  | 11.0.18-tem         
               |     | 11.0.17      | tem     |            | 11.0.17-tem         
               |     | 8.0.362      | tem     | installed  | 8.0.362-tem         
               |     | 8.0.352      | tem     |            | 8.0.352-tem         
               |     | 8.0.345      | tem     |            | 8.0.345-tem         
Omit Identifier to install default version 17.0.6-tem:
    $ sdk install java 17.0.6-tem

```
