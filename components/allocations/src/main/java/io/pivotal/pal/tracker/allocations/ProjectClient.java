package io.pivotal.pal.tracker.allocations;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class ProjectClient {

    private Logger logger = Logger.getLogger(ProjectClient.class.getName());

    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private ConcurrentHashMap<Long, ProjectInfo> cache = new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo = restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        cache.put(projectId, projectInfo);
        return projectInfo;
    }

    private ProjectInfo getProjectFromCache(long projectId) {
        logger.info("get from cache: projectId: " + projectId);
        return cache.get(projectId);
    }

}
