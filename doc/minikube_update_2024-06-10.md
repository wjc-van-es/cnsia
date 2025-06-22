<style>
body {
  font-family: "Spectral", "Gentium Basic", Cardo, "Linux Libertine o", "Palatino Linotype", Cambria, serif;
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

# Minikube update

## update & renew polar profile

- add the registry-creds plugin to be able to pull images from my ghcr.io account
```bash
willem@linux-laptop:~/git/cnsia$ minikube version
minikube version: v1.32.0
commit: 8220a6eb95f0a4d75f7f2d7b14cef975f050512d
willem@linux-laptop:~/git/cnsia$ minikube update-check
CurrentVersion: v1.32.0
LatestVersion: v1.33.1
willem@linux-laptop:~/git/cnsia$ cd ../..
willem@linux-laptop:~$ curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 91.1M  100 91.1M    0     0  12.3M      0  0:00:07  0:00:07 --:--:-- 13.6M
willem@linux-laptop:~$ sudo install minikube-linux-amd64 /usr/local/bin/minikube
[sudo] password for willem: 
willem@linux-laptop:~$ minikube version
minikube version: v1.33.1
commit: 5883c09216182566a63dff4c326a6fc9ed2982ff
willem@linux-laptop:~$ minikube delete -p polar
🔥  Deleting "polar" in docker ...
🔥  Deleting container "polar" ...
🔥  Removing /home/willem/.minikube/machines/polar ...
💀  Removed all traces of the "polar" cluster.
willem@linux-laptop:~$ minikube start --cpus 2 --memory 4g --driver docker -p polar
😄  [polar] minikube v1.33.1 on Ubuntu 22.04
✨  Using the docker driver based on user configuration
📌  Using Docker driver with root privileges
👍  Starting "polar" primary control-plane node in "polar" cluster
🚜  Pulling base image v0.0.44 ...
💾  Downloading Kubernetes v1.30.0 preload ...
    > preloaded-images-k8s-v18-v1...:  342.90 MiB / 342.90 MiB  100.00% 5.88 Mi
    > gcr.io/k8s-minikube/kicbase...:  481.58 MiB / 481.58 MiB  100.00% 8.04 Mi
🔥  Creating docker container (CPUs=2, Memory=4096MB) ...
🐳  Preparing Kubernetes v1.30.0 on Docker 26.1.1 ...
    ▪ Generating certificates and keys ...
    ▪ Booting up control plane ...
    ▪ Configuring RBAC rules ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
🔎  Verifying Kubernetes components...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
🌟  Enabled addons: storage-provisioner, default-storageclass
🏄  Done! kubectl is now configured to use "polar" cluster and "default" namespace by default
willem@linux-laptop:~$ minikube -p polar image pull postgres:16.1
willem@linux-laptop:~$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.30.0
registry.k8s.io/kube-proxy:v1.30.0
registry.k8s.io/kube-controller-manager:v1.30.0
registry.k8s.io/kube-apiserver:v1.30.0
registry.k8s.io/etcd:3.5.12-0
registry.k8s.io/coredns/coredns:v1.11.1
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/library/postgres:16.1
willem@linux-laptop:~$ minikube -p polar addons configure registry-creds

Do you want to enable AWS Elastic Container Registry? [y/n]: n

Do you want to enable Google Container Registry? [y/n]: n

Do you want to enable Docker Registry? [y/n]: y
-- Enter docker registry server url: ghcr.io
-- Enter docker registry username: wjc-van-es
-- Enter docker registry password: 

Do you want to enable Azure Container Registry? [y/n]: n
✅  registry-creds was successfully configured
willem@linux-laptop:~$ minikube -p polar addons enable registry-creds
❗  registry-creds is a 3rd party addon and is not maintained or verified by minikube maintainers, enable at your own risk.
❗  registry-creds does not currently have an associated maintainer.
    ▪ Using image docker.io/upmcenterprises/registry-creds:1.10
🌟  The 'registry-creds' addon is enabled
willem@linux-laptop:~$ 


```

## Beware to redeploy `polar-postgres` on the new `polar --profile`
```bash
willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ kubectl apply -f services/
deployment.apps/polar-postgres created
service/polar-postgres created
willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ 

```

## deploy catalog-service deployment, service and ingress
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ kubectl apply -f k8s/
deployment.apps/catalog-service created
ingress.networking.k8s.io/polar-ingress created
service/catalog-service created
willem@linux-laptop:~/git/cnsia/catalog-service$ kubectl get all -l app=catalog-service
NAME                                   READY   STATUS    RESTARTS   AGE
pod/catalog-service-64bf7b5b99-g8jx9   1/1     Running   0          22s

NAME                      TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)   AGE
service/catalog-service   ClusterIP   10.102.152.206   <none>        80/TCP    22s

NAME                              READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/catalog-service   1/1     1            1           22s

NAME                                         DESIRED   CURRENT   READY   AGE
replicaset.apps/catalog-service-64bf7b5b99   1         1         1       22s
willem@linux-laptop:~/git/cnsia/catalog-service$ minikube ip -p polar
192.168.49.2

```
However, ingress doesn't work properly
```bash
willem@linux-laptop:~/git/cnsia$ kubectl get ingress
NAME            CLASS   HOSTS   ADDRESS   PORTS   AGE
polar-ingress   nginx   *                 80      18m
willem@linux-laptop:~/git/cnsia$ 

```
 No address available 
 [`http://192.168.49.2/books`](http://192.168.49.2/books) is not available.
 

---

### NOTE we forgot that we have to explicitly activate ingress for each minikube profile
- [Ingress-4-catalog-service-setup.md](Ingress-4-catalog-service-setup.md#enable-the-ingress-addon-of-minikube-with-minikube-addons-enable-ingress--p-polar)

---
