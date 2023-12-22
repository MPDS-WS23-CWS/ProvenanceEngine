import io.kubernetes.client.extended.event.EventType;
import io.kubernetes.client.extended.event.legacy.EventBroadcaster;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Pod;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MetricsUtil {

    private final ApiClient apiClient;
    private final ExecutorService executorService;

    public MetricsUtil(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.executorService = Executors.newCachedThreadPool();
    }
    
    
}