#!/bin/bash   

echo "------port forwarding started------"

# Function to perform port forwarding
port_forward() {
    local namespace=$1
    local app_label=$2
    local local_port=$3
    local pod_port=$4

    POD_NAME=$(kubectl get pods -n "$namespace" -l app="$app_label" -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

    if [ -z "$POD_NAME" ]; then
        echo "Error: No pod found for app=$app_label in namespace=$namespace"
    else
        echo "------port forwarding for $app_label started------"
        kubectl port-forward "pod/$POD_NAME" -n "$namespace" "$local_port:$pod_port" &
    fi
}

# Prometheus
port_forward "monitoring" "prometheus-server" 9090 9090

sleep 3

# PostgreSQL
port_forward "db" "postgresdb" 5432 5432

sleep 3

# pgREST
port_forward "db" "postgrest" 3000 3000

sleep 3

echo "Port forwarding setup complete."
