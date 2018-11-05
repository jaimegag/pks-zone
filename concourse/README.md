# Concourse Helm Chart  
This guide provides basic instructions and a sample script to be used to deploy Concourse via Helm Chart.
Make sure you clone this repo to get access to the yml file in it that will be referenced during this tutorial.

## Prerequisites
You'll need a provisioned kubernetes cluster, and access to it via `kubectl`.


## Install Helm and Tiller
There are two parts to Helm: The Helm client (`helm`) and the Helm server (`Tiller`). This guide shows how to install the client, and then proceeds to show two ways to install the server.

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

## Install Concourse Helm Chart
The following steps will deploy Concourse in your k8s cluster using the [Concourse stable Helm Chart](https://github.com/helm/charts/tree/master/stable/concourse).

To update information of available charts locally from chart repositories, run:
```
helm repo update
```

To install Concourse with name `my-concourse`, run:
```
helm install --name my-concourse stable/concourse
```
After running that command, Concourse components will be installed in the `default` namespace

## Use concourse
To access Concourse API and Web UI we will use the `port forwarding` capabilities of the `kubectl` CLI.
First we need to get the pod name for the `concourse-web` application that was created in the previous step. Run the following command:
```
export POD_NAME=$(kubectl get pods --namespace default -l "app=my-concourse-web" -o jsonpath="{.items[0].metadata.name}")
```
Then use that port to create a tunnel to map a port in your workstation with the port in the pod. Run the following command:
```
kubectl port-forward --namespace default $POD_NAME 8080:8080 &
```
Now you can access the Concourse Web UI on `http://127.0.0.1:8080`
To register this Concourse instance as target in your Concourse CLI (`fly`) run:
```
fly -t k8s login -c http://127.0.0.1:8080/ -k
```

## Wrap up
For convenience, this folder includes a `concourse.sh` script with all previous commands to keep things simple.
