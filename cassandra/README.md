# Cassandra  
This guide provides sample yamls to deploy a 3-node stateful-set Cassandra cluster, load data from an external data set and to deploy a java client dashboard.

Cluster uses the DockerHub Official Cassandra image maintained by Docker community. The main intention of this repo is demonstrate a stateful-set workload and not to recommend this specific Cassandra deployment.

Make sure you clone this repo to get access to the yml files in it that will be referenced during this tutorial.

## Prerequisites
You'll need a provisioned kubernetes cluster, and access to it via `kubectl`
You'll need worker nodes with at least 4GB RAM

## Create namespace
Run this command
```
kubectl create namespace pks-workshop
```

## Configure Storage class
Make sure you have defined a default Storage Class named `standard` in your cluster. If you don't have one, then run the following command:

For vSphere
```
kubectl create -f cluster/storage-class-vsphere.yml
```
For GCP
```
kubectl create -f cluster/storage-class-gcp-lb.yml
```

## Deploy the Cassandra cluster
Install 3-node stateful-set cluster running this command:
```
kubectl apply -f cluster/cassandra-demo-cluster.yml
```
Each node will be created sequentially. Wait for all nodes to be created before moving to next step.
You can also check the status of the cluster running `nodetool` in one of the nodes with a command like this:
```
kubectl exec -it cassandra-pks-demo-0 nodetool status
```
2 services are deployed alongside the stateful-set:
- One of `type: LoadBalancer` to facilitate external clients to connect to the cluster
- One standard and headless to allow internal clients to connect to the cluster
For the LB to work your PKS installation needs to run on GCP or be integrated with NSX-T.

## Bootstrap Cassandra cluster with a sample data set
The following command will bootstrap the Cassandra cluster with a keyspace and a table loaded with data from a sample NYC Species data-set, using a ConfigMap and an external file with the raw data.
```
kubectl apply -f data-import/cassandra-demo-bootstrap.yml
```

## Deploy a client UI
The following command will deploy a Spring Boot app with a UI to be used as a client targeting the Cassandra cluster, with a couple of queries/searches to simulate access to the data:
```
kubectl apply -f java-client/cassandra-demo-client.yml
```
A `type: LoadBalancer` service is deployed alongside the client Deployment to allow access to the 3-replica based application.
For the LB to work your PKS installation needs to run on GCP or be integrated with NSX-T.

Use the external LoadBalancer IP to access the client UI. Here's a screenshot with how it looks like:
![IMAGE](images/client_snapshot.png)

## Deploy a client UI in PAS
If you have Pivotal Application Service you can also deploy the java application there and configure a User Provided Service for the client app to be able to talk to the Cassandra cluster.
The below steps assume you have already targeted a PAS API and logged in with you CF CLI.

First step is to edit the `java-client/cassandra-user-provided-service.json` and replace the `hostname` value with the `EXTERNAL-IP` of the service `cassandra-demo-cluster-external`. You can get that IP by running this command:
```
kubectl get service cassandra-demo-cluster-external -n pks-workshop -o jsonpath="{.status.loadBalancer.ingress[0].ip}"
```

Next we need to create a User Provided Service with that configuration. Run the following command:
```
cf cups cassandra-pks -p java-client/cassandra-user-provided-service.json
```

Finally deploy the application to PAS. Make sure you use the `java-client/manifest.yml` provided:
```
cd java-client
cf push
```

Once the deployment finishes, just grab the app URL provided back by the PAS API and open it in your browser.
