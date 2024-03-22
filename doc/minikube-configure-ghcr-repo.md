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

# Enable deployment manifests to pull images from `ghcr.io/wjc-van-es/`
We have this persistent problem where minikube cannot load a locally built image with the command
`minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT`. As a work-around we want to be able
to pull the same image from our `ghcr.io/wjc-van-es/` GitHub repository instead.
For this to work we need two things
1. Being able to push our locally built image to the repository
2. Enable minikube to pull the image from that same repository.

## Push the image from your local host (laptop)
1. first login into your `ghcr.io` repository with `docker login ghcr.io`
   1. when you have a valid PAT this will succeed without being prompted for credentials
   2. when the PAT has expired you have to renew it on your GitHub account page, see [GitHub_PAT](GitHub_PAT.md)
2. Now you can push your image with
   `minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT`

A CLI session may look something like this:
```bash
willem@linux-laptop:~/git/cnsia$ docker login ghcr.io
Authenticating with existing credentials...
WARNING! Your password will be stored unencrypted in /home/willem/.docker/config.json.
Configure a credential helper to remove this warning. See
https://docs.docker.com/engine/reference/commandline/login/#credentials-store

Login Succeeded
willem@linux-laptop:~/git/cnsia$ docker push ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT
The push refers to repository [ghcr.io/wjc-van-es/catalog-service]
1dc94a70dbaa: Preparing 
2fc0f17fe631: Preparing 
e13d418b1f97: Preparing 
5f70bf18a086: Preparing 
9bb9641f7468: Preparing 
8a2bae6f5391: Preparing 
893f4b853074: Preparing 
ef35bd951312: Preparing 
366ce7d1a7f9: Preparing 
893f4b853074: Pushed 
6baad2bb9a94: Pushed 
417e5bfc3c82: Pushed 
63947728e20f: Pushed 
bea0a3dc2651: Pushed 
59ba1f666b34: Pushed 
000f6b628e38: Pushed 
e3d4978d65c3: Pushed 
57d8e19b3d8a: Pushed 
3586a6217bac: Pushed 
5498e8c22f69: Pushed 
0.0.4-SNAPSHOT: digest: sha256:76c0fe11a107bd1a5c085c0287ad6269693d69b35e100cb8f0b4945813bb7f0e size: 4706
willem@linux-laptop:~/git/cnsia$
```

## Enable minikube to pull images from `ghcr.io/wjc-van-es` with the `registry-creds` addon
Based on [https://stackoverflow.com/questions/71374622/how-do-i-pull-a-github-ghcr-io-from-minikube](https://stackoverflow.com/questions/71374622/how-do-i-pull-a-github-ghcr-io-from-minikube)
1. Configure the addon for our polar minikube profile with `minikube -p polar addons configure registry-creds` and
   from the following questions choose to enable a Docker registry where 
   1. for `docker server url` you choose `ghcr.io`,
   2. for `docker registry username` you choose `wjc-van-es`,
   3. for `docker registry password` you enter the current value of the PAT of your GitHub account.
      see [GitHub_PAT.md](GitHub_PAT.md) if it needs to be created or renewed.
2. Enable the addon for our polar minikube profile with `minikube -p polar addons enable registry-creds`
3. In your minikube dashboard for our polar profile we can now see under `Config and Storage > Secrets` a 
   `kubernetes.io/dockerconfigjson` called `dpr-secret` that on inspection contains json with a base64 encoded 
   auth attribute that decodes to `wjc-van-es:<The-current-GitHub-PAT>`.
4. Finally, in our deployment manifest [../catalog-service/k8s/deployment.yml](../catalog-service/k8s/deployment.yml) 
   we have to set the `spec.template.spec.imagePullSecrets` with list item `- name: dpr-secret`
5. Now we can apply all deployment manifests under `cnsia/catalog-service/k8s/` with
   `~/git/cnsia/catalog-service$ kubectl apply -f k8s/` and we see that it works.
6. We can further check with `kubectl get all -l app=catalog-service` and from the minikube dashboard for our polar 
   profile.
7. When we do our port forward commands we can actually test our new image and changes made to the deployment
   1. `kubectl port-forward service/catalog-service 9001:80` for testing with postman REST APIs on 
   `http://localhost:9001/` and
   2. `kubectl port-forward service/polar-postgres 5432:5432` for testing the database with DBeaver connecting at
      `jdbc:postgresql://localhost:5432/polardb_catalog`

### The CLI sessions
```bash
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
willem@linux-laptop:~/git/cnsia$
```
and after editing our deployment manifest [../catalog-service/k8s/deployment.yml](../catalog-service/k8s/deployment.yml)
as mentioned under step 4. applying the modified manifest in step 5. and checking it in step 6.
```bash
willem@linux-laptop:~/git/cnsia/catalog-service$ kubectl apply -f k8s/
  deployment.apps/catalog-service created
  service/catalog-service unchanged
  willem@linux-laptop:~/git/cnsia/catalog-service$ kubectl get all -l app=catalog-service
  NAME                                   READY   STATUS    RESTARTS   AGE
  pod/catalog-service-5c6cd469b7-q8nmw   1/1     Running   0          4m6s

  NAME                      TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)   AGE
  service/catalog-service   ClusterIP   10.103.73.153   <none>        80/TCP    31m

  NAME                              READY   UP-TO-DATE   AVAILABLE   AGE
  deployment.apps/catalog-service   1/1     1            1           4m6s

  NAME                                         DESIRED   CURRENT   READY   AGE
  replicaset.apps/catalog-service-5c6cd469b7   1         1         1       4m6s
  willem@linux-laptop:~/git/cnsia/catalog-service$
```