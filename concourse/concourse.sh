#!/bin/bash

kubectl apply -f rbac-config.yml
helm init --upgrade --service-account tiller
helm repo update
helm install --name my-concourse stable/concourse
export POD_NAME=$(kubectl get pods --namespace default -l "app=my-concourse-web" -o jsonpath="{.items[0].metadata.name}")
kubectl port-forward --namespace default $POD_NAME 8080:8080 &
fly -t k8s login -c http://127.0.0.1:8080/ -k
