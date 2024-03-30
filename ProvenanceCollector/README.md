# ProvenanceCollector 

## Configuration

`src/main/resources/application.yml` serves as a configuration file for the application.

### Add a new aggregate metric
To configure a new aggregate metric (one value per pod), add the respective promql query in `application.yml`.
The name can be chosen freely. 
However, in addition, a new column of the same name must be added to the resources table in `src/main/resources/schema.sql`.

### Add a new time series metric
To configure a new time series metric (multiple value per pod), add the respective promql query in `application.yml`.
The name can be chosen freely.
Data will be added to the resources_time_series table.

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