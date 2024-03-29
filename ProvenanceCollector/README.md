# ProvenanceCollector 

## Configuration

`src/main/resources/application.yml` serves as a configuration file for the application.

### Add a new 

## Build and deploy application

### Prepare container
```
./gradlew bootBuildImage
docker tag provenance-collector:0.0.1-SNAPSHOT <DOCKERHUB_ACCOUNT>/provenance-collector
docker push <DOCKERHUB_ACCOUNT>/provenance-collector 
```

Replace 'image' in `deployment.yaml` with your dockerhub account.

### Add deployment to cluster
```
kubectl apply -f deployment.yaml
```