# ProvenanceEngine
This repo holds the components for handling provenance data.
- `/ProvenanceCollector` holds a SpringBoot application for scraping Prometheus metrics during workflow execution.
- `/MetricScraper` includes Kubernetes configuration files for setting up Prometheus, node-exporter and kube-state-metrics in the cluster.
- `/database` includes Kubernetes configuration files for setting up a PostgreSQL database and a PostgREST API server in the cluster.


