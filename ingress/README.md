# Ingress
This guide provides steps and configuration samples to deploy an nginix Ingress Controller and connect it to an application using an Ingress resource. This example assumes you are not using NSX-T.
Make sure you clone this repo to get access to the yml files in it that will be referenced during this tutorial.

An Ingress Controller is a daemon, deployed as a Kubernetes Pod, that watches the apiserver's `/ingresses` endpoint for updates to the [Ingress resource](https://kubernetes.io/docs/concepts/services-networking/ingress/). Its job is to satisfy requests for Ingresses.
This example requires RBAC to be enabled as it will use a specific Service Account with a Role that allows the Ingress Controller to connect to the apiserver to do its job.

## Prerequisites
You'll need a provisioned kubernetes cluster with at least 2 worker nodes, and access to it via `kubectl`

## Step by step guide

1. Install Nginx ingress controller using [Nginx docs](https://kubernetes.github.io/ingress-nginx/deploy/)

2. Deploy a workload to test with the Ingress Controller.
We will deploy a pod with a simple timesample app and its corresponding service. Notice this service does not need to be exposed externally, the Ingress setup will route external traffic to it.
```
kubectl create -f timesample-service.yml
kubectl create -f timesample.yml
```

3. Create an Ingress Resource
We will create it with the routing rules required to drive traffic to our timesample app (`timesample-ingress.yml` from this section of the repo). Once this resource is created, the Ingress Controller will know about it and will update its internal `nginx.conf` configuration to incorporate routing to this timesample app based on the rules defined.
In this example we will use context paths to set the routing rule.
```
kubectl create -f timesample-ingress.yml
```

4. Test!
We finally can test everything. To do this we need the LoadBalancer IP of the Ingress Controller service. To get this run the following command and grab the `EXTERNAL-IP` of the `nginx-ingress-controller` service:
```
kubectl get svc -n ingress-nginx
```
You can access your timesample app by accessing `http://<EXTERNAL-IP>/timesample`. Example: `http://35.227.43.212/timesample`

If you set the Ingress Controller service as `type: NodePort` then you need to do a couple of things:
- Grab the IP of one of the worker nodes. Make sure it is routable, or you'll have to assign a public IP to it or put a LB with a public IP in front of the workers.
- Get the NodePort assigned to the `nginx-ingress-controller` service from the last `kubectl` command you run. It'll be the port in the 3XXXX range.
- Now you can access the app  by accessing `http://<NODE-IP>:<NODE-PORT>/timesample`. Example: `http://35.229.102.160:31324/timesample`

Here's a diagram to illustrate the deployments and services in use, courtesy of @datianshi. In that diagram there are two applications configured for Ingress instead of the one we created above, but the mechanics are the same.
![IDEA](https://raw.githubusercontent.com/datianshi/ingress-kubo-poc/master/images/PKS-Ingress-Nginx.png)
