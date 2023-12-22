# Create namespace for prov scraping
kubectl create namespace prometheus

# Appyl files for prometheus setup
kubectl apply -f monitoring-namespace.yaml
kubectl apply -f prometheus-config.yaml
kubectl apply -f prometheus-deployment.yaml
kubectl apply -f prometheus-service.yaml
minikube service --namespace=monitoring prometheus

# deploy node exporter. explain daemonser
kubectl apply -f node-exporter-daemonset.yml


