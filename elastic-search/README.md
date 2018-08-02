# Elastic-Search
This guide provides sample yamls and basic instructions to deploy and use Elastic-Search on Kubernetes. It is based on [Kubernetes samples](https://github.com/kubernetes/examples/tree/master/staging/elasticsearch). Thanks to Maggie Ambrose (@mambrose-pivotal) for polishing the content of the files.
Make sure you clone this repo to get access to the yml files in it that will be referenced during this tutorial.

We will demonstrate the deployment of a single node Elastic-Search cluster with persistent volumes. Once deployed, we can demonstrate K8S self-healing, BOSH resurrection, and persistence capabilities.

## Prerequisites
You'll need a provisioned kubernetes cluster with at least 2 worker nodes and privileged containers enabled, and access to it via `kubectl`

## Step by step guide

1. Create Storage Class.

  On GCP:
  ```
  kubectl create -f storage-class-gcp.yml
  ```

  On vSphere:
  ```
  kubectl create -f storage-class-vsphere.yml
  ```

2. Create Persistent Volume claim
  ```
  kubectl create -f persistent-volume-claim-es.yml
  ```

3. Create Service
  ```
  kubectl create -f es-svc.yml
  kubectl get svc
  ```
  This could be changed from `type: LoadBalancer` to `type: NodePort` if you are not running your cluster on AWS/GCP nor using NSX-T (no SDN).

4. Create Elastic Search Deployment
  ```
  kubectl create -f es-deployment.yml
  ```
5. Validate elasticsearch is running.
  ```
  export ES_IP=<<SERVICE EXTERNAL-IP>>
  curl http://$ES_IP:9200
  ```
6. Populate Elasticsearch cluster with data

  At this point you can run `kubectl exec $POD_NAME -it -- bash -il` to open a bash session in your elasticsearch container.
  `cd /data` to inspect the persistent volume data structure before populating elastic search.

  Add an index
  ```
  curl -H'Content-Type: application/json' -X PUT http://$ES_IP/myindex -d '
  {
    "settings":
    {
      "index": {
        "number_of_shards": 2,
        "number_of_replicas": 1
      }
    }
  }'
  curl -X GET "http://$ES_IP:9200/myindex/_settings?pretty=true"
  ```
  Create a mapping of a `type: order`, which includes two properties - an ID and a customer_id.
  ```
  curl -H'Content-Type: application/json' -X POST http://$ES_IP/myindex/order/_mapping -d \ '
  {"order":
    {
      "properties": {
        "id": {"type": "keyword", "store": "true"},
        "customer_id": {"type": "keyword", "store": "true"}
      }
    }
  }'
  ```

  With this, we add two customer "documents" into the index with ids 1 and 2.
  ```
  curl -i -H "Content-Type: application/json" -X POST "http://$ES_IP:9200/myindex/order/1?pretty=true" -d \
  '{
    "customer_id": "customer1"
  }'
  curl -i -H "Content-Type: application/json" -X POST "http://$ES_IP:9200/myindex/order/2?pretty=true" -d \
  '{
    "customer_id": "customer2"
  }'
  ```

  To validate we can request the data from elastic search:
  ```
  curl -i -X GET "http://$ES_IP:9200/myindex/order/1?pretty=true"
  ```

7. Testing PKS HA

  Run this command to discover which worker node the elastic search pod is running on:
  ```
  kubectl get pods -o wide
  ```

  Go to vCenter and "Power Off" the VM. Run the following command to watch Kubernetes recreate the pod on the other worker:
  ```
  watch kubectl get pods -o wide
  ```

  Run the following command to watch BOSH recreate the missing worker:
  ```
  watch bosh -e <<your bosh alias>> vms
  ```
