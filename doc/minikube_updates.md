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

# Updating minikube

## General remarks
- It is not really possible to smoothly update minikube.
- You have to uninstallation and again install the latest minikube version.
  - [https://medium.com/cloudnloud/how-to-upgrade-minikube-to-latest-version-251c76e65842](https://medium.com/cloudnloud/how-to-upgrade-minikube-to-latest-version-251c76e65842)
  - [https://github.com/kiranmsalunke/minikube_upgrade](https://github.com/kiranmsalunke/minikube_upgrade)
- that requires you to redo all configurations, which shouldn't be all 
  that difficult. however, remember to
  - activate ingress, see
    [Ingress-4-catalog-service-setup.md](Ingress-4-catalog-service-setup.md#enable-the-ingress-addon-of-minikube-with-minikube-addons-enable-ingress--p-polar)
  - pull necessary remote images into the new minikube space. For your own images you may have to
    - renew the PAT of your GitHub account [GitHub_PAT.md](GitHub_PAT.md)
    - enable the `registry-creds` addon, see [minikube-configure-ghcr-repo.md](minikube-configure-ghcr-repo.md)


## session friday 11-10-2024

### re-install minikube with the `registry-creds` and the `ingress` add-ons enabled
<details>

```bash
willem@linux-laptop:~/git/cnsia$ minikube version
minikube version: v1.33.1
commit: 5883c09216182566a63dff4c326a6fc9ed2982ff
willem@linux-laptop:~/git/cnsia$ minikube update-check
CurrentVersion: v1.33.1
LatestVersion: v1.34.0
willem@linux-laptop:~/git/cnsia$ minikube status -p polar
polar
type: Control Plane
host: Stopped
kubelet: Stopped
apiserver: Stopped
kubeconfig: Stopped

willem@linux-laptop:~/git/cnsia$ minikube delete
🙄  "minikube" profile does not exist, trying anyways.
💀  Removed all traces of the "minikube" cluster.
willem@linux-laptop:~/git/cnsia$ minikube delete -p polar
🔥  Deleting "polar" in docker ...
🔥  Deleting container "polar" ...
🔥  Removing /home/willem/.minikube/machines/polar ...
💀  Removed all traces of the "polar" cluster.
willem@linux-laptop:~/git/cnsia$ ls -ltr /usr/local/bin/minikube 
-rwxr-xr-x 1 root root 95595640 jun 10 13:27 /usr/local/bin/minikube
willem@linux-laptop:~/git/cnsia$ sudo rm -rf /usr/local/bin/minikube 
[sudo] password for willem: 
willem@linux-laptop:~/git/cnsia$ ls -ltr /usr/local/bin/minikube 
ls: cannot access '/usr/local/bin/minikube': No such file or directory
willem@linux-laptop:~/git/cnsia$ curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 99.0M  100 99.0M    0     0  12.4M      0  0:00:07  0:00:07 --:--:-- 13.6M
willem@linux-laptop:~/git/cnsia$ sudo install minikube-linux-amd64 /usr/local/bin/minikube && rm minikube-linux-amd64
willem@linux-laptop:~/git/cnsia$ ls -ltr /usr/local/bin/minikube 
-rwxr-xr-x 1 root root 103820392 okt 11 22:11 /usr/local/bin/minikube
willem@linux-laptop:~/git/cnsia$ minikube version
minikube version: v1.34.0
commit: 210b148df93a80eb872ecbeb7e35281b3c582c61
willem@linux-laptop:~/git/cnsia$ minikube start --cpus 2 --memory 4g --driver docker -p polar
😄  [polar] minikube v1.34.0 on Ubuntu 22.04
✨  Using the docker driver based on user configuration
📌  Using Docker driver with root privileges
👍  Starting "polar" primary control-plane node in "polar" cluster
🚜  Pulling base image v0.0.45 ...
💾  Downloading Kubernetes v1.31.0 preload ...
    > gcr.io/k8s-minikube/kicbase...:  487.90 MiB / 487.90 MiB  100.00% 8.89 Mi
    > preloaded-images-k8s-v18-v1...:  326.69 MiB / 326.69 MiB  100.00% 5.55 Mi
🔥  Creating docker container (CPUs=2, Memory=4096MB) ...
🐳  Preparing Kubernetes v1.31.0 on Docker 27.2.0 ...
    ▪ Generating certificates and keys ...
    ▪ Booting up control plane ...
    ▪ Configuring RBAC rules ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
🔎  Verifying Kubernetes components...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
🌟  Enabled addons: storage-provisioner, default-storageclass
🏄  Done! kubectl is now configured to use "polar" cluster and "default" namespace by default
willem@linux-laptop:~/git/cnsia$ minikube -p polar image ls
registry.k8s.io/pause:3.10
registry.k8s.io/kube-scheduler:v1.31.0
registry.k8s.io/kube-proxy:v1.31.0
registry.k8s.io/kube-controller-manager:v1.31.0
registry.k8s.io/kube-apiserver:v1.31.0
registry.k8s.io/etcd:3.5.15-0
registry.k8s.io/coredns/coredns:v1.11.1
gcr.io/k8s-minikube/storage-provisioner:v5
willem@linux-laptop:~/git/cnsia$ minikube -p polar image pull postgres:16.4
willem@linux-laptop:~/git/cnsia$ minikube -p polar image ls
registry.k8s.io/pause:3.10
registry.k8s.io/kube-scheduler:v1.31.0
registry.k8s.io/kube-proxy:v1.31.0
registry.k8s.io/kube-controller-manager:v1.31.0
registry.k8s.io/kube-apiserver:v1.31.0
registry.k8s.io/etcd:3.5.15-0
registry.k8s.io/coredns/coredns:v1.11.1
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/library/postgres:16.4
willem@linux-laptop:~/git/cnsia$ minikube -p polar addons configure registry-creds

Do you want to enable AWS Elastic Container Registry? [y/n]: n

Do you want to enable Google Container Registry? [y/n]: n

Do you want to enable Docker Registry? [y/n]: y
-- Enter docker registry server url: ghcr.io
-- Enter docker registry username: wjc-van-es
-- Enter docker registry password: 

Do you want to enable Azure Container Registry? [y/n]: n
✅  registry-creds was successfully configured
willem@linux-laptop:~/git/cnsia$ minikube -p polar addons enable registry-creds
❗  registry-creds is a 3rd party addon and is not maintained or verified by minikube maintainers, enable at your own risk.
❗  registry-creds does not currently have an associated maintainer.
    ▪ Using image docker.io/upmcenterprises/registry-creds:1.10
🌟  The 'registry-creds' addon is enabled
willem@linux-laptop:~/git/cnsia$ minikube addons enable ingress -p polar
💡  ingress is an addon maintained by Kubernetes. For any concerns contact minikube on GitHub.
You can view the list of minikube maintainers at: https://github.com/kubernetes/minikube/blob/master/OWNERS
    ▪ Using image registry.k8s.io/ingress-nginx/controller:v1.11.2
    ▪ Using image registry.k8s.io/ingress-nginx/kube-webhook-certgen:v1.4.3
    ▪ Using image registry.k8s.io/ingress-nginx/kube-webhook-certgen:v1.4.3
🔎  Verifying ingress addon...
🌟  The 'ingress' addon is enabled
willem@linux-laptop:~/git/cnsia$ kubectl get all -n ingress-nginx
NAME                                           READY   STATUS      RESTARTS   AGE
pod/ingress-nginx-admission-create-6wpgc       0/1     Completed   0          74s
pod/ingress-nginx-admission-patch-hpr7g        0/1     Completed   0          74s
pod/ingress-nginx-controller-bc57996ff-x865n   1/1     Running     0          74s

NAME                                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
service/ingress-nginx-controller             NodePort    10.104.172.13   <none>        80:30985/TCP,443:31925/TCP   74s
service/ingress-nginx-controller-admission   ClusterIP   10.109.48.84    <none>        443/TCP                      74s

NAME                                       READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/ingress-nginx-controller   1/1     1            1           74s

NAME                                                 DESIRED   CURRENT   READY   AGE
replicaset.apps/ingress-nginx-controller-bc57996ff   1         1         1       74s

NAME                                       STATUS     COMPLETIONS   DURATION   AGE
job.batch/ingress-nginx-admission-create   Complete   1/1           6s         74s
job.batch/ingress-nginx-admission-patch    Complete   1/1           6s         74s
willem@linux-laptop:~/git/cnsia$
```

</details>

---

#### NOTE activate dashboard
- in another terminal `minikube dashboard -p polar` where you can also observe the enabled addons:
  - `ingress addon` at 
    - Cluster > Cluster Role Bindings > ingress-nginx, ingress-nginx-admission
    - Cluster Roles > Cluster Role Bindings > ingress-nginx, ingress-nginx-admission
  - `'registry-creds' addon` at
    - Config and Storage > Secrets > dpr-secret
- you can stop with Ctrl+C

---

### testing whether loading the local image works again
- `minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT`
- `minikube -p polar image ls`

<details>

```bash
willem@linux-laptop:~/git/cnsia$ minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT
willem@linux-laptop:~/git/cnsia$ minikube -p polar image ls
registry.k8s.io/pause:3.10
registry.k8s.io/kube-scheduler:v1.31.0
registry.k8s.io/kube-proxy:v1.31.0
registry.k8s.io/kube-controller-manager:v1.31.0
registry.k8s.io/kube-apiserver:v1.31.0
registry.k8s.io/ingress-nginx/kube-webhook-certgen:<none>
registry.k8s.io/ingress-nginx/controller:<none>
registry.k8s.io/etcd:3.5.15-0
registry.k8s.io/coredns/coredns:v1.11.1
ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/upmcenterprises/registry-creds:<none>
docker.io/library/postgres:16.4
docker.io/kubernetesui/metrics-scraper:<none>
docker.io/kubernetesui/dashboard:<none>
willem@linux-laptop:~/git/cnsia$
```

</details>

### from polar-deployment project apply the polar-postgres db deployment and service with the postgres:16.4 image
- update the image declared in `~/git/polar-deployment/kubernetes/platform/development/services/postgresql.yml`
- `~/git/polar-deployment/kubernetes/platform/development$ kubectl apply -f services/`
- `kubectl get all -l db=polar-postgres`

<details>

```bash
willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ kubectl apply -f services/
deployment.apps/polar-postgres created
service/polar-postgres created
willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$ kubectl get all -l db=polar-postgres
NAME                                 READY   STATUS    RESTARTS   AGE
pod/polar-postgres-89ddf95f5-rb8nm   1/1     Running   0          110s

NAME                     TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
service/polar-postgres   ClusterIP   10.96.151.155   <none>        5432/TCP   110s

NAME                             READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/polar-postgres   1/1     1            1           110s

NAME                                       DESIRED   CURRENT   READY   AGE
replicaset.apps/polar-postgres-89ddf95f5   1         1         1       110s
willem@linux-laptop:~/git/polar-deployment/kubernetes/platform/development$
```

</details>

### from cnsia project apply deployment service & ingress of catalog-service
- update the image in `~/git/cnsia/catalog-service/k8s/deployment.yml` to 
  `ghcr.io/wjc-van-es/catalog-service:0.0.5-SNAPSHOT`
- `~/git/cnsia$ kubectl apply -f catalog-service/k8s/deployment.yml`
- `~/git/cnsia$ kubectl apply -f catalog-service/k8s/service.yml`
- `~/git/cnsia$ kubectl apply -f catalog-service/k8s/ingress.yml`
- quick test in browser with [http://192.168.49.2/books](http://192.168.49.2/books)
- and [http://192.168.49.2/books/1234567894](http://192.168.49.2/books/1234567894)

<details>

```bash
willem@linux-laptop:~/git/cnsia$ kubectl apply -f catalog-service/k8s/deployment.yml 
deployment.apps/catalog-service created
willem@linux-laptop:~/git/cnsia$ kubectl get all -l app=catalog-service
NAME                                   READY   STATUS    RESTARTS   AGE
pod/catalog-service-66dc6766b5-k4fhm   1/1     Running   0          34s

NAME                              READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/catalog-service   1/1     1            1           34s

NAME                                         DESIRED   CURRENT   READY   AGE
replicaset.apps/catalog-service-66dc6766b5   1         1         1       34s
willem@linux-laptop:~/git/cnsia$ kubectl apply -f catalog-service/k8s/service.yml 
service/catalog-service created
willem@linux-laptop:~/git/cnsia$ kubectl get all -l app=catalog-service
NAME                                   READY   STATUS    RESTARTS   AGE
pod/catalog-service-66dc6766b5-k4fhm   1/1     Running   0          69s

NAME                      TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)   AGE
service/catalog-service   ClusterIP   10.98.70.119   <none>        80/TCP    5s

NAME                              READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/catalog-service   1/1     1            1           69s

NAME                                         DESIRED   CURRENT   READY   AGE
replicaset.apps/catalog-service-66dc6766b5   1         1         1       69s
willem@linux-laptop:~/git/cnsia$ kubectl apply -f catalog-service/k8s/ingress.yml 
ingress.networking.k8s.io/polar-ingress created
willem@linux-laptop:~/git/cnsia$ 

```

</details>

### finally stop with `minikube stop -p polar`

<details>

```bash
willem@linux-laptop:~/git/cnsia$ minikube stop -p polar
✋  Stopping node "polar"  ...
🛑  Powering off "polar" via SSH ...
🛑  1 node stopped.
willem@linux-laptop:~/git/cnsia$ minikube status -p polar
polar
type: Control Plane
host: Stopped
kubelet: Stopped
apiserver: Stopped
kubeconfig: Stopped

willem@linux-laptop:~/git/cnsia$
```

</details>

---
