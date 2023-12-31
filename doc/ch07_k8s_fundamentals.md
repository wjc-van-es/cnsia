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
</style>

# Chapter 7: Kubernetes fundamentals for Spring Boot

## 7.1 Moving from Docker to Kubernetes
This section mainly deals with what kubernetes delivers what Docker cannot: orchestrating a deployment on a cluster
and maintaining that deployment or even scale it up on demand, even if problems occur.

### 7.1.1 Working with a local Kubernetes cluster
Furthermore, in 7.1.1 installation and configuration of a local test 'cluster' with minikube is explained. 
For details see [k8s-minikube.md](k8s-minikube.md).

- A profile named 'polar' has been established.
- Starting minikube with the 'polar' profile:
  ```bash
  $ minikube start -p polar
  ```
- Running the minikube dashboard for the 'polar' profile:
  ```bash
  $ minikube dashboard -p polar
  ```
  - Stop it with CTRL-C
- Stopping the minikube with the 'polar' profile:
  ```bash
  $ minikube stop -p polar
  ``` 

### Managing data services in a local cluster
7.1.2 goes on to demonstrate a deployment of PostgreSQL on our local minikube cluster.
We have created a separate git repo for deployments:
[https://github.com/wjc-van-es/polar-deployment](https://github.com/wjc-van-es/polar-deployment)
- To deploy this database:
  From our local copy of this project in a terminal:
  ```bash
  willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ kubectl apply -f services
  deployment.apps/polar-postgres created
  service/polar-postgres created
  willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ kubectl get pod
  NAME                              READY   STATUS    RESTARTS   AGE
  polar-postgres-74fc97d96d-fbqlq   1/1     Running   0          75s
    
  ```
- We can also look into the logs of the pod with
  ```bash
  willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ kubectl logs deployment/polar-postgres
  ```

- And we can see the pods on the minikube dashboard under:
  - **Workloads/Deployments**
  - **Workloads/Pods**
  - **Workloads/Replica Sets**
  - **Service/Services**
    - here we see that there are no external endpoint defined, so the database may be accessed from within the cluster, but
      not from the localhost
  - Under **_Config and Storage/Persistent Volume Claims_** is where we would see the configuration of a kind of volume
    mapping that we had configured for our docker compose deployment to persist database transactions between sessions.

- To undeploy the database run
  ```bash
  willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ kubectl delete -f services
  deployment.apps "polar-postgres" deleted
  service "polar-postgres" deleted
  ```

## 7.2 Kubernetes Deployments for Spring Boot

### 7.2.1 From containers to Pods

### 7.2.2 Controlling Pods with Deployments

### 7.2.3 Creating a Deployment for a Spring Boot application

We will create a deployment of our catalog-service application, which is defined in
[../catalog-service/k8s/deployment.yml](../catalog-service/k8s/deployment.yml)

1. Make sure you create an up-to-date image of the application with `mvn spring-boot:build-image -e`
   (after updating all dependencies in the [../catalog-service/pom.xml](../catalog-service/pom.xml))
2. Check the presence of the new image with `docker image ls` my new one is 
   `ghcr.io/wjc-van-es/catalog-service:0.0.3-SNAPSHOT`
3. Give the `spec.template.spec.containers[0].image` of 
   [../catalog-service/k8s/deployment.yml](../catalog-service/k8s/deployment.yml) the value
   `ghcr.io/wjc-van-es/catalog-service:0.0.3-SNAPSHOT`
4. Start the minikube cluster with `minikube start -p polar`
5. Optionally, start the dasboard with `minikube dashboard -p polar`
6. Manually import the new catalog-image we just created with 
   `minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.3-SNAPSHOT`
7. Check whether it's now available with `minikube -p polar image ls`
8. First deploy the postgres database from the _polar-deployment_ project / git repo with
   `~/git/polar-deployment/kubernetes/platform/development$ kubectl apply -f services/` (make sure you do this from
   the right directory)
9. Finally, deploy _catalog-service_ (also from the right directory) with
   `~/git/cnsia/catalog-service$ kubectl apply -f k8s/deployment.yml`

#### diagnostics
- after deploying the postgres database we can check whether it runs without problems with
  `kubectl get all -l db=polar-postgres`
- e.g.
  ```bash
  willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ kubectl get all -l db=polar-postgres
  NAME                                  READY   STATUS    RESTARTS   AGE
  pod/polar-postgres-7d476d7c74-m9h9l   1/1     Running   0          3m1s
  
  NAME                     TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
  service/polar-postgres   ClusterIP   10.109.137.31   <none>        5432/TCP   3m1s
  
  NAME                             READY   UP-TO-DATE   AVAILABLE   AGE
  deployment.apps/polar-postgres   1/1     1            1           3m1s
  
  NAME                                        DESIRED   CURRENT   READY   AGE
  replicaset.apps/polar-postgres-7d476d7c74   1         1         1       3m1s
  willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$

  ```
- after deploying the catalog-service, we can do the same for the application with:
  `kubectl get all -l app=catalog-service`
- e.g.
  ```bash
  willem@linux-laptop:~/git/cnsia/catalog-service$ kubectl get all -l app=catalog-service
  NAME                                  READY   STATUS    RESTARTS   AGE
  pod/catalog-service-f55595cf8-zwwwq   1/1     Running   0          46s
  
  NAME                              READY   UP-TO-DATE   AVAILABLE   AGE
  deployment.apps/catalog-service   1/1     1            1           46s
  
  NAME                                        DESIRED   CURRENT   READY   AGE
  replicaset.apps/catalog-service-f55595cf8   1         1         1       46s

  ```
- We can troubleshoot by looking at the logging with `kubectl logs deployments/catalog-service`
  
## Undeploying and stopping the minikube cluster
1. `~/git/cnsia/catalog-service$ kubectl apply -f k8s/deployment.yml`
2. `~/git/polar-deployment/kubernetes/platform/development$ kubectl delete -f services/`
3. Stop the Dashboard from the right terminal with `CTRL-C`
4. `$ minikube stop -p polar`
