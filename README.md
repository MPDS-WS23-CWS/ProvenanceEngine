# ProvenanceEngine
In this repository, you will find the components we developed for Master Project Distributed Systems at TU-Berlin.

To run the **Provenance Engine** follow the instructions in the **ExperimentsAndResults** repository.

**Important Notice**:
Our experiments were executed on a native Kubernetes cluster with access to a distributed filesystem. Make sure to adjust the **volume claims** for the **ProvenanceCollector** and **pgSQL** deployments.

---

- `/ProvenanceCollector` holds a SpringBoot application for scraping Prometheus metrics during workflow execution.
- `/MetricScraper` includes Kubernetes configuration files for setting up Prometheus, node-exporter and kube-state-metrics in the cluster.
- `/database` includes Kubernetes configuration files for setting up a PostgreSQL database and a PostgREST API server in the cluster.


