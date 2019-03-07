# Stardog Helm Deployment

This example holds a helm chart for deploying a Stardog cluster into
k8s.

## Running

1. Copy a valid Stardog license into the charts home directury.
1. Create a k8s namespace in which the Stardog cluser will be run.
1. Run the following command:
```bash
helm install --debug  --namespace <namespace name> --set 'dockerrepo.password=<docker repo password>' starhelm
```

Additional customization variables can be found in the `values.yaml` file.

## Using The Cluster

After launching the cluster with Helm the following commands are useful
for inspecting it:

List the namespaces in your Kubernetes cluster:
```
kubectl get namespaces
```
List the pods in your namespace:
```
kubectl -n stardog-k8s get pods
```
View the logs for a specific pod:
```
kubectl -n stardog-k8s logs zk-0
```
Connect into a pod in the cluster:
```
kubectl -n stardog-k8s exec -it stardog-cluster-0 -- sh
```
List the services (and their IPs and DNS names):
```
kubectl -n stardog-k8s get svc
```
