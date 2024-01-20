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
```bash
willem@linux-laptop:~/git/cnsia$ minikube start -p polar
😄  [polar] minikube v1.31.2 on Ubuntu 22.04
✨  Using the docker driver based on existing profile
👍  Starting control plane node polar in cluster polar
🚜  Pulling base image ...
🔄  Restarting existing docker container for "polar" ...
🐳  Preparing Kubernetes v1.27.4 on Docker 24.0.4 ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
🔎  Verifying Kubernetes components...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
    ▪ Using image docker.io/kubernetesui/metrics-scraper:v1.0.8
    ▪ Using image docker.io/kubernetesui/dashboard:v2.7.0
💡  Some dashboard features require the metrics-server addon. To enable all features please run:

        minikube -p polar addons enable metrics-server  


🌟  Enabled addons: storage-provisioner, default-storageclass, dashboard
🏄  Done! kubectl is now configured to use "polar" cluster and "default" namespace by default
willem@linux-laptop:~/git/cnsia$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.27.4
registry.k8s.io/kube-proxy:v1.27.4
registry.k8s.io/kube-controller-manager:v1.27.4
registry.k8s.io/kube-apiserver:v1.27.4
registry.k8s.io/etcd:3.5.7-0
registry.k8s.io/coredns/coredns:v1.10.1
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/library/postgres:16.1
docker.io/library/postgres:15.4
docker.io/kubernetesui/metrics-scraper:<none>
docker.io/kubernetesui/dashboard:<none>
willem@linux-laptop:~/git/cnsia$ minikube -p polar image prune
Manage images

Available Commands:
  build         Build a container image in minikube
  load          Load an image into minikube
  ls            List images
  pull          Pull images
  push          Push images
  rm            Remove one or more images
  save          Save a image from minikube
  tag           Tag images

Use "minikube <command> --help" for more information about a given command.

willem@linux-laptop:~/git/cnsia$ minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT

❌  Exiting due to GUEST_IMAGE_LOAD: Failed to load image: save to dir: caching images: caching image "/home/willem/.minikube/cache/images/amd64/ghcr.io/wjc-van-es/catalog-service_0.0.4-SNAPSHOT": write: unable to calculate manifest: blob sha256:84041ef6c694bef320154facfaf9fc00a97bd4258cd48fb3e4feb8968f427acb not found

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

willem@linux-laptop:~/git/cnsia$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.27.4
registry.k8s.io/kube-proxy:v1.27.4
registry.k8s.io/kube-controller-manager:v1.27.4
registry.k8s.io/kube-apiserver:v1.27.4
registry.k8s.io/etcd:3.5.7-0
registry.k8s.io/coredns/coredns:v1.10.1
gcr.io/k8s-minikube/storage-provisioner:v5
docker.io/library/postgres:16.1
docker.io/library/postgres:15.4
docker.io/kubernetesui/metrics-scraper:<none>
docker.io/kubernetesui/dashboard:<none>
willem@linux-laptop:~/git/cnsia$ 

```
- [https://github.com/kubernetes/minikube/issues/11597](https://github.com/kubernetes/minikube/issues/11597)
- [https://github.com/kubernetes/minikube/issues/17696](https://github.com/kubernetes/minikube/issues/17696)
- The image works with docker compose
- Deleting the `polar` profile and recreating it, did not fix it.
```bash
willem@linux-laptop:~/git/cnsia$ minikube delete -p polar
🔥  Deleting "polar" in docker ...
🔥  Deleting container "polar" ...
🔥  Removing /home/willem/.minikube/machines/polar ...
💀  Removed all traces of the "polar" cluster.
willem@linux-laptop:~/git/cnsia$ minikube stop
✋  Stopping node "minikube"  ...
🛑  1 node stopped.
willem@linux-laptop:~/git/cnsia$ minikube start --cpus 2 --memory 4g --driver docker --profile polar
😄  [polar] minikube v1.31.2 on Ubuntu 22.04
✨  Using the docker driver based on user configuration
📌  Using Docker driver with root privileges
👍  Starting control plane node polar in cluster polar
🚜  Pulling base image ...
🔥  Creating docker container (CPUs=2, Memory=4096MB) ...
🐳  Preparing Kubernetes v1.27.4 on Docker 24.0.4 ...
    ▪ Generating certificates and keys ...
    ▪ Booting up control plane ...
    ▪ Configuring RBAC rules ...
🔗  Configuring bridge CNI (Container Networking Interface) ...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
🔎  Verifying Kubernetes components...
🌟  Enabled addons: storage-provisioner, default-storageclass
🏄  Done! kubectl is now configured to use "polar" cluster and "default" namespace by default
willem@linux-laptop:~/git/cnsia$ minikube -p polar image ls
registry.k8s.io/pause:3.9
registry.k8s.io/kube-scheduler:v1.27.4
registry.k8s.io/kube-proxy:v1.27.4
registry.k8s.io/kube-controller-manager:v1.27.4
registry.k8s.io/kube-apiserver:v1.27.4
registry.k8s.io/etcd:3.5.7-0
registry.k8s.io/coredns/coredns:v1.10.1
gcr.io/k8s-minikube/storage-provisioner:v5
willem@linux-laptop:~/git/cnsia$ minikube -p polar image pull postgres:16.1
willem@linux-laptop:~/git/cnsia$ minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT

❌  Exiting due to GUEST_IMAGE_LOAD: Failed to load image: save to dir: caching images: caching image "/home/willem/.minikube/cache/images/amd64/ghcr.io/wjc-van-es/catalog-service_0.0.4-SNAPSHOT": write: unable to calculate manifest: blob sha256:84041ef6c694bef320154facfaf9fc00a97bd4258cd48fb3e4feb8968f427acb not found

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

willem@linux-laptop:~/git/cnsia$ minikube stop -p polar
✋  Stopping node "polar"  ...
🛑  Powering off "polar" via SSH ...
🛑  1 node stopped.
willem@linux-laptop:~/git/cnsia$ minikube stop
✋  Stopping node "minikube"  ...
🛑  1 node stopped.
willem@linux-laptop:~/git/cnsia$ 

```

### The missing blob wasn't removed during image pruning on host
as blob `sha256:84041ef6c694bef320154facfaf9fc00a97bd4258cd48fb3e4feb8968f427acb` is not found in fragment underneath
```bash
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                            TAG              IMAGE ID       CREATED         SIZE
paketobuildpacks/run-jammy-base       latest           d1e9aa4e06de   41 hours ago    105MB
postgres                              16.1             d2d312b19332   5 weeks ago     432MB
paketobuildpacks/run-jammy-base       <none>           1beddb00d6d0   6 weeks ago     104MB
postgres                              15.4             69e765e8cdbe   4 months ago    412MB
postgres                              15.3             8769343ac885   5 months ago    412MB
catalog-service                       df-0.0.1         e0fb1ebaa1b7   5 months ago    495MB
my-java-image                         1.0.0            1016789db6dd   6 months ago    598MB
ghcr.io/wjc-van-es/my-java-image      1.0.0            1016789db6dd   6 months ago    598MB
gcr.io/k8s-minikube/kicbase           v0.0.40          c6cc01e60919   6 months ago    1.19GB
eclipse-temurin                       17               3a958eff0206   6 months ago    456MB
paketobuildpacks/run                  base-cnb         f2e5000af0cb   6 months ago    87.1MB
testcontainers/ryuk                   0.5.1            ec913eeff75a   8 months ago    12.7MB
confluentinc/ksqldb-examples          7.2.1            ae252c2780c4   18 months ago   815MB
confluentinc/cp-ksqldb-server         7.2.1            140d2ac32177   18 months ago   1.36GB
confluentinc/cp-ksqldb-cli            7.2.1            287039530a46   18 months ago   857MB
confluentinc/cp-schema-registry       7.2.1            afaac043dcc1   18 months ago   1.86GB
confluentinc/cp-kafka                 7.2.1            d893473a6510   18 months ago   782MB
confluentinc/cp-kafka                 latest           d893473a6510   18 months ago   782MB
confluentinc/cp-zookeeper             7.2.1            3f28db6a433d   18 months ago   782MB
confluentinc/cp-zookeeper             latest           3f28db6a433d   18 months ago   782MB
confluentinc/cp-kafka-rest            7.2.1            784b8061ad0c   18 months ago   1.76GB
cnfldemos/kafka-connect-datagen       0.5.3-7.1.0      de0e2396b904   21 months ago   1.46GB
zookeeper                             3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                        2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer            1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer            1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                     krb5             ce76df4cb4a8   3 years ago     61MB
paketobuildpacks/builder              base             050ed48532b2   44 years ago    1.31GB
paketobuildpacks/builder-jammy-base   <none>           3fb7d5352751   44 years ago    1.56GB
ghcr.io/wjc-van-es/catalog-service    0.0.4-SNAPSHOT   ad45994fb4cb   44 years ago    322MB
config-service                        0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
ghcr.io/wjc-van-es/catalog-service    0.0.3-SNAPSHOT   5ba2428071a0   44 years ago    320MB
paketobuildpacks/builder-jammy-base   latest           07a12e865f08   44 years ago    1.58GB
willem@linux-laptop:~/git/cnsia$ docker image rm 5ba2428071a0
Untagged: ghcr.io/wjc-van-es/catalog-service:0.0.3-SNAPSHOT
Deleted: sha256:5ba2428071a0015cd97ef1ff293514d7fcd3a06d358565a0ef4680c452c5a9f9
Deleted: sha256:744084613217865cc39ce634d0299952770ecb68f3cc8690502010287fae26c9
Deleted: sha256:ba72b4ece0209608ce6529acc28dd13f0eea79674067c4b965df106b99b37b75
Deleted: sha256:8dd4a7021299f54945cc6ef62c57667a4e8c2b3ec21af32cd9cb78f8d53f921d
Deleted: sha256:a7257b62def95d6074eac6903c1189f5003c416c6a0ea7adc0b594f78aed28e9
Deleted: sha256:992cdf84f31dd06e72598a3fca88bdb3244b9e05e14af44658c79cab08db6003
Deleted: sha256:42e4838e97eb097ed6e84024ce54c0c4b99577d222b0ae35b4e7bd219361b5d2
Deleted: sha256:325eacc1d71cbac7526f5e19b4a0ce16fcfd28e16b81c2055d63b9741893a90a
Deleted: sha256:b5c0ce415fe2ef97041a2979d58b72c1db0f46e1e092f23be76210f083b843ff
Deleted: sha256:799fa5a11bfb68aab14344c68033bdf6faeef297886f106482a6edd31af52621
Deleted: sha256:8a7b16e4191863825ea0f9f0677fe17b8074e9d3683ff8a686189d9b0b5dc4cc
Deleted: sha256:152431e3532931ba4a4a8e6f297626c1861e4409501aab24bfcbd9170b01427a
Deleted: sha256:ef2d2430df1ecb68bf41d515eee3bf3b2e6cb745f0c73ab77772011e2b68b494
Deleted: sha256:204bc27cf0552856251f40ef9cffeeb0de2554538afb42f9b0b2019b24c63f49
Deleted: sha256:3a4ca182cb08bee7ddae5a316d95ea4e53f6b647ce53e3460351a64c475bdb3e
Deleted: sha256:01b2cae185b85d3e296feb2a20a29e54989b2fb33221d376acdce9cc4358b64c
Deleted: sha256:dab1f4986fcf47da70336274ed3f0e379f5a3a34481370177de0dec696f66ee3
Deleted: sha256:4dfaf921759d476fb765daf1bbda80f8fd4af515dbebacd570c0994c00cdd817
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                            TAG              IMAGE ID       CREATED         SIZE
paketobuildpacks/run-jammy-base       latest           d1e9aa4e06de   41 hours ago    105MB
postgres                              16.1             d2d312b19332   5 weeks ago     432MB
paketobuildpacks/run-jammy-base       <none>           1beddb00d6d0   6 weeks ago     104MB
postgres                              15.4             69e765e8cdbe   4 months ago    412MB
postgres                              15.3             8769343ac885   5 months ago    412MB
catalog-service                       df-0.0.1         e0fb1ebaa1b7   5 months ago    495MB
my-java-image                         1.0.0            1016789db6dd   6 months ago    598MB
ghcr.io/wjc-van-es/my-java-image      1.0.0            1016789db6dd   6 months ago    598MB
gcr.io/k8s-minikube/kicbase           v0.0.40          c6cc01e60919   6 months ago    1.19GB
eclipse-temurin                       17               3a958eff0206   6 months ago    456MB
paketobuildpacks/run                  base-cnb         f2e5000af0cb   6 months ago    87.1MB
testcontainers/ryuk                   0.5.1            ec913eeff75a   8 months ago    12.7MB
confluentinc/ksqldb-examples          7.2.1            ae252c2780c4   18 months ago   815MB
confluentinc/cp-ksqldb-server         7.2.1            140d2ac32177   18 months ago   1.36GB
confluentinc/cp-ksqldb-cli            7.2.1            287039530a46   18 months ago   857MB
confluentinc/cp-schema-registry       7.2.1            afaac043dcc1   18 months ago   1.86GB
confluentinc/cp-kafka                 7.2.1            d893473a6510   18 months ago   782MB
confluentinc/cp-kafka                 latest           d893473a6510   18 months ago   782MB
confluentinc/cp-zookeeper             7.2.1            3f28db6a433d   18 months ago   782MB
confluentinc/cp-zookeeper             latest           3f28db6a433d   18 months ago   782MB
confluentinc/cp-kafka-rest            7.2.1            784b8061ad0c   18 months ago   1.76GB
cnfldemos/kafka-connect-datagen       0.5.3-7.1.0      de0e2396b904   21 months ago   1.46GB
zookeeper                             3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                        2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer            1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer            1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                     krb5             ce76df4cb4a8   3 years ago     61MB
paketobuildpacks/builder-jammy-base   <none>           3fb7d5352751   44 years ago    1.56GB
ghcr.io/wjc-van-es/catalog-service    0.0.4-SNAPSHOT   ad45994fb4cb   44 years ago    322MB
paketobuildpacks/builder              base             050ed48532b2   44 years ago    1.31GB
paketobuildpacks/builder-jammy-base   latest           07a12e865f08   44 years ago    1.58GB
config-service                        0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image prune
WARNING! This will remove all dangling images.
Are you sure you want to continue? [y/N] 
Total reclaimed space: 0B
willem@linux-laptop:~/git/cnsia$ docker image prune
WARNING! This will remove all dangling images.
Are you sure you want to continue? [y/N] Y
Deleted Images:
untagged: paketobuildpacks/builder-jammy-base@sha256:2577685b637d2dc50012cb447ab1e549dc8691b762ddf54b8cd010e6cd8e0c4a
deleted: sha256:3fb7d535275194624e6c7eabf94521b52aafe33b6565ee9d09c6bec600ea2070
deleted: sha256:794645d898576bf1ce8967d36990cff3c8ba4ca0c86d00e495a09085a8d1a658
deleted: sha256:ab45b18484b135f21fe94ab386b34eadd119f3c4e181c08ed70de951d70b57fb
deleted: sha256:d16121a2f47fe243534af4ff33ad8262da38d627cdc96c17e8ac31fb889fa787
deleted: sha256:02815c809f4f37aafdd3dce00adec52d08167f8cc078a17873932eab361fa694
deleted: sha256:a9e4fdab31cff85278d1762367722d2f94df175ed9e4178b2040c3600e729443
deleted: sha256:df5e32eab4be3772ca6afc79de85f8560beabac72addbeef4f2546dd7b2a0cab
deleted: sha256:9d3fa76742a5dce084204d5ff9e3042e8eea8ecdcc3f57f52cb51f63ae926946
deleted: sha256:86af6eb893d209d16dd52c693a2858bcf48b11d6ec62de8c239f8277f7715bd9
deleted: sha256:b10e01d85980dc94de86bcfa693a9e25d11dedffac3084472e4b72d27a486580
deleted: sha256:c664d9f181cfc8b7fd635c821d68c29d21ff606a58c5021c726fa204bc9eee5d
deleted: sha256:84302bae3cf34bb719e754874da83aca08324665c4bf039c02fa74216d40f28d
deleted: sha256:88d9e73dd0999f3b07575b655cda078451ea9c5d032c34ac4dedebad3c474791
deleted: sha256:a132dbc9bb7020f90d531811822a2300fd8532691ec06a6ef92d1e904e5c6a25
deleted: sha256:bd82fa9d9e665e52c43e143017b5e188ee298645099634e98b8f6e56c4196864
deleted: sha256:62d2a6166391f6a4b724e10ece039c305643ee37f16468b07bdd371be3ad68a6
deleted: sha256:b9b995622dd08cf9cc4708f306c3335082281f0cc70a41f77bdcab2c88b194a0
deleted: sha256:5eaac9f3c9fb8bb78037791a493a41998753e27dde5001cf29a2fbdffbdd3e0b
deleted: sha256:1cae8c48d9f1f8824466125b5fc997c521b70f7743fad3d7f59ee01349d91e3c
deleted: sha256:81f900ca329052e55560ef3ec83918a7025252ab166cf5993811874306384ef2
deleted: sha256:d8ba373258f20513deff6b339a3b481d4d382adf22d76484a690a267f8e85653
deleted: sha256:2ef393c5eeb422efcd2501446beb8f378b9af8d6086a5093ae9f151c6f50163f
deleted: sha256:e8bd39ea60630908f0426c64a0554c562e7306ddf36365d5365aa35b41343ec5
deleted: sha256:b47f8a8984958031805e8c2b48b2e180fdbb890828030b4d65bab98fecf1dbe5
deleted: sha256:749fc01a8dd94e141a9f36c94e7d9396405733be8c9c3c5ef59f2700a344c494
deleted: sha256:b9e8feec7f32be91fe5c76039e8e6dc0cd10549e4acb89725ec79aa0b337c0e0
deleted: sha256:9fb83a6e42076446b2a06fb7fb45e879eff640699ff3fb9769092194d4b08ab6
deleted: sha256:34008126c22faf0a01e0d7abbe471d48bd4d60a0eed9306ea394a44182446d72
deleted: sha256:bedf7b74e70367b7d3993f1aa89335a78f36ad0af0070f43020a9e87b92a033a
deleted: sha256:1597920b3edcbfa265eb83032222610ed9736ddac225aa3b5ea21a7a80db706a
deleted: sha256:595e712e7a395438f21df1e616becfc24cd2c3c2de90ded8bb290d2ccf432e85
deleted: sha256:d5177bc5f3a955582af9dc052bf934b104fd9d8b589493a6da5d8f4108ff60d3
deleted: sha256:bfa55f8bc324292dcaa150478ffa8a4b58eaa5ab535bafd9f469aa7e1a940169
deleted: sha256:d6b81dae25e86f6810a7cfce16c8ae330a21fb81551823fc62afdcba7de09378
deleted: sha256:4c89a74b3e346e6699c40876e9d156ab48265b2dae8ce8badc2adaf27b8c58ed
deleted: sha256:639d69732fb9eb2af84fb0f4456b50671c719507b673970cc7f00de26d2bd8ac
deleted: sha256:16d10c8ff7f1f1c1514f089f0805cb8def7d6ca382fc74b77475356d8618ea84
deleted: sha256:13650bd161130b233f7e15b73c5746995ca7a188c4e75e4bfd2884b0470b8bff
deleted: sha256:0b95db27a14beec73069f35fab5be2e6cdd4e9efd766e8fd01599134910f2d99
deleted: sha256:17d0cd7a2094726d93cf34e6b4770992d7fd6635a568f694d37abfea2582c287
deleted: sha256:31b5cc21f2ad882a97648a8c2af5988f09f66eb6c1f744639a7ef656a8d08d75
deleted: sha256:3917110d808963d9942c1cccd77d84f3bbe2bf51fb4628a3176f6079361bc755
deleted: sha256:82bd95258542667a44af8ef709b9424b92d5d974a39c06671beb3203d1a5cf10
deleted: sha256:329bb4227c342a9292f45d6d8b59837f84f75ece7699a1ef683406682d5e468e
deleted: sha256:dc0a2ee98f46c42b11fd6497da6597c967a0ddf01ff2af5f20c720407216bee8
deleted: sha256:9b8c7dc3f34113bbf15ca2766901e70afbc72783eb84a088f50de1b911ab1a48
deleted: sha256:35894e45b3e89ff1bcb6483d95e4187d7192e1f6a03cbd9fd96203a7097232d3
deleted: sha256:1dd5bfa55fef8c86c319307658d9e04d62dea39730951b11ddabfae960b729f5
deleted: sha256:815dce360f953926d390a088127ee65bde6bc6baa082c869c151eb0f281b6b6e
deleted: sha256:a1de2dd4d17bdd35d51f2825c5e023cf8931c916e1cd0b6f440e87eda4e649d5
deleted: sha256:9ea2dee66d6d3d5ed06d06f5d657d0c1cbd6e4131d0e11ce2cf8859637faa2b0
deleted: sha256:c4b607e2cfc855f2d6c465124648fe5318f88cd001877adda2862eb54414ad66
deleted: sha256:4346511e3a9fa4a7ffa395a7fc1e25944440d4f374d4dca2446c47dba588fff4
deleted: sha256:76be305ae984905b86ce4cf8547bcd66a40410239959ddbab61198659bc13e98
deleted: sha256:35e6d251ff61556d923337a79ef332269391313c1ae0c03c66df9cf199fb4749
deleted: sha256:b2559b27615ef002447caf1d31ed735412a1c86b4836bd3d79de671c6decd224
deleted: sha256:26559d3a9bb700d59fb9ef6cc9528070f31c27caa584207f767651b9f3b71c13
deleted: sha256:52828d86651e83780dfd5520e16890cbb4b06992486c915d2ee03587b02ca943
deleted: sha256:7d63cc43689184298361b20d273b7f3acb1e03ea9e0bbea569cd200cc0cec0d4
deleted: sha256:d1072410d11fad5742bbcef5e30aa1c6b1f04c3f053a204e082939115a2db484
deleted: sha256:a32c37cf77ef2377a7efb745e0cfc609e142f3ae733edf6ecbd2d0a0893644e4
deleted: sha256:c92a0cf4fb1db8d0f0a18d9667a8bb5e101f96faeb4ac8b5a201379229c1fef5
deleted: sha256:8e58cbd977b622197e3eb011cd58bb793e06915814d62d0d3644997169ddd53e
deleted: sha256:196ccf5f7a5f085dbb4a1e564773c148b331df65d3c9b90ce8a607b03e453d08
deleted: sha256:84bf927f5c7880b3733b47e898305b808dcc2bf7cf75ba34480510b67a3ae001
deleted: sha256:1ac9f572a904028524b5351911c48ff676b58dc83407cae5da9cfcae9d796091
deleted: sha256:eed35b5a06aadd9ab1f19c05da77ce790ead710f16b8a99c072893da1f62231f
deleted: sha256:4668f2ace4aff721f08b4c2a4a3d15367299d0f6bd621f66dfbdd9c4e49650dd
deleted: sha256:720afe9a23086a0ccb80a19fa76084ebfd29d99815124127e6974c92d96787a3
deleted: sha256:719879c4748c5e428f5f747fd2594060c50fc7bfee4ff38c365bd08ee78f5b8c
deleted: sha256:bd574cbf98d7eac14c7f889ece622e528d52b7b5ad9582968177c0d2c18a2ba6
deleted: sha256:d82097adc87d7c78a2477f3dc5e69ce25ed3043879bcee333984cf5cc8ddf537
deleted: sha256:2a7d1e7217c70f10eea4a9bef3e72b3572e1efb06514c38ebf231be741f59a5a
deleted: sha256:9681eaa5066d3af07c98e270cb1d0d96028f6041835e3985f333403071299bd5
deleted: sha256:51ca370e233d4b84daeededb8ad3b6fe1571d93a26aea60e7bb72c13b4aa2323
deleted: sha256:950efc7fd0b35cd48c70d7b19d68021366843c51a4f902f072b5379e497b82f5
deleted: sha256:1a96d7fc7bee48ee84c54e3626e6da5f5fc9c83f8798119f2bcb02a76bfda0c4
deleted: sha256:557dd89ee995079d119a8f990374e38e213459b355111704dbdb8e2f6dc095bf
deleted: sha256:4ca4d027c1399bdf1cf05419c3e470745ef922970caa8d3d1ea7a209388501b4
deleted: sha256:63dacb2d44d14f0f19a49a9f3018af3ef4a85ca61497c4c7ea82b0db51262bbe
deleted: sha256:50adaa344b36d784dca20863d4711fe37d98766e579561d08892ae4b437c29e5
deleted: sha256:e5835a38dbd3dab7cc1ac4f2a024895cb9dcbb2f0a67a91ab57b7ee08204b91e
deleted: sha256:00da8b5f6b1c86d34477825b6403e21c36994af656f60506b21c2ea80b47f492
deleted: sha256:c511bdf849a253c4bb8dc70b85bb78ef291baadea301f9ffa9c9f575c22369be
deleted: sha256:3b5befa230f5c8ff8dfeac1190ba70a77a36c9d09324a8e288e4395c4d9dc504
deleted: sha256:51f680120b6c7f264c9a40d9947d0fc25a7a343a94c15ff8307cf56525ee3d5b
deleted: sha256:614ec80f2dec42cdaccbb38e1785e60cc10e730f757a3d8b171a6fe7df2acb77
deleted: sha256:ed0f038547a4c6e2b3e2429e34e4d107a886c3ea7e832ec27f637bd956ce814c
deleted: sha256:1d21b9dc701925ed083d655543f20d50dfa92deb6414057a9d3713f5f198a42b
deleted: sha256:3729d96099df111582945ed8fa80b6ec0d30ac15cc1f9d64782920a69247ffeb
deleted: sha256:523c402e678d6a8f261213c583d4675e6aff492df8ecff81b7b103a373d5ee39
deleted: sha256:3b6814f981365851251b47344af51b23b1cfaea25bb42ac6d05e15261ddaaacb
deleted: sha256:14c8c4d3f29d4eb9d66437f24294532c455b5a157e56af1cec73f6c054413ec3
deleted: sha256:2829f3ed9803213681e884d9504e4a97511f73bf181fb0156777cb69951dacc9
deleted: sha256:e1a10709206f25a65f2aadd21983539b58d8d3e8f3ac4064c5457182d0ecc5a8
deleted: sha256:2e75eb32fa591c2264e073b87a0b6ea17a58b392fc0deaa2751852b7e3e4cff6
deleted: sha256:8e25342383dad6c703cd3c297a10d71f0ff5ff09056dde690d9597b059443bc4
deleted: sha256:89bcb8be4cd7e673040b0664b828881f5d4b52f05df94cce67a44698aa046890
deleted: sha256:3c01b89309d26768c73a378ef9721958371fea4b37e5cdbf350cf6b02008d0e7
deleted: sha256:72cd03927c07cddba98ff0ac89a56202b952d484918e50599cb20a55025f147e
deleted: sha256:95b303f1a4cfe3a1692f1c6f73bc154eae99af923cd23c0ad345de82002975a3
deleted: sha256:256d88da41857db513b95b50ba9a9b28491b58c954e25477d5dad8abb465430b
untagged: paketobuildpacks/run-jammy-base@sha256:59cc28970311802e0f8a2475050faada233a24ac19683fe0b11c1fe89f01bfb1
deleted: sha256:1beddb00d6d0249ecc05e7ba3591a1eb1663d4354e71b6f0722d35c9df1a2123
deleted: sha256:f28ac65e2c55d7592954d8e402c59ed052806c5f5a68c5722b7d15283d2ae5b3
deleted: sha256:995a173d4ccb647b2e222cddbd8c0604b1a5817e6645a5399147b5e47a6eabbd
deleted: sha256:c42d470ae80cf04536922f5eb7cab673b7d75c67ae562dd615eabe2985754c0f
deleted: sha256:8ceb9643fb36a8ac65882c07e7b2fff9fd117673d6784221a83d3ad076a9733e

Total reclaimed space: 1.66GB
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                            TAG              IMAGE ID       CREATED         SIZE
paketobuildpacks/run-jammy-base       latest           d1e9aa4e06de   41 hours ago    105MB
postgres                              16.1             d2d312b19332   5 weeks ago     432MB
postgres                              15.4             69e765e8cdbe   4 months ago    412MB
postgres                              15.3             8769343ac885   5 months ago    412MB
catalog-service                       df-0.0.1         e0fb1ebaa1b7   5 months ago    495MB
my-java-image                         1.0.0            1016789db6dd   6 months ago    598MB
ghcr.io/wjc-van-es/my-java-image      1.0.0            1016789db6dd   6 months ago    598MB
gcr.io/k8s-minikube/kicbase           v0.0.40          c6cc01e60919   6 months ago    1.19GB
eclipse-temurin                       17               3a958eff0206   6 months ago    456MB
paketobuildpacks/run                  base-cnb         f2e5000af0cb   6 months ago    87.1MB
testcontainers/ryuk                   0.5.1            ec913eeff75a   8 months ago    12.7MB
confluentinc/ksqldb-examples          7.2.1            ae252c2780c4   18 months ago   815MB
confluentinc/cp-ksqldb-server         7.2.1            140d2ac32177   18 months ago   1.36GB
confluentinc/cp-ksqldb-cli            7.2.1            287039530a46   18 months ago   857MB
confluentinc/cp-schema-registry       7.2.1            afaac043dcc1   18 months ago   1.86GB
confluentinc/cp-kafka                 7.2.1            d893473a6510   18 months ago   782MB
confluentinc/cp-kafka                 latest           d893473a6510   18 months ago   782MB
confluentinc/cp-zookeeper             7.2.1            3f28db6a433d   18 months ago   782MB
confluentinc/cp-zookeeper             latest           3f28db6a433d   18 months ago   782MB
confluentinc/cp-kafka-rest            7.2.1            784b8061ad0c   18 months ago   1.76GB
cnfldemos/kafka-connect-datagen       0.5.3-7.1.0      de0e2396b904   21 months ago   1.46GB
zookeeper                             3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                        2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer            1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer            1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                     krb5             ce76df4cb4a8   3 years ago     61MB
config-service                        0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
paketobuildpacks/builder              base             050ed48532b2   44 years ago    1.31GB
ghcr.io/wjc-van-es/catalog-service    0.0.4-SNAPSHOT   ad45994fb4cb   44 years ago    322MB
paketobuildpacks/builder-jammy-base   latest           07a12e865f08   44 years ago    1.58GB
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                            TAG              IMAGE ID       CREATED         SIZE
paketobuildpacks/run-jammy-base       latest           d1e9aa4e06de   41 hours ago    105MB
postgres                              16.1             d2d312b19332   5 weeks ago     432MB
postgres                              15.4             69e765e8cdbe   4 months ago    412MB
postgres                              15.3             8769343ac885   5 months ago    412MB
catalog-service                       df-0.0.1         e0fb1ebaa1b7   5 months ago    495MB
my-java-image                         1.0.0            1016789db6dd   6 months ago    598MB
ghcr.io/wjc-van-es/my-java-image      1.0.0            1016789db6dd   6 months ago    598MB
gcr.io/k8s-minikube/kicbase           v0.0.40          c6cc01e60919   6 months ago    1.19GB
eclipse-temurin                       17               3a958eff0206   6 months ago    456MB
paketobuildpacks/run                  base-cnb         f2e5000af0cb   6 months ago    87.1MB
testcontainers/ryuk                   0.5.1            ec913eeff75a   8 months ago    12.7MB
confluentinc/ksqldb-examples          7.2.1            ae252c2780c4   18 months ago   815MB
confluentinc/cp-ksqldb-server         7.2.1            140d2ac32177   18 months ago   1.36GB
confluentinc/cp-ksqldb-cli            7.2.1            287039530a46   18 months ago   857MB
confluentinc/cp-schema-registry       7.2.1            afaac043dcc1   18 months ago   1.86GB
confluentinc/cp-kafka                 7.2.1            d893473a6510   18 months ago   782MB
confluentinc/cp-kafka                 latest           d893473a6510   18 months ago   782MB
confluentinc/cp-zookeeper             7.2.1            3f28db6a433d   18 months ago   782MB
confluentinc/cp-zookeeper             latest           3f28db6a433d   18 months ago   782MB
confluentinc/cp-kafka-rest            7.2.1            784b8061ad0c   18 months ago   1.76GB
cnfldemos/kafka-connect-datagen       0.5.3-7.1.0      de0e2396b904   21 months ago   1.46GB
zookeeper                             3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                        2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer            1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer            1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                     krb5             ce76df4cb4a8   3 years ago     61MB
paketobuildpacks/builder              base             050ed48532b2   44 years ago    1.31GB
paketobuildpacks/builder-jammy-base   latest           07a12e865f08   44 years ago    1.58GB
config-service                        0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
ghcr.io/wjc-van-es/catalog-service    0.0.4-SNAPSHOT   ad45994fb4cb   44 years ago    322MB
willem@linux-laptop:~/git/cnsia$ docker image rm ad45994fb4cb
Untagged: ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT
Deleted: sha256:ad45994fb4cbcec63729497abfbb1bca32b2ac5aef886951a42e5e54d14e1843
Deleted: sha256:1fa762239735a5ce6b7ce24899bdb608040d779e0520f0c2b3b8634b9738c103
Deleted: sha256:581267276bc18965f7d38d06c8d85d05613ead52e2cf3ed61fff08d042f8b8c3
Deleted: sha256:8ff4b246c7483d82cd3c04dbd55c1da5a1f5ff9a74186483703b02fc7c98e9f8
willem@linux-laptop:~/git/cnsia$ docker image prune
WARNING! This will remove all dangling images.
Are you sure you want to continue? [y/N] y
Total reclaimed space: 0B
willem@linux-laptop:~/git/cnsia$ 


```
Then recreating the image with `mvn spring-boot:build-image -e` and subsequent load still fails

### Trying to further clean up old Buildpack images
```bash
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                         TAG              IMAGE ID       CREATED         SIZE
catalog-service                    latest           81af438a907f   5 minutes ago   501MB
paketobuildpacks/run-jammy-base    latest           d1e9aa4e06de   43 hours ago    105MB
postgres                           16.1             d2d312b19332   5 weeks ago     432MB
postgres                           15.4             69e765e8cdbe   4 months ago    412MB
postgres                           15.3             8769343ac885   5 months ago    412MB
catalog-service                    df-0.0.1         e0fb1ebaa1b7   5 months ago    495MB
my-java-image                      1.0.0            1016789db6dd   6 months ago    598MB
ghcr.io/wjc-van-es/my-java-image   1.0.0            1016789db6dd   6 months ago    598MB
gcr.io/k8s-minikube/kicbase        v0.0.40          c6cc01e60919   6 months ago    1.19GB
eclipse-temurin                    17               3a958eff0206   6 months ago    456MB
paketobuildpacks/run               base-cnb         f2e5000af0cb   6 months ago    87.1MB
testcontainers/ryuk                0.5.1            ec913eeff75a   8 months ago    12.7MB
confluentinc/ksqldb-examples       7.2.1            ae252c2780c4   18 months ago   815MB
confluentinc/cp-ksqldb-server      7.2.1            140d2ac32177   18 months ago   1.36GB
confluentinc/cp-ksqldb-cli         7.2.1            287039530a46   18 months ago   857MB
confluentinc/cp-schema-registry    7.2.1            afaac043dcc1   18 months ago   1.86GB
confluentinc/cp-kafka              7.2.1            d893473a6510   18 months ago   782MB
confluentinc/cp-kafka              latest           d893473a6510   18 months ago   782MB
confluentinc/cp-zookeeper          7.2.1            3f28db6a433d   18 months ago   782MB
confluentinc/cp-zookeeper          latest           3f28db6a433d   18 months ago   782MB
confluentinc/cp-kafka-rest         7.2.1            784b8061ad0c   18 months ago   1.76GB
cnfldemos/kafka-connect-datagen    0.5.3-7.1.0      de0e2396b904   21 months ago   1.46GB
zookeeper                          3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                     2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer         1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer         1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                  krb5             ce76df4cb4a8   3 years ago     61MB
config-service                     0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm catalog-service:latest
Untagged: catalog-service:latest
Deleted: sha256:81af438a907f2023d11d9519be3f0f62e0890c606c4451f5830fb4a9d0c1e5ae
willem@linux-laptop:~/git/cnsia$ docker image rm catalog-service:df-0.0.1 
Untagged: catalog-service:df-0.0.1
Deleted: sha256:e0fb1ebaa1b750829e346f75cbb2c9b0e7613510617aa90dce1d180e8c0616e5
willem@linux-laptop:~/git/cnsia$ docker image rm paketobuildpacks/run
Error response from daemon: No such image: paketobuildpacks/run:latest
willem@linux-laptop:~/git/cnsia$ docker image rm paketobuildpacks/run-jammy-base:latest 
Untagged: paketobuildpacks/run-jammy-base:latest
Untagged: paketobuildpacks/run-jammy-base@sha256:53460f911ac95469e4a796555d8c49f7c70d61caa6c120a1b7eb07248986d52a
Deleted: sha256:d1e9aa4e06dedac0ca4e6cd6b7ddf17311aaff6777cec74e4287369f73b3b6ac
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                         TAG              IMAGE ID       CREATED         SIZE
postgres                           16.1             d2d312b19332   5 weeks ago     432MB
postgres                           15.4             69e765e8cdbe   4 months ago    412MB
postgres                           15.3             8769343ac885   5 months ago    412MB
my-java-image                      1.0.0            1016789db6dd   6 months ago    598MB
ghcr.io/wjc-van-es/my-java-image   1.0.0            1016789db6dd   6 months ago    598MB
gcr.io/k8s-minikube/kicbase        v0.0.40          c6cc01e60919   6 months ago    1.19GB
eclipse-temurin                    17               3a958eff0206   6 months ago    456MB
paketobuildpacks/run               base-cnb         f2e5000af0cb   6 months ago    87.1MB
testcontainers/ryuk                0.5.1            ec913eeff75a   8 months ago    12.7MB
confluentinc/ksqldb-examples       7.2.1            ae252c2780c4   18 months ago   815MB
confluentinc/cp-ksqldb-server      7.2.1            140d2ac32177   18 months ago   1.36GB
confluentinc/cp-ksqldb-cli         7.2.1            287039530a46   18 months ago   857MB
confluentinc/cp-schema-registry    7.2.1            afaac043dcc1   18 months ago   1.86GB
confluentinc/cp-kafka              7.2.1            d893473a6510   18 months ago   782MB
confluentinc/cp-kafka              latest           d893473a6510   18 months ago   782MB
confluentinc/cp-zookeeper          7.2.1            3f28db6a433d   18 months ago   782MB
confluentinc/cp-zookeeper          latest           3f28db6a433d   18 months ago   782MB
confluentinc/cp-kafka-rest         7.2.1            784b8061ad0c   18 months ago   1.76GB
cnfldemos/kafka-connect-datagen    0.5.3-7.1.0      de0e2396b904   21 months ago   1.46GB
zookeeper                          3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                     2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer         1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer         1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                  krb5             ce76df4cb4a8   3 years ago     61MB
config-service                     0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ docker image rm paketobuildpacks/run:base-cnb 
Untagged: paketobuildpacks/run:base-cnb
Untagged: paketobuildpacks/run@sha256:1af9935d8987fd52b2266d288200c9482d1dd5529860bbf5bc2d248de1cb1a38
Deleted: sha256:f2e5000af0cb0db7d16a2939fc88c2aa522e91826c0e87ef2a4ec2825b213585
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                         TAG              IMAGE ID       CREATED         SIZE
postgres                           16.1             d2d312b19332   5 weeks ago     432MB
postgres                           15.4             69e765e8cdbe   4 months ago    412MB
postgres                           15.3             8769343ac885   5 months ago    412MB
my-java-image                      1.0.0            1016789db6dd   6 months ago    598MB
ghcr.io/wjc-van-es/my-java-image   1.0.0            1016789db6dd   6 months ago    598MB
gcr.io/k8s-minikube/kicbase        v0.0.40          c6cc01e60919   6 months ago    1.19GB
eclipse-temurin                    17               3a958eff0206   6 months ago    456MB
testcontainers/ryuk                0.5.1            ec913eeff75a   8 months ago    12.7MB
confluentinc/ksqldb-examples       7.2.1            ae252c2780c4   18 months ago   815MB
confluentinc/cp-ksqldb-server      7.2.1            140d2ac32177   18 months ago   1.36GB
confluentinc/cp-ksqldb-cli         7.2.1            287039530a46   18 months ago   857MB
confluentinc/cp-schema-registry    7.2.1            afaac043dcc1   18 months ago   1.86GB
confluentinc/cp-kafka              7.2.1            d893473a6510   18 months ago   782MB
confluentinc/cp-kafka              latest           d893473a6510   18 months ago   782MB
confluentinc/cp-zookeeper          7.2.1            3f28db6a433d   18 months ago   782MB
confluentinc/cp-zookeeper          latest           3f28db6a433d   18 months ago   782MB
confluentinc/cp-kafka-rest         7.2.1            784b8061ad0c   18 months ago   1.76GB
cnfldemos/kafka-connect-datagen    0.5.3-7.1.0      de0e2396b904   21 months ago   1.46GB
zookeeper                          3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                     2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer         1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer         1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                  krb5             ce76df4cb4a8   3 years ago     61MB
config-service                     0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
willem@linux-laptop:~/git/cnsia$ 

```
- Now with another `mvn spring-boot:build-image -e`
```bash
willem@linux-laptop:~/git/cnsia$ docker image ls
REPOSITORY                            TAG              IMAGE ID       CREATED         SIZE
paketobuildpacks/run-jammy-base       latest           d1e9aa4e06de   43 hours ago    105MB
postgres                              16.1             d2d312b19332   5 weeks ago     432MB
postgres                              15.4             69e765e8cdbe   4 months ago    412MB
postgres                              15.3             8769343ac885   5 months ago    412MB
my-java-image                         1.0.0            1016789db6dd   6 months ago    598MB
ghcr.io/wjc-van-es/my-java-image      1.0.0            1016789db6dd   6 months ago    598MB
gcr.io/k8s-minikube/kicbase           v0.0.40          c6cc01e60919   6 months ago    1.19GB
eclipse-temurin                       17               3a958eff0206   6 months ago    456MB
testcontainers/ryuk                   0.5.1            ec913eeff75a   8 months ago    12.7MB
confluentinc/ksqldb-examples          7.2.1            ae252c2780c4   18 months ago   815MB
confluentinc/cp-ksqldb-server         7.2.1            140d2ac32177   18 months ago   1.36GB
confluentinc/cp-ksqldb-cli            7.2.1            287039530a46   18 months ago   857MB
confluentinc/cp-schema-registry       7.2.1            afaac043dcc1   18 months ago   1.86GB
confluentinc/cp-kafka                 7.2.1            d893473a6510   18 months ago   782MB
confluentinc/cp-kafka                 latest           d893473a6510   18 months ago   782MB
confluentinc/cp-zookeeper             7.2.1            3f28db6a433d   18 months ago   782MB
confluentinc/cp-zookeeper             latest           3f28db6a433d   18 months ago   782MB
confluentinc/cp-kafka-rest            7.2.1            784b8061ad0c   18 months ago   1.76GB
cnfldemos/kafka-connect-datagen       0.5.3-7.1.0      de0e2396b904   21 months ago   1.46GB
zookeeper                             3.6.2            a72350516291   2 years ago     268MB
bsucaciu/kafka                        2.6.0            cbe9ab39d5fc   3 years ago     642MB
bsucaciu/kerberos-producer            1.0.0            eb27c056cc20   3 years ago     354MB
bsucaciu/kerberos-consumer            1.0.0            5486c13e058b   3 years ago     354MB
bsucaciu/kerberos                     krb5             ce76df4cb4a8   3 years ago     61MB
ghcr.io/wjc-van-es/catalog-service    0.0.4-SNAPSHOT   ad45994fb4cb   44 years ago    322MB
config-service                        0.0.1-SNAPSHOT   0726fd86ac0e   44 years ago    297MB
paketobuildpacks/builder-jammy-base   latest           07a12e865f08   44 years ago    1.58GB
willem@linux-laptop:~/git/cnsia$ 

```
- `minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT` still gives the same fault
### Trying to build the image from the old Dockerfile instead of using Buildpacks
- `mvn clean package -e`
- `~/git/cnsia/catalog-service$ docker build -t catalog-service .`
- `~/git/cnsia$ docker tag catalog-service:latest ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT`
- `~/git/cnsia$ minikube -p polar image load ghcr.io/wjc-van-es/catalog-service:0.0.4-SNAPSHOT` still has the same
  error: `write: unable to calculate manifest: blob sha256:f7cdc50d9449c4faea3840bb18a053f15340d08385f9ab9ed1a72bce5f345f48 not found`
  - only the sha256 digest is different.