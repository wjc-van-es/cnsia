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

# Chapter 7: Kubernetes fundamentals for Spring Boot
#### Overview of content
- Main features of Kubernetes
- creating and managing
  - Pods
  - Deployments
  - Services
- graceful shutdown
- scaling
- Automate local development workflow with Tilt
- Visualize workloads with Octant
- Validate Kubernetes manifests

## 7.1 Moving from Docker to Kubernetes
This section mainly deals with what kubernetes delivers what Docker cannot: orchestrating a deployment on a cluster
of multiple servers and maintaining that deployment or even _scale_ it up on demand, even if problems occur (_resilience_).

### Docker setup on a single machine Docker host
!["Docker daemon can only manage resources on the singe machine where it was installed, called the Docker host."](/home/willem/git/cnsia/doc/images/Docker_setup.png)
Docker daemon can only manage resources on the singe machine where it was installed, called the Docker host.

### Kubernetes setup on a cluster that consists of multiple machines
![](/home/willem/git/cnsia/doc/images/Kubernetes_setup.png)
- clients interact with the _Kubernetes Control Plane_ (through kubectl or a web console)
- _Cluster_ consists of a set of nodes where containerized applications can run. Usually the K8s control plane is on a
  separate node and the other nodes are worker nodes where the containerized apps are deployed on
- _Control Plane_ exposes the API and interfaces to define, deploy and manage the lifecycle of Pods
- _working nodes_ provide the necessary cpu, memory, network and storage for containers to run and communicate over a
  network.
- _Pod_ the smallest deployable unit containing at least one application (OCI / Docker) container.

### 7.1.1 Working with a local Kubernetes cluster
Furthermore, in 7.1.1 installation and configuration of a local test 'cluster' with minikube is explained. 
For details see [k8s-minikube.md](k8s-minikube.md).

- A profile named 'polar' has been established.
- Starting minikube with the 'polar' profile:
  ```bash
  $ minikube start -p polar
  ```
  
- kubectl diagnostics
  - `kubectl get nodes`
  - `kubectl config get-contexts`
  - `kubectl config current-context` - if there are more contexts present, check which one is currently used
  - `kubectl config use-context polar` - change the current context whenever necessary
- Running the minikube dashboard for the 'polar' profile:
  ```bash
  $ minikube dashboard -p polar
  ```
  - Stop it with CTRL-C
- Stopping the minikube with the 'polar' profile:
  ```bash
  $ minikube stop -p polar
  ``` 

### 7.1.2 Managing data services in a local cluster
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
- We can also look into the logs of the pod (from any dir) with
  ```bash
  $ kubectl logs deployment/polar-postgres
  ```

- And we can see the pods on the minikube dashboard under:
  - **Workloads/Deployments**
  - **Workloads/Pods**
  - **Workloads/Replica Sets**
  - **Service/Services**
    - here we see that there are no external endpoint defined, so the database may be accessed from within the cluster,
      but not from the localhost
  - Under **_Config and Storage/Persistent Volume Claims_** is where we would see the configuration of a kind of volume
    mapping that we had configured for our docker compose deployment to persist database transactions between sessions.

- To undeploy the database run
  ```bash
  willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ kubectl delete -f services
  deployment.apps "polar-postgres" deleted
  service "polar-postgres" deleted
  ```

## 7.2 Kubernetes Deployments for Spring Boot
A Spring Boot application on Kubernetes is still packaged as a container, but it runs in a Pod controlled by a 
Deployment object.
### 7.2.1 From containers to Pods
A Pod is the smallest Kubernetes object that represents a set of running containers in a cluster. Usually, there will be
a single primary container (with your application) in a pod. However, it may contain additional helper containers with
features like:
- perform initialization tasks,
- logging,
- security or
- monitoring

### 7.2.2 Controlling Pods with Deployments
A Deployment is an object that manages the life cycle of a stateless, replicated application, with each replica running
within a separate pod. The replicas are distributed among the available worker nodes of the cluster for better
resilience. Deployment objects facilitate
- upgrade roll out without downtime,
- roll back in case of an error,
- pause and resume upgrades,
- manage replication, through a _ReplicaSet_, which ensures the desired number of pods are up and running and 
  distributed among all available worker nodes of your cluster.

![](/home/willem/git/cnsia/doc/images/Deployment_replicaSet_Pods.png)

_A deployment object is defined in a manifest file (usually in yaml format) in which you can declare the desired state of your
system._ The orchestrator will figure out _how_ to achieve this.
- _Controllers_ compare the desired state with the actual state and act whenever those two don't match. 
- Deployments and ReplicaSets are both controller objects.

### 7.2.3 Creating a Deployment for a Spring Boot application

We will create a deployment of our catalog-service application, which is defined in
[../catalog-service/k8s/deployment.yml](../catalog-service/k8s/deployment.yml)

#### The four main sections of a manifest file

1. `apiVersion` - the version of the Kubernetes API used to create this object
2. `kind` - What kind of object you want to create, e.g.
   1. Deployment
   2. Service
   3. Pod
   4. ReplicaSet
3. `metadata` - to uniquely identify the object. It takes a set of labels (key/value pairs)
4. `spec` - to declare the desired state for your object. This contains
   1. `selector` part to identify, which objects to scale by a ReplicaSet
   2. `template` part specifying the desired pods and containers

#### Actual deployment workflow
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
  
## 7.3 Service discovery and load balancing
This is about how an application instance running on a pod is able to find and communicate with application instances
of another service.

### 7.3.1 Understanding service discovery and load balancing
When an application needs to call a backing service, it performs a _lookup_ in the registry to determine which IP 
address to contact. If multiple instances are available, a _load-balancing_ strategy is applied to distribute the 
workload across them.

The two main service discovery patterns are
- client-side
  - applications need to be registered with a service registry upon start up and unregistered when shutting down.
  - A native service registry like Netflix Eureka has to be configured and added to your Spring application
  - furthermore, correct integration needs to be added to all deployed applications
- server-side
  - the service registry is managed and updated by the deployment platform (The Kubernetes Control Plane)
  - No integration code is needed within the application, which yields a better separation of concerns

### 7.3.2  Client-side service discovery and load balancing

### 7.3.3 Server-side service discovery and load balancing
The Kubernetes implementation of service discovery based on service objects
- you can define a _Service_ in a (yaml format) manifest file
- A _Service_ definition exposes a set of pods on which the application is deployed as a network service.
  - coupling is achieved by using labels, whereby both the Pods and the service share the same application label
- A service has a long lifespan and is assigned a fixed IP address to forward any connection to the service to
  a corresponding _kube-proxy_.
- The _kube-proxy_ knows all available replicas and adopts a load-balancing strategy based on
  - the type of service
  - the proxy configuration
- The _kube-proxy_ helps the service to forward to one of the replicas, without the need of any DNS resolution.
- This is all transparant to the deployed Spring Boot applications.


![](/home/willem/git/cnsia/doc/images/Server-side_service_discovery_&_load_balancing.png)

### 7.3.4 Exposing Spring Boot applications with Kubernetes Services
there are different types of Services, depending on which access policy you want to enforce for the application.
The default and most common type is called _ClusterIP_, and it exposes a set of Pods to the cluster. This is what makes 
it possible for Pods to communicate with each other (for example, Catalog Service and PostgreSQL).
Four pieces of information characterize a ClusterIP Service:
- The selector label used to match all the Pods that should be targeted and exposed by the Service
- The network protocol used by the Service
- The port on which the Service is listening (we’re going to use port 80 for all our application Services)
- The targetPort, which is the port exposed by the targeted Pods to which the Service will forward requests.
![](/home/willem/git/cnsia/doc/images/ClusterIP_service.png)

#### DEFINING A SERVICE MANIFEST WITH YAML
- see [../catalog-service/k8s/service.yml](../catalog-service/k8s/service.yml) for the definition.

#### Activate the service with `kubectl apply -f k8s/service.yml` (from the right directory)
and you see that the label `app=catalog-service` now comprises a fourth part: `service/catalog-service`
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ kubectl apply -f k8s/service.yml

willem@linux-laptop:~/git/cnsia$ kubectl get all -l app=catalog-service
NAME                                  READY   STATUS    RESTARTS        AGE
pod/catalog-service-f55595cf8-6g2zb   1/1     Running   5 (5m40s ago)   6h45m

NAME                      TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)   AGE
service/catalog-service   ClusterIP   10.110.244.126   <none>        80/TCP    4h11m

NAME                              READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/catalog-service   1/1     1            1           6h45m

NAME                                        DESIRED   CURRENT   READY   AGE
replicaset.apps/catalog-service-f55595cf8   1         1         1       6h45m

```

#### Do a port forward to be able to contact the service from the host environment
The port forwards allow you to use 
- Insomnia on the catalog-service REST API `http://localhost:9001/` and 
- DBeaver with the polar-postgres database with `jdbc:postgresql://localhost:5432/polardb_catalog`
```bash
kubectl port-forward service/catalog-service 9001:80
kubectl port-forward service/polar-postgres 5432:5432
```
- The process started by the kubectl port-forward command will keep running until you explicitly stop it with Ctrl-C. 
  Until then, you’ll need to open another Terminal window if you want to run CLI commands.

#### The communication between an external client and the catalog-service and polar-postgres db within the cluster
![](/home/willem/git/cnsia/doc/images/network-communication-route.png)

The Catalog Service application is exposed to your local machine through port forwarding. Both Catalog Service and 
PostgreSQL are exposed to the inside of the cluster through the cluster-local hostname, IP address, and port assigned 
to the Service objects.

## 7.4 Scalability and disposability
High availability can be achieved by deploying multiple instances of an application. 
The application needs to be scalable and for this to work
- to be disposed off and replaced quickly when it enters a faulty state,
- therefore, it needs to be stateless as well.

### 7.4.1 Ensuring disposability: Fast startup
Spring Boot improves upon fast startup times with every new release.

For serverless applications fast startup times are most important (see chapter 16)

### 7.4.2  Ensuring disposability: Graceful shutdown
- Graceful shutdown means that the application 
  - stops accepting new requests, 
  - completes all requests already in progress and
  - closes all shared resources like database connections.
- The default behavior is that Spring Boot will immediately stop after receiving a termination signal (`SIGTERM`)
- We will set a graceful shutdown with a 15s grace period for the _catalog-service_ application within
  [../catalog-service/src/main/resources/application.yml](../catalog-service/src/main/resources/application.yml) `spring.lifecycle.timeout-per-shutdown-phase`
  and set `server.shutdown` to `graceful`. This means that 
  - after the Spring Boot application receives a `SIGTERM` it will attempt a graceful server shutdown
  - and that 15 s after a `SIGTERM` is sent by Kubernetes and a pod is still running it will receive a `SIGKILL` to
    force the pod's termination.
- Kubernetes won't send new requests to a pod it has sent a `SIGTERM`.
- Then we will update the image tag to `0.0.4-SNAPSHOT` by changing the maven artifact version value in the
  [../catalog-service/pom.xml](../catalog-service/pom.xml)
- To [../catalog-service/k8s/deployment.yml](../catalog-service/k8s/deployment.yml) 
  - we add a _`preStop` hook_ `spec/template/spec/containers[0]/lifecycle/prestop/exec/command` with value 
   `command: [ "sh", "-c", "sleep 5" ]`, which makes Kubernetes wait for 5s before issuing the `SIGTERM` signal
   to the pod.
  - This delay gives Kubernetes enough time to spread the news of eminent pod termination across the cluster so 
    all components involved will already have stopped sending new requests to the failing pod before it receives a
    `SIGTERM` signal.
- Now redo the steps described in 
  [7.2.3 Creating a Deployment for a Spring Boot application](#723-creating-a-deployment-for-a-spring-boot-application)
  - with the modification that the application version will now be `0.0.4-SNAPSHOT` and the image reference in
    [../catalog-service/k8s/deployment.yml](../catalog-service/k8s/deployment.yml)
    now referring to the image `ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT`

### 7.4.3 Scaling Spring Boot applications
If you want to scale the ReplicaSet with the _catalog service_ from one pod to two pods we only need to specify
`spec/replicas` value from 1 to 2 within [../catalog-service/k8s/deployment.yml](../catalog-service/k8s/deployment.yml). This will take effect after
calling `~/git/cnsia/catalog-service$ kubectl apply -f k8s/deployment.yml`. Kubernetes will then notice that the number
desired pods for catalog service has increased from 1 to 2 and will make it so as you can check with:
`kubectl get pods -l app=catalog-service`


---

### NOTE: loading the new version of the local image on minikube polar profile currently doesn't work anymore
The new image tagged `0.0.4-SNAPSHOT` build locally during section 7.4 could not be loaded onto the minikube cluster
with `minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT`
This problem is fairly persistent.
See [minikube-problem-loading-local-image.md](minikube-problem-loading-local-image.md) and 
[minikube-problem-loading-local-image_continued.md](minikube-problem-loading-local-image_continued.md)
for details.

#### A work-around 
- __pushing the new image to my private `ghcr.io/wjc-van-es/` repository and__
- __Enable minikube to pull images from `ghcr.io/wjc-van-es` with the `registry-creds` addon__
- This way could still deploy and test our new modifications successfully with the latest image after all.
- See for a detailed description: [minikube-configure-ghcr-repo.md](minikube-configure-ghcr-repo.md)
  
---

## Undeploying and stopping the minikube cluster
1. `~/git/cnsia/catalog-service$ kubectl delete -f k8s/`
2. `~/git/polar-deployment/kubernetes/platform/development$ kubectl delete -f services/`
3. Stop the Dashboard from the right terminal with `CTRL-C`
4. `$ minikube stop -p polar`
