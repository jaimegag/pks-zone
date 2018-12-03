# Running Istio in PKS
This guide provides steps and configuration samples to instal Istion via Helm Chart Templates
This example assumes you are not using NSX-T.
Make sure you clone this repo to get access to the yml files in it that will be referenced during this tutorial.

## 1- Install Helm

### Install Helm client
To install Helm client (CLI) follow the helm repository instructions [here](https://github.com/helm/helm/blob/master/docs/install.md#installing-the-helm-client)

### Install Tiller
To install Tiller with with RBAC you can refer to the helm repository instructions [here](https://github.com/helm/helm/blob/master/docs/rbac.md)

This repo includes a `rbac-config.yml` file in this same folder with the configuration for Tiller `ServiceAccount` and `ClusterRoleBinding`.
Run these commands to set up the configuration and install Tiller
```
kubectl apply -f rbac-config.yml
helm init --upgrade --service-account tiller
```

## 2- Install Istio with Helm
To install Istio via Helm you can refer to the instructions [here](https://istio.io/docs/setup/kubernetes/helm-install/). We'll detail the different steps below.

### Download the Istio Release
- Download the release artifact from [here](https://github.com/istio/istio/releases)
- Unpackage the artifact and move (`cd`) to the Istio package directory.
- Add the istioctl client to your PATH environment variable, on a macOS or Linux system. Run:
  ```
  export PATH=$PWD/bin:$PATH
  ```

### Install Cert Manager CRD
If you are enabling certmanager, you also need to install its CRDs as well and wait a few seconds for the CRDs to be committed in the kube-apiserver. Run:
```
kubectl apply -f install/kubernetes/helm/istio/charts/certmanager/templates/crds.yaml
```

### Install with Helm via helm template
Render Istio’s core components to a Kubernetes manifest called istio.yaml. Run:
```
helm template install/kubernetes/helm/istio --name istio --namespace istio-system > istio.yaml
```
Install the components via the manifest:
```
kubectl create namespace istio-system
kubectl apply -f istio.yaml
```

## 3- Test Istio with Bookinfo Application
Refer to the details and instructions [here](https://istio.io/docs/examples/bookinfo/). We'll detail the different steps below.

This is a sample application composed of four separate microservices used to demonstrate various Istio features. The application displays information about a book, similar to a single catalog entry of an online book store. Displayed on the page is a description of the book, book details (ISBN, number of pages, and so on), and a few book reviews.

The Bookinfo application is broken into four separate microservices:
- productpage. The productpage microservice calls the details and reviews microservices to populate the page.
- details. The details microservice contains book information.
- reviews. The reviews microservice contains book reviews. It also calls the ratings microservice.
- ratings. The ratings microservice contains book ranking information that accompanies a book review.

There are 3 versions of the reviews microservice:
- Version v1 doesn’t call the ratings service.
- Version v2 calls the ratings service, and displays each rating as 1 to 5 black stars.
- Version v3 calls the ratings service, and displays each rating as 1 to 5 red stars.

### Deploy Bookinfo with manual sidecar injection

Change directory to the root of the Istio installation directory and vring up the application containers. Run:
```
kubectl apply -f <(istioctl kube-inject -f samples/bookinfo/platform/kube/bookinfo.yaml)
```
Confirm all services and pods are running. Run:
```
kubectl get services,pods
```
You should see:
- 4 services listening on por 9080/TCP: `details, productpage, ratings, reviews`
- 6 pods (with envoy sidecar): `details-v1, productpage-v1, ratings-v1` and `reviews-v1, reviews-v2, reviews-v3`

### Expose Bookinfo
Now that the Bookinfo services are up and running, you need to make the application accessible from outside of your Kubernetes cluster, e.g., from a browser. An Istio Gateway is used for this purpose.

Define the ingress-gateway for the application. Run:
```
kubectl apply -f samples/bookinfo/networking/bookinfo-gateway.yaml
```

Determine Host. Run:
```
export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
```
If on AWS run:
```
export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
```

Determine Ports. Run:
```
export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].port}')
export SECURE_INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].port}')
```

Build Your Product Page URL
```
export PRODUCTPAGE_URL=http://${INGRESS_HOST}:${INGRESS_PORT}/productpage
```

Go to your browser and access the `PRODUCTPAGE_URL`:
```
open "${PRODUCTPAGE_URL}"
```
If you refresh the page several times, you should see different versions of reviews shown in productpage, presented in a round robin style (red stars, black stars, no stars), since we haven’t yet used Istio to control the version routing, and all reviews-vX pods have the same `app` label name (`reviews`), so the kubernetes service points to the three pods.

### Define Destination Rules
To control the Bookinfo version routing, you need to define the available versions, called subsets, in destination rules. We will create the default Destination Rules for all services. Run:
```
kubectl apply -f samples/bookinfo/networking/destination-rule-all-mtls.yaml
```
This won't change the behavior yet, but sets the configuration so that we can use Istio to control it.

### Definte v1 Virtual Servers
To route to one version only, you apply virtual services that set the default version for the microservices. In this case, the virtual services will route all traffic to v1 of each microservice. Run:
```
kubectl apply -f samples/bookinfo/networking/virtual-service-all-v1.yaml
```
Open the Bookinfo site in your browser. Notice that the reviews part of the page displays with no rating stars, no matter how many times you refresh. This is because you configured Istio to route all traffic for the reviews service to the version reviews:v1 and this version of the service does not access the star ratings service.
