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

# Exposing the catalog-service in our minikube cluster to the local host with an Ingress deployment
In chapter 9 we will implement the API Gateway pattern, whose service will be exposed outside the k8s cluster with an
Ingress configuration. To be able to expose the catalog-service we created at the end of chapter 7 without starting a
port forwarding process for this, we will configure an Ingress for it as a provision until we get to the API Gateway 
implementation. This is all taken from section 9.5.1 & 9.5.2 of the book.
We are working on Linux Ubuntu.

## Understanding the Kubernetes Ingress API and Ingress Controller
- An Ingress manages external access to services in a cluster, typically through the HTTP protocol, it may provide
  - load balancing
  - TSL termination
  - name based virtual hosting
- An Ingress Object acts as an entrypoint into the k8s cluster and can route traffic from a single IP address to multiple
  services running in the cluster.
- We need to create an Ingress manifest file to declare the desired state of the Ingress Object.
- The actual component that enforces all configured rules and routes traffic is the _ingress controller_, which is
  usually built on top of a reverse proxy like NGINX, HAProxy or Envoy.
- here we will use Ingress NGINX, which is available as addon with minikube

## Enable the Ingress addon of minikube with `minikube addons enable ingress -p polar`
- We will need to do this only once as long as we use the same cluster profile set up.
- Make sure minikube is running otherwise start it with `minikube start -p polar`, then run:
```bash
willem@linux-laptop:~/git/cnsia$ minikube addons enable ingress -p polar
💡  ingress is an addon maintained by Kubernetes. For any concerns contact minikube on GitHub.
You can view the list of minikube maintainers at: https://github.com/kubernetes/minikube/blob/master/OWNERS
    ▪ Using image registry.k8s.io/ingress-nginx/controller:v1.9.4
    ▪ Using image registry.k8s.io/ingress-nginx/kube-webhook-certgen:v20231011-8b53cabe0
    ▪ Using image registry.k8s.io/ingress-nginx/kube-webhook-certgen:v20231011-8b53cabe0
🔎  Verifying ingress addon...
🌟  The 'ingress' addon is enabled
```
You can check everything running under the `ingress-nginx` namespace with `kubectl get all -n ingress-nginx`:
```bash
willem@linux-laptop:~/git/cnsia$ kubectl get all -n ingress-nginx
NAME                                            READY   STATUS      RESTARTS   AGE
pod/ingress-nginx-admission-create-lqm8q        0/1     Completed   0          44s
pod/ingress-nginx-admission-patch-r95x6         0/1     Completed   1          44s
pod/ingress-nginx-controller-7c6974c4d8-k6njm   1/1     Running     0          44s

NAME                                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
service/ingress-nginx-controller             NodePort    10.96.131.209   <none>        80:30504/TCP,443:31116/TCP   44s
service/ingress-nginx-controller-admission   ClusterIP   10.96.147.212   <none>        443/TCP                      44s

NAME                                       READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/ingress-nginx-controller   1/1     1            1           44s

NAME                                                  DESIRED   CURRENT   READY   AGE
replicaset.apps/ingress-nginx-controller-7c6974c4d8   1         1         1       44s

NAME                                       COMPLETIONS   DURATION   AGE
job.batch/ingress-nginx-admission-create   1/1           6s         44s
job.batch/ingress-nginx-admission-patch    1/1           6s         44s
```
Alternatively, you can monitor these from
- Service > Ingress Classes > nginx
- Cluster > Cluster Role Bindings >
  - ingress-nginx
  - ingress-nginx-admission
- Cluster > Cluster Roles >
  - ingress-nginx
  - ingress-nginx-admission

## Defining the ingress manifest file and deploying it within the `minikube -p polar` cluster
see [../catalog-service/k8s/ingress.yml](../catalog-service/k8s/ingress.yml) for the definition
and to deploy it execute:
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ kubectl apply -f k8s/ingress.yml 
ingress.networking.k8s.io/polar-ingress created
willem@linux-laptop:~/git/cnsia/catalog-service$
```

Now you can see the deployed ingress on the dashboard at Service > Ingresses > polar-ingress
- Under _resource information_ you see
  - Ingress class name is __nginx__
  - Endpoints is a singe ip address `192.168.49.2`
- Under _Rules_ you can see that all URLs `/` are forwarded to the service named `catalog-service` on service port 80

- This endpoint ip address is important, because on our linux machine all calls to the service should start with
  `http://192.168.49.2/`
- Another way to discover this ip address is with `minikube ip -p polar`

## Test it with a browser, Postman or Insomnia
- previously, after the port forwarding `kubectl port-forward service/catalog-service 9001:80` we could reach all 
  APIs from [http://localhost:9001/](http://localhost:9001/)
- Now with the Ingress deployed we can reach all APIs from [http://192.168.49.2/](http://192.168.49.2/)
  - a GET request to [http://192.168.49.2/](http://192.168.49.2/) will reach the home page
  - a GET request to [http://192.168.49.2/books](http://192.168.49.2/books) will show a list of all books