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


# Kubernetes setup with kind

## Installing tools to run a Kubernetes test cluster locally on Ubuntu laptop
* [https://kubernetes.io/docs/tasks/tools/](https://kubernetes.io/docs/tasks/tools/)
* Kind
    * [https://kind.sigs.k8s.io/](https://kind.sigs.k8s.io/)
    * [https://kind.sigs.k8s.io/docs/user/quick-start/#installation](https://kind.sigs.k8s.io/docs/user/quick-start/#installation)
    * [https://github.com/kubernetes-sigs/kind](https://github.com/kubernetes-sigs/kind)
* We chose to install kind with Go, which we installed as well:
    * [https://www.cyberciti.biz/faq/how-to-install-gol-ang-on-ubuntu-linux/](https://www.cyberciti.biz/faq/how-to-install-gol-ang-on-ubuntu-linux/)
    * [https://www.digitalocean.com/community/tutorials/how-to-install-go-on-ubuntu-20-04](https://www.digitalocean.com/community/tutorials/how-to-install-go-on-ubuntu-20-04)
    * [https://gist.github.com/nex3/c395b2f8fd4b02068be37c961301caa7#file-path-md](https://gist.github.com/nex3/c395b2f8fd4b02068be37c961301caa7#file-path-md)
    * [https://go.dev/doc/](https://go.dev/doc/)
* Lastly we installed kubectl using snap
* [https://github.com/redhat-developer/intellij-kubernetes](https://github.com/redhat-developer/intellij-kubernetes)

### Subsequently, installing and testing go, kind and kubectl
```bash
$ sudo apt update
$ sudo apt upgrade
$ sudo apt search golang-go
$ sudo apt search gccgo-go
$ sudo apt install golang-go

# test go installation
$ go version
$ cd ~/git/cloud-native-spring-in-action/Chapter02/02-oo
$ go run hello.go
Hello, world!
willem , Let's be friends!
$ go build hello.go
$ ls -l hello*
-rwxrwxr-x 1 willem willem 1766226 mrt 18 18:03 hello
-rw-rw-r-- 1 willem willem     298 mrt 18 17:59 hello.go
$ ./hello
Hello, world!
willem , Let's be friends!

$ go install sigs.k8s.io/kind@v0.17.0
$ kind create cluster
command kind not found
# add $(go env GOPATH)/bin to the PATH environment variable
$ vim ~/.bashrc
i
INSERT

# set up Go lang path at the end of the file
# this is necessary later when you want kind to see be recognized as command
export PATH=$PATH:/usr/local/go/bin:$GOPATH/bin
:wq
$ source ~/.bashrc
$ kind create cluster
$ kind get clusters
kind
$ snap install kubectl --classic
$ kubectl version --client
WARNING: This version information is deprecated and will be replaced with the output from kubectl version --short.  Use --output=yaml|json to get the full version.
Client Version: version.Info{Major:"1", Minor:"26", GitVersion:"v1.26.2", GitCommit:"fc04e732bb3e7198d2fa44efa5457c7c6f8c0f5b", GitTreeState:"clean", BuildDate:"2023-03-01T02:22:36Z", GoVersion:"go1.19.6", Compiler:"gc", Platform:"linux/amd64"}
Kustomize Version: v4.5.7
$ kubectl version --output=yaml
clientVersion:
  buildDate: "2023-03-01T02:22:36Z"
  compiler: gc
  gitCommit: fc04e732bb3e7198d2fa44efa5457c7c6f8c0f5b
  gitTreeState: clean
  gitVersion: v1.26.2
  goVersion: go1.19.6
  major: "1"
  minor: "26"
  platform: linux/amd64
kustomizeVersion: v4.5.7
serverVersion:
  buildDate: "2022-10-25T19:35:11Z"
  compiler: gc
  gitCommit: 434bfd82814af038ad94d62ebe59b133fcb50506
  gitTreeState: clean
  gitVersion: v1.25.3
  goVersion: go1.19.2
  major: "1"
  minor: "25"
  platform: linux/amd64

$ kubectl cluster-info --context kind-kind
Kubernetes control plane is running at https://127.0.0.1:34231
CoreDNS is running at https://127.0.0.1:34231/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.

```

## Kind set up a cluster, load docker image, deploy a service
```bash
$ kind create cluster --name cat-svc --wait 2m
$ kind get clusters
cat-svc
$ kubectl cluster-info --context kind-cat-svc
Kubernetes control plane is running at https://127.0.0.1:35801
CoreDNS is running at https://127.0.0.1:35801/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
$ docker ps
CONTAINER ID   IMAGE                  COMMAND                  CREATED              STATUS              PORTS                       NAMES
02cfa3750d66   kindest/node:v1.25.3   "/usr/local/bin/entr…"   About a minute ago   Up About a minute   127.0.0.1:35801->6443/tcp   cat-svc-control-plane
$ kind load docker-image catalog-service:0.0.1-SNAPSHOT --name cat-svc
Image: "" with ID "sha256:95fa030bf99016dde2e8c38905aafb51a530ddfff60cbc4ec559ab464a842b5c" not yet present on node "cat-svc-control-plane", loading...
$ docker exec -it cat-svc-control-plane crictl images
IMAGE                                      TAG                  IMAGE ID            SIZE
...
docker.io/library/catalog-service          0.0.1-SNAPSHOT       95fa030bf9901       279MB
...
$ kubectl create deployment catalog-service --image=catalog-service:0.0.1-SNAPSHOT
deployment.apps/catalog-service created
$ kubectl get deployment
NAME              READY   UP-TO-DATE   AVAILABLE   AGE
catalog-service   1/1     1            1           3m5s
$ kubectl get pod
NAME                               READY   STATUS    RESTARTS   AGE
catalog-service-6c6f69cc56-sfr59   1/1     Running   0          3m35s
$ kubectl expose deployment catalog-service --name=catalog-service --port=8080
service/catalog-service exposed
$ kubectl get service catalog-service
NAME              TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
catalog-service   ClusterIP   10.96.171.188   <none>        8080/TCP   2m54s
$ kubectl port-forward service/catalog-service 8000:8080
Forwarding from 127.0.0.1:8000 -> 8080
Forwarding from [::1]:8000 -> 8080
Handling connection for 8000
Handling connection for 8000
### The prompt won´t return and you can now access the server with a browser on http://localhost:8000
### with ctrl+C the prompt returns, but the application is no longer accessible at the above address

### It is best to delete the cluster before turning of the laptop
$ kind delete cluster --name cat-svc
Deleting cluster "cat-svc" ...
$ kind get clusters
No kind clusters found.
$ docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
```
