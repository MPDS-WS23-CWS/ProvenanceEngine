namespace="default"

kubectl apply -f ./pgSQL/db-persistent-volume.yaml --namespace $namespace
kubectl apply -f ./pgSQL/db-volume-claim.yaml --namespace $namespace
kubectl apply -f ./pgSQL/db-configmap.yaml --namespace $namespace
kubectl apply -f ./pgSQL/db-deployment.yaml --namespace $namespace
kubectl apply -f ./pgSQL/db-service.yaml --namespace $namespace

echo -e "------database service and pods started------\n"

kubectl apply -f ./pgREST/postgrest-deployment.yaml --namespace $namespace
kubectl apply -f ./pgREST/postgrest-service.yaml --namespace $namespace

echo -e "------postGREST service and pods started------ \n"
