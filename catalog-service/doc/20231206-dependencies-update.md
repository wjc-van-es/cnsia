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
</style>

# Maven (pom) dependencies update on Wednesday, 06-12-2023
I resumed at section _7.2 Kubernetes Deployments for Spring Boot_ with creating the kubernetes deployment spec at 
[`../k8s/deployment.yml`](../k8s/deployment.yml). When I needed to recreate the image, I decided to check for maven dependency updates
## list of updates
- org.springframework.boot:spring-boot-starter-parent:3.1.6
  - from 3.1.4
  - 3.2.0 was already available. but incompatible with the latest Spring Cloud as error logs suggested when running
    [../src/test/java/com/polarbookshop/catalogservice/CatalogServiceApplicationTests.java](../src/test/java/com/polarbookshop/catalogservice/CatalogServiceApplicationTests.java)
- org.testcontainers:testcontainers-bom:1.19.3
  - from 1.19.1
- org.postgresql:postgresql:42.7.0
  - from 42.6.0