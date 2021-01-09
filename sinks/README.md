# Sinks: Workload monitoring in PKS

## Description
This section includes a few examples on how to leverage the [PKS Sink architecture](https://docs.pivotal.io/tkgi/1-9/sink-architecture.html) to collect custom application metrics, expose them in a single prometheus endpoint in a pks k8s cluster, and scrap them by an external prometheus instance running in Healthwatch 2.0.

## How It Works
This example sets up a cluster metric sink(CMS) and a metric sink(MS). The MS is to capture custom metrics from prometheus annotated pods in the namespace and forward those metrics to the CMS. The CMS will then expose all aggregated metrics on a single prometheus `/metrics` enpoint.
This makes it use the built in discovery to aggregate metrics that can be scraped by an external prometheus instance.

We setup the CMS with a [influxdb_listener](https://github.com/influxdata/telegraf/tree/master/plugins/inputs/influxdb_listener) input. The MS in the namespace then can have a [influxdb](https://github.com/influxdata/telegraf/tree/master/plugins/outputs/influxdb) output that send metric to the listener. It also requires a `service` to be set up on the CMS so that the MS can use k8s dns to resolve the listener. We also need to enable a [prometheus](https://github.com/influxdata/telegraf/tree/master/plugins/outputs/prometheus_client) output on the CMS.

The last piece is to configure Healthwatch 2.0 to scrap these metrics.

## Install

1. install the cluster metric sink and service

```
kubectl apply -f cms-prometheus.yml
kubectl apply -f telegrafds-service.yml
```

2. install the metric sink into the default namepsace

```
kubectl apply -f ms-influxdb.yml
```

3. run the example app pod and expose it

```
kubectl apply -f app-sample-metrics.yml
```

Change from NodePort to LoadBalancer if your cluster allows it.

4. Visit the app on the `/metrics` endpoint; the counter will increase as you refresh

5. Visit the prometheus client running on any worker node of the cluster on port `9978` and you should see the metrics from the example app. This `/metrics` endpoint can now be scraped by any prometheus instance.

Together with the application custom metrics, Telegraf also includes the many additional `kubernetes_pod_` and `kubernetes_system_` metrics, thanks to the [Kubernetes Input Plugin](https://github.com/influxdata/telegraf/tree/1.13.2/plugins/inputs/kubernetes).

## Adding in Healthwatch 2.0

You can now view these metrics in the bundled Grafana UI from Healthwatch 2.0. The only requirement being that you configured the Prometheus Configuration setting in the tile to have the following custom Scrape Config Job:

```
job_name: cluster_metric_sinks
dns_sd_configs:
- names:
    - q-s4.worker.*.*.bosh.
  type: A
  port: 9978
```

Configure a grafana query with the following settings.
Metrics: `http_requests_total_counter`

Sample:
![grafana](grafana.png)
