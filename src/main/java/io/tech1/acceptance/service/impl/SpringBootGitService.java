package io.tech1.acceptance.service.impl;

import io.tech1.acceptance.domain.processing.FrameworkType;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import io.tech1.acceptance.domain.reporting.Git;
import io.tech1.acceptance.service.GitService;
import io.tech1.acceptance.support.RestAssureSupport;
import io.tech1.acceptance.configuration.ApplicationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.ConnectException;

@Slf4j
@Service
public class SpringBootGitService implements GitService {
    private final RestAssureSupport restAssureSupport;

    @Autowired
    public SpringBootGitService(RestAssureSupport restAssureSupport) {
        this.restAssureSupport = restAssureSupport;
    }

    public Git retrieve(FrameworkType frameworkType) {
        try {
            String json = restAssureSupport.getResponseJSON(frameworkType.equals(FrameworkType.SPRING_BOOT_V1) ? ApplicationConstants.SPRING_BOOT_INFO_URL_V1 : ApplicationConstants.SPRING_BOOT_INFO_URL_V2);
            return Git.builder()
                    .branch(JsonPath.read(json, "$.git.branch"))
                    .commitId(JsonPath.read(json, "$.git.commit.id"))
                    .commitTime(JsonPath.read(json, "$.git.commit.time"))
                    .build();
        } catch (ConnectException | PathNotFoundException e) {
            log.error("Spring git endpoint is unavailable {}", e);
            return Git.builder()
                    .branch(ApplicationConstants.UNCERTAIN)
                    .commitId(ApplicationConstants.UNCERTAIN)
                    .commitTime(ApplicationConstants.UNCERTAIN)
                    .build();
        } catch (NullPointerException e) {
            log.error("Spring version is null {}", e);
            return Git.builder()
                    .branch(ApplicationConstants.UNCERTAIN)
                    .commitId(ApplicationConstants.UNCERTAIN)
                    .commitTime(ApplicationConstants.UNCERTAIN)
                    .build();
        }
    }
}
