# Guestbook  
This guide is based in the [kubernetes guestbook application tutorial](https://kubernetes.io/docs/tutorials/stateless-application/guestbook/) and provides sample yamls to deploy a 3-replicase Deployment for the frontend UI, a Redis master node and the Persistent Volume configuration required by it.

Make sure you clone this repo to get access to the yml files in it that will be referenced during this tutorial.

## Prerequisites
You'll need a provisioned kubernetes cluster, and access to it via `kubectl`

## Deploy it all
To simplify the steps we've put all yaml in one single file, and prepared three versions:
- If you are in GCP use `pv-guestbook-allinone-gcp-lb.yml`
- If you are in vSphere with NSX-T use `pv-guestbook-allinone-vsphere-lb.yml`
- If you are in vSphere without NSX-T use `pv-guestbook-allinone-vsphere.yml`
To deploy everything run a command like the following with the appropriate yaml file:
```
kubectl apply -f pv-guestbook-allinone-vsphere-lb.yml
```
The following resources will be deployed:
- A Storage Class with name `ci-storage`
- A Persistent Volume Claim with name `ci-claim` defining the size of the persistent disk for the Redis Mater node
- A 3-replicas Deployment for the Frontend UI
- A Redis Master (single) node
