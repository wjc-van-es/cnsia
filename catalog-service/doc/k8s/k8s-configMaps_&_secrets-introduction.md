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

# Introduction of k8s ConfigMaps and Secrets for our polar-postgres deployment
- When we introduced our polar-postgres deployment to provide a test database we hardcoded all the environment variables
in the yaml defining its deployment & service component: 
[polar-deployment/k8s/platform/dev/services/postgresql.yml](../../../polar-deployment/k8s/platform/dev/services/postgresql.yml)
- However, when we want to reuse the same deployment definitions in different environments, we should decouple the 
  values of these environment variables.
- Moreover, we don't want to provide literal credentials in this way.

## Defining a Config Maps definition for the development environment of polar-postgres



## Defining a Secrets definition for the development environment of polar-postgres
- [https://kubernetes.io/docs/concepts/configuration/secret/](https://kubernetes.io/docs/concepts/configuration/secret/)
- We defined a secrets definition of default type opaque, which has no real encryption out of the box only base64
  encoding.
  - to encode use `echo -n {secret-value} | base64`
    - the `-n` flag is super important, because without it `echo` adds a linefeed character, which leads to false values
    - this happened to us see [catalog-service/k8s/logs-failed.txt](../../k8s/logs-failed.txt)
      ```bash
      Caused by: java.lang.IllegalArgumentException: Prohibited character

        at org.postgresql.shaded.com.ongres.saslprep.SaslPrep.saslPrep(SaslPrep.java:105) ~[postgresql-42.7.3.jar:42.7.3]
      ```
    - see also [https://stackoverflow.com/questions/75458490/using-kubernetes-secret-for-postgresql-env-causes-application-pod-crash](https://stackoverflow.com/questions/75458490/using-kubernetes-secret-for-postgresql-env-causes-application-pod-crash)
  - For the resulting definition see [polar-deployment/k8s/platform/dev/config/polar-postgres-secret.yml](../../../polar-deployment/k8s/platform/dev/config/polar-postgres-secret.yml)

## Changing [`polar-deployment/k8s/platform/dev/services/postgresql.yml`](../../../polar-deployment/k8s/platform/dev/services/postgresql.yml) to link environment vars to the secret entries
fragment of `spec.template.spec.containers[0].env`
```yaml
env:
  - name: POSTGRES_USER
#    value: user # hardcoded value is replaced by reference
    valueFrom:
      secretKeyRef:
        name: polar-postgres-secret
        key: polar-db-user

  - name: POSTGRES_PASSWORD
#    value: password # hardcoded value is replaced by reference
    valueFrom:
      secretKeyRef:
        name: polar-postgres-secret
        key: polar-db-password
```
## Changing [`catalog-service/k8s/deployment.yml`](../../k8s/deployment.yml) to link environment vars to the secret entries
- Here we need to once again override the default property values in 
  [`catalog-service/src/main/resources/application.yml`](../../src/main/resources/application.yml)
  - `spring.datasource.username` override by introducing the environment variable 
    `spec.template.spec.containers[0].env[2].name` with value `SPRING_DATASOURCE_USER` to refer to the 
    `polar-db-user` defined in `polar-postgres-secret`
  - `spring.datasource.password` override by introducing the environment variable
    `spec.template.spec.containers[0].env[3].name` with value `SPRING_DATASOURCE_PASSWORD` to refer to the
    `polar-db-password` defined in `polar-postgres-secret`
```yaml
env:
  - name: BPL_JVM_THREAD_COUNT
    value: "50"
  - name: SPRING_DATASOURCE_URL
    value: jdbc:postgresql://polar-postgres/polardb_catalog

  #           OVERRIDE CREDENTIALS IN catalog-service/src/main/resources/application.yml
  - name: SPRING_DATASOURCE_USER
    valueFrom:
      secretKeyRef:
        name: polar-postgres-secret
        key: polar-db-user
  - name: SPRING_DATASOURCE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: polar-postgres-secret
        key: polar-db-password
```

## Steps to set up minikube profile from scratch
1. `minikube start -p polar`
2. in another terminal `minikube dashboard -p polar`
3. `~/git/cnsia$ kubectl apply -f polar-deployment/k8s/platform/dev/config/polar-postgres-config.yml`
4. `~/git/cnsia$ kubectl apply -f polar-deployment/k8s/platform/dev/config/polar-postgres-secret.yml`
5. `~/git/cnsia$ kubectl apply -f polar-deployment/k8s/platform/dev/services/postgresql.yml`
6. `~/git/cnsia$ kubectl apply -f catalog-service/k8s/deployment.yml`
7. `~/git/cnsia$ kubectl apply -f catalog-service/k8s/service.yml`
8. `~/git/cnsia$ kubectl apply -f catalog-service/k8s/ingress.yml`