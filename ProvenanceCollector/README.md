This is the ProvenanceCollector Spring application.

## Build container
```
./gradlew bootBuildImage
docker tag provenance-collector:0.0.1-SNAPSHOT <DOCKERHUB_ACCOUNT>/provenance-collector
docker push <DOCKERHUB_ACCOUNT>/provenance-collector 
```

Replace 'image' in `deployment.yaml` with your dockerhub account.

## add deployment to cluster
```
kubectl apply -f deployment.yaml
```