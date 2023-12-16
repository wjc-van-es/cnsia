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

# Kubernetes setup with minikube

## General introduction
[https://minikube.sigs.k8s.io/docs/](https://minikube.sigs.k8s.io/docs/)

## Installation
[https://minikube.sigs.k8s.io/docs/start/](https://minikube.sigs.k8s.io/docs/start/)
```bash
$ curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
$ sudo install minikube-linux-amd64 /usr/local/bin/minikube 
```

## Start & Stop
```bash
$ minikube start
```

### diagnostics
With kubectl already installed we can use it
```bash
$ kubectl get po -A
NAMESPACE     NAME                               READY   STATUS    RESTARTS   AGE
kube-system   coredns-5d78c9869d-pbhnt           1/1     Running   0          65s
kube-system   etcd-minikube                      1/1     Running   0          77s
kube-system   kube-apiserver-minikube            1/1     Running   0          79s
kube-system   kube-controller-manager-minikube   1/1     Running   0          77s
kube-system   kube-proxy-zpkg4                   1/1     Running   0          65s
kube-system   kube-scheduler-minikube            1/1     Running   0          77s
kube-system   storage-provisioner                1/1     Running   0          76s
willem@linux-laptop:~/git/cnsia$ 

```
To spin up a web-console dashboard
```bash
$ minikube dashboard
```
this is attached to the console, use Ctrl-C to stop

To Halt the minikube processes:
```bash
$ minikube stop
```

## The `polar` minikube profile
first time starting minikube & creating the `polar` profile
```bash
$ minikube start --cpus 2 --memory 4g --driver docker --profile polar
```

### investigate available contexts
```bash
willem@linux-laptop:~/git/cnsia$ kubectl get nodes
NAME    STATUS   ROLES           AGE   VERSION
polar   Ready    control-plane   36s   v1.27.4
willem@linux-laptop:~/git/cnsia$ kubectl config get-contexts
CURRENT   NAME    CLUSTER   AUTHINFO   NAMESPACE
*         polar   polar     polar      default
willem@linux-laptop:~/git/cnsia$ kubectl config current-context
polar
willem@linux-laptop:~/git/cnsia$ kubectl get po -A
NAMESPACE     NAME                            READY   STATUS    RESTARTS      AGE
kube-system   coredns-5d78c9869d-6wjb6        1/1     Running   0             11m
kube-system   etcd-polar                      1/1     Running   0             11m
kube-system   kube-apiserver-polar            1/1     Running   0             11m
kube-system   kube-controller-manager-polar   1/1     Running   0             11m
kube-system   kube-proxy-44f8g                1/1     Running   0             11m
kube-system   kube-scheduler-polar            1/1     Running   0             11m
kube-system   storage-provisioner             1/1     Running   1 (10m ago)   11m

willem@linux-laptop:~/git/cnsia$ 
```

### running the dashboard must include the profile now
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ minikube dashboard
🤷  The control plane node must be running for this command
👉  To start a cluster, run: "minikube start"
willem@linux-laptop:~/git/cnsia/catalog-service$ minikube dashboard --profile polar
🔌  Enabling dashboard ...
    ▪ Using image docker.io/kubernetesui/dashboard:v2.7.0
    ▪ Using image docker.io/kubernetesui/metrics-scraper:v1.0.8
💡  Some dashboard features require the metrics-server addon. To enable all features please run:

        minikube -p polar addons enable metrics-server  


🤔  Verifying dashboard health ...
🚀  Launching proxy ...
🤔  Verifying proxy health ...
🎉  Opening http://127.0.0.1:33537/api/v1/namespaces/kubernetes-dashboard/services/http:kubernetes-dashboard:/proxy/ in your default browser...
Gtk-Message: 19:37:38.372: Failed to load module "xapp-gtk3-module"
Stub sandbox ignoring command: /app/extra/nacl_helper
[2:2:0910/193738.556179:ERROR:nacl_fork_delegate_linux.cc(313)] Bad NaCl helper startup ack (0 bytes)
Gtk-Message: 19:37:38.588: Failed to load module "xapp-gtk3-module"
Opening in existing browser session.

```

## Stopping minikube must include the profile info
To halt the cluster is the same with either `--profile polar` or `-p profile`
```bash
$ minikube stop -p profile
```

### Now diagnostics are not available
```bash
willem@linux-laptop:~/git/cnsia$ minikube stop -p polar
✋  Stopping node "polar"  ...
🛑  Powering off "polar" via SSH ...
🛑  1 node stopped.
willem@linux-laptop:~/git/cnsia$ kubectl config current-context
error: current-context is not set
willem@linux-laptop:~/git/cnsia$ kubectl config get-contexts
CURRENT   NAME   CLUSTER   AUTHINFO   NAMESPACE
willem@linux-laptop:~/git/cnsia$ kubectl get po -A
E0910 19:46:38.347545   50877 memcache.go:265] couldn't get current server API group list: Get "http://localhost:8080/api?timeout=32s": dial tcp 127.0.0.1:8080: connect: connection refused
E0910 19:46:38.348105   50877 memcache.go:265] couldn't get current server API group list: Get "http://localhost:8080/api?timeout=32s": dial tcp 127.0.0.1:8080: connect: connection refused
E0910 19:46:38.349365   50877 memcache.go:265] couldn't get current server API group list: Get "http://localhost:8080/api?timeout=32s": dial tcp 127.0.0.1:8080: connect: connection refused
E0910 19:46:38.350980   50877 memcache.go:265] couldn't get current server API group list: Get "http://localhost:8080/api?timeout=32s": dial tcp 127.0.0.1:8080: connect: connection refused
E0910 19:46:38.352462   50877 memcache.go:265] couldn't get current server API group list: Get "http://localhost:8080/api?timeout=32s": dial tcp 127.0.0.1:8080: connect: connection refused
The connection to the server localhost:8080 was refused - did you specify the right host or port?
willem@linux-laptop:~/git/cnsia$ 

```

## restarting a minikube cluster whose profile is already defined

Now if you restart the profile no configuration arguments are necessary just the name with either `--profile polar` or
`-p polar`
```bash
$ minikube start -p polar
```

## Managing images within a minikube cluster
See [https://minikube.sigs.k8s.io/docs/commands/image/](https://minikube.sigs.k8s.io/docs/commands/image/)

### List available images with `minikube image ls`
- Don't forget to specify our profile with `-p polar`

### Add a locally built image to a minikube cluster with `minikube image load <image-name>`
- Again, don't forget to specify our profile with `-p polar`.
- <image-name> should be the full image name you have found with `docker image ls`.
#### e.g.
```bash
willem@linux-laptop:~/git/cnsia$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.27.4
registry.k8s.io/kube-proxy:v1.27.4
registry.k8s.io/kube-controller-manager:v1.27.4
registry.k8s.io/kube-apiserver:v1.27.4
registry.k8s.io/etcd:3.5.7-0
registry.k8s.io/coredns/coredns:v1.10.1
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/library/postgres:15.4
docker.io/kubernetesui/metrics-scraper:<none>
docker.io/kubernetesui/dashboard:<none>
willem@linux-laptop:~/git/cnsia$ minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.3-SNAPSHOT
willem@linux-laptop:~/git/cnsia$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.27.4
registry.k8s.io/kube-proxy:v1.27.4
registry.k8s.io/kube-controller-manager:v1.27.4
registry.k8s.io/kube-apiserver:v1.27.4
registry.k8s.io/etcd:3.5.7-0
registry.k8s.io/coredns/coredns:v1.10.1
ghcr.io/wjc-van-es/catalog-service:0.0.3-SNAPSHOT
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/library/postgres:15.4
docker.io/kubernetesui/metrics-scraper:<none>
docker.io/kubernetesui/dashboard:<none>
willem@linux-laptop:~/git/cnsia$ 

```

### Add a remote Docker Hub image to a minikube cluster with `minikube image pull <image-name>`
- Again, don't forget to specify our profile with `-p polar`.

#### e.g. a new postgres image
- See [https://hub.docker.com/_/postgres](https://hub.docker.com/_/postgres)

```bash
willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ minikube -p polar image pull postgres:16.1
willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.27.4
registry.k8s.io/kube-proxy:v1.27.4
registry.k8s.io/kube-controller-manager:v1.27.4
registry.k8s.io/kube-apiserver:v1.27.4
registry.k8s.io/etcd:3.5.7-0
registry.k8s.io/coredns/coredns:v1.10.1
ghcr.io/wjc-van-es/catalog-service:0.0.3-SNAPSHOT
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/library/postgres:16.1
docker.io/library/postgres:15.4
docker.io/kubernetesui/metrics-scraper:<none>
docker.io/kubernetesui/dashboard:<none>

```
Don't forget to change the image version in the polar-deployment project
[../../polar-deployment/kubernetes/platform/development/services/postgresql.yml](../../polar-deployment/kubernetes/platform/development/services/postgresql.yml)

