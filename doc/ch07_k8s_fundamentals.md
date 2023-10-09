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