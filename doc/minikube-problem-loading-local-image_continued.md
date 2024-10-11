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
  width: 80%;
  max-width: 100%;
  height: auto;
}
</style>

# Unable to load the local, newly built `ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT` into minikube polar profile
## CONTINUED ON friday 15-03-2024

## Upgrading minikube to v1.32.0

### Major steps
- updating minikube
- finding `minikube start -p polar` reveals
  - we should upgrade the kubernetes version used: `--kubernetes-version=v1.28.3`
  - The polar profile was created under the previous version and that's probably no good:
    `❗  Image was not built for the current minikube version. To resolve this you can delete and recreate your minikube 
     cluster using the latest images. Expected minikube version: v1.31.0 -> Actual minikube version: v1.32.0`
- Therefore:
  - `minikube stop -p polar`
  - `minikube delete -p polar`
  - `minikube start --cpus 2 --memory 4g --driver docker -p polar --kubernetes-version=v1.28.3`
- Try to pull the necessary postgres image `minikube -p polar image pull postgres:16.1`
- Before retrying to pull the local docker image of our application first do a serious docker image clean up
  (see next section)
- For now stop the new minikube polar profile: `minikube stop -p polar` We will postpone the local image rebuild and pull
  until tomorrow.
```bash
willem@linux-laptop:~$ minikube version
minikube version: v1.31.2
commit: fd7ecd9c4599bef9f04c0986c4a0187f98a4396e
willem@linux-laptop:~$ minikube update-check
CurrentVersion: v1.31.2
LatestVersion: v1.32.0
willem@linux-laptop:~$ minikube update-check
CurrentVersion: v1.31.2
LatestVersion: v1.32.0
willem@linux-laptop:~$ curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 89.3M  100 89.3M    0     0  12.4M      0  0:00:07  0:00:07 --:--:-- 13.5M
willem@linux-laptop:~$ sudo install minikube-linux-amd64 /usr/local/bin/minikube
[sudo] password for willem: 
willem@linux-laptop:~$ minikube version
minikube version: v1.32.0
commit: 8220a6eb95f0a4d75f7f2d7b14cef975f050512d

willem@linux-laptop:~$ minikube start -p polar
😄  [polar] minikube v1.32.0 on Ubuntu 22.04
🆕  Kubernetes 1.28.3 is now available. If you would like to upgrade, specify: --kubernetes-version=v1.28.3
✨  Using the docker driver based on existing profile
👍  Starting control plane node polar in cluster polar
🚜  Pulling base image ...
🔄  Restarting existing docker container for "polar" ...
❗  Image was not built for the current minikube version. To resolve this you can delete and recreate your minikube cluster using the latest images. Expected minikube version: v1.31.0 -> Actual minikube version: v1.32.0
🐳  Preparing Kubernetes v1.27.4 on Docker 24.0.4 ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
🔎  Verifying Kubernetes components...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
🌟  Enabled addons: storage-provisioner, default-storageclass

❗  /snap/bin/kubectl is version 1.29.2, which may have incompatibilities with Kubernetes 1.27.4.
    ▪ Want kubectl v1.27.4? Try 'minikube kubectl -- get pods -A'
🏄  Done! kubectl is now configured to use "polar" cluster and "default" namespace by default
willem@linux-laptop:~$ minikube stop -p polar
✋  Stopping node "polar"  ...
🛑  Powering off "polar" via SSH ...
🛑  1 node stopped.
willem@linux-laptop:~$ minikube start -p polar --kubernetes-version=v1.28.3
😄  [polar] minikube v1.32.0 on Ubuntu 22.04
✨  Using the docker driver based on existing profile
👍  Starting control plane node polar in cluster polar
🚜  Pulling base image ...
🔄  Restarting existing docker container for "polar" ...
❗  Image was not built for the current minikube version. To resolve this you can delete and recreate your minikube cluster using the latest images. Expected minikube version: v1.31.0 -> Actual minikube version: v1.32.0
🐳  Preparing Kubernetes v1.28.3 on Docker 24.0.4 ...
🤦  Unable to restart cluster, will reset it: apiserver health: apiserver healthz never reported healthy: context deadline exceeded
    ▪ Generating certificates and keys ...
    ▪ Booting up control plane ...
    ▪ Configuring RBAC rules ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
🔎  Verifying Kubernetes components...
🌟  Enabled addons: default-storageclass, storage-provisioner
🏄  Done! kubectl is now configured to use "polar" cluster and "default" namespace by default
willem@linux-laptop:~$ minikube delete -p polar
🔥  Deleting "polar" in docker ...
🔥  Deleting container "polar" ...
🔥  Removing /home/willem/.minikube/machines/polar ...
💀  Removed all traces of the "polar" cluster.
willem@linux-laptop:~$ minikube start --cpus 2 --memory 4g --driver docker --profile polar
😄  [polar] minikube v1.32.0 on Ubuntu 22.04
✨  Using the docker driver based on user configuration
📌  Using Docker driver with root privileges
👍  Starting control plane node polar in cluster polar
🚜  Pulling base image ...
🔥  Creating docker container (CPUs=2, Memory=4096MB) ...
🐳  Preparing Kubernetes v1.28.3 on Docker 24.0.7 ...
    ▪ Generating certificates and keys ...
    ▪ Booting up control plane ...
    ▪ Configuring RBAC rules ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
🔎  Verifying Kubernetes components...
🌟  Enabled addons: storage-provisioner, default-storageclass
🏄  Done! kubectl is now configured to use "polar" cluster and "default" namespace by default
willem@linux-laptop:~$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.28.3
registry.k8s.io/kube-proxy:v1.28.3
registry.k8s.io/kube-controller-manager:v1.28.3
registry.k8s.io/kube-apiserver:v1.28.3
registry.k8s.io/etcd:3.5.9-0
registry.k8s.io/coredns/coredns:v1.10.1
gcr.io/k8s-minikube/storage-provisioner:v5
willem@linux-laptop:~$ minikube -p polar image pull postgres:16.1
willem@linux-laptop:~$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.28.3
registry.k8s.io/kube-proxy:v1.28.3
registry.k8s.io/kube-controller-manager:v1.28.3
registry.k8s.io/kube-apiserver:v1.28.3
registry.k8s.io/etcd:3.5.9-0
registry.k8s.io/coredns/coredns:v1.10.1
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/library/postgres:16.1
willem@linux-laptop:~$ minikube stop -p polar
✋  Stopping node "polar"  ...
🛑  Powering off "polar" via SSH ...
🛑  1 node stopped.
willem@linux-laptop:~$ 

```

## Serious clean up our laptop host of old Docker  images that may interfere with our local catalog-service build and pull

### Major steps

- Checking images with `docker image ls`
- Removing all available versions of the `catalog-service` and `paketobuildpacks` with `docker image rm ${image-name:tag}`
- `docker image rm eclipse-temurin:17`
- `docker image rm my-java-image:1.0.0 `
- Also removing the old image of the previous minikube version with `docker image rm gcr.io/k8s-minikube/kicbase:v0.0.40`
- `gcr.io/k8s-minikube/kicbase:v0.0.42` still used by a Docker container holding the new minikube polar profile
  as was revealed with `docker ps -a`

```bash
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                            TAG              IMAGE ID       CREATED         SIZE
catalog-service                       latest           81af438a907f   7 weeks ago     501MB
ghcr.io/wjc-van-es/catalog-service    0.0.4-SNAPSHOT   81af438a907f   7 weeks ago     501MB
paketobuildpacks/run-jammy-base       latest           d1e9aa4e06de   8 weeks ago     105MB
postgres                              16.1             d2d312b19332   3 months ago    432MB
gcr.io/k8s-minikube/kicbase           v0.0.42          dbc648475405   4 months ago    1.2GB
postgres                              15.4             69e765e8cdbe   6 months ago    412MB
postgres                              15.3             8769343ac885   7 months ago    412MB
my-java-image                         1.0.0            1016789db6dd   7 months ago    598MB
ghcr.io/wjc-van-es/my-java-image      1.0.0            1016789db6dd   7 months ago    598MB
gcr.io/k8s-minikube/kicbase           v0.0.40          c6cc01e60919   8 months ago    1.19GB
eclipse-temurin                       17               3a958eff0206   8 months ago    456MB
testcontainers/ryuk                   0.5.1            ec913eeff75a   10 months ago   12.7MB
confluentinc/ksqldb-examples          7.2.1            ae252c2780c4   20 months ago   815MB
confluentinc/cp-ksqldb-server         7.2.1            140d2ac32177   20 months ago   1.36GB
confluentinc/cp-ksqldb-cli            7.2.1            287039530a46   20 months ago   857MB
confluentinc/cp-schema-registry       7.2.1            afaac043dcc1   20 months ago   1.86GB
confluentinc/cp-kafka                 7.2.1            d893473a6510   20 months ago   782MB
confluentinc/cp-kafka                 latest           d893473a6510   20 months ago   782MB
confluentinc/cp-zookeeper             7.2.1            3f28db6a433d   20 months ago   782MB
confluentinc/cp-zookeeper             latest           3f28db6a433d   20 months ago   782MB
confluentinc/cp-kafka-rest            7.2.1            784b8061ad0c   20 months ago   1.76GB
cnfldemos/kafka-connect-datagen       0.5.3-7.1.0      de0e2396b904   23 months ago   1.46GB
zookeeper                             3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                        2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer            1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer            1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                     krb5             ce76df4cb4a8   3 years ago     61MB
config-service                        0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
ghcr.io/wjc-van-es/catalog-service    0.0.3-SNAPSHOT   0a58f1fe3115   44 years ago    322MB
paketobuildpacks/builder-jammy-base   latest           07a12e865f08   44 years ago    1.58GB
willem@linux-laptop:~/git/cnsia$ history | grep "docker image"
 1011  docker image ls
 1019  docker image ls
 1020  docker image inspect
 1021  docker image inspect catalog-service-03:0.0.1-SNAPSHOT 
 1022  docker image inspect catalog-service-03:0.0.1-SNAPSHOT | jq '.[0].Config'
 1027  docker image inspect catalog-service-03:0.0.1-SNAPSHOT | jq '.[0].Config'
 1030  docker image inspect catalog-service-03:0.0.1-SNAPSHOT 
 1031  docker image inspect catalog-service-03:0.0.1-SNAPSHOT | jq '.[0].Config'
 1032  docker image inspect catalog-service-03:0.0.1-SNAPSHOT | jq '.[0].Config.Env'
 1033  docker image inspect catalog-service-03:0.0.1-SNAPSHOT | jq '$[0].Config.Env'
 1034  docker image inspect catalog-service-03:0.0.1-SNAPSHOT | jq '.[0].Config.Env'
 1035  docker image inspect catalog-service-03:0.0.1-SNAPSHOT -f  '{{.Config.Env}}'
 1116  docker image ls
 1117  docker image inspect catalog-service-03:0.0.1-SNAPSHOT 
 1123  docker image ls
 1128  docker image prune
 1129  docker image ls
 1187  docker image ls
 1203  docker image ls
 1204  docker image prune
 1205  docker image ls
 1206  docker image prune
 1207  docker image ls
 1341  docker image ls
 1342  docker image rm catalog-service-03:0.0.1-SNAPSHOT 
 1343  docker image rm catalog-service:0.0.1-SNAPSHOT 
 1345  docker image ls
 1348  docker image ls
 1350  docker image ls
 1356  docker image rm catalog-service:latest 
 1357  docker image rm catalog-service:0.0.1-SNAPSHOT 
 1358  docker image ls
 1372  docker image ls
 1375  docker image ls
 1400  docker image ls
 1405  docker image ls
 1428  docker image ls
 1477  docker image ls | catalog-service
 1478  docker image ls | grep catalog-service
 1479  docker image ls | grep "catalog-service"
 1480  docker image ls 
 1488  docker image ls 
 1502  docker image ls
 1504  docker image ls
 1505  docker image ls | grep catalog-service
 1740  docker image ls
 1741  docker image rm catalog-service:latest
 1742  docker image rm ghcr.io/wjc-van-es/catalog-service:0.0.1-SNAPSHOT 
 1743  docker image rm ghcr.io/wjc-van-es/catalog-service:0.0.2-SNAPSHOT 
 1744  docker image ls
 1745  docker image rm ghcr.io/wjc-van-es/catalog-service
 1746  docker image rm ghcr.io/wjc-van-es/catalog-service:latest
 1747  docker image ls
 1748  docker image rm postgres:15.3.0
 1749  docker image rm postgres:15.3
 1750  docker image rm postgres:latest
 1751  docker image ls
 1752  docker image rm ghcr.io/wjc-van-es/catalog-service
 1753  docker image rm ghcr.io/wjc-van-es/catalog-service:<none>
 1754  docker image rm c04f1dac659e
 1755  docker image ls
 1756  docker image prune
 1757  docker image ls
 1777  docker image ls
 1778  docker image pull postgres:16.1
 1779  docker image ls
 1797  docker image ls
 1861  docker image ls
 1862  docker image rm 5ba2428071a0
 1863  docker image ls
 1864  docker image prune
 1865  docker image ls
 1866  docker image rm ad45994fb4cb
 1867  docker image prune
 1868  docker image ls
 1869  docker image rm ad45994fb4cb
 1870  docker image prune
 1871  docker image ls
 1872  docker image rm 07a12e865f08
 1873  docker image rm 050ed48532b2
 1874  docker image prune
 1875  docker image ls
 1876  docker image rm catalog-service:latest
 1877  docker image rm catalog-service:df-0.0.1 
 1878  docker image rm paketobuildpacks/run
 1879  docker image rm paketobuildpacks/run-jammy-base:latest 
 1880  docker image ls
 1881  docker image rm paketobuildpacks/run:base-cnb 
 1882  docker image ls
 1883  docker image rm paketobuildpacks/run:base-cnb 
 1884  docker image rm paketobuildpacks/run-jammy-base:latest 
 1885  docker image rm paketobuildpacks/builder-jammy-base:latest 
 1886  docker image ls
 1887  docker image rm ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT 
 1888  docker image ls
 1890  docker image ls
 1996  docker image ls
 1997  history | grep "docker image"
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                            TAG              IMAGE ID       CREATED         SIZE
catalog-service                       latest           81af438a907f   7 weeks ago     501MB
ghcr.io/wjc-van-es/catalog-service    0.0.4-SNAPSHOT   81af438a907f   7 weeks ago     501MB
paketobuildpacks/run-jammy-base       latest           d1e9aa4e06de   8 weeks ago     105MB
postgres                              16.1             d2d312b19332   3 months ago    432MB
gcr.io/k8s-minikube/kicbase           v0.0.42          dbc648475405   4 months ago    1.2GB
postgres                              15.4             69e765e8cdbe   6 months ago    412MB
postgres                              15.3             8769343ac885   7 months ago    412MB
my-java-image                         1.0.0            1016789db6dd   7 months ago    598MB
ghcr.io/wjc-van-es/my-java-image      1.0.0            1016789db6dd   7 months ago    598MB
gcr.io/k8s-minikube/kicbase           v0.0.40          c6cc01e60919   8 months ago    1.19GB
eclipse-temurin                       17               3a958eff0206   8 months ago    456MB
testcontainers/ryuk                   0.5.1            ec913eeff75a   10 months ago   12.7MB
confluentinc/ksqldb-examples          7.2.1            ae252c2780c4   20 months ago   815MB
confluentinc/cp-ksqldb-server         7.2.1            140d2ac32177   20 months ago   1.36GB
confluentinc/cp-ksqldb-cli            7.2.1            287039530a46   20 months ago   857MB
confluentinc/cp-schema-registry       7.2.1            afaac043dcc1   20 months ago   1.86GB
confluentinc/cp-kafka                 7.2.1            d893473a6510   20 months ago   782MB
confluentinc/cp-kafka                 latest           d893473a6510   20 months ago   782MB
confluentinc/cp-zookeeper             7.2.1            3f28db6a433d   20 months ago   782MB
confluentinc/cp-zookeeper             latest           3f28db6a433d   20 months ago   782MB
confluentinc/cp-kafka-rest            7.2.1            784b8061ad0c   20 months ago   1.76GB
cnfldemos/kafka-connect-datagen       0.5.3-7.1.0      de0e2396b904   23 months ago   1.46GB
zookeeper                             3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                        2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer            1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer            1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                     krb5             ce76df4cb4a8   3 years ago     61MB
ghcr.io/wjc-van-es/catalog-service    0.0.3-SNAPSHOT   0a58f1fe3115   44 years ago    322MB
paketobuildpacks/builder-jammy-base   latest           07a12e865f08   44 years ago    1.58GB
config-service                        0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm paketobuildpacks/builder-jammy-base:latest
Untagged: paketobuildpacks/builder-jammy-base:latest
Untagged: paketobuildpacks/builder-jammy-base@sha256:92811b61280130099527b40c7650c871187a7e1cebb21744d93e480fc34cbcb0
Deleted: sha256:07a12e865f08b756cef2db8a52e81429c82134ae0a9d8043ab57e11422969991
Deleted: sha256:c7e477c714189770a567ca6b3339524079d5e29e29cbf5a3a74d8cd8970cfcd3
Deleted: sha256:8697ae6897528a25447a6cf304ceab40dc231e0de1799dace45cdf0aead756f1
Deleted: sha256:bf10909f0719747729d146587d43533b485070025004824f07f73e2e42ab887e
Deleted: sha256:1a8adfca3f226f622fb14b580bd6d913e0904d21d887eb355f765f6880ca0065
Deleted: sha256:a418a4ac034193c6fdccebdeef9b0592bd7dbcd1ca5205e54608ca5b15333ce0
Deleted: sha256:e817b5237e81d08ca9d5512a1c70606332ef3613a4beeeddfa0d5985296a6029
Deleted: sha256:c136b46a72fd29c77cc735d3788ad5e1cc830778b77b1eb1bf015041bb92b465
Deleted: sha256:424bc76b0ffabf6980adeb29a54aaf36889ad83bd21113a4c6c1dfe6f3d1301d
Deleted: sha256:767d69c48d54172283b215f5dc1b9c0290582f607d116c8bb0a61ffd6125644c
Deleted: sha256:42a742f6214ce12633137820f85179cc3b7fcb154a9800d76347336f7d07c8cf
Deleted: sha256:8114141f2da4fd2831d359242c5e01c817ea38017fe78d3868b7bdac7a14fe5a
Deleted: sha256:e9dd5dab987f22fe0880bea5b02fbc02284fde5b6fef8aca2178aa606aa5c18d
Deleted: sha256:b64594de0ae128db1f4333f0500f569d951f3ca21c2fc27d13ab259ea578a06c
Deleted: sha256:06b517b28e1a48d6c26c99d63a046813907ec54ddb786ca277976bafb6b6c270
Deleted: sha256:1596b9adf290730a37b06a6dee2ee30354fe4d6f8931f5706b29b03f69c53a11
Deleted: sha256:a1af1a9118e84615b551ec2ebd60e752d88715c91010d9e74cff7d7d25c1c1f7
Deleted: sha256:0088faab97f806f37355ac81bf8d7c30e5b4c10644c0bfe841f58f88c8bfd114
Deleted: sha256:fa5e6da5e7e6f5e2e064c64032e00b159c341576b8b30dabefe08facd2a8ba9c
Deleted: sha256:deeb05b633f7d270175fcb7cfa57dc76f29d60b7378eb785b4a42bf97279f65a
Deleted: sha256:009762aa654dfa0384cbeed55e4a03227aa46f9a69940fc2bdcf46f7ecebb17f
Deleted: sha256:4ce05cf013d558bc2fb0b2faf454341e1d2bb724cb8440fd79669765a60b067c
Deleted: sha256:c342f5da5273ca3baae07262fca3b4910ceefde90cb33d5e7ca6b82ff72b9851
Deleted: sha256:6916906df520eaf7d1b80c5f6fda6ed301666e8732c3d28e68ac45212ba2080c
Deleted: sha256:15f4a8ac2d0496a28c1e73d1d2f1e36f2ee972dcfb2ba72d8981e09c8c41ecd3
Deleted: sha256:096e8c220c569023ae53ecedac3cb9f3f5b1b4fa37e0d53d31829c96a4abe9f2
Deleted: sha256:8f440d003ca0a9ea1dae4afff3df5c96090be33dc0975bc5753e4bc81a2f7eaf
Deleted: sha256:45a51eed58d399e6021814360dd8358093f6d6dd7814d1a59e41fb767094615e
Deleted: sha256:135968076b3c215cf411876d4fc06fdb3dd57a2e2c3af897f69603ba7930561e
Deleted: sha256:97ebbbfc67e3451674990fc326056a26a0b82219794234c58692e97f84dc7e24
Deleted: sha256:732d7d9a312f0ac8a107a7f78353b4f8744e32366c87be501762589bbdb5acf8
Deleted: sha256:d3ca50b8ee3d3da7227c08115ab32d3d246d9ffc953666c457823cb8a2d95598
Deleted: sha256:33a466a977ee69661bf2a0126ac0c935666bd662de2afc73942912e67fa0d933
Deleted: sha256:d2b24c2abc1983f64abe1f24ea6744fcdef1351b27bb0332fdc69fe9f7d75591
Deleted: sha256:3f6ff1a9940c250b0216eb65bcde67a55c1ece1672d5998ff0f80fd67f2c846b
Deleted: sha256:5659434844fcad534d5639afe79252190f9331606f7ed597b78a4e1e52a15d22
Deleted: sha256:eb8f323b574bd127245762d14d86a592a753d4c41789e7db308d9ae9752334f1
Deleted: sha256:b2b61007737cd8e6cf156c643a187fd670e8aad3b9e15a45ece279725a10a0ba
Deleted: sha256:f99a3e512c8b227a2feb7fe04c5aea471adb6018310810fabefadde81f897f73
Deleted: sha256:fcf9a580ecc7b10a1f4b9dbe9597b1b1113394ba21fca47962247ba39e5cff88
Deleted: sha256:63e684db614c0b83f2e997096096faa667ca51391f30bea01ee49c888cb8c629
Deleted: sha256:a896d6a876679e616cf697c46591f3d1692043b445aa4079bf3a684f84b243c7
Deleted: sha256:7f38166eb9e0a91e0afd1f50e6799cbb76c9d3ae71639e3827ff817256b985db
Deleted: sha256:fa8a6a8bb695bb1f45ed09c0bb0223cc71c4108a8b03db612e218911e3592dcd
Deleted: sha256:97eae6d7b9e935bc4751a6e246e452d0f72799f9809c98ac7394e4c9987277cd
Deleted: sha256:949a73d57d26f7298682eede872b2cfef16e73df244f29462df6c41ef048c9d1
Deleted: sha256:2834174092893d9580c329f67959500e08be916fa2dd3a0d78a07dcea749f18a
Deleted: sha256:dc7e53bc49c330febe6e49ea702abf2a93ebe7fd988f5166f2b4b4e7feef8aaf
Deleted: sha256:89c66b0bea9bd520548832124b63c2274924d4c7170d42fbe3a9e9206136da94
Deleted: sha256:ad934ef64a72b33eea1aa6ab40a44dca7c800ec85073e82a79d9a10ff27d3b4e
Deleted: sha256:4a50402d07d37585e4fa710ca310fe1bc901dc983791e7b474207e0abfa3242f
Deleted: sha256:ebde54d1e20e88460ab830f31f9c53be07da2f95e9b783bc47af57ea99297741
Deleted: sha256:b30c806bd8f35143d3c522135af08a8c0dceefd2bb621cbe4ed4a1ff3ab56448
Deleted: sha256:69d9950831e62442c32e491d9ddbe60f0827328cd00aa7ec8acd553d6f5fe93e
Deleted: sha256:39a89eb3b09cace9449364b0ad975dbbdd96a6fa581f0f42b7eb3f7c3f7453ce
Deleted: sha256:7ec6c7b77dc9d7b6fab0154f1e1e86c9080c0581806b5063a73cc0f969768e99
Deleted: sha256:4653237c1e907ca825c335a994f088212045068ef98693550c1eabecff8ca971
Deleted: sha256:318b007c0d880a4ffa11508cd30d94bad9aa3d61958b80f1d9816b74098765a8
Deleted: sha256:0d71c8c878d95ba0bd5932caeb756147741c33e025f388bd4b77ee45f81a19d6
Deleted: sha256:49fbe70bde22b94f37b535bcab3cbe410573fe9065e9d0834f774163de616922
Deleted: sha256:09abc086c1c45c76eaaa54e7bd38ee675a629e78308f67a3d14bceabf0bc6f6d
Deleted: sha256:79939be9211508554231e172c2c2faf73b45c4aa63e67308fd377d285ec6435c
Deleted: sha256:977fd8644409d5ccd710a412c64ff42603796ffb53a3f97475395692ade4a28c
Deleted: sha256:2aecf3c45f60cb74557ee503ebc7fe135dab3b6f7ae38a08fbb515a6338eba07
Deleted: sha256:73797e343ac24dae9ed3330cbf968c897f10b30bf9a8a8c90cb57ab895ab182a
Deleted: sha256:d5273ad542e8ad4cc6327d474d1d3b7129112d959dfeb1dacfa76efb4f59e032
Deleted: sha256:03abf6da143d92e1e3a3895ea9de41d5b179b1a8b74e0e34533d00e5ad9ef381
Deleted: sha256:705c0668ca62a3b67f75001791494fb119a791e185b5b90422d06ceb35a4a50b
Deleted: sha256:2f67f1ce8423950654de797e24f4e9e56095f32c5c525a2c4f03aaec0c8c0266
Deleted: sha256:da72c4df501ea633516dc3e69fbdbe1a98744a039be3d3116d298fe116627e99
Deleted: sha256:71b89e7628232807a14eaf22b5bb9c9256429d89a1cb449e569c018b77d4d396
Deleted: sha256:f380dd5173ba92c82f0dce9651eb69533d9b9be55c6e67143a7bd3c3cb721768
Deleted: sha256:ab3f3912844cc3363b65a356acc76e4247004868ac4588a045dc76c968f91f47
Deleted: sha256:076aa7e340c1b45ba22cee4410cd2469f2bc24d5e674960fbb2b28290c459328
Deleted: sha256:5718b7326f5b0ba5033a68611df246332770a2f653edd7864c72e03ace108d52
Deleted: sha256:9e9c87e147e89f9c3658757b7148851a7fe5ed452371a5492f3f107a2467885c
Deleted: sha256:aa99e7789bf2f708f0abf4d69a6c49e990af9921ed9eaeadb683263f583f580b
Deleted: sha256:3b86fb554f4e70c21a5213b881bac30dfd079399d51f4a20926b9b78bb3e16b5
Deleted: sha256:7f80120569c324b927ac056f5e71c2175779ac49c3dce52e4c7837101728f8a7
Deleted: sha256:5d3da53392467d137cdf505d5d4e3304c87b80c52995e850a93059952bef1ae1
Deleted: sha256:3e452bd68081f6d30d55ab172f18ae1ffeffdaa84ce7bb9fd6ff6983ccdd11af
Deleted: sha256:8378eadae30aea4f5a66837b8078ee560aba00bc675d7a023420388ee02ba2bc
Deleted: sha256:8d3adecd6628e8ecc284e64c6ae401de366fefa3f1abaeeda35e07b989187a12
Deleted: sha256:a0acf56580f299d2ec25f5272b4bb4fbf9807deb0989113dc870ef3220f62382
Deleted: sha256:5b6cfa66c68f3cb9b11bb22c00f175abc689684b51871da95ff2795e7fb614dc
Deleted: sha256:6a4a9198390e8dd95ceb29d01bbf4ab0672fb6ac622efdfcd48a5c45b0a49e12
Deleted: sha256:3bfe5ea709586b60ef6e259f2bf56087a2153a5efeccba432987a6a84d87c47d
Deleted: sha256:ce2eb136aeb44ae2ed3cdae5336b36fe0e5d0724607fb7f73a94d67d25022bc1
Deleted: sha256:2ea2a1c133bc341c44fd18644ba9e087f18fbf99f12dd4a1c6fd19e677abeb99
Deleted: sha256:c19eecbc414c96d27a91ed440f2f673ee10c18cff31168d26e5e1dfce17eb286
Deleted: sha256:a1890bbaa373452a382c15009edc9ab843d0fe58fcbaf9d3add18f02856133ac
Deleted: sha256:6b4097cf76e310863ac04780170eed88872d148b6e3f7bcad1f23b0feb9f6e2e
Deleted: sha256:292aaae9a29caae7365da026a723595177a093b234f242d94cb835aa3f0b86d5
Deleted: sha256:75c6efe378cc4e95a98f8883a9842192c1069291ef512ed9101c092a527d8c1c
Deleted: sha256:3ace97f3757a03711ed1251ea1e74420fe72acefc2aa496d68625d0155501a27
Deleted: sha256:0313544a01fb58e8084a6fbaf8f0391a96e9c18c305a1dfac64977eee03b160d
Deleted: sha256:7c78cedefe55f6dc4be3bd1cd8b2bfc9b2875abda49e370560bf54600b3123a7
Deleted: sha256:70f3c29d8b4c9ebc79d90119cbe29bbe24287911c866a836939ee8b5f5e59cbd
Deleted: sha256:5bd8e51b77318543762610faf47b5a0a979f5e81abeadf6602a27b952175c513
Deleted: sha256:dff5ecef64fcafb0e60e2c9f57ae651a860e6f813000826ca8e83f2e0061f294
Deleted: sha256:36050e8e41512e3bfc9e5eff4b93b8a74be013f99cfd17d88cacc3c7201a46ca
Deleted: sha256:26a5f1f65428493ed09b9915a39c271c16973c8f4e6101cd96c71484e56d8728
Deleted: sha256:8892c26fd66077e16488260354607b575a3bc9094a91739f0b89a4ab7d26b4cc
Deleted: sha256:beca064e4fe854408464abafd7911f6f09ec97e42d2d318beb0b34b28c256f65
Deleted: sha256:bff7d0b01c75b442c8bb958814802236625cd916fc714709cad575fe7fa9eedf
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                           TAG              IMAGE ID       CREATED         SIZE
catalog-service                      latest           81af438a907f   7 weeks ago     501MB
ghcr.io/wjc-van-es/catalog-service   0.0.4-SNAPSHOT   81af438a907f   7 weeks ago     501MB
paketobuildpacks/run-jammy-base      latest           d1e9aa4e06de   8 weeks ago     105MB
postgres                             16.1             d2d312b19332   3 months ago    432MB
gcr.io/k8s-minikube/kicbase          v0.0.42          dbc648475405   4 months ago    1.2GB
postgres                             15.4             69e765e8cdbe   6 months ago    412MB
postgres                             15.3             8769343ac885   7 months ago    412MB
my-java-image                        1.0.0            1016789db6dd   7 months ago    598MB
ghcr.io/wjc-van-es/my-java-image     1.0.0            1016789db6dd   7 months ago    598MB
gcr.io/k8s-minikube/kicbase          v0.0.40          c6cc01e60919   8 months ago    1.19GB
eclipse-temurin                      17               3a958eff0206   8 months ago    456MB
testcontainers/ryuk                  0.5.1            ec913eeff75a   10 months ago   12.7MB
confluentinc/ksqldb-examples         7.2.1            ae252c2780c4   20 months ago   815MB
confluentinc/cp-ksqldb-server        7.2.1            140d2ac32177   20 months ago   1.36GB
confluentinc/cp-ksqldb-cli           7.2.1            287039530a46   20 months ago   857MB
confluentinc/cp-schema-registry      7.2.1            afaac043dcc1   20 months ago   1.86GB
confluentinc/cp-kafka                7.2.1            d893473a6510   20 months ago   782MB
confluentinc/cp-kafka                latest           d893473a6510   20 months ago   782MB
confluentinc/cp-zookeeper            7.2.1            3f28db6a433d   20 months ago   782MB
confluentinc/cp-zookeeper            latest           3f28db6a433d   20 months ago   782MB
confluentinc/cp-kafka-rest           7.2.1            784b8061ad0c   20 months ago   1.76GB
cnfldemos/kafka-connect-datagen      0.5.3-7.1.0      de0e2396b904   23 months ago   1.46GB
zookeeper                            3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                       2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer           1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer           1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                    krb5             ce76df4cb4a8   3 years ago     61MB
ghcr.io/wjc-van-es/catalog-service   0.0.3-SNAPSHOT   0a58f1fe3115   44 years ago    322MB
config-service                       0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm ghcr.io/wjc-van-es/catalog-service:0.0.3-SNAPSHOT 
Untagged: ghcr.io/wjc-van-es/catalog-service:0.0.3-SNAPSHOT
Deleted: sha256:0a58f1fe31151c97e1aeadf5d417709f00781b9557f73ad7044499e6f48aed11
Deleted: sha256:dd5f435546c6a4737164543aa84d3cc558c42a2923892a19741bc113818a7383
Deleted: sha256:6dcd3aa86c1966294a483f36b840177000978b495d85d4986f36efc23a0f70d3
Deleted: sha256:1ea643acad0497d2c27901df012a17b5410f4bd64c187b4e1bf0803398a4c205
Deleted: sha256:31737702ac450f657199626c91f550bdc34ae084e3a0ded4dd3b51d9f2c745fa
Deleted: sha256:c7a4869a7e4347ec15c93d469d6ea69ea993bd4ea04dd82679665ff3c9274037
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                           TAG              IMAGE ID       CREATED         SIZE
catalog-service                      latest           81af438a907f   7 weeks ago     501MB
ghcr.io/wjc-van-es/catalog-service   0.0.4-SNAPSHOT   81af438a907f   7 weeks ago     501MB
paketobuildpacks/run-jammy-base      latest           d1e9aa4e06de   8 weeks ago     105MB
postgres                             16.1             d2d312b19332   3 months ago    432MB
gcr.io/k8s-minikube/kicbase          v0.0.42          dbc648475405   4 months ago    1.2GB
postgres                             15.4             69e765e8cdbe   6 months ago    412MB
postgres                             15.3             8769343ac885   7 months ago    412MB
my-java-image                        1.0.0            1016789db6dd   7 months ago    598MB
ghcr.io/wjc-van-es/my-java-image     1.0.0            1016789db6dd   7 months ago    598MB
gcr.io/k8s-minikube/kicbase          v0.0.40          c6cc01e60919   8 months ago    1.19GB
eclipse-temurin                      17               3a958eff0206   8 months ago    456MB
testcontainers/ryuk                  0.5.1            ec913eeff75a   10 months ago   12.7MB
confluentinc/ksqldb-examples         7.2.1            ae252c2780c4   20 months ago   815MB
confluentinc/cp-ksqldb-server        7.2.1            140d2ac32177   20 months ago   1.36GB
confluentinc/cp-ksqldb-cli           7.2.1            287039530a46   20 months ago   857MB
confluentinc/cp-schema-registry      7.2.1            afaac043dcc1   20 months ago   1.86GB
confluentinc/cp-kafka                7.2.1            d893473a6510   20 months ago   782MB
confluentinc/cp-kafka                latest           d893473a6510   20 months ago   782MB
confluentinc/cp-zookeeper            7.2.1            3f28db6a433d   20 months ago   782MB
confluentinc/cp-zookeeper            latest           3f28db6a433d   20 months ago   782MB
confluentinc/cp-kafka-rest           7.2.1            784b8061ad0c   20 months ago   1.76GB
cnfldemos/kafka-connect-datagen      0.5.3-7.1.0      de0e2396b904   23 months ago   1.46GB
zookeeper                            3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                       2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer           1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer           1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                    krb5             ce76df4cb4a8   3 years ago     61MB
config-service                       0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm paketobuildpacks/run-jammy-base:latest
Untagged: paketobuildpacks/run-jammy-base:latest
Untagged: paketobuildpacks/run-jammy-base@sha256:53460f911ac95469e4a796555d8c49f7c70d61caa6c120a1b7eb07248986d52a
Deleted: sha256:d1e9aa4e06dedac0ca4e6cd6b7ddf17311aaff6777cec74e4287369f73b3b6ac
willem@linux-laptop:~/git/cnsia$ docker image rm gcr.io/k8s-minikube/kicbase:v0.0.40 
Untagged: gcr.io/k8s-minikube/kicbase:v0.0.40
Untagged: gcr.io/k8s-minikube/kicbase@sha256:8cadf23777709e43eca447c47a45f5a4635615129267ce025193040ec92a1631
Deleted: sha256:c6cc01e6091959400f260dc442708e7c71630b58dab1f7c344cb00926bd84950
Deleted: sha256:67e5f5868bd18a3e5ef58e4a57c7c8fead51cc3a4003f4fde52cd9061431c40a
willem@linux-laptop:~/git/cnsia$ docker image prune
WARNING! This will remove all dangling images.
Are you sure you want to continue? [y/N] y
Total reclaimed space: 0B
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                           TAG              IMAGE ID       CREATED         SIZE
catalog-service                      latest           81af438a907f   7 weeks ago     501MB
ghcr.io/wjc-van-es/catalog-service   0.0.4-SNAPSHOT   81af438a907f   7 weeks ago     501MB
postgres                             16.1             d2d312b19332   3 months ago    432MB
gcr.io/k8s-minikube/kicbase          v0.0.42          dbc648475405   4 months ago    1.2GB
postgres                             15.4             69e765e8cdbe   6 months ago    412MB
postgres                             15.3             8769343ac885   7 months ago    412MB
my-java-image                        1.0.0            1016789db6dd   7 months ago    598MB
ghcr.io/wjc-van-es/my-java-image     1.0.0            1016789db6dd   7 months ago    598MB
eclipse-temurin                      17               3a958eff0206   8 months ago    456MB
testcontainers/ryuk                  0.5.1            ec913eeff75a   10 months ago   12.7MB
confluentinc/ksqldb-examples         7.2.1            ae252c2780c4   20 months ago   815MB
confluentinc/cp-ksqldb-server        7.2.1            140d2ac32177   20 months ago   1.36GB
confluentinc/cp-ksqldb-cli           7.2.1            287039530a46   20 months ago   857MB
confluentinc/cp-schema-registry      7.2.1            afaac043dcc1   20 months ago   1.86GB
confluentinc/cp-kafka                7.2.1            d893473a6510   20 months ago   782MB
confluentinc/cp-kafka                latest           d893473a6510   20 months ago   782MB
confluentinc/cp-zookeeper            7.2.1            3f28db6a433d   20 months ago   782MB
confluentinc/cp-zookeeper            latest           3f28db6a433d   20 months ago   782MB
confluentinc/cp-kafka-rest           7.2.1            784b8061ad0c   20 months ago   1.76GB
cnfldemos/kafka-connect-datagen      0.5.3-7.1.0      de0e2396b904   23 months ago   1.46GB
zookeeper                            3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                       2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer           1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer           1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                    krb5             ce76df4cb4a8   3 years ago     61MB
config-service                       0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT 
Untagged: ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                         TAG              IMAGE ID       CREATED         SIZE
catalog-service                    latest           81af438a907f   7 weeks ago     501MB
postgres                           16.1             d2d312b19332   3 months ago    432MB
gcr.io/k8s-minikube/kicbase        v0.0.42          dbc648475405   4 months ago    1.2GB
postgres                           15.4             69e765e8cdbe   6 months ago    412MB
postgres                           15.3             8769343ac885   7 months ago    412MB
ghcr.io/wjc-van-es/my-java-image   1.0.0            1016789db6dd   7 months ago    598MB
my-java-image                      1.0.0            1016789db6dd   7 months ago    598MB
eclipse-temurin                    17               3a958eff0206   8 months ago    456MB
testcontainers/ryuk                0.5.1            ec913eeff75a   10 months ago   12.7MB
confluentinc/ksqldb-examples       7.2.1            ae252c2780c4   20 months ago   815MB
confluentinc/cp-ksqldb-server      7.2.1            140d2ac32177   20 months ago   1.36GB
confluentinc/cp-ksqldb-cli         7.2.1            287039530a46   20 months ago   857MB
confluentinc/cp-schema-registry    7.2.1            afaac043dcc1   20 months ago   1.86GB
confluentinc/cp-kafka              7.2.1            d893473a6510   20 months ago   782MB
confluentinc/cp-kafka              latest           d893473a6510   20 months ago   782MB
confluentinc/cp-zookeeper          7.2.1            3f28db6a433d   20 months ago   782MB
confluentinc/cp-zookeeper          latest           3f28db6a433d   20 months ago   782MB
confluentinc/cp-kafka-rest         7.2.1            784b8061ad0c   20 months ago   1.76GB
cnfldemos/kafka-connect-datagen    0.5.3-7.1.0      de0e2396b904   23 months ago   1.46GB
zookeeper                          3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                     2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer         1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer         1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                  krb5             ce76df4cb4a8   3 years ago     61MB
config-service                     0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm ghcr.io/wjc-van-es/catalog-service:latest
Error response from daemon: No such image: ghcr.io/wjc-van-es/catalog-service:latest
willem@linux-laptop:~/git/cnsia$ docker image rm catalog-service:latest
Untagged: catalog-service:latest
Deleted: sha256:81af438a907f2023d11d9519be3f0f62e0890c606c4451f5830fb4a9d0c1e5ae
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                         TAG              IMAGE ID       CREATED         SIZE
postgres                           16.1             d2d312b19332   3 months ago    432MB
gcr.io/k8s-minikube/kicbase        v0.0.42          dbc648475405   4 months ago    1.2GB
postgres                           15.4             69e765e8cdbe   6 months ago    412MB
postgres                           15.3             8769343ac885   7 months ago    412MB
my-java-image                      1.0.0            1016789db6dd   7 months ago    598MB
ghcr.io/wjc-van-es/my-java-image   1.0.0            1016789db6dd   7 months ago    598MB
eclipse-temurin                    17               3a958eff0206   8 months ago    456MB
testcontainers/ryuk                0.5.1            ec913eeff75a   10 months ago   12.7MB
confluentinc/ksqldb-examples       7.2.1            ae252c2780c4   20 months ago   815MB
confluentinc/cp-ksqldb-server      7.2.1            140d2ac32177   20 months ago   1.36GB
confluentinc/cp-ksqldb-cli         7.2.1            287039530a46   20 months ago   857MB
confluentinc/cp-schema-registry    7.2.1            afaac043dcc1   20 months ago   1.86GB
confluentinc/cp-kafka              7.2.1            d893473a6510   20 months ago   782MB
confluentinc/cp-kafka              latest           d893473a6510   20 months ago   782MB
confluentinc/cp-zookeeper          7.2.1            3f28db6a433d   20 months ago   782MB
confluentinc/cp-zookeeper          latest           3f28db6a433d   20 months ago   782MB
confluentinc/cp-kafka-rest         7.2.1            784b8061ad0c   20 months ago   1.76GB
cnfldemos/kafka-connect-datagen    0.5.3-7.1.0      de0e2396b904   23 months ago   1.46GB
zookeeper                          3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                     2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer         1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer         1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                  krb5             ce76df4cb4a8   3 years ago     61MB
config-service                     0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm eclipse-temurin:17 
Untagged: eclipse-temurin:17
Untagged: eclipse-temurin@sha256:638d0adcd35fc5fa5f9065307d7c458e2589b500951c001c42a1a0d6c3f71e6c
Deleted: sha256:3a958eff02065968e82032d6ecee6fd45086dfe2b32e6ae8b68d2ed161fe5db5
willem@linux-laptop:~/git/cnsia$ docker image rm my-java-image:1.0.0 
Untagged: my-java-image:1.0.0
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                         TAG              IMAGE ID       CREATED         SIZE
postgres                           16.1             d2d312b19332   3 months ago    432MB
gcr.io/k8s-minikube/kicbase        v0.0.42          dbc648475405   4 months ago    1.2GB
postgres                           15.4             69e765e8cdbe   6 months ago    412MB
postgres                           15.3             8769343ac885   7 months ago    412MB
ghcr.io/wjc-van-es/my-java-image   1.0.0            1016789db6dd   7 months ago    598MB
testcontainers/ryuk                0.5.1            ec913eeff75a   10 months ago   12.7MB
confluentinc/ksqldb-examples       7.2.1            ae252c2780c4   20 months ago   815MB
confluentinc/cp-ksqldb-server      7.2.1            140d2ac32177   20 months ago   1.36GB
confluentinc/cp-ksqldb-cli         7.2.1            287039530a46   20 months ago   857MB
confluentinc/cp-schema-registry    7.2.1            afaac043dcc1   20 months ago   1.86GB
confluentinc/cp-kafka              7.2.1            d893473a6510   20 months ago   782MB
confluentinc/cp-kafka              latest           d893473a6510   20 months ago   782MB
confluentinc/cp-zookeeper          7.2.1            3f28db6a433d   20 months ago   782MB
confluentinc/cp-zookeeper          latest           3f28db6a433d   20 months ago   782MB
confluentinc/cp-kafka-rest         7.2.1            784b8061ad0c   20 months ago   1.76GB
cnfldemos/kafka-connect-datagen    0.5.3-7.1.0      de0e2396b904   23 months ago   1.46GB
zookeeper                          3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                     2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer         1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer         1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                  krb5             ce76df4cb4a8   3 years ago     61MB
config-service                     0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm ghcr.io/wjc-van-es/my-java-image:1.0.0 
Untagged: ghcr.io/wjc-van-es/my-java-image:1.0.0
Untagged: ghcr.io/wjc-van-es/my-java-image@sha256:3c3b014fa0dd2504ab94a3ae65499d2099b0ed1ef03b7fe31238e140c6a62a35
Deleted: sha256:1016789db6ddb06ef54dbf3cadd2516239ad8f9495cda2ddad10e5c39ba88b76
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                        TAG              IMAGE ID       CREATED         SIZE
postgres                          16.1             d2d312b19332   3 months ago    432MB
gcr.io/k8s-minikube/kicbase       v0.0.42          dbc648475405   4 months ago    1.2GB
postgres                          15.4             69e765e8cdbe   6 months ago    412MB
postgres                          15.3             8769343ac885   7 months ago    412MB
testcontainers/ryuk               0.5.1            ec913eeff75a   10 months ago   12.7MB
confluentinc/ksqldb-examples      7.2.1            ae252c2780c4   20 months ago   815MB
confluentinc/cp-ksqldb-server     7.2.1            140d2ac32177   20 months ago   1.36GB
confluentinc/cp-ksqldb-cli        7.2.1            287039530a46   20 months ago   857MB
confluentinc/cp-schema-registry   7.2.1            afaac043dcc1   20 months ago   1.86GB
confluentinc/cp-kafka             7.2.1            d893473a6510   20 months ago   782MB
confluentinc/cp-kafka             latest           d893473a6510   20 months ago   782MB
confluentinc/cp-zookeeper         7.2.1            3f28db6a433d   20 months ago   782MB
confluentinc/cp-zookeeper         latest           3f28db6a433d   20 months ago   782MB
confluentinc/cp-kafka-rest        7.2.1            784b8061ad0c   20 months ago   1.76GB
cnfldemos/kafka-connect-datagen   0.5.3-7.1.0      de0e2396b904   23 months ago   1.46GB
zookeeper                         3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                    2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer        1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer        1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                 krb5             ce76df4cb4a8   3 years ago     61MB
config-service                    0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm gcr.io/k8s-minikube/kicbase:v0.0.42 
Error response from daemon: conflict: unable to remove repository reference "gcr.io/k8s-minikube/kicbase:v0.0.42" (must force) - container 187b09d3c044 is using its referenced image dbc648475405
willem@linux-laptop:~/git/cnsia$ docker ps 
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
willem@linux-laptop:~/git/cnsia$ docker ps -a
CONTAINER ID   IMAGE                                 COMMAND                  CREATED          STATUS                       PORTS     NAMES
187b09d3c044   gcr.io/k8s-minikube/kicbase:v0.0.42   "/usr/local/bin/entr…"   34 minutes ago   Exited (130) 2 minutes ago             polar
willem@linux-laptop:~/git/cnsia$ 
willem@linux-laptop:~/git/cnsia$ docker image prune
WARNING! This will remove all dangling images.
Are you sure you want to continue? [y/N] y
Total reclaimed space: 0B
willem@linux-laptop:~/git/cnsia$ 
```

### After upgrades and cleanups loading a local inage still fails
```bash
willem@linux-laptop:~/git/cnsia$ minikube start -p polar
😄  [polar] minikube v1.32.0 on Ubuntu 22.04
✨  Using the docker driver based on existing profile
👍  Starting control plane node polar in cluster polar
🚜  Pulling base image ...
🔄  Restarting existing docker container for "polar" ...
🐳  Preparing Kubernetes v1.28.3 on Docker 24.0.7 ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
🔎  Verifying Kubernetes components...
    ▪ Using image docker.io/kubernetesui/dashboard:v2.7.0
    ▪ Using image docker.io/kubernetesui/metrics-scraper:v1.0.8
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
💡  Some dashboard features require the metrics-server addon. To enable all features please run:

        minikube -p polar addons enable metrics-server  


🌟  Enabled addons: storage-provisioner, default-storageclass, dashboard
🏄  Done! kubectl is now configured to use "polar" cluster and "default" namespace by default

```

````bash
willem@linux-laptop:~/git/cnsia$ minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT

❌  Exiting due to GUEST_IMAGE_LOAD: Failed to load image: save to dir: caching images: caching image "/home/willem/.minikube/cache/images/amd64/ghcr.io/wjc-van-es/catalog-service_0.0.4-SNAPSHOT": write: unable to calculate manifest: blob sha256:3f85b0460b4d0045fe49b022b729579cdad5e950204ce9e1d4bb9023297e5598 not found

╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                                                                           │
│    😿  If the above advice does not help, please let us know:                             │
│    👉  https://github.com/kubernetes/minikube/issues/new/choose                           │
│                                                                                           │
│    Please run `minikube logs --file=logs.txt` and attach logs.txt to the GitHub issue.    │
│    Please also attach the following file to the GitHub issue:                             │
│    - /tmp/minikube_image_600345b87f3be210badc6b5a32c1c99d523003c3_0.log                   │
│                                                                                           │
╰───────────────────────────────────────────────────────────────────────────────────────────╯

willem@linux-laptop:~/git/cnsia$ 
````
#### Possible cause: incompatibility between Minikube and Docker 25
- [https://github.com/kubernetes/minikube/issues/18021](https://github.com/kubernetes/minikube/issues/18021)
- [https://github.com/moby/moby/issues/47207](https://github.com/moby/moby/issues/47207)


### Possible workaround push local image to repository and then `image pull` instead of `image load`
But we saw that a remote pull did work:
- `minikube -p polar image pull postgres:16.1` works
- `minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT`

- Renew the expired PAT or personal access token
- `docker login ghcr.io`
  - you will be prompted for your username `wjc-van-es`
  - and password, which is the PAT you just created.
  - after the message `login Succeeded` you can continue with
- `docker push ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT`

- `minikube -p polar image pull ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT` gives no complaints,
- however, `minikube -p polar image ls` doesn't show our image.
- Possible solution:
  - [https://dev.to/asizikov/using-github-container-registry-with-kubernetes-38fb](https://dev.to/asizikov/using-github-container-registry-with-kubernetes-38fb)
  - [https://stackoverflow.com/questions/71374622/how-do-i-pull-a-github-ghcr-io-from-minikube](https://stackoverflow.com/questions/71374622/how-do-i-pull-a-github-ghcr-io-from-minikube)

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
willem@linux-laptop:~/git/cnsia$ minikube -p polar image pull ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT
willem@linux-laptop:~/git/cnsia$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.28.3
registry.k8s.io/kube-proxy:v1.28.3
registry.k8s.io/kube-controller-manager:v1.28.3
registry.k8s.io/kube-apiserver:v1.28.3
registry.k8s.io/etcd:3.5.9-0
registry.k8s.io/coredns/coredns:v1.10.1
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/upmcenterprises/registry-creds:<none>
docker.io/library/postgres:16.1
docker.io/kubernetesui/metrics-scraper:<none>
docker.io/kubernetesui/dashboard:<none>
willem@linux-laptop:~/git/cnsia$ 

```

```bash
willem@linux-laptop:~/git/cnsia$ cd catalog-service/
willem@linux-laptop:~/git/cnsia/catalog-service$ kubectl apply -f k8s/
service/catalog-service created
Error from server (BadRequest): error when creating "k8s/deployment.yml": Deployment in version "v1" cannot be handled as a Deployment: strict decoding error: unknown field "spec.template.spec.containers[0].imagePullSecrets"
willem@linux-laptop:~/git/cnsia/catalog-service$ 

```

However, in the webconsole, I can see the dpr-secret under secrets and I can verify that its content is OK:
```bash
willem@linux-laptop:~$ tldr base64
base64
Encode or decode file or standard input to/from Base64, to standard output.More information: https://www.gnu.org/software/coreutils/base64.

 - Encode the contents of a file as base64 and write the result to stdout:
   base64 {{path/to/file}}

 - Decode the base64 contents of a file and write the result to stdout:
   base64 --decode {{path/to/file}}

 - Encode from stdin:
   {{somecommand}} | base64

 - Decode from stdin:
   {{somecommand}} | base64 --decode
willem@linux-laptop:~$ echo <Base64 from data: .dockerconfigjson: > | base64 --decode > dpr-secret.json
willem@linux-laptop:~$ gedit dpr-secret.json 
willem@linux-laptop:~$ echo <Base64-from the dpr-secret.json> | base64 --decode >> dpr-secret.json
willem@linux-laptop:~$ gedit dpr-secret.json 

```

- unknown field "spec.template.spec.containers[0].imagePullSecrets" reveals the entry is in the wrong place and should
  be at unknown field "spec.template.spec.imagePullSecrets"

- Now it works:
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
  
- __To be continued with actual testing, but for now__
```bash
willem@linux-laptop:~/git/cnsia$ minikube stop -p polar
✋  Stopping node "polar"  ...
🛑  Powering off "polar" via SSH ...
🛑  1 node stopped.
willem@linux-laptop:~/git/cnsia$
```